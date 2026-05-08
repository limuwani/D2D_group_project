package com.example.d2d;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Button;
import android.view.View;
import android.widget.EditText;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    EditText email;
    EditText password;
    Button loginBtn,signup;
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);
        email = findViewById(R.id.email_edit_text);
        loginBtn = findViewById(R.id.sign_in_button);
        password = findViewById(R.id.password_edit_text);
        signup.setOnClickListener(v->{
            Intent intent = new Intent(LoginActivity.this,SignUp.class);
            startActivity(intent);
        });
    }
    @SuppressLint("UseCompatTextViewDrawableApis")
    public boolean validate(boolean isValid, String Email, String Pass){
        if(Email.isEmpty()){
            isValid=true;
            email.setCompoundDrawableTintList(ColorStateList.valueOf(Color.RED));
        }
        if(Pass.isEmpty()){
            isValid = true;
            password.setCompoundDrawableTintList(ColorStateList.valueOf(Color.RED));
        }
        return isValid;
    }
    @SuppressLint("UseCompatTextViewDrawableApis")
    public void onClickListener(){
        loginBtn.setOnClickListener(v->{
            String Email = email.getText().toString().trim();
            String Pass = password.getText().toString().trim();
            boolean isValid = true;
            isValid = validate(isValid,Email,Pass);
            if(isValid){
                Intent intent = new Intent(LoginActivity.this,SignUp.class);
                startActivity(intent);
            }
        });
    }
}
