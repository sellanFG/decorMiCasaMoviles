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
    private Button btnRegistrar, btnVolver;
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
        btnVolver = view.findViewById(R.id.btnVolver);

        btnRegistrar.setOnClickListener(v -> registrarProducto());
        btnVolver.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());
        return view;
    }

    private void registrarProducto() {
        String nombre = editTextNombre.getText().toString().trim();
        String descripcion = editTextDescripcion.getText().toString().trim();
        String precioCompraStr = editTextPrecioCompra.getText().toString().trim();
        String precioVentaStr = editTextPrecioVenta.getText().toString().trim();
        String stockStr = editTextStock.getText().toString().trim();
        String imagen = editTextImagen.getText().toString().trim();
        String estado = spinnerEstado.getSelectedItem().toString();
        String caracteristicas = editTextCaracteristicas.getText().toString().trim();
        String usos = editTextUsos.getText().toString().trim();
        String idMarcaStr = editTextIdMarca.getText().toString().trim();

        if (nombre.isEmpty() || descripcion.isEmpty() || precioCompraStr.isEmpty() || precioVentaStr.isEmpty() ||
                stockStr.isEmpty() || imagen.isEmpty() || caracteristicas.isEmpty() || usos.isEmpty() || idMarcaStr.isEmpty()) {
            Toast.makeText(requireContext(), "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!imagen.startsWith("http://") && !imagen.startsWith("https://")) {
            Toast.makeText(requireContext(), "Ingrese un enlace válido para la imagen, por ejemplo: https://imagen.jpg", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Float precioCompra = Float.parseFloat(precioCompraStr);
            Float precioVenta = Float.parseFloat(precioVentaStr);
            Integer stock = Integer.parseInt(stockStr);
            Integer idMarca = Integer.parseInt(idMarcaStr);

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
