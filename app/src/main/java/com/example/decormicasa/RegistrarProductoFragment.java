package com.example.decormicasa;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import android.provider.MediaStore;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.decormicasa.Interface.decorMiCasaApi;
import com.example.decormicasa.model.ImagenResponse;
import com.example.decormicasa.model.ProductRequest;
import com.bumptech.glide.Glide;

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

public class RegistrarProductoFragment extends Fragment {

    private EditText editTextNombre, editTextDescripcion, editTextPrecioCompra,
            editTextPrecioVenta, editTextStock, editTextCaracteristicas,
            editTextUsos, editTextIdMarca;
    private Spinner spinnerEstado;
    private Button btnRegistrar, btnVolver, btnSeleccionarImagen, btnTomarFoto;
    private ImageView imageViewProducto;
    private Uri imagenSeleccionadaUri;
    private ActivityResultLauncher<Intent> seleccionarImagenLauncher;
    private ActivityResultLauncher<Uri> tomarFotoLauncher;

    public RegistrarProductoFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registrar_producto, container, false);

        editTextNombre = view.findViewById(R.id.editTextNombre);
        editTextDescripcion = view.findViewById(R.id.editTextDescripcion);
        editTextPrecioCompra = view.findViewById(R.id.editTextPrecioCompra);
        editTextPrecioVenta = view.findViewById(R.id.editTextPrecioVenta);
        editTextStock = view.findViewById(R.id.editTextStock);
        //editTextImagen = view.findViewById(R.id.editTextImagen);
        spinnerEstado = view.findViewById(R.id.spinnerEstado);
        editTextCaracteristicas = view.findViewById(R.id.editTextCaracteristicas);
        editTextUsos = view.findViewById(R.id.editTextUsos);
        editTextIdMarca = view.findViewById(R.id.editTextIdMarca);
        btnSeleccionarImagen = view.findViewById(R.id.btnSeleccionarImagen);
        btnTomarFoto = view.findViewById(R.id.btnTomarFoto);
        imageViewProducto = view.findViewById(R.id.imageViewProducto);
        btnRegistrar = view.findViewById(R.id.btnRegistrar);
        btnVolver = view.findViewById(R.id.btnVolver);
        btnSeleccionarImagen.setOnClickListener(v -> abrirGaleria());
        btnTomarFoto.setOnClickListener(v -> tomarFoto());
        btnRegistrar.setOnClickListener(v -> registrarProducto());
        btnVolver.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());
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
                    "producto_" + System.currentTimeMillis() + ".jpg"
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
                .into(imageViewProducto);
    }

    private void registrarProducto() {
        String nombre = editTextNombre.getText().toString().trim();
        String descripcion = editTextDescripcion.getText().toString().trim();
        String precioCompraStr = editTextPrecioCompra.getText().toString().trim();
        String precioVentaStr = editTextPrecioVenta.getText().toString().trim();
        String stockStr = editTextStock.getText().toString().trim();

        String estado = spinnerEstado.getSelectedItem().toString();
        String caracteristicas = editTextCaracteristicas.getText().toString().trim();
        String usos = editTextUsos.getText().toString().trim();
        String idMarcaStr = editTextIdMarca.getText().toString().trim();

        if (nombre.isEmpty() || descripcion.isEmpty() || precioCompraStr.isEmpty() || precioVentaStr.isEmpty() ||
                stockStr.isEmpty() || imagenSeleccionadaUri == null  || caracteristicas.isEmpty() || usos.isEmpty() || idMarcaStr.isEmpty()) {
            Toast.makeText(requireContext(), "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }


        try {
            InputStream inputStream = requireContext().getContentResolver().openInputStream(imagenSeleccionadaUri);
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            inputStream.close();

            String nombreArchivoUnico = "producto_" + System.currentTimeMillis() + ".jpg";
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
                        registrarProductoConImagen(nombre, descripcion, precioCompraStr, precioVentaStr,
                                stockStr, urlImagen, caracteristicas, usos, idMarcaStr);
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
            Toast.makeText(requireContext(), "Error al registrar el producto: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    private void registrarProductoConImagen(String nombre, String descripcion, String precioCompraStr,
                                            String precioVentaStr, String stockStr, String urlImagen,
                                            String caracteristicas, String usos, String idMarcaStr) {


        try {

            SharedPreferences sharedPreferences = requireContext().getSharedPreferences("decorMiCasa", Context.MODE_PRIVATE);
            String token = sharedPreferences.getString("tokenJWT", "");

            if (token == null || token.isEmpty()) {
                Toast.makeText(requireContext(), "Token no disponible. Inicie sesión nuevamente.", Toast.LENGTH_LONG).show();
                return;
            }
            Float precioCompra = Float.parseFloat(precioCompraStr);
            Float precioVenta = Float.parseFloat(precioVentaStr);
            Integer stock = Integer.parseInt(stockStr);
            Integer idMarca = Integer.parseInt(idMarcaStr);

            ProductRequest nuevoProducto = new ProductRequest(nombre, descripcion, precioCompra, precioVenta,
                    stock, urlImagen, spinnerEstado.getSelectedItem().toString(), caracteristicas, usos, idMarca);

            Log.d("Producto JSON", nuevoProducto.toString());
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(requireContext().getString(R.string.dominioservidor))
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            decorMiCasaApi api = retrofit.create(decorMiCasaApi.class);

            Call<Void> call = api.registrarProducto("JWT " +token,nuevoProducto);

            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(requireContext(), "Producto registrado con éxito", Toast.LENGTH_SHORT).show();
                        requireActivity().getSupportFragmentManager().popBackStack();
                    } else {
                        try {
                            assert response.errorBody() != null;
                            String errorBody = response.errorBody().string();
                            Log.e("API Error", errorBody);
                            Toast.makeText(requireContext(), "Error: " + errorBody, Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(requireContext(), "Error al registrar el producto y al leer el cuerpo del error", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(requireContext(), "Ingrese valores numéricos válidos para el precio, stock e ID de marca", Toast.LENGTH_SHORT).show();
        }
    }

}
