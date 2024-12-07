package com.example.decormicasa;

import android.content.Context;
import android.content.Intent;
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
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.decormicasa.Interface.decorMiCasaApi;
import com.example.decormicasa.login.LoginActivity;
import com.example.decormicasa.model.ComprasRequest;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CrudComprasFragment extends Fragment {

    private RecyclerView recyclerView;
    private ComprasAdapter adapter;
    private List<ComprasRequest> compras;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crud_compras, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewCompras);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        compras = new ArrayList<>();

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("decorMiCasa", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("tokenJWT", "");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.dominioservidor))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        decorMiCasaApi api = retrofit.create(decorMiCasaApi.class);

        if (token == null || token.isEmpty()) {
            Toast.makeText(getContext(), "Sesión expirada. Por favor, inicie sesión nuevamente.", Toast.LENGTH_LONG).show();
            startActivity(new Intent(getContext(), LoginActivity.class));
        } else {
            adapter = new ComprasAdapter(compras, getContext());
            recyclerView.setAdapter(adapter);
            obtenerCompras(api, token);  // Llamada a la API para obtener las compras
        }

        // Botón para registrar una compra con un nuevo producto
        Button btnRegistrarNuevoProducto = view.findViewById(R.id.btnRegistrarNuevoProducto);
        btnRegistrarNuevoProducto.setOnClickListener(v -> {
            try {
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new RegistrarCompraNuevoFragment())
                        .addToBackStack(null)
                        .commit();
            } catch (Exception e) {
                Log.e("CrudComprasFragment", "Error al abrir RegistrarCompraFragment", e);
                Toast.makeText(getContext(), "Error al abrir el registro de compra", Toast.LENGTH_SHORT).show();
            }
        });

        // Botón para registrar una compra con un producto ya registrado
        Button btnRegistrarProductoRegistrado = view.findViewById(R.id.btnRegistrarProductoRegistrado);
        btnRegistrarProductoRegistrado.setOnClickListener(v -> {
            try {
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new RegistrarCompraExistenteFragment())
                        .addToBackStack(null)
                        .commit();
            } catch (Exception e) {
                Log.e("CrudComprasFragment", "Error al abrir RegistrarCompraFragment", e);
                Toast.makeText(getContext(), "Error al abrir el registro de compra", Toast.LENGTH_SHORT).show();
            }
        });

        // Botón Volver
        Button btnVolver = view.findViewById(R.id.btnVolver);
        btnVolver.setOnClickListener(v -> requireActivity().onBackPressed());

        return view;
    }

    private void obtenerCompras(decorMiCasaApi api, String tokenJWT) {
        Call<List<ComprasRequest>> call = api.obtenerCompras("JWT " + tokenJWT);

        call.enqueue(new Callback<List<ComprasRequest>>() {
            @Override
            public void onResponse(Call<List<ComprasRequest>> call, Response<List<ComprasRequest>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    compras.clear();
                    compras.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "Error al cargar compras: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ComprasRequest>> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Fallo en la conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


}
