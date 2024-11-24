package com.example.decormicasa;

import androidx.annotation.NonNull;

import com.example.decormicasa.Interface.decorMiCasaApi;
import com.example.decormicasa.model.PedidoRequest;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NosotrosActivity extends AppCompatActivity {
    private NavigationManager navigationManager;
    private BottomNavigationView bottomNavigationView;
    private ImageButton btnMenu;
    private DrawerLayout drawerLayout;
    private ViewPager2 viewPager2;
    private int[] sampleImages = {R.drawable.banner1, R.drawable.carrusel_img1, R.drawable.carrusel_img2};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nosotros);

        // Inicializar el BottomNavigationView
        bottomNavigationView = findViewById(R.id.bottomNavigation);

        // Inicializar el NavigationManager después de setContentView
        navigationManager = new NavigationManager(this);
        navigationManager.setupNavigation();

        //
        NavigationView navigationView = findViewById(R.id.nav_view);
        drawerLayout = findViewById(R.id.drawer_layout);

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

        // Configurar el listener para el BottomNavigationView
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                // Cerrar el menú lateral antes de cambiar de actividad
                if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START); // Cierra el menú lateral si está abierto
                }
                // Acciones para cada opción del BottomNavigationView
                if (id == R.id.btnHome) {
                    // Acción para el botón de "Home"
                    Intent homeIntent = new Intent(NosotrosActivity.this, ClienteActivity.class);
                    startActivity(homeIntent);
                    return true;
                } else if (id == R.id.btnFav) {
                    // Acción para el botón de "Favoritos"
                    Intent favIntent = new Intent(NosotrosActivity.this, ClienteActivity.class);
                    favIntent.putExtra("initialMode", "favoritos"); // Enviar el modo de favoritos
                    startActivity(favIntent);
                    return true;
                } else if (id == R.id.btnShop) {
                    mostrarPedidos();
                    return true;
                }
                return false;
            }
        });

        // Manejo de opciones del menú lateral
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                int id = menuItem.getItemId();
                // Cerrar el menú lateral antes de cambiar de actividad
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
                if (id == R.id.nav_nosotros) {
                    Intent intent = new Intent(NosotrosActivity.this, NosotrosActivity.class);
                    startActivity(intent);
                    drawerLayout.closeDrawer(GravityCompat.START);
                    return true;
                } else if (id == R.id.nav_privacidad) {
                    Intent intent = new Intent(NosotrosActivity.this, PoliticaActivity.class);
                    startActivity(intent);
                    drawerLayout.closeDrawer(GravityCompat.START);
                    return true;
                } else if (id == R.id.nav_mapa) {
                    Intent intent = new Intent(NosotrosActivity.this, MapActivity.class);
                    startActivity(intent);
                    drawerLayout.closeDrawer(GravityCompat.START);
                    return true;
                } else if (id == R.id.nav_home) {
                    Intent intent = new Intent(NosotrosActivity.this, ClienteActivity.class);
                    startActivity(intent);
                    drawerLayout.closeDrawer(GravityCompat.START);
                    return true;
                }
                return false;
            }
        });

        // Configuración del ViewPager2 para carrusel
        viewPager2 = findViewById(R.id.viewPager2);
        CarouselAdapter carouselAdapter = new CarouselAdapter(sampleImages);
        viewPager2.setAdapter(carouselAdapter);

        // Desliza automáticamente cada 3 segundos
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                int currentItem = viewPager2.getCurrentItem();
                int nextItem = (currentItem + 1) % sampleImages.length;
                viewPager2.setCurrentItem(nextItem, true);
                new Handler().postDelayed(this, 3000);
            }
        }, 3000);
    }

    private void mostrarPedidos() {
        // Primero, ocultar otros layouts
        findViewById(R.id.layoutFiltros).setVisibility(View.GONE);
        findViewById(R.id.layoutProductos).setVisibility(View.GONE);

        // Mostrar layout de pedidos
        findViewById(R.id.layoutPedidos).setVisibility(View.VISIBLE);

        List<PedidoRequest> pedidos = new ArrayList<>();
        SharedPreferences sharedPreferences = getSharedPreferences("decorMiCasa", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("tokenJWT", "");

        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "Sesión expirada. Por favor, inicie sesión nuevamente.", Toast.LENGTH_LONG).show();
            return;
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.dominioservidor)) // Asegúrate de tener la URL base configurada correctamente
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        decorMiCasaApi api = retrofit.create(decorMiCasaApi.class);
        Call<List<PedidoRequest>> call = api.obtenerPedidos("JWT " + token);

        call.enqueue(new Callback<List<PedidoRequest>>() {
            @Override
            public void onResponse(@NonNull Call<List<PedidoRequest>> call, @NonNull Response<List<PedidoRequest>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    pedidos.clear();
                    pedidos.addAll(response.body());

                    // Configura el RecyclerView para mostrar los pedidos
                    RecyclerView recyclerViewPedidos = findViewById(R.id.recyclerViewPedidos);
                    PedidosAdapter adapter = new PedidosAdapter(pedidos, NosotrosActivity.this);
                    recyclerViewPedidos.setLayoutManager(new LinearLayoutManager(NosotrosActivity.this));
                    recyclerViewPedidos.setAdapter(adapter);
                } else {
                    Toast.makeText(NosotrosActivity.this, "Error al cargar los pedidos.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<PedidoRequest>> call, @NonNull Throwable t) {
                Toast.makeText(NosotrosActivity.this, "Fallo en la conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.main, fragment);
        transaction.commit();
    }

    private class CarouselAdapter extends androidx.recyclerview.widget.RecyclerView.Adapter<CarouselAdapter.ViewHolder> {
        private final int[] images;

        public CarouselAdapter(int[] images) {
            this.images = images;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ImageView imageView = new ImageView(parent.getContext());
            imageView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            return new ViewHolder(imageView);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.imageView.setImageResource(images[position]);
        }

        @Override
        public int getItemCount() {
            return images.length;
        }

        public class ViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
            ImageView imageView;

            public ViewHolder(View itemView) {
                super(itemView);
                imageView = (ImageView) itemView;
            }
        }
    }
}
