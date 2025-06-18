package com.example.nusago.Models;

import java.io.Serializable;
public class News implements Serializable {
    private int id;
    private String title;
    private String image; // URL bisa null
    private String description;
    private String body;
    private int user_id;
    private String created_at;

    public News(int id, String title, String image, String description, String body, int user_id, String created_at) {
        this.id = id;
        this.title = title;
        this.image = image;
        this.description = description;
        this.body = body;
        this.user_id = user_id;
        this.created_at = created_at;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getImage() {
        return image;
    }

    public String getDescription() {
        return description;
    }

    public String getBody() {
        return body;
    }

    public int getUserId() {
        return user_id;
    }

    public String getCreatedAt() {
        return created_at;
    }
}
