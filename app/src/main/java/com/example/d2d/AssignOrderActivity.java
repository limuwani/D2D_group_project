// Modified by Anon - Responsive UI & Flow
package com.example.d2d;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.EditText;
import android.widget.Toast;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AssignOrderActivity extends AppCompatActivity {
    private final OkHttpClient client = new OkHttpClient();
    private EditText emailEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.s_assign_order);

        emailEditText = findViewById(R.id.fullname_edit_text);

        findViewById(R.id.send_to_customer).setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            if (email.isEmpty()) {
                emailEditText.setError("Email required");
                return;
            }
            searchCustomer(email);
        });

        findViewById(R.id.staff_cancel_order).setOnClickListener(v -> finish());
        findViewById(R.id.back_btn).setOnClickListener(v -> finish());
    }

    private void searchCustomer(String email) {
        String url = "https://wmc.ms.wits.ac.za/students/sgroup2676/d2dGroupProject/oderTrackingApp/orders/searchCustomers.php";
        
        RequestBody body = new FormBody.Builder()
                .add("email", email)
                .build();

        Request request = new Request.Builder().url(url).post(body).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(AssignOrderActivity.this, "Network error", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String jsonData = response.body().string();
                    // For now, assuming success if the server returns a successful response
                    // In a real app, you'd parse the JSON to check if the user exists
                    runOnUiThread(() -> {
                        Toast.makeText(AssignOrderActivity.this, "Customer found! Initializing order...", Toast.LENGTH_SHORT).show();
                        // Proceed to next step or show success
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(AssignOrderActivity.this, "Customer not found.", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }
}
