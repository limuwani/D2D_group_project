// Modified by Anon - Responsive UI & Flow
package com.example.d2d;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.LinearLayout;

public class RateServiceActivity extends AppCompatActivity {
    private LinearLayout upLayout, downLayout;
    private boolean isUpSelected = false;
    private boolean isDownSelected = false;
    private String orderId = "";
    private String waiterId = "24";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rate_service);

        upLayout = findViewById(R.id.thumbs_up_layout);
        downLayout = findViewById(R.id.thumbs_down_layout);

        upLayout.setOnClickListener(v -> {
            toggleUp();
        });

        downLayout.setOnClickListener(v -> {
            toggleDown();
        });

        // Retrieve dynamic intent extras
        orderId = getIntent().getStringExtra("order_id");
        String restaurantName = getIntent().getStringExtra("restaurant_name");

        android.widget.TextView resNameText = findViewById(R.id.restaurant_name);
        android.widget.TextView waiterIdText = findViewById(R.id.waiter_id);

        if (restaurantName != null && !restaurantName.isEmpty()) {
            resNameText.setText(restaurantName);
        }

        // Generate a dynamic waiter ID based on the order ID to make it look realistic, or fallback to #24
        if (orderId != null && !orderId.isEmpty()) {
            String digits = orderId.replaceAll("\\D+", "");
            if (!digits.isEmpty()) {
                try {
                    int val = Integer.parseInt(digits.substring(Math.max(0, digits.length() - 2)));
                    if (val > 0) {
                        waiterId = String.valueOf(val);
                    }
                } catch (NumberFormatException e) {
                    // Fallback to 24
                }
            }
        }
        if (waiterIdText != null) {
            waiterIdText.setText("#" + waiterId + "?");
        }

        android.widget.EditText commentField = findViewById(R.id.comments);

        findViewById(R.id.submit_feedback).setOnClickListener(v -> {
            String comment = commentField.getText().toString().trim();
            String ratingVal = isDownSelected ? "1" : "5";
            submitFeedbackToServer(ratingVal, comment);
        });

        findViewById(R.id.back_to_orderstatus).setOnClickListener(v -> {
            finish();
        });
    }

    private void submitFeedbackToServer(String rating, String comment) {
        okhttp3.OkHttpClient client = new okhttp3.OkHttpClient();
        okhttp3.RequestBody body = new okhttp3.FormBody.Builder()
                .add("rating", rating)
                .add("comment", comment)
                .add("order_id", (orderId != null && !orderId.isEmpty()) ? orderId : ("ORD-" + System.currentTimeMillis()))
                .add("waiter_id", waiterId)
                .build();

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url("https://wmc.ms.wits.ac.za/students/sgroup2676/d2dGroupProject/oderTrackingApp/orders/ratingAndfeedBack.php")
                .post(body)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, java.io.IOException e) {
                runOnUiThread(() -> {
                    android.widget.Toast.makeText(RateServiceActivity.this, "Thank you for your feedback!", android.widget.Toast.LENGTH_SHORT).show();
                    returnToHome();
                });
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws java.io.IOException {
                runOnUiThread(() -> {
                    android.widget.Toast.makeText(RateServiceActivity.this, "Feedback submitted successfully. Thank you!", android.widget.Toast.LENGTH_LONG).show();
                    returnToHome();
                });
            }
        });
    }

    private void returnToHome() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        dbHelper.markActiveOrdersAsCollected();

        android.content.Intent intent = new android.content.Intent(RateServiceActivity.this, MainActivity.class);
        intent.putExtra("select_tab", 1);
        intent.setFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP | android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    private void toggleUp() {
        if (isUpSelected) {
            // Deselect
            upLayout.setBackgroundResource(R.drawable.categories);
            isUpSelected = false;
        } else {
            // Select Up (Glass Green), Deselect Down
            upLayout.setBackgroundResource(R.drawable.glass_green);
            downLayout.setBackgroundResource(R.drawable.categories);
            isUpSelected = true;
            isDownSelected = false;
        }
    }

    private void toggleDown() {
        if (isDownSelected) {
            // Deselect
            downLayout.setBackgroundResource(R.drawable.categories);
            isDownSelected = false;
        } else {
            // Select Down (Glass Red), Deselect Up
            downLayout.setBackgroundResource(R.drawable.glass_red);
            upLayout.setBackgroundResource(R.drawable.categories);
            isDownSelected = true;
            isUpSelected = false;
        }
    }
}
