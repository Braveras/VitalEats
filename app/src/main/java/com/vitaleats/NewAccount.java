package com.vitaleats;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

public class NewAccount extends AppCompatActivity {

    private FormPagerAdapter adapter;
    FragmentForm1 formFragment1;
    FragmentForm2 formFragment2;
    FragmentForm3 formFragment3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_account);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.container, new FragmentForm1());
        transaction.commit();
    }

}