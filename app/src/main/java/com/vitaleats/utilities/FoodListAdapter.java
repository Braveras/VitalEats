package com.vitaleats.utilities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.vitaleats.R;

import java.util.HashMap;
import java.util.List;

public class FoodListAdapter extends BaseAdapter {
    private Context context;
    private List<HashMap<String, Object>> foodList;
    private DatabaseReference databaseReference;
    private FirebaseHandler firebaseHandler;

    public FoodListAdapter(Context context, int food_item, List<HashMap<String, Object>> foodList) {
        this.context = context;
        this.foodList = foodList;
        firebaseHandler = new FirebaseHandler();
    }

    @Override
    public int getCount() {
        return foodList.size();
    }

    @Override
    public Object getItem(int position) {
        return foodList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.food_item, null);
        }

        TextView foodNameTextView = convertView.findViewById(R.id.food_name);
        TextView foodCountTextView = convertView.findViewById(R.id.food_count);
        Button minusButton = convertView.findViewById(R.id.minus_button);
        Button plusButton = convertView.findViewById(R.id.plus_button);

        final HashMap<String, Object> food = foodList.get(position);
        foodNameTextView.setText((String) food.get("name"));
        final String foodId = (String) food.get("id");
        final int foodCount = (int) food.get("count");
        foodCountTextView.setText(String.valueOf(foodCount));

        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity = Integer.parseInt(foodCountTextView.getText().toString());
                if (quantity > 0) {
                    quantity--;
                    foodCountTextView.setText(String.valueOf(quantity));
                    food.put("count", quantity);
                    firebaseHandler.updateFoodCount(foodId, quantity);
                } else {
                    // Eliminar el elemento de la lista y guardar su ID
                    final HashMap<String, Object> deletedFood = foodList.remove(position);
                    notifyDataSetChanged();

                    // Eliminar el elemento de Firebase
                    firebaseHandler.removeFood(foodId);

                }
            }
        });

        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity = Integer.parseInt(foodCountTextView.getText().toString());
                if (quantity >= 0) {
                    quantity++;
                    foodCountTextView.setText(String.valueOf(quantity));
                    food.put("count", quantity);
                    firebaseHandler.updateFoodCount(foodId, quantity);
                }
            }
        });

        return convertView;
    }
}

