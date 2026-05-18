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
        android.widget.TextView orderIdText = findViewById(R.id.status);
        android.widget.TextView restaurantText = findViewById(R.id.restaurant_name);

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        android.database.Cursor cursor = dbHelper.getActiveOrder();

        if (cursor != null && cursor.moveToFirst()) {
            // Found an active order
            String id = cursor.getString(cursor.getColumnIndexOrThrow("order_id"));
            String restaurant = cursor.getString(cursor.getColumnIndexOrThrow("restaurant_name"));
            cursor.close();

            if (emptyState != null) emptyState.setVisibility(android.view.View.GONE);
            if (orderCard != null) {
                orderCard.setVisibility(android.view.View.VISIBLE);
                if (orderIdText != null) orderIdText.setText("ORDER #" + id);
                if (restaurantText != null) restaurantText.setText(restaurant.toUpperCase());

                orderCard.setOnClickListener(v -> {
                    android.content.Intent intent = new android.content.Intent(OrderStatusActivity.this, RateServiceActivity.class);
                    intent.putExtra("order_id", id);
                    intent.putExtra("restaurant_name", restaurant);
                    startActivity(intent);
                });
            }
        } else {
            if (emptyState != null) emptyState.setVisibility(android.view.View.VISIBLE);
            if (orderCard != null) orderCard.setVisibility(android.view.View.GONE);
        }
    }
}
