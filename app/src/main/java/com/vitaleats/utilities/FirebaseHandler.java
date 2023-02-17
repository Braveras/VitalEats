package com.vitaleats.utilities;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.HashMap;
import java.util.Map;

public class FirebaseHandler {

    private DatabaseReference databaseReference;

    public FirebaseHandler() {
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public void addFood(String foodName) {
        databaseReference.child("foods").push().setValue(foodName);
    }

    public void removeFood(String foodItemId) {
        databaseReference.child("foods").child(foodItemId)
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("FirebaseHandler", "Food item deleted from Firebase");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("FirebaseHandler", "Error deleting food item from Firebase", e);
                    }
                });
    }
}
