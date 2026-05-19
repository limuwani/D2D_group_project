package com.example.d2d;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HomeFragment extends Fragment {

    private final OkHttpClient client = new OkHttpClient();
    private RecyclerView recyclerView;
    private ProgressBar loadingSpinner;
    private RestaurantAdapter adapter;
    private List<Restaurant> restaurantList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Reuse the existing select_res layout
        return inflater.inflate(R.layout.select_res, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.restaurant_recycler_view);
        loadingSpinner = view.findViewById(R.id.loading_spinner);
        
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        restaurantList = new ArrayList<>();
        adapter = new RestaurantAdapter(getActivity(), restaurantList);
        recyclerView.setAdapter(adapter);

        // Fetch restaurants from your API
        fetchRestaurants();
    }

    private void fetchRestaurants() {
        if (loadingSpinner != null) loadingSpinner.setVisibility(View.VISIBLE);
        if (recyclerView != null) recyclerView.setVisibility(View.GONE);

        String url = "https://wmc.ms.wits.ac.za/students/sgroup2676/d2dGroupProject/oderTrackingApp/images/displayAllRestaurant.php";
        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (loadingSpinner != null) loadingSpinner.setVisibility(View.GONE);
                    });
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String jsonData = response.body().string();
                        JSONObject jsonObject = new JSONObject(jsonData);
                        if (jsonObject.getString("status").equals("success")) {
                            JSONArray array = jsonObject.getJSONArray("restaurants");

                            restaurantList.clear();
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject resObj = array.getJSONObject(i);
                                int statusInt = resObj.optInt("is_open", 1);

                                restaurantList.add(new Restaurant(
                                        resObj.getInt("restaurant_id"),
                                        resObj.getString("name"),
                                        resObj.getString("image_url"),
                                        resObj.optString("address", "Location TBD"),
                                        resObj.optDouble("average_rating", 0.0),
                                        statusInt == 1
                                ));
                            }

                            if (getActivity() != null) {
                                getActivity().runOnUiThread(() -> {
                                    adapter.notifyDataSetChanged();
                                    if (loadingSpinner != null) loadingSpinner.setVisibility(View.GONE);
                                    if (recyclerView != null) recyclerView.setVisibility(View.VISIBLE);
                                });
                            }
                        } else {
                            if (getActivity() != null) {
                                getActivity().runOnUiThread(() -> {
                                    if (loadingSpinner != null) loadingSpinner.setVisibility(View.GONE);
                                });
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                if (loadingSpinner != null) loadingSpinner.setVisibility(View.GONE);
                            });
                        }
                    }
                } else {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            if (loadingSpinner != null) loadingSpinner.setVisibility(View.GONE);
                        });
                    }
                }
            }
        });
    }
}
