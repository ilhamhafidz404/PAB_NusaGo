package com.example.nusago.Models;

import java.io.Serializable;

public class User implements Serializable {
    private int id;
    private String name;
    private String email;
    private String role;
    private String deleted_at;

    public User(int id, String name, String email, String role, String deleted_at) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.deleted_at = deleted_at;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }
    public String getDeletedAt() {
        return deleted_at;
    }

    public String getFormattedDeletedAt() {
        if (deleted_at == null || deleted_at.trim().isEmpty() || "null".equalsIgnoreCase(deleted_at.trim())) {
            return null;
        }

        try {
            java.text.SimpleDateFormat serverFormat = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            java.text.SimpleDateFormat desiredFormat = new java.text.SimpleDateFormat("dd/MM/yy");

            java.util.Date date = serverFormat.parse(deleted_at);
            return desiredFormat.format(date);
        } catch (Exception e) {
            return deleted_at;
        }
    }

}
