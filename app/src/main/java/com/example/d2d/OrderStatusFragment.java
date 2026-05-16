package com.example.d2d;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class OrderStatusFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.cus_order_status, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Views
        View backBtn = view.findViewById(R.id.back_btn);
        View browseBtn = view.findViewById(R.id.browse_restaurants_btn);
        View orderCard = view.findViewById(R.id.active_order_card);
        View emptyState = view.findViewById(R.id.empty_state_layout);
        android.widget.TextView orderIdText = view.findViewById(R.id.status);
        android.widget.TextView restaurantText = view.findViewById(R.id.restaurant_name);

        if (backBtn != null) backBtn.setVisibility(View.GONE);
        if (browseBtn != null) browseBtn.setVisibility(View.GONE);

        // Fetch active order from DB
        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
        android.database.Cursor cursor = dbHelper.getActiveOrder();

        if (cursor != null && cursor.moveToFirst()) {
            // Found an active order
            String id = cursor.getString(cursor.getColumnIndexOrThrow("order_id"));
            String restaurant = cursor.getString(cursor.getColumnIndexOrThrow("restaurant_name"));
            cursor.close();

            if (emptyState != null) emptyState.setVisibility(View.GONE);
            if (orderCard != null) {
                orderCard.setVisibility(View.VISIBLE);
                if (orderIdText != null) orderIdText.setText("ORDER #" + id);
                if (restaurantText != null) restaurantText.setText(restaurant.toUpperCase());

                orderCard.setOnClickListener(v -> {
                    Intent intent = new Intent(getActivity(), RateServiceActivity.class);
                    startActivity(intent);
                });
            }
        } else {
            // No active order
            if (emptyState != null) emptyState.setVisibility(View.VISIBLE);
            if (orderCard != null) orderCard.setVisibility(View.GONE);
        }
    }
}
