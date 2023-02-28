package com.vitaleats.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.vitaleats.R;
import com.vitaleats.utilities.Recipe;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DetailedRecipeActivity extends AppCompatActivity {

    private Recipe mRecipe;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference votesRef = db.collection("votes");

    private TextView recipeTitle, recipeIngredients, recipeElaboration, tvRecipeServings, tvRecipeTime, tvRecipeCreatorUsername, tvRecipeCreatedAt, tvVoteCounter;
    private RatingBar ratingBar;
    private ViewPager mViewPager;
    private ChipGroup chipGroup;
    private Chip chip_1, chip_2, chip_3, chip_4;
    private View separador_chip;
    private ImageView servings_icon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipe_big_view);

        // Obtener el objeto Recipe enviado desde la actividad anterior
        mRecipe = getIntent().getParcelableExtra("recipe");

        recipeTitle = findViewById(R.id.tvRecipeTitle);
        ratingBar = findViewById(R.id.recipe_rating);
        recipeIngredients = findViewById(R.id.tvRecipeIngredients);
        recipeElaboration = findViewById(R.id.tvRecipeElaboration);
        tvRecipeServings = findViewById(R.id.tvRecipeServings);
        tvRecipeTime = findViewById(R.id.tvRecipeTime);
        tvRecipeCreatorUsername = findViewById(R.id.tvRecipeCreatorUsername);
        tvRecipeCreatedAt = findViewById(R.id.tvRecipeCreatedAt);
        tvVoteCounter = findViewById(R.id.tvVoteCounter);
        mViewPager = findViewById(R.id.recipe_viewPager);
        servings_icon = findViewById(R.id.iv_servings_big_view);
        chipGroup = findViewById(R.id.chipGroup);
        chip_1 = findViewById(R.id.chip_1);
        chip_2 = findViewById(R.id.chip_2);
        chip_3 = findViewById(R.id.chip_3);
        chip_4 = findViewById(R.id.chip_4);
        separador_chip = findViewById(R.id.separador_chip);
        List<String> images = mRecipe.getImages();
        ViewPagerAdapter adapter = new ViewPagerAdapter(this, images);
        mViewPager.setAdapter(adapter);

        recipeTitle.setText(mRecipe.getRecipeTitle());

        // Checks para comprobar si es singular o plural, y poner persona o personas de forma correspondiente
        String servings = mRecipe.getTvRecipeServings();
        char lastChar = servings.charAt(servings.length() - 1);
        String servingsStr = (lastChar > '1')
                ? getApplicationContext().getString(R.string.num_people_value)
                : getApplicationContext().getString(R.string.num_person_value);
        int imageResId = (lastChar > '1')
                ? R.drawable.ic_people_newrecipe
                : R.drawable.ic_person_newrecipe;

        servings_icon.setImageResource(imageResId);
        tvRecipeServings.setText(servings + " " + servingsStr);

        recipeIngredients.setText(mRecipe.getRecipeIngredients());
        recipeElaboration.setText(mRecipe.getRecipeElaboration());
        tvRecipeTime.setText(mRecipe.getTvRecipeTime());
        // Verificar si la lista de etiquetas está vacía
        if (mRecipe.getTags() != null && mRecipe.getTags().size() > 0) {
            for (int i = 0; i < mRecipe.getTags().size(); i++) {
                String tag = mRecipe.getTags().get(i);

                if (i == 0) {
                    chip_1.setText(tag);
                    chip_1.setVisibility(View.VISIBLE);
                } else if (i == 1) {
                    chip_2.setText(tag);
                    chip_2.setVisibility(View.VISIBLE);
                } else if (i == 2) {
                    chip_3.setText(tag);
                    chip_3.setVisibility(View.VISIBLE);
                } else if (i == 3) {
                    chip_4.setText(tag);
                    chip_4.setVisibility(View.VISIBLE);
                }
            }
        } else {
            chipGroup.setVisibility(View.GONE);
            separador_chip.setVisibility(View.GONE);
        }
        ratingBar.setRating(mRecipe.getRating());
        tvRecipeCreatorUsername.setText(getString(R.string.created_by) + " " + mRecipe.getCreatorUsername());
        // Format date value into a proper date
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy HH:mm");
        String createdAtString = sdf.format(mRecipe.getCreatedAt());
        tvRecipeCreatedAt.setText(createdAtString);
        tvVoteCounter.setText("(" + mRecipe.getVoteCounter() + ")");

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

    public class ViewPagerAdapter extends PagerAdapter {

        private Context mContext;
        private List<String> mImages;

        public ViewPagerAdapter(Context context, List<String> images) {
            mContext = context;
            mImages = images;
        }

        @Override
        public int getCount() {
            return mImages.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            ImageView imageView = new ImageView(mContext);
            Glide.with(mContext)
                    .load(Uri.parse(mImages.get(position)))
                    .into(imageView);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            container.addView(imageView);
            return imageView;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((ImageView) object);
        }
    }

}