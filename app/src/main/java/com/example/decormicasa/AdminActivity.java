package com.example.decormicasa;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.decormicasa.login.LoginActivity;
import com.example.decormicasa.utils.TokenManager;
import com.google.android.material.button.MaterialButton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

public class AdminActivity extends AppCompatActivity implements TokenManager.TokenExpirationListener {

    private static final String TAG = "AdminActivity";
    private ImageButton btnUsuario;
    private TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // Inicialización de vistas
        MaterialButton btnProductos = findViewById(R.id.btnProductos);
        MaterialButton btnEmpleado = findViewById(R.id.btnEmpleados);
        MaterialButton btnCompras = findViewById(R.id.btnCompras);
        View gridOptions = findViewById(R.id.gridOptions);
        View fragmentContainer = findViewById(R.id.fragment_container);
        btnUsuario = findViewById(R.id.btnUsuario);  // Botón de usuario

        // Verificar si las vistas existen
        if (btnProductos == null || gridOptions == null || fragmentContainer == null || btnUsuario == null || btnEmpleado == null) {
            Log.e(TAG, "Error al obtener las vistas");
            return;
        }

        // Inicializar TokenManager
        tokenManager = new TokenManager(this, this);

        // Obtener token de SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("decorMiCasa", MODE_PRIVATE);
        String token = sharedPreferences.getString("tokenJWT", "");

        // Iniciar el timer
        if (!token.isEmpty()) {
            tokenManager.startTokenExpirationTimer(token);
        }

        // Acción al presionar el botón de productos
        btnProductos.setOnClickListener(v -> {
            Log.d(TAG, "Botón de productos clickeado");
            gridOptions.setVisibility(View.GONE);
            fragmentContainer.setVisibility(View.VISIBLE);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, new CrudProductosFragment());
            transaction.addToBackStack(null);
            transaction.commit();
        });

        // Acción al presionar el botón de empleados

        btnEmpleado.setOnClickListener(v -> {
            Log.d(TAG, "Botón de empleados clickeado");
            gridOptions.setVisibility(View.GONE);
            fragmentContainer.setVisibility(View.VISIBLE);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, new CrudUsuariosFragment());
            transaction.addToBackStack(null);
            transaction.commit();
        });

        btnCompras.setOnClickListener(v -> {
            Log.d(TAG, "Botón de productos clickeado");
            gridOptions.setVisibility(View.GONE);
            fragmentContainer.setVisibility(View.VISIBLE);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, new CrudComprasFragment());
            transaction.addToBackStack(null);
            transaction.commit();
        });

        // Listener para cambios en la pila de fragmentos (back stack)
        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            Log.d(TAG, "Back stack changed: " + getSupportFragmentManager().getBackStackEntryCount());
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                gridOptions.setVisibility(View.VISIBLE);
                fragmentContainer.setVisibility(View.GONE);
            }
        });


        btnUsuario.setOnClickListener(view -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Usuario")
                    .setMessage("¿Estás seguro que deseas cerrar sesión?")
                    .setPositiveButton("Cerrar sesión", (dialog, which) -> {
                        cerrarSesion();
                    })
                    .setNegativeButton("Cancelar", (dialog, which) -> {
                        dialog.dismiss();
                    });

            builder.create().show();
        });
        MaterialButton btnCategorias = findViewById(R.id.btnCategorias);


        if (btnCategorias == null || gridOptions == null || fragmentContainer == null) {
            Log.e(TAG, "Error al obtener las vistas");
            return;
        }

        btnCategorias.setOnClickListener(v -> {
            Log.d(TAG, "Botón de Categorias clickeado");
            gridOptions.setVisibility(View.GONE);
            fragmentContainer.setVisibility(View.VISIBLE);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, new CrudCategoriasFragment());
            transaction.addToBackStack(null);
            transaction.commit();
        });
        MaterialButton btnMarcas = findViewById(R.id.btnMarcas);


        if (btnMarcas == null || gridOptions == null || fragmentContainer == null) {
            Log.e(TAG, "Error al obtener las vistas");
            return;
        }

        btnMarcas.setOnClickListener(v -> {
            Log.d(TAG, "Botón de marcas clickeado");
            gridOptions.setVisibility(View.GONE);
            fragmentContainer.setVisibility(View.VISIBLE);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, new CrudMarcasFragment());
            transaction.addToBackStack(null);
            transaction.commit();
        });
    }

    private void cerrarSesion() {
        Intent intent = new Intent(this, LoginActivity.class); // Cambia a la actividad de login
        startActivity(intent);
        finish();
    }

    @Override
    public void onTokenWarning(long timeRemaining) {
        int minutesRemaining = (int) (timeRemaining / (1000 * 60));

        new android.app.AlertDialog.Builder(this)
                .setTitle("Sesión por expirar")
                .setMessage("Tu sesión expirará en " + minutesRemaining + " minutos.")
                .setNegativeButton("Cerrar sesión", (dialog, which) -> {
                    // Cerrar sesión automáticamente
                    tokenManager.logout();
                })
                .setCancelable(true)
                .create() // Crea el AlertDialog
                .show(); // Muestra el diálogo
    }

    @Override
    public void onTokenExpired() {
        Toast.makeText(this, "Tu sesión ha expirado", Toast.LENGTH_LONG).show();
        tokenManager.logout();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tokenManager.stopTimer();
    }
}
