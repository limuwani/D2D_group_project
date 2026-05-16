package com.example.d2d;

public class Feedback {
    private double rating;
    private String date;
    private String comment;

    public Feedback(double rating, String date, String comment) {
        this.rating = rating;
        this.date = date;
        this.comment = comment;
    }

    public double getRating() { return rating; }
    public String getDate() { return date; }
    public String getComment() { return comment; }
}
