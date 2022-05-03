package com.example.tattoosalon;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class RegistrationActivity extends AppCompatActivity {
    private static final String LOG_TAG = RegistrationActivity.class.getName();
    EditText userNameET;
    EditText emailET;
    EditText pwET;
    EditText pwConET;

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
    }

    public void registration(View view) {
        String username = userNameET.getText().toString();
        String email = emailET.getText().toString();
        String pw = pwET.getText().toString();
        String pwConfirm = pwConET.getText().toString();

        if (!pw.equals(pwConfirm)) {
            Log.e(LOG_TAG, "password != passwordConfirm");
        }
        Log.i(LOG_TAG, "registration: " + username + " email: " + email);
    }

    public void cancel(View view) {
        finish();
    }
}