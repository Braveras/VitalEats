package com.vitaleats;

import static android.app.PendingIntent.getActivity;
import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Pattern;

public class Login extends AppCompatActivity {

    TextView olvidado;
    Button button;
    EditText editmail, editpass;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        olvidado = findViewById(R.id.registerTextView);
        button = findViewById(R.id.button);
        editmail = findViewById(R.id.editmail);
        editpass = findViewById(R.id.editpass);
        mAuth = FirebaseAuth.getInstance();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!editmail.getText().toString().isEmpty() && !editpass.getText().toString().isEmpty() && validarEmail(editmail.getText().toString())) {

                    mAuth.signInWithEmailAndPassword(editmail.getText().toString(), editpass.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // success
                                        Toast.makeText(Login.this, "INICIO DE SESIÓN CORRECTO", Toast.LENGTH_LONG).show();
                                        limpiar();
                                    } else {
                                        Toast.makeText(Login.this, "USUARIO NO REGISTRADO",
                                                Toast.LENGTH_LONG).show();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // exception
                                }
                            });

                } else if (editmail.getText().toString().isEmpty() && editpass.getText().toString().isEmpty()) {
                    editmail.setError("DEBE COMPLETAR LOS CAMPOS OBLIGATORIOS");
                    editpass.setError("DEBE COMPLETAR LOS CAMPOS OBLIGATORIOS");

                } else if (editmail.getText().toString().isEmpty()) {
                    editmail.setError("DEBE COMPLETAR LOS CAMPOS OBLIGATORIOS");

                } else if (!validarEmail(editmail.getText().toString())) {
                    editmail.setError("INTRODUCE UN CORREO VÁLIDO");

                } else if (editpass.getText().toString().isEmpty()) {
                    editpass.setError("DEBE COMPLETAR LOS CAMPOS OBLIGATORIOS");

                } else {
                    Toast.makeText(Login.this, "ERROR AL INICIAR SESIÓN", Toast.LENGTH_LONG).show();
                }
            }
        });

        olvidado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Login.this, ResetPassword.class);
                startActivity(i);

                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            }
        });
    }

    private void limpiar() {//Clear email entered
        editmail.setText("");
        editpass.setText("");
    }

    private boolean validarEmail(String email) {//Validate email entered
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            currentUser.reload();
        }
    }

}