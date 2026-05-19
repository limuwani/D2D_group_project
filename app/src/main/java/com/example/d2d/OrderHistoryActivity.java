package com.example.d2d;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OrderHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private OrderAdapter adapter;
    private List<Order> orderList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        ImageButton backBtn = findViewById(R.id.back_btn);
        if (backBtn != null) {
            backBtn.setOnClickListener(v -> finish());
        }

        recyclerView = findViewById(R.id.orders_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        orderList = new ArrayList<>();
        adapter = new OrderAdapter(orderList);
        recyclerView.setAdapter(adapter);

        fetchOrdersFromServer();
    }

    private void fetchOrdersFromServer() {
        SharedPreferences pref = getSharedPreferences("D2D_PREFS", MODE_PRIVATE);
        String userId = pref.getString("user_id", "unknown");
        String role = pref.getString("user_role", "customer");
        String restaurantId = pref.getString("restaurant_id", "1");

        OkHttpClient client = new OkHttpClient();
        String url;
        
        if ("staff".equals(role)) {
            // URL for staff history (restaurant specific)
            url = "https://wmc.ms.wits.ac.za/students/sgroup2676/d2dGroupProject/oderTrackingApp/orders/getRestaurantOrdersHistory.php?restaurant_id=" + restaurantId;
        } else {
            // Updated URL for customer history (collected orders)
            url = "https://wmc.ms.wits.ac.za/students/sgroup2676/d2dGroupProject/oderTrackingApp/orders/customerOrdersHistory.php?customer_id=" + userId;
        }

        Log.d("HISTORY_URL", "Fetching from: " + url);
        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String json = response.body().string();
                    Log.d("HISTORY_JSON", "Response: " + json);
                    try {
                        JSONArray array = null;
                        String trimmed = json.trim();
                        if (trimmed.startsWith("[")) {
                            array = new JSONArray(trimmed);
                        } else if (trimmed.startsWith("{")) {
                            JSONObject obj = new JSONObject(trimmed);
                            if (obj.has("data")) array = obj.getJSONArray("data");
                            else if (obj.has("orders")) array = obj.getJSONArray("orders");
                            else if (obj.has("history")) array = obj.getJSONArray("history");
                        }

                        List<Order> orders = new ArrayList<>();
                        if (array != null) {
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);
                                String orderId = obj.optString("order_id", "N/A");
                                String restaurant = obj.optString("restaurant_name", "Restaurant");
                                String status = obj.optString("status", "completed");
                                String customerName = obj.optString("customer_name", "Customer");
                                String customerId = obj.optString("customer_id", "");
                                int isRated = obj.optInt("is_rated", 0);
                                orders.add(new Order(orderId, restaurant, status, customerName, customerId, isRated));
                            }
                        }
                        
                        runOnUiThread(() -> {
                            orderList.clear();
                            orderList.addAll(orders);
                            adapter.notifyDataSetChanged();
                            if (orders.isEmpty()) {
                                Toast.makeText(OrderHistoryActivity.this, "No history found", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (Exception e) {
                        Log.e("HISTORY_ERROR", "Parsing error", e);
                        runOnUiThread(() -> Toast.makeText(OrderHistoryActivity.this, "No records found", Toast.LENGTH_SHORT).show());
                    }
                } else {
                    runOnUiThread(() -> Toast.makeText(OrderHistoryActivity.this, "Server error: " + response.code(), Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> Toast.makeText(OrderHistoryActivity.this, "Network error", Toast.LENGTH_SHORT).show());
            }
        });
    }
}
