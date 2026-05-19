package com.example.d2d;

import android.content.Context;
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

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * OrderStatusFragment uses confirm_takeaway.xml as the default customer Orders
 * tab.
 * Handles three states:
 * 1. Empty state — no orders (default)
 * 2. Pending confirmation — staff assigned, awaiting customer confirm
 * 3. Active tracking — order confirmed, shows live order card with status
 */
public class OrderStatusFragment extends Fragment {

    private final OkHttpClient client = new OkHttpClient();

    // UI references
    private View emptyState;
    private View confirmLayout;
    private View trackingLayout;
    private TextView orderDescription;
    private TextView trackingOrderId;
    private TextView trackingRestaurantName;
    private TextView trackingStatusText;
    private TextView trackingHint;
    private View trackingStatusBadge;
    private Button confirmBtn;
    private Button cancelBtn;
    private Button rateServiceBtn;

    private String activeOrderId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.confirm_takeaway, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Hide the back button since this is inside bottom navigation
        View backBtn = view.findViewById(R.id.back_to_home);
        if (backBtn != null)
            backBtn.setVisibility(View.GONE);

        // The browse button on the empty state should be visible to match the Activity
        View browseBtn = view.findViewById(R.id.browse_restaurants_btn);
        if (browseBtn != null) {
            browseBtn.setVisibility(View.VISIBLE);
            browseBtn.setOnClickListener(v -> {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).selectTab(0);
                }
            });
        }

        // Empty state
        emptyState = view.findViewById(R.id.empty_state_layout);

        // Pending confirmation state
        confirmLayout = view.findViewById(R.id.confirm_layout);
        orderDescription = view.findViewById(R.id.order_description);
        confirmBtn = view.findViewById(R.id.confirm_order);
        cancelBtn = view.findViewById(R.id.customer_cancel_order);

        // Active tracking state
        trackingLayout = view.findViewById(R.id.tracking_layout);
        trackingOrderId = view.findViewById(R.id.tracking_order_id);
        trackingRestaurantName = view.findViewById(R.id.tracking_restaurant_name);
        trackingStatusText = view.findViewById(R.id.tracking_status_text);
        trackingHint = view.findViewById(R.id.tracking_hint);
        trackingStatusBadge = view.findViewById(R.id.tracking_status_badge);
        rateServiceBtn = view.findViewById(R.id.rate_service_btn);

        // Default: empty state visible, others hidden
        showState("empty");

        // Wire confirm button
        if (confirmBtn != null) {
            confirmBtn.setOnClickListener(v -> confirmOrder());
        }

        // Wire cancel button
        if (cancelBtn != null) {
            cancelBtn.setOnClickListener(v -> dismissOrder());
        }

        // Wire rate service button
        if (rateServiceBtn != null) {
            rateServiceBtn.setOnClickListener(v -> {
                DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
                android.database.Cursor cursor = dbHelper.getActiveOrder();
                String orderId = "";
                String restaurant = "";
                if (cursor != null && cursor.moveToFirst()) {
                    orderId = cursor.getString(cursor.getColumnIndexOrThrow("order_id"));
                    restaurant = cursor.getString(cursor.getColumnIndexOrThrow("restaurant_name"));
                    cursor.close();
                }
                Intent intent = new Intent(getActivity(), RateServiceActivity.class);
                intent.putExtra("order_id", orderId);
                intent.putExtra("restaurant_name", restaurant);
                startActivity(intent);
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateGreeting();
        refreshState();
        pollServerForPendingOrders();
    }

    private void updateGreeting() {
        if (getView() == null) return;
        android.widget.TextView greetingText = getView().findViewById(R.id.greeting_text);
        if (greetingText != null) {
            DatabaseHelper dbHelper = new DatabaseHelper(getContext());
            android.database.Cursor userCursor = dbHelper.getActiveUser();
            String firstName = "";
            if (userCursor != null && userCursor.moveToFirst()) {
                @android.annotation.SuppressLint("Range") String name = userCursor.getString(userCursor.getColumnIndex("name"));
                if (name != null && !name.isEmpty() && !name.startsWith("User ")) {
                    firstName = name.split("[ .]")[0]; // Split by space or dot
                    if (firstName.length() > 0) {
                        firstName = firstName.substring(0, 1).toUpperCase() + firstName.substring(1).toLowerCase();
                    }
                } else {
                    // Fallback to email parsing if name wasn't provided by API
                    @android.annotation.SuppressLint("Range") String email = userCursor.getString(userCursor.getColumnIndex("email"));
                    if (email != null && email.contains("@")) {
                        String namePart = email.split("@")[0].replaceAll("[0-9]", "");
                        if (namePart.length() > 0) {
                            firstName = namePart.substring(0, 1).toUpperCase() + namePart.substring(1).toLowerCase();
                        }
                    }
                }
                userCursor.close();
            }
            if (firstName.isEmpty()) firstName = "Student";
            greetingText.setText("Hello " + firstName);
        }
    }

    /**
     * Shows one of the three states and hides the others.
     */
    private void showState(String state) {
        if (emptyState != null)
            emptyState.setVisibility("empty".equals(state) ? View.VISIBLE : View.GONE);
        if (confirmLayout != null)
            confirmLayout.setVisibility("pending".equals(state) ? View.VISIBLE : View.GONE);
        if (trackingLayout != null)
            trackingLayout.setVisibility("tracking".equals(state) ? View.VISIBLE : View.GONE);
    }

    /**
     * Checks local SQLite for orders and decides which state to show:
     * 1. Active order (preparing/ready) → tracking state
     * 2. Pending confirmation → confirm state
     * 3. Nothing → empty state
     */
    private void refreshState() {
        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());

        // Priority 1: Check for active orders (already confirmed)
        android.database.Cursor activeCursor = dbHelper.getActiveOrder();
        if (activeCursor != null && activeCursor.moveToFirst()) {
            String orderId = activeCursor.getString(activeCursor.getColumnIndexOrThrow("order_id"));
            String restaurant = activeCursor.getString(activeCursor.getColumnIndexOrThrow("restaurant_name"));
            String status = activeCursor.getString(activeCursor.getColumnIndexOrThrow("status"));
            activeCursor.close();

            showState("tracking");
            showTrackingCard(orderId, restaurant, status);
            return;
        }
        if (activeCursor != null)
            activeCursor.close();

        // Priority 2: Check for pending confirmation orders
        android.database.Cursor pendingCursor = dbHelper.getPendingConfirmationOrder();
        if (pendingCursor != null && pendingCursor.moveToFirst()) {
            activeOrderId = pendingCursor.getString(pendingCursor.getColumnIndexOrThrow("order_id"));
            String restaurantName = pendingCursor.getString(pendingCursor.getColumnIndexOrThrow("restaurant_name"));
            pendingCursor.close();

            showState("pending");
            if (orderDescription != null) {
                orderDescription
                        .setText("The restaurant \"" + restaurantName + "\" has initialized an order for you (Order #"
                                + activeOrderId + "). Please confirm to start tracking.");
            }
            return;
        }
        if (pendingCursor != null)
            pendingCursor.close();

        // Default: empty state
        activeOrderId = null;
        showState("empty");
    }

    /**
     * Populates the tracking card with order details and dynamic status.
     */
    private void showTrackingCard(String orderId, String restaurant, String status) {
        if (trackingOrderId != null)
            trackingOrderId.setText("ORDER #" + orderId);
        if (trackingRestaurantName != null)
            trackingRestaurantName.setText(restaurant.toUpperCase());

        String displayStatus = status.toUpperCase();
        String hint = "Your order is being prepared…";

        if ("ready".equalsIgnoreCase(status)) {
            displayStatus = "READY";
            hint = "Your order is ready for collection!";
            if (trackingStatusBadge != null)
                trackingStatusBadge.setBackgroundResource(R.drawable.glass_green);
            if (trackingStatusText != null)
                trackingStatusText.setTextColor(android.graphics.Color.parseColor("#2E7D32"));
        } else if ("preparing".equalsIgnoreCase(status)) {
            displayStatus = "PREPARING";
            hint = "Your order is being prepared…";
        } else if ("pending".equalsIgnoreCase(status)) {
            displayStatus = "PENDING";
            hint = "Your order has been placed.";
        }

        if (trackingStatusText != null)
            trackingStatusText.setText(displayStatus);
        if (trackingHint != null)
            trackingHint.setText(hint);
    }

    /**
     * Confirms the pending order — updates local DB status and syncs with server.
     */
    private void confirmOrder() {
        if (activeOrderId == null)
            return;

        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
        dbHelper.updateOrderStatus(activeOrderId, "preparing");

        // Sync confirmation to server
        String url = "https://wmc.ms.wits.ac.za/students/sgroup2676/d2dGroupProject/oderTrackingApp/orders/updateOder.php?order_id="
                + activeOrderId;
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
            }
        });

        Toast.makeText(getContext(), "Order #" + activeOrderId + " confirmed!", Toast.LENGTH_SHORT).show();

        // Refresh — will now show tracking state since order is "preparing"
        refreshState();
    }

    /**
     * Dismisses the pending order — removes it from local DB.
     */
    private void dismissOrder() {
        if (activeOrderId == null)
            return;

        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
        dbHelper.updateOrderStatus(activeOrderId, "Collected");

        Toast.makeText(getContext(), "Order dismissed.", Toast.LENGTH_SHORT).show();
        refreshState();
    }

    /**
     * Polls the server for orders assigned to this customer.
     * If a new order exists on the server that isn't in the local SQLite DB,
     * it inserts it as "pending_confirmation" so the confirm card appears.
     */
    private void pollServerForPendingOrders() {
        SharedPreferences pref = requireActivity().getSharedPreferences("D2D_PREFS", Context.MODE_PRIVATE);
        String userId = pref.getString("user_id", "");
        android.util.Log.d("POLL_DEBUG", "Polling with user_id: [" + userId + "]");
        if (userId.isEmpty())
            return;

        String url = "https://wmc.ms.wits.ac.za/students/sgroup2676/d2dGroupProject/oderTrackingApp/orders/displayOderHistory.php"
                + "?user_id=" + userId + "&role=customer";
        android.util.Log.d("POLL_DEBUG", "Poll URL: " + url);

        Request request = new Request.Builder().url(url).get().build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                android.util.Log.e("POLL_DEBUG", "Poll FAILED: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful() || response.body() == null) {
                    android.util.Log.e("POLL_DEBUG", "Poll response unsuccessful: " + response.code());
                    return;
                }

                try {
                    String responseData = response.body().string().trim();
                    android.util.Log.d("POLL_DEBUG", "Poll response: " + responseData);

                    if (!responseData.startsWith("[")) {
                        android.util.Log.d("POLL_DEBUG", "Response is NOT a JSON array, skipping");
                        return;
                    }

                    org.json.JSONArray ordersArray = new org.json.JSONArray(responseData);
                    android.util.Log.d("POLL_DEBUG", "Found " + ordersArray.length() + " orders from server");
                    if (ordersArray.length() == 0)
                        return;

                    DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
                    boolean foundNew = false;

                    for (int i = 0; i < ordersArray.length(); i++) {
                        org.json.JSONObject orderObj = ordersArray.getJSONObject(i);
                        String serverId = String.valueOf(orderObj.optInt("order_id", 0));
                        String serverStatus = orderObj.optString("status", "pending");
                        String restaurantName = orderObj.optString("restaurant_name", "Restaurant");

                        android.util.Log.d("POLL_DEBUG", "Order #" + serverId + " status=" + serverStatus + " restaurant=" + restaurantName);

                        if (serverId.equals("0"))
                            continue;

                        if (!"collected".equalsIgnoreCase(serverStatus)) {
                            android.database.Cursor localCheck = dbHelper.getReadableDatabase()
                                    .rawQuery("SELECT * FROM orders WHERE order_id = ?", new String[] { serverId });
                            boolean existsLocally = (localCheck != null && localCheck.getCount() > 0);
                            if (localCheck != null)
                                localCheck.close();

                            android.util.Log.d("POLL_DEBUG", "Order #" + serverId + " existsLocally=" + existsLocally);

                            if (!existsLocally) {
                                String userId2 = requireActivity()
                                        .getSharedPreferences("D2D_PREFS", Context.MODE_PRIVATE)
                                        .getString("user_id", "unknown");
                                dbHelper.saveOrder(serverId, restaurantName, "pending_confirmation", "R 0.00", "Me",
                                        userId2);
                                foundNew = true;
                                android.util.Log.d("POLL_DEBUG", "SAVED new pending_confirmation order #" + serverId);
                            }
                        }
                    }

                    if (foundNew && getActivity() != null) {
                        android.util.Log.d("POLL_DEBUG", "Refreshing UI with new orders");
                        getActivity().runOnUiThread(() -> refreshState());
                    }

                } catch (Exception e) {
                    android.util.Log.e("POLL_DEBUG", "Poll parse error: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }
}
