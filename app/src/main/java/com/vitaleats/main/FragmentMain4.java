package com.vitaleats.main;

import static android.app.Activity.RESULT_OK;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.vitaleats.R;
import com.vitaleats.login.MainActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

public class FragmentMain4 extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1000;
    private static final int CAMERA_REQUEST = 1888;
    private Button cerrar, editar, eliminar;
    private ImageView imagen;
    private StorageReference mStorageRef;
    private String storage_path = "images/";
    private StorageReference storageReference;
    private ContentResolver contentResolver;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main4, container, false);
        cerrar = view.findViewById(R.id.cerrar);
        editar = view.findViewById(R.id.editar);
        eliminar = view.findViewById(R.id.eliminar);
        imagen = view.findViewById(R.id.imagen);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        contentResolver = getContentResolver();

        loadImage();

        cerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cerrar sesión en Firebase
                FirebaseAuth.getInstance().signOut();
                // Redirigir al usuario a la pantalla de inicio de sesión
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                Toast.makeText(getActivity(), getString(R.string.closedSession), Toast.LENGTH_LONG).show();
                getActivity().finish();
            }
        });

        editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CharSequence[] options = {"Tomar foto", "Elegir de galería"};
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Elige una opción");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (options[item].equals("Tomar foto")) {
                            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(cameraIntent, CAMERA_REQUEST);
                        } else if (options[item].equals("Elegir de galería")) {
                            Intent pickImageIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            pickImageIntent.setType("image/*");
                            startActivityForResult(pickImageIntent, PICK_IMAGE_REQUEST);
                        }
                    }
                });
                builder.show();
            }
        });

        eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener referencia a la ubicación de la imagen
                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("images").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                // Borrar la imagen
                storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getActivity(), "Imagen eliminada con éxito", Toast.LENGTH_SHORT).show();
                        imagen.setImageBitmap(null);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Error al eliminar la imagen", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        return view;
    }

    private ContentResolver getContentResolver() {
        return this.getContext().getContentResolver();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri selectedImage = data.getData();

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CAMERA_REQUEST:
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    uploadImageToFirebase(imageBitmap);
                    break;
                case PICK_IMAGE_REQUEST:
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                        uploadImageToFirebase(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

    private void saveProfilePicture(Bitmap imageBitmap) {
        try {
            FileOutputStream fos = getContext().openFileOutput("profile_picture.png", getContext().MODE_PRIVATE);
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadImage() {
        // Obtiene la ruta de la imagen guardada en el dispositivo
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        File imagePath = new File(getContext().getFilesDir(), "profile_picture.png");
        if (imagePath.exists()) {
            // Carga la imagen en el ImageView
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath.getAbsolutePath());
            imagen.setImageBitmap(bitmap);
        } else {
            if (user != null) {
                Bitmap bitmap = BitmapFactory.decodeFile(imagePath.getAbsolutePath());
                imagen.setImageBitmap(bitmap);
            } else {
                // Carga la imagen predeterminada
                imagen.setImageBitmap(null);
            }
        }
    }

    /*public void loadImage(String userId) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference imagesRef = storageRef.child("images/" + userId + "/profile_picture.png");

        imagesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getContext())
                        .load(uri)
                        .into(imagen);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
    }*/

    private void uploadImageToFirebase(Bitmap imageBitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("images/" + UUID.randomUUID().toString());
        storageReference.putBytes(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                // Imagen subida con éxito
                                Toast.makeText(getActivity(), "Imagen subida con éxito", Toast.LENGTH_SHORT).show();
                                Picasso.with(getActivity())
                                        .load(uri)
                                        .into(imagen);
                                saveProfilePicture(imageBitmap);
                                imagen.setImageBitmap(imageBitmap);
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Error al subir la imagen
                        Toast.makeText(getActivity(), "Error al subir la imagen", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}