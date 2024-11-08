package com.example.decormicasa;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.decormicasa.Interface.decorMiCasaApi;
import com.example.decormicasa.model.ProductRequest;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class CrudProductosFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProductosAdapter adapter;
    private Button btnRegistrarProducto;
    private List<ProductRequest> productos;
    private decorMiCasaApi api;
    private String token;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crud_productos, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewProductos);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        productos = new ArrayList<>();


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.dominioservidor))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        api = retrofit.create(decorMiCasaApi.class);

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("decorMiCasa", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("tokenJWT", "");

        adapter = new ProductosAdapter(productos, api, getContext(), token);
        recyclerView.setAdapter(adapter);

        btnRegistrarProducto = view.findViewById(R.id.btnRegistrarProducto);
        btnRegistrarProducto.setOnClickListener(v -> {
            try {
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new RegistrarProductoFragment())
                        .addToBackStack(null)
                        .commit();
            } catch (Exception e) {
                Log.e("CrudProductosFragment", "Error al abrir RegistrarProductoFragment", e);
                Toast.makeText(getContext(), "Error al abrir el registro de producto", Toast.LENGTH_SHORT).show();
            }
        });

        obtenerProductos();

        return view;
    }

    private void obtenerProductos() {
        if (token == null || token.isEmpty()) {
            Toast.makeText(getContext(), "Token no disponible. Inicie sesión nuevamente.", Toast.LENGTH_LONG).show();
            return;
        }

        Call<List<ProductRequest>> call = api.obtenerproductos("JWT " + token);
        call.enqueue(new Callback<List<ProductRequest>>() {
            @Override
            public void onResponse(@NonNull Call<List<ProductRequest>> call, @NonNull Response<List<ProductRequest>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    productos.clear();

                    productos.addAll(response.body());
                    adapter.notifyDataSetChanged();
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
}
