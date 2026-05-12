package com.example.d2d;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private BottomNavigationView bottomNav;
    private BottomNavigationView bottomNavStaff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.main_view_pager);
        bottomNav = findViewById(R.id.bottom_navigation);
        bottomNavStaff = findViewById(R.id.bottom_navigation_staff);

        // Get user role from intent or prefs
        String role = getIntent().getStringExtra("user_role");
        if (role == null) {
            role = getSharedPreferences("D2D_PREFS", MODE_PRIVATE).getString("user_role", "customer");
        }

        // Set up ViewPager Adapter with role
        MainViewPagerAdapter adapter = new MainViewPagerAdapter(this, role);
        viewPager.setAdapter(adapter);

        // Show correct Bottom Nav
        BottomNavigationView activeNav;
        if ("staff".equals(role)) {
            bottomNavStaff.setVisibility(View.VISIBLE);
            bottomNav.setVisibility(View.GONE);
            activeNav = bottomNavStaff;
        } else {
            bottomNav.setVisibility(View.VISIBLE);
            bottomNavStaff.setVisibility(View.GONE);
            activeNav = bottomNav;
        }

        // Link BottomNav with ViewPager
        activeNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.home_button || itemId == R.id.staff_home_button) {
                viewPager.setCurrentItem(0);
                return true;
            } else if (itemId == R.id.orders || itemId == R.id.manage) {
                viewPager.setCurrentItem(1);
                return true;
            } else if (itemId == R.id.profile || itemId == R.id.staff_profile) {
                // --- LOGOUT LOGIC ---
                DatabaseHelper dbHelper = new DatabaseHelper(MainActivity.this);
                dbHelper.getWritableDatabase().execSQL("UPDATE sessions SET is_active = 0");
                getSharedPreferences("D2D_PREFS", MODE_PRIVATE).edit().clear().apply();
                
                android.content.Intent intent = new android.content.Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                return true;
            }
            return false;
        });

        // Link ViewPager swipe with BottomNav icons
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position) {
                    case 0:
                        activeNav.getMenu().findItem("staff".equals(getIntent().getStringExtra("user_role")) ? R.id.staff_home_button : R.id.home_button).setChecked(true);
                        break;
                    case 1:
                        activeNav.getMenu().findItem("staff".equals(getIntent().getStringExtra("user_role")) ? R.id.manage : R.id.orders).setChecked(true);
                        break;
                    case 2:
                        activeNav.getMenu().findItem("staff".equals(getIntent().getStringExtra("user_role")) ? R.id.staff_profile : R.id.profile).setChecked(true);
                        break;
                }
            }
        });
    }
}
