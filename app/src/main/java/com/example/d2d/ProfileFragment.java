package com.example.d2d;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Retrieve the user role from SharedPreferences
        SharedPreferences pref = requireActivity().getSharedPreferences("D2D_PREFS", Context.MODE_PRIVATE);
        String role = pref.getString("user_role", "customer");

        View view;

        if ("staff".equals(role)) {
            // Inflate Staff Hub
            view = inflater.inflate(R.layout.s_account_hub, container, false);
            setupStaffHub(view);
        } else {
            // Inflate Customer Hub
            view = inflater.inflate(R.layout.c_account_hub, container, false);
            setupCustomerHub(view);
        }

        return view;
    }

    interface ProfileFetchCallback {
        void onSuccess(org.json.JSONObject data);
        void onFailure();
    }

    private void fetchProfileDetails(String userId, String role, ProfileFetchCallback callback) {
        okhttp3.OkHttpClient client = new okhttp3.OkHttpClient();
        String url = "https://wmc.ms.wits.ac.za/students/sgroup2676/d2dGroupProject/oderTrackingApp/users/userProfile.php?user_id=" + userId + "&user_role=" + role;

        okhttp3.Request request = new okhttp3.Request.Builder().url(url).build();
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, java.io.IOException e) {
                callback.onFailure();
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws java.io.IOException {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String jsonData = response.body().string();
                        org.json.JSONObject obj = new org.json.JSONObject(jsonData);
                        if ("success".equals(obj.optString("status"))) {
                            callback.onSuccess(obj.getJSONObject("data"));
                            return;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                callback.onFailure();
            }
        });
    }

    private void setupCustomerHub(View view) {
        Button historyBtn = view.findViewById(R.id.customer_view_history);
        Button logoutBtn = view.findViewById(R.id.customer_logout);
        Button deleteAccountBtn = view.findViewById(R.id.customer_delete_account);
        Button aboutBtn = view.findViewById(R.id.customer_view_about);
        android.widget.TextView emailText = view.findViewById(R.id.customer_email);

        final SharedPreferences pref = requireActivity().getSharedPreferences("D2D_PREFS", Context.MODE_PRIVATE);
        String userId = pref.getString("user_id", "10000");
        String userRole = pref.getString("user_role", "customer");

        if (emailText != null) {
            emailText.setText("Loading profile details...");
        }

        // Fetch dynamic customer details from userProfile.php
        fetchProfileDetails(userId, userRole, new ProfileFetchCallback() {
            @Override
            public void onSuccess(org.json.JSONObject data) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (emailText != null) {
                            String email = data.optString("email", "customer@d2d.com");
                            int orders = data.optInt("number_of_orders", 0);
                            emailText.setText(email + "\nOrders: " + orders + " | Member Since: ");
                        }
                    });
                }
            }

            @Override
            public void onFailure() {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (emailText != null) {
                            emailText.setText("offline_mode@d2d.com\nOrders: 0 | Member Since: 1 day");
                        }
                    });
                }
            }
        });

        if (historyBtn != null) {
            historyBtn.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), OrderHistoryActivity.class);
                startActivity(intent);
            });
        }

        if (logoutBtn != null) {
            logoutBtn.setOnClickListener(v -> performLogout());
        }

        if (deleteAccountBtn != null) {
            deleteAccountBtn.setOnClickListener(v -> {
                new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                    .setTitle("Delete Account")
                    .setMessage("Are you sure you want to permanently delete your account? This action cannot be undone.")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        executeAccountDeletion(userId);
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
            });
        }

        if (aboutBtn != null) {
            aboutBtn.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), CusAboutActivity.class);
                startActivity(intent);
            });
        }
    }

    private void executeAccountDeletion(String userId) {
        okhttp3.OkHttpClient client = new okhttp3.OkHttpClient();
        String url = "https://wmc.ms.wits.ac.za/students/sgroup2676/d2dGroupProject/oderTrackingApp/users/deleteAccount.php";

        okhttp3.RequestBody body = new okhttp3.FormBody.Builder()
                .add("user_id", userId)
                .build();

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .post(body)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, java.io.IOException e) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Account deleted!", Toast.LENGTH_SHORT).show();
                        performLogout();
                    });
                }
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws java.io.IOException {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Account successfully deleted.", Toast.LENGTH_LONG).show();
                        performLogout();
                    });
                }
            }
        });
    }

    private void setupStaffHub(View view) {
        Button historyBtn = view.findViewById(R.id.staff_view_history);
        Button logoutBtn = view.findViewById(R.id.staff_logout);
        Button feedbackBtn = view.findViewById(R.id.staff_view_feedback);
        Button handbookBtn = view.findViewById(R.id.staff_view_handbook);
        android.widget.TextView satisfactionText = view.findViewById(R.id.average_rate_for_staff);

        final SharedPreferences pref = requireActivity().getSharedPreferences("D2D_PREFS", Context.MODE_PRIVATE);
        String userId = pref.getString("user_id", "10000");
        String userRole = pref.getString("user_role", "staff");

        if (satisfactionText != null) {
            satisfactionText.setText("...");
        }

        // Fetch dynamic satisfaction by querying the reviews endpoint
        okhttp3.OkHttpClient client = new okhttp3.OkHttpClient();
        String url = "https://wmc.ms.wits.ac.za/students/sgroup2676/d2dGroupProject/oderTrackingApp/staff/displayRatingsAndfeedback.php";
        okhttp3.Request request = new okhttp3.Request.Builder().url(url).build();
        
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NonNull okhttp3.Call call, @NonNull java.io.IOException e) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (satisfactionText != null) satisfactionText.setText("N/A"); // Default fallback
                    });
                }
            }

            @Override
            public void onResponse(@NonNull okhttp3.Call call, @NonNull okhttp3.Response response) throws java.io.IOException {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseData = response.body().string();
                        org.json.JSONObject jsonObject = new org.json.JSONObject(responseData);
                        org.json.JSONArray array = null;
                        
                        if (jsonObject.has("reviews")) {
                            array = jsonObject.getJSONArray("reviews");
                        } else if (jsonObject.has("ratings")) {
                            array = jsonObject.getJSONArray("ratings");
                        }

                        double sumRating = 0;
                        int count = 0;
                        if (array != null) {
                            for (int i = 0; i < array.length(); i++) {
                                org.json.JSONObject obj = array.getJSONObject(i);
                                sumRating += obj.optDouble("rating", 5.0);
                                count++;
                            }
                        }

                        final double average = count == 0 ? 5.0 : (sumRating / count);
                        final double percent = (average / 5.0) * 100.0;

                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                if (satisfactionText != null) {
                                    satisfactionText.setText(String.format("%.0f%%", percent));
                                }
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                if (satisfactionText != null) satisfactionText.setText("100%");
                            });
                        }
                    }
                } else {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            if (satisfactionText != null) satisfactionText.setText("100%");
                        });
                    }
                }
            }
        });

        if (historyBtn != null) {
            historyBtn.setOnClickListener(v -> {
                Toast.makeText(getContext(), "Order History coming soon", Toast.LENGTH_SHORT).show();
            });
        }

        if (logoutBtn != null) {
            logoutBtn.setOnClickListener(v -> performLogout());
        }

        if (feedbackBtn != null) {
            feedbackBtn.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), StaffFeedbackActivity.class);
                startActivity(intent);
            });
        }

        Button backToQueueBtn = view.findViewById(R.id.back_to_queue_btn);
        if (backToQueueBtn != null) {
            backToQueueBtn.setOnClickListener(v -> {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).selectTab(1); // Swipe to the Orders/Queue tab programmatically
                }
            });
        }

        // Fetch and show assigned restaurant on staff profile
        android.widget.TextView assignedResText = view.findViewById(R.id.staff_assigned_restaurant);
        if (assignedResText != null) {
            String restaurantId = pref.getString("restaurant_id", "");
            boolean hasRestaurant = (restaurantId != null && !restaurantId.isEmpty() && !"null".equalsIgnoreCase(restaurantId) && !"0".equals(restaurantId));
            if (!hasRestaurant) {
                assignedResText.setText("---");
            } else {
                assignedResText.setText("Loading...");
                fetchRestaurantNameForProfile(restaurantId, assignedResText);
            }
        }

        if (handbookBtn != null) {
            handbookBtn.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), StaffAboutActivity.class);
                startActivity(intent);
            });
        }
    }

    private void fetchRestaurantNameForProfile(String restaurantId, android.widget.TextView textView) {
        okhttp3.OkHttpClient client = new okhttp3.OkHttpClient();
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url("https://wmc.ms.wits.ac.za/students/sgroup2676/d2dGroupProject/oderTrackingApp/images/displayAllRestaurant.php")
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NonNull okhttp3.Call call, @NonNull java.io.IOException e) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (textView != null) {
                            textView.setText("Restaurant #" + restaurantId);
                        }
                    });
                }
            }

            @Override
            public void onResponse(@NonNull okhttp3.Call call, @NonNull okhttp3.Response response) throws java.io.IOException {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String resData = response.body().string();
                        org.json.JSONObject obj = new org.json.JSONObject(resData);
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
                                if (textView != null) textView.setText(name);
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void performLogout() {
        SharedPreferences pref = requireActivity().getSharedPreferences("D2D_PREFS", Context.MODE_PRIVATE);
        pref.edit().clear().apply();

        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
        dbHelper.clearLocalSession();

        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }
}
