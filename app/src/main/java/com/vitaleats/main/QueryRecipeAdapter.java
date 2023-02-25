package com.vitaleats.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.vitaleats.R;
import com.vitaleats.utilities.Recipe;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class QueryRecipeAdapter extends RecyclerView.Adapter<QueryRecipeAdapter.RecipeViewHolder> {

    private ArrayList<Recipe> mRecetas;
    private OnRecipeClickListener mClickListener;

    public QueryRecipeAdapter(ArrayList<Recipe> recetas) {
        mRecetas = recetas;
    }

    @Override
    public RecipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_item, parent, false);
        return new RecipeViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecipeViewHolder holder, int position) {
        Recipe receta = mRecetas.get(position);
        holder.bind(receta);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClickListener != null) {
                    mClickListener.onRecipeClick(holder.getAdapterPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mRecetas.size();
    }

    public interface OnRecipeClickListener {
        void onRecipeClick(int position);
    }

    public void setOnRecipeClickListener(OnRecipeClickListener listener) {
        mClickListener = listener;
    }

    public static class RecipeViewHolder extends RecyclerView.ViewHolder {
        private Context mContext;
        public TextView titleTextView;
        public ImageView imageView;
        public TextView typeTextView;
        public ChipGroup chipGroup;
        public TextView timeTextView;
        public TextView servingsTextView;
        public RatingBar ratingBar;
        public Chip chip1, chip2, chip3, chip4;
        public TextView recipeCreatortv;

        public RecipeViewHolder(View itemView) {
            super(itemView);
            mContext = itemView.getContext();
            titleTextView = itemView.findViewById(R.id.recipe_title);
            imageView = itemView.findViewById(R.id.recipe_image);
            chip1 = itemView.findViewById(R.id.chip_1);
            chip2 = itemView.findViewById(R.id.chip_2);
            chip3 = itemView.findViewById(R.id.chip_3);
            chip4 = itemView.findViewById(R.id.chip_4);
            typeTextView = itemView.findViewById(R.id.recipe_type);
            chipGroup = itemView.findViewById(R.id.recipe_tags);
            timeTextView = itemView.findViewById(R.id.recipe_time);
            servingsTextView = itemView.findViewById(R.id.recipe_servings);
            ratingBar = itemView.findViewById(R.id.recipe_rating);
            recipeCreatortv = itemView.findViewById(R.id.recipe_creator);
        }

        public void bind(Recipe recipe) {
            titleTextView.setText(recipe.getRecipeTitle());
            timeTextView.setText(recipe.getTvRecipeTime());

            //Checks for comensales
            String servings = recipe.getTvRecipeServings();
            char lastChar = servings.charAt(servings.length() - 1);
            String servingsStr = (lastChar > '1')
                    ? mContext.getString(R.string.num_people_value)
                    : mContext.getString(R.string.num_person_value);

            servingsTextView.setText(" " + recipe.getTvRecipeServings() + " " + servingsStr);
            typeTextView.setText(recipe.getSelectedRecipeType());
            ratingBar.setRating(recipe.getRating());
            Glide.with(imageView.getContext())
                    .load(recipe.getImages().get(0))
                    .into(imageView);

            // Verificar si la lista de etiquetas está vacía
            if (recipe.getTags() != null && recipe.getTags().size() > 0) {
                for (int i = 0; i < recipe.getTags().size(); i++) {
                    String tag = recipe.getTags().get(i);

                    if (i == 0) {
                        chip1.setText(tag);
                        chip1.setVisibility(View.VISIBLE);
                    } else if (i == 1) {
                        chip2.setText(tag);
                        chip2.setVisibility(View.VISIBLE);
                    } else if (i == 2) {
                        chip3.setText(tag);
                        chip3.setVisibility(View.VISIBLE);
                    } else if (i == 3) {
                        chip4.setText(tag);
                        chip4.setVisibility(View.VISIBLE);
                    }
                }
            } else {
                // Si la lista de etiquetas está vacía, ocultar el ChipGroup
                this.chipGroup.setVisibility(View.GONE);
            }

            recipeCreatortv.setText(recipe.getCreatorUsername());
        }
    }
}
