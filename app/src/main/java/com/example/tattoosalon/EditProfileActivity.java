package com.example.tattoosalon;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class EditProfileActivity extends AppCompatActivity {
    private static final String LOG_TAG = EditProfileActivity.class.getName();

    private FirebaseAuth mAuth;
    private FirebaseUser user;

    private FirebaseFirestore mFirestore;
    private CollectionReference mProfiles;
    private currentProfile mProfile;

    EditText name;
    EditText email;
    EditText phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

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

        name = findViewById(R.id.name_edit);
        email = findViewById(R.id.email_edit);
        phone = findViewById(R.id.phone_edit);

        getUserProfile();

    }

    private void setCurrentProfile(String name, String email, String phone) {
        this.name.setHint(name);
        this.email.setHint(email);
        this.phone.setHint(phone);
    }

    private void getUserProfile() {
        mProfiles.whereEqualTo("email", user.getEmail()).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                if (document != null) {
                    mProfile = document.toObject(currentProfile.class);
                    Log.d(LOG_TAG, "getUserProfile: " + mProfile);
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

    public void cancelEdit(View view) {
        finish();
    }

    public void saveChanges(View view) {
        DocumentReference ref = mProfiles.document(mProfile._getId());
        if (name.getText().toString().length() > 4 && !mProfile.getName().equals(name.getText().toString())) {
            ref.update("name", name.getText().toString()).addOnCompleteListener(success -> {
                Log.d(LOG_TAG, "saveChanges: " + mProfile._getId() + " new name: " + name.getText().toString());
            }).addOnFailureListener(fail -> {
                Toast.makeText(this, fail.getMessage(), Toast.LENGTH_LONG).show();
            });
        }
        if (email.getText().toString().length() != 0 && !mProfile.getEmail().equals(email.getText().toString())) {
            user.updateEmail(email.getText().toString()).addOnSuccessListener(success -> {
                Toast.makeText(this, success.toString(), Toast.LENGTH_LONG).show();
                ref.update("email", email.getText().toString()).addOnCompleteListener(success2 -> {
                    Log.d(LOG_TAG, "saveChanges: " + mProfile._getId() + " new email: " + email.getText().toString());
                }).addOnFailureListener(fail -> {
                    Toast.makeText(this, fail.getMessage(), Toast.LENGTH_LONG).show();
                });
            }).addOnFailureListener(fail -> {
                Toast.makeText(this, fail.getMessage(), Toast.LENGTH_LONG).show();
            });
        }
        if (phone.getText().toString().length() == 12 && !mProfile.getPhone().equals(phone.getText().toString())) {
            ref.update("phone", phone.getText().toString()).addOnCompleteListener(success -> {
                Log.d(LOG_TAG, "saveChanges: " + mProfile._getId() + " new phone: " + phone.getText().toString());
            }).addOnFailureListener(fail -> {
                Toast.makeText(this, fail.getMessage(), Toast.LENGTH_LONG).show();
            });
        }
        finish();
    }
}