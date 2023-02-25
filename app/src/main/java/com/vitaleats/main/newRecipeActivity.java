package com.vitaleats.main;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.vitaleats.R;
import com.vitaleats.utilities.Recipe;

import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class newRecipeActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageButton selectedImageButton;

    private String selectedRecipeType, recipeTitle, recipeIngredients, recipeElaboration;

    ImageButton increase_time, increase_servings, decrease_time, decrease_servings, recipe_imageButton1, recipe_imageButton2, recipe_imageButton3;
    EditText etRecipeTitle;
    EditText etRecipeIngredients;
    EditText etRecipeDirections;
    TextView tvRecipeTime;
    TextView tvRecipeServings;
    TextView tvTimeModifier;
    private int time_modifier_value = 5;
    private int recipeTime = 0;
    private boolean isRange = false;
    private int servings = 1;
    ChipGroup chipGroup;
    Chip chipHighProtein, chipLowFat, chipGlutenFree, chipLactoseFree;
    SwitchMaterial switch_public_private;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_recipe);

        etRecipeTitle = findViewById(R.id.et_title);
        recipe_imageButton1 = findViewById(R.id.recipe_imageButton1);
        recipe_imageButton2 = findViewById(R.id.recipe_imageButton2);
        recipe_imageButton3 = findViewById(R.id.recipe_imageButton3);
        etRecipeIngredients = findViewById(R.id.et_ingredients);
        etRecipeDirections = findViewById(R.id.et_elaboration);
        tvRecipeTime = findViewById(R.id.tv_time_value);
        tvTimeModifier = findViewById(R.id.time_amount);
        tvRecipeServings = findViewById(R.id.tv_num_people_value);
        decrease_time = findViewById(R.id.btn_decrease_time);
        decrease_servings = findViewById(R.id.btn_decrease_servings);
        increase_time = findViewById(R.id.btn_increase_time);
        increase_servings = findViewById(R.id.btn_increase_servings);
        Spinner spRecipeType = findViewById(R.id.sp_recipe_type);
        chipGroup = findViewById(R.id.chip_group);
        Button btnCreateRecipe = findViewById(R.id.btn_create_recipe);
        switch_public_private = findViewById(R.id.switch_public_private);

        recipe_imageButton1.setOnClickListener(view -> {
            selectedImageButton = recipe_imageButton1;
            openGallery();
        });
        recipe_imageButton2.setOnClickListener(view -> {
            selectedImageButton = recipe_imageButton2;
            openGallery();
        });
        recipe_imageButton3.setOnClickListener(view -> {
            selectedImageButton = recipe_imageButton3;
            openGallery();
        });

        chipHighProtein = findViewById(R.id.chip_high_protein);
        chipHighProtein.setChipBackgroundColorResource(R.color.chip_background_high_protein_unselected);
        chipHighProtein.setChipStrokeColorResource(R.color.chip_stroke_high_protein_unselected);
        chipHighProtein.setChipStrokeWidthResource(R.dimen.chip_stroke_width);
        chipHighProtein.setTextColor(ContextCompat.getColor(this, R.color.chip_text_high_protein_unselected));
        chipHighProtein.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                chipHighProtein.setChipBackgroundColorResource(R.color.chip_background_high_protein_selected);
                chipHighProtein.setChipStrokeColorResource(R.color.chip_stroke_high_protein_selected);
                chipHighProtein.setTextColor(ContextCompat.getColor(this, R.color.chip_text_high_protein_selected));
            } else {
                chipHighProtein.setChipBackgroundColorResource(R.color.chip_background_high_protein_unselected);
                chipHighProtein.setChipStrokeColorResource(R.color.chip_stroke_high_protein_unselected);
                chipHighProtein.setTextColor(ContextCompat.getColor(this, R.color.chip_text_high_protein_unselected));
            }
        });

        chipLowFat = findViewById(R.id.chip_low_fat);
        chipLowFat.setChipBackgroundColorResource(R.color.chip_background_low_fat_unselected);
        chipLowFat.setChipStrokeColorResource(R.color.chip_stroke_low_fat_unselected);
        chipLowFat.setChipStrokeWidthResource(R.dimen.chip_stroke_width);
        chipLowFat.setTextColor(ContextCompat.getColor(this, R.color.chip_text_low_fat_unselected));
        chipLowFat.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                chipLowFat.setChipBackgroundColorResource(R.color.chip_background_low_fat_selected);
                chipLowFat.setChipStrokeColorResource(R.color.chip_stroke_low_fat_selected);
                chipLowFat.setTextColor(ContextCompat.getColor(this, R.color.chip_text_low_fat_selected));
            } else {
                chipLowFat.setChipBackgroundColorResource(R.color.chip_background_low_fat_unselected);
                chipLowFat.setChipStrokeColorResource(R.color.chip_stroke_low_fat_unselected);
                chipLowFat.setTextColor(ContextCompat.getColor(this, R.color.chip_text_low_fat_unselected));
            }
        });

        chipGlutenFree = findViewById(R.id.chip_gluten_free);
        chipGlutenFree.setChipBackgroundColorResource(R.color.chip_background_gluten_free_unselected);
        chipGlutenFree.setChipStrokeColorResource(R.color.chip_stroke_gluten_free_unselected);
        chipGlutenFree.setChipStrokeWidthResource(R.dimen.chip_stroke_width);
        chipGlutenFree.setTextColor(ContextCompat.getColor(this, R.color.chip_text_gluten_free_unselected));
        chipGlutenFree.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                chipGlutenFree.setChipBackgroundColorResource(R.color.chip_background_gluten_free_selected);
                chipGlutenFree.setChipStrokeColorResource(R.color.chip_stroke_gluten_free_selected);
                chipGlutenFree.setTextColor(ContextCompat.getColor(this, R.color.chip_text_gluten_free_selected));
            } else {
                chipGlutenFree.setChipBackgroundColorResource(R.color.chip_background_gluten_free_unselected);
                chipGlutenFree.setChipStrokeColorResource(R.color.chip_stroke_gluten_free_unselected);
                chipGlutenFree.setTextColor(ContextCompat.getColor(this, R.color.chip_text_gluten_free_unselected));
            }
        });

        chipLactoseFree = findViewById(R.id.chip_lactose_free);
        chipLactoseFree.setChipBackgroundColorResource(R.color.chip_background_lactose_free_unselected);
        chipLactoseFree.setChipStrokeColorResource(R.color.chip_stroke_lactose_free_unselected);
        chipLactoseFree.setChipStrokeWidthResource(R.dimen.chip_stroke_width);
        chipLactoseFree.setTextColor(ContextCompat.getColor(this, R.color.chip_text_lactose_free_unselected));
        chipLactoseFree.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                chipLactoseFree.setChipBackgroundColorResource(R.color.chip_background_lactose_free_selected);
                chipLactoseFree.setChipStrokeColorResource(R.color.chip_stroke_lactose_free_selected);
                chipLactoseFree.setTextColor(ContextCompat.getColor(this, R.color.chip_text_lactose_free_selected));
            } else {
                chipLactoseFree.setChipBackgroundColorResource(R.color.chip_background_lactose_free_unselected);
                chipLactoseFree.setChipStrokeColorResource(R.color.chip_stroke_lactose_free_unselected);
                chipLactoseFree.setTextColor(ContextCompat.getColor(this, R.color.chip_text_lactose_free_unselected));
            }
        });


        tvRecipeTime.setText(recipeTime + " " + getString(R.string.time_value));
        tvTimeModifier.setText("±" + time_modifier_value + " " + getString(R.string.time_value));
        tvRecipeServings.setText(servings + " " + getString(R.string.num_person_value));

        ConstraintLayout time_modifier = findViewById(R.id.time_layout_clickable);
        time_modifier.setOnClickListener(v -> {
            if (time_modifier_value == 5) {
                time_modifier_value = 10;
            } else if (time_modifier_value == 10) {
                time_modifier_value = 30;
            } else {
                time_modifier_value = 5;
            }
            tvTimeModifier.setText("±" + time_modifier_value + " " + getString(R.string.time_value));

        });

        increase_time.setOnClickListener(v -> {
            recipeTime += time_modifier_value;
            if (recipeTime >= 60) {
                int[] hoursAndMinutes = getHoursAndMinutes(recipeTime);
                tvRecipeTime.setText(hoursAndMinutes[0] + " h " + hoursAndMinutes[1] + " min");
            } else {
                tvRecipeTime.setText(recipeTime + " " + getString(R.string.time_value));
            }
        });

        decrease_time.setOnClickListener(v -> {
            if (recipeTime > 0) {
                recipeTime -= time_modifier_value;
                recipeTime = Math.max(recipeTime, 0); //establecer el tiempo total a 0 si ha bajado de 0 por los modificadores
                if (recipeTime >= 60) {
                    int[] hoursAndMinutes = getHoursAndMinutes(recipeTime);
                    tvRecipeTime.setText(hoursAndMinutes[0] + " h " + hoursAndMinutes[1] + " min");
                } else {
                    tvRecipeTime.setText(recipeTime + " " + getString(R.string.time_value));
                }
            }
        });

        ConstraintLayout servings_modifier = findViewById(R.id.servings_layout_clickable);
        servings_modifier.setOnClickListener(v -> {
            isRange = !isRange;
            if (isRange) {
                if (servings == 9) servings = 8;
                tvRecipeServings.setText(servings + " - " + (servings + 1) + " " + getResources().getString(R.string.num_people_value));
            } else {
                if (servings == 1) {
                    tvRecipeServings.setText(servings + " " + getResources().getString(R.string.num_person_value));
                } else {
                    tvRecipeServings.setText(servings + " " + getResources().getString(R.string.num_people_value));
                }
            }
        });

        increase_servings.setOnClickListener(v -> {
            if (isRange) {
                if (servings < 8) {
                    servings += 1;
                    tvRecipeServings.setText(servings + " - " + (servings + 1) + " " + getResources().getString(R.string.num_people_value));
                }
            } else {
                if (servings < 9) {
                    servings += 1;
                    if (servings == 1) {
                        tvRecipeServings.setText(servings + " " + getResources().getString(R.string.num_person_value));
                    } else {
                        tvRecipeServings.setText(servings + " " + getResources().getString(R.string.num_people_value));
                    }
                }
            }
        });

        decrease_servings.setOnClickListener(v -> {
            if (isRange) {
                if (servings > 1) {
                    servings -= 1;
                    tvRecipeServings.setText(servings + " - " + (servings + 1) + " " + getResources().getString(R.string.num_people_value));
                }
            } else {
                if (servings > 1) {
                    servings -= 1;
                    if (servings == 1) {
                        tvRecipeServings.setText(servings + " " + getResources().getString(R.string.num_person_value));
                    } else {
                        tvRecipeServings.setText(servings + " " + getResources().getString(R.string.num_people_value));
                    }
                }
            }
        });

        /* //Suponiendo que hay un boton para guardar el estado, sacar una lista de los seleccionados
        Button saveButton = findViewById(R.id.save_button);
        saveButton.setOnClickListener(v -> {
            List<String> selectedTags = new ArrayList<>();
            int numChips = chipGroup.getChildCount();
            for (int i = 0; i < numChips; i++) {
                Chip chip = (Chip) chipGroup.getChildAt(i);
                if (chip.isChecked()) {
                    selectedTags.add(chip.getText().toString());
                }
            }
            // Hacer algo con las tags seleccionadas
        });

         */

        // Set up Spinner adapter
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.recipe_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRecipeType.setAdapter(adapter);

        // Add OnItemSelectedListener to Spinner
        spRecipeType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // Get the selected item from the Spinner
                selectedRecipeType = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        // Set up button click listener
        btnCreateRecipe.setOnClickListener(view -> {
            btnCreateRecipe.setEnabled(false);
            recipeTitle = String.valueOf(etRecipeTitle.getText());
            recipeIngredients = String.valueOf(etRecipeIngredients.getText());
            recipeElaboration = String.valueOf(etRecipeDirections.getText());
            if (!isDefaultImage(recipe_imageButton1) || !isDefaultImage(recipe_imageButton2) || !isDefaultImage(recipe_imageButton3)) {
                if (!recipeTitle.isEmpty() || !recipeIngredients.isEmpty() || !recipeElaboration.isEmpty()) {
                    if (recipeTime != 0) {
                        if (!Objects.equals(selectedRecipeType, "-")) {
                            createRecipe();
                        }
                    }
                }
            } else {
                Toast.makeText(newRecipeActivity.this, getString(R.string.create_recipe_error_emptyfields), Toast.LENGTH_LONG).show();
                btnCreateRecipe.setEnabled(true);
            }
        });
    }

    public static int[] getHoursAndMinutes(int minutes) {
        int[] result = new int[2];
        int hours = minutes / 60;
        int remainingMinutes = minutes % 60;

        result[0] = hours;
        result[1] = remainingMinutes;
        return result;
    }

    // Método que se llama cuando se presiona un ImageButton
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    // Método que se llama cuando el usuario selecciona una imagen de la galería
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();
            selectedImageButton.setImageURI(selectedImageUri);
        }
    }

    private boolean isDefaultImage(ImageButton imageButton) {
        Drawable currentDrawable = imageButton.getDrawable();
        Drawable defaultDrawable = getResources().getDrawable(R.drawable.ic_add_image);
        return currentDrawable.getConstantState().equals(defaultDrawable.getConstantState());
    }

    private void createRecipe() {
        //selectedRecipeType

        recipeTitle = etRecipeTitle.getText().toString().trim();
        recipeIngredients = etRecipeIngredients.getText().toString().trim();
        recipeElaboration = etRecipeDirections.getText().toString().trim();
        byte[] recipe_image1 = null;
        byte[] recipe_image2 = null;
        byte[] recipe_image3 = null;
        final String[] image1 = new String[1];
        final String[] image2 = new String[1];
        final String[] image3 = new String[1];

        List<String> selectedTags = new ArrayList<>();
        GridLayout gridLayout = (GridLayout) chipGroup.getChildAt(0);
        for (int i = 0; i < gridLayout.getChildCount(); i++) {
            View child = gridLayout.getChildAt(i);
            if (child instanceof Chip) {
                Chip chip = (Chip) child;
                if (chip.isChecked()) {
                    selectedTags.add(chip.getText().toString());
                }
            }
        }

        List<byte[]> imageBytesList = new ArrayList<>();
        if (!isDefaultImage(recipe_imageButton1)) {
            Drawable drawable = recipe_imageButton1.getDrawable();
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            recipe_image1 = bitmapToPng(bitmap);
            imageBytesList.add(recipe_image1);
        }

        if (!isDefaultImage(recipe_imageButton2)) {
            Drawable drawable = recipe_imageButton2.getDrawable();
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            recipe_image2 = bitmapToPng(bitmap);
            imageBytesList.add(recipe_image2);
        }

        if (!isDefaultImage(recipe_imageButton3)) {
            Drawable drawable = recipe_imageButton3.getDrawable();
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            recipe_image3 = bitmapToPng(bitmap);
            imageBytesList.add(recipe_image3);
        }

        byte[][] imageBytes = new byte[imageBytesList.size()][];
        for (int i = 0; i < imageBytesList.size(); i++) {
            imageBytes[i] = imageBytesList.get(i);
        }

        String recipeServingsStr;
        if (isRange) {
            recipeServingsStr = servings + " - " + (servings+1);
        } else {
            recipeServingsStr = Integer.toString(servings);
        }

        Task<String> uploadTask = uploadImages(imageBytes);
        uploadTask.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<String> imageUrlList = Arrays.asList(task.getResult().split(","));

                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                FirebaseUser user = firebaseAuth.getCurrentUser();
                String currentUserId = user.getUid();

                // Creamos una instancia de la clase Recipe
                Recipe recipe = new Recipe(recipeTitle, recipeIngredients, recipeElaboration, recipeServingsStr, tvRecipeTime.getText().toString(), selectedRecipeType, selectedTags, imageUrlList, currentUserId, user.getDisplayName(), 0.0f, 0, new Date(), !switch_public_private.isChecked());

                // Subimos la receta a Firebase Firestore
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                CollectionReference recipesRef = db.collection("recipes");
                recipesRef.add(recipe).addOnSuccessListener(documentReference -> Toast.makeText(this, "New recipe created!", Toast.LENGTH_SHORT).show())//documentReference.getId() para obtener el ID del objeto subido
                        .addOnFailureListener(e -> Toast.makeText(this, "Error creating recipe!", Toast.LENGTH_SHORT).show());
                // Finish activity
                finish();
            } else {
                Toast.makeText(this, "Error al crear receta (error de imagenes)", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Failed to upload images: ", task.getException());
            }
        });
    }

    private Task<String> uploadImages(byte[][] imageBytes) {
        int numImages = imageBytes.length;
        TaskCompletionSource<String> taskCompletionSource = new TaskCompletionSource<>();

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        String currentUserId = user.getUid();

        // Arrays para almacenar la información de cada imagen
        String[] imageNames = new String[numImages];
        StorageReference[] imagesRefs = new StorageReference[numImages];
        Task<Uri>[] urlTasks = new Task[numImages];

        for (int i = 0; i < numImages; i++) {
            // Generar un nombre único para cada imagen
            String imageuid = UUID.randomUUID().toString();
            imageNames[i] = imageuid + ".png";

            // Subir la imagen a Firebase Storage
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            imagesRefs[i] = storageRef.child("documents/users/" + currentUserId + "/recipes_images/" + imageNames[i]);
            UploadTask uploadTask = imagesRefs[i].putBytes(imageBytes[i]);

            int finalI = i;
            urlTasks[i] = uploadTask.continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return imagesRefs[finalI].getDownloadUrl();
            });
        }

        // Combinar todas las tareas en una sola tarea
        Tasks.whenAllComplete(urlTasks).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                StringBuilder imageUrlBuilder = new StringBuilder();
                for (Task<Uri> urlTask : urlTasks) {
                    if (urlTask.isSuccessful()) {
                        Uri downloadUri = urlTask.getResult();
                        if (downloadUri != null) {
                            imageUrlBuilder.append(downloadUri.toString());
                            imageUrlBuilder.append(",");
                        }
                    } else {
                        taskCompletionSource.setException(urlTask.getException());
                        return;
                    }
                }
                // Devolver una cadena de texto con todas las URLs de las imágenes
                String imageUrlString = imageUrlBuilder.toString();
                if (imageUrlString.length() > 0) {
                    imageUrlString = imageUrlString.substring(0, imageUrlString.length() - 1);
                }
                taskCompletionSource.setResult(imageUrlString);
            } else {
                taskCompletionSource.setException(task.getException());
            }
        });

        return taskCompletionSource.getTask();
    }

    private byte[] bitmapToPng(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }
}