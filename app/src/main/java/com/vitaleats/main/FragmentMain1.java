package com.vitaleats.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;

import com.vitaleats.R;
import com.vitaleats.login.MainActivity;

public class FragmentMain1 extends Fragment {

    private ImageButton newRecipe_btn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main1, container, false);
        newRecipe_btn = view.findViewById(R.id.new_recipe);

        newRecipe_btn.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), newRecipe.class);
            startActivity(intent);
        });




        return view;


    }
}