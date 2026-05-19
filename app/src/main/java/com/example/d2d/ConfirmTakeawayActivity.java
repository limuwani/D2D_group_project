// Modified by Anon - Responsive UI & Flow
package com.example.d2d;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONException;
import org.json.JSONObject;
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
    private String restaurantName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirm_takeaway);

        int resIdInt = getIntent().getIntExtra("restaurant_id", -1);
        restaurantId = String.valueOf(resIdInt);
        restaurantName = getIntent().getStringExtra("restaurant_name");
        SharedPreferences pref = getSharedPreferences("D2D_PREFS", MODE_PRIVATE);
        userId = pref.getString("user_id", "unknown");

        android.view.View emptyState = findViewById(R.id.empty_state_layout);
        android.view.View confirmLayout = findViewById(R.id.confirm_layout);
        android.widget.TextView orderDescriptionText = findViewById(R.id.order_description);

        if (resIdInt != -1) {
            // Search specifically for active orders the customer has with THIS restaurant
            checkForOrdersAtThisRestaurant(emptyState, confirmLayout, orderDescriptionText);
        } else {
            // Just check generic pending orders
            checkForPendingOrders(emptyState, confirmLayout, orderDescriptionText);
        }

        findViewById(R.id.confirm_order).setOnClickListener(v -> placeOrder());
        findViewById(R.id.customer_cancel_order).setOnClickListener(v -> finish());
        findViewById(R.id.back_to_home).setOnClickListener(v -> finish());
        findViewById(R.id.browse_restaurants_btn).setOnClickListener(v -> finish());
    }

    private void checkForOrdersAtThisRestaurant(android.view.View emptyState, android.view.View confirmLayout, android.widget.TextView orderDescriptionText) {
        // Use active orders API to check if there is an existing live order with this restaurant
        String url = "https://wmc.ms.wits.ac.za/students/sgroup2676/d2dGroupProject/oderTrackingApp/orders/customerActiveOrders.php?customer_id=" + userId;
        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> showNewOrderState(orderDescriptionText, emptyState, confirmLayout));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String json = response.body().string();
                        org.json.JSONArray arr = null;
                        if (json.trim().startsWith("[")) {
                            arr = new org.json.JSONArray(json);
                        } else if (json.trim().startsWith("{")) {
                            org.json.JSONObject obj = new org.json.JSONObject(json);
                            if (obj.has("data")) arr = obj.getJSONArray("data");
                        }

                        if (arr != null) {
                            for (int i = 0; i < arr.length(); i++) {
                                JSONObject obj = arr.getJSONObject(i);
                                String status = obj.optString("status", "");
                                String rId = obj.optString("restaurant_id", "");

                                // Check if there's an active order with this restaurant
                                if (rId.equals(restaurantId)) {
                                    activeOrderId = obj.getString("order_id");
                                    String resName = obj.optString("restaurant_name", restaurantName);

                                    runOnUiThread(() -> {
                                        if (orderDescriptionText != null) {
                                        android.widget.TextView titleText = findViewById(R.id.confirm_title);
                                        if ("pending_confirmation".equalsIgnoreCase(status)) {
                                            if (titleText != null) titleText.setText("Incoming Order!");
                                            orderDescriptionText.setText("The restaurant \"" + resName + "\" has initialized an order for you (Order #" + activeOrderId + "). Please confirm to start tracking.");
                                            findViewById(R.id.confirm_order).setVisibility(android.view.View.VISIBLE);
                                        } else {
                                            if (titleText != null) titleText.setText("Active Order Found");
                                            orderDescriptionText.setText("You have an active order with \"" + resName + "\" (Order #" + activeOrderId + "). Current status: " + status.toUpperCase());
                                            findViewById(R.id.confirm_order).setVisibility(android.view.View.GONE);
                                        }
                                    }
                                        emptyState.setVisibility(android.view.View.GONE);
                                        confirmLayout.setVisibility(android.view.View.VISIBLE);
                                    });
                                    return;
                                }
                            }
                        }
                    } catch (Exception e) { e.printStackTrace(); }
                }
                runOnUiThread(() -> showNewOrderState(orderDescriptionText, emptyState, confirmLayout));
            }
        });
    }

    private void showNewOrderState(android.widget.TextView orderDescriptionText, android.view.View emptyState, android.view.View confirmLayout) {
        activeOrderId = null;
        String displayName = (restaurantName != null) ? restaurantName : ("Restaurant #" + restaurantId);
        
        android.widget.TextView titleText = findViewById(R.id.confirm_title);
        if (titleText != null) titleText.setText("Place New Order");

        if (orderDescriptionText != null) {
            orderDescriptionText.setText("You are placing a new takeaway order from \"" + displayName + "\". Please click Confirm below.");
        }
        findViewById(R.id.confirm_order).setVisibility(android.view.View.VISIBLE);
        emptyState.setVisibility(android.view.View.GONE);
        confirmLayout.setVisibility(android.view.View.VISIBLE);
    }

    private void checkForPendingOrders(android.view.View emptyState, android.view.View confirmLayout, android.widget.TextView orderDescriptionText) {
        String url = "https://wmc.ms.wits.ac.za/students/sgroup2676/d2dGroupProject/oderTrackingApp/orders/getActiveOrder.php?customer_id=" + userId;
        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    emptyState.setVisibility(android.view.View.VISIBLE);
                    confirmLayout.setVisibility(android.view.View.GONE);
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String json = response.body().string();
                        JSONObject obj = new JSONObject(json);
                        if (obj.has("order_id") && "pending_confirmation".equalsIgnoreCase(obj.optString("status"))) {
                            activeOrderId = obj.getString("order_id");
                            restaurantId = obj.optString("restaurant_id", "0");
                            String resName = obj.optString("restaurant_name", "the restaurant");
                            
                            runOnUiThread(() -> {
                                if (orderDescriptionText != null) {
                                    orderDescriptionText.setText("The restaurant \"" + resName + "\" has initialized an order for you (Order #" + activeOrderId + "). Please confirm to start tracking.");
                                }
                                emptyState.setVisibility(android.view.View.GONE);
                                confirmLayout.setVisibility(android.view.View.VISIBLE);
                            });
                            return;
                        }
                    } catch (Exception e) { e.printStackTrace(); }
                }
                runOnUiThread(() -> {
                    emptyState.setVisibility(android.view.View.VISIBLE);
                    confirmLayout.setVisibility(android.view.View.GONE);
                });
            }
        });
    }

    private void placeOrder() {
        if (activeOrderId != null) {
            updateOrderStatusOnServer(activeOrderId, "preparing");
            return;
        }

        String url = "https://wmc.ms.wits.ac.za/students/sgroup2676/d2dGroupProject/oderTrackingApp/orders/createOder.php"
                + "?customer_id=" + userId
                + "&staff_id=0"
                + "&restaurant_id=" + restaurantId;

        Request request = new Request.Builder().url(url).get().build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(ConfirmTakeawayActivity.this, "Network error", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String json = response.body().string();
                    try {
                        if (json.trim().equals("[]")) {
                            fetchLatestOrderIdAndFinish();
                        } else {
                            JSONObject obj = new JSONObject(json);
                            if ("success".equalsIgnoreCase(obj.optString("status"))) {
                                String newOrderId = obj.optString("order_id", "");
                                runOnUiThread(() -> completeOrderWithId(newOrderId));
                            } else {
                                runOnUiThread(() -> fetchLatestOrderIdAndFinish());
                            }
                        }
                    } catch (Exception e) {
                        runOnUiThread(() -> fetchLatestOrderIdAndFinish());
                    }
                } else {
                    runOnUiThread(() -> Toast.makeText(ConfirmTakeawayActivity.this, "Server error. Code: " + response.code(), Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private void updateOrderStatusOnServer(String orderId, String newStatus) {
        String url = "https://wmc.ms.wits.ac.za/students/sgroup2676/d2dGroupProject/oderTrackingApp/orders/updateOder.php"
                + "?order_id=" + orderId
                + "&status=" + newStatus;

        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(ConfirmTakeawayActivity.this, "Failed to confirm order.", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> completeOrderWithId(orderId));
                } else {
                    runOnUiThread(() -> Toast.makeText(ConfirmTakeawayActivity.this, "Server error", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private void fetchLatestOrderIdAndFinish() {
        String url = "https://wmc.ms.wits.ac.za/students/sgroup2676/d2dGroupProject/oderTrackingApp/orders/displayOderHistory.php?customer_id=" + userId;
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> completeOrderWithId(""));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String id = "";
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        org.json.JSONArray arr = new org.json.JSONArray(response.body().string());
                        if (arr.length() > 0) {
                            id = arr.getJSONObject(0).optString("order_id", "");
                        }
                    } catch (Exception e) {}
                }
                final String finalId = id;
                runOnUiThread(() -> completeOrderWithId(finalId));
            }
        });
    }
    private void completeOrderWithId(String resolvedOrderId) {
        Toast.makeText(this, "Order #" + resolvedOrderId + " placed!", Toast.LENGTH_LONG).show();

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("select_tab", 1);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finishAffinity();
    }
}
