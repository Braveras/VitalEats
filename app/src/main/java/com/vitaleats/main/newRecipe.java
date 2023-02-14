package com.vitaleats.main;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.vitaleats.R;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class newRecipe extends AppCompatActivity {

    // Views
    private EditText etRecipeTitle;
    private ImageView ivRecipeImage;
    private EditText etRecipeIngredients;
    private EditText etRecipeDirections;
    private TextView tvRecipeTime;

    private TextView tvRecipeServings;
    private Spinner spRecipeType;
    private EditText etRecipeTags;
    private Button btnCreateRecipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_recipe);

        // Initialize views
        etRecipeTitle = findViewById(R.id.et_title);
        ivRecipeImage = findViewById(R.id.iv_photo);
        etRecipeIngredients = findViewById(R.id.et_ingredients);
        etRecipeDirections = findViewById(R.id.et_elaboration);
        tvRecipeTime = findViewById(R.id.tv_time_value);
        tvRecipeServings = findViewById(R.id.tv_num_people_value);
        spRecipeType = findViewById(R.id.sp_recipe_type);
        etRecipeTags = findViewById(R.id.et_recipe_tags);
        btnCreateRecipe = findViewById(R.id.btn_create_recipe);

        // Set up Spinner adapter
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.recipe_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRecipeType.setAdapter(adapter);

        // Set up button click listener
        btnCreateRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //createRecipe();
            }
        });
    }

    // Method to create new recipe
    private void createRecipe() {
        // Get input values
        String title = etRecipeTitle.getText().toString().trim();
        // Get and validate other input values
        // ...

        // Perform validation checks
        // ...

        // Create new recipe object with input values
        //Recipe newRecipe = new Recipe(title, image, ingredients, directions, time, servings, type, tags);

        // TODO: Add code to save new recipe to database or elsewhere

        // Display success message to user
        Toast.makeText(this, "New recipe created!", Toast.LENGTH_SHORT).show();

        // Finish activity
        finish();
    }
}