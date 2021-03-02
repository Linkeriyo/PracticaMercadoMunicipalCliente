package com.example.mercadomunicipalcliente.products;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mercadomunicipalcliente.R;
import com.example.mercadomunicipalcliente.data.AppData;
import com.example.mercadomunicipalcliente.models.Product;
import com.example.mercadomunicipalcliente.models.Store;


import java.util.List;

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ProductViewHolder> {

    private final List<Product> productList;
    private final Context context;
    private final Store store;

    public ProductsAdapter(List<Product> productList, Context context, Store store) {
        this.productList = productList;
        this.context = context;
        this.store = store;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.product_row, parent, false);
        return new ProductViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        Glide.with(context).load(product.image).centerCrop().into(holder.productImageView);
        holder.productNameTextView.setText(product.desc);
        holder.productIdTextView.setText(product.ID);
        holder.productPriceTextView.setText(priceToString(product.price));
        if (AppData.activeInvoice != null) {
            holder.productCantEditText.setText("Cant. " + AppData.activeInvoice.getCurrentQuantityOf(product));
        }

        holder.productAddImagenView.setOnClickListener(v -> {
            AppData.activeInvoice.addProduct(product);
            holder.productCantEditText.setText("Cant. " + AppData.activeInvoice.getCurrentQuantityOf(product));
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder{

        TextView productNameTextView;
        TextView productIdTextView;
        TextView productPriceTextView;
        TextView productCantEditText;
        ImageView productImageView;
        ImageButton productAddImagenView;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productNameTextView = itemView.findViewById(R.id.product_name_textview);
            productIdTextView = itemView.findViewById(R.id.product_id_textview);
            productImageView = itemView.findViewById(R.id.product_image_imageview);
            productPriceTextView = itemView.findViewById(R.id.product_price_textview);
            productAddImagenView = itemView.findViewById(R.id.add_product_button);
            productCantEditText = itemView.findViewById(R.id.editTextCant);
        }
    }

    private static String priceToString(double price) {
        String priceString = String.valueOf(price);
        if (priceString.endsWith(".0")) {
            priceString = priceString.substring(0, priceString.length() - 2);
        }
        return priceString + "€";
    }
}
