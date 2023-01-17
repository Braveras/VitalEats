package com.vitaleats;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.regex.Pattern;

public class ResetPassword extends AppCompatActivity {

    EditText editmail;
    Button reset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        editmail = findViewById(R.id.editmail);
        reset = findViewById(R.id.reset);

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (editmail.getText().toString().isEmpty()) {
                    editmail.setError("DEBE COMPLETAR LOS CAMPOS OBLIGATORIOS");

                } else if (!validarEmail(editmail.getText().toString())) {
                    editmail.setError("INTRODUCE UN CORREO VÁLIDO");

                } else {
                    Intent i = new Intent(ResetPassword.this, ResetPassword.class);
                    startActivity(i);

                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    Toast.makeText(ResetPassword.this, "CORREO ENVIADO", Toast.LENGTH_LONG).show();
                    limpiar();
                }
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