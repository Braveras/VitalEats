package com.vitaleats;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

public class NewAccount extends AppCompatActivity {

    Button hombre, mujer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_account);

        //Handle the fragments sliding
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(new FormPagerAdapter(getSupportFragmentManager()));

    }

}