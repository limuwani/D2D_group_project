package com.example.d2d;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.content.res.ColorStateList;
import android.graphics.Color;

public class MainActivity extends AppCompatActivity {

    EditText email;
    EditText password;
    Button Login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showLoginScreen();
        setContentView(R.layout.login_page);
        EditText email = findViewById(R.id.email_edit_text);
        password = findViewById(R.id.password_edit_text);
        Login = findViewById(R.id.sign_in_button);
    }

    @SuppressLint("UseCompatTextViewDrawableApis")
    public void onLoginClick(View v){
        String Email = email.getText().toString();
        String Pass = password.getText().toString().trim();
        if(Email.isEmpty()){
            email.setCompoundDrawableTintList(ColorStateList.valueOf(Color.RED));
        }
        if(Pass.isEmpty()){
            password.setCompoundDrawableTintList(ColorStateList.valueOf(Color.RED));
        }
    }
    private void showLoginScreen() {
        setContentView(R.layout.login_page);

        Button signUpBtn = findViewById(R.id.sign_up_button);
        Button signInBtn = findViewById(R.id.sign_in_button);

        signUpBtn.setOnClickListener(v -> showSignUpScreen());
        signInBtn.setOnClickListener(v -> showCustomerScreen());
    }

    private void showSignUpScreen() {
        setContentView(R.layout.sign_up);

        Button backToLoginBtn = findViewById(R.id.back_to_login_button);
        backToLoginBtn.setOnClickListener(v -> showLoginScreen());
    }

    private void showCustomerScreen() {
        setContentView(R.layout.customer_page);
        // Add more logic here if needed (e.g. back button to login)
    }
}

