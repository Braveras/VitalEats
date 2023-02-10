package com.vitaleats.login;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.vitaleats.R;
import com.vitaleats.main.MainBn;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class Login extends AppCompatActivity {

    TextView forgottenPassword_btn;
    Button mailPassworAccess_btn, googleAccess_btn, guestButton;
    EditText editmail, editpass;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.vitaleats.R.layout.activity_login);

        Toolbar toolbar = findViewById(com.vitaleats.R.id.toolbar);
        setSupportActionBar(toolbar);
        forgottenPassword_btn = findViewById(com.vitaleats.R.id.forgottenPassword);
        mailPassworAccess_btn = findViewById(com.vitaleats.R.id.mailPasswordAccess_button);
        editmail = findViewById(com.vitaleats.R.id.editmail);
        editpass = findViewById(com.vitaleats.R.id.editpass);
        mAuth = FirebaseAuth.getInstance();

        mailPassworAccess_btn.setOnClickListener(view -> {

            if (!editmail.getText().toString().isEmpty() && !editpass.getText().toString().isEmpty() && validarEmail(editmail.getText().toString())) {
                mAuth.signInWithEmailAndPassword(editmail.getText().toString(), editpass.getText().toString())
                        .addOnCompleteListener(task -> {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user.isEmailVerified()) {
                                // Correo electrónico verificado
                                if (task.isSuccessful()) {
                                    Toast.makeText(Login.this, getString(R.string.loginSuccess), Toast.LENGTH_LONG).show();
                                    limpiar();
                                    updateUI(user);
                                } else {
                                    try {
                                        throw task.getException();
                                    } catch (FirebaseAuthInvalidCredentialsException e) {
                                        Toast.makeText(Login.this, getString(R.string.wrongPassword), Toast.LENGTH_LONG).show();
                                    } catch (FirebaseAuthInvalidUserException e) {
                                        Toast.makeText(Login.this, getString(R.string.userNotFound), Toast.LENGTH_LONG).show();
                                    } catch (Exception e) {
                                        Toast.makeText(Login.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                    updateUI(null);
                                }
                            } else {
                                Toast.makeText(Login.this, getString(R.string.mailNotVerified), Toast.LENGTH_LONG).show();
                            }
                        }).addOnFailureListener(e -> {
                            // exception
                            Toast.makeText(Login.this, "Error al iniciar sesión", Toast.LENGTH_LONG).show();
                        });

            } else if (editmail.getText().toString().isEmpty() || editpass.getText().toString().isEmpty()) {
                if (editmail.getText().toString().isEmpty())
                    editmail.setError(getString(R.string.emptyFields));
                if (editpass.getText().toString().isEmpty())
                    editpass.setError(getString(R.string.emptyFields));
            } else if (!validarEmail(editmail.getText().toString())) {
                editmail.setError(getString(R.string.invalidEmail));
            } else {
                Toast.makeText(Login.this, getString(R.string.loginError), Toast.LENGTH_LONG).show();
            }
        });

        forgottenPassword_btn.setOnClickListener(view -> {
            Intent i = new Intent(Login.this, ResetPassword.class);
            startActivity(i);

            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        });

    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Intent i = new Intent(Login.this, MainBn.class);
            startActivity(i);
        }
    }

    private boolean validarEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }

    private void limpiar() {
        editmail.setText("");
        editpass.setText("");
    }

}