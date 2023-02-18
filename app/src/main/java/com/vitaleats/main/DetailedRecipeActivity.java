package com.vitaleats.main;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.vitaleats.R;
import com.vitaleats.utilities.Recipe;

import java.util.HashMap;
import java.util.Map;

public class DetailedRecipeActivity extends AppCompatActivity {

    private Recipe mRecipe;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference votesRef = db.collection("votes");

    private TextView recipeTitle, recipeIngredients, recipeElaboration;
    private RatingBar ratingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipe_big_view);

        recipeTitle = findViewById(R.id.tvRecipeTitle);
        ratingBar = findViewById(R.id.recipe_rating);


        // Obtener el objeto Recipe enviado desde la actividad anterior
        mRecipe = getIntent().getParcelableExtra("recipe");

        recipeTitle.setText(mRecipe.getRecipeTitle());
        ratingBar.setRating(mRecipe.getRating());


        ratingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            if (fromUser) {
                mostrarConfirmacion(rating);
            }
        });

    }

    private void mostrarConfirmacion(float rating) {
        String recipeId = mRecipe.getRef().getId();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Consultar si el usuario ya ha votado esta receta
        Query query = votesRef.whereEqualTo("recipeId", recipeId)
                .whereEqualTo("userId", userId);
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot result = task.getResult();
                if (result != null && !result.isEmpty()) {
                    // Si ya existe un voto, mostrar mensaje de error
                    Toast.makeText(this, "Ya has votado esta receta", Toast.LENGTH_SHORT).show();
                } else {
                    // Si no existe un voto, mostrar la confirmación al usuario
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("¿Quieres valorar la receta con " + rating + " estrellas?")
                            .setCancelable(false)
                            .setPositiveButton("Sí", (dialog, id) -> guardarValoracion(rating))
                            .setNegativeButton("No", (dialog, id) -> dialog.cancel());
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            } else {
                Toast.makeText(this, "Error al consultar votos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void guardarValoracion(float valoracion) {
        // Obtenemos la referencia a la receta y el usuario actual
        DocumentReference ref = mRecipe.getRef();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        // Incrementamos el contador de votos en firebase
        ref.update("voteCounter", FieldValue.increment(1));

        // Calcular y actualizar la valoración promedio
        ref.get().addOnSuccessListener(documentSnapshot -> {
            int voteCounter = documentSnapshot.getLong("voteCounter").intValue();
            float oldRating = documentSnapshot.getDouble("rating").floatValue();
            float newRating = (oldRating * (voteCounter - 1) + valoracion) / voteCounter;
            ref.update("rating", newRating);
            ratingBar.setRating(newRating);
        });

        // Registrar el voto del usuario actual en la colección "votes"
        Map<String, Object> voteData = new HashMap<>();
        voteData.put("recipeId", ref.getId());
        voteData.put("userId", userId);
        voteData.put("rating", valoracion);
        votesRef.add(voteData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Voto registrado correctamente", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al registrar voto", Toast.LENGTH_SHORT).show();
                });
    }

}