package com.example.mercadomunicipalcliente.models;

import com.example.mercadomunicipalcliente.data.AppData;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Invoice {

    public int number;
    public List<InvoiceLine> lines;
    public double total;
    public Date date;
    public String uid;
    public boolean paid, cancelled;

    public Invoice(int number, List<InvoiceLine> lines, double total, Date date, String uid) {
        this.number = number;
        this.lines = lines;
        this.total = total;
        this.date = date;
        this.uid = uid;
        this.paid = false;
        this.cancelled = false;
    }

    public Invoice() {

    }

    public Invoice(int number) {
        this.number = number;
        this.lines = new ArrayList<>();
        this.total = 0;
        this.date = new Date();
        this.uid = FirebaseAuth.getInstance().getUid();
        this.paid = false;
        this.cancelled = false;
    }

    public Invoice(Invoice invoice) {
        this.number = AppData.getNextInvoiceNumber();
        this.lines = invoice.lines;
        this.total = invoice.total;
        this.date = invoice.date;
        this.uid = invoice.uid;
        this.paid = invoice.paid;
        this.cancelled = invoice.cancelled;
    }

    public void addProduct(Product product) {
        boolean found = false;
        for (InvoiceLine line : lines) {
            if (line.productID.equals(product.ID)) {
                line.quantity++;
                found = true;
            }
        }
        if (!found) {
            InvoiceLine newLine = new InvoiceLine(product.storeID, product.ID, 1);
            lines.add(newLine);
        }
        calculateTotal();
    }

    public double getCurrentQuantityOf(Product product) {
        for (InvoiceLine line : lines) {
            if (line.productID.equals(product.ID)){
                return line.quantity;
            }
        }
        return 0;
    }

    public void addLine(InvoiceLine line) {
        lines.add(line);
        calculateTotal();
    }

    public void removeLine(InvoiceLine line) {
        lines.remove(line);
        calculateTotal();
    }

    public void calculateTotal() {
        int total = 0;
        for (InvoiceLine line : lines) {
            Product product = AppData.getProductById(line.storeID, line.productID);
            if (product != null) {
                total += line.quantity * product.price;
            }
        }
        this.total = total;
    }
}
