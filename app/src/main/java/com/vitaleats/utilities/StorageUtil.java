package com.vitaleats.utilities;

import static android.content.ContentValues.TAG;

import android.util.Log;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.vitaleats.R;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class StorageUtil {

    public static void createStorageUser(FirebaseUser user, String peso, String edad, String altura) {
        String uid = user.getUid();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference rootRef = storage.getReference();
        StorageReference folderRef = rootRef.child("documents/users/" + uid + "/userInformation.json");

        Map<String, String> userInformation = new HashMap<>();
        userInformation.put("edad", peso);
        userInformation.put("peso", edad);
        userInformation.put("altura", altura);
        userInformation.put("status", String.valueOf(R.string.defaultStatus));

        folderRef.putBytes(new Gson().toJson(userInformation).getBytes(StandardCharsets.UTF_8))
                .addOnSuccessListener(taskSnapshot -> Log.d(TAG, "User information file written successfully"))
                .addOnFailureListener(exception -> Log.w(TAG, "Error writing user information file", exception));
    }

}
