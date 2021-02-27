package com.example.mercadomunicipalcliente;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterEmailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_email);

        //Event Analytics
        final FirebaseAnalytics analytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString("message", "Full Firebase integration");
        analytics.logEvent("InitScreen", bundle);

        TextView accept = findViewById(R.id.textViewNext);
        TextView cancel = findViewById(R.id.textViewCancel);
        final EditText email = findViewById(R.id.editTextEmailAddressRegister);
        final EditText password = findViewById(R.id.editTextPasswordRegister);

        accept.setOnClickListener(v -> {
            if (email.getText().toString().isEmpty() || password.getText().toString().isEmpty()) {
                Toast empty = Toast.makeText(getApplicationContext(), "Dejaste algun campo en blanco", Toast.LENGTH_SHORT);
                empty.show();
            } else {
                final FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast correct = Toast.makeText(getApplicationContext(), "El usuario se creó satisfactoriamente", Toast.LENGTH_SHORT);
                            correct.show();
                            FirebaseAuth.getInstance().signInWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Intent nextActivityIntent = new Intent(getApplicationContext(), RegisterDataActivity.class).putExtra("email", email.getText().toString());
                                        startActivity(nextActivityIntent);
                                        finish();
                                    } else {
                                        Toast incorrect = Toast.makeText(getApplicationContext(), "No se pudo iniciar sesion wtf", Toast.LENGTH_SHORT);
                                        incorrect.show();
                                    }
                                }
                            });
                        } else {
                            Toast incorrect = Toast.makeText(getApplicationContext(), "El usuario no se pudo crear, revise que la contraseña sea mas de 5 dígitos y el correo este bien escrito", Toast.LENGTH_SHORT);
                            incorrect.show();
                        }
                    }
                });
            }
        });
        cancel.setOnClickListener(v -> {
            Intent nextActivityIntent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(nextActivityIntent);
            finish();
        });

    }
}