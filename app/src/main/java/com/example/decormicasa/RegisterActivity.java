package com.example.decormicasa;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.decormicasa.Interface.decorMiCasaApi;
import com.example.decormicasa.login.LoginActivity;
import com.example.decormicasa.model.User;
import com.example.decormicasa.model.UserResponse;
import com.google.android.material.navigation.NavigationView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterActivity extends AppCompatActivity {

    private EditText nameEditText, emailEditText, passwordEditText, addressEditText, phoneEditText;
    private Button registerButton, backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Inicializar los campos de texto
        nameEditText = findViewById(R.id.name_edit_text);
        emailEditText = findViewById(R.id.email_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        addressEditText = findViewById(R.id.direction_edit_text);
        phoneEditText = findViewById(R.id.phone_edit_text);

        // Inicializar los botones
        registerButton = findViewById(R.id.register_button);
        backButton = findViewById(R.id.back_button);

        // Configuración del DrawerLayout
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Manejar los clics de los elementos del Drawer
        navigationView.setNavigationItemSelectedListener(menuItem -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        // Acción al hacer clic en el botón "Registrar"
        registerButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String address = addressEditText.getText().toString().trim();
            String phone = phoneEditText.getText().toString().trim();

            // Validar campos
            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || address.isEmpty() || phone.isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
            } else {
                // Llamada al API para registrar el usuario
                registerUser(name, email, password, address, phone);
            }
        });

        // Acción al hacer clic en el botón "Volver"
        backButton.setOnClickListener(v -> {
            // Regresar a la pantalla de Login
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void registerUser(String name, String email, String password, String address, String phone) {

        User user = new User(name, email, password, address, phone);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.dominioservidor))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        decorMiCasaApi.ApiService apiService = retrofit.create(decorMiCasaApi.ApiService.class);

        // Realizar la solicitud de registro
        Call<UserResponse> call = apiService.registerUser(user);
        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful()) {
                    // Si la respuesta es exitosa
                    UserResponse userResponse = response.body();
                    if (userResponse != null && userResponse.isSuccess()) {
                        Toast.makeText(RegisterActivity.this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                        // Redirigir al login después de registrarse
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish(); // Cierra la actividad de registro
                    } else {
                        Toast.makeText(RegisterActivity.this, userResponse != null ? userResponse.getMessage() : "Error en el registro", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(RegisterActivity.this, "Error en el registro: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
