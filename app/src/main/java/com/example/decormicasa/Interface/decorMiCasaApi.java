package com.example.decormicasa.Interface;

import com.example.decormicasa.model.AuthRequest;
import com.example.decormicasa.model.AuthResponse;
import com.example.decormicasa.model.ProductRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface decorMiCasaApi {

    @POST("auth")
    Call<AuthResponse> autenticar(@Body AuthRequest authRequest);


    @GET("api_obtenerproductos")
    Call<List<ProductRequest>> obtenerproductos(@Header("Authorization") String authorization);

    @POST("registrarProducto")
    Call<Void> registrarProducto(@Body ProductRequest nuevoProducto);

    @DELETE("eliminarProducto/{id}")
    Call<Void> eliminarProducto(@Path("id") int id);

}
