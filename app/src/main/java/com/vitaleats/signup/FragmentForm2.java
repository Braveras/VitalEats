package com.vitaleats.signup;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.textfield.TextInputEditText;
import com.vitaleats.R;
import com.vitaleats.utilities.SharedPrefsUtil;

public class FragmentForm2 extends Fragment {
    private TextInputEditText mHeightEditText;
    private TextInputEditText mWeightEditText;
    private TextInputEditText mAgeEditText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_form_2, container, false);

        mHeightEditText = view.findViewById(R.id.editAltura);
        mWeightEditText = view.findViewById(R.id.editPeso);
        mAgeEditText = view.findViewById(R.id.editEdad);

        Button nextButton = view.findViewById(R.id.btn_next_2);
        nextButton.setOnClickListener(view1 -> {
            SharedPrefsUtil.saveString(getContext(), "height", mHeightEditText.getText().toString());
            SharedPrefsUtil.saveString(getContext(), "weight", mWeightEditText.getText().toString());
            SharedPrefsUtil.saveString(getContext(), "age", mAgeEditText.getText().toString());

            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.container, new FragmentForm3());
            transaction.commit();
        });

        return view;
    }
}