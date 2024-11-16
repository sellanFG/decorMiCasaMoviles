package com.example.decormicasa;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class PoliticaActivity extends AppCompatActivity  {
    private ImageButton btnMenu;
    private DrawerLayout drawerLayout;
    private TextView sectionContent;
    private ImageView arrowIcon;
    private boolean isExpanded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacidad);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

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

                if (id == R.id.nav_categorias) {
                    Intent intent = new Intent(PoliticaActivity.this, CategoriasActivity.class);
                    startActivity(intent);
                    drawerLayout.closeDrawer(GravityCompat.START);
                    return true;
                } else if (id == R.id.nav_nosotros) {
                    Intent intent = new Intent(PoliticaActivity.this, NosotrosActivity.class);
                    startActivity(intent);
                    drawerLayout.closeDrawer(GravityCompat.START);
                    return true;
                } else if (id == R.id.nav_privacidad) {
                    Intent intent = new Intent(PoliticaActivity.this, PoliticaActivity.class);
                    startActivity(intent);
                    drawerLayout.closeDrawer(GravityCompat.START);
                    return true;
                } else if (id == R.id.nav_mapa) {
                    Intent intent = new Intent(PoliticaActivity.this, MapActivity.class);
                    startActivity(intent);
                    drawerLayout.closeDrawer(GravityCompat.START);
                    return true;
                }
                return false;
            }
        });
        sectionContent = findViewById(R.id.sectionContent);
        arrowIcon = findViewById(R.id.arrowIcon);

        findViewById(R.id.toggleCard).setOnClickListener(v -> toggleSection());
    }

    private void toggleSection() {
        if (isExpanded) {
            sectionContent.setVisibility(View.GONE);
            Animation rotateDown = AnimationUtils.loadAnimation(this, R.anim.rotate_down);
            arrowIcon.startAnimation(rotateDown);
        } else {
            sectionContent.setVisibility(View.VISIBLE);
            Animation rotateUp = AnimationUtils.loadAnimation(this, R.anim.rotate_up);
            arrowIcon.startAnimation(rotateUp);
        }
        isExpanded = !isExpanded;
    }
}