package com.example.decormicasa;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.decormicasa.Interface.decorMiCasaApi;
import com.example.decormicasa.model.CategoriaRequest;

import java.io.IOException;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegistrarCategoriaFragment extends Fragment {

    private EditText editTextNombre, editTextDescripcion, editTextImagen;
    private Button btnRegistrar;


    public RegistrarCategoriaFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registrar_categorias, container, false);

        editTextNombre = view.findViewById(R.id.editTextNombre);
        editTextDescripcion = view.findViewById(R.id.editTextDescripcion);
        editTextImagen = view.findViewById(R.id.editTextImagen);


        btnRegistrar = view.findViewById(R.id.btnRegistrar);

        btnRegistrar.setOnClickListener(v -> registrarCategoria());

        return view;
    }

    private void registrarCategoria() {
        String nombre = editTextNombre.getText().toString();
        String descripcion = editTextDescripcion.getText().toString();

        String imagen = editTextImagen.getText().toString();
        if (nombre.isEmpty() || descripcion.isEmpty() || imagen.isEmpty()) {
            Toast.makeText(requireContext(), "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }
         /*if (!imagen.startsWith("http://") && !imagen.startsWith("https://")) {
            Toast.makeText(requireContext(), "Ingrese un enlace válido para la imagen, por ejemplo: https://imagen.jpg", Toast.LENGTH_SHORT).show();
            return;
        }*/

        CategoriaRequest nuevoCategoria = new CategoriaRequest(nombre, descripcion, imagen);

        Log.d("Producto JSON", nuevoCategoria.toString());
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(requireContext().getString(R.string.dominioservidor))
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        decorMiCasaApi api = retrofit.create(decorMiCasaApi.class);

        Call<Void> call = api.registrarCategoria(nuevoCategoria);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(requireContext(), "Categoria registrado con éxito", Toast.LENGTH_SHORT).show();
                    requireActivity().getSupportFragmentManager().popBackStack(); // Cierra el fragmento
                } else {
                    try {
                        assert response.errorBody() != null;
                        String errorBody = response.errorBody().string();
                        Log.e("API Error", errorBody); // Imprimir el error en Logcat
                        Toast.makeText(requireContext(), "Error: " + errorBody, Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(requireContext(), "Error al registrar la categoria y al leer el cuerpo del error", Toast.LENGTH_SHORT).show();
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
