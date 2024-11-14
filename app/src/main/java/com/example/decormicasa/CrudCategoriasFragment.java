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
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.decormicasa.Interface.decorMiCasaApi;
import com.example.decormicasa.model.CategoriaRequest;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class CrudCategoriasFragment extends Fragment {

    private RecyclerView recyclerView;
    private CategoriaAdapter adapter;
    private Button btnRegistrarCategoria, btnVolver;
    private List<CategoriaRequest> categorias;
    private decorMiCasaApi api;
    private String token;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fargment_crud_categorias, container, false);
        btnVolver = view.findViewById(R.id.btnVolver);
        recyclerView = view.findViewById(R.id.recyclerViewCategorias);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        categorias = new ArrayList<>();


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.dominioservidor))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        api = retrofit.create(decorMiCasaApi.class);

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("decorMiCasa", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("tokenJWT", "");

        adapter = new CategoriaAdapter(categorias, api, getContext(), token, getParentFragmentManager());
        recyclerView.setAdapter(adapter);


        AppCompatImageButton btnRegistrarCategoria = view.findViewById(R.id.btnRegistrarCategoria);
        btnRegistrarCategoria.setOnClickListener(v -> {
            try {
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new RegistrarCategoriaFragment())
                        .addToBackStack(null)
                        .commit();
            } catch (Exception e) {
                Log.e("CrudCategoriasFragment", "Error al abrir RegistrarProductoFragment", e);
                Toast.makeText(getContext(), "Error al abrir el registro de producto", Toast.LENGTH_SHORT).show();
            }
        });

        obtenerCategorias();
        btnVolver.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());
        return view;

    }

    private void obtenerCategorias() {
        if (token == null || token.isEmpty()) {
            Toast.makeText(getContext(), "Token no disponible. Inicie sesión nuevamente.", Toast.LENGTH_LONG).show();
            return;
        }

        Call<List<CategoriaRequest>> call = api.obtenercategorias("JWT " + token);
        call.enqueue(new Callback<List<CategoriaRequest>>() {
            @Override
            public void onResponse(@NonNull Call<List<CategoriaRequest>> call, @NonNull Response<List<CategoriaRequest>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categorias.clear();

                    categorias.addAll(response.body());
                    System.out.println(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "Error al cargar categorias", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<CategoriaRequest>> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Fallo en la conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
