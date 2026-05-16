package com.example.d2d;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class StaffQueueAdapter extends RecyclerView.Adapter<StaffQueueAdapter.ViewHolder> {
    private List<Order> orderList;
    private Activity activity;
    private OnStatusChangeListener listener;

    public interface OnStatusChangeListener {
        void onStatusChange(Order order, String newStatus);
    }

    public StaffQueueAdapter(Activity activity, List<Order> orderList, OnStatusChangeListener listener) {
        this.activity = activity;
        this.orderList = orderList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_staff_queue, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order order = orderList.get(position);
        
        holder.orderIdText.setText("ORDER #" + order.getOrderId());
        holder.customerNameText.setText("Customer: " + order.getCustomerName());
        holder.statusText.setText(order.getStatus().toUpperCase());

        if (order.getStatus().equalsIgnoreCase("preparing") || order.getStatus().equalsIgnoreCase("in prep")) {
            holder.statusText.setBackgroundResource(R.drawable.glass_red);
        } else if (order.getStatus().equalsIgnoreCase("ready")) {
            holder.statusText.setBackgroundResource(R.drawable.green_border_bg);
        } else {
            holder.statusText.setBackgroundResource(R.drawable.white_border_bg);
        }

        holder.markReadyBtn.setOnClickListener(v -> {
            if (listener != null) listener.onStatusChange(order, "Ready");
        });

        holder.markCollectedBtn.setOnClickListener(v -> {
            if (listener != null) listener.onStatusChange(order, "Collected");
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView orderIdText, statusText, customerNameText;
        Button markReadyBtn, markCollectedBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            orderIdText = itemView.findViewById(R.id.queue_order_id_text);
            statusText = itemView.findViewById(R.id.queue_order_status_text);
            customerNameText = itemView.findViewById(R.id.queue_customer_name_text);
            markReadyBtn = itemView.findViewById(R.id.queue_mark_ready_btn);
            markCollectedBtn = itemView.findViewById(R.id.queue_mark_collected_btn);
        }
    }
}
