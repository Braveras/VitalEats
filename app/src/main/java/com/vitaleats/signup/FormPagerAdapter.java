package com.vitaleats.signup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.vitaleats.signup.FragmentForm1;
import com.vitaleats.signup.FragmentForm2;
import com.vitaleats.signup.FragmentForm3;

public class FormPagerAdapter extends FragmentPagerAdapter {
    private int currentPosition = 0;

    public FormPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new FragmentForm1();
            case 1:
                return new FragmentForm2();
            case 2:
                return new FragmentForm3();
            // Add additional cases for each fragment
            default:
                return null;
        }
    }

    public void setCurrentPosition(int position) {
        currentPosition = position;
        notifyDataSetChanged();
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    @Override
    public int getCount() {
        return 3; // Number of fragments
    }
}