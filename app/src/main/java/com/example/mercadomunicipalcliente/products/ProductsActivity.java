package com.example.mercadomunicipalcliente.products;

import android.annotation.SuppressLint;
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
import com.example.mercadomunicipalcliente.models.Product;
import com.example.mercadomunicipalcliente.models.Store;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ProductsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    Toolbar toolbar;
    List<Product> productList;
    Store store;
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_products);
        if (AppData.invoiceList == null) {
            loadInvoicesIfNull();
        } else {
            store = AppData.storeList.get(getIntent().getIntExtra("storeNumber", 0));
            System.out.println(store.name);
            setupProductList();
            setupRecyclerView();
            setupToolBar();
            setupDatabaseListener();
        }

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
            store = AppData.storeList.get(getIntent().getIntExtra("storeNumber", 0));
            System.out.println(store.name);
            setupProductList();
            setupRecyclerView();
            setupToolBar();
            setupDatabaseListener();
        });
    }

    @SuppressLint("NonConstantResourceId")
    private void setupToolBar() {
        toolbar = findViewById(R.id.products_toolbar);
        toolbar.setSubtitle(store.name);
        toolbar.setNavigationOnClickListener(v -> {
            finish();
        });
    }

    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.products_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new ProductsAdapter(productList, this, store));
        if (productList.isEmpty()) {
            findViewById(R.id.no_products_textview).setVisibility(View.VISIBLE);
        }
        recyclerView.getAdapter().registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                if (!productList.isEmpty()) {
                    findViewById(R.id.no_products_textview).setVisibility(View.INVISIBLE);
                } else {
                    findViewById(R.id.no_products_textview).setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void setupDatabaseListener() {
        DatabaseReference productsReference = FirebaseDatabase.getInstance()
                .getReference("stores")
                .child(store.ID)
                .child("products");
        productsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                setupProductList();
                recyclerView.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void setupProductList() {
        if (store.products != null) {
            productList = store.products;
        } else {
            productList = new ArrayList<>();
        }
    }

}