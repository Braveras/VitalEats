package com.vitaleats;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class Recetas extends AppCompatActivity {

    ImageButton buttoninicio, buttonalimentos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recetas);

        buttoninicio=findViewById(R.id.buttoninicio);
        buttonalimentos=findViewById(R.id.buttonalimentos);

        buttoninicio.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                paginaSiguiente();
            }
        });
        buttonalimentos.setOnClickListener(new View.OnClickListener() {
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
        Intent intent=new Intent(this, Alimentos.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //no volver para atras
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

}