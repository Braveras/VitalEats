package com.vitaleats.main;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.vitaleats.R;
import com.vitaleats.utilities.FirebaseHandler;
import com.vitaleats.utilities.FoodListAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class FragmentMain2 extends Fragment {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseHandler firebaseHandler;
    private EditText newItemEditText;
    private Button addButton;
    private ListView foodListView;

    private DatabaseReference databaseReference;
    private ArrayList<HashMap<String, Object>> foodList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main2, container, false);

        newItemEditText = view.findViewById(R.id.new_item_edit_text);
        addButton = view.findViewById(R.id.add_button);
        foodListView = view.findViewById(R.id.food_list_view);

        firebaseHandler = new FirebaseHandler();

        foodList = new ArrayList<>();
        FoodListAdapter adapter = new FoodListAdapter(getContext(), R.layout.food_item, foodList);
        foodListView.setAdapter(adapter);

        // Initialize database reference
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("foodList");

        // Add child event listener to receive real-time updates
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String foodName = dataSnapshot.getKey();
                if (!isFoodAlreadyExist(foodName)) {
                    HashMap<String, Object> food = new HashMap<>();
                    food.put("key", foodName);
                    food.put("name", dataSnapshot.getValue());
                    foodList.add(food);
                    adapter.notifyDataSetChanged();
                    Log.d("TAG", "Data synchronized successfully.");
                } else {
                    Log.d("TAG", "Data already exists in the list.");
                }
            }

            @Override
            public void onChildChanged(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d("TAG", "Data changed successfully.");
            }

            @Override
            public void onChildRemoved(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot) {
                String foodKey = dataSnapshot.getKey();
                int position = -1;
                for (int i = 0; i < foodList.size(); i++) {
                    HashMap<String, Object> food = foodList.get(i);
                    if (food.containsKey("key") && food.get("key").equals(foodKey)) {
                        position = i;
                        break;
                    }
                }
                if (position != -1) {
                    foodList.remove(position);
                    adapter.notifyDataSetChanged();
                    Log.d("TAG", "Data removed successfully from local list.");
                }
                Log.d("TAG", "Data removed successfully.");
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d("TAG", "onChildMoved: " + dataSnapshot.getKey());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("TAG", "onCancelled: " + databaseError.getMessage());
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String foodName = newItemEditText.getText().toString().toLowerCase().trim();
                if (!foodName.isEmpty()) {
                    HashMap<String, Object> food = new HashMap<>();
                    firebaseHandler.addFood(foodName);
                    newItemEditText.setText("");
                    food.put("name", foodName);
                    db.collection("foodList")
                            .add(food)
                            .addOnSuccessListener(documentReference -> {
                                Log.d("Firebase", "DocumentSnapshot added with ID: " + documentReference.getId());
                                foodList.add(food);
                                adapter.notifyDataSetChanged();
                            })
                            .addOnFailureListener(e -> {
                                Log.w("Firebase", "Error adding document", e);
                            });
                } else {
                    Toast.makeText(getActivity(), getString(R.string.enterFood
                    ), Toast.LENGTH_LONG).show();
                }
            }
        });

        return view;
    }

    private boolean isFoodAlreadyExist(String foodName) {
        for (HashMap<String, Object> food : foodList) {
            if (food.get("name").equals(foodName)) {
                return true;
            }
        }
        return false;
    }
}