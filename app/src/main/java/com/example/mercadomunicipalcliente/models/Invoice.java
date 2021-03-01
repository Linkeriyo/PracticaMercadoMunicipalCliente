package com.example.mercadomunicipalcliente.models;

import java.util.Date;
import java.util.List;

public class Invoice {

    public int number;
    public List<InvoiceLine> lines;
    public double total;
    public Date date;
    public String  uid;
    public boolean paid;

    public Invoice(int number, List<InvoiceLine> lines, double total, Date date, String uid) {
        this.number = number;
        this.lines = lines;
        this.total = total;
        this.date = date;
        this.uid = uid;
        this.paid = false;
    }
}
