package com.example.mercadomunicipalcliente.invoices;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.ActionMenuItem;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mercadomunicipalcliente.R;
import com.example.mercadomunicipalcliente.data.AppData;
import com.example.mercadomunicipalcliente.models.Invoice;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class InvoicesActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    Toolbar toolbar;
    FirebaseAuth auth;
    List<Invoice> recyclerList;
    public static InvoicesActivity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_invoices);
        if (AppData.invoiceList == null) {
            loadInvoicesIfNull();
        } else {
            setupRecyclerView();
            setupToolBar();
            setupDatabaseListener();
            if (AppData.activeInvoice == null) {
                AppData.createActiveInvoice();
            }
        }
        activity = this;
    }

    private void loadInvoicesIfNull() {
        DatabaseReference invoicesReference = FirebaseDatabase.getInstance().getReference("users").child(auth.getUid()).child("invoices");
        invoicesReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Invoice> invoices = new ArrayList<>();
                task.getResult().getChildren().forEach(child -> {
                    invoices.add(child.getValue(Invoice.class));
                });
                AppData.invoiceList = invoices;
            } else {
                AppData.invoiceList = new ArrayList<>();
            }
            if (AppData.invoiceList.isEmpty() || AppData.activeInvoice == null) {
                AppData.createActiveInvoice();
            }
            setupRecyclerView();
            setupToolBar();
            setupDatabaseListener();
        });
    }

    @SuppressLint("NonConstantResourceId")
    private void setupToolBar() {
        toolbar = findViewById(R.id.invoices_toolbar);
        toolbar.setNavigationOnClickListener(v -> {
            finish();
        });
    }

    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.invoices_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerList = AppData.invoiceList;
        recyclerList.add(0, AppData.activeInvoice);
        recyclerView.setAdapter(new InvoicesAdapter(recyclerList, this));
    }

    private void setupDatabaseListener() {
        DatabaseReference invoicesReference = FirebaseDatabase.getInstance().getReference("users").child(auth.getUid()).child("invoices");
        invoicesReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Invoice> invoices = new ArrayList<>();
                snapshot.getChildren().forEach(child -> {
                    invoices.add(child.getValue(Invoice.class));
                });
                AppData.invoiceList.clear();
                AppData.invoiceList.addAll(invoices);
                recyclerView.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void notifyRecyclerView() {
        recyclerList = AppData.invoiceList;
        recyclerView.getAdapter().notifyDataSetChanged();
    }
}