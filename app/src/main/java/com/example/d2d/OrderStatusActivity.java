// Modified by Anon - Responsive UI & Flow
package com.example.d2d;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class OrderStatusActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cus_order_status);

        findViewById(R.id.browse_restaurants_btn).setOnClickListener(v -> {
            finish(); // Goes back to select_res
        });

        findViewById(R.id.back_btn).setOnClickListener(v -> {
            finish();
        });

        // For testing purposes, let's make the order card visible and clickable
        android.view.View orderCard = findViewById(R.id.active_order_card);
        android.view.View emptyState = findViewById(R.id.empty_state_layout);

        // If we have an order, hide empty state and show card
        emptyState.setVisibility(android.view.View.GONE);
        orderCard.setVisibility(android.view.View.VISIBLE);

        orderCard.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(OrderStatusActivity.this, RateServiceActivity.class);
            startActivity(intent);
        });
    }
}
