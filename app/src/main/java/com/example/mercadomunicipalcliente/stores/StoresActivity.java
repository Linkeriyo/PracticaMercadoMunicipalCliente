package com.example.mercadomunicipalcliente.stores;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mercadomunicipalcliente.LoginActivity;
import com.example.mercadomunicipalcliente.R;
import com.example.mercadomunicipalcliente.data.AppData;
import com.example.mercadomunicipalcliente.models.Store;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class StoresActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stores);
        setupRecyclerView();
        setupToolBar();
        setupDatabaseListener();
    }

    @SuppressLint("NonConstantResourceId")
    private void setupToolBar() {
        toolbar = findViewById(R.id.stores_toolbar);
        toolbar.setNavigationOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.stores_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new StoresAdapter(AppData.storeList, this));
        if (AppData.storeList.isEmpty()) {
            findViewById(R.id.no_stores_textview).setVisibility(View.VISIBLE);
        }
        recyclerView.getAdapter().registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                if (!AppData.storeList.isEmpty()) {
                    findViewById(R.id.no_stores_textview).setVisibility(View.INVISIBLE);
                } else {
                    findViewById(R.id.no_stores_textview).setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void setupDatabaseListener() {
        DatabaseReference storesReference = FirebaseDatabase.getInstance().getReference("stores");
        storesReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Store> stores = new ArrayList<>();
                snapshot.getChildren().forEach(child -> {
                    stores.add(child.getValue(Store.class));
                });
                AppData.storeList.clear();
                AppData.storeList.addAll(stores);
                recyclerView.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

}