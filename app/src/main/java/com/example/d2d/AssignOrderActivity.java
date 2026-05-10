// Modified by Anon - Responsive UI & Flow
package com.example.d2d;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class AssignOrderActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.s_assign_order);

        findViewById(R.id.send_to_customer).setOnClickListener(v -> {
            android.widget.Toast.makeText(AssignOrderActivity.this, "Order initialized and sent to customer!", android.widget.Toast.LENGTH_SHORT).show();
            finish();
        });

        findViewById(R.id.staff_cancel_order).setOnClickListener(v -> {
            finish();
        });

        findViewById(R.id.back_btn).setOnClickListener(v -> {
            finish();
        });
    }
}
