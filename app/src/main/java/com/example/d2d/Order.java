package com.example.d2d;

public class Order {
    private String orderId;
    private String restaurantName;
    private String status;
    private String customerName;
    private String customerId;

    public Order(String orderId, String restaurantName, String status) {
        this.orderId = orderId;
        this.restaurantName = restaurantName;
        this.status = status;
        this.customerName = "Unknown";
        this.customerId = "N/A";
    }
    
    public Order(String orderId, String restaurantName, String status, String customerName) {
        this.orderId = orderId;
        this.restaurantName = restaurantName;
        this.status = status;
        this.customerName = customerName;
        this.customerId = "N/A";
    }

    public Order(String orderId, String restaurantName, String status, String customerName, String customerId) {
        this.orderId = orderId;
        this.restaurantName = restaurantName;
        this.status = status;
        this.customerName = customerName;
        this.customerId = customerId;
    }

    public String getOrderId() { return orderId; }
    public String getRestaurantName() { return restaurantName; }
    public String getStatus() { return status; }
    public String getCustomerName() { return customerName; }
    public String getCustomerId() { return customerId; }
    
    public void setStatus(String status) { this.status = status; }
}
