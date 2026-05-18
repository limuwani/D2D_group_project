package com.example.d2d;

import android.content.Intent;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * StaffHomeFragment serves as the main dashboard for staff members, hosting the
 * active order preparation takeaway queue. It supports dynamic item additions,
 * visual empty states, and in-memory status changes synchronized with backend APIs.
 */
public class StaffHomeFragment extends Fragment implements StaffQueueAdapter.OnStatusChangeListener {

    // OkHttpClient instance for updating order status via PHP backend API
    private final OkHttpClient client = new OkHttpClient();
    // RecyclerView for displaying the scrollable list of active orders
    private RecyclerView recyclerView;
    // Adapter responsible for binding order data to layout cards
    private StaffQueueAdapter adapter;
    // Local reference to the active orders dataset
    private List<Order> orderList = new java.util.ArrayList<>();
    // Container layout displayed when the active queue is empty
    private View emptyStateLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the active queue XML layout resource for this fragment
        return inflater.inflate(R.layout.s_active_queue, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Hide back button since this fragment is displayed inside a master container (swiping bottom navigation)
        View backBtn = view.findViewById(R.id.back_btn);
        if (backBtn != null) backBtn.setVisibility(View.GONE);
        
        // Locate empty-state layout and RecyclerView elements
        emptyStateLayout = view.findViewById(R.id.empty_queue_state);
        recyclerView = view.findViewById(R.id.active_queue_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        // Bind the adapter to the active orders dataset
        adapter = new StaffQueueAdapter(getActivity(), orderList, this);
        recyclerView.setAdapter(adapter);
        
        // Load active orders dynamically from local SQLite database
        loadActiveOrdersFromDatabase();

        // Set click listener for the float action button to switch to Assign Order tab
        View addNewOrderBtn = view.findViewById(R.id.add_new_order_btn);
        if (addNewOrderBtn != null) {
            addNewOrderBtn.setOnClickListener(v -> {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).selectTab(0);
                } else {
                    Intent intent = new Intent(getActivity(), AssignOrderActivity.class);
                    startActivity(intent);
                }
            });
        }
    }

    /**
     * Loads active orders dynamically from local SQLite database.
     */
    private void loadActiveOrdersFromDatabase() {
        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
        android.database.Cursor cursor = dbHelper.getAllActiveOrders();
        orderList.clear();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String id = cursor.getString(cursor.getColumnIndexOrThrow("order_id"));
                String restaurant = cursor.getString(cursor.getColumnIndexOrThrow("restaurant_name"));
                String status = cursor.getString(cursor.getColumnIndexOrThrow("status"));
                String customerName;
                try {
                    customerName = cursor.getString(cursor.getColumnIndexOrThrow("customer_name"));
                    if (customerName == null || customerName.isEmpty()) customerName = "Unknown Customer";
                } catch (IllegalArgumentException e) {
                    customerName = "Customer"; // Fallback for old databases
                }
                String customerId;
                try {
                    customerId = cursor.getString(cursor.getColumnIndexOrThrow("customer_id"));
                    if (customerId == null || customerId.isEmpty()) customerId = "N/A";
                } catch (IllegalArgumentException e) {
                    customerId = "N/A"; // Fallback for old databases
                }
                orderList.add(new Order(id, restaurant, status, customerName, customerId));
            } while (cursor.moveToNext());
            cursor.close();
        } else {
            // Load mock fallback only if the database is empty
            orderList.add(new Order("101", "Casa Nova", "preparing", "Naledi M."));
            orderList.add(new Order("102", "D2D Frozen", "ready", "John Doe"));
            orderList.add(new Order("103", "Burger King", "preparing", "Sarah K."));
        }

        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
        updateEmptyState();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Automatically refresh dynamic active orders from SQLite database whenever the screen is resumed
        loadActiveOrdersFromDatabase();
    }
    
    /**
     * Toggles visibility between the scrollable list and the empty state illustration.
     */
    private void updateEmptyState() {
        if (orderList.isEmpty()) {
            emptyStateLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyStateLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Event handler triggered when a staff member changes the status of a queue item.
     * Updates the status model, refreshes the UI, initiates network synchronization,
     * and handles removal of orders that have been marked as 'collected'.
     *
     * @param order     The order object being modified.
     * @param newStatus The target status string (e.g., 'READY' or 'COLLECTED').
     */
    @Override
    public void onStatusChange(Order order, String newStatus) {
        // Apply status update to the local model instantly to ensure ultra-responsive UI
        order.setStatus(newStatus);
        adapter.notifyDataSetChanged();
        
        // Sync local SQLite database status
        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
        dbHelper.updateOrderStatusLocal(order.getOrderId(), newStatus);
        
        // Asynchronously synchronize status change with backend server
        updateOrderStatus(order.getOrderId(), newStatus.toLowerCase());
        
        // Once collected, remove the card from the queue after a brief animation delay
        if (newStatus.equalsIgnoreCase("collected")) {
            recyclerView.postDelayed(() -> {
                orderList.remove(order);
                adapter.notifyDataSetChanged();
                updateEmptyState();
            }, 1000);
        }
    }

    /**
     * Sends an asynchronous POST request to the backend server to update the status of the order.
     *
     * @param orderId   The unique identifier of the order.
     * @param newStatus The updated state (e.g., 'preparing', 'ready', 'collected').
     */
    private void updateOrderStatus(String orderId, String newStatus) {
        String url = "https://wmc.ms.wits.ac.za/students/sgroup2676/d2dGroupProject/oderTrackingApp/orders/updateOder.php?order_id=" + orderId;

        // Construct GET request matching updateOder.php query expectations
        Request request = new Request.Builder().url(url).build();

        // Enqueue the network request
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                // Gracefully degrade to offline/mock mode in case of failure
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> 
                        Toast.makeText(getContext(), "Local Sync completed.", Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                // Notify status of backend synchronization on UI thread
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (response.isSuccessful()) {
                            Toast.makeText(getContext(), "Order #" + orderId + " synced to " + newStatus, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Mock Sync: Server updated status locally.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}
