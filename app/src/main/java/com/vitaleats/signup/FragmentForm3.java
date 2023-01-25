package com.vitaleats.signup;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
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

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class FragmentForm3 extends Fragment {
    private TextInputEditText mPasswordEditText;
    private TextInputEditText mConfirmPasswordEditText;

    private final String AES = "AES";
    private final String KEY = "Xp2s5v8y/B?E(G+KbPeShVmYq3t6w9z$";

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

}