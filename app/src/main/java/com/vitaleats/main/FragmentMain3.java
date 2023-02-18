package com.vitaleats.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.vitaleats.R;
import com.vitaleats.utilities.Recipe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FragmentMain3 extends Fragment implements QueryRecipeAdapter.OnRecipeClickListener {

    Chip chip1, chip2, chip3;
    EditText textoBusqueda;
    ImageButton btn_buscar;
    String chip1str = "";
    String chip2str = "";
    String chip3str = "";

    ArrayList<Recipe> mRecetas = new ArrayList<>();
    QueryRecipeAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main3, container, false);

        //Obtenemos las referencias a las vistas
        textoBusqueda = view.findViewById(R.id.textoBusquedaEditText);
        btn_buscar = view.findViewById(R.id.buscarButton);
        chip1 = view.findViewById(R.id.chip1);
        chip2 = view.findViewById(R.id.chip2);
        chip3 = view.findViewById(R.id.chip3);

        // Configuramos el RecyclerView
        RecyclerView resultadosRecyclerView = view.findViewById(R.id.resultadosRecyclerView);
        resultadosRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new QueryRecipeAdapter(mRecetas);
        mAdapter.setOnRecipeClickListener(this);
        resultadosRecyclerView.setAdapter(mAdapter);

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
            // Comprobar los chips vacíos y crear nuevo array de los que no estén vacíos.
            String[] chipstrs = {chip1str, chip2str, chip3str};
            String[] keywords = Arrays.stream(chipstrs).filter(s -> !s.isEmpty()).toArray(String[]::new);

            // Crear query para búqueda en firebase
            CollectionReference recetasRef = FirebaseFirestore.getInstance().collection("recipes");
            Query query = recetasRef;

            // Crear listener del query para obtener todas las recetas
            query.addSnapshotListener((querySnapshot, error) -> {
                if (error != null) {
                    Log.w("Error getting documents.", error);
                    return;
                }

                List<Recipe> allRecipes = new ArrayList<>();

                for (QueryDocumentSnapshot document : querySnapshot) {
                    Recipe receta = document.toObject(Recipe.class);
                    receta.setRef(document.getReference()); // Agregar el DocumentReference a la receta
                    allRecipes.add(receta);
                }

                List<Recipe> filteredRecipes = new ArrayList<>();

                // Filtrar las recetas utilizando las palabras clave
                for (Recipe receta : allRecipes) {
                    String ingredients = receta.getRecipeIngredients();
                    boolean containsKeywords = true;

                    // Verificar que todas las palabras clave están presentes en los ingredientes
                    for (String keyword : keywords) {
                        if (!ingredients.contains(keyword)) {
                            containsKeywords = false;
                            break;
                        }
                    }

                    // Si todas las palabras clave están presentes, agregar la receta a la lista filtrada
                    if (containsKeywords) {
                        filteredRecipes.add(receta);
                    }
                }

                // Actualizar el RecyclerView con las recetas filtradas
                mRecetas.clear(); // Limpiamos la lista de recetas
                mRecetas.addAll(filteredRecipes);
                mAdapter.notifyDataSetChanged();
            });
        });


        return view;
    }

    @Override
    public void onRecipeClick(int position) {
        Recipe recipe = mRecetas.get(position);

        Intent intent = new Intent(getActivity(), DetailedRecipeActivity.class);
        intent.putExtra("recipe", recipe);
        startActivity(intent);
    }
}
