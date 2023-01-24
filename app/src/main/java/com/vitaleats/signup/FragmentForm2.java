package com.vitaleats.signup;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.textfield.TextInputEditText;
import com.vitaleats.R;

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
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mHeightEditText.getText().toString().isEmpty() || mWeightEditText.getText().toString().isEmpty() || mAgeEditText.getText().toString().isEmpty()) {
                    if (mHeightEditText.getText().toString().isEmpty())
                        mHeightEditText.setError("*");
                    if (mWeightEditText.getText().toString().isEmpty())
                        mWeightEditText.setError("*");
                    if (mAgeEditText.getText().toString().isEmpty())
                        mAgeEditText.setError("*");
                    Toast.makeText(getContext(), "Por favor rellena todos los campos", Toast.LENGTH_SHORT).show();
                } else {
                    SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("height", mHeightEditText.getText().toString());
                    editor.putString("weight", mWeightEditText.getText().toString());
                    editor.putString("age", mAgeEditText.getText().toString());
                    editor.apply();

                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.container, new FragmentForm3());
                    transaction.commit();
                }
            }
        });

        return view;
    }
}