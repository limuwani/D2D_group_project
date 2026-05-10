// Modified by Anon - Responsive UI & Flow
package com.example.d2d;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.content.Intent;

public class SecureAccountActivity extends AppCompatActivity {
    Spinner questionSpinner;
    EditText answerText;
    Button completeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.secure_account);

        questionSpinner = findViewById(R.id.pick_question);
        answerText = findViewById(R.id.secret_answer_edit_text);
        completeBtn = findViewById(R.id.complete_setup);

        // Setup Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.security_questions, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        questionSpinner.setAdapter(adapter);

        completeBtn.setOnClickListener(v -> {
            if (validate()) {
                Intent intent = new Intent(SecureAccountActivity.this, select_res.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private boolean validate() {
        boolean isValid = true;
        
        if (questionSpinner.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Please select a security question", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        if (answerText.getText().toString().trim().isEmpty()) {
            answerText.setBackgroundResource(R.drawable.edittext_error_style);
            answerText.setError("Answer is required");
            isValid = false;
        } else {
            answerText.setBackgroundResource(R.drawable.edittext_bg);
        }
        return isValid;
    }
}
