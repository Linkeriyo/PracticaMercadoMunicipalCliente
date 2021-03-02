package com.example.mercadomunicipalcliente.models;

public class InvoiceLine {

    public String storeID, productID;
    public double quantity;

    public InvoiceLine(String storeID, String productID, double quantity) {
        this.storeID = storeID;
        this.productID = productID;
        this.quantity = quantity;
    }

    public InvoiceLine() {

    }
}
