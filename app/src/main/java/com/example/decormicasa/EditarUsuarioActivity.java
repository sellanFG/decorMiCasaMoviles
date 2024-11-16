package com.example.decormicasa;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.decormicasa.Interface.decorMiCasaApi;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EditarUsuarioActivity extends AppCompatActivity {

    private TextInputEditText editTextNombre, editTextEmail, editTextDireccion, editTextTelefono, editTextPassword;
    private MaterialButton btnGuardar;
    private decorMiCasaApi api;
    private int userId;
    private String token;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_usuario);

        editTextNombre = findViewById(R.id.editTextNombre);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextDireccion = findViewById(R.id.editTextDireccion);
        editTextTelefono = findViewById(R.id.editTextTelefono);
        editTextPassword = findViewById(R.id.editTextPassword);
        btnGuardar = findViewById(R.id.btnGuardar);

        // Nuevo: Botón Cancelar
        MaterialButton btnCancelar = findViewById(R.id.btnCancelar);
        btnCancelar.setOnClickListener(v -> {
            // Finaliza la actividad y vuelve a la pantalla anterior
            finish();
        });

        SharedPreferences sharedPreferences = getSharedPreferences("decorMiCasa", Context.MODE_PRIVATE);
        userId = sharedPreferences.getInt("id_cliente", 0);
        token = sharedPreferences.getString("tokenJWT", "");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.dominioservidor))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(decorMiCasaApi.class);

        cargarDatosUsuario();

        btnGuardar.setOnClickListener(v -> editarUsuario());
    }

    private void cargarDatosUsuario() {
        SharedPreferences sharedPreferences = getSharedPreferences("decorMiCasa", Context.MODE_PRIVATE);
        int userId = sharedPreferences.getInt("id_cliente", 0);
        String token = sharedPreferences.getString("tokenJWT", "");

        if (userId == 0 || token.isEmpty()) {
            Toast.makeText(this, "Error al obtener el usuario autenticado", Toast.LENGTH_SHORT).show();
            return;
        }

        // Llamada a la API para obtener los datos del usuario
        Call<JsonObject> call = api.obtenerUsuario("JWT " + token, userId);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    JsonObject usuario = response.body();
                    editTextNombre.setText(usuario.has("nombre") ? usuario.get("nombre").getAsString() : "");
                    editTextEmail.setText(usuario.has("email") ? usuario.get("email").getAsString() : "");
                    editTextDireccion.setText(usuario.has("direccion") ? usuario.get("direccion").getAsString() : "");
                    editTextTelefono.setText(usuario.has("telefono") ? usuario.get("telefono").getAsString() : "");
                } else {
                    Toast.makeText(EditarUsuarioActivity.this, "No se pudo cargar los datos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                Toast.makeText(EditarUsuarioActivity.this, "Error en la conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void editarUsuario() {
        String nombre = editTextNombre.getText().toString();
        String email = editTextEmail.getText().toString();
        String direccion = editTextDireccion.getText().toString();
        String telefono = editTextTelefono.getText().toString();
        String password = editTextPassword.getText().toString();

        SharedPreferences sharedPreferences = getSharedPreferences("decorMiCasa", Context.MODE_PRIVATE);
        int userId = sharedPreferences.getInt("id_cliente", 0);
        String token = sharedPreferences.getString("tokenJWT", "");

        JsonObject data = new JsonObject();
        data.addProperty("nombre", nombre);
        data.addProperty("email", email);
        data.addProperty("direccion", direccion);
        data.addProperty("telefono", telefono);
        if (!password.isEmpty()) {
            data.addProperty("password", password);
        }

        Call<Void> call = api.editarUsuario("JWT " + token, userId, data);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EditarUsuarioActivity.this, "Usuario actualizado", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(EditarUsuarioActivity.this, "Error al actualizar", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Toast.makeText(EditarUsuarioActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }



}
