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
        String tempRole = getIntent().getStringExtra("user_role");
        if (tempRole == null) {
            tempRole = getSharedPreferences("D2D_PREFS", MODE_PRIVATE).getString("user_role", "customer");
        }
        final String role = tempRole;
        final String finalRole = role;

        // Set up ViewPager Adapter with role
        MainViewPagerAdapter adapter = new MainViewPagerAdapter(this, finalRole);
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
                viewPager.setCurrentItem(2);
                return true;
            }
            return false;
        });

        // Link ViewPager swipe with BottomNav icons
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if ("staff".equals(finalRole)) {
                    switch (position) {
                        case 0: activeNav.setSelectedItemId(R.id.staff_home_button); break;
                        case 1: activeNav.setSelectedItemId(R.id.manage); break;
                        case 2: activeNav.setSelectedItemId(R.id.staff_profile); break;
                    }
                } else {
                    switch (position) {
                        case 0: activeNav.setSelectedItemId(R.id.home_button); break;
                        case 1: activeNav.setSelectedItemId(R.id.orders); break;
                        case 2: activeNav.setSelectedItemId(R.id.profile); break;
                    }
                }
            }
        });

        // Programmatically select tab if passed via intent
        int selectTab = getIntent().getIntExtra("select_tab", -1);
        if (selectTab != -1) {
            viewPager.setCurrentItem(selectTab);
        }
    }

    @Override
    protected void onNewIntent(android.content.Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        int selectTab = intent.getIntExtra("select_tab", -1);
        if (selectTab != -1 && viewPager != null) {
            viewPager.setCurrentItem(selectTab);
        }
    }

    public void selectTab(int position) {
        if (viewPager != null) {
            viewPager.setCurrentItem(position);
        }
    }
}
