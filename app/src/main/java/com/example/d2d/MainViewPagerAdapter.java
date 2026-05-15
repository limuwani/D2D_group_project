/*
package com.example.d2d;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class MainViewPagerAdapter extends FragmentStateAdapter {
    private String role;

    public MainViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, String role) {
        super(fragmentActivity);
        this.role = role;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:
                return new OrdersFragment();
            case 2:
                return new ProfileFragment();
            default:
                if ("staff".equals(role)) {
                    return new StaffHomeFragment();
                }
                return new HomeFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}

*/