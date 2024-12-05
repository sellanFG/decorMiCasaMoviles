package com.example.decormicasa.network;

import android.content.Context;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static Retrofit retrofit;
    private static final String BASE_URL = "https://damgrupo3.pythonanywhere.com/"; // Cambia por tu base URL

    public static Retrofit getRetrofitInstance(Context context) {
        if (retrofit == null) {
            // Crear el OkHttpClient con el interceptor
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new AuthInterceptor(context))  // Agregar el interceptor
                    .build();

            // Crear Retrofit
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)  // Usar el cliente con interceptor
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
