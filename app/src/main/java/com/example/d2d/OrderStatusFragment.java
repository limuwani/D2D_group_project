package com.example.d2d;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * OrderStatusFragment displays the customer's current active takeaway order.
 * It automatically queries SQLite and refreshes dynamically when the tab is swiped or loaded.
 * On each resume, it also polls the server for new orders created by staff that the customer
 * hasn't yet confirmed, and surfaces a confirmation card inline.
 */
public class OrderStatusFragment extends Fragment {

    private final OkHttpClient client = new OkHttpClient();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.cus_order_status, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Hide navigation element overrides inside bottom navigation
        View backBtn = view.findViewById(R.id.back_btn);
        View browseBtn = view.findViewById(R.id.browse_restaurants_btn);
        
        if (backBtn != null) backBtn.setVisibility(View.GONE);
        if (browseBtn != null) browseBtn.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
        // First show whatever we have locally, then poll the server for new orders
        refreshActiveOrderStatus();
        pollServerForPendingOrders();
    }

    /**
     * Queries SQLite for the latest active customer order and auto-loads the details card.
     */
    private void refreshActiveOrderStatus() {
        View view = getView();
        if (view == null) return;

        View orderCard = view.findViewById(R.id.active_order_card);
        View emptyState = view.findViewById(R.id.empty_state_layout);
        View confirmCard = view.findViewById(R.id.pending_confirm_card);
        android.widget.TextView orderIdText = view.findViewById(R.id.status);
        android.widget.TextView restaurantText = view.findViewById(R.id.restaurant_name);

        // Fetch active order from SQLite DB (excludes pending_confirmation)
        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
        android.database.Cursor cursor = dbHelper.getActiveOrder();

        if (cursor != null && cursor.moveToFirst()) {
            // Found an active order - auto-load details
            String id = cursor.getString(cursor.getColumnIndexOrThrow("order_id"));
            String restaurant = cursor.getString(cursor.getColumnIndexOrThrow("restaurant_name"));
            cursor.close();

            if (emptyState != null) emptyState.setVisibility(View.GONE);
            if (confirmCard != null) confirmCard.setVisibility(View.GONE);
            if (orderCard != null) {
                orderCard.setVisibility(View.VISIBLE);
                if (orderIdText != null) orderIdText.setText("ORDER #" + id);
                if (restaurantText != null) restaurantText.setText(restaurant.toUpperCase());

                orderCard.setOnClickListener(v -> {
                    Intent intent = new Intent(getActivity(), RateServiceActivity.class);
                    intent.putExtra("order_id", id);
                    intent.putExtra("restaurant_name", restaurant);
                    startActivity(intent);
                });
            }
        } else {
            // Check for pending_confirmation orders to show the confirm card
            if (cursor != null) cursor.close();
            android.database.Cursor pendingCursor = dbHelper.getPendingConfirmationOrder();

            if (pendingCursor != null && pendingCursor.moveToFirst()) {
                String pendingId = pendingCursor.getString(pendingCursor.getColumnIndexOrThrow("order_id"));
                String pendingRestaurant = pendingCursor.getString(pendingCursor.getColumnIndexOrThrow("restaurant_name"));
                pendingCursor.close();

                if (emptyState != null) emptyState.setVisibility(View.GONE);
                if (orderCard != null) orderCard.setVisibility(View.GONE);
                showPendingConfirmCard(pendingId, pendingRestaurant);
            } else {
                // No active order exists - auto-load clean empty state
                if (pendingCursor != null) pendingCursor.close();
                if (emptyState != null) emptyState.setVisibility(View.VISIBLE);
                if (orderCard != null) orderCard.setVisibility(View.GONE);
                if (confirmCard != null) confirmCard.setVisibility(View.GONE);
            }
        }
    }

    /**
     * Shows the inline pending confirmation card with Confirm/Dismiss actions.
     */
    private void showPendingConfirmCard(String orderId, String restaurantName) {
        View view = getView();
        if (view == null) return;

        View confirmCard = view.findViewById(R.id.pending_confirm_card);
        android.widget.TextView confirmTitle = view.findViewById(R.id.pending_confirm_title);
        android.widget.TextView confirmDesc = view.findViewById(R.id.pending_confirm_description);
        android.widget.Button confirmBtn = view.findViewById(R.id.pending_confirm_btn);
        android.widget.Button dismissBtn = view.findViewById(R.id.pending_dismiss_btn);

        if (confirmCard == null) return;

        confirmCard.setVisibility(View.VISIBLE);
        if (confirmTitle != null) confirmTitle.setText("Order #" + orderId);
        if (confirmDesc != null) confirmDesc.setText("\"" + restaurantName + "\" has initialized an order for you. Tap Confirm to start tracking.");

        if (confirmBtn != null) {
            confirmBtn.setOnClickListener(v -> {
                // Update local status from pending_confirmation -> preparing
                DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
                dbHelper.updateOrderStatus(orderId, "preparing");

                // Also call the server to confirm the order
                sendServerConfirmation(orderId);

                Toast.makeText(getContext(), "Order #" + orderId + " confirmed!", Toast.LENGTH_SHORT).show();
                refreshActiveOrderStatus();
            });
        }

        if (dismissBtn != null) {
            dismissBtn.setOnClickListener(v -> {
                // Remove the pending order from local DB
                DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
                dbHelper.updateOrderStatus(orderId, "Collected");
                Toast.makeText(getContext(), "Order dismissed.", Toast.LENGTH_SHORT).show();
                refreshActiveOrderStatus();
            });
        }
    }

    /**
     * Sends confirmation to the server by calling updateOder.php to change status to 'preparing'.
     */
    private void sendServerConfirmation(String orderId) {
        String url = "https://wmc.ms.wits.ac.za/students/sgroup2676/d2dGroupProject/oderTrackingApp/orders/updateOder.php?order_id=" + orderId;
        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                // Silent fail — order is confirmed locally regardless
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                // Confirmation synced to server
            }
        });
    }

    /**
     * Polls the server for orders assigned to this customer.
     * If a new order exists on the server that isn't in the local SQLite DB,
     * it inserts it as "pending_confirmation" so the confirmation card appears.
     */
    private void pollServerForPendingOrders() {
        SharedPreferences pref = requireActivity().getSharedPreferences("D2D_PREFS", Context.MODE_PRIVATE);
        String userId = pref.getString("user_id", "");
        if (userId.isEmpty()) return;

        String url = "https://wmc.ms.wits.ac.za/students/sgroup2676/d2dGroupProject/oderTrackingApp/orders/displayOderHistory.php"
                + "?user_id=" + userId + "&role=customer";

        Request request = new Request.Builder().url(url).get().build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                // Offline — rely on local data only
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful() || response.body() == null) return;

                try {
                    String responseData = response.body().string().trim();
                    if (!responseData.startsWith("[")) return; // Not an array — likely "No orders found"

                    org.json.JSONArray ordersArray = new org.json.JSONArray(responseData);
                    if (ordersArray.length() == 0) return;

                    DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
                    boolean foundNew = false;

                    for (int i = 0; i < ordersArray.length(); i++) {
                        org.json.JSONObject orderObj = ordersArray.getJSONObject(i);
                        String serverId = String.valueOf(orderObj.optInt("order_id", 0));
                        String serverStatus = orderObj.optString("status", "pending");
                        String restaurantName = orderObj.optString("restaurant_name", "Restaurant");

                        if (serverId.equals("0")) continue;

                        // If the server has a non-collected order that doesn't exist locally, insert it
                        if (!"collected".equalsIgnoreCase(serverStatus)) {
                            android.database.Cursor localCheck = dbHelper.getReadableDatabase()
                                    .rawQuery("SELECT * FROM orders WHERE order_id = ?", new String[]{serverId});
                            boolean existsLocally = (localCheck != null && localCheck.getCount() > 0);
                            if (localCheck != null) localCheck.close();

                            if (!existsLocally) {
                                // New order from server — save as pending_confirmation
                                dbHelper.saveOrder(serverId, restaurantName, "pending_confirmation", "R 0.00", "Me", userId);
                                foundNew = true;
                            }
                        }
                    }

                    if (foundNew && getActivity() != null) {
                        getActivity().runOnUiThread(() -> refreshActiveOrderStatus());
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
