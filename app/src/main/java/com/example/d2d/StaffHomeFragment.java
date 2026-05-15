/*
package com.example.d2d;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class StaffHomeFragment extends Fragment {

    private final OkHttpClient client = new OkHttpClient();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.s_active_queue, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Hide back button in fragment mode
        View backBtn = view.findViewById(R.id.back_btn);
        if (backBtn != null) backBtn.setVisibility(View.GONE);

        // --- BUTTON HANDLERS FOR MOCK DATA (Testing updateOder.php) ---
        // Normally these would be inside a RecyclerView adapter
        View setReadyBtn = view.findViewById(R.id.mark_collected_1);
        if (setReadyBtn != null) {
            setReadyBtn.setOnClickListener(v -> updateOrderStatus("101", "ready"));
        }

        View collectedBtn = view.findViewById(R.id.mark_collected_btn_1);
        if (collectedBtn != null) {
            collectedBtn.setOnClickListener(v -> updateOrderStatus("101", "collected"));
        }

        view.findViewById(R.id.add_new_order_btn).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AssignOrderActivity.class);
            startActivity(intent);
        });
    }

    private void updateOrderStatus(String orderId, String newStatus) {
        String url = "https://wmc.ms.wits.ac.za/students/sgroup2676/d2dGroupProject/oderTrackingApp/orders/updateOder.php";

        RequestBody body = new FormBody.Builder()
                .add("order_id", orderId)
                .add("order_status", newStatus)
                .build();

        Request request = new Request.Builder().url(url).post(body).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> 
                        Toast.makeText(getContext(), "Network Error", Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (response.isSuccessful()) {
                            Toast.makeText(getContext(), "Order #" + orderId + " set to " + newStatus, Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getContext(), "Server Error: Failed to update", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}

*/