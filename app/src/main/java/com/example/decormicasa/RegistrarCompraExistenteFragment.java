package com.example.decormicasa;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.decormicasa.login.LoginActivity;
import com.example.decormicasa.model.ComprasRequest;
import com.example.decormicasa.model.ProductRequest;
import com.example.decormicasa.Interface.decorMiCasaApi;
import com.example.decormicasa.model.ComprasRequest;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegistrarCompraExistenteFragment extends Fragment {

    private EditText etCantidad, etPrecioCompra, etProveedor, etDescripcionCompra;
    private Button btnRegistrarCompra;
    private Spinner spinnerProductos;
    private List<ProductRequest> productos;
    private ArrayAdapter<ProductRequest> adapter;

    public RegistrarCompraExistenteFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registrar_compra_existente, container, false);

        etCantidad = view.findViewById(R.id.etCantidad);
        etPrecioCompra = view.findViewById(R.id.etPrecioCompra);
        etProveedor = view.findViewById(R.id.etProveedor);
        etDescripcionCompra = view.findViewById(R.id.etDescripcionCompra);
        btnRegistrarCompra = view.findViewById(R.id.btnRegistrarCompra);
        spinnerProductos = view.findViewById(R.id.spinnerProductos);

        productos = new ArrayList<>();
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, productos);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProductos.setAdapter(adapter);

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("decorMiCasa", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("tokenJWT", "");

        if (token == null || token.isEmpty()) {
            Toast.makeText(getContext(), "Sesión expirada. Por favor, inicie sesión nuevamente.", Toast.LENGTH_LONG).show();
            startActivity(new Intent(getContext(), LoginActivity.class));
            return view;
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.dominioservidor))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        decorMiCasaApi api = retrofit.create(decorMiCasaApi.class);

        obtenerProductos(api, token);

        btnRegistrarCompra.setOnClickListener(v -> registrarCompra(token));

        return view;
    }

    private void obtenerProductos(decorMiCasaApi api, String tokenJWT) {
        Call<List<ProductRequest>> call = api.obtenerproductos("JWT " + tokenJWT);
        call.enqueue(new Callback<List<ProductRequest>>() {
            @Override
            public void onResponse(Call<List<ProductRequest>> call, Response<List<ProductRequest>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    productos.clear();
                    productos.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else if (response.code() == 401) {
                    Toast.makeText(getContext(), "Sesión expirada. Por favor, inicie sesión nuevamente.", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(getContext(), LoginActivity.class));
                } else {
                    Toast.makeText(getContext(), "Error al cargar productos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ProductRequest>> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Fallo en la conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void registrarCompra(String tokenJWT) {
        // Obtenemos el producto seleccionado (solo necesitamos el idProducto)
        ProductRequest productoSeleccionado = (ProductRequest) spinnerProductos.getSelectedItem();
        String cantidadStr = etCantidad.getText().toString();
        String precioCompraStr = etPrecioCompra.getText().toString();
        String proveedor = etProveedor.getText().toString();
        String descripcionCompra = etDescripcionCompra.getText().toString();

        if (productoSeleccionado == null || cantidadStr.isEmpty() || precioCompraStr.isEmpty() || proveedor.isEmpty()) {
            Toast.makeText(getContext(), "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        int cantidad = Integer.parseInt(cantidadStr);
        double precioCompra = Double.parseDouble(precioCompraStr);

        ComprasRequest compraRequest = new ComprasRequest(
                String.valueOf(productoSeleccionado.getIdProducto()),
                cantidad,
                precioCompra,
                proveedor,
                descripcionCompra
        );

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.dominioservidor)) // Asegúrate de tener esta URL configurada correctamente
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        decorMiCasaApi api = retrofit.create(decorMiCasaApi.class);

        api.registrarCompra("JWT " + tokenJWT, compraRequest)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(getContext(), "Compra registrada exitosamente", Toast.LENGTH_SHORT).show();
                        } else {
                            try {
                                // Leer el cuerpo del error del servidor
                                String errorBody = response.errorBody().string();
                                Log.e("Error Servidor", errorBody);
                                Toast.makeText(getContext(), "Error del servidor: " + errorBody, Toast.LENGTH_LONG).show();
                                getActivity().onBackPressed();
                            } catch (Exception e) {
                                Log.e("Error Parsing", "Error al leer el cuerpo de error", e);
                                Toast.makeText(getContext(), "Error desconocido del servidor", Toast.LENGTH_LONG).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Log.e("Error Conexión", t.getMessage(), t);
                        Toast.makeText(getContext(), "Fallo en la conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

}

