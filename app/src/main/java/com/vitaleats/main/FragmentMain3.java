package com.vitaleats.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;

import com.google.android.material.chip.Chip;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.vitaleats.R;

public class FragmentMain3 extends Fragment {

    Chip chip1, chip2, chip3;
    EditText textoBusqueda;
    ImageButton btn_buscar;
    String chip1str = "";
    String chip2str = "";
    String chip3str = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main3, container, false);

        textoBusqueda = view.findViewById(R.id.textoBusquedaEditText);
        btn_buscar = view.findViewById(R.id.buscarButton);
        chip1 = view.findViewById(R.id.chip1);
        chip2 = view.findViewById(R.id.chip2);
        chip3 = view.findViewById(R.id.chip3);

        textoBusqueda.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // Hide keyboard
                InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);
                return true;
            }
            return false;
        });

        // Configuramos el TextChangedListener del EditText para que actualice los chips
        textoBusqueda.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Update chips
                if (s.toString().endsWith(",") || s.toString().endsWith(".")) {
                    String text = s.toString().substring(0, s.length() - 1).trim();
                    if (chip1str.isEmpty()) {
                        chip1str = text;
                    } else if (chip2str.isEmpty()) {
                        chip2str = text;
                    } else if (chip3str.isEmpty()) {
                        chip3str = text;
                    }
                    // Clear EditText
                    textoBusqueda.setText("");
                    // Disable EditText if all chips are filled
                    if (!chip1str.isEmpty() && !chip2str.isEmpty() && !chip3str.isEmpty()) {
                        textoBusqueda.setEnabled(false);
                    }
                    // Update chips
                    if (chip1str.isEmpty()) {
                        chip1.setText("");
                        chip1.setVisibility(View.GONE);
                    } else {
                        chip1.setText(chip1str);
                        chip1.setVisibility(View.VISIBLE);
                    }
                    if (chip2str.isEmpty()) {
                        chip2.setText("");
                        chip2.setVisibility(View.GONE);
                    } else {
                        chip2.setText(chip2str);
                        chip2.setVisibility(View.VISIBLE);
                    }
                    if (chip3str.isEmpty()) {
                        chip3.setText("");
                        chip3.setVisibility(View.GONE);
                    } else {
                        chip3.setText(chip3str);
                        chip3.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Configuramos los OnClickListeners de los chips para que los borre cuando se haga clic en ellos
        chip1.setOnClickListener(view1 -> {
            chip1str = "";
            chip1.setVisibility(View.GONE);
            textoBusqueda.setEnabled(true);
        });
        chip2.setOnClickListener(view1 -> {
            chip2str = "";
            chip2.setVisibility(View.GONE);
            textoBusqueda.setEnabled(true);
        });
        chip3.setOnClickListener(view1 -> {
            chip3str = "";
            chip3.setVisibility(View.GONE);
            textoBusqueda.setEnabled(true);
        });


        btn_buscar.setOnClickListener(view12 -> {
            // Create search query using chip keywords
            String query = chip1str + " " + chip2str + " " + chip3str;
            System.out.println(query);
        });

        return view;
    }
}
