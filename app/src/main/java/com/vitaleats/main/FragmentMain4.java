package com.vitaleats.main;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;

import static android.app.Activity.RESULT_OK;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUserMetadata;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.vitaleats.R;
import com.vitaleats.login.MainActivity;
import com.vitaleats.utilities.StorageUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FragmentMain4 extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView profilePicture;
    private ImageButton editStatus, signout_btn, about_btn;
    private StorageReference mStorageRef;
    private FirebaseAuth firebaseAuth;
    FirebaseUserMetadata userMetadata;
    private String currentUserId;
    private FirebaseUser user;
    private TextView userTextView, mailTextView, userStatus, recipe_counter, profile_creation_date, icm_result_value, icm_height, icm_weight;

    private FirebaseStorage firebaseStorage;
    private Uri imageUri;
    private LinearLayout icm_result;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main4, container, false);

        signout_btn = view.findViewById(R.id.signout_btn);
        about_btn = view.findViewById(R.id.about_btn);
        profilePicture = view.findViewById(R.id.profilePicture);
        userTextView = view.findViewById(R.id.username_profile);
        mailTextView = view.findViewById(R.id.mailTextView);
        userStatus = view.findViewById(R.id.status);
        editStatus = view.findViewById(R.id.edit_status);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        recipe_counter = view.findViewById(R.id.profile_recipe_counter);
        profile_creation_date = view.findViewById(R.id.profile_creation_date);
        icm_result = view.findViewById(R.id.icm_result);
        icm_result_value = view.findViewById(R.id.icm_calculation);
        icm_height = view.findViewById(R.id.icm_height);
        icm_weight = view.findViewById(R.id.icm_weight);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        user = firebaseAuth.getCurrentUser();
        userMetadata = user.getMetadata();
        currentUserId = user.getUid();

        // Cargar datos de JSON en firebase mediante listener con interfaz en StorageUtil
        StorageUtil.OnReadUserInformationListener listener = userInformation -> {
            userStatus.setText(userInformation.get("status"));
            icm_weight.setText(userInformation.get("peso"));
            icm_height.setText(userInformation.get("altura"));
        };
        StorageUtil.readStorageUser(user, listener);

        // Cargar datos del objeto user de firebase
        userTextView.setText(user.getDisplayName());
        mailTextView.setText(user.getEmail());
        Glide.with(this).load(user.getPhotoUrl()).into(profilePicture);

        // Cambiar foto de perfil
        profilePicture.setOnClickListener(view1 -> {
            PopupMenu popup = new PopupMenu(getContext(), profilePicture);
            popup.getMenuInflater().inflate(R.menu.updateordelete_profile_pic, popup.getMenu());
            popup.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.update:
                        // Code to update the picture
                        selectImageFromGallery();
                        return true;
                    case R.id.delete:
                        // Code to delete the picture
                        if (user.getPhotoUrl() == null) {
                            //Picture is already null, what u gonna delete, idiot?
                            Toast.makeText(getActivity(), "No hay ninguna foto!", Toast.LENGTH_SHORT).show();
                        } else {
                            profilePicture.setImageBitmap(null);
                            updateUserProfileWithImageUri(null);
                        }
                        return true;
                    default:
                        return false;
                }
            });
            popup.show();
        });

        // Cambiar estado
        editStatus.setOnClickListener(view12 -> {
            View edit_status = getLayoutInflater().inflate(R.layout.edit_status, null);
            BottomSheetDialog dialog = new BottomSheetDialog(getActivity());
            dialog.setContentView(edit_status);
            dialog.show();

            Button saveStatusButton = edit_status.findViewById(R.id.save_status_button);
            saveStatusButton.setOnClickListener(view13 -> {
                EditText inputStatus = edit_status.findViewById(R.id.status_edittext);
                String status = inputStatus.getText().toString();

                // Saving it to a database & updating profile
                Map<String, String> updatedUserInformation = new HashMap<>();
                updatedUserInformation.put("status", status);
                StorageUtil.updateStorageUser(user, updatedUserInformation);
                userStatus.setText(status);
                // Dismiss the bottom sheet after the status has been saved
                dialog.dismiss();
            });
        });

        // Contar las recetas del usuario
        Query query = db.collectionGroup("recipes").whereEqualTo("creatorUid", currentUserId);
        query.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int count = queryDocumentSnapshots.size();
                    recipe_counter.setText(String.valueOf(count));
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error al obtener las recetas del usuario", e));

        // Obtener y formatear fecha de creación del usuario
        Date creationDate = new Date(userMetadata.getCreationTimestamp());
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yy");
        String formattedDate = formatter.format(creationDate);
        profile_creation_date.setText(formattedDate);

        //Calcular IMC
        icm_result.setOnClickListener(v -> {
            // Comprobar que hay valores en los campos
            if (icm_weight.getText().toString().trim().isEmpty() || icm_height.getText().toString().trim().isEmpty()) {
                Toast.makeText(getActivity(), getString(R.string.icm_fill_all), Toast.LENGTH_LONG).show();
            } else {
                // Guardar valores en la información del usuario en firebase
                Map<String, String> valuesMap = new HashMap<>();
                valuesMap.put("peso", icm_weight.getText().toString().trim());
                valuesMap.put("altura", icm_height.getText().toString().trim());
                StorageUtil.updateStorageUser(user, valuesMap);
                // Convertir strings a double y calcular
                icm_result_value.setText(String.valueOf(calcularIMC(Double.parseDouble(icm_weight.getText().toString().trim()), Double.parseDouble(icm_height.getText().toString().trim()))));
            }
        });

        // Abrir actividad informativa (about)
        about_btn.setOnClickListener(v -> {
            Intent Bmeters = new Intent(getActivity(), Info.class);
            startActivity(Bmeters);
        });

        // Sign out button
        signout_btn.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(getString(R.string.sign_out));
            builder.setMessage(getString(R.string.sign_out_confirm));
            builder.setPositiveButton(getString(R.string.yes), (dialog, which) -> {
                // Sign-out from firebase
                FirebaseAuth.getInstance().signOut();
                // Start login activity
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                Toast.makeText(getActivity(), getString(R.string.closedSession), Toast.LENGTH_LONG).show();
                // Set default values upon log-out
                profilePicture.setImageBitmap(null);
                userTextView.setText("");
                requireActivity().finish();
            });
            builder.setNegativeButton(getString(R.string.no), (dialog, which) -> {
                // User clicked "No", do nothing
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        });

        return view;
    }

    private ContentResolver getContentResolver() {
        return this.requireContext().getContentResolver();
    }

    private void selectImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_photo)), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST) {
            if (resultCode == RESULT_OK && data != null && data.getData() != null) {
                Uri selectedImageUri = data.getData();
                // Convertir la imagen a PNG
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] dataBytes = baos.toByteArray();

                // Subir la imagen a Firebase Storage
                StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                StorageReference imagesRef = storageRef.child("documents/users/" + currentUserId + "/profile_picture.png");
                UploadTask uploadTask = imagesRef.putBytes(dataBytes);
                Task<Uri> urlTask = uploadTask.continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return imagesRef.getDownloadUrl();
                }).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        // Guardar la referencia a la imagen en el objeto firebaseUser
                        updateUserProfileWithImageUri(downloadUri);
                        Glide.with(this).load(downloadUri).into(profilePicture);
                    } else {
                        // Mostrar un mensaje de error si no se puede subir la imagen a Firebase
                        Toast.makeText(getActivity(), getString(R.string.uploadImageError), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                // Mostrar un mensaje de error o hacer algo si el usuario no seleccionó ninguna imagen
                Toast.makeText(getActivity(), getString(R.string.no_image_selected), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updateUserProfileWithImageUri(@Nullable Uri imageUri) {
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setPhotoUri(imageUri).build();

        firebaseAuth.getCurrentUser().updateProfile(profileUpdates).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "User profile updated.");
                Toast.makeText(getActivity(), getString(R.string.user_updated), Toast.LENGTH_SHORT).show();
            } else {
                Log.e(TAG, "Failed to update user profile.", task.getException());
                Toast.makeText(getActivity(), getString(R.string.user_not_updated), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static double calcularIMC(double alturaCm, double pesoKg) {
        // Convertir altura a metros
        double alturaM = alturaCm / 100;

        // Calcular el IMC
        double imc = pesoKg / (alturaM * alturaM);

        return imc;
    }
}