package com.example.decormicasa;

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

public class RegistrarProductoFragment extends Fragment {

    private EditText editTextNombre, editTextDescripcion, editTextPrecioCompra,
            editTextPrecioVenta, editTextStock, editTextImagen,
            editTextCaracteristicas, editTextUsos, editTextIdMarca;
    private Button btnRegistrar;
    private Spinner spinnerEstado;

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
        editTextImagen = view.findViewById(R.id.editTextImagen);
        spinnerEstado = view.findViewById(R.id.spinnerEstado);
        editTextCaracteristicas = view.findViewById(R.id.editTextCaracteristicas);
        editTextUsos = view.findViewById(R.id.editTextUsos);
        editTextIdMarca = view.findViewById(R.id.editTextIdMarca);
        btnRegistrar = view.findViewById(R.id.btnRegistrar);

        btnRegistrar.setOnClickListener(v -> registrarProducto());

        return view;
    }

    private void registrarProducto() {
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

        ProductRequest nuevoProducto = new ProductRequest(nombre, descripcion, precioCompra, precioVenta, stock, imagen, estado, caracteristicas, usos, idMarca);

        Log.d("Producto JSON", nuevoProducto.toString());
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(requireContext().getString(R.string.dominioservidor))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        decorMiCasaApi api = retrofit.create(decorMiCasaApi.class);

        Call<Void> call = api.registrarProducto(nuevoProducto);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(requireContext(), "Producto registrado con éxito", Toast.LENGTH_SHORT).show();
                    requireActivity().getSupportFragmentManager().popBackStack(); // Cierra el fragmento
                } else {
                    try {
                        assert response.errorBody() != null;
                        String errorBody = response.errorBody().string();
                        Log.e("API Error", errorBody); // Imprimir el error en Logcat
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
    }
}
