package com.vitaleats.main;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import com.vitaleats.R;
import com.vitaleats.utilities.FirebaseHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FragmentMain2 extends Fragment {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseHandler firebaseHandler;
    private EditText newItemEditText;
    private Button addButton;
    private ListView foodListView;
    private ArrayList<String> foodList;
    private List<Integer> foodListCount;
    private ArrayAdapter<String> foodListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main2, container, false);

        newItemEditText = view.findViewById(R.id.new_item_edit_text);
        addButton = view.findViewById(R.id.add_button);
        foodListView = view.findViewById(R.id.food_list_view);

        firebaseHandler = new FirebaseHandler();
        foodList = new ArrayList<>();
        foodListCount = new ArrayList<>();
        foodListAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, foodList);
        foodListView.setAdapter(foodListAdapter);

        addButton.setOnClickListener(view12 -> {
            String newItem = newItemEditText.getText().toString().toLowerCase();
            if (!newItem.isEmpty()) {
                if (foodList.contains(newItem)) {
                    int index = foodList.indexOf(newItem);
                    int count = foodListCount.get(index) + 1;
                    foodListCount.set(index, count);
                    foodList.set(index, newItem + " x" + count);
                } else {
                    foodList.add(newItem);
                    foodListCount.add(1);
                }
                foodListAdapter.notifyDataSetChanged();
                firebaseHandler.addFood(newItem);
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
                        String foodToRemove = foodList.get(position);
                        foodList.remove(position);
                        foodListAdapter.notifyDataSetChanged();
                        firebaseHandler.removeFood(foodToRemove);
                    })
                    .setNegativeButton(getResources().getString(R.string.no), (dialog, which) -> dialog.dismiss())
                    .create()
                    .show();
        });

        db.collection("foodList")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            foodList.add(document.getString("name"));
                        }
                        foodListAdapter.notifyDataSetChanged();
                    } else {
                        Log.w("Firebase", "Error getting documents.", task.getException());
                    }
                });

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < foodList.size(); i++) {
            sb.append(foodList.get(i)).append(",");
        }
        editor.putString(getResources().getString(R.string.food), sb.toString());
        editor.apply();
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        String foodListString = sharedPreferences.getString(getResources().getString(R.string.food), "");
        if (!foodListString.equals("")) {
            String[] items = foodListString.split(",");
            foodList = new ArrayList<>(Arrays.asList(items));
            foodListAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, foodList);
            foodListView.setAdapter(foodListAdapter);
        }
    }

    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result ->
    {
        if (result.getContents() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(getResources().getString(R.string.result));
            builder.setMessage(result.getContents());
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            }).show();
        }
    });
}