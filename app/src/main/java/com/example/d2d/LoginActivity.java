package com.example.d2d;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

//internet classes
import java.io.IOException;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Call;


//import   Gson it helps us with   puting the data in json file in a class
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

//this class help with transporting messages accros the forms
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


public class LoginActivity extends AppCompatActivity {
    OkHttpClient client=new OkHttpClient();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);

    }

//this function will help with loging in;
    public void doLogin(View view){


        Log.d("DEBUG", "Sign in button clicked!");
        EditText usernameText=findViewById(R.id.email_edit_text);
        String username=usernameText.getText().toString();
        EditText passwordText=findViewById(R.id.password_edit_text);
        String password=passwordText.getText().toString();

        //create our body that will be used in the request
        RequestBody body=new FormBody.Builder()
                .add("username",username)
                .add("password",password)
                .build();

        //build our request,we will use post  as in php
        Request request=new Request.Builder()
                .url("https://wmc.ms.wits.ac.za/students/sgroup2676/d2dGroupProject/oderTrackingApp/users/login.php")
                .post(body)
                .build();


        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                /*display the app error,
                tells the user that there is an error on our side
                 */
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                if(response.isSuccessful() && response.body() != null){
                    String jsonData = response.body().string();
                    Log.d("JSON_RESPONSE", jsonData);

                    Gson gson = new Gson();
                    LoginResponse user = gson.fromJson(jsonData, LoginResponse.class);

                    runOnUiThread(() -> {


                        if(user != null && "success".equals(user.getStatus())){
                            if("customer".equals(user.getRole())){
                                Intent intent = new Intent(LoginActivity.this, CustomerActivity.class);
                                intent.putExtra("user_id", user.getUser_id());
                                startActivity(intent);
                                //finish();
                            } else if("staff".equals(user.getRole())){
                                Intent intent = new Intent(LoginActivity.this, StaffActivity.class);
                                intent.putExtra("user_id", user.getUser_id());
                                startActivity(intent);
                                finish();

                            }
                        } else {
                            Log.d("LOGIN", "Login failed");
                        }
                    });
                }
            }
        });
    }


}