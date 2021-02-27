package com.example.mercadomunicipalcliente;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.mercadomunicipalcliente.models.Invoice;
import com.example.mercadomunicipalcliente.models.User;
import com.example.mercadomunicipalcliente.stores.StoresActivity;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RegisterDataActivity extends AppCompatActivity {
    String imageUri = "", imgStorage = "";
    ImageView imageView;
    private static final int PICK_IMAGE = 1;
    FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_data);

        //Event Analytics
        final FirebaseAnalytics analytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString("message", "Full Firebase integration");
        analytics.logEvent("InitScreen", bundle);

        TextView create = findViewById(R.id.textViewCreate);
        final EditText name = findViewById(R.id.editTextNameRegister);
        final EditText saldo = findViewById(R.id.editTextBalanceRegister);

        storage = FirebaseStorage.getInstance();

        List<Invoice> invoiceList = new ArrayList<>();
        imageView = findViewById(R.id.new_store_image_imageview2);
        imageView.setOnClickListener(v -> {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, PICK_IMAGE);
        });

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (name.getText().toString().isEmpty() || saldo.getText().toString().isEmpty()) {
                    Toast empty = Toast.makeText(getApplicationContext(), "Dejaste algun campo en blanco", Toast.LENGTH_SHORT);
                    empty.show();
                } else {
                    final FirebaseAuth auth = FirebaseAuth.getInstance();

                    newUser(false, auth.getCurrentUser().getUid(), name.getText().toString(), imageUri, imgStorage, getIntent().getStringExtra("email"), invoiceList, Double.parseDouble(saldo.getText().toString()));
                    Intent nextActivityIntent = new Intent(getApplicationContext(), StoresActivity.class);
                    startActivity(nextActivityIntent);
                    finish();
                }
            }
        });
    }

    public void newUser(boolean admin, String id, String name, String image, String imgStorage, String email, List<Invoice> invoiceList, Double saldo) {
        User user = new User(admin, id, name, image, imgStorage, email, invoiceList, saldo);

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").child(id).setValue(user);
    }

    private void putImage(String imageUri) {
        Glide.with(this)
                .load(imageUri)
                .centerCrop()
                .into(imageView);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE) {
            assert data != null;
            Uri uri = data.getData();
            imgStorage = uri.toString();
            StorageReference fileReference = storage.getReference("images").child(uri.getLastPathSegment());
            fileReference.putFile(uri).continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    throw Objects.requireNonNull(task.getException());
                }
                return fileReference.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    imageUri = Objects.requireNonNull(task.getResult()).toString();
                    putImage(imageUri);
                }
            });
        }
    }
}