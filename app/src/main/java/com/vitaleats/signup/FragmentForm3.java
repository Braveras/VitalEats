package com.vitaleats.signup;

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
import com.vitaleats.utilities.SharedPrefsUtil;

public class FragmentForm3 extends Fragment {
    private TextInputEditText mPasswordEditText;
    private TextInputEditText mConfirmPasswordEditText;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_form_3, container, false);

        mPasswordEditText = view.findViewById(R.id.editPassword);
        mConfirmPasswordEditText = view.findViewById(R.id.editRepeatPassword);

        Button submitButton = view.findViewById(R.id.btn_submit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPasswordEditText.getText().toString().isEmpty() || mConfirmPasswordEditText.getText().toString().isEmpty()) {
                    if (mPasswordEditText.getText().toString().isEmpty())
                        mPasswordEditText.setError("*");
                    if (mConfirmPasswordEditText.getText().toString().isEmpty())
                        mConfirmPasswordEditText.setError("*");
                    Toast.makeText(getContext(), getString(R.string.emptyFields), Toast.LENGTH_SHORT).show();
                } else if (!mPasswordEditText.getText().toString().equals(mConfirmPasswordEditText.getText().toString())) {
                    mConfirmPasswordEditText.setError(getString(R.string.passwordsDoNotMatch));
                } else if (!isPasswordValid(mPasswordEditText.getText().toString())) {
                    mConfirmPasswordEditText.setError(getString(R.string.passwordSecurity));
                } else {
                    SharedPrefsUtil.saveString(getContext(), "password", mPasswordEditText.getText().toString());

                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.container, new FragmentForm4());
                    transaction.commit();
                }
            }
        });

        return view;
    }

    private boolean isPasswordValid(String password) {
        // Comprobar la longitud de la contraseña
        if (password.length() < 7) {
            return false;
        }

        // Comprobar si contiene al menos 1 mayúscula y 2 números
        int upperCase = 0, digits = 0;
        for (int i = 0; i < password.length(); i++) {
            char c = password.charAt(i);
            if (Character.isUpperCase(c)) {
                upperCase++;
            } else if (Character.isDigit(c)) {
                digits++;
            }
        }
        if (upperCase < 1 || digits < 2) {
            return false;
        }

        // La contraseña cumple los requisitos de seguridad
        return true;
    }

}