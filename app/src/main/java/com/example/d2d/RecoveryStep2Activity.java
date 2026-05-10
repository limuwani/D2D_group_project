// Modified by Anon - Responsive UI & Flow
package com.example.d2d;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

public class RecoveryStep2Activity extends AppCompatActivity {
    Spinner questionSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.revover_account_step_2);

        questionSpinner = findViewById(R.id.choose_question);

        // Setup Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.security_questions, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        questionSpinner.setAdapter(adapter);

        Button verifyBtn = findViewById(R.id.verify_answer);
        verifyBtn.setOnClickListener(v -> {
            Intent intent = new Intent(RecoveryStep2Activity.this, NewPasswordActivity.class);
            startActivity(intent);
        });

        Button goBackBtn = findViewById(R.id.go_back);
        goBackBtn.setOnClickListener(v -> {
            finish();
        });
    }
}
