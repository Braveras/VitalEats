package com.vitaleats.signup;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.vitaleats.R;
import com.vitaleats.utilities.SharedPrefsUtil;

public class FragmentForm3 extends Fragment {
    private TextInputEditText mPasswordEditText, mConfirmPasswordEditText;
    private TextInputLayout lPasswd, lConfirmPasswd;
    private ImageButton inforBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_form_3, container, false);

        mPasswordEditText = view.findViewById(R.id.editPassword);
        mConfirmPasswordEditText = view.findViewById(R.id.editRepeatPassword);
        lPasswd = view.findViewById(R.id.newPassword);
        lConfirmPasswd = view.findViewById(R.id.newRepeatPassword);
        inforBtn = view.findViewById(R.id.password_infobutton);

        Button submitButton = view.findViewById(R.id.btn_submit);
        submitButton.setOnClickListener(view1 -> {
            lPasswd.setError(null);
            lConfirmPasswd.setError(null);
            if (mPasswordEditText.getText().toString().isEmpty() || mConfirmPasswordEditText.getText().toString().isEmpty()) {
                if (mPasswordEditText.getText().toString().isEmpty())
                    lPasswd.setError(getString(R.string.emptyFields));
                if (mConfirmPasswordEditText.getText().toString().isEmpty())
                    lConfirmPasswd.setError(getString(R.string.emptyFields));
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

        inforBtn.setOnClickListener(view12 -> {
            View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet, null);
            BottomSheetDialog dialog = new BottomSheetDialog(getActivity());
            dialog.setContentView(bottomSheetView);
            dialog.show();
        });

        return view;
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= 6 && password.matches(".*[A-Z].*[0-9].*[!@#$%^&*+=_\\-\\[\\]{}|;:'\",.<>/?].*");
    }

    private String getPasswordError(String password) {
        StringBuilder errorString = new StringBuilder();
        if (password.length() < 6) {
            errorString.append(getString(R.string.error_password_length) + "\n");
        }
        boolean hasDigit = false, hasUppercase = false, hasSpecial = false;
        for (int i = 0; i < password.length(); i++) {
            char c = password.charAt(i);
            if (Character.isDigit(c)) {
                hasDigit = true;
            } else if (Character.isUpperCase(c)) {
                hasUppercase = true;
            } else if ("!@#$%^&*+=_-[]{}|;:'\",.<>/?".indexOf(c) >= 0) {
                hasSpecial = true;
            }
        }
        if (!hasDigit) {
            errorString.append(getString(R.string.error_password_digit) + "\n");
        }
        if (!hasUppercase) {
            errorString.append(getString(R.string.error_password_uppercase) + "\n");
        }
        if (!hasSpecial) {
            errorString.append(getString(R.string.error_password_special));
        }
        if (errorString.length() > 0) {
            errorString.insert(0, getString(R.string.error_password_header) + "\n");
            return errorString.toString();
        }
        return null;
    }

}