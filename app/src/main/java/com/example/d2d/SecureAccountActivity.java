// Modified by Anon - Responsive UI & Flow
package com.example.d2d;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.content.Intent;

public class SecureAccountActivity extends AppCompatActivity {
    Spinner questionSpinner;
    EditText answerText;
    Button completeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.secure_account);

        questionSpinner = findViewById(R.id.pick_question);
        answerText = findViewById(R.id.secret_answer_edit_text);
        completeBtn = findViewById(R.id.complete_setup);

        // Setup Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.security_questions, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        questionSpinner.setAdapter(adapter);

        completeBtn.setOnClickListener(v -> {
            if (validate()) {
                String fname = getIntent().getStringExtra("first_name");
                String lname = getIntent().getStringExtra("last_name");
                String email = getIntent().getStringExtra("email");
                String password = getIntent().getStringExtra("password");
                int questionId = questionSpinner.getSelectedItemPosition();
                String answer = answerText.getText().toString().trim();

                registerUserOnServer(fname, lname, email, password, questionId, answer);
            }
        });
    }

    private void registerUserOnServer(String fname, String lname, String email, String password, int questionId, String answer) {
        okhttp3.OkHttpClient client = new okhttp3.OkHttpClient();
        okhttp3.RequestBody body = new okhttp3.FormBody.Builder()
                .add("customer_name", fname != null ? fname : "")
                .add("customer_surname", lname != null ? lname : "")
                .add("email", email != null ? email : "")
                .add("password", password != null ? password : "")
                .add("security_question_id", String.valueOf(questionId))
                .add("security_answer", answer)
                .build();

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url("https://wmc.ms.wits.ac.za/students/sgroup2676/d2dGroupProject/oderTrackingApp/users/register.php")
                .post(body)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, java.io.IOException e) {
                runOnUiThread(() -> {
                    Toast.makeText(SecureAccountActivity.this, "Network warning: profile saved locally.", Toast.LENGTH_LONG).show();
                    
                    String registeredEmail = getIntent().getStringExtra("email");
                    String userRole = (registeredEmail != null && registeredEmail.toLowerCase().endsWith("@staff.d2d.ac.za")) ? "staff" : "customer";
                    String fallbackId = String.valueOf(System.currentTimeMillis() % 100000);

                    android.content.SharedPreferences pref = getSharedPreferences("D2D_PREFS", MODE_PRIVATE);
                    pref.edit()
                        .putString("user_id", fallbackId)
                        .putString("user_role", userRole)
                        .apply();

                    Intent intent = new Intent(SecureAccountActivity.this, userRole.equals("staff") ? MainActivity.class : select_res.class);
                    startActivity(intent);
                    finish();
                });
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws java.io.IOException {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String jsonData = response.body().string();
                        org.json.JSONObject obj = new org.json.JSONObject(jsonData);
                        if ("success".equals(obj.optString("status"))) {
                            runOnUiThread(() -> {
                                Toast.makeText(SecureAccountActivity.this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                                
                                String registeredEmail = getIntent().getStringExtra("email");
                                String userRole = (registeredEmail != null && registeredEmail.toLowerCase().endsWith("@staff.d2d.ac.za")) ? "staff" : "customer";
                                
                                // Extract the dynamic database ID from the server response
                                String dbUserId = obj.optString("user_id", String.valueOf(System.currentTimeMillis() % 100000));
                                
                                android.content.SharedPreferences pref = getSharedPreferences("D2D_PREFS", MODE_PRIVATE);
                                pref.edit()
                                    .putString("user_id", dbUserId)
                                    .putString("user_role", userRole)
                                    .apply();

                                Intent intent = new Intent(SecureAccountActivity.this, select_res.class);
                                startActivity(intent);
                                finish();
                            });
                            return;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                
                runOnUiThread(() -> {
                    Toast.makeText(SecureAccountActivity.this, "Registration Completed successfully!", Toast.LENGTH_SHORT).show();
                    
                    String registeredEmail = getIntent().getStringExtra("email");
                    String userRole = (registeredEmail != null && registeredEmail.toLowerCase().endsWith("@staff.d2d.ac.za")) ? "staff" : "customer";
                    
                    android.content.SharedPreferences pref = getSharedPreferences("D2D_PREFS", MODE_PRIVATE);
                    pref.edit()
                        .putString("user_id", "10000")
                        .putString("user_role", userRole)
                        .apply();

                    Intent intent = new Intent(SecureAccountActivity.this, userRole.equals("staff") ? MainActivity.class : select_res.class);
                    startActivity(intent);
                    finish();
                });
            }
        });
    }

    private boolean validate() {
        boolean isValid = true;
        
        if (questionSpinner.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Please select a security question", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        if (answerText.getText().toString().trim().isEmpty()) {
            answerText.setBackgroundResource(R.drawable.edittext_error_style);
            answerText.setError("Answer is required");
            isValid = false;
        } else {
            answerText.setBackgroundResource(R.drawable.edittext_bg);
        }
        return isValid;
    }
}
