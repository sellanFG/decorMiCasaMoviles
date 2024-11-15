package com.example.decormicasa;

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
import com.example.decormicasa.model.MarcasRequest;

import java.io.IOException;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegistrarMarcaFragment extends Fragment {

    private EditText editTextNombre, editTextDescripcion,
             editTextIdCategoria, editTextImagen;
    private Button btnRegistrar, btnVolver;
    private Spinner spinnerEstado;

    public RegistrarMarcaFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registrar_marca, container, false);

        editTextNombre = view.findViewById(R.id.editTextNombre);
        editTextDescripcion = view.findViewById(R.id.editTextDescripcion);

        editTextIdCategoria = view.findViewById(R.id.editTextIdCategoria);
        editTextImagen = view.findViewById(R.id.editTextImagen);
        btnRegistrar = view.findViewById(R.id.btnRegistrar);
        btnVolver = view.findViewById(R.id.btnVolver);

        btnRegistrar.setOnClickListener(v -> registrarMarca());
        btnVolver.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());
        return view;
    }

    private void registrarMarca() {
        String nombre = editTextNombre.getText().toString().trim();
        String descripcion = editTextDescripcion.getText().toString().trim();

        String idMarcaStr = editTextIdCategoria.getText().toString().trim();
        String imagen = editTextImagen.getText().toString();
        if (nombre.isEmpty() || descripcion.isEmpty() || idMarcaStr.isEmpty()|| imagen.isEmpty()) {
            Toast.makeText(requireContext(), "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        /*if (!imagen.startsWith("http://") && !imagen.startsWith("https://")) {
            Toast.makeText(requireContext(), "Ingrese un enlace válido para la imagen, por ejemplo: https://imagen.jpg", Toast.LENGTH_SHORT).show();
            return;
        }*/

        try {

            Integer idCategoria = Integer.parseInt(idMarcaStr);

            MarcasRequest nuevoMarca = new MarcasRequest(nombre, descripcion, idCategoria, imagen);

            Log.d("Producto JSON", nuevoMarca.toString());
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(requireContext().getString(R.string.dominioservidor))
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            decorMiCasaApi api = retrofit.create(decorMiCasaApi.class);

            Call<Void> call = api.registrarMarca(nuevoMarca);

            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(requireContext(), "Marca registrada con éxito", Toast.LENGTH_SHORT).show();
                        requireActivity().getSupportFragmentManager().popBackStack();
                    } else {
                        try {
                            assert response.errorBody() != null;
                            String errorBody = response.errorBody().string();
                            Log.e("API Error", errorBody);
                            Toast.makeText(requireContext(), "Error: " + errorBody, Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(requireContext(), "Error al registrar la marca y al leer el cuerpo del error", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(requireContext(), "Ingrese valores numéricos válidos", Toast.LENGTH_SHORT).show();
        }
    }

}
