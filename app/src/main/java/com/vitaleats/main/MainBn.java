package com.vitaleats.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.vitaleats.R;
import com.vitaleats.login.MainActivity;

public class MainBn extends AppCompatActivity {

    //    private ActivityMainBinding binding;
    private MenuItem prevMenuItem;
    private SectionsPagerAdapter sectionsPagerAdapter;
    Button cerrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainbn);
        cerrar = findViewById(R.id.cerrar);
//        binding = ActivityMainBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());

        //el adaptador coloca las Pages -los fragmentos con las diferentes vistas- dentro de la vista padre Viewpager del xml
        sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
//        ViewPager viewPager = binding.viewPager;
        ViewPager viewPager1 = findViewById(R.id.view_pager);
        viewPager1.setAdapter(sectionsPagerAdapter);


        BottomNavigationView mybottomNavView = findViewById(R.id.bottom_navigation);

        // crear badges
        BottomNavigationMenuView bottomNavigationMenuView =
                (BottomNavigationMenuView) mybottomNavView.getChildAt(0);
        View v = bottomNavigationMenuView.getChildAt(2);
        BottomNavigationItemView itemView = (BottomNavigationItemView) v;


        mybottomNavView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.inicio:
                        item.setChecked(true);
                        removeBadge(mybottomNavView, item.getItemId());
                        viewPager1.setCurrentItem(0);
                        break;

                    case R.id.alimentos:
                        item.setChecked(true);
                        removeBadge(mybottomNavView, item.getItemId());
                        viewPager1.setCurrentItem(1);
                        break;

                    case R.id.recetas:
                        item.setChecked(true);
                        removeBadge(mybottomNavView, item.getItemId());
                        viewPager1.setCurrentItem(2);
                        break;
                }
                return false;
            }
        });

        viewPager1.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (prevMenuItem != null)
                    prevMenuItem.setChecked(false);

                else
                    mybottomNavView.getMenu().getItem(0).setChecked(false);
                mybottomNavView.getMenu().getItem(position).setChecked(true);
                removeBadge(mybottomNavView, mybottomNavView.getMenu().getItem(position).getItemId());
                prevMenuItem = mybottomNavView.getMenu().getItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        cerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(mainIntent);
                finish();
            }
        });

    }

    public static void removeBadge(BottomNavigationView bottomNavigationView, @IdRes int itemId) {
        BottomNavigationItemView itemView = bottomNavigationView.findViewById(itemId);
        if (itemView.getChildCount() == 3) {
            itemView.removeViewAt(2);
        }
    }

    @Override
    public void onBackPressed() {
        //Do not do anything
    }

}
