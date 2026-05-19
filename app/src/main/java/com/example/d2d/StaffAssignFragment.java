package com.example.d2d;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
 * StaffAssignFragment manages new order initialization directly inside the main staff dashboard tab.
 * It queries the student server search API to find customers and saves initialized orders locally.
 */
public class StaffAssignFragment extends Fragment {

    private final OkHttpClient client = new OkHttpClient();
    private EditText emailEditText;
    private EditText restaurantEditText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the s_assign_order XML layout resource for this fragment
        return inflater.inflate(R.layout.s_assign_order, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Hide back button since this fragment is hosted within the main dashboard container
        View backBtn = view.findViewById(R.id.back_btn);
        if (backBtn != null) {
            backBtn.setVisibility(View.GONE);
        }

        // Initialize inputs
        emailEditText = view.findViewById(R.id.fullname_edit_text);
        restaurantEditText = view.findViewById(R.id.selected_restaurant);

        // Make restaurant field read-only — it is set from the staff's assigned restaurant
        restaurantEditText.setFocusable(false);
        restaurantEditText.setClickable(false);

        // Load the staff's assigned restaurant name from their profile
        android.content.SharedPreferences pref = requireActivity().getSharedPreferences("D2D_PREFS", android.content.Context.MODE_PRIVATE);
        String restaurantId = pref.getString("restaurant_id", "");
        final boolean hasRestaurant = (restaurantId != null && !restaurantId.isEmpty() && !"null".equalsIgnoreCase(restaurantId) && !"0".equals(restaurantId));

        if (!hasRestaurant) {
            restaurantEditText.setText("---");
        } else {
            restaurantEditText.setText("Loading…");
            fetchRestaurantName(restaurantId);
        }

        // Repurpose cancel button to clear inputs
        View cancelBtn = view.findViewById(R.id.staff_cancel_order);
        if (cancelBtn != null) {
            cancelBtn.setOnClickListener(v -> {
                if (emailEditText != null) emailEditText.setText("");
                Toast.makeText(getContext(), "Inputs cleared", Toast.LENGTH_SHORT).show();
            });
        }

        // Click listener for the "Initialize Order" button
        View initializeBtn = view.findViewById(R.id.send_to_customer);
        if (initializeBtn != null) {
            initializeBtn.setOnClickListener(v -> {
                if (!hasRestaurant) {
                    Toast.makeText(getContext(), "You must be assigned to a restaurant to initialize orders.", Toast.LENGTH_LONG).show();
                    return;
                }
                String email = emailEditText.getText().toString().trim();
                if (email.isEmpty()) {
                    emailEditText.setError("Email required");
                    return;
                }
                searchCustomer(email);
            });
        }
    }

    /**
     * Fetches the restaurant list from the server and finds the name matching the staff's restaurant_id.
     * Populates the (read-only) restaurant field automatically.
     */
    private void fetchRestaurantName(String restaurantId) {
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url("https://wmc.ms.wits.ac.za/students/sgroup2676/d2dGroupProject/oderTrackingApp/images/displayAllRestaurant.php")
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NonNull okhttp3.Call call, @NonNull java.io.IOException e) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (restaurantEditText != null) restaurantEditText.setHint("Restaurant unavailable");
                    });
                }
            }

            @Override
            public void onResponse(@NonNull okhttp3.Call call, @NonNull okhttp3.Response response) throws java.io.IOException {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String json = response.body().string();
                        org.json.JSONObject obj = new org.json.JSONObject(json);
                        org.json.JSONArray arr = obj.optJSONArray("restaurants");
                        String resolvedName = null;
                        if (arr != null) {
                            for (int i = 0; i < arr.length(); i++) {
                                org.json.JSONObject r = arr.getJSONObject(i);
                                if (String.valueOf(r.optInt("restaurant_id")).equals(restaurantId)) {
                                    resolvedName = r.optString("name");
                                    break;
                                }
                            }
                        }
                        final String name = (resolvedName != null) ? resolvedName : "Restaurant #" + restaurantId;
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                if (restaurantEditText != null) restaurantEditText.setText(name);
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void searchCustomer(String email) {
        okhttp3.HttpUrl url = okhttp3.HttpUrl.parse("https://wmc.ms.wits.ac.za/students/sgroup2676/d2dGroupProject/oderTrackingApp/orders/searchCustomers.php")
                .newBuilder()
                .addQueryParameter("email", email)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(), "Network error: check connection.", Toast.LENGTH_SHORT).show()
                    );
                }
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String jsonData = response.body().string();
                    com.google.gson.Gson gson = new com.google.gson.Gson();
                    CustomerSearchResponse customer = gson.fromJson(jsonData, CustomerSearchResponse.class);

                    if (customer != null && "found".equals(customer.status)) {
                        createOrderOnServer(String.valueOf(customer.user_id), customer.user_fname);
                    } else {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() ->
                                    Toast.makeText(getContext(), "Customer email not found.", Toast.LENGTH_SHORT).show()
                            );
                        }
                    }
                } else {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() ->
                                Toast.makeText(getContext(), "Server error", Toast.LENGTH_SHORT).show()
                        );
                    }
                }
            }
        });
    }

    private void createOrderOnServer(String customerId, String fullName) {
        android.content.SharedPreferences pref = requireActivity().getSharedPreferences("D2D_PREFS", android.content.Context.MODE_PRIVATE);
        String staffId = pref.getString("user_id", "10000");
        String restaurantId = pref.getString("restaurant_id", "10000");
        if (restaurantId == null || restaurantId.isEmpty() || "null".equalsIgnoreCase(restaurantId) || "0".equals(restaurantId)) {
            restaurantId = "10000";
        }

        String url = "https://wmc.ms.wits.ac.za/students/sgroup2676/d2dGroupProject/oderTrackingApp/orders/createOder.php"
                + "?customer_id=" + customerId
                + "&staff_id=" + staffId
                + "&restaurant_id=" + restaurantId;

        Request request = new Request.Builder().url(url).get().build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        String localOrderId = String.valueOf(100 + (System.currentTimeMillis() % 100000));
                        saveAndShowOrder(localOrderId, fullName, customerId);
                    });
                }
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    // Success! Since createOder.php returns [] on success, immediately fetch customer order history to resolve the newly generated database order ID.
                    fetchLatestOrderIdAndComplete(customerId, fullName);
                } else {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            String localOrderId = String.valueOf(100 + (System.currentTimeMillis() % 100000));
                            saveAndShowOrder(localOrderId, fullName, customerId);
                        });
                    }
                }
            }
        });
    }

    private void fetchLatestOrderIdAndComplete(String customerId, String fullName) {
        String url = "https://wmc.ms.wits.ac.za/students/sgroup2676/d2dGroupProject/oderTrackingApp/orders/displayOderHistory.php"
                + "?user_id=" + customerId
                + "&role=customer";

        Request request = new Request.Builder().url(url).get().build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        String localOrderId = String.valueOf(100 + (System.currentTimeMillis() % 100000));
                        saveAndShowOrder(localOrderId, fullName, customerId);
                    });
                }
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String orderIdStr = null;
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseData = response.body().string().trim();
                        if (responseData.startsWith("[")) {
                            org.json.JSONArray arr = new org.json.JSONArray(responseData);
                            int maxId = 0;
                            for (int i = 0; i < arr.length(); i++) {
                                org.json.JSONObject obj = arr.getJSONObject(i);
                                int id = obj.optInt("order_id", 0);
                                if (id > maxId) {
                                    maxId = id;
                                }
                            }
                            if (maxId > 0) {
                                orderIdStr = String.valueOf(maxId);
                            }
                        } else if (responseData.startsWith("{")) {
                            org.json.JSONObject obj = new org.json.JSONObject(responseData);
                            int id = obj.optInt("order_id", 0);
                            if (id > 0) {
                                orderIdStr = String.valueOf(id);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                final String finalOrderId = (orderIdStr != null && !orderIdStr.isEmpty()) ? orderIdStr : String.valueOf(100 + (System.currentTimeMillis() % 100000));
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> saveAndShowOrder(finalOrderId, fullName, customerId));
                }
            }
        });
    }

    private void saveAndShowOrder(String orderId, String fullName, String customerId) {
        String restaurant = restaurantEditText.getText().toString().trim();
        if (restaurant.isEmpty() || restaurant.equals("Loading…")) {
            restaurant = "Restaurant"; // Safety fallback
        }

        // Save order dynamically to SQLite database
        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
        dbHelper.saveOrder(orderId, restaurant, "pending_confirmation", "R 0.00", fullName, customerId);

        Toast.makeText(getContext(), "Initialized Order #" + orderId + " for " + fullName, Toast.LENGTH_LONG).show();

        // Clear only the email field for the next order (restaurant stays)
        if (emailEditText != null) emailEditText.setText("");

        // Programmatically switch to Orders/Queue tab (Tab 1) so staff sees their new card
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).selectTab(1);
        }
    }

    private static class CustomerSearchResponse {
        String status;
        int user_id;
        String user_fname;
        String user_lname;
    }
}
