package com.example.d2d;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    @SerializedName("status")
    private String status;

    @SerializedName("user_role")
    private String role;

    @SerializedName("user_id")
    private String user_id;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @SerializedName("restaurant_id")
    private String restaurant_id;

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getRestaurant_id() {
        return restaurant_id;
    }

    public void setRestaurant_id(String restaurant_id) {
        this.restaurant_id = restaurant_id;
    }
}
