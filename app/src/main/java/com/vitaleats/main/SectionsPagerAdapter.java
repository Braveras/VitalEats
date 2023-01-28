package com.vitaleats.main;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.vitaleats.main.FragmentMain1;
import com.vitaleats.main.FragmentMain2;
import com.vitaleats.main.FragmentMain3;
import com.vitaleats.main.MainBn;

public class SectionsPagerAdapter extends FragmentPagerAdapter {
    private int currentPosition = 0;

    public SectionsPagerAdapter(MainBn mainBn, FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new FragmentMain1();
            case 1:
                return new FragmentMain2();
            case 2:
                return new FragmentMain3();
            case 3:
                return new FragmentMain4();
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
        return 4; // Number of fragments
    }
}