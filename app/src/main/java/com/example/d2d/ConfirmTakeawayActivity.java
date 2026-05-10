// Modified by Anon - Responsive UI & Flow
package com.example.d2d;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class ConfirmTakeawayActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirm_takeaway);

        // Make the layout visible for testing
        findViewById(R.id.confirm_layout).setVisibility(android.view.View.VISIBLE);

        findViewById(R.id.confirm_order).setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(ConfirmTakeawayActivity.this, OrderStatusActivity.class);
            startActivity(intent);
            finish();
        });

        findViewById(R.id.customer_cancel_order).setOnClickListener(v -> {
            finish();
        });

        findViewById(R.id.back_to_home).setOnClickListener(v -> {
            finish();
        });
    }
}
