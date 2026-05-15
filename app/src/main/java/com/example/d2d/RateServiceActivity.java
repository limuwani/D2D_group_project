/*
// Modified by Anon - Responsive UI & Flow
package com.example.d2d;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.LinearLayout;

public class RateServiceActivity extends AppCompatActivity {
    private LinearLayout upLayout, downLayout;
    private boolean isUpSelected = false;
    private boolean isDownSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rate_service);

        upLayout = findViewById(R.id.thumbs_up_layout);
        downLayout = findViewById(R.id.thumbs_down_layout);

        upLayout.setOnClickListener(v -> {
            toggleUp();
        });

        downLayout.setOnClickListener(v -> {
            toggleDown();
        });

        findViewById(R.id.submit_feedback).setOnClickListener(v -> {
            android.widget.Toast.makeText(RateServiceActivity.this, "Thank you for your feedback!", android.widget.Toast.LENGTH_SHORT).show();
            // Return to restaurant selection after feedback
            android.content.Intent intent = new android.content.Intent(RateServiceActivity.this, select_res.class);
            intent.setFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP | android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        findViewById(R.id.back_to_orderstatus).setOnClickListener(v -> {
            finish();
        });
    }

    private void toggleUp() {
        if (isUpSelected) {
            // Deselect
            upLayout.setBackgroundResource(R.drawable.categories);
            isUpSelected = false;
        } else {
            // Select Up (Glass Green), Deselect Down
            upLayout.setBackgroundResource(R.drawable.glass_green);
            downLayout.setBackgroundResource(R.drawable.categories);
            isUpSelected = true;
            isDownSelected = false;
        }
    }

    private void toggleDown() {
        if (isDownSelected) {
            // Deselect
            downLayout.setBackgroundResource(R.drawable.categories);
            isDownSelected = false;
        } else {
            // Select Down (Glass Red), Deselect Up
            downLayout.setBackgroundResource(R.drawable.glass_red);
            upLayout.setBackgroundResource(R.drawable.categories);
            isDownSelected = true;
            isUpSelected = false;
        }
    }
}

*/