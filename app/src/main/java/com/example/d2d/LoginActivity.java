package com.example.d2d;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    EditText email;
    EditText password;
    Button loginBtn, signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);
        email = findViewById(R.id.email_edit_text);
        loginBtn = findViewById(R.id.sign_in_button);
        password = findViewById(R.id.password_edit_text);
        signup = findViewById(R.id.sign_up_button);
        signup.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignUp.class);
            startActivity(intent);
        });
        onClickListener();
    }

    @SuppressLint("UseCompatTextViewDrawableApis")
    public boolean validate(String Email, String Pass) {
        boolean isValid = true;
        if (Email.isEmpty()) {
            email.setBackgroundResource(R.drawable.edittext_error_style);
            email.setError("Email is required");
            isValid = false;
        }
        if (Pass.isEmpty()) {
            password.setBackgroundResource(R.drawable.edittext_error_style);
            password.setError("Password is required");
            isValid = false;
        }
        return isValid;
    }

    @SuppressLint("UseCompatTextViewDrawableApis")
    public void onClickListener() {
        loginBtn.setOnClickListener(v -> {
            String Email = email.getText().toString().trim();
            String Pass = password.getText().toString().trim();
            boolean isValid = true;
            isValid = validate(Email, Pass);
            if (isValid) {
                // If login is valid, go to the restaurant selection screen
                Intent intent = new Intent(LoginActivity.this, select_res.class);
                startActivity(intent);
            }
        });
    }
}
