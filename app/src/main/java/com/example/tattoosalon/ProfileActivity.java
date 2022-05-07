package com.example.tattoosalon;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class ProfileActivity extends AppCompatActivity {
    private static final String LOG_TAG = ProfileActivity.class.getName();

    private FirebaseAuth mAuth;
    private FirebaseUser user;

    private FirebaseFirestore mFirestore;
    private CollectionReference mProfiles;
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
                finish();
                return true;
            case R.id.profile:
                Log.d(LOG_TAG, "Profile clicked!");
                return true;
            case R.id.appointmentList:
                Log.d(LOG_TAG, "Appointments clicked!");
                Intent intent = new Intent(this, AppointmentListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
        //todo: popup are u sure?
        Log.d(LOG_TAG, "deleteProfile: clicked!");
    }


}