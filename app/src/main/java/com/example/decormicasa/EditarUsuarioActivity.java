package com.example.decormicasa;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.decormicasa.Interface.decorMiCasaApi;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
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
    private NavigationManager navigationManager;
    private DrawerLayout drawerLayout;
    private ImageButton btnMenu;
    private TextInputEditText editTextNombre, editTextEmail, editTextDireccion, editTextTelefono, editTextPassword;
    private MaterialButton btnGuardar;
    private decorMiCasaApi api;
    private int userId;
    private String token;

    // Declara originalPassword aquí
    private String originalPassword = ""; // Valor por defecto vacío

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_usuario);

        // Inicializar el NavigationManager después de setContentView
        navigationManager = new NavigationManager(this);
        navigationManager.setupNavigation();

        // para menu lateral

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        btnMenu = findViewById(R.id.btnMenu);
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                int id = menuItem.getItemId();

                if (id == R.id.nav_nosotros) {
                    Intent intent = new Intent(EditarUsuarioActivity.this, NosotrosActivity.class);
                    startActivity(intent);
                    drawerLayout.closeDrawer(GravityCompat.START);
                    return true;
                } else if (id == R.id.nav_privacidad) {
                    Intent intent = new Intent(EditarUsuarioActivity.this, PoliticaActivity.class);
                    startActivity(intent);
                    drawerLayout.closeDrawer(GravityCompat.START);
                    return true;
                } else if (id == R.id.nav_mapa) {
                    Intent intent = new Intent(EditarUsuarioActivity.this, MapActivity.class);
                    startActivity(intent);
                    drawerLayout.closeDrawer(GravityCompat.START);
                    return true;
                }
                return false;
            }
        });

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
        String nombre = editTextNombre.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String direccion = editTextDireccion.getText().toString().trim();
        String telefono = editTextTelefono.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim(); // Contraseña visible en el campo

        if (nombre.isEmpty() || email.isEmpty() || direccion.isEmpty() || telefono.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos obligatorios.", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences sharedPreferences = getSharedPreferences("decorMiCasa", Context.MODE_PRIVATE);
        int userId = sharedPreferences.getInt("id_cliente", 0);
        String token = sharedPreferences.getString("tokenJWT", "");

        JsonObject data = new JsonObject();
        data.addProperty("nombre", nombre);
        data.addProperty("email", email);
        data.addProperty("direccion", direccion);
        data.addProperty("telefono", telefono);

        // Solo enviar contraseña si se modificó
        if (!password.isEmpty() && !password.equals(originalPassword)) {
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
