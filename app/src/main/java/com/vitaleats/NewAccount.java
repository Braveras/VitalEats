package com.vitaleats;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

public class NewAccount extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_account);

        //Handle the fragments sliding
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(new FormPagerAdapter(getSupportFragmentManager()));

    }

}