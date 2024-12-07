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
import com.bumptech.glide.Glide;
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

import com.example.decormicasa.Interface.decorMiCasaApi;
import com.example.decormicasa.model.ImagenResponse;
import com.example.decormicasa.model.ProductRequest;

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

public class EditarProductoFragment extends Fragment {

    private EditText editTextNombre, editTextDescripcion, editTextPrecioCompra,
            editTextPrecioVenta, editTextStock, editTextCaracteristicas,
            editTextUsos, editTextIdMarca;
    private Spinner spinnerEstado;
    private Button btnEditar, btnVolver, btnSeleccionarImagen, btnTomarFoto;
    private ImageView imageViewProducto;
    private Uri imagenSeleccionadaUri;
    private ActivityResultLauncher<Intent> seleccionarImagenLauncher;
    private ActivityResultLauncher<Uri> tomarFotoLauncher;
    private int idProducto; // ID del producto a editar



    public EditarProductoFragment() {
    }

    // Recibe el objeto del producto a editar, junto con los datos actuales
    public static EditarProductoFragment newInstance(ProductRequest producto) {
        EditarProductoFragment fragment = new EditarProductoFragment();
        Bundle args = new Bundle();
        args.putSerializable("producto", producto);  // Pasa el objeto completo para mostrar los datos
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // Recupera el producto completo desde los argumentos
            ProductRequest producto = (ProductRequest) getArguments().getSerializable("producto");
            if (producto != null) {
                idProducto = producto.getIdProducto(); // Obtener el ID del producto
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_editar_producto, container, false);

        // Inicializa los elementos de la vista
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
        btnEditar = view.findViewById(R.id.btnEditar);
        btnVolver = view.findViewById(R.id.btnVolver);
        btnSeleccionarImagen = view.findViewById(R.id.btnEditSeleccionarImagen);
        btnTomarFoto = view.findViewById(R.id.btnEditTomarFoto);
        imageViewProducto = view.findViewById(R.id.EditimageViewProducto);
        configurarSeleccionarImagenLauncher();
        configurarTomarFotoLauncher();

        btnSeleccionarImagen.setOnClickListener(v -> abrirGaleria());
        btnTomarFoto.setOnClickListener(v -> tomarFoto());

        if (getArguments() != null) {
            ProductRequest producto = (ProductRequest) getArguments().getSerializable("producto");
            if (producto != null) {
                mostrarDatosProducto(producto);
            }
        }

        btnEditar.setOnClickListener(v -> editarProducto(idProducto));

        btnVolver.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        return view;
    }

    // Método para mostrar los datos del producto en los campos
    private void mostrarDatosProducto(ProductRequest producto) {
        editTextNombre.setText(producto.getNombre());
        editTextDescripcion.setText(producto.getDescripcion());
        editTextPrecioCompra.setText(String.valueOf(producto.getPrecioCompra()));
        editTextPrecioVenta.setText(String.valueOf(producto.getPrecioVenta()));
        editTextStock.setText(String.valueOf(producto.getStock()));
        //editTextImagen.setText(producto.getImagen());
        editTextCaracteristicas.setText(producto.getCaracteristicas());
        editTextUsos.setText(producto.getUsos());
        editTextIdMarca.setText(String.valueOf(producto.getIdMarca()));
        String imageUrl = producto.getImagen();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.loading_image)
                    .error(R.drawable.default_image)
                    .into(imageViewProducto);
        } else {
            imageViewProducto.setImageResource(R.drawable.default_image);
        }
        // Aquí puedes agregar lógica para seleccionar el estado en el Spinner (Activo/Inactivo)
        String estado = producto.getEstado();
        if ("Activo".equals(estado)) {
            spinnerEstado.setSelection(0); // Selecciona el primer item (Activo)
        } else if ("Inactivo".equals(estado)) {
            spinnerEstado.setSelection(1); // Selecciona el segundo item (Inactivo)
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
                                .into(imageViewProducto);
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
                                .into(imageViewProducto);
                    } else {
                        Toast.makeText(requireContext(), "No se tomó ninguna foto", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void tomarFoto() {
        File archivoFoto = new File(
                requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "producto_" + System.currentTimeMillis() + ".jpg"
        );

        imagenSeleccionadaUri = FileProvider.getUriForFile(
                requireContext(),
                "com.example.decormicasa.fileprovider",
                archivoFoto
        );

        tomarFotoLauncher.launch(imagenSeleccionadaUri);
    }
    // Método para editar el producto
    private void editarProducto(int id) {
        String nombre = editTextNombre.getText().toString();
        String descripcion = editTextDescripcion.getText().toString();
        Float precioCompra = Float.parseFloat(editTextPrecioCompra.getText().toString());
        Float precioVenta = Float.parseFloat(editTextPrecioVenta.getText().toString());
        Integer stock = Integer.parseInt(editTextStock.getText().toString());
        //String imagen = editTextImagen.getText().toString();

        String estado = spinnerEstado.getSelectedItem().toString();
        String caracteristicas = editTextCaracteristicas.getText().toString();
        String usos = editTextUsos.getText().toString();
        Integer idMarca = Integer.parseInt(editTextIdMarca.getText().toString());
        if (imagenSeleccionadaUri != null) {
            subirImagenYActualizarProducto(nombre, descripcion, precioCompra, precioVenta, stock, caracteristicas, usos, idMarca, id);
        } else {
            actualizarProducto(nombre, descripcion, null, precioCompra, precioVenta, stock, caracteristicas, usos, idMarca, id);
        }
    }
    private void subirImagenYActualizarProducto(String nombre, String descripcion, Float precioCompra, Float precioVenta,
                                                Integer stock, String caracteristicas, String usos, Integer idMarca, int id) {
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
                        String nuevaUrlImagen = response.body().getUrl();
                        actualizarProducto(nombre, descripcion, nuevaUrlImagen, precioCompra, precioVenta, stock, caracteristicas, usos, idMarca, id);
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
    private void actualizarProducto(String nombre, String descripcion, String urlImagen, Float precioCompra,
                                    Float precioVenta, Integer stock, String caracteristicas, String usos,
                                    Integer idMarca, int id) {


        ProductRequest producto = new ProductRequest(nombre, descripcion, precioCompra, precioVenta,
                stock, urlImagen, spinnerEstado.getSelectedItem().toString(), caracteristicas, usos, idMarca);

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("decorMiCasa", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("tokenJWT", "");

        if (token == null || token.isEmpty()) {
            Toast.makeText(requireContext(), "Token no disponible. Inicie sesión nuevamente.", Toast.LENGTH_LONG).show();
            return;
        }

        Log.d("Producto JSON", producto.toString());
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(requireContext().getString(R.string.dominioservidor))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        decorMiCasaApi api = retrofit.create(decorMiCasaApi.class);

        Call<Void> call = api.editarProducto("JWT " +token,id, producto);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(requireContext(), "Producto editado con éxito", Toast.LENGTH_SHORT).show();
                    requireActivity().getSupportFragmentManager().popBackStack(); // Cierra el fragmento
                } else {
                    try {
                        assert response.errorBody() != null;
                        String errorBody = response.errorBody().string();
                        Log.e("API Error", errorBody); // Imprimir el error en Logcat
                        Toast.makeText(requireContext(), "Error: " + errorBody, Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(requireContext(), "Error al editar el producto y al leer el cuerpo del error", Toast.LENGTH_SHORT).show();
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
