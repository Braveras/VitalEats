package com.vitaleats.login;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.vitaleats.R;

import java.util.regex.Pattern;

public class ResetPassword extends AppCompatActivity {

    private EditText editmail;
    private Button reset;
    private TextInputLayout lMail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        editmail = findViewById(R.id.resetEditMail);
        reset = findViewById(R.id.reset);
        lMail = findViewById(R.id.lResetMail);

        // Manejamos el envío de correo de recuperación
        reset.setOnClickListener(view -> {

            if (editmail.getText().toString().isEmpty()) {
                lMail.setError(getString(R.string.emptyFields));
            } else if (!validarEmail(editmail.getText().toString())) {
                lMail.setError(getString(R.string.invalidEmail));
            } else {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.sendPasswordResetEmail(editmail.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Intent i = new Intent(ResetPassword.this, ResetPassword.class);
                                    startActivity(i);

                                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

                                    Toast.makeText(ResetPassword.this, getString(R.string.sentEmail), Toast.LENGTH_LONG).show();
                                    limpiar();
                                } else {
                                    try {
                                        throw task.getException();
                                    } catch (FirebaseAuthInvalidUserException e) {
                                        Toast.makeText(ResetPassword.this, getString(R.string.mailNotFound), Toast.LENGTH_LONG).show();
                                    } catch (Exception e) {
                                        Log.e(TAG, e.toString());
                                        Toast.makeText(ResetPassword.this, "Error!", Toast.LENGTH_LONG).show();
                                    }
                                }
                            }
                        });
            }
        });
    }

    private void limpiar() {
        reset.setText("");
    }//Clear email entered

    private boolean validarEmail(String email) {//Validate email entered
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }

}