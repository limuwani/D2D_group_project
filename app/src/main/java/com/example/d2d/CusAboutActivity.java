// Created by Anon - D2D About Screen
package com.example.d2d;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;

public class CusAboutActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cus_about);

        Button backBtn = findViewById(R.id.terms_to_login);
        if (backBtn != null) {
            backBtn.setOnClickListener(v -> finish());
        }
    }
}
