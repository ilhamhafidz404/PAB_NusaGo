package com.example.nusago.Models;

public class Event {
    private String title;
    private String description;
    private String date;
    private String location;
    private int imageResId;

    public Event(String title, String description, String date, String location, int imageResId) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.location = location;
        this.imageResId = imageResId;
    }

    // getter
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getDate() { return date; }
    public String getLocation() { return location; }
    public int getImageResId() { return imageResId; }
}
