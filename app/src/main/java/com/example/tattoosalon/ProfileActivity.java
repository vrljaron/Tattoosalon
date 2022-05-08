package com.example.tattoosalon;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {
    private static final String LOG_TAG = ProfileActivity.class.getName();

    private FirebaseAuth mAuth;
    private FirebaseUser user;

    private FirebaseFirestore mFirestore;
    private CollectionReference mProfiles;
    private CollectionReference mAppointments;
    private currentProfile mProfile;

    TextView name;
    TextView email;
    TextView phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();

        user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            Log.d(LOG_TAG, "Auth user!");
        } else {
            Log.d(LOG_TAG, "Unauth user!");
            finish();
        }

        mFirestore = FirebaseFirestore.getInstance();
        mProfiles = mFirestore.collection("Profiles");
        mAppointments = mFirestore.collection("Appointments");

        name = findViewById(R.id.name_text);
        email = findViewById(R.id.email_text);
        phone = findViewById(R.id.phone_text);

        getUserProfile();
    }

    private void setCurrentProfile(String name, String email, String phone) {
        this.name.setText(name);
        this.email.setText(email);
        this.phone.setText(phone);
    }

    private void getUserProfile() {
        mProfiles.whereEqualTo("email", user.getEmail()).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                if (document != null) {
                    mProfile = document.toObject(currentProfile.class);
                    mProfile.setId(document.getId());
                    setCurrentProfile(mProfile.getName(), mProfile.getEmail(), mProfile.getPhone());
                }
            }
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
        switch (item.getItemId()) {
            case R.id.logout:
                Log.d(LOG_TAG, "Logout clicked!");
                FirebaseAuth.getInstance().signOut();
                finishAffinity();
                finish();
                return true;
            case R.id.profile:
                Log.d(LOG_TAG, "Profile clicked!");
                return true;
            case R.id.appointmentList:
                Log.d(LOG_TAG, "Appointments clicked!");
                Intent intent = new Intent(this, AppointmentListActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void editProfile(View view) {
        Intent intent = new Intent(this, EditProfileActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void deleteProfile(View view) {
        ArrayList<String> amount = new ArrayList<>();
        DocumentReference ref = mProfiles.document(mProfile._getId());
        new AlertDialog.Builder(this)
                .setTitle("Delete entry")
                .setMessage("Are you sure you want to delete your profile?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mAppointments.whereEqualTo("guestEmail", mProfile.getEmail()).get().addOnSuccessListener(success -> {
                            for (QueryDocumentSnapshot documentSnapshot : success) {
                                if (documentSnapshot != null) {
                                    amount.add(documentSnapshot.getId());
                                }
                            }
                            if (amount.size() == 0) {
                                user.delete().addOnSuccessListener(success2 -> {
                                    ref.delete().addOnSuccessListener(success3 -> {
                                        Log.d(LOG_TAG, "Deleted user " + mProfile.toString());
                                        finishAffinity();
                                        finish();
                                    });
                                });
                            } else {
                                Toast.makeText(getApplicationContext(), "Cannot be deleted.\n You have to delete the appointments.", Toast.LENGTH_LONG).show();
                            }
                        });

                    }
                })

                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
        Log.d(LOG_TAG, "deleteProfile: clicked!");
    }

    @Override
    protected void onResume() {
        super.onResume();
        getUserProfile();
    }
}