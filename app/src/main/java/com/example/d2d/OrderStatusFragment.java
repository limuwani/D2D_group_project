package com.example.d2d;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OrderStatusFragment extends Fragment {

    private final OkHttpClient client = new OkHttpClient();
    private String userId;
    private Timer refreshTimer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.cus_order_status, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences pref = requireActivity().getSharedPreferences("D2D_PREFS", android.content.Context.MODE_PRIVATE);
        userId = pref.getString("user_id", "unknown");

        View backBtn = view.findViewById(R.id.back_btn);
        View browseBtn = view.findViewById(R.id.browse_restaurants_btn);
        View historyBtn = view.findViewById(R.id.view_history_btn);

        if (backBtn != null) backBtn.setVisibility(View.GONE);
        if (browseBtn != null) {
            browseBtn.setOnClickListener(v -> {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).selectTab(0);
                }
            });
        }
        if (historyBtn != null) {
            historyBtn.setOnClickListener(v -> startActivity(new Intent(getActivity(), OrderHistoryActivity.class)));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        startRefreshTimer();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopRefreshTimer();
    }

    private void startRefreshTimer() {
        refreshTimer = new Timer();
        refreshTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                fetchActiveOrder();
            }
        }, 0, 5000); // Refresh every 5 seconds
    }

    private void stopRefreshTimer() {
        if (refreshTimer != null) {
            refreshTimer.cancel();
            refreshTimer = null;
        }
    }

    private void fetchActiveOrder() {
        if (getView() == null) return;

        ViewGroup container = getView().findViewById(R.id.orders_container);
        View emptyState = getView().findViewById(R.id.empty_state_layout);
        
        String url = "https://wmc.ms.wits.ac.za/students/sgroup2676/d2dGroupProject/oderTrackingApp/orders/customerActiveOrders.php?customer_id=" + userId;

        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {}

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String json = response.body().string();
                    try {
                        JSONArray orders = null;
                        String trimmed = json.trim();
                        if (trimmed.startsWith("[")) {
                            orders = new JSONArray(trimmed);
                        } else if (trimmed.startsWith("{")) {
                            JSONObject obj = new JSONObject(trimmed);
                            if (obj.has("data")) orders = obj.getJSONArray("data");
                            else {
                                orders = new JSONArray();
                                if (obj.has("order_id")) orders.put(obj);
                            }
                        }
                        
                        if (orders != null && orders.length() > 0) {
                            final JSONArray finalOrders = orders;
                            if (getActivity() != null) {
                                getActivity().runOnUiThread(() -> {
                                    emptyState.setVisibility(View.GONE);
                                    container.removeAllViews();
                                    for (int i = 0; i < finalOrders.length(); i++) {
                                        try {
                                            JSONObject obj = finalOrders.getJSONObject(i);
                                            addOrderCard(container, obj);
                                        } catch (Exception e) {}
                                    }
                                });
                            }
                            return;
                        }
                    } catch (Exception e) {}
                }
                
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        emptyState.setVisibility(View.VISIBLE);
                        container.removeAllViews();
                    });
                }
            }
        });
    }

    private void addOrderCard(ViewGroup container, JSONObject obj) throws Exception {
        String orderId = obj.getString("order_id");
        String restaurant = obj.optString("restaurant_name", "Restaurant");
        String status = obj.optString("status", "pending");

        LayoutInflater inflater = LayoutInflater.from(getContext());
        
        if ("pending_confirmation".equalsIgnoreCase(status)) {
            View card = inflater.inflate(R.layout.pending_order_item, container, false);
            TextView title = card.findViewById(R.id.pending_confirm_title);
            TextView desc = card.findViewById(R.id.pending_confirm_description);
            View confirmBtn = card.findViewById(R.id.pending_confirm_btn);

            title.setText("ORDER #" + orderId);
            desc.setText("The restaurant \"" + restaurant + "\" has initialized an order for you. Tap Confirm to start tracking.");
            confirmBtn.setOnClickListener(v -> updateOrderStatus(orderId, "preparing"));
            container.addView(card);
        } else {
            View card = inflater.inflate(R.layout.active_order_item, container, false);
            TextView idText = card.findViewById(R.id.status);
            TextView resText = card.findViewById(R.id.restaurant_name);
            TextView statusBadge = card.findViewById(R.id.status_badge_text);
            Button collectedBtn = card.findViewById(R.id.mark_collected_btn);

            idText.setText("ORDER #" + orderId);
            resText.setText(restaurant.toUpperCase());
            if (statusBadge != null) statusBadge.setText(status.toUpperCase());

            // Constraint: Can only mark as collected if status is READY
            boolean isReady = "READY".equalsIgnoreCase(status);
            if (collectedBtn != null) {
                collectedBtn.setVisibility(isReady ? View.VISIBLE : View.GONE);
                collectedBtn.setOnClickListener(v -> updateOrderStatus(orderId, "collected"));
            }

            card.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), RateServiceActivity.class);
                intent.putExtra("order_id", orderId);
                intent.putExtra("restaurant_name", restaurant);
                startActivity(intent);
            });
            container.addView(card);
        }
    }

    private void updateOrderStatus(String orderId, String newStatus) {
        String url = "https://wmc.ms.wits.ac.za/students/sgroup2676/d2dGroupProject/oderTrackingApp/orders/updateOder.php?order_id=" + orderId + "&status=" + newStatus;
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(@NonNull Call call, @NonNull IOException e) {}
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Order status updated to " + newStatus, Toast.LENGTH_SHORT).show();
                        fetchActiveOrder();
                    });
                }
            }
        });
    }
}
