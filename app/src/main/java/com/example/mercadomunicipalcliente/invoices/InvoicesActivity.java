package com.example.mercadomunicipalcliente.invoices;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mercadomunicipalcliente.R;
import com.example.mercadomunicipalcliente.data.AppData;
import com.example.mercadomunicipalcliente.models.Invoice;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class InvoicesActivity extends AppCompatActivity{

    RecyclerView recyclerView;
    Toolbar toolbar;
    List<Invoice> invoiceList;
    Invoice invoice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoices);
        setupRecyclerView();
        setupToolBar();
        setupDatabaseListener();
    }

    @SuppressLint("NonConstantResourceId")
    private void setupToolBar() {
        toolbar = findViewById(R.id.invoices_toolbar);
        /*toolbar.setOnMenuItemClickListener(item -> {
            //Aun no cree nuevas facturas
            if (item.getItemId() == R.id.add_invoice_option) {
                startActivity(new Intent(this, com.example.practicamercadomunicipal.invoices.NewInvoiceActivity.class).putExtra("storeID", store.ID));
            }
            return true;
        });*/
        toolbar.setNavigationOnClickListener(v -> {
            finish();
        });
    }

    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.invoices_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new InvoicesAdapter(invoiceList, this));
        if (invoiceList.isEmpty()) {
            findViewById(R.id.no_invoices_textview).setVisibility(View.VISIBLE);
        }
        recyclerView.getAdapter().registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                if (!invoiceList.isEmpty()) {
                    findViewById(R.id.no_invoices_textview).setVisibility(View.INVISIBLE);
                } else {
                    findViewById(R.id.no_invoices_textview).setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void setupDatabaseListener() {
        DatabaseReference storesReference = FirebaseDatabase.getInstance().getReference("invoices");
        storesReference.addValueEventListener(new ValueEventListener() {
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

}