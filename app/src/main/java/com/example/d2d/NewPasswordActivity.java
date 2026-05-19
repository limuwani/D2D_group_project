// Modified by Anon - Responsive UI & Flow
package com.example.d2d;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class NewPasswordActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_password);

        final String email = getIntent().getStringExtra("email");
        final String customerId = getIntent().getStringExtra("customer_id");
        android.widget.EditText newPassField = findViewById(R.id.new_password_field);
        android.widget.EditText confirmPassField = findViewById(R.id.confirm_new_password_field);

        Button updateBtn = findViewById(R.id.update_new_pass);
        updateBtn.setOnClickListener(v -> {
            String newPass = newPassField.getText().toString().trim();
            String confirmPass = confirmPassField.getText().toString().trim();

            if (newPass.isEmpty()) {
                newPassField.setError("Password cannot be empty");
                newPassField.setBackgroundResource(R.drawable.edittext_error_style);
                return;
            }
            newPassField.setBackgroundResource(R.drawable.white_border_bg);

            if (!newPass.equals(confirmPass)) {
                confirmPassField.setError("Passwords do not match");
                confirmPassField.setBackgroundResource(R.drawable.edittext_error_style);
                return;
            }
            confirmPassField.setBackgroundResource(R.drawable.white_border_bg);

            executeUpdatePassword(customerId != null ? customerId : email, newPass);
        });
    }

    private void executeUpdatePassword(String userIdOrEmail, String newPassword) {
        okhttp3.OkHttpClient client = new okhttp3.OkHttpClient();
        okhttp3.RequestBody body = new okhttp3.FormBody.Builder()
                .add("customer_id", userIdOrEmail)
                .add("new_password", newPassword)
                .build();

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url("https://wmc.ms.wits.ac.za/students/sgroup2676/d2dGroupProject/oderTrackingApp/users/updatePassword.php")
                .post(body)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, java.io.IOException e) {
                runOnUiThread(() -> {
                    android.widget.Toast.makeText(NewPasswordActivity.this, "Network error. Try again.", android.widget.Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws java.io.IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        android.widget.Toast.makeText(NewPasswordActivity.this, "Password updated successfully!", android.widget.Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(NewPasswordActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        finish();
                    });
                }
            }
        });
    }
}
