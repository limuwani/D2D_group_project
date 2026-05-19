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

        // Fetch active order dynamically from server
        android.view.View orderCard = findViewById(R.id.active_order_card);
        android.view.View emptyState = findViewById(R.id.empty_state_layout);
        android.widget.TextView orderIdText = findViewById(R.id.status);
        android.widget.TextView restaurantText = findViewById(R.id.restaurant_name);

        String userId = getSharedPreferences("D2D_PREFS", MODE_PRIVATE).getString("user_id", "unknown");
        fetchActiveOrderFromServer(userId, orderCard, emptyState, orderIdText, restaurantText);
    }

    private void fetchActiveOrderFromServer(String userId, android.view.View orderCard, android.view.View emptyState, android.widget.TextView orderIdText, android.widget.TextView restaurantText) {
        okhttp3.OkHttpClient client = new okhttp3.OkHttpClient();
        String url = "https://wmc.ms.wits.ac.za/students/sgroup2676/d2dGroupProject/oderTrackingApp/orders/getActiveOrder.php?customer_id=" + userId;

        okhttp3.Request request = new okhttp3.Request.Builder().url(url).build();
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, java.io.IOException e) {
                runOnUiThread(() -> {
                    if (emptyState != null) emptyState.setVisibility(android.view.View.VISIBLE);
                    if (orderCard != null) orderCard.setVisibility(android.view.View.GONE);
                });
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws java.io.IOException {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String json = response.body().string();
                        org.json.JSONObject obj = new org.json.JSONObject(json);
                        if (obj.has("order_id")) {
                            String id = obj.getString("order_id");
                            String restaurant = obj.getString("restaurant_name");
                            runOnUiThread(() -> {
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
                            });
                            return;
                        }
                    } catch (Exception e) { e.printStackTrace(); }
                }
                runOnUiThread(() -> {
                    if (emptyState != null) emptyState.setVisibility(android.view.View.VISIBLE);
                    if (orderCard != null) orderCard.setVisibility(android.view.View.GONE);
                });
            }
        });
    }
}
