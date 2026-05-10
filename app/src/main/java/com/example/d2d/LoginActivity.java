// Modified by Anon - Responsive UI & Flow
package com.example.d2d;

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

        // Hardcoded Bypass for Testing
        if ("naledi@D2D.com".equalsIgnoreCase(username) && "naledi123".equals(password)) {
            getSharedPreferences("D2D_PREFS", MODE_PRIVATE).edit()
                .putString("user_id", "501")
                .putString("user_role", "customer").apply();
                
            Intent intent = new Intent(LoginActivity.this, SecureAccountActivity.class);
            intent.putExtra("user_id", "501");
            startActivity(intent);
            finish();
            return;
        } else if ("zandile_waiter@D2d.com".equalsIgnoreCase(username) && "zandile123".equals(password)) {
            getSharedPreferences("D2D_PREFS", MODE_PRIVATE).edit()
                .putString("user_id", "201")
                .putString("user_role", "staff").apply();

            Intent intent = new Intent(LoginActivity.this, StaffActivity.class);
            intent.putExtra("user_id", "201");
            startActivity(intent);
            finish();
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

                    if (user != null && "success".equals(user.getStatus())) {
                        // SAVE USER IDENTITY TO VAULT (SharedPreferences)
                        android.content.SharedPreferences pref = getSharedPreferences("D2D_PREFS", MODE_PRIVATE);
                        pref.edit()
                            .putString("user_id", user.getUser_id())
                            .putString("user_role", user.getRole())
                            .apply();
                    }

                    runOnUiThread(() -> {
                        if (user != null && "success".equals(user.getStatus())) {
                            if ("customer".equals(user.getRole())) {
                                Intent intent = new Intent(LoginActivity.this, SecureAccountActivity.class);
                                intent.putExtra("user_id", user.getUser_id());
                                startActivity(intent);
                                finish();
                            } else if ("staff".equals(user.getRole())) {
                                Intent intent = new Intent(LoginActivity.this, StaffActivity.class);
                                intent.putExtra("user_id", user.getUser_id());
                                startActivity(intent);
                                finish();
                            }
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
}
