package com.example.mercadomunicipalcliente.products;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import com.example.mercadomunicipalcliente.models.Product;
import com.example.mercadomunicipalcliente.models.Store;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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
        holder.productStockTextView.setText(stockToString(product.stock));

        holder.deleteProductButton.setOnClickListener(v -> {
            //Borrar la imagen de firebase.
            Uri storageUri = Uri.parse(productList.get(position).imgStorage);
            StorageReference imagesReference = FirebaseStorage.getInstance().getReference("images");
            imagesReference.child(storageUri.getLastPathSegment()).delete();

            //Borrar el local de la base de datos.
            DatabaseReference productsReference = FirebaseDatabase.getInstance().getReference("products");
            productsReference.child(productList.get(position).ID).removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    notifyDataSetChanged();
                }
            });
        });

        holder.editProductButton.setOnClickListener(v -> {
            context.startActivity(new Intent(context, EditProductActivity.class)
                    .putExtra("productNumber", position)
                    .putExtra("storeID", store.ID)
            );
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder{

        TextView productNameTextView;
        TextView productStockTextView;
        TextView productIdTextView;
        TextView productPriceTextView;
        ImageView productImageView;
        ImageButton deleteProductButton;
        ImageButton editProductButton;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productNameTextView = itemView.findViewById(R.id.product_name_textview);
            productStockTextView = itemView.findViewById(R.id.product_stock_textview);
            productIdTextView = itemView.findViewById(R.id.product_id_textview);
            productImageView = itemView.findViewById(R.id.product_image_imageview);
            productPriceTextView = itemView.findViewById(R.id.product_price_textview);
            deleteProductButton = itemView.findViewById(R.id.delete_product_button);
            editProductButton = itemView.findViewById(R.id.edit_product_button);
        }
    }

    private static String priceToString(double price) {
        String priceString = String.valueOf(price);
        if (priceString.endsWith(".0")) {
            priceString = priceString.substring(0, priceString.length() - 2);
        }
        return priceString + "€";
    }

    private static String stockToString(int stock) {
        String stockString = String.valueOf(stock);

        return stockString + " En stock";
    }
}
