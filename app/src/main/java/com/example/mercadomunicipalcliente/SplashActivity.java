package com.example.mercadomunicipalcliente;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.example.mercadomunicipalcliente.data.AppData;
import com.example.mercadomunicipalcliente.models.Store;
import com.example.mercadomunicipalcliente.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SplashActivity extends AppCompatActivity {
    private final static String TAG = "SplashActivity";

    protected Context context = this;
    boolean storesLoaded = false, usersLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadData();
    }

    private void loadData() {
        DatabaseReference storesReference = FirebaseDatabase.getInstance().getReference("stores");
        storesReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Store> stores = new ArrayList<>();
                snapshot.getChildren().forEach(child -> {
                    Store store = child.getValue(Store.class);
                    if (store != null) {
                        stores.add(store);
                    }
                });
                AppData.storeList = stores;
                storesLoaded = true;
                tryNextActivity();
                storesReference.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                error.toException().printStackTrace();
            }
        });

        DatabaseReference usersReference = FirebaseDatabase.getInstance().getReference("users");
        usersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<User> users = new ArrayList<>();
                snapshot.getChildren().forEach(child -> {
                    User user = child.getValue(User.class);
                    if (user != null) {
                        users.add(user);
                    }
                });
                AppData.userList = users;
                usersLoaded = true;
                tryNextActivity();
                usersReference.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                error.toException().printStackTrace();
            }
        });
    }

    private void tryNextActivity() {
        if (storesLoaded && usersLoaded) {
            startActivity(new Intent(context, LoginActivity.class));
            finish();
        }
    }

    //      __
    //  ___( o)> (cuak)
    //  \ <_. )
    //   `---'
}
