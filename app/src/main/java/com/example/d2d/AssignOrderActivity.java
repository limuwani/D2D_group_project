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

/**
 * AssignOrderActivity allows staff members to create a new order by searching
 * for a customer using their email address. Upon finding the customer,
 * a new order is initialized and appended to the staff's active takeaway queue.
 */
public class AssignOrderActivity extends AppCompatActivity {
    // HTTP Client utilized for backend communication
    private final OkHttpClient client = new OkHttpClient();
    // EditText element to capture the customer's email input
    private EditText emailEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Bind the activity to the corresponding XML layout resource
        setContentView(R.layout.s_assign_order);

        // Retrieve reference to the customer email input field
        emailEditText = findViewById(R.id.fullname_edit_text);

        // Click listener for the "Initialize Order" button
        findViewById(R.id.send_to_customer).setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            // Validate that the email field is not empty before processing
            if (email.isEmpty()) {
                emailEditText.setError("Email required");
                return;
            }
            // Trigger customer lookup with the validated email
            searchCustomer(email);
        });

        // Click listeners to handle cancellation and screen exit
        findViewById(R.id.staff_cancel_order).setOnClickListener(v -> finish());
        findViewById(R.id.back_btn).setOnClickListener(v -> finish());
    }

    /**
     * Queries the backend search API to look up a customer using their email.
     * If the customer is found, a new order is initialized with the customer's
     * full name and added to the active queue.
     *
     * @param email The email address of the customer to search for.
     */
    private void searchCustomer(String email) {
        // Construct the GET URL with query parameters for the customer search endpoint
        okhttp3.HttpUrl url = okhttp3.HttpUrl.parse("https://wmc.ms.wits.ac.za/students/sgroup2676/d2dGroupProject/oderTrackingApp/orders/searchCustomers.php")
                .newBuilder()
                .addQueryParameter("email", email)
                .build();

        // Build the HTTP GET Request
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        // Enqueue the network request asynchronously
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Dispatch error toast on the UI thread in case of failure
                runOnUiThread(
                        () -> Toast.makeText(AssignOrderActivity.this, "Network error", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // Verify if the HTTP request was successful and response contains a body
                if (response.isSuccessful() && response.body() != null) {
                    String jsonData = response.body().string();
                    
                    // Parse the JSON payload using Gson
                    com.google.gson.Gson gson = new com.google.gson.Gson();
                    CustomerSearchResponse customer = gson.fromJson(jsonData, CustomerSearchResponse.class);
                    
                    // Check if the customer account was successfully found in the backend
                    if (customer != null && "found".equals(customer.status)) {
                        runOnUiThread(() -> {
                            // Extract full name from first name and last name
                            String fullName = customer.user_fname; // User requested only first name
                            
                            // Initialize details for the new order
                            String orderId = String.valueOf(100 + (System.currentTimeMillis() % 100000));
                            EditText restaurantEdit = findViewById(R.id.selected_restaurant);
                            String restaurant = restaurantEdit.getText().toString().trim();
                            if (restaurant.isEmpty()) {
                                restaurant = "Casa Nova"; // Fallback value if empty
                            }
                            
                            // Save order dynamically to local SQLite database
                            DatabaseHelper dbHelper = new DatabaseHelper(AssignOrderActivity.this);
                            dbHelper.saveOrder(orderId, restaurant, "preparing", "R 0.00", fullName, String.valueOf(customer.user_id));
                            
                            // Notify user and finish activity
                            Toast.makeText(AssignOrderActivity.this, "Initialized Order #" + orderId + " for " + fullName, Toast.LENGTH_LONG).show();
                            finish();
                        });
                    } else {
                        // Notify that customer email was not found in database
                        runOnUiThread(() -> Toast.makeText(AssignOrderActivity.this, "Customer not found.", Toast.LENGTH_SHORT).show());
                    }
                } else {
                    // Handle generic server-side errors
                    runOnUiThread(() -> Toast.makeText(AssignOrderActivity.this, "Server error", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    /**
     * POJO matching the backend response payload from searchCustomers.php.
     */
    private static class CustomerSearchResponse {
        String status;
        int user_id;
        String user_fname;
        String user_lname;
    }
}