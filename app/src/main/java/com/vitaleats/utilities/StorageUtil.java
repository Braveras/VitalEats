package com.vitaleats.utilities;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vitaleats.R;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class StorageUtil {

    public static void createStorageUser(FirebaseUser user, String peso, String edad, String altura, String status) {
        String uid = user.getUid();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference rootRef = storage.getReference();
        StorageReference folderRef = rootRef.child("documents/users/" + uid + "/userInformation.json");

        Map<String, String> userInformation = new HashMap<>();
        userInformation.put("edad", peso);
        userInformation.put("peso", edad);
        userInformation.put("altura", altura);
        userInformation.put("status", status);

        folderRef.putBytes(new Gson().toJson(userInformation).getBytes(StandardCharsets.UTF_8))
                .addOnSuccessListener(taskSnapshot -> Log.d(TAG, "User information file written successfully"))
                .addOnFailureListener(exception -> Log.w(TAG, "Error writing user information file", exception));
    }

    public static void readStorageUser(FirebaseUser user, OnReadUserInformationListener listener) {
        String uid = user.getUid();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference rootRef = storage.getReference();
        StorageReference fileRef = rootRef.child("documents/users/" + uid + "/userInformation.json");

        fileRef.getBytes(Long.MAX_VALUE).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                byte[] bytes = task.getResult();
                String json = new String(bytes, StandardCharsets.UTF_8);
                Gson gson = new Gson();
                Map<String, String> userInformation = gson.fromJson(json, new TypeToken<Map<String, String>>(){}.getType());
                listener.onReadUserInformation(userInformation);
            } else {
                Log.w(TAG, "Error reading user information file", task.getException());
                listener.onReadUserInformation(null);
            }
        });
    }

    public interface OnReadUserInformationListener {
        void onReadUserInformation(Map<String, String> userInformation);
    }

    public static void updateStorageUser(FirebaseUser user, Map<String, String> updatedUserInformation) {
        String uid = user.getUid();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference rootRef = storage.getReference();
        StorageReference fileRef = rootRef.child("documents/users/" + uid + "/userInformation.json");

        fileRef.getBytes(Long.MAX_VALUE).addOnSuccessListener(bytes -> {
            String json = new String(bytes, StandardCharsets.UTF_8);
            Gson gson = new Gson();
            Map<String, String> userInformation = gson.fromJson(json, new TypeToken<Map<String, String>>(){}.getType());

            // Update the user information with the new values
            userInformation.putAll(updatedUserInformation);

            fileRef.putBytes(new Gson().toJson(userInformation).getBytes(StandardCharsets.UTF_8))
                    .addOnSuccessListener(taskSnapshot -> Log.d(TAG, "User information file updated successfully"))
                    .addOnFailureListener(exception -> Log.w(TAG, "Error updating user information file", exception));
        }).addOnFailureListener(exception -> Log.w(TAG, "Error reading user information file", exception));
    }


}
