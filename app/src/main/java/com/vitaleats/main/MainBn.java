package com.vitaleats.main;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.vitaleats.R;

public class MainBn extends AppCompatActivity {

    private MenuItem prevMenuItem;
    private SectionsPagerAdapter sectionsPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainbn);

        // Configuramos adapter para manejar el bottom nav menu
        sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Lo manejamos
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.inicio:
                    viewPager.setCurrentItem(0);
                    break;
                case R.id.alimentos:
                    viewPager.setCurrentItem(1);
                    break;
                case R.id.recetas:
                    viewPager.setCurrentItem(2);
                    break;
                case R.id.perfil:
                    viewPager.setCurrentItem(3);
                    break;
            }
            return true;
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (prevMenuItem != null)
                    prevMenuItem.setChecked(false);
                else
                    bottomNavigationView.getMenu().getItem(0).setChecked(false);
                bottomNavigationView.getMenu().getItem(position).setChecked(true);
                prevMenuItem = bottomNavigationView.getMenu().getItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    public void onBackPressed() {
        //Do not do anything
    }
}
