/*
// Modified by Anon - Responsive UI & Flow
package com.example.d2d;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ConfirmTakeawayActivity extends AppCompatActivity {
    private final OkHttpClient client = new OkHttpClient();
    private String restaurantId;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirm_takeaway);

        // 1. Get Data from previous screen and vault
        restaurantId = String.valueOf(getIntent().getIntExtra("restaurant_id", -1));
        SharedPreferences pref = getSharedPreferences("D2D_PREFS", MODE_PRIVATE);
        userId = pref.getString("user_id", "unknown");

        // Make the layout visible for testing
        android.view.View emptyState = findViewById(R.id.empty_state_layout);
        android.view.View confirmLayout = findViewById(R.id.confirm_layout);

        // For testing: Hide empty state and show confirm layout
        emptyState.setVisibility(android.view.View.GONE);
        confirmLayout.setVisibility(android.view.View.VISIBLE);

        findViewById(R.id.confirm_order).setOnClickListener(v -> {
            placeOrder();
        });

        findViewById(R.id.customer_cancel_order).setOnClickListener(v -> finish());
        findViewById(R.id.back_to_home).setOnClickListener(v -> finish());
    }

    private void placeOrder() {
        // --- OFFICIAL API URL FOR CREATING ORDERS (GET request) ---
        String url = "https://wmc.ms.wits.ac.za/students/sgroup2676/d2dGroupProject/oderTrackingApp/orders/createOder.php"
                + "?customer_id=" + userId
                + "&staff_id=0"
                + "&restaurant_id=" + restaurantId;

        Request request = new Request.Builder().url(url).get().build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(ConfirmTakeawayActivity.this, "Network error. Please try again.", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        Toast.makeText(ConfirmTakeawayActivity.this, "Order Placed Successfully!", Toast.LENGTH_LONG).show();
                        
                        // --- STANDARD SQLITE PERSISTENCE ---
                        DatabaseHelper dbHelper = new DatabaseHelper(ConfirmTakeawayActivity.this);
                        dbHelper.saveOrder(
                                "ORD-" + System.currentTimeMillis(),
                                "Restaurant #" + restaurantId,
                                "pending",
                                "R 0.00"
                        );

                        Intent intent = new Intent(ConfirmTakeawayActivity.this, OrderStatusActivity.class);
                        startActivity(intent);
                        finish();
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(ConfirmTakeawayActivity.this, "Server error. Please try again.", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }
}

*/