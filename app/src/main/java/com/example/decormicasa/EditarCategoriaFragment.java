package com.example.decormicasa;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
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

public class EditarCategoriaFragment extends Fragment {

    private EditText editTextNombre, editTextDescripcion;
    private Button btnEditar, btnVolver, btnSeleccionarImagen, btnTomarFoto;
    private ImageView imageViewCategoria;
    private Uri imagenSeleccionadaUri;
    private ActivityResultLauncher<Intent> seleccionarImagenLauncher;
    private ActivityResultLauncher<Uri> tomarFotoLauncher;
    private int idCategoria; // ID de la categoría a editar

    public EditarCategoriaFragment() {
    }

    // Recibe el objeto del producto a editar, junto con los datos actuales
    public static EditarCategoriaFragment newInstance(CategoriaRequest categoria) {
        EditarCategoriaFragment fragment = new EditarCategoriaFragment();
        Bundle args = new Bundle();
        args.putSerializable("categoria", categoria);  // Pasa el objeto completo para mostrar los datos
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // Recupera el categoria completo desde los argumentos
            CategoriaRequest categoria = (CategoriaRequest) getArguments().getSerializable("categoria");
            if (categoria != null) {
                idCategoria = categoria.getIdCategoria(); // Obtener el ID del categoria
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_editar_categoria, container, false);

        // Inicializa los elementos de la vista
        editTextNombre = view.findViewById(R.id.editTextNombre);
        editTextDescripcion = view.findViewById(R.id.editTextDescripcion);
        imageViewCategoria = view.findViewById(R.id.EditimageViewCategoria);
        btnEditar = view.findViewById(R.id.btnEditar);
        btnVolver = view.findViewById(R.id.btnVolver);
        btnSeleccionarImagen = view.findViewById(R.id.btnEditSeleccionarImagen);
        btnTomarFoto = view.findViewById(R.id.btnEditTomarFoto);

        btnSeleccionarImagen.setOnClickListener(v -> abrirGaleria());
        btnTomarFoto.setOnClickListener(v -> tomarFoto());

        configurarSeleccionarImagenLauncher();
        configurarTomarFotoLauncher();
        // Si la categoria es válida, llena los campos con los datos
        if (getArguments() != null) {
            CategoriaRequest categoria = (CategoriaRequest) getArguments().getSerializable("categoria");
            if (categoria != null) {
                mostrarDatosCategoria(categoria);
            }
        }

        // Acción del botón para editar la categoria
        btnEditar.setOnClickListener(v -> editarCategoria(idCategoria));

        // Configura el botón de volver
        btnVolver.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        return view;
    }

    // Método para mostrar los datos de la categoria en los campos
    private void mostrarDatosCategoria(CategoriaRequest categoria) {
        editTextNombre.setText(categoria.getNombre());
        editTextDescripcion.setText(categoria.getDescripcion());

        String imageUrl = categoria.getImagen();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.loading_image)
                    .error(R.drawable.default_image)
                    .into(imageViewCategoria);
        } else {
            imageViewCategoria.setImageResource(R.drawable.default_image);
        }

    }
    private void configurarSeleccionarImagenLauncher() {
        seleccionarImagenLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        imagenSeleccionadaUri = result.getData().getData();
                        Glide.with(this)
                                .load(imagenSeleccionadaUri)
                                .placeholder(R.drawable.loading_image)
                                .error(R.drawable.default_image)
                                .into(imageViewCategoria);
                    } else {
                        Toast.makeText(requireContext(), "No se seleccionó ninguna imagen", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        seleccionarImagenLauncher.launch(intent);
    }

    private void configurarTomarFotoLauncher() {
        tomarFotoLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                isPhotoTaken -> {
                    if (isPhotoTaken) {
                        Glide.with(this)
                                .load(imagenSeleccionadaUri)
                                .placeholder(R.drawable.loading_image)
                                .error(R.drawable.default_image)
                                .into(imageViewCategoria);
                    } else {
                        Toast.makeText(requireContext(), "No se tomó ninguna foto", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void tomarFoto() {
        File archivoFoto = new File(
                requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "categoria_" + System.currentTimeMillis() + ".jpg"
        );

        imagenSeleccionadaUri = FileProvider.getUriForFile(
                requireContext(),
                "com.example.decormicasa.fileprovider",
                archivoFoto
        );

        tomarFotoLauncher.launch(imagenSeleccionadaUri);
    }

    // Método para editar la categoria
    private void editarCategoria(int id) {
        String nombre = editTextNombre.getText().toString();
        String descripcion = editTextDescripcion.getText().toString();
        if (nombre.isEmpty() || descripcion.isEmpty()) {
            Toast.makeText(requireContext(), "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (imagenSeleccionadaUri != null) {
            subirImagenYActualizarCategoria(nombre, descripcion, id);
        } else {
            actualizarCategoria(nombre, descripcion, null, id);
        }

    }
    private void subirImagenYActualizarCategoria(String nombre, String descripcion, int id) {
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
                        String nuevaUrlImagen = response.body().getUrl();
                        actualizarCategoria(nombre, descripcion, nuevaUrlImagen, id);
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
            Toast.makeText(requireContext(), "Error al subir la imagen", Toast.LENGTH_SHORT).show();
        }
    }
    private void actualizarCategoria(String nombre, String descripcion, String urlImagen, int id) {

        CategoriaRequest categoria = new CategoriaRequest(nombre, descripcion, urlImagen);
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("decorMiCasa", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("tokenJWT", "");

        if (token == null || token.isEmpty()) {
            Toast.makeText(requireContext(), "Token no disponible. Inicie sesión nuevamente.", Toast.LENGTH_LONG).show();
            return;
        }
        Log.d("Categoria JSON", categoria.toString());
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(requireContext().getString(R.string.dominioservidor))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        decorMiCasaApi api = retrofit.create(decorMiCasaApi.class);

        Call<Void> call = api.actualizarCategoria("JWT " +token, id, categoria);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(requireContext(), "Categoria editado con éxito", Toast.LENGTH_SHORT).show();
                    requireActivity().getSupportFragmentManager().popBackStack(); // Cierra el fragmento
                } else {
                    try {
                        assert response.errorBody() != null;
                        String errorBody = response.errorBody().string();
                        Log.e("API Error", errorBody); // Imprimir el error en Logcat
                        Toast.makeText(requireContext(), "Error: " + errorBody, Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(requireContext(), "Error al editar la Categoria y al leer el cuerpo del error", Toast.LENGTH_SHORT).show();
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
