package com.example.decormicasa;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import com.google.android.material.button.MaterialButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

public class AdminActivity extends AppCompatActivity {

    private static final String TAG = "AdminActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // Cambiar el tipo de ImageButton a MaterialButton
        MaterialButton btnProductos = findViewById(R.id.btnProductos);
        View gridOptions = findViewById(R.id.gridOptions);
        View fragmentContainer = findViewById(R.id.fragment_container);

        if (btnProductos == null || gridOptions == null || fragmentContainer == null) {
            Log.e(TAG, "Error al obtener las vistas");
            return;
        }

        // Agregar evento de clic para el botón de productos
        btnProductos.setOnClickListener(v -> {
            Log.d(TAG, "Botón de productos clickeado");
            gridOptions.setVisibility(View.GONE); // Ocultar los botones
            fragmentContainer.setVisibility(View.VISIBLE); // Mostrar el contenedor del fragmento

            // Transacción para reemplazar el fragmento
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, new CrudProductosFragment());
            transaction.addToBackStack(null); // Agrega a la pila para retroceso
            transaction.commit();
        });

        // Listener para manejar el retroceso en la pila de fragmentos
        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            Log.d(TAG, "Back stack changed: " + getSupportFragmentManager().getBackStackEntryCount());
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                gridOptions.setVisibility(View.VISIBLE); // Volver a mostrar los botones
                fragmentContainer.setVisibility(View.GONE); // Ocultar el fragmento
            }
        });
    }
}
