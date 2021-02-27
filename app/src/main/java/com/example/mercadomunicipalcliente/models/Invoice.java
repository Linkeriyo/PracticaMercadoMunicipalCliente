package com.example.mercadomunicipalcliente.models;

import java.util.Date;
import java.util.List;

public class Invoice {

    int number;
    List<InvoiceLine> lines;
    double total;
    Date date;

}
