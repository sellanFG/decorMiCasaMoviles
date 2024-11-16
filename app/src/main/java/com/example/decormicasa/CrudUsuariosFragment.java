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
import com.example.decormicasa.model.UsuarioRequest;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CrudUsuariosFragment extends Fragment {

    private RecyclerView recyclerView;
    private UsuariosAdapter adapter;
    private List<UsuarioRequest> usuarios;
    private Button btnRegistrarUsuario;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crud_usuarios_admin, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewUsuarios);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        usuarios = new ArrayList<>();

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
            adapter = new UsuariosAdapter(usuarios, api, getContext(), token, getParentFragmentManager());
            recyclerView.setAdapter(adapter);
        }

        obtener_empleados(api, token);

        btnRegistrarUsuario = view.findViewById(R.id.btnRegistrarUsuario);
        btnRegistrarUsuario.setOnClickListener(v -> {
            try {
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new RegistrarUsuarioFragment())
                        .addToBackStack(null)
                        .commit();
            } catch (Exception e) {
                Log.e("CrudusuarioFragment", "Error al abrir RegistrarusuarioFragment", e);
                Toast.makeText(getContext(), "Error al abrir el registro de usuario", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void obtener_empleados(decorMiCasaApi api, String tokenJWT) {
        if (tokenJWT == null || tokenJWT.isEmpty()) {
            Toast.makeText(getContext(), "Token no disponible. Inicie sesión nuevamente.", Toast.LENGTH_LONG).show();
            return;
        }

        Call<List<UsuarioRequest>> call = api.obtener_empleados("JWT " + tokenJWT);
        call.enqueue(new Callback<List<UsuarioRequest>>() {
            @Override
            public void onResponse(Call<List<UsuarioRequest>> call, Response<List<UsuarioRequest>> response) {
                Context context = null;
                if (response.isSuccessful() && response.body() != null) {
                    usuarios.clear();
                    usuarios.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else if (response.code() == 401) {
                    // Código 401 indica que la autenticación falló
                    Toast.makeText(context, "Sesión expirada. Por favor, inicie sesión nuevamente.", Toast.LENGTH_LONG).show();
                    context.startActivity(new Intent(context, LoginActivity.class));
                } else {
                    Toast.makeText(context, "Error al cargar usuarios", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<UsuarioRequest>> call, @NonNull Throwable t){
                Toast.makeText(getContext(), "Fallo en la conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
