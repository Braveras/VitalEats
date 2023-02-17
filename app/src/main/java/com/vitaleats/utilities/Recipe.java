package com.vitaleats.utilities;

import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Recipe {

    private String recipeTitle;
    private String recipeIngredients;
    private String recipeElaboration;
    private String tvRecipeServings;
    private String tvRecipeTime;
    private String selectedRecipeType;
    private List<String> tags;
    private List<String> images;
    private String creatorUid;
    private String creatorUsername;
    private float rating;
    private Date createdAt;
    private Boolean isPublished;

    public Recipe(String recipeTitle, String recipeIngredients, String recipeElaboration, String tvRecipeServings,
                  String tvRecipeTime, String selectedRecipeType, List<String> tags, List<String> images, String creatorUid,
                  String creatorUsername, float rating, Date createdAt, Boolean isPublished) {

        this.recipeTitle = recipeTitle;
        this.recipeIngredients = recipeIngredients;
        this.recipeElaboration = recipeElaboration;
        this.tvRecipeServings = tvRecipeServings;
        this.tvRecipeTime = tvRecipeTime;
        this.selectedRecipeType = selectedRecipeType;
        this.tags = tags;
        this.images = images;
        this.creatorUid = creatorUid;
        this.creatorUsername = creatorUsername;
        this.rating = rating;
        this.createdAt = createdAt;
        this.isPublished = isPublished;
    }

    public Recipe() {

    }

    public String getRecipeTitle() {
        return recipeTitle;
    }

    public String getRecipeIngredients() {
        return recipeIngredients;
    }

    public String getRecipeElaboration() {
        return recipeElaboration;
    }

    public String getTvRecipeServings() {
        return tvRecipeServings;
    }

    public String getTvRecipeTime() {
        return tvRecipeTime;
    }

    public String getSelectedRecipeType() {
        return selectedRecipeType;
    }

    public List<String> getTags() {
        return tags;
    }

    public List<String> getImages() {
        return images;
    }

    public String getCreatorUid() {
        return creatorUid;
    }

    public String getCreatorUsername() {
        return creatorUsername;
    }

    public float getRating() {
        return rating;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Boolean getIsPublished() {
        return isPublished;
    }

    // Método para guardar el objeto Recipe en una base de datos Firestore
    public void saveToFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Crea un mapa con los datos del objeto Recipe
        Map<String, Object> recipeData = new HashMap<>();
        recipeData.put("Title", recipeTitle);
        recipeData.put("Ingredients", recipeIngredients);
        recipeData.put("Elaboration", recipeElaboration);
        recipeData.put("Servings", tvRecipeServings);
        recipeData.put("Time", tvRecipeTime);
        recipeData.put("Type", selectedRecipeType);
        recipeData.put("Tags", Arrays.asList(tags));
        recipeData.put("Images", Arrays.asList(images));
        recipeData.put("CreatorUid", creatorUid);
        recipeData.put("CreatorUsername", creatorUsername);
        recipeData.put("Rating", rating);
        recipeData.put("CreatedAt", createdAt);
        recipeData.put("IsPublished", isPublished);

        // Añade el mapa a la base de datos Firestore
        db.collection("recipes").add(recipeData)
                .addOnSuccessListener(documentReference -> {
                    Log.d("Recipe", "Recipe added with ID: " + documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    Log.w("Recipe", "Error adding recipe", e);
                });
    }
}
