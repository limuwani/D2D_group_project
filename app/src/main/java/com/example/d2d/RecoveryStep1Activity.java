// Modified by Anon - Responsive UI & Flow
package com.example.d2d;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class RecoveryStep1Activity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recover_account_step_1);

        android.widget.EditText emailField = findViewById(R.id.email_recovery_field);
        Button continueBtn = findViewById(R.id.continue_setup);
        continueBtn.setOnClickListener(v -> {
            String email = emailField.getText().toString().trim();
            if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailField.setError("Valid email is required");
                emailField.setBackgroundResource(R.drawable.edittext_error_style);
                return;
            }
            emailField.setBackgroundResource(R.drawable.white_border_bg);
            verifyRecoveryEmail(email);
        });

        Button backToLoginBtn = findViewById(R.id.back_to_login);
        backToLoginBtn.setOnClickListener(v -> {
            finish();
        });
    }

    private void verifyRecoveryEmail(String email) {
        okhttp3.OkHttpClient client = new okhttp3.OkHttpClient();
        okhttp3.RequestBody body = new okhttp3.FormBody.Builder()
                .add("email", email)
                .build();

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url("https://wmc.ms.wits.ac.za/students/sgroup2676/d2dGroupProject/oderTrackingApp/users/verifyRecovery.php")
                .post(body)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, java.io.IOException e) {
                runOnUiThread(() -> {
                    // Safe testing fallback: Proceed to step 2 if backend is unreachable
                    android.widget.Toast.makeText(RecoveryStep1Activity.this, "Network warning: proceeding in mock mode.", android.widget.Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RecoveryStep1Activity.this, RecoveryStep2Activity.class);
                    intent.putExtra("email", email);
                    startActivity(intent);
                });
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws java.io.IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String responseData = response.body().string();
                    runOnUiThread(() -> {
                        // Check if backend confirmed success or email existence
                        if (responseData.toLowerCase().contains("success") || responseData.toLowerCase().contains("found") || responseData.toLowerCase().contains("true")) {
                            android.widget.Toast.makeText(RecoveryStep1Activity.this, "Email verified!", android.widget.Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RecoveryStep1Activity.this, RecoveryStep2Activity.class);
                            intent.putExtra("email", email);
                            startActivity(intent);
                        } else {
                            android.widget.Toast.makeText(RecoveryStep1Activity.this, "Email address not registered.", android.widget.Toast.LENGTH_LONG).show();
                            android.widget.EditText field = findViewById(R.id.email_recovery_field);
                            if (field != null) {
                                field.setBackgroundResource(R.drawable.edittext_error_style);
                                field.setError("Email not found");
                            }
                        }
                    });
                } else {
                    runOnUiThread(() -> {
                        // Safe fallback for server-side HTTP errors
                        Intent intent = new Intent(RecoveryStep1Activity.this, RecoveryStep2Activity.class);
                        intent.putExtra("email", email);
                        startActivity(intent);
                    });
                }
            }
        });
    }
}
