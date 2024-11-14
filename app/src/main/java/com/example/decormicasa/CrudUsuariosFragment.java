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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.decormicasa.Interface.decorMiCasaApi;
import com.example.decormicasa.login.LoginActivity;
import com.example.decormicasa.model.ProductRequest;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CrudUsuariosFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProductosAdapter adapter;
    private List<ProductRequest> productos;
    private Button btnRegistrarUsuario;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crud_usuarios_admin, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewUsuarios);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        productos = new ArrayList<>();

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("decorMiCasa", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("tokenJWT", "");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.dominioservidor))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        decorMiCasaApi api = retrofit.create(decorMiCasaApi.class);

        if (token == null || token.isEmpty()) {
            Toast.makeText(getContext(), "Sesión expirada. Por favor, inicie sesión nuevamente.", Toast.LENGTH_LONG).show();
            // Aquí puedes iniciar la LoginActivity manualmente si el token no está disponible
            startActivity(new Intent(getContext(), LoginActivity.class));
        } else {
            // Configura el adaptador y RecyclerView normalmente
            adapter = new ProductosAdapter(productos, api, getContext(), token, getParentFragmentManager());
            recyclerView.setAdapter(adapter);
        }

        obtenerProductos(api, token);

        btnRegistrarUsuario = view.findViewById(R.id.btnRegistrarUsuario);
        btnRegistrarUsuario.setOnClickListener(v -> {
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

        return view;
    }

    private void obtenerProductos(decorMiCasaApi api, String tokenJWT) {
        if (tokenJWT == null || tokenJWT.isEmpty()) {
            Toast.makeText(getContext(), "Token no disponible. Inicie sesión nuevamente.", Toast.LENGTH_LONG).show();
            return;
        }

        Call<List<ProductRequest>> call = api.obtenerproductos("JWT " + tokenJWT);
        call.enqueue(new Callback<List<ProductRequest>>() {
            @Override
            public void onResponse(Call<List<ProductRequest>> call, Response<List<ProductRequest>> response) {
                Context context = null;
                if (response.isSuccessful() && response.body() != null) {
                    productos.clear();
                    productos.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else if (response.code() == 401) {
                    // Código 401 indica que la autenticación falló
                    Toast.makeText(context, "Sesión expirada. Por favor, inicie sesión nuevamente.", Toast.LENGTH_LONG).show();
                    context.startActivity(new Intent(context, LoginActivity.class));
                } else {
                    Toast.makeText(context, "Error al cargar productos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ProductRequest>> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Fallo en la conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
