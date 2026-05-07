package com.example.d2d;

public class Restaurant {
    private String id;
    private String name;
    private String location;
    private String rating;
    private String imageUrl; // URL for the image
    private int imageResource; // Fallback for local resources during testing

    public Restaurant() {
        // Empty constructor for Firebase
    }

    public Restaurant(String name, String location, String rating, int imageResource) {
        this.name = name;
        this.location = location;
        this.rating = rating;
        this.imageResource = imageResource;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getRating() { return rating; }
    public void setRating(String rating) { this.rating = rating; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public int getImageResource() { return imageResource; }
    public void setImageResource(int imageResource) { this.imageResource = imageResource; }
}
