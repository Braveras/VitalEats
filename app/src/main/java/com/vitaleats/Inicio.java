package com.vitaleats;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class Inicio extends AppCompatActivity {

    ImageButton buttonalimentos, buttonrecetas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        buttonalimentos=findViewById(R.id.buttonalimentos);

        buttonalimentos.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                paginaSiguiente();
            }
        });
    }

    public void paginaSiguiente() {
        Intent intent=new Intent(this, Alimentos.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //no volver para atras
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}