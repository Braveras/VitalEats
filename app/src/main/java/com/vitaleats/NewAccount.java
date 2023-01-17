package com.vitaleats;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

public class NewAccount extends AppCompatActivity {


    private FormPagerAdapter adapter;
    FragmentForm1 formFragment1;
    FragmentForm2 formFragment2;
    FragmentForm3 formFragment3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_account);

        //Handle the fragments sliding
        NonSweepViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(new FormPagerAdapter(getSupportFragmentManager()));
        viewPager.setOffscreenPageLimit(3);
        viewPager.setCurrentItem(0);

        Button next = findViewById(R.id.btn_next);
        next.setOnClickListener(view -> {
            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
        });
    }

}