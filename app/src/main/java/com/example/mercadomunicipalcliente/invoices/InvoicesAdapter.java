package com.example.mercadomunicipalcliente.invoices;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mercadomunicipalcliente.R;
import com.example.mercadomunicipalcliente.data.AppData;
import com.example.mercadomunicipalcliente.models.Invoice;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class InvoicesAdapter extends RecyclerView.Adapter<InvoicesAdapter.InvoiceViewHolder> {

    private final List<Invoice> invoiceList;
    private final Context context;
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd-mm-yyyy");

    public InvoicesAdapter(List<Invoice> invoiceList, Context context) {
        this.invoiceList = new ArrayList<>(invoiceList);
        this.context = context;
    }

    @NonNull
    @Override
    public InvoiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.invoice_row, parent, false);
        return new InvoiceViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull InvoiceViewHolder holder, int position) {
        Invoice invoice = invoiceList.get(position);
        holder.invoiceIDTextView.setText("nº: " + invoice.number);
        holder.invoiceDateTextView.setText(sdf.format(invoice.date));
        if (invoice.lines == null) {
            invoice.lines = new ArrayList<>();
        }
        holder.invoiceLinesTextView.setText("Artículos: " + invoice.lines.size());
        holder.invoiceTotalTextView.setText("Total: " + priceToString(invoice.total));
        if (invoice.paid) {
            holder.invoicePagadoTextView.setText("Pagado");
            holder.invoicePagadoTextView.setTextColor(Color.GREEN);
        } else {
            holder.invoicePagadoTextView.setText("No pagado");
            holder.invoicePagadoTextView.setTextColor(Color.RED);
        }

        if (invoice.equals(AppData.activeInvoice)) {
            TypedValue typedValue = new TypedValue();

            context.getTheme().resolveAttribute(R.attr.colorButtonNormal, typedValue, true);
            holder.itemView.setBackgroundColor(typedValue.data);
            holder.deleteInvoiceButton.setVisibility(View.INVISIBLE);
        }

        holder.deleteInvoiceButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("¿Seguro que quieres eliminar el producto? Los datos no se podrán recuperar.")
                    .setPositiveButton("Sí", (dialog, which) -> {

                        //Borrar la lista de la base de datos.
                        DatabaseReference invoicesReference = FirebaseDatabase.getInstance().getReference("invoices");
                        invoicesReference.child(String.valueOf(AppData.invoiceList.get(position).number)).removeValue().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                notifyDataSetChanged();
                            }
                        });
                    })
                    .setNegativeButton("No", (dialog, which) -> {
                    });
            builder.create().show();
        });

        holder.itemView.setOnClickListener(v -> {
            context.startActivity(new Intent(context, InvoiceDetailsActivity.class).putExtra("invoiceNumber", invoice.number));
        });
    }

    @Override
    public int getItemCount() {
        return invoiceList.size();
    }

    public static class InvoiceViewHolder extends RecyclerView.ViewHolder {

        TextView invoiceIDTextView;
        TextView invoiceTotalTextView;
        TextView invoiceDateTextView;
        TextView invoiceLinesTextView;
        TextView invoicePagadoTextView;
        ImageButton deleteInvoiceButton;

        public InvoiceViewHolder(@NonNull View itemView) {
            super(itemView);
            invoiceIDTextView = itemView.findViewById(R.id.invoice_id_textview);
            invoiceTotalTextView = itemView.findViewById(R.id.invoice_total_textview);
            invoiceDateTextView = itemView.findViewById(R.id.invoice_date_textview);
            invoiceLinesTextView = itemView.findViewById(R.id.invoice_number_lines_textview);
            deleteInvoiceButton = itemView.findViewById(R.id.delete_invoice_button);
            invoicePagadoTextView = itemView.findViewById(R.id.invoice_paid_textview);
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
