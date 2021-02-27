package com.example.mercadomunicipalcliente;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mercadomunicipalcliente.models.User;
import com.example.mercadomunicipalcliente.stores.StoresActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.atomic.AtomicReference;

public class LoginActivity extends AppCompatActivity {
    final int RC_SIGN_IN = 5;
    final String TAG = "LoginActivity";
    GoogleSignInOptions gSignInOptions;
    GoogleSignInClient gSignInClient;
    FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Si el usuario está logeado, procede a la siguiente actividad.
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            Intent nextActivityIntent = new Intent(getApplicationContext(), StoresActivity.class);
            startActivity(nextActivityIntent);
            finish();
        }
        setContentView(R.layout.activity_login);


        gSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        final EditText emailSign = findViewById(R.id.editTextEmailAddressLogin);
        final EditText passwordSign = findViewById(R.id.editTextPasswordLogin);
        final TextView create = findViewById((R.id.textViewRegister));
        Button login = findViewById(R.id.buttonLoginEmail);
        TextView google = findViewById(R.id.textViewLoginGoogle);

        login.setOnClickListener(v -> {
            if (emailSign.getText().toString().isEmpty() || passwordSign.getText().toString().isEmpty()) {
                Toast empty = Toast.makeText(getApplicationContext(), "Has dejado uno de los campo en blanco", Toast.LENGTH_SHORT);
                empty.show();
            } else {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(emailSign.getText().toString(), passwordSign.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast correct = Toast.makeText(getApplicationContext(), "Inicio de sesión satisfactorio", Toast.LENGTH_SHORT);
                            correct.show();
                            AuthNullUser();
                        } else {
                            Toast incorrect = Toast.makeText(getApplicationContext(), "No se pudo iniciar sesion, revisa los campos", Toast.LENGTH_SHORT);
                            incorrect.show();
                        }
                    }
                });
            }
        });

        google.setOnClickListener(v -> {
            gSignInClient = GoogleSignIn.getClient(this, gSignInOptions);
            startActivityForResult(gSignInClient.getSignInIntent(), RC_SIGN_IN);
        });



        create.setOnClickListener(v -> {
            Intent nextActivityIntent = new Intent(getApplicationContext(), RegisterEmailActivity.class);
            startActivity(nextActivityIntent);
            finish();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                GoogleSignInAccount account = (GoogleSignInAccount) task.getResult(ApiException.class);
                assert account != null;
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
                Toast.makeText(
                        this,
                        "Se ha iniciado sesión satisfactoriamente.",
                        Toast.LENGTH_SHORT
                ).show();
                Thread.sleep(3000);
                AuthNullUser();

            } catch (ApiException e) {
                Log.w(TAG, "Google sign in failed", e);
                Toast.makeText(
                        this,
                        "Error al iniciar sesión",
                        Toast.LENGTH_SHORT
                ).show();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "signInWithCredential:success");
            } else {
                Log.w(TAG, "signInWithCredential:failure", task.getException());
            }
        });
    }

    //Si no existe usuario con los datos (saldo nombre e imagen) lo envia a la activity para que lo cree
    private void AuthNullUser() {
        DatabaseReference usersReference = FirebaseDatabase.getInstance().getReference("users");
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        usersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                AtomicReference<Boolean> exist = new AtomicReference<>(false);
                snapshot.getChildren().forEach(child -> {
                    if (child.getValue(User.class).userID.equals(auth.getCurrentUser().getUid())) {
                        exist.set(true);

                    }
                });
                if (exist.get()) {
                    Intent nextActivityIntent = new Intent(getApplicationContext(), StoresActivity.class);
                    startActivity(nextActivityIntent);
                    finish();
                } else {
                    Intent nextActivityIntent = new Intent(getApplicationContext(), RegisterDataActivity.class).putExtra("email", currentUser.getEmail());
                    startActivity(nextActivityIntent);
                    finish();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}