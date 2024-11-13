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
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface decorMiCasaApi {

    @POST("auth")
    Call<AuthResponse> autenticar(@Body AuthRequest authRequest);


    @GET("api_obtenerproductos")
    Call<List<ProductRequest>> obtenerproductos(@Header("Authorization") String authorization);

    @GET("ruta/obtenerProducto/{id}")
    Call<ProductRequest> obtenerProductoPorId(@Path("id") int idProducto);

    @POST("registrarProducto")
    Call<Void> registrarProducto(@Body ProductRequest nuevoProducto);

    @POST("editarProducto/{id}")
    Call<Void> editarProducto(@Path("id") int id, @Body ProductRequest producto);

    @DELETE("eliminarProducto/{id}")
    Call<Void> eliminarProducto(@Path("id") int id);

}
