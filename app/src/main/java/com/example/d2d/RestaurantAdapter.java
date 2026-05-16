// Modified by Anon - Responsive UI & Flow
package com.example.d2d;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import android.graphics.Color;
import android.widget.Button;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.RestaurantViewHolder> {

    private List<Restaurant> restaurantList;
    private Activity activity;
    private final OkHttpClient client = new OkHttpClient();

    public RestaurantAdapter(Activity activity, List<Restaurant> restaurantList) {
        this.activity = activity;
        this.restaurantList = restaurantList;
    }

    @NonNull
    @Override
    public RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_restaurant, parent, false);
        return new RestaurantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantViewHolder holder, int position) {
        Restaurant restaurant = restaurantList.get(position);
        holder.resName.setText(restaurant.getName());
        
        // Dynamic Location
        if (restaurant.getAddress() != null && !restaurant.getAddress().equals("null") && !restaurant.getAddress().isEmpty()) {
            holder.resLocation.setText(restaurant.getAddress());
        } else {
            holder.resLocation.setText("Location TBD");
        }

        // Dynamic Average Rating
        holder.resRating.setText(String.format("Avg: %.1f", restaurant.getAverageRating()));

        // Dynamic Open/Closed Status
        if (restaurant.isOpen()) {
            holder.resStatusBtn.setText("OPEN NOW");
            holder.resStatusBtn.setTextColor(Color.GREEN);
            holder.resStatusBtn.setBackgroundResource(R.drawable.green_border_bg);
            holder.resImage.setAlpha(1.0f); // Full brightness
        } else {
            holder.resStatusBtn.setText("CLOSED");
            holder.resStatusBtn.setTextColor(Color.RED);
            holder.resStatusBtn.setBackgroundResource(R.drawable.red_border_bg);
            holder.resImage.setAlpha(0.5f); // Dimmed
        }
        
        // Load image from URL
        loadImage(restaurant.getImageUrl(), holder.resImage);

        holder.itemView.setOnClickListener(v -> {
            if (restaurant.isOpen()) {
                Intent intent = new Intent(activity, ConfirmTakeawayActivity.class);
                intent.putExtra("restaurant_id", restaurant.getId());
                intent.putExtra("restaurant_name", restaurant.getName());
                activity.startActivity(intent);
            } else {
                // Optional: Show a message that the restaurant is closed
                android.widget.Toast.makeText(activity, restaurant.getName() + " is currently closed.", android.widget.Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return restaurantList.size();
    }

    private void loadImage(String url, ImageView imageView) {
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) { e.printStackTrace(); }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    InputStream inputStream = response.body().byteStream();
                    final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    activity.runOnUiThread(() -> {
                        if (bitmap != null) {
                            imageView.setImageBitmap(bitmap);
                        }
                    });
                }
            }
        });
    }

    public static class RestaurantViewHolder extends RecyclerView.ViewHolder {
        ImageView resImage;
        TextView resName;
        TextView resLocation;
        TextView resRating;
        Button resStatusBtn;

        public RestaurantViewHolder(@NonNull View itemView) {
            super(itemView);
            resImage = itemView.findViewById(R.id.res_image_item);
            resName = itemView.findViewById(R.id.res_name_item);
            resLocation = itemView.findViewById(R.id.res_location_item);
            resRating = itemView.findViewById(R.id.res_rating_item);
            resStatusBtn = itemView.findViewById(R.id.res_status_btn);
        }
    }
}
