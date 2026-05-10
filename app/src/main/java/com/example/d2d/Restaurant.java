// Modified by Anon - Responsive UI & Flow
package com.example.d2d;

public class Restaurant {
    private int id;
    private String name;
    private String imageUrl;
    private String address;
    private double averageRating;

    public Restaurant(int id, String name, String imageUrl, String address, double averageRating) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.address = address;
        this.averageRating = averageRating;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getImageUrl() { return imageUrl; }
    public String getAddress() { return address; }
    public double getAverageRating() { return averageRating; }
}
