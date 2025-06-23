package com.example.nusago.Models;

import java.io.Serializable;

public class Transaction implements Serializable {
    private int id;
    private String invoice;
    private int userId;
    private String userName;
    private String createdAt;

    public Transaction(int id, String invoice, int userId, String userName, String createdAt) {
        this.id = id;
        this.invoice = invoice;
        this.userId = userId;
        this.userName = userName;
        this.createdAt = createdAt;
    }

    public int getId()        { return id; }
    public String getInvoice(){ return invoice; }
    public int getUserId()    { return userId; }
    public String getUserName()    { return userName; }
    public String getCreatedAt() { return createdAt; }
}
