package com.example.d2d;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private List<Order> orderList;

    public OrderAdapter(List<Order> orderList) {
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);
        Context context = holder.itemView.getContext();

        holder.orderId.setText("ORDER #" + order.getOrderId());
        holder.restaurantName.setText(order.getRestaurantName());
        holder.status.setText(order.getStatus().toUpperCase());

        // Get user role from prefs
        android.content.SharedPreferences pref = context.getSharedPreferences("D2D_PREFS", Context.MODE_PRIVATE);
        String role = pref.getString("user_role", "customer");

        // ONLY customers are allowed to rate service
        if ("customer".equalsIgnoreCase(role) && order.getStatus().equalsIgnoreCase("collected") && order.getIsRated() == 0) {
            holder.rateBtn.setVisibility(View.VISIBLE);
            holder.rateBtn.setOnClickListener(v -> {
                Intent intent = new Intent(context, RateServiceActivity.class);
                intent.putExtra("order_id", order.getOrderId());
                intent.putExtra("restaurant_name", order.getRestaurantName());
                context.startActivity(intent);
            });
        } else {
            holder.rateBtn.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView orderId, restaurantName, status;
        Button rateBtn;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderId = itemView.findViewById(R.id.order_id_text);
            restaurantName = itemView.findViewById(R.id.order_restaurant_text);
            status = itemView.findViewById(R.id.order_status_text);
            rateBtn = itemView.findViewById(R.id.rate_order_btn);
        }
    }
}
