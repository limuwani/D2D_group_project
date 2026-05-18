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

        // Fetch active order dynamically from SQLite database
        android.view.View orderCard = findViewById(R.id.active_order_card);
        android.view.View emptyState = findViewById(R.id.empty_state_layout);

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        android.database.Cursor cursor = dbHelper.getActiveOrder();

        if (cursor != null && cursor.moveToFirst()) {
            // Found an active order
            String id = cursor.getString(cursor.getColumnIndexOrThrow("order_id"));
            String restaurant = cursor.getString(cursor.getColumnIndexOrThrow("restaurant_name"));
            cursor.close();

            emptyState.setVisibility(android.view.View.GONE);
            orderCard.setVisibility(android.view.View.VISIBLE);

            android.widget.TextView orderIdText = findViewById(R.id.status);
            android.widget.TextView restaurantText = findViewById(R.id.restaurant_name);
            if (orderIdText != null) orderIdText.setText("ORDER #" + id);
            if (restaurantText != null) restaurantText.setText(restaurant.toUpperCase());

            orderCard.setOnClickListener(v -> {
                android.content.Intent intent = new android.content.Intent(OrderStatusActivity.this, RateServiceActivity.class);
                startActivity(intent);
            });
        } else {
            // No active order to show
            emptyState.setVisibility(android.view.View.VISIBLE);
            orderCard.setVisibility(android.view.View.GONE);
        }
    }
}
