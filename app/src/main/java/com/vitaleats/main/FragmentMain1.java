package com.vitaleats.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.vitaleats.R;
import com.vitaleats.utilities.Recipe;

public class FragmentMain1 extends Fragment implements OnRecipeClickListener, OnRecipeLongClickListener {

    private ImageButton newRecipe_btn;
    private RecyclerView feed_recetas;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main1, container, false);
        newRecipe_btn = view.findViewById(R.id.new_recipe);
        feed_recetas = view.findViewById(R.id.recipes_feed_recycler_view);

        newRecipe_btn.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), newRecipeActivity.class);
            startActivity(intent);
        });

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        CollectionReference recipeRef = firestore.collection("recipes");
        Query query = recipeRef.orderBy("createdAt", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Recipe> options = new FirestoreRecyclerOptions.Builder<Recipe>()
                .setQuery(query, Recipe.class)
                .build();

        // Configurar el RecyclerView con un LinearLayoutManager
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        feed_recetas.setLayoutManager(layoutManager);

        FirestoreRecyclerAdapter<Recipe, RecipeViewHolder> adapter = new FirestoreRecyclerAdapter<Recipe, RecipeViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull RecipeViewHolder holder, int position, @NonNull Recipe model) {
                String recipeId = this.getSnapshots().getSnapshot(position).getId();
                DocumentReference recipeRef = firestore.collection("recipes").document(recipeId);
                model.setRef(recipeRef);
                // Bind the recipe data to the view holder
                holder.bind(model);
            }

            @NonNull
            @Override
            public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                // Create a new view holder for the recipe items
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_item, parent, false);
                return new RecipeViewHolder(view, FragmentMain1.this, FragmentMain1.this);
            }
        };

        feed_recetas.setAdapter(adapter);
        adapter.startListening();

        return view;
    }

    @Override
    public void onRecipeClick(int position) {
        // Obtener el adaptador del RecyclerView
        FirestoreRecyclerAdapter<Recipe, RecipeViewHolder> adapter = (FirestoreRecyclerAdapter<Recipe, RecipeViewHolder>) feed_recetas.getAdapter();

        // Obtener el objeto Recipe correspondiente a la posición del elemento clicado
        Recipe recipe = adapter.getItem(position);

        Intent intent = new Intent(getActivity(), DetailedRecipeActivity.class);
        intent.putExtra("recipe", recipe);
        startActivity(intent);
    }

    @Override
    public void onRecipeLongClick(int position) {
        System.out.println("hehe");
    }
}