package com.example.d2d;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Retrieve the user role from SharedPreferences
        SharedPreferences pref = requireActivity().getSharedPreferences("D2D_PREFS", Context.MODE_PRIVATE);
        String role = pref.getString("user_role", "customer");

        View view;

        if ("staff".equals(role)) {
            // Inflate Staff Hub
            view = inflater.inflate(R.layout.s_account_hub, container, false);
            setupStaffHub(view);
        } else {
            // Inflate Customer Hub
            view = inflater.inflate(R.layout.c_account_hub, container, false);
            setupCustomerHub(view);
        }

        return view;
    }

    private void setupCustomerHub(View view) {
        Button historyBtn = view.findViewById(R.id.customer_view_history);
        Button logoutBtn = view.findViewById(R.id.customer_logout);
        Button deleteAccountBtn = view.findViewById(R.id.customer_delete_account);

        if (historyBtn != null) {
            historyBtn.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), OrderHistoryActivity.class);
                startActivity(intent);
            });
        }

        if (logoutBtn != null) {
            logoutBtn.setOnClickListener(v -> performLogout());
        }

        if (deleteAccountBtn != null) {
            deleteAccountBtn.setOnClickListener(v -> {
                Toast.makeText(getContext(), "Delete Account coming soon", Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void setupStaffHub(View view) {
        Button historyBtn = view.findViewById(R.id.staff_view_history);
        Button logoutBtn = view.findViewById(R.id.staff_logout);
        Button feedbackBtn = view.findViewById(R.id.staff_view_feedback);

        if (historyBtn != null) {
            historyBtn.setOnClickListener(v -> {
                Toast.makeText(getContext(), "Order History coming soon", Toast.LENGTH_SHORT).show();
            });
        }

        if (logoutBtn != null) {
            logoutBtn.setOnClickListener(v -> performLogout());
        }

        if (feedbackBtn != null) {
            feedbackBtn.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), StaffFeedbackActivity.class);
                startActivity(intent);
            });
        }
    }

    private void performLogout() {
        // Clear shared preferences
        SharedPreferences pref = requireActivity().getSharedPreferences("D2D_PREFS", Context.MODE_PRIVATE);
        pref.edit().clear().apply();

        Intent intent = new Intent(getActivity(), LoginActivity.class);
        // Clear task stack so user can't press back to return
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }
}
