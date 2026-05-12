package com.example.d2d;

public class Order {
    private String orderId;
    private String restaurantName;
    private String status;

    public Order(String orderId, String restaurantName, String status) {
        this.orderId = orderId;
        this.restaurantName = restaurantName;
        this.status = status;
    }

    public String getOrderId() { return orderId; }
    public String getRestaurantName() { return restaurantName; }
    public String getStatus() { return status; }
}
