package com.example.d2d;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.RestaurantViewHolder> {

    private List<Restaurant> restaurantList;
    private OnRestaurantClickListener listener;

    public interface OnRestaurantClickListener {
        void onRestaurantClick(Restaurant restaurant);
    }

    public RestaurantAdapter(List<Restaurant> restaurantList, OnRestaurantClickListener listener) {
        this.restaurantList = restaurantList;
        this.listener = listener;
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
        holder.resLocation.setText(restaurant.getLocation());
        holder.resRating.setText(restaurant.getRating());
        
        // For now, using local resources. Later we will use Glide for URLs.
        if (restaurant.getImageResource() != 0) {
            holder.resImage.setImageResource(restaurant.getImageResource());
        }

        holder.itemView.setOnClickListener(v -> listener.onRestaurantClick(restaurant));
    }

    @Override
    public int getItemCount() {
        return restaurantList.size();
    }

    public static class RestaurantViewHolder extends RecyclerView.ViewHolder {
        ImageView resImage;
        TextView resName, resLocation, resRating;

        public RestaurantViewHolder(@NonNull View itemView) {
            super(itemView);
            resImage = itemView.findViewById(R.id.res_image);
            resName = itemView.findViewById(R.id.res_name);
            resLocation = itemView.findViewById(R.id.res_location);
            resRating = itemView.findViewById(R.id.res_rating);
        }
    }
}
