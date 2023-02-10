package com.vitaleats.login;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.vitaleats.R;
import com.vitaleats.main.MainBn;
import com.vitaleats.signup.NewAccount;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    ImageView logo, fondo;
    ImageButton googleAccess_btn;
    TextView slogan, tengoCuenta;
    Button crearCuenta;
    GoogleSignInClient mGoogleSignInClient;
    FirebaseAuth mAuth;
    int RC_SIGN_IN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        logo = findViewById(R.id.logo);
        fondo = findViewById(R.id.fondo);
        slogan = findViewById(R.id.slogan);
        tengoCuenta = findViewById(R.id.tengoCuenta);
        crearCuenta = findViewById(R.id.crearCuenta);
        googleAccess_btn = findViewById(R.id.google_logo_button);
        mAuth = FirebaseAuth.getInstance();

        Glide.with(this)
                .load(R.drawable.fondo)
                .transition(DrawableTransitionOptions.withCrossFade(100))
                .centerCrop()
                .into(fondo);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        googleAccess_btn.setOnClickListener(view -> signInWithGoogle());

        crearCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, NewAccount.class);
                startActivity(i);

                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            }
        });

        tengoCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(MainActivity.this, Login.class);
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
                Toast.makeText(MainActivity.this, getString(R.string.googleLoginSuccess), Toast.LENGTH_LONG).show();
            } catch (ApiException e) {
                Log.i("APIException: ", e.toString());
                Toast.makeText(MainActivity.this, getString(R.string.googleLoginError), Toast.LENGTH_LONG).show();
                updateUI(null);
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        String uid = user.getUid();
                        //Check if user information exists or not
                        StorageReference fileRef = FirebaseStorage.getInstance().getReference().child("documents/users/" + uid + "/" + "userInformation.json");
                        fileRef.getMetadata().addOnSuccessListener(storageMetadata -> {
                            //Exists
                            updateUI(user);
                        }).addOnFailureListener(exception -> {
                            //Not exist
                            createStorageUser(user);
                            updateUI(user);
                        });
                    } else {
                        updateUI(null);
                    }
                });
    }

    private void createStorageUser(FirebaseUser user) {
        String uid = user.getUid();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference rootRef = storage.getReference();
        StorageReference folderRef = rootRef.child("documents/users/" + uid + "/userInformation.json");

        Map<String, String> userInformation = new HashMap<>();
        userInformation.put("edad", "0");
        userInformation.put("peso", "0");
        userInformation.put("altura", "0");

        folderRef.putBytes(new Gson().toJson(userInformation).getBytes(StandardCharsets.UTF_8))
                .addOnSuccessListener(taskSnapshot -> Log.d(TAG, "User information file written successfully"))
                .addOnFailureListener(exception -> Log.w(TAG, "Error writing user information file", exception));
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Intent i = new Intent(MainActivity.this, MainBn.class);
            startActivity(i);
        }
    }

    @Override
    public void onBackPressed() {
        //Do not do anything
    }
}