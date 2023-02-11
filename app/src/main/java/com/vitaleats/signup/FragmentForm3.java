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
import com.google.android.material.textfield.TextInputLayout;
import com.vitaleats.R;
import com.vitaleats.utilities.SharedPrefsUtil;

public class FragmentForm3 extends Fragment {
    private TextInputEditText mPasswordEditText, mConfirmPasswordEditText;
    private TextInputLayout lPasswd, lConfirmPasswd;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_form_3, container, false);

        mPasswordEditText = view.findViewById(R.id.editPassword);
        mConfirmPasswordEditText = view.findViewById(R.id.editRepeatPassword);
        lPasswd = view.findViewById(R.id.newPassword);
        lConfirmPasswd = view.findViewById(R.id.newRepeatPassword);

        Button submitButton = view.findViewById(R.id.btn_submit);
        submitButton.setOnClickListener(view1 -> {
            lPasswd.setError(null);
            lConfirmPasswd.setError(null);
            if (mPasswordEditText.getText().toString().isEmpty() || mConfirmPasswordEditText.getText().toString().isEmpty()) {
                if (mPasswordEditText.getText().toString().isEmpty())
                    lPasswd.setError(getString(R.string.emptyFields2));
                if (mConfirmPasswordEditText.getText().toString().isEmpty())
                    lConfirmPasswd.setError(getString(R.string.emptyFields2));
            } else if (!mPasswordEditText.getText().toString().equals(mConfirmPasswordEditText.getText().toString())) {
                lPasswd.setError(" ");
                lConfirmPasswd.setError(getString(R.string.passwordsDoNotMatch));
            } else if (!isPasswordValid(mPasswordEditText.getText().toString())) {
                String error = getPasswordError(mPasswordEditText.getText().toString());
                lPasswd.setError(" ");
                lConfirmPasswd.setError(error);
            } else {
                SharedPrefsUtil.saveString(requireContext(), "password", mPasswordEditText.getText().toString());
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.container, new FragmentForm4());
                transaction.commit();
            }
        });

        return view;
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= 6 && password.matches(".*[A-Z].*[0-9].*[!@#$%^&*+=?-_].*");
    }

    private String getPasswordError(String password) {
        String SPECIAL_CHARS = "!@#$%^&*+=_\\-[]{}|;:'\",.<>/?";
        String to_append = "";
        StringBuilder errorString = new StringBuilder();
        if (password.length() < 6) {
            to_append += getString(R.string.error_password_length) + "\n";
        }
        int digits = 0, uppercase = 0, special = 0;
        for (int i = 0; i < password.length(); i++) {
            char c = password.charAt(i);
            if (Character.isDigit(c)) {
                digits++;
            } else if (Character.isUpperCase(c)) {
                uppercase++;
            } else if (SPECIAL_CHARS.indexOf(c) >= 0) {
                special++;
            }
        }
        if (digits < 1) {
            to_append += getString(R.string.error_password_digit) + "\n";
        }
        if (uppercase < 1) {
            to_append += getString(R.string.error_password_uppercase) + "\n";
        }
        if (special < 1) {
            to_append += getString(R.string.error_password_special);
        }
        if (to_append.length() > 0) {
            errorString.append(getString(R.string.error_password_header) + "\n");
            errorString.append(to_append.trim());
            return errorString.toString();
        }
        return null;
    }

}