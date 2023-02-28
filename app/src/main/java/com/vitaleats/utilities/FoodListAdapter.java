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
        firebaseHandler = new FirebaseHandler(); // Crea un nuevo objeto FirebaseHandler para manejar las operaciones de la base de datos de Firebase
    }

    @Override
    public int getCount() {
        return foodList.size(); // Devuelve el tamaño de la lista de alimentos
    }

    @Override
    public Object getItem(int position) {
        return foodList.get(position); // Devuelve el elemento de la lista de alimentos en la posición especificada
    }

    @Override
    public long getItemId(int position) {
        return position; // Devuelve el ID del elemento en la posición especificada
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.food_item, null);
        }

        // Obtiene referencias a los elementos de la vista del elemento de alimento
        TextView foodNameTextView = convertView.findViewById(R.id.food_name);
        TextView foodCountTextView = convertView.findViewById(R.id.food_count);
        Button minusButton = convertView.findViewById(R.id.minus_button);
        Button plusButton = convertView.findViewById(R.id.plus_button);

        final HashMap<String, Object> food = foodList.get(position); // Obtiene el elemento de la lista de alimentos en la posición especificada
        foodNameTextView.setText((String) food.get("name")); // Establece el nombre del alimento en el TextView correspondiente
        final String foodId = (String) food.get("id"); // Obtiene el ID del alimento
        final Integer countObj = (Integer) food.get("count");
        final int foodCount = (countObj != null) ? countObj.intValue() : 1;
        foodCountTextView.setText(String.valueOf(foodCount)); // Establece el número de unidades del alimento en el TextView correspondiente

        // Agrega un Listener al botón menos para actualizar el número de unidades del alimento o eliminar el alimento si el número de unidades es cero
        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity = Integer.parseInt(foodCountTextView.getText().toString());
                if (quantity > 1) {
                    quantity--;
                    foodCountTextView.setText(String.valueOf(quantity)); // Actualiza el número de unidades del alimento en el TextView correspondiente
                    food.put("count", quantity); // Actualiza el número de unidades del alimento en la lista de alimentos
                } else {
                    // Elimina el elemento de la lista y guarda su ID
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
                    foodCountTextView.setText(String.valueOf(quantity)); // Actualiza el número de unidades del alimento en el TextView correspondiente
                    food.put("count", quantity); // Actualiza el número de unidades del alimento en la lista de alimentos
                }
            }
        });
        return convertView;
    }
}

