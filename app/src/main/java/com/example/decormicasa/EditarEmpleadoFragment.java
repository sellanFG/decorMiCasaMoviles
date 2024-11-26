package com.example.decormicasa;

import android.content.Context;
import android.content.SharedPreferences;
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
import com.example.decormicasa.model.ProductRequest;
import com.example.decormicasa.model.UsuarioRequest;

import java.io.IOException;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EditarEmpleadoFragment extends Fragment {

    private EditText editTextNombre, editTextEmail, editTextdireccion, editTextTelefono;
    private Button btnEditar, btnVolver;
    private int idUsuarios; // ID del Empleado a editar

    public EditarEmpleadoFragment() {
    }

    // Recibe el objeto del empleado a editar, junto con los datos actuales
    public static EditarEmpleadoFragment newInstance(UsuarioRequest usuario) {
        EditarEmpleadoFragment fragment = new EditarEmpleadoFragment();
        Bundle args = new Bundle();
        args.putSerializable("usuario", usuario);  // Pasa el objeto completo para mostrar los datos
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // Recupera el empleado completo desde los argumentos
            UsuarioRequest usuario = (UsuarioRequest) getArguments().getSerializable("usuario");
            if (usuario != null) {
                idUsuarios = usuario.getIdUsuario(); // Obtener el ID del empleado
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_editar_empleado, container, false);

        // Inicializa los elementos de la vista
        editTextNombre = view.findViewById(R.id.editTextNombre);
        editTextEmail = view.findViewById(R.id.editTextEmail);
        editTextTelefono = view.findViewById(R.id.editTextTelefono);
        btnEditar = view.findViewById(R.id.btnEditar);
        btnVolver = view.findViewById(R.id.btnVolver);

        if (getArguments() != null) {
            UsuarioRequest usuario = (UsuarioRequest) getArguments().getSerializable("usuario");
            if (usuario != null) {
                mostrarDatosEmpleado(usuario);
            }
        }

        btnEditar.setOnClickListener(v -> editarEmpleado(idUsuarios));

        btnVolver.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        return view;
    }

    // Método para mostrar los datos del empleado en los campos
    private void mostrarDatosEmpleado(UsuarioRequest usuario) {
        editTextNombre.setText(usuario.getNombre());
        editTextEmail.setText(usuario.getEmail());
        editTextTelefono.setText(usuario.getTelefono());
    }

    // Método para editar el empleado
    private void editarEmpleado(int id) {
        String nombre = editTextNombre.getText().toString();
        String email = editTextEmail.getText().toString();
        String telefono = editTextTelefono.getText().toString();

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("decorMiCasa", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("tokenJWT", "");

        if (token == null || token.isEmpty()) {
            Toast.makeText(requireContext(), "Token no disponible. Inicie sesión nuevamente.", Toast.LENGTH_LONG).show();
            return;
        }

        UsuarioRequest usuario = new UsuarioRequest(nombre, email, telefono);

        Log.d("Empleado JSON", usuario.toString());
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(requireContext().getString(R.string.dominioservidor))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        decorMiCasaApi api = retrofit.create(decorMiCasaApi.class);

        Call<Void> call = api.editarEmpleado( "JWT " +token,id, usuario);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(requireContext(), "Empleado editado con éxito", Toast.LENGTH_SHORT).show();
                    requireActivity().getSupportFragmentManager().popBackStack(); // Cierra el fragmento
                } else {
                    try {
                        assert response.errorBody() != null;
                        String errorBody = response.errorBody().string();
                        Log.e("API Error", errorBody); // Imprimir el error en Logcat
                        Toast.makeText(requireContext(), "Error: " + errorBody, Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(requireContext(), "Error al editar el empleado y al leer el cuerpo del error", Toast.LENGTH_SHORT).show();
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
