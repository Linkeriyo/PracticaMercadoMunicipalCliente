package com.example.mercadomunicipalcliente.stores;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mercadomunicipalcliente.R;
import com.example.mercadomunicipalcliente.models.Store;
import com.example.mercadomunicipalcliente.products.ProductsActivity;

import java.util.List;

public class StoresAdapter extends RecyclerView.Adapter<StoresAdapter.StoreViewHolder> {

    private final List<Store> storeList;
    private final Context context;

    public StoresAdapter(List<Store> storeList, Context context) {
        this.storeList = storeList;
        this.context = context;
    }

    @NonNull
    @Override
    public StoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.store_row, parent, false);
        return new StoreViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull StoreViewHolder holder, int position) {
        Store store = storeList.get(position);
        Glide.with(context).load(store.image).centerCrop().into(holder.storeImageView);
        holder.storeNameTextView.setText(store.name);
        holder.storeIdTextView.setText(store.ID);


        holder.itemView.setOnClickListener(v -> {
            context.startActivity(new Intent(context, ProductsActivity.class).putExtra("storeNumber", position));
        });
    }

    @Override
    public int getItemCount() {
        return storeList.size();
    }

    public static class StoreViewHolder extends RecyclerView.ViewHolder{

        TextView storeNameTextView;
        TextView storeIdTextView;
        ImageView storeImageView;

        public StoreViewHolder(@NonNull View itemView) {
            super(itemView);
            storeNameTextView = itemView.findViewById(R.id.store_name_textview);
            storeIdTextView = itemView.findViewById(R.id.store_id_textview);
            storeImageView = itemView.findViewById(R.id.store_image_imageview);
        }
    }
}
