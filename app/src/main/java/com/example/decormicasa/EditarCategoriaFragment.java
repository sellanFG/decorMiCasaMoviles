package com.example.decormicasa;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.decormicasa.Interface.decorMiCasaApi;
import com.example.decormicasa.model.CategoriaRequest;

import java.io.IOException;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EditarCategoriaFragment extends Fragment {

    private EditText editTextNombre, editTextDescripcion, editTextPrecioCompra, editTextImagen;

    private Button btnEditar, btnVolver;
    private Spinner spinnerEstado;
    private int idProducto; // ID del producto a edit

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
            // Recupera el producto completo desde los argumentos
            CategoriaRequest producto = (CategoriaRequest) getArguments().getSerializable("categoria");
            if (producto != null) {
                idProducto = producto.getIdCategoria(); // Obtener el ID del producto
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


        editTextImagen = view.findViewById(R.id.editTextImagen);


        btnEditar = view.findViewById(R.id.btnEditar);
        btnVolver = view.findViewById(R.id.btnVolver);

        // Si el producto es válido, llena los campos con los datos
        if (getArguments() != null) {
            CategoriaRequest producto = (CategoriaRequest) getArguments().getSerializable("categoria");
            if (producto != null) {
                mostrarDatosProducto(producto);
            }
        }

        // Acción del botón para editar el producto
        btnEditar.setOnClickListener(v -> editarProducto(idProducto));

        // Configura el botón de volver
        btnVolver.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        return view;
    }

    // Método para mostrar los datos del producto en los campos
    private void mostrarDatosProducto(CategoriaRequest producto) {
        editTextNombre.setText(producto.getNombre());
        editTextDescripcion.setText(producto.getDescripcion());


        editTextImagen.setText(producto.getImagen());

    }

    // Método para editar el producto
    private void editarProducto(int id) {
        String nombre = editTextNombre.getText().toString();
        String descripcion = editTextDescripcion.getText().toString();
        String imagen = editTextImagen.getText().toString();

        CategoriaRequest producto = new CategoriaRequest(nombre, descripcion, imagen);

        Log.d("Producto JSON", producto.toString());
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(requireContext().getString(R.string.dominioservidor))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        decorMiCasaApi api = retrofit.create(decorMiCasaApi.class);

        Call<Void> call = api.actualizarCategoria(id, producto);

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
