package com.example.d2d;

import com.google.gson.annotations.SerializedName;

public class Order {
    @SerializedName("order_id")
    private String orderId;

    @SerializedName("restaurant_name")
    private String restaurantName;

    private String status;

    @SerializedName("customer_name")
    private String customerName;

    @SerializedName("customer_id")
    private String customerId;

    @SerializedName("is_rated")
    private int isRated; // 0 for no, 1 for yes

    public Order(String orderId, String restaurantName, String status, String customerName, String customerId) {
        this(orderId, restaurantName, status, customerName, customerId, 0);
    }

    public Order(String orderId, String restaurantName, String status, String customerName, String customerId, int isRated) {
        this.orderId = orderId;
        this.restaurantName = restaurantName;
        this.status = status;
        this.customerName = customerName;
        this.customerId = customerId;
        this.isRated = isRated;
    }

    public String getOrderId() { return orderId; }
    public String getRestaurantName() { return restaurantName; }
    public String getStatus() { return status; }
    public String getCustomerName() { return customerName; }
    public String getCustomerId() { return customerId; }
    public void setStatus(String status) { this.status = status; }
    public int getIsRated() { return isRated; }
    public void setIsRated(int isRated) { this.isRated = isRated; }
}
