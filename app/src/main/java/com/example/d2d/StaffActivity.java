/*
// Modified by Anon - Responsive UI & Flow
package com.example.d2d;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class StaffActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.s_active_queue);

        findViewById(R.id.back_btn).setOnClickListener(v -> {
            finish();
        });

        findViewById(R.id.add_new_order_btn).setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(StaffActivity.this, AssignOrderActivity.class);
            startActivity(intent);
        });

        // For testing, make the empty state visible and the button functional
        // If we had a list, we'd wire individual items here.
    }
}

*/