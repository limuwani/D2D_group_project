// Modified by Anon - Responsive UI & Flow
package com.example.d2d;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SignUp extends AppCompatActivity {
    EditText name, surname, email, password, confirmed_pass;
    Button btnSignUp;
    CheckBox termsCheckBox;
    private final OkHttpClient client = new OkHttpClient();

    @SuppressLint({"CutPasteId", "WrongViewCast"})
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);

        name = findViewById(R.id.name_edit_text);
        surname = findViewById(R.id.Surname_edit_text);
        email = findViewById(R.id.signup_email_edit_text);
        password = findViewById(R.id.signup_password_edit_text);
        confirmed_pass = findViewById(R.id.confirm_password_edit_text);
        btnSignUp = findViewById(R.id.signup_submit_button);
        termsCheckBox = findViewById(R.id.terms_condions_checkbox);

        Button backToLogin = findViewById(R.id.back_to_login_button);
        backToLogin.setOnClickListener(v -> {
            Intent intent = new Intent(SignUp.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        Button termsBtn = findViewById(R.id.terms_conditions);
        termsBtn.setOnClickListener(v -> {
            Intent intent = new Intent(SignUp.this, TermsActivity.class);
            startActivity(intent);
        });

        OnClickButtonListener();
    }

    public boolean validate(String Email, String Name, String Surname, String pass, String confirmed_password) {
        boolean isValid = true;

        if (Email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(Email).matches()) {
            email.setBackgroundResource(R.drawable.edittext_error_style);
            email.setError("Valid email is required");
            isValid = false;
        } else {
            email.setBackgroundResource(R.drawable.edittext_bg);
        }

        if (Name.isEmpty()) {
            name.setBackgroundResource(R.drawable.edittext_error_style);
            name.setError("First name is required");
            isValid = false;
        } else {
            name.setBackgroundResource(R.drawable.edittext_bg);
        }

        if (Surname.isEmpty()) {
            surname.setBackgroundResource(R.drawable.edittext_error_style);
            surname.setError("Last name is required");
            isValid = false;
        } else {
            surname.setBackgroundResource(R.drawable.edittext_bg);
        }

        if (pass.isEmpty()) {
            password.setBackgroundResource(R.drawable.edittext_error_style);
            password.setError("Password is required");
            isValid = false;
        } else {
            password.setBackgroundResource(R.drawable.edittext_bg);
        }

        if (!confirmed_password.equals(pass) || confirmed_password.isEmpty()) {
            confirmed_pass.setBackgroundResource(R.drawable.edittext_error_style);
            confirmed_pass.setError("Passwords do not match");
            isValid = false;
        } else {
            confirmed_pass.setBackgroundResource(R.drawable.edittext_bg);
        }

        return isValid;
    }

    public void OnClickButtonListener() {
        btnSignUp.setOnClickListener(v -> {
            String Fname = name.getText().toString().trim();
            String Lname = surname.getText().toString().trim();
            String Email = email.getText().toString().trim();
            String pass = password.getText().toString().trim();
            String conPass = confirmed_pass.getText().toString().trim();

            boolean isValid = validate(Email, Fname, Lname, pass, conPass);

            if (!termsCheckBox.isChecked()) {
                Toast.makeText(this, "You must accept the terms and conditions", Toast.LENGTH_SHORT).show();
                isValid = false;
            }

            if (isValid) {
                registerUser(Fname, Lname, Email, pass);
            }
        });
    }

    private void registerUser(String fname, String lname, String emailAddr, String pass) {
        String url = "https://wmc.ms.wits.ac.za/students/sgroup2676/d2dGroupProject/oderTrackingApp/users/register.php";

        RequestBody body = new FormBody.Builder()
                .add("first_name", fname)
                .add("last_name", lname)
                .add("email", emailAddr)
                .add("password", pass)
                .add("role", "customer")
                .build();

        Request request = new Request.Builder().url(url).post(body).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(SignUp.this, "Network error", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        Toast.makeText(SignUp.this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SignUp.this, SecureAccountActivity.class);
                        startActivity(intent);
                        finish();
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(SignUp.this, "Registration failed. Try again.", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }
}
