package com.example.d2d;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.Button;
import android.view.View;
import android.widget.EditText;
import android.graphics.Color;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
public class SignUp extends AppCompatActivity {
    EditText fullname,email,password;
    public static Button btnSignUp;
    @SuppressLint("CutPasteId")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);
        fullname = findViewById(R.id.fullname_edit_text);
        email = findViewById(R.id.signup_email_edit_text);
        password = findViewById(R.id.signup_email_edit_text);
    }

    public boolean validate(Boolean valide,String Email,String FullName,String pass){
        valide = true;
        if(Email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(Email).matches()){
            email.setCompoundDrawableTintList(ColorStateList.valueOf(Color.RED));
            valide = false;
        }
        if(FullName.isEmpty()){
            fullname.setCompoundDrawableTintList(ColorStateList.valueOf(Color.RED));
            valide = false;
        }
        if(pass.isEmpty()){
            password.setCompoundDrawableTintList(ColorStateList.valueOf(Color.RED));
            valide = false;
        }
        return valide;
    }
    public void OnClickButtonListener() {
        btnSignUp = findViewById(R.id.sign_up_button);
        btnSignUp.setOnClickListener(
                new View.OnClickListener() {
                    boolean isValide = true;
                    String Email = email.getText().toString().trim();
                    String Fname = fullname.getText().toString().trim();
                    String pass = password.getText().toString().trim();
                    isValide = validate(isValide, Email, Fname, pass);
                    if(isValide){
                        Intent intent = new Intent(SignUp.this,select_res.class);
                    }
                }
            );

    }

}
