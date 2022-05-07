package com.example.tattoosalon;

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
import android.widget.SearchView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class ReserveAppointmentListActivity extends AppCompatActivity {
    private static final String LOG_TAG = ReserveAppointmentListActivity.class.getName();
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    private RecyclerView mRecyclerView;
    private ArrayList<AppointmentItem> mAppointmentList;
    private ReserveAppointmentListAdapter mReserveAppointmentListAdapter;

    private FirebaseFirestore mFirestore;
    private CollectionReference mAppointments;

    private NotificationHandler mNotificationHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserve_appointment_list);

        mAuth = FirebaseAuth.getInstance();

        user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            Log.d(LOG_TAG, "Auth user!");
        } else {
            Log.d(LOG_TAG, "Unauth user!");
            finish();
        }

        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        mAppointmentList = new ArrayList<>();

        mReserveAppointmentListAdapter = new ReserveAppointmentListAdapter(mAppointmentList, this);
        mRecyclerView.setAdapter(mReserveAppointmentListAdapter);

        mFirestore = FirebaseFirestore.getInstance();
        mAppointments = mFirestore.collection("Appointments");

        mNotificationHandler = new NotificationHandler(this);

        queryData();
    }

    private void queryData() {
        mAppointmentList.clear();
        mAppointments.whereEqualTo("guestEmail", user.getEmail()).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                AppointmentItem item = document.toObject(AppointmentItem.class);
                item.setId(document.getId());
                mAppointmentList.add(item);
            }


            // Notify the adapter of the change.
            mReserveAppointmentListAdapter.notifyDataSetChanged();
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.base_menu, menu);
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
                finishAffinity();
                startActivity(intent);
                return true;
            case R.id.appointmentList:
                Log.d(LOG_TAG, "Appointments clicked!");
                intent = new Intent(this, AppointmentListActivity.class);
                finishAffinity();
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void deleteAppointment(AppointmentItem item) {
        DocumentReference ref = mAppointments.document(item._getId());
        //todo:finish
    }

    public void reserveAppointment(AppointmentItem item) {
        DocumentReference ref = mAppointments.document(item._getId());
        mNotificationHandler.send(item.getDate() + ". " + item.getTime());
        //todo:finish
    }
}