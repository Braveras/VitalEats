package com.vitaleats.main;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;


import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.vitaleats.R;
import com.vitaleats.login.Login;
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

    // Identificador único del usuario actual
    private String currentUserUid;

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

        // Obtener el UID del usuario actual
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            currentUserUid = currentUser.getUid();
        } else {
            // Si no hay usuario logueado, redirigir a pantalla de login
            Intent intent = new Intent(getActivity(), Login.class);
            startActivity(intent);
            getActivity().finish();
        }

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String foodName = newItemEditText.getText().toString().toLowerCase().trim();
                if (!foodName.isEmpty()) {
                    HashMap<String, Object> food = new HashMap<>();
                    firebaseHandler.addFood(foodName);
                    newItemEditText.setText("");
                    food.put("name", foodName);
                    food.put("userId", currentUserUid); // Agregar UID del usuario actual
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
                    Toast.makeText(getActivity(), getString(R.string.enterFood), Toast.LENGTH_LONG).show();
                }
            }
        });

        // Obtener los alimentos del usuario actual
        db.collection("foodList")
                .whereEqualTo("userId", currentUserUid)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        HashMap<String, Object> food = new HashMap<>();
                        food.put("name", document.getString("name"));
                        foodList.add(food);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.w("Firebase", "Error getting documents.", e);
                });

        return view;
    }
}