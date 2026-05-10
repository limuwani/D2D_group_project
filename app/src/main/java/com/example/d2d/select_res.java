// Modified by Anon - Responsive UI & Flow
package com.example.d2d;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class select_res extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_res);

        findViewById(R.id.the_organic_res).setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(select_res.this, ConfirmTakeawayActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.casanova).setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(select_res.this, ConfirmTakeawayActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.back_to_login).setOnClickListener(v -> {
            finish();
        });
    }
}
