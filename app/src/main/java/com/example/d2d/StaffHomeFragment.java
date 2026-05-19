package com.example.d2d;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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

public class StaffHomeFragment extends Fragment implements StaffQueueAdapter.OnStatusChangeListener {

    private final OkHttpClient client = new OkHttpClient();
    private RecyclerView recyclerView;
    private StaffQueueAdapter adapter;
    private List<Order> orderList = new ArrayList<>();
    private View emptyStateLayout;
    private String restaurantId = "1";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.s_active_queue, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        emptyStateLayout = view.findViewById(R.id.empty_queue_state);
        recyclerView = view.findViewById(R.id.active_queue_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new StaffQueueAdapter(getActivity(), orderList, this);
        recyclerView.setAdapter(adapter);

        SharedPreferences prefs = requireContext().getSharedPreferences("D2D_PREFS", android.content.Context.MODE_PRIVATE);
        restaurantId = prefs.getString("restaurant_id", "1");

        View addNewOrderBtn = view.findViewById(R.id.add_new_order_btn);
        if (addNewOrderBtn != null) {
            addNewOrderBtn.setOnClickListener(v -> {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).selectTab(0);
                }
            });
        }

        fetchActiveOrders();
    }

    private void fetchActiveOrders() {
        // Use the exact API provided by the user
        String url = "https://wmc.ms.wits.ac.za/students/sgroup2676/d2dGroupProject/oderTrackingApp/orders/getRestaurantOrders.php?restaurant_id=" + restaurantId;
        
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                showEmpty();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String json = response.body().string();
                    parseAndDisplayOrders(json);
                } else {
                    showEmpty();
                }
            }
        });
    }

    private void parseAndDisplayOrders(String json) {
        try {
            List<Order> newOrders = new ArrayList<>();
            JSONArray array = new JSONArray(json);

            for (int i = 0; i < array.length(); i++) {
                JSONObject o = array.getJSONObject(i);
                String status = o.optString("status", "pending");
                
                // PHP API already filters out 'collected', but we double check here
                if (!status.equalsIgnoreCase("collected")) {
                    newOrders.add(new Order(
                            o.optString("order_id", "0"),
                            o.optString("restaurant_name", "Restaurant"),
                            status.replace("_", " ").toUpperCase(),
                            o.optString("customer_name", "Customer"),
                            o.optString("customer_id", "0")
                    ));
                }
            }

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    orderList.clear();
                    orderList.addAll(newOrders);
                    adapter.notifyDataSetChanged();
                    updateEmptyState();
                });
            }
        } catch (Exception e) {
            showEmpty();
        }
    }

    private void showEmpty() {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                orderList.clear();
                adapter.notifyDataSetChanged();
                updateEmptyState();
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchActiveOrders();
    }

    private void updateEmptyState() {
        if (orderList.isEmpty()) {
            emptyStateLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyStateLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onStatusChange(Order order, String newStatus) {
        // Immediately update UI for better responsiveness
        order.setStatus(newStatus.toUpperCase());
        adapter.notifyDataSetChanged();

        // Update on server
        updateOrderStatus(order.getOrderId(), newStatus.toLowerCase());
        
        if (newStatus.equalsIgnoreCase("collected")) {
            recyclerView.postDelayed(() -> {
                orderList.remove(order);
                adapter.notifyDataSetChanged();
                updateEmptyState();
            }, 1000);
        }
    }

    private void updateOrderStatus(String orderId, String newStatus) {
        // Log for debugging
        android.util.Log.d("STATUS_UPDATE", "Updating order " + orderId + " to " + newStatus);

        String url = "https://wmc.ms.wits.ac.za/students/sgroup2676/d2dGroupProject/oderTrackingApp/orders/updateOder.php"
                + "?order_id=" + orderId
                + "&status=" + newStatus;

        Request request = new Request.Builder().url(url).get().build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> 
                        Toast.makeText(getContext(), "Server sync failed", Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (response.isSuccessful()) {
                            Toast.makeText(getContext(), "Order status updated: " + newStatus.toUpperCase(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}
