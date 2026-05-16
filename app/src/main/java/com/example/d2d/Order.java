package com.example.d2d;

public class Order {
    private String orderId;
    private String restaurantName;
    private String status;
    private String customerName;

    public Order(String orderId, String restaurantName, String status) {
        this.orderId = orderId;
        this.restaurantName = restaurantName;
        this.status = status;
        this.customerName = "Unknown";
    }
    
    public Order(String orderId, String restaurantName, String status, String customerName) {
        this.orderId = orderId;
        this.restaurantName = restaurantName;
        this.status = status;
        this.customerName = customerName;
    }

    public String getOrderId() { return orderId; }
    public String getRestaurantName() { return restaurantName; }
    public String getStatus() { return status; }
    public String getCustomerName() { return customerName; }
    
    public void setStatus(String status) { this.status = status; }
}
