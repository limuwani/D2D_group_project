package com.example.d2d;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class OrderHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private OrderAdapter adapter;
    private List<Order> orderList;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        ImageButton backBtn = findViewById(R.id.back_btn);
        if (backBtn != null) {
            backBtn.setOnClickListener(v -> finish());
        }

        recyclerView = findViewById(R.id.orders_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        orderList = new ArrayList<>();
        adapter = new OrderAdapter(orderList);
        recyclerView.setAdapter(adapter);

        dbHelper = new DatabaseHelper(this);
        loadOrdersFromDatabase();
    }

    private void loadOrdersFromDatabase() {
        Cursor cursor = dbHelper.getCompletedOrders();
        orderList.clear();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String id = cursor.getString(cursor.getColumnIndexOrThrow("order_id"));
                String restaurant = cursor.getString(cursor.getColumnIndexOrThrow("restaurant_name"));
                String status = cursor.getString(cursor.getColumnIndexOrThrow("status"));

                orderList.add(new Order(id, restaurant, status));
            } while (cursor.moveToNext());
            cursor.close();
        } else {
            // Inject mock order history if the database is empty for demonstration
            orderList.add(new Order("101", "Casa Nova", "Collected"));
            orderList.add(new Order("89", "D2D Frozen", "Collected"));
            orderList.add(new Order("74", "Wits Dining", "Collected"));
        }
        
        adapter.notifyDataSetChanged();
    }
}
