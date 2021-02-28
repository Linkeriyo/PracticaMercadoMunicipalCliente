package com.example.mercadomunicipalcliente.models;

import android.net.Uri;

public class Product {

    public String desc, ID, storeID, image, imgStorage;
    public double price, stock;
    public boolean kgUnit;

    public Product() {

    }

    public Product(String storeID, String ID, String desc, double price, Uri image, Uri imgStorage, double stock, boolean kgUnit) {
        this.storeID = storeID;
        this.ID = ID;
        this.desc = desc;
        this.price = price;
        this.image = image.toString();
        this.imgStorage = imgStorage.toString();
        this.stock = stock;
        this.kgUnit = kgUnit;
    }
}
