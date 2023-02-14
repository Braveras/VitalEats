package com.vitaleats.signup;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.vitaleats.R;
import com.vitaleats.login.Login;
import com.vitaleats.utilities.SharedPrefsUtil;
import com.vitaleats.utilities.StorageUtil;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

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


        mUsernameTextView.setText(SharedPrefsUtil.getString(getContext(), "username"));
        mEmailTextView.setText(SharedPrefsUtil.getString(getContext(), "email"));
        mHeightTextView.setText(SharedPrefsUtil.getString(getContext(), "height") + " cm");
        mWeightTextView.setText(SharedPrefsUtil.getString(getContext(), "weight") + " kg");
        mAgeTextView.setText(SharedPrefsUtil.getString(getContext(), "age"));

        TextView[] textViews = {mHeightTextView, mWeightTextView, mAgeTextView};
        for (TextView textView : textViews) {
            if (textView.getText().toString().isEmpty()) {
                textView.setVisibility(View.INVISIBLE);
            }
        }


        mCancelButton.setOnClickListener(v -> getFragmentManager().beginTransaction().replace(R.id.container, new FragmentForm1()).commit());

        mAcceptButton.setOnClickListener(view1 -> createUser(SharedPrefsUtil.getString(getContext(), "email"), SharedPrefsUtil.getString(getContext(), "password")));

        return view;
    }

    private void createUser(String email, String password) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        user.sendEmailVerification();
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(SharedPrefsUtil.getString(getContext(), "username"))
                                .build();
                        user.updateProfile(profileUpdates)
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        Toast.makeText(getContext(), getString(R.string.newUserSuccess), Toast.LENGTH_SHORT).show();
                                        Intent i = new Intent(getContext(), Login.class);
                                        startActivity(i);
                                    }
                                });
                        StorageUtil.createStorageUser(user, SharedPrefsUtil.getString(getContext(), "weight"), SharedPrefsUtil.getString(getContext(), "age"), SharedPrefsUtil.getString(getContext(), "height"), getString(R.string.defaultStatus));
                    } else {
                        Toast.makeText(getContext(), getString(R.string.newUserFail), Toast.LENGTH_SHORT).show();
                    }
                });
    }

}