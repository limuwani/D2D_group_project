// Modified by Anon - Responsive UI & Flow
package com.example.d2d;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.content.Intent;
import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Call;

import com.google.gson.Gson;

public class LoginActivity extends AppCompatActivity {
    OkHttpClient client = new OkHttpClient();
    EditText emailText;
    EditText passwordText;
    Button loginBtn;

    Button signUpBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // --- CHECK FOR ACTIVE SESSION (Auto-Login) ---
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        android.database.Cursor sessionCursor = dbHelper.getActiveSession();
        if (sessionCursor != null && sessionCursor.moveToFirst()) {
            android.database.Cursor userCursor = dbHelper.getActiveUser();
            if (userCursor != null && userCursor.moveToFirst()) {
                @SuppressLint("Range") String userId = userCursor.getString(userCursor.getColumnIndex("user_id"));
                @SuppressLint("Range") String role = userCursor.getString(userCursor.getColumnIndex("role"));
                
                userCursor.close();
                sessionCursor.close();

                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("user_id", userId);
                intent.putExtra("user_role", role);
                startActivity(intent);
                finish();
                return;
            }
            if (userCursor != null) userCursor.close();
        }
        if (sessionCursor != null) sessionCursor.close();

        setContentView(R.layout.login_page);

        emailText = findViewById(R.id.email_edit_text);
        passwordText = findViewById(R.id.password_edit_text);
        loginBtn = findViewById(R.id.sign_in_button);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doLogin(v);
            }
        });

        signUpBtn = findViewById(R.id.sign_up_button);
        signUpBtn.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignUp.class);
            startActivity(intent);
        });
        
        TextView forgotPassword = findViewById(R.id.forgot_password);
        forgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RecoveryStep1Activity.class);
            startActivity(intent);
        });
    }

    public boolean validate(String Email, String Pass) {
        boolean isValid = true;
        if (Email.isEmpty()) {
            emailText.setBackgroundResource(R.drawable.edittext_error_style);
            emailText.setError("Email is required");
            isValid = false;
        } else {
            emailText.setBackgroundResource(R.drawable.edittext_bg);
        }

        if (Pass.isEmpty()) {
            passwordText.setBackgroundResource(R.drawable.edittext_error_style);
            passwordText.setError("Password is required");
            isValid = false;
        } else {
            passwordText.setBackgroundResource(R.drawable.edittext_bg);
        }
        return isValid;
    }

    public void doLogin(View view) {
        Log.d("DEBUG", "Sign in button clicked!");
        String username = emailText.getText().toString().trim();
        String password = passwordText.getText().toString().trim();

        if (!validate(username, password)) {
            return;
        }

        RequestBody body = new FormBody.Builder()
                .add("username", username)
                .add("password", password)
                .build();

        Request request = new Request.Builder()
                .url("https://wmc.ms.wits.ac.za/students/sgroup2676/d2dGroupProject/oderTrackingApp/users/login.php")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String jsonData = response.body().string();
                    Log.d("JSON_RESPONSE", jsonData);

                    Gson gson = new Gson();
                    LoginResponse user = gson.fromJson(jsonData, LoginResponse.class);

                    String parsedUserId = null;
                    String parsedRole = null;

                    if (user != null && "success".equals(user.getStatus())) {
                        parsedUserId = user.getUser_id();
                        parsedRole = user.getRole();
                        
                        // Robust manual JSON parsing fallback for all possible layouts (root or nested "data")
                        try {
                            org.json.JSONObject jsonObj = new org.json.JSONObject(jsonData);
                            if (parsedUserId == null || parsedUserId.isEmpty()) {
                                parsedUserId = jsonObj.optString("user_id", "");
                                if (parsedUserId.isEmpty() && jsonObj.has("data")) {
                                    org.json.JSONObject dataObj = jsonObj.optJSONObject("data");
                                    if (dataObj != null) parsedUserId = dataObj.optString("user_id", "");
                                }
                            }
                            if (parsedRole == null || parsedRole.isEmpty()) {
                                parsedRole = jsonObj.optString("user_role", "");
                                if (parsedRole.isEmpty()) parsedRole = jsonObj.optString("role", "");
                                if (parsedRole.isEmpty() && jsonObj.has("data")) {
                                    org.json.JSONObject dataObj = jsonObj.optJSONObject("data");
                                    if (dataObj != null) {
                                        parsedRole = dataObj.optString("role", "");
                                        if (parsedRole.isEmpty()) parsedRole = dataObj.optString("user_role", "");
                                    }
                                }
                            }

                            // Robust extraction of restaurant_id
                            String restaurantId = jsonObj.optString("restaurant_id", "");
                            if (restaurantId.isEmpty()) {
                                restaurantId = jsonObj.optString("restaurantId", "");
                            }
                            if (restaurantId.isEmpty() && jsonObj.has("data")) {
                                org.json.JSONObject dataObj = jsonObj.optJSONObject("data");
                                if (dataObj != null) {
                                    restaurantId = dataObj.optString("restaurant_id", "");
                                    if (restaurantId.isEmpty()) {
                                        restaurantId = dataObj.optString("restaurantId", "");
                                    }
                                }
                            }

                            android.content.SharedPreferences pref = getSharedPreferences("D2D_PREFS", MODE_PRIVATE);
                            android.content.SharedPreferences.Editor editor = pref.edit()
                                .putString("user_id", parsedUserId)
                                .putString("user_role", parsedRole);
                            
                            if (!restaurantId.isEmpty() && !"null".equalsIgnoreCase(restaurantId) && !"0".equals(restaurantId)) {
                                editor.putString("restaurant_id", restaurantId);
                            } else {
                                editor.putString("restaurant_id", "10000"); // Safe fallback
                            }
                            editor.apply();
                        } catch (Exception e) {
                            Log.e("LOGIN_ERROR", "Robust JSON extraction error", e);
                        }

                        // --- STANDARD SQLITE PERSISTENCE ---
                        DatabaseHelper dbHelper = new DatabaseHelper(LoginActivity.this);
                        dbHelper.saveUser(parsedUserId, parsedRole, username, "User " + parsedUserId);
                        dbHelper.saveSession(parsedUserId, "mock_token_" + System.currentTimeMillis());

                        // Background sync of staff profile/restaurant details from userProfile.php
                        if ("staff".equalsIgnoreCase(parsedRole) && parsedUserId != null) {
                            fetchAndSaveStaffRestaurant(parsedUserId);
                        }
                    }

                    final String finalUserId = parsedUserId;
                    final String finalRole = parsedRole;

                    runOnUiThread(() -> {
                        if (user != null && "success".equals(user.getStatus())) {
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra("user_id", finalUserId);
                            intent.putExtra("user_role", finalRole);
                            startActivity(intent);
                            finish();
                        } else {
                            android.widget.Toast.makeText(LoginActivity.this, "Invalid credentials. Please try again.", android.widget.Toast.LENGTH_LONG).show();
                            passwordText.setText("");
                            passwordText.setBackgroundResource(R.drawable.edittext_error_style);
                        }
                    });
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("LOGIN_ERROR", "Network failure", e);
                runOnUiThread(() -> {
                    android.widget.Toast.makeText(LoginActivity.this, "Network error. Please check your connection.", android.widget.Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void fetchAndSaveStaffRestaurant(String userId) {
        okhttp3.OkHttpClient client = new okhttp3.OkHttpClient();
        String url = "https://wmc.ms.wits.ac.za/students/sgroup2676/d2dGroupProject/oderTrackingApp/users/userProfile.php?user_id=" + userId + "&user_role=staff";
        okhttp3.Request request = new okhttp3.Request.Builder().url(url).build();
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NonNull okhttp3.Call call, @NonNull java.io.IOException e) {}

            @Override
            public void onResponse(@NonNull okhttp3.Call call, @NonNull okhttp3.Response response) throws java.io.IOException {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String jsonData = response.body().string();
                        org.json.JSONObject obj = new org.json.JSONObject(jsonData);
                        if ("success".equals(obj.optString("status")) && obj.has("data")) {
                            org.json.JSONObject data = obj.getJSONObject("data");
                            String restaurantId = data.optString("restaurant_id", "");
                            if (restaurantId.isEmpty()) {
                                restaurantId = data.optString("restaurantId", "");
                            }
                            if (!restaurantId.isEmpty() && !"null".equalsIgnoreCase(restaurantId) && !"0".equals(restaurantId)) {
                                getSharedPreferences("D2D_PREFS", MODE_PRIVATE).edit()
                                    .putString("restaurant_id", restaurantId)
                                    .apply();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
