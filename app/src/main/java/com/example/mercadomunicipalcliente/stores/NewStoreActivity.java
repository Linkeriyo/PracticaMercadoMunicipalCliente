package com.example.mercadomunicipalcliente.stores;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.mercadomunicipalcliente.R;
import com.example.mercadomunicipalcliente.models.Store;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

public class NewStoreActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 1;
    FirebaseDatabase database;
    FirebaseStorage storage;
    Toolbar toolbar;
    TextView nameTextView, idTextView;
    ImageView imageView;
    Uri imageUri, postImageUri;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_store);
        setupFirebaseVariables();
        setupViews();
        setupToolbar();
    }

    private void setupFirebaseVariables() {
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
    }

    private void setupViews() {
        nameTextView = findViewById(R.id.new_store_name_textview);
        idTextView = findViewById(R.id.new_store_id_textview);
        imageView = findViewById(R.id.new_store_image_imageview);
        imageView.setOnClickListener(v -> {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, PICK_IMAGE);
        });
        progressBar = findViewById(R.id.new_store_progressbar);
    }

    private void setupToolbar() {
        toolbar = findViewById(R.id.new_store_toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.edit_store_confirm_option && isStoreOk()) {
                if (postImageUri == null) {
                    postImageUri = Uri.EMPTY;
                }
                Store store = new Store(idTextView.getText().toString(), nameTextView.getText().toString(), imageUri, postImageUri);
                DatabaseReference storesReference = FirebaseDatabase.getInstance().getReference("stores");
                storesReference.child(idTextView.getText().toString()).setValue(store)
                        .addOnCompleteListener(task -> finish());
            } else {
                Toast.makeText(this,
                        "Los campos no deben estar vacíos",
                        Toast.LENGTH_SHORT
                ).show();
            }
            return true;
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE) {
            if (data != null) {
                progressBar.setVisibility(View.VISIBLE);
                Uri uri = data.getData();
                StorageReference fileReference = storage.getReference("images").child(uri.getLastPathSegment());
                fileReference.putFile(uri).continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw Objects.requireNonNull(task.getException());
                    }
                    return fileReference.getDownloadUrl();
                }).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        imageUri = Objects.requireNonNull(task.getResult());
                        if (postImageUri != null) {
                            StorageReference previousFileReference = storage.getReference("images").child(postImageUri.getLastPathSegment());
                            previousFileReference.delete();
                        }
                        putImage(imageUri);
                        postImageUri = uri;
                    }
                });
            }
        }
    }

    private void putImage(Uri imageUri) {
        Glide.with(this)
                .load(imageUri)
                .centerCrop()
                .into(imageView);
        progressBar.setVisibility(View.INVISIBLE);
    }

    private boolean isStoreOk() {
        return (!nameTextView.getText().toString().isEmpty()
                && !idTextView.getText().toString().isEmpty());
    }
}