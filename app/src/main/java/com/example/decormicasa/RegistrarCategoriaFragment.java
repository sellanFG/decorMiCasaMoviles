package com.example.decormicasa;

import android.app.Activity;
import android.content.Intent;
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
import android.widget.Toast;
import com.bumptech.glide.Glide;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.decormicasa.Interface.decorMiCasaApi;
import com.example.decormicasa.model.CategoriaRequest;
import com.example.decormicasa.model.ImagenResponse;

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

public class RegistrarCategoriaFragment extends Fragment {

    private EditText editTextNombre, editTextDescripcion;
    private Button btnRegistrar, btnVolver, btnSeleccionarImagen, btnTomarFoto;
    private ImageView imageViewCategoria;
    private Uri imagenSeleccionadaUri;
    private ActivityResultLauncher<Intent> seleccionarImagenLauncher;
    private ActivityResultLauncher<Uri> tomarFotoLauncher;


    public RegistrarCategoriaFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registrar_categorias, container, false);

        editTextNombre = view.findViewById(R.id.editTextNombre);
        editTextDescripcion = view.findViewById(R.id.editTextDescripcion);
        //editTextImagen = view.findViewById(R.id.editTextImagen);
        btnTomarFoto = view.findViewById(R.id.btnTomarFoto);
        btnSeleccionarImagen = view.findViewById(R.id.btnSeleccionarImagen);
        imageViewCategoria  = view.findViewById(R.id.imageViewCategoria);

        btnRegistrar = view.findViewById(R.id.btnRegistrar);
        btnVolver = view.findViewById(R.id.btnVolver);
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

        btnRegistrar.setOnClickListener(v -> registrarCategoria());
        btnVolver.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());
        return view;

    }
    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        seleccionarImagenLauncher.launch(intent);
    }

    private void tomarFoto() {
        try {
            File archivoFoto = new File(
                    requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                    "categoria_" + System.currentTimeMillis() + ".jpg"
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
    private void mostrarImagenSeleccionada(Uri uri) {
        Glide.with(requireContext())
                .load(uri)
                .placeholder(R.drawable.loading_image)
                .error(R.drawable.default_image)
                .into(imageViewCategoria);
    }


    private void registrarCategoria() {
        String nombre = editTextNombre.getText().toString();
        String descripcion = editTextDescripcion.getText().toString();

        //String imagen = editTextImagen.getText().toString();
        if (nombre.isEmpty() || descripcion.isEmpty() || imagenSeleccionadaUri == null) {
            Toast.makeText(requireContext(), "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            InputStream inputStream = requireContext().getContentResolver().openInputStream(imagenSeleccionadaUri);
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            inputStream.close();

            String nombreArchivoUnico = "categoria_" + System.currentTimeMillis() + ".jpg";
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), bytes);
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", nombreArchivoUnico, requestFile);

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(requireContext().getString(R.string.dominioservidor))
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            decorMiCasaApi api = retrofit.create(decorMiCasaApi.class);

            Call<ImagenResponse> call = api.subirImagen(body);

            call.enqueue(new Callback<ImagenResponse>() {
                @Override
                public void onResponse(@NonNull Call<ImagenResponse> call, Response<ImagenResponse> response) {
                    if (response.isSuccessful()) {
                        String urlImagen = response.body().getUrl();
                        registrarCategoriaConImagen(nombre, descripcion, urlImagen);
                    } else {
                        Toast.makeText(requireContext(), "Error al subir la imagen", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ImagenResponse> call, Throwable t) {
                    Toast.makeText(requireContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Error al registrar la categoría: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }



    }
    private void registrarCategoriaConImagen(String nombre, String descripcion, String urlImagen) {
        CategoriaRequest nuevaCategoria = new CategoriaRequest(nombre, descripcion, urlImagen);

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("decorMiCasa", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("tokenJWT", "");

        if (token == null || token.isEmpty()) {
            Toast.makeText(requireContext(), "Token no disponible. Inicie sesión nuevamente.", Toast.LENGTH_LONG).show();
            return;
        }

        Log.d("Categoria JSON", nuevaCategoria.toString());
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(requireContext().getString(R.string.dominioservidor))
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        decorMiCasaApi api = retrofit.create(decorMiCasaApi.class);

        Call<Void> call = api.registrarCategoria("JWT " +token, nuevaCategoria);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(requireContext(), "Categoria registrado con éxito", Toast.LENGTH_SHORT).show();
                    requireActivity().getSupportFragmentManager().popBackStack(); // Cierra el fragmento
                } else {
                    try {
                        assert response.errorBody() != null;
                        String errorBody = response.errorBody().string();
                        Log.e("API Error", errorBody); // Imprimir el error en Logcat
                        Toast.makeText(requireContext(), "Error: " + errorBody, Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(requireContext(), "Error al registrar la categoria y al leer el cuerpo del error", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, Throwable t) {
                Log.e("API Failure", Objects.requireNonNull(t.getMessage()));
                Toast.makeText(requireContext(), "Error en la conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
