package com.vitaleats.login;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.vitaleats.R;
import com.vitaleats.main.MainBn;

import java.util.regex.Pattern;

public class Login extends AppCompatActivity {

    private TextView forgottenPassword_btn;
    private Button mailPassworAccess_btn;
    private TextInputLayout lMail, lPasswd;
    private EditText editmail, editpass;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.vitaleats.R.layout.activity_login);

        Toolbar toolbar = findViewById(com.vitaleats.R.id.toolbar);
        setSupportActionBar(toolbar);
        forgottenPassword_btn = findViewById(com.vitaleats.R.id.forgottenPassword);
        mailPassworAccess_btn = findViewById(com.vitaleats.R.id.mailPasswordAccess_button);
        lMail = findViewById(R.id.til_login_mail);
        lPasswd = findViewById(R.id.til_login_password);
        editmail = findViewById(com.vitaleats.R.id.editmail);
        editpass = findViewById(com.vitaleats.R.id.editpass);
        mAuth = FirebaseAuth.getInstance();

        mailPassworAccess_btn.setOnClickListener(view -> {

            if (editmail.getText().toString().isEmpty() || editpass.getText().toString().isEmpty()) {
                if (editmail.getText().toString().isEmpty())
                    lMail.setError(getString(R.string.emptyFields));
                if (editpass.getText().toString().isEmpty())
                    lPasswd.setError(getString(R.string.emptyFields));
            } else if (!validarEmail(editmail.getText().toString())) {
                lMail.setError(getString(R.string.invalidEmail));
            } else {
                mAuth.signInWithEmailAndPassword(editmail.getText().toString(), editpass.getText().toString())
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user.isEmailVerified()) {
                                    Toast.makeText(Login.this, getString(R.string.loginSuccess), Toast.LENGTH_SHORT).show();
                                    limpiar();
                                    updateUI(user);
                                } else {
                                    Toast.makeText(Login.this, getString(R.string.mailNotVerified), Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                try {
                                    throw task.getException();
                                } catch (FirebaseAuthInvalidCredentialsException e) {
                                    Toast.makeText(Login.this, getString(R.string.wrongPassword), Toast.LENGTH_SHORT).show();
                                } catch (FirebaseAuthInvalidUserException e) {
                                    Toast.makeText(Login.this, getString(R.string.userNotFound), Toast.LENGTH_SHORT).show();
                                } catch (Exception e) {
                                    Log.e(TAG, e.toString());
                                    Toast.makeText(Login.this, getString(R.string.loginError), Toast.LENGTH_SHORT).show();
                                }
                                updateUI(null);
                            }
                        });
            }
        });

        forgottenPassword_btn.setOnClickListener(view -> {
            Intent i = new Intent(Login.this, ResetPassword.class);
            startActivity(i);

            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        });

    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Intent i = new Intent(Login.this, MainBn.class);
            startActivity(i);
        }
    }

    private boolean validarEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }

    private void limpiar() {
        editmail.setText("");
        editpass.setText("");
    }

}