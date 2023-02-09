package com.vitaleats.utilities;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

public class FirebaseHandler {

    private DatabaseReference databaseReference;

    public FirebaseHandler() {
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public void addFood(String foodName) {
        databaseReference.child("foods").push().setValue(foodName);
    }

    public void removeFood(String foodName) {
        databaseReference.child("foods").orderByValue().equalTo(foodName)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            snapshot.getRef().removeValue();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Handle error
                    }
                });
    }
}
