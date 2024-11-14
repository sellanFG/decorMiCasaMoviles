package com.example.decormicasa.Interface;

import androidx.annotation.Nullable;

import com.example.decormicasa.model.AuthRequest;
import com.example.decormicasa.model.AuthResponse;
import com.example.decormicasa.model.CategoriaRequest;
import com.example.decormicasa.model.MarcaRequest;
import com.example.decormicasa.model.PedidoRequest;
import com.example.decormicasa.model.ProductRequest;
import com.example.decormicasa.model.ProductoClienteRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
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

    @GET("api_obtenercategorias")
    Call<List<CategoriaRequest>> obtenercategorias(@Header("Authorization") String authorization);
    @POST("categorias")
    Call<Void> registrarCategoria(@Body CategoriaRequest categoria);
    @PUT("editar_categorias/{id}")
    Call<Void> actualizarCategoria(@Path("id") int id, @Body CategoriaRequest categoria);

    @DELETE("eliminar_categorias/{id}")
    Call<Void> eliminarCategoria(@Path("id") int id);

    @GET("api_obtenermarcas")
    Call<List<MarcaRequest>> obtenermarcas(@Header("Authorization") String authorization);

    @FormUrlEncoded
    @POST("api_obtenerproductos_cliente")
    Call<List<ProductoClienteRequest>> obtenerproductoscliente(@Header("Authorization") String authorization,
                                                               @Field("idCategoria") @Nullable Integer idCategoria,
                                                               @Field("idMarca") @Nullable Integer idMarca);

    @FormUrlEncoded
    @POST("api_guardarpedido")
    Call<PedidoRequest> guardarpedido(@Header("Authorization") String authorization,
                                            @Field("id_cliente") int idCliente,
                                            @Field("total") double total,
                                            @Field("igv") double igv,
                                            @Field("metodoPago") String metodoPago,
                                            @Field("detalleVenta") String detalleVenta
    );
}
