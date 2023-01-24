package com.vitaleats.signup;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.vitaleats.R;
import com.vitaleats.login.Login;
import com.vitaleats.signup.FragmentForm1;

public class FragmentForm4 extends Fragment {

    private TextView mUsernameTextView, mAgeTextView, mEmailTextView, mHeightTextView, mWeightTextView;
    private Button mAcceptButton, mCancelButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_form_4, container, false);

        mUsernameTextView = view.findViewById(R.id.username_text_view);
        mEmailTextView = view.findViewById(R.id.email_text_view);
        mHeightTextView = view.findViewById(R.id.height_text_view);
        mWeightTextView = view.findViewById(R.id.weight_text_view);
        mAgeTextView = view.findViewById(R.id.age_text_view);
        mAcceptButton = view.findViewById(R.id.accept_button);
        mCancelButton = view.findViewById(R.id.cancel_button);

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
            public void onClick(View view) {
                SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                String email = sharedPref.getString("email", "");
                String password = sharedPref.getString("password", "");

                createUser(email, password);
            }
        });

        return view;
    }

    private void createUser(String email, String password) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Usuario creado con éxito", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(getContext(), Login.class);
                            startActivity(i);

                        } else {
                            Toast.makeText(getContext(), "Error al crear usuario", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}