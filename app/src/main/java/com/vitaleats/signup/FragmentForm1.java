package com.vitaleats.signup;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.vitaleats.R;
import com.vitaleats.utilities.SharedPrefsUtil;

import java.util.regex.Pattern;

public class FragmentForm1 extends Fragment {
    private TextInputEditText mUsernameEditText;
    private TextInputEditText mEmailEditText;
    private TextInputLayout lUsername, lMail;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_form_1, container, false);

        mUsernameEditText = view.findViewById(R.id.editusername);
        mEmailEditText = view.findViewById(R.id.editmail);
        lUsername = view.findViewById(R.id.til_register_username);
        lMail = view.findViewById(R.id.til_register_mail);

        Button nextButton = view.findViewById(R.id.btn_next_1);
        nextButton.setOnClickListener(view1 -> {
            if (TextUtils.isEmpty(mUsernameEditText.getText()) || TextUtils.isEmpty(mEmailEditText.getText())) {
                if (TextUtils.isEmpty(mUsernameEditText.getText()))
                    lUsername.setError(getString(R.string.emptyFields));
                if (TextUtils.isEmpty(mEmailEditText.getText()))
                    lMail.setError(getString(R.string.emptyFields));
            } else if (!validarEmail(mEmailEditText.getText().toString())) {
                lMail.setError(getString(R.string.invalidEmail));
            } else {
                SharedPrefsUtil.saveString(getContext(), "username", mUsernameEditText.getText().toString());
                SharedPrefsUtil.saveString(getContext(), "email", mEmailEditText.getText().toString());

                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.container, new FragmentForm2());
                transaction.commit();
            }
        });

        return view;
    }

    //Método para validar el email del usuario
    private boolean validarEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }
}