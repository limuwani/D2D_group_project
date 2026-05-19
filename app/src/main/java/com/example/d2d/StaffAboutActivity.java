// Created by Anon - D2D Staff Agent Handbook Screen
package com.example.d2d;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;

public class StaffAboutActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.staff_about);

        Button backBtn = findViewById(R.id.terms_to_login);
        if (backBtn != null) {
            backBtn.setOnClickListener(v -> finish());
        }
    }
}
