package com.example.decormicasa;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.decormicasa.Interface.decorMiCasaApi;
import com.example.decormicasa.model.ProductRequest;

import java.io.IOException;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EditarProductoFragment extends Fragment {

    private EditText editTextNombre, editTextDescripcion, editTextPrecioCompra,
            editTextPrecioVenta, editTextStock, editTextImagen,
            editTextCaracteristicas, editTextUsos, editTextIdMarca;
    private Button btnEditar, btnVolver;
    private Spinner spinnerEstado;
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
        editTextImagen = view.findViewById(R.id.editTextImagen);
        spinnerEstado = view.findViewById(R.id.spinnerEstado);
        editTextCaracteristicas = view.findViewById(R.id.editTextCaracteristicas);
        editTextUsos = view.findViewById(R.id.editTextUsos);
        editTextIdMarca = view.findViewById(R.id.editTextIdMarca);
        btnEditar = view.findViewById(R.id.btnEditar);
        btnVolver = view.findViewById(R.id.btnVolver);





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
        editTextImagen.setText(producto.getImagen());
        editTextCaracteristicas.setText(producto.getCaracteristicas());
        editTextUsos.setText(producto.getUsos());
        editTextIdMarca.setText(String.valueOf(producto.getIdMarca()));

        // Aquí puedes agregar lógica para seleccionar el estado en el Spinner (Activo/Inactivo)
        String estado = producto.getEstado();
        if ("Activo".equals(estado)) {
            spinnerEstado.setSelection(0); // Selecciona el primer item (Activo)
        } else if ("Inactivo".equals(estado)) {
            spinnerEstado.setSelection(1); // Selecciona el segundo item (Inactivo)
        }
    }


    // Método para editar el producto
    private void editarProducto(int id) {
        String nombre = editTextNombre.getText().toString();
        String descripcion = editTextDescripcion.getText().toString();
        Float precioCompra = Float.parseFloat(editTextPrecioCompra.getText().toString());
        Float precioVenta = Float.parseFloat(editTextPrecioVenta.getText().toString());
        Integer stock = Integer.parseInt(editTextStock.getText().toString());
        String imagen = editTextImagen.getText().toString();

        String estado = spinnerEstado.getSelectedItem().toString();
        String caracteristicas = editTextCaracteristicas.getText().toString();
        String usos = editTextUsos.getText().toString();
        Integer idMarca = Integer.parseInt(editTextIdMarca.getText().toString());

        ProductRequest producto = new ProductRequest(nombre, descripcion, precioCompra, precioVenta, stock, imagen, estado, caracteristicas, usos, idMarca);

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
