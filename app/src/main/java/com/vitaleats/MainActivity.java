package com.vitaleats;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

public class MainActivity extends AppCompatActivity {

    ImageView logo, fondo;
    TextView slogan, tengoCuenta;
    Button crearCuenta;
    MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        logo = findViewById(R.id.logo);
        fondo = findViewById(R.id.fondo);
        slogan = findViewById(R.id.slogan);
        tengoCuenta = findViewById(R.id.tengoCuenta);
        crearCuenta = findViewById(R.id.crearCuenta);
        mp = MediaPlayer.create(this, R.raw.straw);

        Glide.with(this)
                .load(R.drawable.fondo)
                .transition(DrawableTransitionOptions.withCrossFade(100))
                .centerCrop()
                .into(fondo);

        crearCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, NewAccount.class);
                startActivity(i);

                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            }
        });

        tengoCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mp.start();

                Intent i = new Intent(MainActivity.this, Login.class);
                startActivity(i);

                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            }
        });
    }

    /*private long backPressedTime;

    @Override
    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
            finish();
        } else {
            Toast.makeText(getBaseContext(), "Presiona de nuevo para salir", Toast.LENGTH_SHORT).show();
        }
        backPressedTime = System.currentTimeMillis();
    }*/

    @Override
    public void onBackPressed() {
        //Do not do anything
    }
}