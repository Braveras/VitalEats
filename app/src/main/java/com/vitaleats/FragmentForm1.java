package com.vitaleats;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
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

import java.util.regex.Pattern;

public class FragmentForm1 extends Fragment {
    private TextInputEditText mUsernameEditText;
    private TextInputEditText mEmailEditText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_form_1, container, false);

        mUsernameEditText = view.findViewById(R.id.editusername);
        mEmailEditText = view.findViewById(R.id.editmail);

        Button nextButton = view.findViewById(R.id.btn_next_1);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(mUsernameEditText.getText()) || TextUtils.isEmpty(mEmailEditText.getText())) {
                    if (TextUtils.isEmpty(mUsernameEditText.getText()))
                        mUsernameEditText.setError("*");
                    if (TextUtils.isEmpty(mEmailEditText.getText()))
                        mEmailEditText.setError("*");
                    Toast.makeText(getContext(), "Por favor rellena todos los campos", Toast.LENGTH_SHORT).show();
                } else if (!validarEmail(mEmailEditText.getText().toString())) {
                    mEmailEditText.setError("INTRODUCE UN CORREO VÁLIDO");
                } else {
                    SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("username", mUsernameEditText.getText().toString());
                    editor.putString("email", mEmailEditText.getText().toString());
                    editor.apply();

                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.container, new FragmentForm2());
                    transaction.commit();
                }
            }
        });

        return view;
    }

    private boolean validarEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }
}