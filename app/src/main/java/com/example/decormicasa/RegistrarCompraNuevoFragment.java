package com.example.decormicasa;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.decormicasa.Interface.decorMiCasaApi;
import com.example.decormicasa.model.ComprasRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegistrarCompraNuevoFragment extends Fragment {

    private EditText etNombreProducto, etDescripcionProducto, etCantidad, etPrecioCompra, etProveedor, etDescripcionCompra;
    private EditText  etCaracteristicas, etUsos, etIdMarca; // Nuevos campos para imagen, características, usos y marca
    private Button btnRegistrarCompra;

    public RegistrarCompraNuevoFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registrar_compra_nuevo, container, false);

        etNombreProducto = view.findViewById(R.id.etNombreProducto);
        etDescripcionProducto = view.findViewById(R.id.etDescripcionProducto);
        etCantidad = view.findViewById(R.id.etCantidad);
        etPrecioCompra = view.findViewById(R.id.etPrecioCompra);
        etProveedor = view.findViewById(R.id.etProveedor);
        etDescripcionCompra = view.findViewById(R.id.etDescripcionCompra);

        etCaracteristicas = view.findViewById(R.id.etCaracteristicas);
        etUsos = view.findViewById(R.id.etUsos);
        etIdMarca = view.findViewById(R.id.etIdMarca);

        btnRegistrarCompra = view.findViewById(R.id.btnRegistrarCompra);

        btnRegistrarCompra.setOnClickListener(v -> registrarCompra());

        return view;
    }

    private void registrarCompra() {
        String nombreProducto = etNombreProducto.getText().toString();
        String descripcionProducto = etDescripcionProducto.getText().toString();
        String cantidadStr = etCantidad.getText().toString();
        String precioCompraStr = etPrecioCompra.getText().toString();
        String proveedor = etProveedor.getText().toString();
        String descripcionCompra = etDescripcionCompra.getText().toString();

        // Nuevos campos
        String imagen = "https//img.com";
        String caracteristicas = etCaracteristicas.getText().toString();
        String usos = etUsos.getText().toString();
        String idMarcaStr = etIdMarca.getText().toString();

        // Validación de los campos
        if (nombreProducto.isEmpty() || cantidadStr.isEmpty() || precioCompraStr.isEmpty() || proveedor.isEmpty() || idMarcaStr.isEmpty()) {
            Toast.makeText(getContext(), "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        int cantidad = Integer.parseInt(cantidadStr);
        double precioCompra = Double.parseDouble(precioCompraStr);
        int idMarca = Integer.parseInt(idMarcaStr);

        // Creamos el objeto ComprasRequest para un producto nuevo
        ComprasRequest comprasRequest = new ComprasRequest(
                nombreProducto,
                descripcionProducto,
                imagen, // Imagen proporcionada por el usuario
                caracteristicas, // Características proporcionadas por el usuario
                usos, // Usos proporcionados por el usuario
                idMarca, // ID de la marca proporcionada por el usuario
                cantidad,
                precioCompra,
                proveedor,
                descripcionCompra
        );

        // Obtener el token JWT desde SharedPreferences
        SharedPreferences prefs = getActivity().getSharedPreferences("decorMiCasa", Context.MODE_PRIVATE);
        String tokenJWT = prefs.getString("tokenJWT", "");

        if (tokenJWT.isEmpty()) {
            Toast.makeText(getContext(), "Token no disponible. Inicie sesión nuevamente.", Toast.LENGTH_LONG).show();
            return;
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.dominioservidor))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        decorMiCasaApi api = retrofit.create(decorMiCasaApi.class);

        // Llamamos a la API para registrar la compra con el producto nuevo
        api.registrarCompra("JWT " + tokenJWT, comprasRequest)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(getContext(), "Compra y producto registrado exitosamente", Toast.LENGTH_SHORT).show();
                            // Quitar este fragmento de la pila y volver al anterior
                            requireActivity().getSupportFragmentManager().popBackStack();
                        } else {
                            try {
                                String errorBody = response.errorBody().string();
                                Toast.makeText(getContext(), "Error del servidor: " + errorBody, Toast.LENGTH_LONG).show();
                            } catch (Exception e) {
                                Toast.makeText(getContext(), "Error desconocido del servidor", Toast.LENGTH_LONG).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(getContext(), "Fallo en la conexión", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
