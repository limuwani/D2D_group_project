package com.example.d2d;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Patterns;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Button;
import android.view.View;
import android.widget.EditText;
import android.graphics.Color;
import android.content.Intent;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textfield.TextInputEditText;


public class SignUp extends AppCompatActivity {
    EditText name,surname,email,password,confirmed_pass;
    Button btnSignUp;
    CheckBox termsCheckBox;
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
        OnClickButtonListener();
    }

    @SuppressLint("UseCompatTextViewDrawableApis")
    public boolean validate(boolean valide,String Email, String Name, String Surname, String pass, String confirmed_password, CheckBox termCon) {

        if (Email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(Email).matches()) {
            email.setBackgroundResource(R.drawable.edittext_error_style);
            email.setError("Email is required");
            valide = false;
        }
        else{
            email.setBackgroundResource(R.drawable.edittext_bg);
            email.setError(null);
        }
        if (Name.isEmpty()) {
            name.setBackgroundResource(R.drawable.edittext_error_style);
            name.setError("Your name is required");
            valide = false;
        }
        if (Surname.isEmpty()) {
            surname.setBackgroundResource(R.drawable.edittext_error_style);
            surname.setError("Last name is required");
            valide = false;
        }
        if(pass.isEmpty()){
            password.setBackgroundResource(R.drawable.edittext_error_style);
            password.setError("Password is required");
            valide = false;
        }
        if (!confirmed_password.equals(pass) || confirmed_password.isEmpty()) {
            confirmed_pass.setBackgroundResource(R.drawable.edittext_error_style);
            confirmed_pass.setError("Confirm email");
            valide = false;
        }
        return valide;
    }


    public void OnClickButtonListener(){
        btnSignUp.setOnClickListener(v->{
            String Fname = name.getText().toString().trim();
            String Lname = surname.getText().toString().trim();
            String Email = email.getText().toString().trim();
            String pass = password.getText().toString().trim();
            String conPass = confirmed_pass.getText().toString().trim();
            boolean isValid = true;
            isValid = validate(isValid,Email,Fname,Lname,pass,conPass,termsCheckBox);
            if(!termsCheckBox.isChecked()){
                Toast.makeText(this,"You must accept the terms and conditions",Toast.LENGTH_SHORT).show();
            }
            if(isValid){
                Intent intent = new Intent(SignUp.this,select_res.class);
                startActivity(intent);
            }
        });
    }
}
