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
    private String activeOrderId;
    private String restaurantId;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirm_takeaway);

        // 1. Get Data from previous screen and vault
        int resIdInt = getIntent().getIntExtra("restaurant_id", -1);
        restaurantId = String.valueOf(resIdInt);
        String intentResName = getIntent().getStringExtra("restaurant_name");
        SharedPreferences pref = getSharedPreferences("D2D_PREFS", MODE_PRIVATE);
        userId = pref.getString("user_id", "unknown");

        android.view.View emptyState = findViewById(R.id.empty_state_layout);
        android.view.View confirmLayout = findViewById(R.id.confirm_layout);
        android.widget.TextView totalOverdueText = findViewById(R.id.total_overdue);

        // Check if there is a staff-initialized order awaiting confirmation in SQLite
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        android.database.Cursor cursor = dbHelper.getPendingConfirmationOrder();

        if (cursor != null && cursor.moveToFirst()) {
            activeOrderId = cursor.getString(cursor.getColumnIndexOrThrow("order_id"));
            String dbRestaurantName = cursor.getString(cursor.getColumnIndexOrThrow("restaurant_name"));
            cursor.close();

            if (totalOverdueText != null) {
                totalOverdueText.setText("The restaurant \"" + dbRestaurantName + "\" has initialized an order for you (Order #" + activeOrderId + "). Please confirm to start tracking.");
            }

            emptyState.setVisibility(android.view.View.GONE);
            confirmLayout.setVisibility(android.view.View.VISIBLE);
        } else if (resIdInt != -1) {
            // Customer manually initiated order by selecting an open restaurant
            activeOrderId = null;
            String displayName = (intentResName != null) ? intentResName : ("Restaurant #" + restaurantId);
            if (totalOverdueText != null) {
                totalOverdueText.setText("You are placing a new takeaway order from \"" + displayName + "\". Please click Confirm below.");
            }
            emptyState.setVisibility(android.view.View.GONE);
            confirmLayout.setVisibility(android.view.View.VISIBLE);
        } else {
            // Nothing pending and no restaurant selected
            emptyState.setVisibility(android.view.View.VISIBLE);
            confirmLayout.setVisibility(android.view.View.GONE);
        }

        findViewById(R.id.confirm_order).setOnClickListener(v -> {
            placeOrder();
        });

        findViewById(R.id.customer_cancel_order).setOnClickListener(v -> finish());
        findViewById(R.id.back_to_home).setOnClickListener(v -> finish());

        // Browse button on empty state
        findViewById(R.id.browse_restaurants_btn).setOnClickListener(v -> finish());
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
                        
                        android.content.SharedPreferences pref = getSharedPreferences("D2D_PREFS", MODE_PRIVATE);
                        String currentUserId = pref.getString("user_id", "501");

                        // --- STANDARD SQLITE PERSISTENCE ---
                        DatabaseHelper dbHelper = new DatabaseHelper(ConfirmTakeawayActivity.this);
                        if (activeOrderId != null) {
                            dbHelper.updateOrderStatus(activeOrderId, "preparing");
                        } else {
                            dbHelper.saveOrder(
                                    "ORD-" + System.currentTimeMillis(),
                                    (getIntent().getStringExtra("restaurant_name") != null) ? getIntent().getStringExtra("restaurant_name") : ("Restaurant #" + restaurantId),
                                    "pending",
                                    "R 0.00",
                                    "Me",
                                    currentUserId
                            );
                        }

                        Intent intent = new Intent(ConfirmTakeawayActivity.this, MainActivity.class);
                        intent.putExtra("select_tab", 1);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
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
