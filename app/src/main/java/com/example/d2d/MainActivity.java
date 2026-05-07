package com.example.d2d;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
<<<<<<< HEAD
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.content.res.ColorStateList;
import android.graphics.Color;

public class MainActivity extends AppCompatActivity {

    Button login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
=======
<<<<<<< HEAD
<<<<<<< Updated upstream
import android.view.View;
import android.view.Menu;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
=======
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
>>>>>>> Stashed changes
=======
import android.widget.Button;
>>>>>>> Anonsurf
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

<<<<<<< HEAD
<<<<<<< Updated upstream
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
=======
    private FrameLayout contentFrame;
    private BottomNavigationView customerNav;
    private BottomNavigationView staffNav;
>>>>>>> Stashed changes

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
<<<<<<< Updated upstream

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                        .setAnchorView(R.id.fab).show();
            }
        });
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
=======
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showLoginScreen();
>>>>>>> Anonsurf
    }

    private void showLoginScreen() {
        setContentView(R.layout.login_page);

        Button signUpBtn = findViewById(R.id.sign_up_button);
        Button signInBtn = findViewById(R.id.sign_in_button);

        signUpBtn.setOnClickListener(v -> showSignUpScreen());
        signInBtn.setOnClickListener(v -> showCustomerScreen());
    }

    private void showSignUpScreen() {
        setContentView(R.layout.sign_up);

        Button backToLoginBtn = findViewById(R.id.back_to_login_button);
        backToLoginBtn.setOnClickListener(v -> showLoginScreen());
    }
<<<<<<< HEAD
}
=======
        setContentView(R.layout.activity_main);

        contentFrame = findViewById(R.id.content_frame);
        customerNav = findViewById(R.id.bottom_navigation);
        staffNav = findViewById(R.id.bottom_navigation_staff);

        setupNavigation();
>>>>>>> origin/main
        showLoginScreen();
        setContentView(R.layout.login_page);
        login = findViewById(R.id.sign_in_button);
        login.setOnClickListener(v->{
            Intent intent = new Intent(MainActivity.this,LoginActivity.class);
            startActivity(intent);
        });
    }
    private void showLoginScreen() {
        setContentView(R.layout.login_page);

        Button signUpBtn = findViewById(R.id.sign_up_button);
        Button signInBtn = findViewById(R.id.sign_in_button);

        signUpBtn.setOnClickListener(v -> showSignUpScreen());
        signInBtn.setOnClickListener(v -> showCustomerScreen());
    }

    private void showSignUpScreen() {
        setContentView(R.layout.sign_up);

        Button backToLoginBtn = findViewById(R.id.back_to_login_button);
        backToLoginBtn.setOnClickListener(v -> showLoginScreen());
    }

    private void showCustomerScreen() {
        setContentView(R.layout.customer_page);
        // Add more logic here if needed (e.g. back button to login)
    }
}

<<<<<<< HEAD
=======
>>>>>>> Stashed changes
=======

    private void showCustomerScreen() {
        setContentView(R.layout.casanova_cat);
        // Add more logic here if needed (e.g. back button to login)
    }
}

>>>>>>> Anonsurf
>>>>>>> origin/main
