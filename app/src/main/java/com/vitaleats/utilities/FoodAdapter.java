package com.vitaleats.utilities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.vitaleats.R;

import java.util.ArrayList;

public class FoodAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Food> listItems;
    private int resource;
    private FirebaseHandler firebaseHandler;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public FoodAdapter(Context context, int resource, ArrayList<Food> listItems) {
        this.context = context;
        this.resource = resource;
        this.listItems = listItems;
        this.firebaseHandler = new FirebaseHandler();
    }

    public FoodAdapter(Context context, ArrayList<Food> listItems) {
        this.context = context;
        this.listItems = listItems;
    }

    @Override
    public int getCount() {
        return listItems.size();
    }

    @Override
    public Object getItem(int position) {
        return listItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        // Obtener la vista inflada
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.list_item_layout, parent, false);
        }

        // Obtener referencias a los elementos de la vista
        TextView itemNameTextView = view.findViewById(R.id.item_name_text_view);
        Button minusButton = view.findViewById(R.id.minus_button);
        TextView quantityTextView = view.findViewById(R.id.quantity_text_view);
        Button plusButton = view.findViewById(R.id.plus_button);

        // Configurar el botón "-" para restar uno al contador
        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity = Integer.parseInt(quantityTextView.getText().toString());
                if (quantity > 0) {
                    quantity--;
                    quantityTextView.setText(String.valueOf(quantity));
                } else {
                    // Eliminar el elemento de la lista
                    listItems.remove(position);
                    notifyDataSetChanged();

                    // Eliminar el elemento de Firebase
                    //firebaseHandler.removeFood(listItems.get(position).getId());
                }
            }
        });

        // Configurar el botón "+" para aumentar uno al contador
        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity = Integer.parseInt(quantityTextView.getText().toString());
                if (quantity >= 0) {
                    quantity++;
                    quantityTextView.setText(String.valueOf(quantity));
                }
            }
        });

        // Mostrar el nombre del alimento en la vista
        itemNameTextView.setText(listItems.get(position).getName());

        return view;
    }
}