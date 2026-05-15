/*
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

        Button continueBtn = findViewById(R.id.continue_setup);
        continueBtn.setOnClickListener(v -> {
            Intent intent = new Intent(RecoveryStep1Activity.this, RecoveryStep2Activity.class);
            startActivity(intent);
        });

        Button backToLoginBtn = findViewById(R.id.back_to_login);
        backToLoginBtn.setOnClickListener(v -> {
            finish();
        });
    }
}

*/