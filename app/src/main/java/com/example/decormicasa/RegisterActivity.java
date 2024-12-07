package com.example.decormicasa;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;
import com.google.mlkit.vision.common.InputImage;

import com.example.decormicasa.login.LoginActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class RegisterActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_PERMISSION_CODE = 101;
    private static final int CAMERA_PERMISSION_CODE = 102;
    // Definir las claves para las SharedPreferences
    private static final String PREFS_NAME = "UserPrefs";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";
    private EditText nameEditText, emailEditText, passwordEditText, addressEditText, phoneEditText;
    private Button registerButton, backButton, takePhotoButton;
    private ImageView profileImageView;  // Cambié el nombre aquí para coincidir con el ID en el XML

    private Bitmap userPhoto; // Para almacenar la foto tomada

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Inicializar vistas
        nameEditText = findViewById(R.id.name_edit_text);
        emailEditText = findViewById(R.id.email_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        addressEditText = findViewById(R.id.direction_edit_text);
        phoneEditText = findViewById(R.id.phone_edit_text);

        registerButton = findViewById(R.id.register_button);
        backButton = findViewById(R.id.back_button);
        takePhotoButton = findViewById(R.id.take_photo_button);
        profileImageView = findViewById(R.id.profile_image_view);  // Cambié el nombre aquí también

        // Solicitar permisos si son necesarios
        requestStoragePermissions();
        requestCameraPermissions();

        // Botón para tomar foto
        takePhotoButton.setOnClickListener(v -> openCamera());

        // Botón para registrarse
        registerButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String address = addressEditText.getText().toString().trim();
            String phone = phoneEditText.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "El correo y la contraseña son obligatorios", Toast.LENGTH_SHORT).show();
            } else {
                // Guardar en SharedPreferences
                SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(KEY_EMAIL, email);
                editor.putString(KEY_PASSWORD, password);
                editor.apply(); // Guardar los cambios
                saveImageToLocal(userPhoto); // Guardar en almacenamiento privado
                saveImageToMediaStore(userPhoto); // Guardar en galería
                Toast.makeText(this, "Registro completado exitosamente", Toast.LENGTH_SHORT).show();
            }
        });

        // Botón para volver
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
        // Solicitar permisos si son necesarios
        requestStoragePermissions();
        requestCameraPermissions();

        // Botón para tomar foto
        takePhotoButton.setOnClickListener(v -> openCamera());
        
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null && data.getExtras() != null) {
                userPhoto = (Bitmap) data.getExtras().get("data");
                profileImageView.setImageBitmap(userPhoto); // Mostrar la imagen en el ImageView
            }
        }
    }

    private void saveImageToLocal(Bitmap bitmap) {
        // Guardar la imagen
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (storageDir != null && !storageDir.exists()) {
            storageDir.mkdirs();
        }

        String fileName = "user_photo_" + System.currentTimeMillis() + ".jpg";
        File imageFile = new File(storageDir, fileName);

        try (FileOutputStream fos = new FileOutputStream(imageFile)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            // Detectar la cara y guardar las características faciales
            detectFace(bitmap);
            Toast.makeText(this, "Imagen guardada en: " + imageFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al guardar la imagen", Toast.LENGTH_SHORT).show();
        }
    }
    private void detectFace(Bitmap bitmap) {
        InputImage image = InputImage.fromBitmap(bitmap, 0);

        FaceDetectorOptions options = new FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                .build();

        FaceDetector detector = FaceDetection.getClient(options);

        Task<List<Face>> result = detector.process(image)
                .addOnSuccessListener(faces -> {
                    if (!faces.isEmpty()) {
                        Face face = faces.get(0);
                        // Guardar las características faciales
                        float leftEyeOpenProb = face.getLeftEyeOpenProbability();
                        float rightEyeOpenProb = face.getRightEyeOpenProbability();
                        // Aquí puedes guardar estos valores en una base de datos o archivo
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error al detectar cara", Toast.LENGTH_SHORT).show());
    }

    private void saveImageToMediaStore(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentResolver resolver = getContentResolver();
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DISPLAY_NAME, "user_photo_" + System.currentTimeMillis() + ".jpg");
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/MyApp");

            try {
                Uri uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                if (uri != null) {
                    try (OutputStream out = resolver.openOutputStream(uri)) {
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                        Toast.makeText(this, "Imagen guardada en la galería", Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error al guardar la imagen", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void requestStoragePermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        }
    }

    private void requestCameraPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        }
    }
}
