package com.example.decormicasa;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class CategoriasActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categorias);
    }

    public void onCategoryClick(View view) {
        int id = view.getId();
        if (id == R.id.categoria1_image) {
            Toast.makeText(this, "Seleccionaste Categoría 1", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.categoria2_image) {
            Toast.makeText(this, "Seleccionaste Categoría 2", Toast.LENGTH_SHORT).show();
        }
    }
}
