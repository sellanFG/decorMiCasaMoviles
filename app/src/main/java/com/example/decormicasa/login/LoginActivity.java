package com.example.decormicasa.login;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.decormicasa.MainActivity;
import com.example.decormicasa.R;
import com.example.decormicasa.RegisterActivity;
import com.google.android.material.button.MaterialButton;

public class LoginActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST_CODE = 100;

    private MaterialButton capturePhotoButton, nextButton, cancelButton;
    private Bitmap capturedPhoto; // Foto tomada para comparación

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Referencias a los botones del diseño original
        capturePhotoButton = findViewById(R.id.capture_photo_button); // Botón "Tomar Foto"
        nextButton = findViewById(R.id.next_button); // Botón "Iniciar sesión"
        cancelButton = findViewById(R.id.cancel_button); // Botón "Registrar"

        // Configurar acciones de los botones
        capturePhotoButton.setOnClickListener(v -> openCamera());
        nextButton.setOnClickListener(v -> login());
        cancelButton.setOnClickListener(v -> goToRegister());
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
        } else {
            Toast.makeText(this, "No se pudo abrir la cámara", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            capturedPhoto = (Bitmap) data.getExtras().get("data");
            Toast.makeText(this, "Foto capturada. Intenta iniciar sesión", Toast.LENGTH_SHORT).show();
        }
    }

    private void login() {
        if (capturedPhoto == null) {
            Toast.makeText(this, "Debes tomar una foto para iniciar sesión", Toast.LENGTH_SHORT).show();
            return;
        }

        // Comparar la foto capturada con las almacenadas
        boolean photoMatches = compareWithStoredPhotos(capturedPhoto);

        if (photoMatches) {
            Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            Toast.makeText(this, "No se encontró coincidencia", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean compareWithStoredPhotos(Bitmap photo) {
        // Implementa aquí la lógica de comparación
        // Retorna true si hay coincidencia, false en caso contrario
        return false; // Placeholder: reemplazar con la lógica real
    }

    private void goToRegister() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
        finish();
    }
}
