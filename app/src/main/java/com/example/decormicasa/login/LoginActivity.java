package com.example.decormicasa.login;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.decormicasa.ClienteActivity;
import com.example.decormicasa.MainActivity;
import com.example.decormicasa.R;
import com.example.decormicasa.RegisterActivity;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;
import com.google.mlkit.vision.common.InputImage;
import android.content.SharedPreferences;
import java.io.File;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST_CODE = 100;

    private MaterialButton capturePhotoButton, nextButton, cancelButton;
    private Bitmap capturedPhoto; // Foto tomada para comparación

    // Claves para SharedPreferences
    private static final String PREFS_NAME = "UserPrefs";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";

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
        // Verificar si el inicio es por foto
        if (capturedPhoto != null) {
            // Lógica de comparación por foto
            compareWithStoredPhotos(capturedPhoto, new PhotoComparisonCallback() {
                @Override
                public void onComparisonResult(boolean photoMatches) {
                    if (photoMatches) {
                        Toast.makeText(LoginActivity.this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, ClienteActivity.class));
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "No se encontró coincidencia", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            // Verificar con el correo y la contraseña
            String enteredEmail = ((EditText) findViewById(R.id.email_edit_text)).getText().toString().trim();
            String enteredPassword = ((EditText) findViewById(R.id.password_edit_text)).getText().toString().trim();

            SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            String storedEmail = sharedPreferences.getString(KEY_EMAIL, null);
            String storedPassword = sharedPreferences.getString(KEY_PASSWORD, null);

            if (storedEmail != null && storedPassword != null) {
                if (enteredEmail.equals("c@cliente.com") && enteredPassword.equals("holamundo")) {
                    // Si el usuario ingresa "c@cliente.com" y "holamundo"
                    Toast.makeText(LoginActivity.this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, ClienteActivity.class));
                    finish();
                } else if (enteredEmail.equals(storedEmail) && enteredPassword.equals(storedPassword)) {
                    // Si coincide el correo y la contraseña registrados
                    Toast.makeText(LoginActivity.this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, ClienteActivity.class));
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(LoginActivity.this, "No hay cuenta registrada", Toast.LENGTH_SHORT).show();
            }
        }

    }


    public interface PhotoComparisonCallback {
        void onComparisonResult(boolean photoMatches);
    }

    private void compareWithStoredPhotos(Bitmap capturedPhoto, PhotoComparisonCallback callback) {
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (storageDir != null && storageDir.exists()) {
            File[] files = storageDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    Bitmap storedPhoto = BitmapFactory.decodeFile(file.getAbsolutePath());

                    // Aquí se realiza la detección de las caras en ambas imágenes
                    detectFace(storedPhoto, new FaceDetectionCallback() {
                        @Override
                        public void onFaceDetected(float[] storedFaceFeatures) {
                            detectFace(capturedPhoto, new FaceDetectionCallback() {
                                @Override
                                public void onFaceDetected(float[] capturedFaceFeatures) {
                                    // Comparar las características faciales
                                    boolean photoMatches = areFacesSimilar(storedFaceFeatures, capturedFaceFeatures);
                                    callback.onComparisonResult(photoMatches);  // Retornar el resultado al callback
                                }

                                @Override
                                public void onError(Exception e) {
                                    callback.onComparisonResult(false); // Si hay un error, retornar false
                                }
                            });
                        }

                        @Override
                        public void onError(Exception e) {
                            callback.onComparisonResult(false); // Si hay un error, retornar false
                        }
                    });
                }
            }
        } else {
            callback.onComparisonResult(false); // Si no hay imágenes almacenadas, retornar false
        }
    }

    private void compareWithStoredPhotos(Bitmap photo) {
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (storageDir != null && storageDir.exists()) {
            File[] files = storageDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    Bitmap storedPhoto = BitmapFactory.decodeFile(file.getAbsolutePath());

                    // Detectar las características faciales de la imagen almacenada
                    detectFace(storedPhoto, new FaceDetectionCallback() {
                        @Override
                        public void onFaceDetected(float[] faceFeatures) {
                            // Detectar las características faciales de la imagen capturada
                            detectFace(photo, new FaceDetectionCallback() {
                                @Override
                                public void onFaceDetected(float[] capturedFaceFeatures) {
                                    // Comparar las características de las dos imágenes
                                    if (areFacesSimilar(faceFeatures, capturedFaceFeatures)) {
                                        // Si las características son similares
                                        Toast.makeText(LoginActivity.this, "Las caras coinciden", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(LoginActivity.this, "No se encontraron coincidencias", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onError(Exception e) {
                                    Toast.makeText(LoginActivity.this, "Error en la detección de la cara", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onError(Exception e) {
                            Toast.makeText(LoginActivity.this, "Error al detectar la cara", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }
    }
    private void detectFace(Bitmap bitmap, FaceDetectionCallback callback) {
        InputImage image = InputImage.fromBitmap(bitmap, 0);

        // Configuración del detector de cara con opciones
        FaceDetectorOptions options = new FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                .build();

        // Creación del detector de caras
        FaceDetector detector = FaceDetection.getClient(options);

        // Procesar la imagen
        Task<List<Face>> result = detector.process(image)
                .addOnSuccessListener(faces -> {
                    if (!faces.isEmpty()) {
                        Face face = faces.get(0);
                        float leftEyeOpenProb = face.getLeftEyeOpenProbability();
                        float rightEyeOpenProb = face.getRightEyeOpenProbability();
                        // Devolver las características faciales detectadas al callback
                        callback.onFaceDetected(new float[]{leftEyeOpenProb, rightEyeOpenProb});
                    } else {
                        callback.onError(new Exception("No se detectó ninguna cara."));
                    }
                })
                .addOnFailureListener(e -> {
                    callback.onError(e); // Llamar al callback en caso de error
                });
    }
    public interface FaceDetectionCallback {
        void onFaceDetected(float[] faceFeatures);
        void onError(Exception e);
    }


    private boolean areFacesSimilar(float[] face1, float[] face2) {
        // Compara las características faciales (por ejemplo, la probabilidad de los ojos abiertos)
        return Math.abs(face1[0] - face2[0]) < 0.2 && Math.abs(face1[1] - face2[1]) < 0.2;
    }
    // Método para comparar dos imágenes (puedes usar un método más avanzado si es necesario)
    private boolean isPhotoMatch(Bitmap photo1, Bitmap photo2) {
        // Comparar imágenes por similitud (esto es solo un ejemplo básico)
        return photo1.sameAs(photo2); // Función que compara los píxeles de las dos imágenes
    }


    private void goToRegister() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
        finish();
    }
}
