package com.vitaleats.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.*;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.vitaleats.R;
import com.vitaleats.main.MainBn;

import java.util.regex.Pattern;

public class Login extends AppCompatActivity {

    TextView olvidado;
    Button button, button2, guestButton;
    EditText editmail, editpass;
    FirebaseAuth mAuth;
    GoogleSignInClient mGoogleSignInClient;
    int RC_SIGN_IN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.vitaleats.R.layout.activity_login);

        Toolbar toolbar = findViewById(com.vitaleats.R.id.toolbar);
        setSupportActionBar(toolbar);
        olvidado = findViewById(com.vitaleats.R.id.registerTextView);
        button = findViewById(com.vitaleats.R.id.button);
        button2 = findViewById(com.vitaleats.R.id.button2);
        guestButton = findViewById(R.id.guest);
        editmail = findViewById(com.vitaleats.R.id.editmail);
        editpass = findViewById(com.vitaleats.R.id.editpass);
        mAuth = FirebaseAuth.getInstance();

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!editmail.getText().toString().isEmpty() && !editpass.getText().toString().isEmpty() && validarEmail(editmail.getText().toString())) {
                    mAuth.signInWithEmailAndPassword(editmail.getText().toString(), editpass.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    if (user.isEmailVerified()) {
                                        // Correo electrónico verificado
                                        if (task.isSuccessful()) {
                                            Toast.makeText(Login.this, getString(R.string.loginSuccess), Toast.LENGTH_LONG).show();
                                            limpiar();
                                            updateUI(user);
                                        } else {
                                            try {
                                                throw task.getException();
                                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                                Toast.makeText(Login.this, getString(R.string.wrongPassword), Toast.LENGTH_LONG).show();
                                            } catch (FirebaseAuthInvalidUserException e) {
                                                Toast.makeText(Login.this, getString(R.string.userNotFound), Toast.LENGTH_LONG).show();
                                            } catch (Exception e) {
                                                Toast.makeText(Login.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                            updateUI(null);
                                        }
                                    } else {
                                        Toast.makeText(Login.this, getString(R.string.mailNotVerified), Toast.LENGTH_LONG).show();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // exception
                                }
                            });

                } else if (editmail.getText().toString().isEmpty() || editpass.getText().toString().isEmpty()) {
                    if (editmail.getText().toString().isEmpty())
                        editmail.setError(getString(R.string.emptyFields));
                    if (editpass.getText().toString().isEmpty())
                        editpass.setError(getString(R.string.emptyFields));
                } else if (!validarEmail(editmail.getText().toString())) {
                    editmail.setError(getString(R.string.invalidEmail));
                } else {
                    Toast.makeText(Login.this, getString(R.string.loginError), Toast.LENGTH_LONG).show();
                }
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInWithGoogle();
            }
        });

        guestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginAnonymous();
            }
        });

        olvidado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Login.this, ResetPassword.class);
                startActivity(i);

                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            }
        });

    }

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
                Toast.makeText(Login.this, getString(R.string.googleLoginSuccess), Toast.LENGTH_LONG).show();
            } catch (ApiException e) {
                Toast.makeText(Login.this, getString(R.string.googleLoginError), Toast.LENGTH_LONG).show();
                updateUI(null);
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            updateUI(null);
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Intent i = new Intent(Login.this, MainBn.class);
            startActivity(i);
        }
    }

    private void limpiar() {
        editmail.setText("");
        editpass.setText("");
    }

    private boolean validarEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }

    private void loginAnonymous() {
        mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser user = mAuth.getCurrentUser();
                            startActivity(new Intent(Login.this, MainBn.class));
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Login.this, "Error al acceder", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}