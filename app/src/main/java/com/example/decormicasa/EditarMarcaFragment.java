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
import android.widget.Spinner;
import android.widget.Toast;
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

public class EditarMarcaFragment extends Fragment {

    private EditText editTextNombre, editTextDescripcion, editTextPrecioCompra,
            editTextPrecioVenta, editTextStock, editTextImagen,
            editTextCaracteristicas, editTextUsos, editTextIdCategoria;
    private Button btnEditar, btnVolver;
    private Spinner spinnerEstado;
    private int idMarca;// ID de la marca a editar
    private Context context;
    ImageView imageMarca;
    private ActivityResultLauncher<Intent> seleccionarImagenLauncher;
    private Uri imagenSeleccionadaUri;
    private ImageView imageViewMarca;
    private Uri fotoUri;
    private ActivityResultLauncher<Uri> tomarFotoLauncher;

    public EditarMarcaFragment() {
    }

    // Recibe el objeto del marca a editar, junto con los datos actuales
    public static EditarMarcaFragment newInstance(MarcasRequest marca) {
        EditarMarcaFragment fragment = new EditarMarcaFragment();
        Bundle args = new Bundle();
        args.putSerializable("marca", marca);  // Pasa el objeto completo para mostrar los datos
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // Recupera la marca completo desde los argumentos
            MarcasRequest marca = (MarcasRequest) getArguments().getSerializable("marca");
            if (marca != null) {
                idMarca = marca.getIdMarca(); // Obtener el ID de la marca
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_editar_marca, container, false);

        // Inicializa los elementos de la vista
        editTextNombre = view.findViewById(R.id.editTextNombre);
        editTextDescripcion = view.findViewById(R.id.editTextDescripcion);

        editTextIdCategoria = view.findViewById(R.id.editTextIdCategoria);
        editTextImagen = view.findViewById(R.id.editTextImagen);
        btnEditar = view.findViewById(R.id.btnEditar);
        btnVolver = view.findViewById(R.id.btnVolver);
        imageViewMarca  = view.findViewById(R.id.EditimageViewMarca);
        Button btnSeleccionarImagen = view.findViewById(R.id.btnEditSeleccionarImagen);
        Button btnTomarFoto = view.findViewById(R.id.btnEditTomarFoto);

        btnSeleccionarImagen.setOnClickListener(v -> abrirGaleria());
        btnTomarFoto.setOnClickListener(v -> tomarFoto());
        if (getArguments() != null) {
            MarcasRequest marca = (MarcasRequest) getArguments().getSerializable("marca");
            if (marca != null) {
                mostrarDatosMarca(marca);
            }
        }
        // Configura los launchers para seleccionar imagen o tomar foto
        configurarSeleccionarImagenLauncher();
        configurarTomarFotoLauncher();

        btnEditar.setOnClickListener(v -> editarMarca(idMarca));

        btnVolver.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        return view;
    }

    // Método para mostrar los datos de la marca en los campos
    private void mostrarDatosMarca(MarcasRequest marca) {
        editTextNombre.setText(marca.getNombre());
        editTextDescripcion.setText(marca.getDescripcion());
        editTextIdCategoria.setText(String.valueOf(marca.getIdCategoria()));
        editTextImagen.setText(marca.getImagen());
        String imageUrl = marca.getImagen();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.loading_image)
                    .error(R.drawable.default_image)
                    .into(imageViewMarca);
        } else {
            // Si la URL es nula, cargar una imagen por defecto
            imageViewMarca.setImageResource(R.drawable.default_image);
        }

    }
    /*private void mostrarImagenSeleccionada(Uri uri) {
        Glide.with(requireContext())
                .load(uri)
                .placeholder(R.drawable.loading_image)
                .error(R.drawable.default_image)
                .into(imageViewMarca);
    }*/
    // Configurar launcher para seleccionar imagen de la galería
    private void configurarSeleccionarImagenLauncher() {
        seleccionarImagenLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        imagenSeleccionadaUri = result.getData().getData();
                        // Actualiza el ImageView con la nueva imagen seleccionada
                        Glide.with(this)
                                .load(imagenSeleccionadaUri)
                                .placeholder(R.drawable.loading_image)
                                .error(R.drawable.default_image)
                                .into(imageViewMarca);
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

    // Configurar launcher para tomar foto con la cámara
    private void configurarTomarFotoLauncher() {tomarFotoLauncher = registerForActivityResult(
            new ActivityResultContracts.TakePicture(),
            isPhotoTaken -> {
                if (isPhotoTaken) {
                    // Actualiza el ImageView con la nueva foto tomada
                    Glide.with(this)
                            .load(imagenSeleccionadaUri)
                            .placeholder(R.drawable.loading_image)
                            .error(R.drawable.default_image)
                            .into(imageViewMarca);
                } else {
                    Toast.makeText(requireContext(), "No se tomó ninguna foto", Toast.LENGTH_SHORT).show();
                }
            }
        );
    }
    private void tomarFoto() {
        File archivoFoto = new File(
                requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "marca_" + System.currentTimeMillis() + ".jpg"
        );

        imagenSeleccionadaUri = FileProvider.getUriForFile(
                requireContext(),
                "com.example.decormicasa.fileprovider",
                archivoFoto
        );

        tomarFotoLauncher.launch(imagenSeleccionadaUri);
    }
    // Método para editar la marca
    private void editarMarca(int id) {
        String nombre = editTextNombre.getText().toString();
        String descripcion = editTextDescripcion.getText().toString();
        Integer idCategoria = Integer.parseInt(editTextIdCategoria.getText().toString());
        String imagenUrl = editTextImagen.getText().toString();
        //String imagenUrl = editTextImagen.getText().toString();

        // Si hay una nueva imagen seleccionada o tomada, subirla y usar la nueva URL
        if (imagenSeleccionadaUri != null) {
            subirImagenYActualizarMarca(nombre, descripcion, idCategoria, id);
        } else {
            // Si no hay nueva imagen, usar la URL existente
            actualizarMarca(nombre, descripcion, idCategoria, imagenUrl, id);
        }

    }
    private void subirImagenYActualizarMarca(String nombre, String descripcion, int idCategoria, int id) {
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
                        String nuevaUrlImagen  = response.body().getUrl();
                        actualizarMarca(nombre, descripcion, idCategoria, nuevaUrlImagen, id);

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
    private void actualizarMarca(String nombre, String descripcion, int idCategoria,  String imagenUrl, int id) {
        MarcasRequest marca = new MarcasRequest(nombre, descripcion, idCategoria, imagenUrl);
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("decorMiCasa", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("tokenJWT", "");

        if (token == null || token.isEmpty()) {
            Toast.makeText(requireContext(), "Token no disponible. Inicie sesión nuevamente.", Toast.LENGTH_LONG).show();
            return;
        }
        Log.d("Marca JSON", marca.toString());
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(requireContext().getString(R.string.dominioservidor))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        decorMiCasaApi api = retrofit.create(decorMiCasaApi.class);

        Call<Void> call = api.actualizarMarca("JWT " +token, id, marca);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(requireContext(), "Marca editada con éxito", Toast.LENGTH_SHORT).show();
                    requireActivity().getSupportFragmentManager().popBackStack(); // Cierra el fragmento
                } else {
                    try {
                        assert response.errorBody() != null;
                        String errorBody = response.errorBody().string();
                        Log.e("API Error", errorBody); // Imprimir el error en Logcat
                        Toast.makeText(requireContext(), "Error: " + errorBody, Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(requireContext(), "Error al editar la marca y al leer el cuerpo del error", Toast.LENGTH_SHORT).show();
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
