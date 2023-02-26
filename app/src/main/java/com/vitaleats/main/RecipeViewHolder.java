package com.vitaleats.main;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.vitaleats.R;
import com.vitaleats.utilities.Recipe;

import java.text.SimpleDateFormat;

public class RecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

    private OnRecipeClickListener mListener;
    private OnRecipeLongClickListener mLongListener;

    private Context mContext;
    public TextView titleTextView;
    public ImageView imageView, iv_servings;
    public TextView typeTextView;
    public ChipGroup chipGroup;
    public TextView timeTextView;
    public TextView servingsTextView;
    public RatingBar ratingBar;
    public Chip chip1, chip2, chip3, chip4;
    public TextView createdAtTextView, recipeCreatortv;

    public RecipeViewHolder(@NonNull View itemView, OnRecipeClickListener listener, OnRecipeLongClickListener longListener) {
        super(itemView);
        mContext = itemView.getContext();
        mListener = listener;
        mLongListener = longListener;

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
        iv_servings = itemView.findViewById(R.id.iv_servings);

        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
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

        int imageResId = (lastChar > '1')
                ? R.drawable.ic_people_newrecipe
                : R.drawable.ic_person_newrecipe;

        iv_servings.setImageResource(imageResId);
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

    @Override
    public void onClick(View v) {
        mListener.onRecipeClick(getAdapterPosition());
    }

    @Override
    public boolean onLongClick(View v) {
        mLongListener.onRecipeLongClick(getAdapterPosition());
        return true;
    }
}