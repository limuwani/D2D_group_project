// Modified by Anon - Responsive UI & Flow
package com.example.d2d;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class RateServiceActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rate_service);

        findViewById(R.id.submit_feedback).setOnClickListener(v -> {
            android.widget.Toast.makeText(RateServiceActivity.this, "Thank you for your feedback!", android.widget.Toast.LENGTH_SHORT).show();
            // Return to restaurant selection after feedback
            android.content.Intent intent = new android.content.Intent(RateServiceActivity.this, select_res.class);
            intent.setFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP | android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        findViewById(R.id.back_to_orderstatus).setOnClickListener(v -> {
            finish();
        });
    }
}
