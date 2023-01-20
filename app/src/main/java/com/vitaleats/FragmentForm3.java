package com.vitaleats;
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
                    Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                } else if(!mPasswordEditText.getText().toString().equals(mConfirmPasswordEditText.getText().toString())) {
                    mConfirmPasswordEditText.setError("Passwords do not match");
                } else {
                    String encryptedPassword = null;
                    try {
                        encryptedPassword = encrypt(mPasswordEditText.getText().toString());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("password", encryptedPassword);
                    editor.apply();

                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.container, new FragmentForm4());
                    transaction.commit();
                }
            }
        });

        return view;
    }

    public String encrypt(String password) throws Exception {
        SecretKeySpec key = new SecretKeySpec(KEY.getBytes(), AES);
        Cipher c = Cipher.getInstance(AES);
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encVal = c.doFinal(password.getBytes());
        return Base64.encodeToString(encVal, Base64.DEFAULT);
    }
}