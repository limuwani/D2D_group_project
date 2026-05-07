package com.example.d2d;

import android.os.Bundle;
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
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

import com.example.d2d.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
=======
        setContentView(R.layout.activity_main);

        contentFrame = findViewById(R.id.content_frame);
        customerNav = findViewById(R.id.bottom_navigation);
        staffNav = findViewById(R.id.bottom_navigation_staff);

        setupNavigation();
        showLoginScreen();
    }

    private void setupNavigation() {
        customerNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.home_button) {
                loadScreen(R.layout.select_res);
            } else if (id == R.id.orders) {
                loadScreen(R.layout.cus_order_status);
            } else if (id == R.id.profile) {
                showLoginScreen(); // For now, logout
            }
            return true;
        });

        staffNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.staff_home_button) {
                loadScreen(R.layout.s_staff_portal);
            } else if (id == R.id.manage) {
                loadScreen(R.layout.s_active_queue);
            } else if (id == R.id.staff_profile) {
                showLoginScreen(); // For now, logout
            }
            return true;
        });
    }

    private void loadScreen(int layoutId) {
        contentFrame.removeAllViews();
        LayoutInflater.from(this).inflate(layoutId, contentFrame, true);
        bindListeners(layoutId);
    }

    private void bindListeners(int layoutId) {
        // Handle common Back Button
        View backBtn = findViewById(R.id.back_btn);
        if (backBtn != null) {
            backBtn.setOnClickListener(v -> handleBackNavigation(layoutId));
        }

        // Screen-specific listeners
        if (layoutId == R.layout.select_res) {
            setupRestaurantRecyclerView();
        } else if (layoutId == R.layout.s_staff_portal) {
            View startOrder = findViewById(R.id.start_order);
            View activeStatus = findViewById(R.id.view_active_status);
            if (startOrder != null) startOrder.setOnClickListener(v -> loadScreen(R.layout.s_assign_order));
            if (activeStatus != null) activeStatus.setOnClickListener(v -> loadScreen(R.layout.s_active_queue));
        }
    }

    private void handleBackNavigation(int currentLayoutId) {
        if (currentLayoutId == R.layout.start_order) {
            showCustomerScreen();
        } else if (currentLayoutId == R.id.bottom_navigation || currentLayoutId == R.layout.select_res) {
            showLoginScreen();
        } else if (currentLayoutId == R.layout.s_assign_order || currentLayoutId == R.layout.s_active_queue) {
            showStaffScreen();
        } else {
            showLoginScreen();
        }
    }

    private void showLoginScreen() {
        customerNav.setVisibility(View.GONE);
        staffNav.setVisibility(View.GONE);
        loadScreen(R.layout.login_page);

        Button signUpBtn = findViewById(R.id.sign_up_button);
        Button signInBtn = findViewById(R.id.sign_in_button);

        if (signUpBtn != null) signUpBtn.setOnClickListener(v -> showSignUpScreen());
        if (signInBtn != null) signInBtn.setOnClickListener(v -> handleLogin());
    }

    private void handleLogin() {
        EditText emailText = findViewById(R.id.email_edit_text);
        if (emailText == null) return;
        String email = emailText.getText().toString().toLowerCase();

        if (email.contains("staff") || email.contains("waiter")) {
            showStaffScreen();
        } else {
            showCustomerScreen();
        }
    }

    private void showSignUpScreen() {
        customerNav.setVisibility(View.GONE);
        staffNav.setVisibility(View.GONE);
        loadScreen(R.layout.sign_up);

        Button backToLoginBtn = findViewById(R.id.back_to_login_button);
        if (backToLoginBtn != null) {
            backToLoginBtn.setOnClickListener(v -> showLoginScreen());
        }
    }

    private void showCustomerScreen() {
        customerNav.setVisibility(View.VISIBLE);
        staffNav.setVisibility(View.GONE);
        customerNav.setSelectedItemId(R.id.home_button);
        loadScreen(R.layout.select_res);
    }

    private void showStaffScreen() {
        customerNav.setVisibility(View.GONE);
        staffNav.setVisibility(View.VISIBLE);
        staffNav.setSelectedItemId(R.id.staff_home_button);
        loadScreen(R.layout.s_staff_portal);
    }

    private void setupRestaurantRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.restaurant_recycler_view);
        if (recyclerView == null) return;

        // 1. Create a list of restaurants (In a real app, this would come from a Database)
        List<Restaurant> restaurants = new ArrayList<>();
        restaurants.add(new Restaurant("The Organic Restaurant", "Sandton, Hazelwood Rd", "No ratings yet", R.drawable.organic));
        restaurants.add(new Restaurant("Casa Nova Restaurant", "Braamfontein, Juta St", "No ratings yet", R.drawable.casanova));
        
        // Add a third one just to show it's dynamic!
        restaurants.add(new Restaurant("Quick Bite Express", "Pretoria, Main Ave", "4.5 stars", R.drawable.product_bg));

        // 2. Set up the Adapter
        RestaurantAdapter adapter = new RestaurantAdapter(restaurants, restaurant -> {
            // When a restaurant is clicked, go to the order screen
            loadScreen(R.layout.start_order);
        });

        // 3. Connect it all
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }
}

>>>>>>> Stashed changes
