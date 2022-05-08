package com.example.tattoosalon;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AppointmentListActivity extends AppCompatActivity {
    private static final String LOG_TAG = AppointmentListActivity.class.getName();
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    private RecyclerView mRecyclerView;
    private ArrayList<AppointmentItem> mAppointmentList;
    private AppointmentItemAdapter mAppointmentItemAdapter;

    private FrameLayout redCircle;
    private TextView countTextView;

    private FirebaseFirestore mFirestore;
    private CollectionReference mAppointments;

    private int gridNumber = 1;
    private int cartItems = 0;
    private boolean viewRow = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_list);
        mAuth = FirebaseAuth.getInstance();

        user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            Log.d(LOG_TAG, "Auth user!");
        } else {
            Log.d(LOG_TAG, "Unauth user!");
            finish();
        }

        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, gridNumber));
        mAppointmentList = new ArrayList<>();

        mAppointmentItemAdapter = new AppointmentItemAdapter(mAppointmentList, this);
        mRecyclerView.setAdapter(mAppointmentItemAdapter);

        mFirestore = FirebaseFirestore.getInstance();
        mAppointments = mFirestore.collection("Appointments");
        queryData();
    }

    private void queryData() {
        mAppointmentList.clear();
        mAppointments.orderBy("date").get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                AppointmentItem item = document.toObject(AppointmentItem.class);
                if (!item.isReserved()) {
                    if (item.getGuestEmail().equals("") || item.getGuestEmail().length() == 0) {
                        item.setId(document.getId());
                        mAppointmentList.add(item);
                    }
                }
            }
            if (mAppointmentList.size() == 0) {
                initializeData();
                queryData();
            }


            // Notify the adapter of the change.
            mAppointmentItemAdapter.notifyDataSetChanged();
        });
    }

    private void initializeData() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd");
        String currentDate = formatter.format(new Date());
        String[] tmp = currentDate.split("\\.");
        int day = Integer.parseInt(tmp[2]);
        ArrayList<String> time = new ArrayList<>();
        time.add("8:00");
        time.add("10:00");
        time.add("12:00");
        time.add("14:00");
        time.add("16:00");
        String date = tmp[0] + "." + tmp[1] + ".";
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 5; j++) {
                mAppointments.add(new AppointmentItem(
                        date + (day + i > 9 ? (day + i) : "0" + (day + i)),
                        time.get(j),
                        "",
                        false)
                );
            }
        }
        queryData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.appointment_list_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.search_bar);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                Log.d(LOG_TAG, s);
                mAppointmentItemAdapter.getFilter().filter(s);
                return false;
            }
        });

        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.logout:
                Log.d(LOG_TAG, "Logout clicked!");
                FirebaseAuth.getInstance().signOut();
                finishAffinity();
                finish();
                return true;
            case R.id.profile:
                Log.d(LOG_TAG, "Profile clicked!");
                intent = new Intent(this, ProfileActivity.class);
                startActivity(intent);
                return true;
            case R.id.appointments:
                Log.d(LOG_TAG, "Appointments clicked!");
                intent = new Intent(this, ReserveAppointmentListActivity.class);
                startActivity(intent);
                return true;
            case R.id.view_selector:
                if (viewRow) {
                    changeSpanCount(item, R.drawable.ic_view_grid, 1);
                } else {
                    changeSpanCount(item, R.drawable.ic_table_rows, 2);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void changeSpanCount(MenuItem item, int drawableId, int spanCount) {
        viewRow = !viewRow;
        item.setIcon(drawableId);
        GridLayoutManager layoutManager = (GridLayoutManager) mRecyclerView.getLayoutManager();
        layoutManager.setSpanCount(spanCount);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        final MenuItem alertMenuItem = menu.findItem(R.id.appointments);
        FrameLayout rootView = (FrameLayout) alertMenuItem.getActionView();

        redCircle = (FrameLayout) rootView.findViewById(R.id.view_alert_red_circle);
        countTextView = (TextView) rootView.findViewById(R.id.view_alert_count_textview);

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(alertMenuItem);
            }
        });
        return super.onPrepareOptionsMenu(menu);
    }

    public void updateAlertIcon(AppointmentItem currentItem) {
        cartItems = (cartItems + 1);
        if (0 < cartItems) {
            countTextView.setText(String.valueOf(cartItems));
        } else {
            countTextView.setText("");
        }

        redCircle.setVisibility((cartItems > 0) ? VISIBLE : GONE);
        updateCurrentItem(currentItem);
    }

    public void updateCurrentItem(AppointmentItem currentItem) {
        DocumentReference ref = mAppointments.document(currentItem._getId());
        ref.update("guestEmail", user.getEmail()).addOnCompleteListener(success -> {
            Log.d(LOG_TAG, "updateCurrentItem: " + currentItem.getDate() + ":" + currentItem.getTime());
        }).addOnFailureListener(fail -> {
            Toast.makeText(this, "Item " + currentItem._getId() + " cannot be updated.", Toast.LENGTH_LONG).show();
        });
        queryData();
    }
}