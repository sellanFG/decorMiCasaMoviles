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

import java.util.HashMap;
import java.util.Map;

public class PoliticaActivity extends AppCompatActivity {
    private NavigationManager navigationManager;
    private ImageButton btnMenu;
    private DrawerLayout drawerLayout;
    private TextView sectionContent, sectionContent1, sectionContent2;
    private ImageView arrowIcon, arrowIcon1, arrowIcon2;
    private boolean isExpanded, isExpanded1 = false, isExpanded2 = false;

    private Map<Integer, Class<?>> navigationMap;

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

        // Inicializar el NavigationManager después de setContentView
        navigationManager = new NavigationManager(this);
        navigationManager.setupNavigation();

        btnMenu = findViewById(R.id.btnMenu);
        btnMenu.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        // Inicializar el mapa de navegación
        navigationMap = new HashMap<>();
        navigationMap.put(R.id.nav_nosotros, NosotrosActivity.class);
        navigationMap.put(R.id.nav_privacidad, PoliticaActivity.class);
        navigationMap.put(R.id.nav_mapa, MapActivity.class);
        navigationMap.put(R.id.nav_home, ClienteActivity.class);

        // Aquí solo necesitamos una vez el setNavigationItemSelectedListener
        navigationView.setNavigationItemSelectedListener(menuItem -> {
            handleNavigation(menuItem);
            return true;
        });

        // Obtener las referencias de los nuevos elementos
        sectionContent = findViewById(R.id.sectionContent);
        sectionContent1 = findViewById(R.id.sectionContent1);
        sectionContent2 = findViewById(R.id.sectionContent2);
        arrowIcon = findViewById(R.id.arrowIcon);
        arrowIcon1 = findViewById(R.id.arrowIcon1);
        arrowIcon2 = findViewById(R.id.arrowIcon2);

        // Setear los listeners para las nuevas tarjetas
        findViewById(R.id.toggleCard).setOnClickListener(v -> toggleSection());
        findViewById(R.id.toggleCard1).setOnClickListener(v -> toggleSection1());
        findViewById(R.id.toggleCard2).setOnClickListener(v -> toggleSection2());
    }

    private void handleNavigation(MenuItem menuItem) {
        Class<?> activityClass = navigationMap.get(menuItem.getItemId());

        if (activityClass != null) {
            Intent intent = new Intent(PoliticaActivity.this, activityClass);
            startActivity(intent);
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    private void toggleSection() {
        toggleSection(sectionContent, arrowIcon, isExpanded);
        isExpanded = !isExpanded;
    }

    private void toggleSection1() {
        toggleSection(sectionContent1, arrowIcon1, isExpanded1);
        isExpanded1 = !isExpanded1;
    }

    private void toggleSection2() {
        toggleSection(sectionContent2, arrowIcon2, isExpanded2);
        isExpanded2 = !isExpanded2;
    }

    private void toggleSection(TextView sectionContent, ImageView arrowIcon, boolean isExpanded) {
        if (isExpanded) {
            sectionContent.setVisibility(View.GONE);
            Animation rotateDown = AnimationUtils.loadAnimation(this, R.anim.rotate_down);
            arrowIcon.startAnimation(rotateDown);
        } else {
            sectionContent.setVisibility(View.VISIBLE);
            Animation rotateUp = AnimationUtils.loadAnimation(this, R.anim.rotate_up);
            arrowIcon.startAnimation(rotateUp);
        }
    }
}