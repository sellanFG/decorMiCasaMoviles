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

        MaterialButton btnProductos = findViewById(R.id.btnProductos);
        View gridOptions = findViewById(R.id.gridOptions);
        View fragmentContainer = findViewById(R.id.fragment_container);

        if (btnProductos == null || gridOptions == null || fragmentContainer == null) {
            Log.e(TAG, "Error al obtener las vistas");
            return;
        }

        btnProductos.setOnClickListener(v -> {
            Log.d(TAG, "Botón de productos clickeado");
            gridOptions.setVisibility(View.GONE);
            fragmentContainer.setVisibility(View.VISIBLE);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, new CrudProductosFragment());
            transaction.addToBackStack(null);
            transaction.commit();
        });

        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            Log.d(TAG, "Back stack changed: " + getSupportFragmentManager().getBackStackEntryCount());
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                gridOptions.setVisibility(View.VISIBLE);
                fragmentContainer.setVisibility(View.GONE);
            }
        });
    }
}
