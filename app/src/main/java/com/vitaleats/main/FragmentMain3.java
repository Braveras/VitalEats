package com.vitaleats.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.vitaleats.R;

public class FragmentMain3 extends Fragment {
    private Button add_recipe_button;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main3, container, false);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        add_recipe_button = view.findViewById(R.id.add_recipe_button);

        add_recipe_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(getActivity(), AddRecipeActivity.class);
                //startActivity(intent);
            }
        });

        return view;
    }
}
