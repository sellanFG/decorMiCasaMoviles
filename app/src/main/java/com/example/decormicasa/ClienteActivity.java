package com.example.decormicasa;

import static java.security.AccessController.getContext;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.decormicasa.Interface.decorMiCasaApi;
import com.example.decormicasa.login.LoginActivity;
import com.example.decormicasa.model.CategoriaRequest;
import com.example.decormicasa.model.MarcaRequest;
import com.example.decormicasa.model.PedidoRequest;
import com.example.decormicasa.model.ProductoClienteRequest;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import android.widget.PopupMenu;


public class ClienteActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private ImageButton btnMenu;
    List<CategoriaRequest> listaCategorias = new ArrayList<>();
    List<MarcaRequest> listaMarcas = new ArrayList<>();
    List<ProductoClienteRequest> listaProductos = new ArrayList<>();
    private TextView txtAviso, txtTituloFavoritos;
    private AutoCompleteTextView actvCategoria, actvMarca;
    private SwipeRefreshLayout layoutProductos;
    private ProductoClienteAdapter productoClienteAdapter;
    private RecyclerView recyclerViewProductos;
    HorizontalScrollView layoutFiltros;
    MaterialButton btnLimpiar;
    String modo;
    ImageButton btnCarrito, btnFav, btnHome, btnUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cliente);

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
                Animation slideBounce = AnimationUtils.loadAnimation(ClienteActivity.this, R.anim.slide_bounce);
                findViewById(R.id.btnMenu).startAnimation(slideBounce);
                v.postDelayed(() -> {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
                }, 300); // Tiempo de la animación antes de mostrar el diálogo
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (id == R.id.nav_nosotros) {
                    Intent intent = new Intent(ClienteActivity.this, NosotrosActivity.class);
                    startActivity(intent);
                    drawerLayout.closeDrawer(GravityCompat.START);
                    return true;
                } else if (id == R.id.nav_privacidad) {
                    Intent intent = new Intent(ClienteActivity.this, PoliticaActivity.class);
                    startActivity(intent);
                    drawerLayout.closeDrawer(GravityCompat.START);
                    return true;
                } else if (id == R.id.nav_mapa) {
                    Intent intent = new Intent(ClienteActivity.this, MapActivity.class);
                    startActivity(intent);
                    drawerLayout.closeDrawer(GravityCompat.START);
                    return true;
                } else if (id == R.id.nav_home) {
                    Intent intent = new Intent(ClienteActivity.this, ClienteActivity.class);
                    startActivity(intent);
                    drawerLayout.closeDrawer(GravityCompat.START);
                    return true;
                }
                return false;
            }
        });

        modo = "listado";
        actvMarca = findViewById(R.id.actvMarca);
        actvCategoria = findViewById(R.id.actvCategoria);
        layoutProductos = findViewById(R.id.layoutProductos);
        recyclerViewProductos = findViewById(R.id.recyclerViewProductos);
        txtAviso = findViewById(R.id.txtAviso);
        txtTituloFavoritos = findViewById(R.id.txtTituloFavoritos);
        btnLimpiar = findViewById(R.id.btnLimpiar);
        btnCarrito = findViewById(R.id.btnCarrito);
        layoutFiltros = findViewById(R.id.layoutFiltros);
        btnFav = findViewById(R.id.btnFav);
        btnHome = findViewById(R.id.btnHome);

        layoutFiltros.setVisibility(View.VISIBLE);
        productoClienteAdapter = new ProductoClienteAdapter(ClienteActivity.this, listaProductos);
        recyclerViewProductos.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewProductos.setAdapter(productoClienteAdapter);
        mostrarProductos();
        llenarCategorias();
        llenarMarcas();

        layoutProductos.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (modo.equals("listado")){
                    mostrarProductos();
                }else{
                    mostrarFavoritos();
                }
                layoutProductos.setRefreshing(false);
            }
        });

        //Limpiar los filtros
        btnLimpiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actvCategoria.setText("");
                actvCategoria.clearFocus();
                actvMarca.setText("");
                actvMarca.clearFocus();
                mostrarProductos();
            }
        });

        //Actualizar los productos al seleccionar una marca o categoria
        actvMarca.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mostrarProductos();
            }
        });
        actvCategoria.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mostrarProductos();
            }
        });

        //Abrir carrito y guardar venta
        btnCarrito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // animación
                Animation slideBounce = AnimationUtils.loadAnimation(ClienteActivity.this, R.anim.slide_bounce);
                findViewById(R.id.btnCarrito).startAnimation(slideBounce);

                v.postDelayed(() -> {
                //Validar shay productos en el carrito
                int cantidadProductosCarrito = productoClienteAdapter.carrito.size();
                if (cantidadProductosCarrito == 0){
                    Toast.makeText(ClienteActivity.this, "Agregue productos al carrito", Toast.LENGTH_SHORT).show();
                    return;
                }
                SharedPreferences sharedPreferences = getSharedPreferences("decorMiCasa", Context.MODE_PRIVATE);
                int id_cliente = sharedPreferences.getInt("id_cliente", 0);

                Dialog dialog = new Dialog(ClienteActivity.this);
                dialog.setContentView(R.layout.dialog_carrito);
                dialog.setCancelable(true);

                //Declarar y enlazar los controles
                TextView txtProductosVenta = dialog.findViewById(R.id.txtProductosVenta);
                TextInputEditText txtSubTotal = dialog.findViewById(R.id.txtSubTotal);
                TextInputLayout input_layout_monto_igv = dialog.findViewById(R.id.input_layout_monto_igv);
                TextInputEditText txtMontoIGV = dialog.findViewById(R.id.txtMontoIGV);
                TextInputEditText txtTotal = dialog.findViewById(R.id.txtTotal);
                MaterialButton btnGuardarPedido = dialog.findViewById(R.id.btnGuardarPedido);
                MaterialButton btnSalir = dialog.findViewById(R.id.btnSalir);
                AutoCompleteTextView actvMetodoPago = dialog.findViewById(R.id.actvMetodoPago);

                //Mostrar los productos agregados al carrito
                txtProductosVenta.setText("");
                for(ProductoClienteRequest producto: productoClienteAdapter.carrito){
                    txtProductosVenta.append("(" + producto.getCantidad() + ") " + producto.getNombre() + "\n");
                }

                // Llenar métodos de pago
                List<String> metodosPago = Arrays.asList("Tarjeta de Crédito", "Tarjeta de Débito", "Yape", "Efectivo");
                ArrayAdapter<String> adapterMetodo = new ArrayAdapter<>(ClienteActivity.this, android.R.layout.simple_dropdown_item_1line, metodosPago);
                actvMetodoPago.setAdapter(adapterMetodo);

                //Mostrar el porcentaje de IGV
                input_layout_monto_igv.setHint("IGV(18%)");

                //Mostrar los totales de la venta
                double[] totales = productoClienteAdapter.calcularTotales();
                txtSubTotal.setText(formatearNumero(totales[0]));
                txtMontoIGV.setText(formatearNumero(totales[1]));
                txtTotal.setText(formatearNumero(totales[2]));


                //Implementar el botón salir
                btnSalir.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                btnGuardarPedido.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (actvMetodoPago.getText().toString().isEmpty()){
                            Toast.makeText(ClienteActivity.this, "Ingrese el método de pago", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        btnGuardarPedido.setBackgroundColor(getResources().getColor(R.color.primaryBlueDesactivado));
                        btnGuardarPedido.setClickable(false);

                        guardarPedido(actvMetodoPago.getText().toString(), dialog, btnGuardarPedido);

                    }
                });

                //Mostrar el dialog
                dialog.show();
                }, 300); // Tiempo de la animación antes de mostrar el diálogo
            }
        });

        ImageButton btnShop = findViewById(R.id.btnShop);

        btnShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Aplicar animación al botón
                Animation slideBounce = AnimationUtils.loadAnimation(ClienteActivity.this, R.anim.slide_bounce);
                findViewById(R.id.btnShop).startAnimation(slideBounce);

                // Retraso para asegurar que la animación se ejecute antes de ocultar y mostrar vistas
                v.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Ocultar vistas de Home y Favoritos
                        layoutFiltros.setVisibility(View.GONE);
                        txtTituloFavoritos.setVisibility(View.GONE);
                        findViewById(R.id.layoutProductos).setVisibility(View.GONE);

                        // Mostrar vistas de Pedidos
                        findViewById(R.id.layoutPedidos).setVisibility(View.VISIBLE);

                        // Llamar a la función para mostrar los pedidos
                        mostrarPedidos(); // Este método debe cargar los pedidos en el RecyclerView

                        // Actualizar el estado de la pantalla (modo)
                        modo = "pedidos";
                    }
                }, 300); // Tiempo de la animación antes de cambiar la visibilidad
            }
        });


        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation slideBounce = AnimationUtils.loadAnimation(ClienteActivity.this, R.anim.slide_bounce);
                findViewById(R.id.btnHome).startAnimation(slideBounce);

                v.postDelayed(() -> {
                    // Mostrar vistas de Home
                    layoutFiltros.setVisibility(View.VISIBLE);
                    layoutProductos.setVisibility(View.VISIBLE);
                    // Ocultar vistas de Favoritos y Pedidos
                    txtTituloFavoritos.setVisibility(View.GONE);
                    findViewById(R.id.layoutPedidos).setVisibility(View.GONE);

                    mostrarProductos(); // Mostrar productos en Home
                    modo = "listado"; // Cambiar el modo
                }, 300); // Tiempo de la animación antes de mostrar el diálogo
            }
        });

        //ir a favoritos
        btnFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation slideBounce = AnimationUtils.loadAnimation(ClienteActivity.this, R.anim.slide_bounce);
                findViewById(R.id.btnFav).startAnimation(slideBounce);

                v.postDelayed(() -> {
                    layoutFiltros.setVisibility(View.GONE);
                    findViewById(R.id.layoutPedidos).setVisibility(View.GONE);

                    txtTituloFavoritos.setVisibility(View.VISIBLE);

                    mostrarFavoritos();
                    layoutProductos.setVisibility(View.VISIBLE);
                    modo = "favoritos";
                }, 300);
            }
        });

        // Inicializar el botón de usuario
        ImageButton btnUsuario = findViewById(R.id.btnUsuario);

        // Configurar el PopupMenu para el botón de usuario
            btnUsuario.setOnClickListener(view -> {
                aplicarAnimacion(view);
            // Crear el PopupMenu anclado al botón de usuario
            PopupMenu popupMenu = new PopupMenu(ClienteActivity.this, view);
            popupMenu.setGravity(Gravity.END); // Desplegar desde el lado derecho
            popupMenu.getMenuInflater().inflate(R.menu.menu_usuario, popupMenu.getMenu());

            // Forzar la visualización de íconos en el PopupMenu
            try {
                java.lang.reflect.Field popup = PopupMenu.class.getDeclaredField("mPopup");
                popup.setAccessible(true);
                Object menuHelper = popup.get(popupMenu);
                Class<?> classPopupHelper = Class.forName(menuHelper.getClass().getName());
                java.lang.reflect.Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                setForceIcons.invoke(menuHelper, true);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Manejar las opciones del menú
            popupMenu.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();

                if (id == R.id.opcion_editar) {
                    Intent intent = new Intent(ClienteActivity.this, EditarUsuarioActivity.class);
                    startActivity(intent);
                    return true;
                } else if (id == R.id.opcion_cerrar_sesion) {
                    // Aquí añade la lógica para cerrar sesión
                    Toast.makeText(ClienteActivity.this, "Cerrar Sesión", Toast.LENGTH_SHORT).show();
                    cerrarSesion();
                    return true;
                }
                return false;
            });
            popupMenu.show();
        });
    }

    private void inicializarComponentes() {
        // Enlazar vistas
        actvMarca = findViewById(R.id.actvMarca);
        actvCategoria = findViewById(R.id.actvCategoria);
        layoutProductos = findViewById(R.id.layoutProductos);
        recyclerViewProductos = findViewById(R.id.recyclerViewProductos);
        txtAviso = findViewById(R.id.txtAviso);
        txtTituloFavoritos = findViewById(R.id.txtTituloFavoritos);
        btnLimpiar = findViewById(R.id.btnLimpiar);
        btnCarrito = findViewById(R.id.btnCarrito);
        layoutFiltros = findViewById(R.id.layoutFiltros);
        btnFav = findViewById(R.id.btnFav);
        btnHome = findViewById(R.id.btnHome);

        // Configuración del RecyclerView
        productoClienteAdapter = new ProductoClienteAdapter(this, listaProductos);
        recyclerViewProductos.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewProductos.setAdapter(productoClienteAdapter);

        // Configurar SwipeRefreshLayout para actualizar datos
        layoutProductos.setOnRefreshListener(() -> {
            if ("listado".equals(modo)) {
                mostrarProductos();
            } else if ("favoritos".equals(modo)) {
                mostrarFavoritos();
            }
            layoutProductos.setRefreshing(false);
        });

        // Configurar el botón de limpiar filtros
        btnLimpiar.setOnClickListener(v -> {
            actvCategoria.setText("");
            actvCategoria.clearFocus();
            actvMarca.setText("");
            actvMarca.clearFocus();
            mostrarProductos();
        });

        // Configuración inicial de productos
        mostrarProductos();
    }

    private void setupNavigation() {
        NavigationManager navigationManager = new NavigationManager(this);
        navigationManager.setupNavigation();
    }

    private void cerrarSesion() {
        // Mostrar el diálogo de confirmación
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Usuario")
                .setMessage("¿Estás seguro que deseas cerrar sesión?")
                .setPositiveButton("Cerrar sesión", (dialog, which) -> {
                    // Aquí se ejecuta el cierre de sesión al confirmar
                    SharedPreferences sharedPreferences = getSharedPreferences("decorMiCasa", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.clear();
                    editor.apply();

                    // Redirigir al usuario a la pantalla de inicio de sesión
                    Intent intent = new Intent(ClienteActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Cancelar", (dialog, which) -> {
                    dialog.dismiss(); // Cerrar el diálogo sin hacer nada
                });

        // Mostrar el cuadro de diálogo
        builder.create().show();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    void llenarCategorias() {

        SharedPreferences sharedPreferences = getSharedPreferences("decorMiCasa", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("tokenJWT", "");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.dominioservidor))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        decorMiCasaApi api = retrofit.create(decorMiCasaApi.class);

        if (token == null || token.isEmpty()) {
            Toast.makeText(ClienteActivity.this, "Token no disponible. Inicie sesión nuevamente.", Toast.LENGTH_SHORT).show();
            return;
        }

        Call<List<CategoriaRequest>> call = api.obtenercategorias("JWT " + token);
        call.enqueue(new Callback<List<CategoriaRequest>>() {
            @Override
            public void onResponse(@NonNull Call<List<CategoriaRequest>> call, @NonNull Response<List<CategoriaRequest>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaCategorias.clear();
                    listaCategorias.addAll(response.body());
                    ArrayList<String> nombresCategorias = new ArrayList<>();
                    for (CategoriaRequest cat : listaCategorias){
                        nombresCategorias.add(cat.getNombre());
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(ClienteActivity.this, R.layout.list_item, nombresCategorias);
                    actvCategoria.setAdapter(adapter);

                } else {
                    Toast.makeText(ClienteActivity.this, "Error al cargar categorias", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<CategoriaRequest>> call, @NonNull Throwable t) {
                Toast.makeText(ClienteActivity.this, "Fallo en la conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    void llenarMarcas() {

        SharedPreferences sharedPreferences = getSharedPreferences("decorMiCasa", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("tokenJWT", "");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.dominioservidor))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        decorMiCasaApi api = retrofit.create(decorMiCasaApi.class);

        if (token == null || token.isEmpty()) {
            Toast.makeText(ClienteActivity.this, "Token no disponible. Inicie sesión nuevamente.", Toast.LENGTH_LONG).show();
            return;
        }

        Call<List<MarcaRequest>> call = api.obtenermarcas("JWT " + token);
        call.enqueue(new Callback<List<MarcaRequest>>() {
            @Override
            public void onResponse(@NonNull Call<List<MarcaRequest>> call, @NonNull Response<List<MarcaRequest>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaMarcas.clear();
                    listaMarcas.addAll(response.body());
                    ArrayList<String> nombresMarcas = new ArrayList<>();
                    for (MarcaRequest marc : listaMarcas){
                        nombresMarcas.add(marc.getNombre());
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(ClienteActivity.this, R.layout.list_item, nombresMarcas);
                    actvMarca.setAdapter(adapter);

                } else {
                    Toast.makeText(ClienteActivity.this, "Error al cargar marcas", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(@NonNull Call<List<MarcaRequest>> call, @NonNull Throwable t) {
                Toast.makeText(ClienteActivity.this, "Fallo en la conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("ERROR", "onResponse: "+t.getMessage());
            }
        });
    }

    void mostrarPedidos() {
        List<PedidoRequest> pedidos = new ArrayList<>();
        SharedPreferences sharedPreferences = getSharedPreferences("decorMiCasa", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("tokenJWT", "");

        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "Sesión expirada. Por favor, inicie sesión nuevamente.", Toast.LENGTH_LONG).show();
            return;
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.dominioservidor))
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

                    RecyclerView recyclerViewPedidos = findViewById(R.id.recyclerViewPedidos);
                    PedidosAdapter adapter = new PedidosAdapter(pedidos, ClienteActivity.this);
                    recyclerViewPedidos.setLayoutManager(new LinearLayoutManager(ClienteActivity.this));
                    recyclerViewPedidos.setAdapter(adapter);
                } else {
                    Toast.makeText(ClienteActivity.this, "Error al cargar los pedidos.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<PedidoRequest>> call, @NonNull Throwable t) {
                Toast.makeText(ClienteActivity.this, "Fallo en la conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    void mostrarProductos(){
        layoutFiltros.setVisibility(View.VISIBLE);
        txtTituloFavoritos.setVisibility(View.GONE);
        listaProductos.clear();

        List<ProductoClienteRequest> marcas = new ArrayList<>();

        SharedPreferences sharedPreferences = getSharedPreferences("decorMiCasa", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("tokenJWT", "");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.dominioservidor))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        decorMiCasaApi api = retrofit.create(decorMiCasaApi.class);

        if (token == null || token.isEmpty()) {
            Toast.makeText(ClienteActivity.this, "Token no disponible. Inicie sesión nuevamente.", Toast.LENGTH_LONG).show();
            return;
        }

        //obtener categoria y marca (ambos opcionales)
        Integer idCategoria = null;
        Integer idMarca = null;
        if (!actvCategoria.getText().toString().isEmpty()){
            for (CategoriaRequest cat : listaCategorias){
                if (cat.getNombre().equals(actvCategoria.getText().toString())){
                    idCategoria = cat.getIdCategoria();
                    Log.e("CATEGORIA", " "+idCategoria);
                    break;
                }
            }
        }
        if (!actvMarca.getText().toString().isEmpty()){
            for (MarcaRequest marc : listaMarcas){
                if (marc.getNombre().equals(actvMarca.getText().toString())){
                    idMarca = marc.getIdMarca();
                    Log.e("MARCA", " "+idMarca);

                    break;
                }
            }
        }
        Log.e("CATEGORIA", " "+idCategoria);
        Log.e("MARCA", " "+idMarca);
        Call<List<ProductoClienteRequest>> call = api.obtenerproductoscliente("JWT " + token, idCategoria, idMarca);

        call.enqueue(new Callback<List<ProductoClienteRequest>>() {
            @Override
            public void onResponse(@NonNull Call<List<ProductoClienteRequest>> call, @NonNull Response<List<ProductoClienteRequest>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaProductos.clear();
                    listaProductos.addAll(response.body());
                    productoClienteAdapter.notifyDataSetChanged();
                    if (listaProductos.isEmpty()) {
                        txtAviso.setText("No se encontraron productos de dicha categoría y/o marca");
                        txtAviso.setVisibility(View.VISIBLE);
                    }else{
                        txtAviso.setVisibility(View.GONE);
                    }
                } else {
                    Toast.makeText(ClienteActivity.this, "Error al cargar productos", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ProductoClienteRequest>> call, @NonNull Throwable t) {
                Toast.makeText(ClienteActivity.this, "Fallo en la conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("ERROR", "onResponse: "+t.getMessage());
            }
        });
    }

    void guardarPedido(String metodoPago, Dialog dialog, MaterialButton btnGuardarPedido){
        SharedPreferences sharedPreferences = getSharedPreferences("decorMiCasa", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("tokenJWT", "");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.dominioservidor))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        decorMiCasaApi api = retrofit.create(decorMiCasaApi.class);

        if (token == null || token.isEmpty()) {
            Toast.makeText(ClienteActivity.this, "Token no disponible. Inicie sesión nuevamente.", Toast.LENGTH_LONG).show();
            return;
        }

        //Obtener el id del cliente
        int id_cliente = sharedPreferences.getInt("id_cliente", 0);
        double total = productoClienteAdapter.calcularTotales()[2];
        double igv = productoClienteAdapter.calcularTotales()[1];
        //generar el detalle de venta en JSON
        JSONArray jsonArrayDetalleVenta = new JSONArray();
        for(ProductoClienteRequest producto: productoClienteAdapter.carrito){
            jsonArrayDetalleVenta.put(producto.getJSONObjectProducto());
        }
        String detalleVentaJSON = jsonArrayDetalleVenta.toString();


        Call<PedidoRequest> call = api.guardarpedido("JWT " + token, id_cliente, total, igv, metodoPago, detalleVentaJSON);
        call.enqueue(new Callback<PedidoRequest>() {
            @Override
            public void onResponse(@NonNull Call<PedidoRequest> call, @NonNull Response<PedidoRequest> response) {
                btnGuardarPedido.setBackgroundColor(getResources().getColor(R.color.primaryBlue));
                btnGuardarPedido.setClickable(true);
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(ClienteActivity.this, "Pedido guardado correctamente", Toast.LENGTH_SHORT).show();
                    productoClienteAdapter.carrito.clear();
                    productoClienteAdapter.notifyDataSetChanged();
                    mostrarProductos();
                    dialog.dismiss();
                } else {
                    Toast.makeText(ClienteActivity.this, "Error al guardar pedido", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<PedidoRequest> call, @NonNull Throwable t) {
                btnGuardarPedido.setBackgroundColor(getResources().getColor(R.color.primaryBlue));
                btnGuardarPedido.setClickable(true);
                Toast.makeText(ClienteActivity.this, "Fallo en la conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("ERROR", t.getMessage());
            }
        });
    }

    void mostrarFavoritos() {
        listaProductos.clear();  // Asegúrate de limpiar la lista antes de agregar los nuevos datos

        SharedPreferences prefs = getSharedPreferences("FavoritosPrefs", MODE_PRIVATE);
        Set<String> favoritos = prefs.getStringSet("favoritos", new HashSet<>());

        if (favoritos != null && !favoritos.isEmpty()) {
            for (String productoString : favoritos) {
                String[] params = productoString.split(";");
                ProductoClienteRequest producto = new ProductoClienteRequest();
                producto.setIdProducto(Integer.parseInt(params[0]));
                producto.setNombre(params[1]);
                producto.setStock(Integer.parseInt(params[2]));
                producto.setPrecioVenta(Float.parseFloat(params[3]));
                listaProductos.add(producto);
            }

            // Asegúrate de actualizar el RecyclerView
            productoClienteAdapter.notifyDataSetChanged();

            // Si no hay productos en favoritos
            if (listaProductos.isEmpty()) {
                txtAviso.setText("No tienes productos favoritos.");
                txtAviso.setVisibility(View.VISIBLE);
            } else {
                txtAviso.setVisibility(View.GONE);
            }
        } else {
            txtAviso.setText("No hay favoritos guardados.");
            txtAviso.setVisibility(View.VISIBLE);
        }
    }

    public static String formatearNumero(final double numero){
        final DecimalFormatSymbols simbolos = new DecimalFormatSymbols();
        simbolos.setDecimalSeparator('.');
        simbolos.setGroupingSeparator(',');

        final DecimalFormat formato = new DecimalFormat("###,##0.0", simbolos);

        return formato.format(numero);

    }

    private void aplicarAnimacion(View view) {
        // Cargar la animación desde el archivo de recursos
        Animation slideBounce = AnimationUtils.loadAnimation(ClienteActivity.this, R.anim.slide_bounce);
        // Iniciar la animación en la vista
        view.startAnimation(slideBounce);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);  // Actualiza el Intent

        String initialMode = intent.getStringExtra("initialMode");
        if ("favoritos".equals(initialMode)) {
            mostrarFavoritos();  // Llama al método para mostrar favoritos
        } else {
            mostrarProductos();  // Muestra la pantalla principal de productos
        }
    }
}

