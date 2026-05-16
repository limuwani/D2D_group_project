// Modified by Anon - Responsive UI & Flow
package com.example.d2d;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;
import java.io.IOException;
import java.io.InputStream;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class select_res extends AppCompatActivity {
    private final OkHttpClient client = new OkHttpClient();
    private RecyclerView recyclerView;
    private RestaurantAdapter adapter;
    private List<Restaurant> restaurantList;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_res);

        recyclerView = findViewById(R.id.restaurant_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        restaurantList = new ArrayList<>();
        adapter = new RestaurantAdapter(this, restaurantList);
        recyclerView.setAdapter(adapter);

        findViewById(R.id.back_to_login).setOnClickListener(v -> finish());

        // Fetch restaurants from your API
        fetchRestaurants();
    }

    private void fetchRestaurants() {
        String url = "https://wmc.ms.wits.ac.za/students/sgroup2676/d2dGroupProject/oderTrackingApp/images/displayAllRestaurant.php";
        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) { e.printStackTrace(); }

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
                                // Check if status is 1 (open) or 0 (closed)
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

                            runOnUiThread(() -> adapter.notifyDataSetChanged());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
