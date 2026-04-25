package com.example.d2d;

import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showLoginScreen();
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
