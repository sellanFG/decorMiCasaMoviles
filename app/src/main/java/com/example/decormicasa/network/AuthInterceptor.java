package com.example.decormicasa.network;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.decormicasa.login.LoginActivity;

import org.json.JSONObject;  // Necesario para decodificar el token

public class AuthInterceptor implements Interceptor {

    private Context context;

    public AuthInterceptor(Context context) {
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        // Obtener la solicitud original
        Request originalRequest = chain.request();

        // Obtener el token almacenado
        String token = getTokenFromSharedPreferences();

        if (token != null) {
            // Verificar si el token está por expirar
            if (isTokenExpiringSoon(token)) {
                // Realizar logout y redirigir a LoginActivity
                logout();
                redirectToLogin();
            }

            // Modificar la solicitud para agregar el token en el encabezado
            Request.Builder requestBuilder = originalRequest.newBuilder()
                    .header("Authorization", "Bearer " + token);

            Request request = requestBuilder.build();

            // Ejecutar la solicitud
            Response response = chain.proceed(request);

            // Si la respuesta es 401 (token expirado)
            if (response.code() == 401) {
                // Realizar logout y redirigir a LoginActivity
                logout();
                redirectToLogin();
            }

            return response;
        } else {
            // Si no hay token, redirigir al LoginActivity
            redirectToLogin();
            throw new IOException("Token no disponible.");
        }
    }

    // Obtener el token almacenado
    private String getTokenFromSharedPreferences() {
        SharedPreferences preferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        return preferences.getString("access_token", null);  // O lo que sea el nombre de la clave del token
    }

    // Verificar si el token está por expirar
    private boolean isTokenExpiringSoon(String token) {
        try {
            // Decodificar el token JWT
            String[] splitToken = token.split("\\.");
            String payload = new String(android.util.Base64.decode(splitToken[1], android.util.Base64.DEFAULT));
            JSONObject jsonObject = new JSONObject(payload);

            // Obtener el tiempo de expiración
            long expirationTime = jsonObject.getLong("exp");
            long currentTime = System.currentTimeMillis() / 1000;

            // Verificar si quedan menos de 5 minutos (300 segundos)
            return (expirationTime - currentTime) < 300;
        } catch (Exception e) {
            e.printStackTrace();
            return false;  // Por defecto, asumimos que no está por expirar si falla la decodificación
        }
    }

    // Realizar el logout eliminando el token almacenado
    private void logout() {
        SharedPreferences preferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("access_token");  // Eliminar el token
        editor.apply();
    }

    // Redirigir al LoginActivity
    private void redirectToLogin() {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }
}
