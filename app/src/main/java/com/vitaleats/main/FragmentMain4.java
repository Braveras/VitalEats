package com.vitaleats.main;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.vitaleats.utilities.User;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.vitaleats.R;
import com.vitaleats.login.MainActivity;
import com.vitaleats.utilities.StorageUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FragmentMain4 extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;
    private Button signout_btn;
    private ImageView profilePicture;
    private ImageButton editStatus, ic_info;
    private StorageReference mStorageRef;
    private FirebaseAuth firebaseAuth;
    private String currentUserId;
    private FirebaseUser user;
    private TextView userTextView, mailTextView, userStatus;
    Map<String, String> userInfo;

    private EditText editTextName, editTextAge, editTextWeight, editTextHeight;
    private Button buttonSave;
    private DatabaseReference databaseReference;
    private FirebaseStorage firebaseStorage;
    private Uri imageUri;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main4, container, false);
        signout_btn = view.findViewById(R.id.signout_btn);
        profilePicture = view.findViewById(R.id.profilePicture);
        userTextView = view.findViewById(R.id.username_profile);
        mailTextView = view.findViewById(R.id.mailTextView);
        userStatus = view.findViewById(R.id.status);
        editStatus = view.findViewById(R.id.edit_status);
        ic_info = view.findViewById(R.id.ic_info);
        mStorageRef = FirebaseStorage.getInstance().getReference();

        editTextName = view.findViewById(R.id.editTextName);
        editTextAge = view.findViewById(R.id.editTextAge);
        editTextWeight = view.findViewById(R.id.editTextWeight);
        editTextHeight = view.findViewById(R.id.editTextHeight);
        buttonSave = view.findViewById(R.id.buttonSave);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        firebaseStorage = FirebaseStorage.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        currentUserId = user.getUid();
        StorageUtil.OnReadUserInformationListener listener = userInformation -> {
            userInfo = userInformation;
            userStatus.setText(userInfo.get("status"));
        };

        StorageUtil.readStorageUser(user, listener);

        userTextView.setText(user.getDisplayName());
        mailTextView.setText(user.getEmail());


        Glide.with(this).load(user.getPhotoUrl()).into(profilePicture);

        editStatus.setOnClickListener(view12 -> {
            View edit_status = getLayoutInflater().inflate(R.layout.edit_status, null);
            BottomSheetDialog dialog = new BottomSheetDialog(getActivity());
            dialog.setContentView(edit_status);
            dialog.show();

            Button saveStatusButton = edit_status.findViewById(R.id.save_status_button);
            saveStatusButton.setOnClickListener(view13 -> {
                EditText inputStatus = edit_status.findViewById(R.id.status_edittext);
                String status = inputStatus.getText().toString();

                // Perform any necessary actions with the entered status, such as saving it to a database or updating a profile
                // ...
                Map<String, String> updatedUserInformation = new HashMap<>();
                updatedUserInformation.put("status", status);
                StorageUtil.updateStorageUser(user, updatedUserInformation);
                userStatus.setText(status);
                // Dismiss the bottom sheet after the status has been saved
                dialog.dismiss();
            });
        });

        signout_btn.setOnClickListener(v -> {
            // Sign-out from firebase
            FirebaseAuth.getInstance().signOut();
            // Start login activity
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
            Toast.makeText(getActivity(), getString(R.string.closedSession), Toast.LENGTH_LONG).show();
            // Set default values upon log-out
            profilePicture.setImageBitmap(null);
            userTextView.setText("");
            getActivity().finish();
        });

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

        ic_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent Bmeters = new Intent(getActivity(), Info.class);
                startActivity(Bmeters);
            }
        });

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUserData();
            }
        });
        loadUserData();

        return view;
    }

    private ContentResolver getContentResolver() {
        return this.getContext().getContentResolver();
    }

    private void selectImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Seleccionar Foto"), PICK_IMAGE_REQUEST);
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
                        Toast.makeText(getActivity(), "Error al subir imagen", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                // Mostrar un mensaje de error o hacer algo si el usuario no seleccionó ninguna imagen
                Toast.makeText(getActivity(), "Ninguna imagen seleccionada", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updateUserProfileWithImageUri(@Nullable Uri imageUri) {
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setPhotoUri(imageUri).build();

        firebaseAuth.getCurrentUser().updateProfile(profileUpdates).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "User profile updated.");
                Toast.makeText(getActivity(), "Foto de perfil actualizada", Toast.LENGTH_SHORT).show();
            } else {
                Log.e(TAG, "Failed to update user profile.", task.getException());
                Toast.makeText(getActivity(), "Error al actualizar perfil", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveUserData() {

        // Comprobar si todos los campos están rellenos
        if (TextUtils.isEmpty(editTextName.getText().toString().trim())) {
            editTextName.setError(getString(R.string.fields));
            editTextName.requestFocus();
            return;
        } else if (TextUtils.isEmpty(editTextAge.getText().toString().trim())) {
            editTextAge.setError(getString(R.string.fields));
            editTextAge.requestFocus();
            return;
        } else if (TextUtils.isEmpty(editTextWeight.getText().toString().trim())) {
            editTextWeight.setError(getString(R.string.fields));
            editTextWeight.requestFocus();
            return;
        } else if (TextUtils.isEmpty(editTextHeight.getText().toString().trim())) {
            editTextHeight.setError(getString(R.string.fields));
            editTextHeight.requestFocus();
            return;
        }

        // Obtener los valores de los campos
        String name = editTextName.getText().toString().trim();
        int age, weight, height;
        try {
            age = Integer.parseInt(editTextAge.getText().toString().trim());
            weight = Integer.parseInt(editTextWeight.getText().toString().trim());
            height = Integer.parseInt(editTextHeight.getText().toString().trim());
        } catch (NumberFormatException e) {
            // Mostrar un mensaje de error si los campos no son números
            if (!TextUtils.isDigitsOnly(editTextAge.getText().toString().trim())) {
                editTextAge.setError(getString(R.string.userAge));
                editTextAge.requestFocus();
            }
            if (!TextUtils.isDigitsOnly(editTextWeight.getText().toString().trim())) {
                editTextWeight.setError(getString(R.string.userWeight));
                editTextWeight.requestFocus();
            }
            if (!TextUtils.isDigitsOnly(editTextHeight.getText().toString().trim())) {
                editTextHeight.setError(getString(R.string.userHeight));
                editTextHeight.requestFocus();
            }
            return;
        }

        // Comprobar si el nombre es válido (solo letras y espacios)
        if (!name.matches("[a-zA-Z\\s]+")) {
            editTextName.setError(getString(R.string.userName));
            editTextName.requestFocus();
            return;
        }

        User user = new User(name, age, weight, height);

        String userId = firebaseAuth.getCurrentUser().getUid();

        // Guardar el objeto User en Firebase
        databaseReference.child(userId).setValue(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getContext(), R.string.userData, Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void loadUserData() {
        String userId = firebaseAuth.getCurrentUser().getUid();

        databaseReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    editTextName.setText(user.getName());
                    editTextAge.setText(String.valueOf(user.getAge()));
                    editTextWeight.setText(String.valueOf(user.getWeight()));
                    editTextHeight.setText(String.valueOf(user.getHeight()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

}