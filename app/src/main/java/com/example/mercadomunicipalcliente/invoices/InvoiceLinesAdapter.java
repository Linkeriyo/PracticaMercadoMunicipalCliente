package com.example.mercadomunicipalcliente.invoices;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mercadomunicipalcliente.R;
import com.example.mercadomunicipalcliente.data.AppData;
import com.example.mercadomunicipalcliente.models.Invoice;
import com.example.mercadomunicipalcliente.models.InvoiceLine;
import com.example.mercadomunicipalcliente.models.Product;

import java.util.List;

public class InvoiceLinesAdapter extends RecyclerView.Adapter<InvoiceLinesAdapter.InvoicelineViewHolder> {

    private final List<InvoiceLine> invoicelineList;
    private final Context context;
    private final Invoice invoice;

    public InvoiceLinesAdapter(List<InvoiceLine> invoicelineList, Context context, Invoice invoice) {
        this.invoicelineList = invoicelineList;
        this.context = context;
        this.invoice = invoice;
    }

    @NonNull
    @Override
    public InvoicelineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.invoice_line_row, parent, false);
        return new InvoicelineViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull InvoicelineViewHolder holder, int position) {
        InvoiceLine invoiceline = invoicelineList.get(position);
        Product product = AppData.getProductById(invoiceline.storeID, invoiceline.productID);
        holder.invoiceRowProductTextView.setText(invoiceline.productID);
        holder.invoiceRowQuantityTextView.setText(invoiceline.quantity + "");
        holder.invoiceRowStoreTextView.setText(invoiceline.storeID);
        holder.invoiceRowSubtotalTextView.setText(invoiceline.quantity * product.price + "");

        if (invoice.paid) {
            holder.deleteInvoiceLineButton.setEnabled(false);
            holder.downInvoiceRowButton.setEnabled(false);
            holder.upInvoiceRowButton.setEnabled(false);
        }

        holder.deleteInvoiceLineButton.setOnClickListener(v -> {
            invoice.removeLine(invoiceline);
            notifyDataSetChanged();
        });
        holder.upInvoiceRowButton.setOnClickListener(v -> {
            invoiceline.quantity++;
            notifyDataSetChanged();
        });
        holder.downInvoiceRowButton.setOnClickListener(v -> {
            invoiceline.quantity--;
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return invoicelineList.size();
    }

    public static class InvoicelineViewHolder extends RecyclerView.ViewHolder {

        ImageButton deleteInvoiceLineButton;
        ImageButton upInvoiceRowButton;
        ImageButton downInvoiceRowButton;
        TextView invoiceRowSubtotalTextView;
        TextView invoiceRowQuantityTextView;
        TextView invoiceRowStoreTextView;
        TextView invoiceRowProductTextView;

        public InvoicelineViewHolder(@NonNull View itemView) {
            super(itemView);
            deleteInvoiceLineButton = itemView.findViewById(R.id.delete_invoice_row_button);
            upInvoiceRowButton = itemView.findViewById(R.id.up_invoice_row_button);
            downInvoiceRowButton = itemView.findViewById(R.id.down_invoice_row_button);
            invoiceRowSubtotalTextView = itemView.findViewById(R.id.invoice_row_subtotal_textview);
            invoiceRowQuantityTextView = itemView.findViewById(R.id.invoice_row_quantity_textview);
            invoiceRowStoreTextView = itemView.findViewById(R.id.invoice_row_store_textview);
            invoiceRowProductTextView = itemView.findViewById(R.id.invoice_row_product_textview);
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
