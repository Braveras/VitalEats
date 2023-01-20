package com.vitaleats;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class Alimentos extends AppCompatActivity {

    ImageButton buttoninicio, buttonrecetas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alimentos);

        buttoninicio=findViewById(R.id.buttoninicio);
        buttonrecetas=findViewById(R.id.buttonrecetas);

        buttoninicio.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                paginaSiguiente();
            }
        });
        buttonrecetas.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                paginaSiguiente2();
            }
        });
    }

    public void paginaSiguiente() {
        Intent intent=new Intent(this, Inicio.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //no volver para atras
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
    public void paginaSiguiente2() {
        Intent intent=new Intent(this, Recetas.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //no volver para atras
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

}