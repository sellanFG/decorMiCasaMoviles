package com.example.decormicasa;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.example.decormicasa.login.LoginActivity;

public class NavigationManager {
    private final Activity activity;
    private DrawerLayout drawerLayout;
    private ImageButton btnMenu, btnCarrito, btnFav, btnShop, btnHome, btnUsuario;

    public NavigationManager(Activity activity) {
        this.activity = activity;
    }

    public void setupNavigation() {
        // Initialize buttons
        btnMenu = activity.findViewById(R.id.btnMenu);
        btnCarrito = activity.findViewById(R.id.btnCarrito);
        btnFav = activity.findViewById(R.id.btnFav);
        btnShop = activity.findViewById(R.id.btnShop);
        btnHome = activity.findViewById(R.id.btnHome);
        btnUsuario = activity.findViewById(R.id.btnUsuario);
        drawerLayout = activity.findViewById(R.id.drawer_layout);

        setupMenuButton();
        setupCartButton();
        setupFavButton();
        setupShopButton();
        setupHomeButton();
        setupUserButton();
    }

    private void setupMenuButton() {
        if (btnMenu != null && drawerLayout != null) {
            btnMenu.setOnClickListener(v -> {
                applyAnimation(v);
                v.postDelayed(() -> {
                    if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                        drawerLayout.closeDrawer(GravityCompat.START);
                    } else {
                        drawerLayout.openDrawer(GravityCompat.START);
                    }
                }, 300);
            });
        }
    }

    private void setupFavButton() {
        if (btnFav != null) {
            btnFav.setOnClickListener(v -> {
                applyAnimation(v);
                v.postDelayed(() -> {
                    Intent intent = new Intent(activity, ClienteActivity.class);
                    intent.putExtra("initialMode", "favoritos"); // Activar favoritos
                    activity.startActivity(intent);
                }, 300);
            });
        }
    }

    private void setupCartButton() {
        if (btnCarrito != null) {
            btnCarrito.setOnClickListener(v -> {
                applyAnimation(v);
                v.postDelayed(() -> {
                    Intent intent = new Intent(activity, ClienteActivity.class);
                    intent.putExtra("showCart", true);
                    activity.startActivity(intent);
                }, 300);
            });
        }
    }

    private void setupShopButton() {
        if (btnShop != null) {
            btnShop.setOnClickListener(v -> {
                applyAnimation(v);
                v.postDelayed(() -> {
                    Intent intent = new Intent(activity, ClienteActivity.class);
                    intent.putExtra("initialMode", "pedidos"); // Mostrar pedidos
                    activity.startActivity(intent);
                }, 300);
            });
        }
    }

    private void setupHomeButton() {
        if (btnHome != null) {
            btnHome.setOnClickListener(v -> {
                applyAnimation(v);
                v.postDelayed(() -> {
                    if (!(activity instanceof ClienteActivity)) {
                        Intent intent = new Intent(activity, ClienteActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        activity.startActivity(intent);
                    }
                }, 300);
            });
        }
    }

    private void setupUserButton() {
        if (btnUsuario != null) {
            btnUsuario.setOnClickListener(view -> {
                applyAnimation(view);
                showUserMenu(view);
            });
        }
    }

    private void showUserMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(activity, view);
        popupMenu.setGravity(Gravity.END);
        popupMenu.getMenuInflater().inflate(R.menu.menu_usuario, popupMenu.getMenu());

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

        popupMenu.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.opcion_editar) {
                Intent intent = new Intent(activity, EditarUsuarioActivity.class);
                activity.startActivity(intent);
                return true;
            } else if (id == R.id.opcion_cerrar_sesion) {
                showLogoutConfirmation();
                return true;
            }
            return false;
        });

        popupMenu.show();
    }

    private void showLogoutConfirmation() {
        new AlertDialog.Builder(activity)
                .setTitle("Usuario")
                .setMessage("¿Estás seguro que deseas cerrar sesión?")
                .setPositiveButton("Cerrar sesión", (dialog, which) -> {
                    SharedPreferences sharedPreferences = activity.getSharedPreferences("decorMiCasa", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.clear();
                    editor.apply();

                    Intent intent = new Intent(activity, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    activity.startActivity(intent);
                    activity.finish();
                })
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    private void applyAnimation(View view) {
        Animation slideBounce = AnimationUtils.loadAnimation(activity, R.anim.slide_bounce);
        view.startAnimation(slideBounce);
    }

    public void handleBackPress() {
        if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            activity.onBackPressed();
        }
    }
}