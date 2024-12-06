package com.example.decormicasa.Interface;

import androidx.annotation.Nullable;

import com.example.decormicasa.model.AuthRequest;
import com.example.decormicasa.model.AuthResponse;
import com.example.decormicasa.model.CategoriaRequest;
import com.example.decormicasa.model.ImagenResponse;
import com.example.decormicasa.model.MarcaRequest;
import com.example.decormicasa.model.MarcasRequest;
import com.example.decormicasa.model.PedidoRequest;
import com.example.decormicasa.model.ProductRequest;
import com.example.decormicasa.model.User;
import com.example.decormicasa.model.UserResponse;
import com.example.decormicasa.model.UsuarioRequest;
import com.example.decormicasa.model.ProductoClienteRequest;
import com.example.decormicasa.utils.RefreshTokenRequest;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.List;
import retrofit2.http.Query;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface decorMiCasaApi {

    @POST("auth")
    Call<AuthResponse> autenticar(@Body AuthRequest authRequest);


    @GET("api_obtenerproductos")
    Call<List<ProductRequest>> obtenerproductos(@Header("Authorization") String authorization);

    @GET("/api_obtenerpedidos")
    Call<List<PedidoRequest>> obtenerPedidos(@Header("Authorization") String token);

    @GET("ruta/obtenerProducto/{id}")
    Call<ProductRequest> obtenerProductoPorId(@Path("id") int idProducto);

    @POST("registrarProducto")
    Call<Void> registrarProducto(@Header("Authorization") String authorization, @Body ProductRequest nuevoProducto);

    @POST("editarProducto/{id}")
    Call<Void> editarProducto(@Header("Authorization") String authorization,@Path("id") int id, @Body ProductRequest producto);

    @DELETE("eliminarProducto/{id}")
    Call<Void> eliminarProducto(@Header("Authorization") String authorization,@Path("id") int id);


    @GET("api_obtenercategorias")
    Call<List<CategoriaRequest>> obtenercategorias(@Header("Authorization") String authorization);
    @POST("categorias")
    Call<Void> registrarCategoria(@Header("Authorization") String authorization, @Body CategoriaRequest categoria);
    @PUT("editar_categorias/{id}")
    Call<Void> actualizarCategoria(@Header("Authorization") String authorization, @Path("id") int id, @Body CategoriaRequest categoria);

    @DELETE("eliminar_categorias/{id}")
    Call<Void> eliminarCategoria(@Header("Authorization") String authorization, @Path("id") int id);

    @GET("api_obtenermarcas")
    Call<List<MarcaRequest>> obtenermarcas(@Header("Authorization") String authorization);

    @GET("api_obtenermarcas_imagen")
    Call<List<MarcasRequest>> obtenerMarcasImg(@Header("Authorization") String authorization);
    @POST("marcas")
    Call<Void> registrarMarca(@Header("Authorization") String authorization, @Body MarcasRequest marca);
    @PUT("editar_marcas/{id}")
    Call<Void> actualizarMarca(@Header("Authorization") String authorization, @Path("id") int id, @Body MarcasRequest marca);

    @DELETE("eliminar_marcas/{id}")
    Call<Void> eliminarMarca(@Header("Authorization") String authorization, @Path("id") int id);
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

    @GET("obtener_usuario/{id}")
    Call<JsonObject> obtenerUsuario(@Header("Authorization") String token, @Path("id") int id);

    @PUT("editar_usuario/{id}")
    Call<Void> editarUsuario(@Header("Authorization") String token, @Path("id") int id, @Body JsonObject data);


    @POST("register_admin")
    Call<Void> registrarAdmin(@Header("Authorization") String authorization, @Body UsuarioRequest UsuarioRequest);

    public interface ApiService {
        @POST("register")
        Call<UserResponse> registerUser(@Body User user);

    }
    @GET("obtener_empleados")
    Call<List<UsuarioRequest>> obtener_empleados(@Header("Authorization") String authorization);

    @PUT("editar_empleado/{id}")
    Call<Void> editarEmpleado(@Header("Authorization") String token, @Path("id") int id, @Body UsuarioRequest usuario);

    @DELETE("eliminar_empleado/{id}")
    Call<Void> eliminarEmpleado(@Header("Authorization") String token, @Path("id") int id);

    // Método para cargar imágenes (subir imágenes)
    @Multipart
    @POST("/api/upload-imagen")  // Asegúrate de que esta URL coincida con la del servidor
    Call<ImagenResponse> subirImagen(@Part MultipartBody.Part file);

    // @GET("usuarios/verificar-telefono")
    // Call<Boolean> verificarTelefonoConEmail(@Query("email") String email, @Query("numeroTelefono") String numeroTelefono);

    @POST("refresh-token") // Ajusta la ruta según tu API
    Call<AuthResponse> refreshToken(@Body RefreshTokenRequest request);
}