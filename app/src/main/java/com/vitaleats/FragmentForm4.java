package com.vitaleats;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;

public class FragmentForm4 extends Fragment {

    private TextView mUsernameTextView;
    private TextView mEmailTextView;
    private TextView mHeightTextView;
    private TextView mWeightTextView;
    private TextView mAgeTextView;
    private Button mCancelButton;
    private Button mAcceptButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_form_4, container, false);

        mUsernameTextView = view.findViewById(R.id.username_text_view);
        mEmailTextView = view.findViewById(R.id.email_text_view);
        mHeightTextView = view.findViewById(R.id.height_text_view);
        mWeightTextView = view.findViewById(R.id.weight_text_view);
        mAgeTextView = view.findViewById(R.id.age_text_view);
        mCancelButton = view.findViewById(R.id.cancel_button);
        mAcceptButton = view.findViewById(R.id.accept_button);

        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        mUsernameTextView.setText(sharedPref.getString("username", ""));
        mEmailTextView.setText(sharedPref.getString("email", ""));
        mHeightTextView.setText(sharedPref.getString("height", ""));
        mWeightTextView.setText(sharedPref.getString("weight", ""));
        mAgeTextView.setText(sharedPref.getString("age", ""));

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().replace(R.id.container, new FragmentForm1()).commit();
            }
        });

        mAcceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(getContext(), AnotherActivity.class);
                //startActivity(intent);
            }
        });

        return view;
    }
}