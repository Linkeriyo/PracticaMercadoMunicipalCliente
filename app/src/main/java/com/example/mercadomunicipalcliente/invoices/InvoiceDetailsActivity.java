package com.example.mercadomunicipalcliente.invoices;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mercadomunicipalcliente.R;
import com.example.mercadomunicipalcliente.data.AppData;
import com.example.mercadomunicipalcliente.models.Invoice;
import com.example.mercadomunicipalcliente.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class InvoiceDetailsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    Toolbar toolbar;
    Invoice invoice;
    Button payButton, delayButton;
    TextView totalTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice_details);
        totalTextView = findViewById(R.id.invoice_details_total_textview);
        invoice = AppData.getInvoiceByNumber(getIntent().getIntExtra("invoiceNumber", 0));
        if (invoice == null) {
            invoice = AppData.activeInvoice;
        }
        setupToolBar();
        setupRecyclerView();
        setupButtons();
    }

    @SuppressLint("NonConstantResourceId")
    private void setupToolBar() {
        toolbar = findViewById(R.id.invoice_details_toolbar);
        toolbar.setSubtitle("Número de factura: " + invoice.number);
        toolbar.setNavigationOnClickListener(v -> {
            finish();
        });
    }

    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.invoice_details_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new InvoiceLinesAdapter(invoice.lines, this, invoice));
        if (invoice.lines.isEmpty()) {
            findViewById(R.id.no_invoice_details_textview).setVisibility(View.VISIBLE);
        }
        totalTextView.setText("Total: " + invoice.calculateTotal());
        recyclerView.getAdapter().registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                if (!invoice.lines.isEmpty()) {
                    findViewById(R.id.no_invoice_details_textview).setVisibility(View.INVISIBLE);
                } else {
                    findViewById(R.id.no_invoice_details_textview).setVisibility(View.VISIBLE);
                }
                totalTextView.setText("Total: " + invoice.calculateTotal());
            }
        });
    }

    private void setupButtons() {
        payButton = findViewById(R.id.pay_invoice_button);
        delayButton = findViewById(R.id.delay_invoice_button);

        if (invoice.lines.isEmpty()) {
            payButton.setEnabled(false);
            delayButton.setEnabled(false);
        }

        if (invoice.cancelled) {
            payButton.setVisibility(View.INVISIBLE);
            delayButton.setVisibility(View.INVISIBLE);
        } else {
            if (invoice.paid) {
                payButton.setVisibility(View.INVISIBLE);
                delayButton.setVisibility(View.INVISIBLE);
            } else if (!invoice.equals(AppData.activeInvoice)) {
                delayButton.setVisibility(View.INVISIBLE);
            }
        }

        payButton.setOnClickListener(v -> {
            if (AppData.getUserById(invoice.uid).balance > invoice.total) {
                if (AppData.checkStock(invoice.lines)) {
                    AppData.substractStock(invoice.lines);
                    AppData.getUserById(invoice.uid).balance -= invoice.total;
                    invoice.paid = true;
                    if (invoice.equals(AppData.activeInvoice)) {
                        AppData.invoiceList.add(0, invoice);
                        AppData.createActiveInvoice();
                    }
                    saveChanges();
                } else {
                    Toast.makeText(this,
                            "Alguno de los productos no tiene el stock necesario.",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            } else {
                Toast.makeText(this,
                        "No tienes suficiente dinero para pagar.",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });

        delayButton.setOnClickListener(v -> {
            AppData.invoiceList.add(0, invoice);
            AppData.createActiveInvoice();
            saveChanges();
        });
    }

    private void saveChanges() {
        String uid = FirebaseAuth.getInstance().getUid();
        User user = AppData.getUserById(uid);
        user.invoices = AppData.invoiceList;
        DatabaseReference userReference = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(uid);
        userReference.setValue(user).addOnCompleteListener(task -> {
            InvoicesActivity.activity.notifyRecyclerView();
            finish();
        });
    }
}