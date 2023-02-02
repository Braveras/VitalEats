package com.vitaleats.main;

import static android.app.Activity.RESULT_OK;

import static com.google.common.io.Files.getFileExtension;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.vitaleats.R;
import com.vitaleats.login.MainActivity;

import java.io.IOException;

public class FragmentMain4 extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;
    private Button cerrar, editar, eliminar;
    private ImageView imagen;
    private StorageReference mStorageRef;
    private Uri filePath;
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
            public void onClick(View view) {
                // Abrir galería para seleccionar una imagen
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Selecciona una imágen"), PICK_IMAGE_REQUEST);
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

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imagen.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("images/" + System.currentTimeMillis() + "." + getFileExtension(filePath.toString()));
        storageReference.putFile(filePath)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(getActivity(), "Imagen subida con éxito", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Falló la subida de la imagen", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}