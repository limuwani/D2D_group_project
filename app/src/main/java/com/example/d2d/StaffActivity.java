package com.example.d2d;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.app.AppCompatActivity;

public class StaffActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
        setContentView(R.layout.waiter_page);

        // Get the user_id passed from MainActivity
        int userId = getIntent().getIntExtra("user_id", -1);
        //String role = getIntent().getStringExtra("user_role");

        //TextView textView = findViewById(R.id.temp);
       // textView.setText("Welcome! User ID: " + userId);
    }
}
