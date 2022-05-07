package com.example.tattoosalon;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegistrationActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static final String LOG_TAG = RegistrationActivity.class.getName();
    private static final String PREF_KEY = RegistrationActivity.class.getPackage().toString();
    EditText userNameET;
    EditText emailET;
    EditText pwET;
    EditText pwConET;
    EditText phoneET;

    private SharedPreferences preferences;
    private FirebaseAuth mAuth;

    private FirebaseFirestore mFirestore;
    private CollectionReference mProfiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        int secret_key = getIntent().getIntExtra("SECRET_KEY", 0);

        if (secret_key != 99) {
            finish();
        }
        userNameET = findViewById(R.id.RegUserName);
        emailET = findViewById(R.id.regEmail);
        pwET = findViewById(R.id.regPassword);
        pwConET = findViewById(R.id.regPassword_Again);
        phoneET = findViewById(R.id.regPhone);

        preferences = getSharedPreferences(PREF_KEY, MODE_PRIVATE);

        mFirestore = FirebaseFirestore.getInstance();
        mProfiles = mFirestore.collection("Profiles");

        String username = preferences.getString("userName", "");
        String password = preferences.getString("password", "");

        userNameET.setText(username);
        pwET.setText(password);

        mAuth = FirebaseAuth.getInstance();
    }

    public void registration(View view) {
        String username = userNameET.getText().toString();
        String email = emailET.getText().toString();
        String pw = pwET.getText().toString();
        String pwConfirm = pwConET.getText().toString();
        String phone = phoneET.getText().toString();

        if (username.length() == 0) {
            Toast.makeText(RegistrationActivity.this, "You have to write a name.", Toast.LENGTH_LONG).show();
        } else if (email.length() == 0) {
            Toast.makeText(RegistrationActivity.this, "You have to write an email.", Toast.LENGTH_LONG).show();
        } else if (pw.length() == 0) {
            Toast.makeText(RegistrationActivity.this, "You have to write a password.", Toast.LENGTH_LONG).show();
        } else if (!pw.equals(pwConfirm)) {
            Toast.makeText(RegistrationActivity.this, "The password and the confirm password are not the same.", Toast.LENGTH_LONG).show();
        } else {
            mAuth.createUserWithEmailAndPassword(email, pw).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        mProfiles.add(new currentProfile(username, email, phone));
                        Log.d(LOG_TAG, "User created successfully");
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("email", emailET.getText().toString());
                        editor.putString("password", pwET.getText().toString());
                        editor.apply();
                        finish();

                    } else {
                        Log.d(LOG_TAG, "User hasn't created successfully");
                        Toast.makeText(RegistrationActivity.this, "User hasn't created successfully " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    public void cancel(View view) {
        finish();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}