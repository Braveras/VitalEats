package com.vitaleats;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

public class Login extends AppCompatActivity {

    TextView olvidado;
    Button button;
    EditText editmail, editpass;

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

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!editmail.getText().toString().isEmpty() && !editpass.getText().toString().isEmpty() && validarEmail(editmail.getText().toString())) {

                    Toast.makeText(Login.this, "INICIO DE SESIÓN CORRECTO", Toast.LENGTH_LONG).show();
                    limpiar();

                    /*FirebaseAuth.getInstance().signInWithEmailAndPassword(editmail.getText().toString(),
                            editpass.getText().toString());*/

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

}