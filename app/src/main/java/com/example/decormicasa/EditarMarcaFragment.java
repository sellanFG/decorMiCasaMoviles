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
import com.example.decormicasa.model.MarcasRequest;

import java.io.IOException;
import java.util.Objects;

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
    private int idMarca; // ID del producto a editar

    public EditarMarcaFragment() {
    }

    // Recibe el objeto del producto a editar, junto con los datos actuales
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
            // Recupera el producto completo desde los argumentos
            MarcasRequest marca = (MarcasRequest) getArguments().getSerializable("marca");
            if (marca != null) {
                idMarca = marca.getIdMarca(); // Obtener el ID del producto
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

        if (getArguments() != null) {
            MarcasRequest marca = (MarcasRequest) getArguments().getSerializable("marca");
            if (marca != null) {
                mostrarDatosProducto(marca);
            }
        }

        btnEditar.setOnClickListener(v -> editarProducto(idMarca));

        btnVolver.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        return view;
    }

    // Método para mostrar los datos del producto en los campos
    private void mostrarDatosProducto(MarcasRequest marca) {
        editTextNombre.setText(marca.getNombre());
        editTextDescripcion.setText(marca.getDescripcion());
        editTextIdCategoria.setText(String.valueOf(marca.getIdCategoria()));
        editTextImagen.setText(marca.getImagen());

    }

    // Método para editar el producto
    private void editarProducto(int id) {
        String nombre = editTextNombre.getText().toString();
        String descripcion = editTextDescripcion.getText().toString();
        Integer idMarca = Integer.parseInt(editTextIdCategoria.getText().toString());
        String imagen = editTextImagen.getText().toString();
        MarcasRequest producto = new MarcasRequest(nombre, descripcion, idMarca, imagen);

        Log.d("Producto JSON", producto.toString());
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(requireContext().getString(R.string.dominioservidor))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        decorMiCasaApi api = retrofit.create(decorMiCasaApi.class);

        Call<Void> call = api.actualizarMarca(id, producto);

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