package com.example.mercadomunicipalcliente.models;

import android.net.Uri;

public class Product {

    public String desc, ID, storeID, image, imgStorage;
    public double price;

    public Product() {

    }

    public Product(String storeID, String ID, String desc, double price, String image, String imgStorage) {
        this.storeID = storeID;
        this.ID = ID;
        this.desc = desc;
        this.price = price;
        this.image = image;
        this.imgStorage = imgStorage;
    }

    public Product(String storeID, String ID, String desc, double price, Uri image, Uri imgStorage) {
        this.storeID = storeID;
        this.ID = ID;
        this.desc = desc;
        this.price = price;
        this.image = image.toString();
        this.imgStorage = imgStorage.toString();
    }
}
