package com.example.decormicasa;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import android.content.Context;
import android.content.SharedPreferences;
import com.bumptech.glide.Glide;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.decormicasa.Interface.decorMiCasaApi;
import com.example.decormicasa.model.ImagenResponse;
import com.example.decormicasa.model.MarcasRequest;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegistrarMarcaFragment extends Fragment {

    private EditText editTextNombre, editTextDescripcion,
            editTextIdCategoria, editTextImagen;
    private Button btnRegistrar, btnVolver, btnSeleccionarImagen, btnTomarFoto;
    private Spinner spinnerEstado;
    private ActivityResultLauncher<Intent> seleccionarImagenLauncher;
    private Uri imagenSeleccionadaUri;
    private ImageView imageViewMarca;
    private Uri fotoUri;
    private ActivityResultLauncher<Uri> tomarFotoLauncher;




    public RegistrarMarcaFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registrar_marca, container, false);

        editTextNombre = view.findViewById(R.id.editTextNombre);
        editTextDescripcion = view.findViewById(R.id.editTextDescripcion);

        editTextIdCategoria = view.findViewById(R.id.editTextIdCategoria);
        //editTextImagen = view.findViewById(R.id.editTextImagen);
        btnRegistrar = view.findViewById(R.id.btnRegistrar);
        btnVolver = view.findViewById(R.id.btnVolver);
        btnTomarFoto = view.findViewById(R.id.btnTomarFoto);
        btnSeleccionarImagen = view.findViewById(R.id.btnSeleccionarImagen);
        imageViewMarca = view.findViewById(R.id.imageViewMarca);
        // Configurar el launcher para seleccionar imágenes
        seleccionarImagenLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        imagenSeleccionadaUri = result.getData().getData();
                        mostrarImagenSeleccionada(imagenSeleccionadaUri);
                    }
                }
        );
        // Configurar el launcher para tomar fotos
        tomarFotoLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                isPhotoTaken -> {
                    if (isPhotoTaken) {
                        mostrarImagenSeleccionada(imagenSeleccionadaUri);
                    } else {
                        Toast.makeText(requireContext(), "Foto no tomada", Toast.LENGTH_SHORT).show();
                    }
                }
        );
        // Configurar el click para seleccionar imagen
        btnSeleccionarImagen.setOnClickListener(v -> abrirGaleria());
        // Configurar el botón para tomar fotos
        btnTomarFoto.setOnClickListener(v -> tomarFoto());
        btnRegistrar.setOnClickListener(v -> registrarMarca());
        btnVolver.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        return view;

    }
    // Método para abrir la galería
    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        seleccionarImagenLauncher.launch(intent);
    }

    // Método para tomar foto
    private void tomarFoto() {
        try {
            File archivoFoto = new File(
                    requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                    "marca_" + System.currentTimeMillis() + ".jpg"
            );

            if (!archivoFoto.exists() && !archivoFoto.createNewFile()) {
                Toast.makeText(requireContext(), "Error al crear archivo temporal", Toast.LENGTH_SHORT).show();
                return;
            }

            imagenSeleccionadaUri = FileProvider.getUriForFile(
                    requireContext(),
                    "com.example.decormicasa.fileprovider",
                    archivoFoto
            );

            tomarFotoLauncher.launch(imagenSeleccionadaUri);

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Error al crear el archivo de imagen", Toast.LENGTH_SHORT).show();
        }
    }


        // Método para mostrar la imagen seleccionada en el ImageView
    private void mostrarImagenSeleccionada(Uri uri) {
        Glide.with(requireContext())
                .load(uri)
                .placeholder(R.drawable.loading_image)
                .error(R.drawable.default_image)
                .into(imageViewMarca);
    }


    private void registrarMarca() {
        String nombre = editTextNombre.getText().toString().trim();
        String descripcion = editTextDescripcion.getText().toString().trim();

        String idCategoriaStr = editTextIdCategoria.getText().toString().trim();

        if (nombre.isEmpty() || descripcion.isEmpty() || idCategoriaStr.isEmpty()||  imagenSeleccionadaUri == null) {
            Toast.makeText(requireContext(), "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            // Leer el contenido de la URI directamente
            InputStream inputStream = requireContext().getContentResolver().openInputStream(imagenSeleccionadaUri);
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            inputStream.close();
            // Generar un nombre único para la imagen
            String nombreArchivoUnico = "marca_" + System.currentTimeMillis() + ".jpg";
            // Crear el RequestBody usando el contenido del archivo
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), bytes);
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", nombreArchivoUnico, requestFile);

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(requireContext().getString(R.string.dominioservidor))
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            decorMiCasaApi api = retrofit.create(decorMiCasaApi.class);

            // Llamada a la API para subir la imagen
            Call<ImagenResponse> call = api.subirImagen(body);

            call.enqueue(new Callback<ImagenResponse>() {
                @Override
                public void onResponse(@NonNull Call<ImagenResponse> call, Response<ImagenResponse> response) {
                    if (response.isSuccessful()) {
                        // Obtener la URL de la imagen
                        String urlImagen = response.body().getUrl();
                        // Registrar la marca con la URL de la imagen
                        registrarMarcaConImagen(nombre, descripcion, idCategoriaStr, urlImagen);
                    } else {
                        // Imprimir detalles de la respuesta de error
                        try {
                            String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                            Log.e("Error Subida Imagen", errorBody);  // Log de error más detallado
                            Toast.makeText(requireContext(), "Error al subir la imagen: " + errorBody, Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(requireContext(), "Error al leer el cuerpo de la respuesta: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ImagenResponse> call, Throwable t) {
                    Toast.makeText(requireContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Error al registrar la marca: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }


    }
    private void registrarMarcaConImagen(String nombre, String descripcion, String idCategoriaStr, String urlImagen) {
        Integer idCategoria = Integer.parseInt(idCategoriaStr);

        MarcasRequest nuevaMarca = new MarcasRequest(nombre, descripcion, idCategoria, urlImagen);

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("decorMiCasa", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("tokenJWT", "");

        if (token == null || token.isEmpty()) {
            Toast.makeText(requireContext(), "Token no disponible. Inicie sesión nuevamente.", Toast.LENGTH_LONG).show();
            return;
        }

        try {

            Log.d("Marca JSON", nuevaMarca.toString());
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(requireContext().getString(R.string.dominioservidor))
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            decorMiCasaApi api = retrofit.create(decorMiCasaApi.class);

            Call<Void> call = api.registrarMarca("JWT " +token, nuevaMarca);

            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(requireContext(), "Marca registrada con éxito", Toast.LENGTH_SHORT).show();
                        requireActivity().getSupportFragmentManager().popBackStack();
                    } else {
                        try {
                            assert response.errorBody() != null;
                            String errorBody = response.errorBody().string();
                            Log.e("API Error", errorBody);
                            Toast.makeText(requireContext(), "Error: " + errorBody, Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(requireContext(), "Error al registrar la marca y al leer el cuerpo del error", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, Throwable t) {
                    Log.e("API Failure", Objects.requireNonNull(t.getMessage()));
                    Toast.makeText(requireContext(), "Error en la conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } catch (NumberFormatException e) {
            Toast.makeText(requireContext(), "Ingrese valores numéricos válidos", Toast.LENGTH_SHORT).show();
        }

    }
    // Método para obtener el path real de la URI (desde el dispositivo)
    private String getRealPathFromURI(Uri contentUri) {
        Cursor cursor = requireContext().getContentResolver().query(contentUri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

}
