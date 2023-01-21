package com.vitaleats;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

public class Fragment3 extends AppCompatActivity {

    Button aceptar;
    CheckBox checkBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_3);

        aceptar = findViewById(R.id.aceptar);
        checkBox = findViewById(R.id.checkBox);

        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (checkBox.isChecked()) {
                    Intent i = new Intent(Fragment3.this, MainActivity.class);
                    startActivity(i);

                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                } else {
                    Toast.makeText(Fragment3.this, "DEBES ACEPTAR NUESTROS TÉRMINOS Y CONDICIONES", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        //Do not do anything
    }

}