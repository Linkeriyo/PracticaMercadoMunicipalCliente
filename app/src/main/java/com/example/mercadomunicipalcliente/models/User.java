package com.example.mercadomunicipalcliente.models;

import android.net.Uri;

import java.util.List;

public class User {

    public boolean admin;
    public String userID, name, image, imgStorage, email;
    public List<Invoice> invoices;
    public double balance;

    public User() {

    }

    public User(boolean admin, String userID, String name, String image, String imgStorage, String email, List<Invoice> invoices, double balance) {
        this.admin = admin;
        this.userID = userID;
        this.name = name;
        this.image = image;
        this.imgStorage = imgStorage;
        this.email = email;
        this.invoices = invoices;
        this.balance = balance;
    }

    public User(boolean admin, String userID, String name, Uri image, Uri imgStorage, String email, List<Invoice> invoices, double balance) {
        this.admin = admin;
        this.userID = userID;
        this.name = name;
        this.image = image.toString();
        this.imgStorage = imgStorage.toString();
        this.email = email;
        this.invoices = invoices;
        this.balance = balance;
    }
}
