// Modified by Anon - Responsive UI & Flow
package com.example.d2d;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class RecoveryStep2Activity extends AppCompatActivity {
    Spinner questionSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recover_account_step_2);

        questionSpinner = findViewById(R.id.choose_question);

        // Setup Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.security_questions, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        questionSpinner.setAdapter(adapter);

        final String email = getIntent().getStringExtra("email");
        android.widget.EditText answerField = findViewById(R.id.secret_answer);

        Button verifyBtn = findViewById(R.id.verify_answer);
        verifyBtn.setOnClickListener(v -> {
            String question = questionSpinner.getSelectedItem().toString();
            String answer = answerField.getText().toString().trim();

            if (answer.isEmpty()) {
                answerField.setError("Please provide an answer");
                return;
            }

            verifySecurityAnswer(email, question, answer);
        });

        Button goBackBtn = findViewById(R.id.go_back);
        goBackBtn.setOnClickListener(v -> finish());
    }

    private void verifySecurityAnswer(String email, String question, String answer) {
        okhttp3.OkHttpClient client = new okhttp3.OkHttpClient();
        okhttp3.RequestBody body = new okhttp3.FormBody.Builder()
                .add("email", email)
                .add("question", question)
                .add("answer", answer)
                .build();

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url("https://wmc.ms.wits.ac.za/students/sgroup2676/d2dGroupProject/oderTrackingApp/users/resetPassword.php")
                .post(body)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, java.io.IOException e) {
                runOnUiThread(() -> android.widget.Toast.makeText(RecoveryStep2Activity.this, "Network error", android.widget.Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws java.io.IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String json = response.body().string();
                    runOnUiThread(() -> {
                        try {
                            org.json.JSONObject obj = new org.json.JSONObject(json);
                            if ("success".equals(obj.optString("status"))) {
                                String customerId = obj.optString("customer_id", "");
                                if (customerId.isEmpty() && obj.has("data")) {
                                    customerId = obj.getJSONObject("data").optString("user_id", "");
                                }
                                
                                Intent intent = new Intent(RecoveryStep2Activity.this, NewPasswordActivity.class);
                                intent.putExtra("email", email);
                                intent.putExtra("customer_id", customerId);
                                startActivity(intent);
                            } else {
                                android.widget.Toast.makeText(RecoveryStep2Activity.this, "Incorrect answer or question.", android.widget.Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            android.widget.Toast.makeText(RecoveryStep2Activity.this, "Error: " + e.getMessage(), android.widget.Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}
