package com.vitaleats.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.vitaleats.R;
import com.vitaleats.utilities.FirebaseHandler;
import com.vitaleats.utilities.Food;
import com.vitaleats.utilities.FoodAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FragmentMain2 extends Fragment {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseHandler firebaseHandler;
    private EditText newItemEditText;
    private Button addButton;
    private ListView foodListView;
    private ArrayList<Food> mFoodList;
    private FoodAdapter mFoodAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main2, container, false);

        newItemEditText = view.findViewById(R.id.new_item_edit_text);
        addButton = view.findViewById(R.id.add_button);
        foodListView = view.findViewById(R.id.food_list_view);

        firebaseHandler = new FirebaseHandler();

        mFoodList = new ArrayList<>();
        mFoodAdapter = new FoodAdapter(getContext(), R.layout.list_item_layout, mFoodList);
        foodListView.setAdapter(mFoodAdapter);

        addButton.setOnClickListener(view12 -> {
            String newItem = newItemEditText.getText().toString().toLowerCase().trim();
            if (!newItem.isEmpty()) {
                firebaseHandler.addFood(newItem);
                mFoodList.add(new Food(newItem));
                mFoodAdapter.notifyDataSetChanged();
                newItemEditText.setText("");
                Map<String, Object> food = new HashMap<>();
                food.put("name", newItem);
                db.collection("foodList")
                        .add(food)
                        .addOnSuccessListener(documentReference -> {
                            Log.d("Firebase", "DocumentSnapshot added with ID: " + documentReference.getId());
                        })
                        .addOnFailureListener(e -> {
                            Log.w("Firebase", "Error adding document", e);
                        });
            } else {
                Toast.makeText(getActivity(), getString(R.string.enterFood), Toast.LENGTH_LONG).show();
            }
        });

        foodListView.setOnItemClickListener((adapterView, view1, position, id) -> {
            new AlertDialog.Builder(getContext())
                    .setTitle(getResources().getString(R.string.deleteItem))
                    .setPositiveButton(getResources().getString(R.string.yes), (dialog, which) -> {
                        String foodToRemove = mFoodList.get(position).getName();
                        mFoodList.remove(position);
                        mFoodAdapter.notifyDataSetChanged();
                        firebaseHandler.removeFood(foodToRemove);
                        db.collection("foodList")
                                .whereEqualTo("name", foodToRemove)
                                .get()
                                .addOnSuccessListener(querySnapshot -> {
                                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                        String documentId = document.getId();
                                        db.collection("foodList")
                                                .document(documentId)
                                                .delete()
                                                .addOnSuccessListener(aVoid -> {
                                                    Log.d("Firebase", "DocumentSnapshot successfully deleted!");
                                                })
                                                .addOnFailureListener(e -> {
                                                    Log.w("Firebase", "Error deleting document", e);
                                                });
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Log.w("Firebase", "Error querying documents", e);
                                });
                    })
                    .setNegativeButton(getResources().getString(R.string.no), (dialog, which) -> dialog.dismiss())
                    .create()
                    .show();
        });

        db.collection("foodList")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.w("Firebase", "Listen failed.", error);
                        return;
                    }
                    mFoodList.clear();
                    for (QueryDocumentSnapshot doc : value) {
                        String foodName = doc.getString("name");
                        mFoodList.add(new Food(foodName));
                    }
                    mFoodAdapter.notifyDataSetChanged();
                });

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mFoodList.size(); i++) {
            sb.append(mFoodList.get(i)).append(",");
        }
        editor.putString(getResources().getString(R.string.bottom_menu_grocery), sb.toString());
        editor.apply();
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        String foodListString = sharedPreferences.getString(getResources().getString(R.string.bottom_menu_grocery), "");
        if (!foodListString.equals("")) {
            String[] items = foodListString.split(",");
            mFoodList = new ArrayList<>();
            for (String item : items) {
                mFoodList.add(new Food(item));
            }
            if (mFoodAdapter == null) { // Comprobar si el adaptador es nulo
                mFoodAdapter = new FoodAdapter(getContext(), mFoodList);
                foodListView.setAdapter(mFoodAdapter);
            } else {
                mFoodAdapter.notifyDataSetChanged(); // Notificar al adaptador de los cambios en los datos
            }
        }
    }
}