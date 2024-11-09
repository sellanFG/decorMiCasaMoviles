package com.example.decormicasa;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.decormicasa.Interface.decorMiCasaApi;
import com.example.decormicasa.model.ProductRequest;
import java.util.List;
import android.content.Context;
import android.widget.Toast;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductosAdapter extends RecyclerView.Adapter<ProductosAdapter.ProductoViewHolder> {

    private List<ProductRequest> productos;
    private decorMiCasaApi api;
    private Context context;  // Agregamos el contexto para mostrar mensajes Toast
    private String tokenJWT;

    public ProductosAdapter(List<ProductRequest> productos, decorMiCasaApi api, Context context, String tokenJWT) {
        this.productos = productos;
        this.api = api;
        this.context = context;
        this.tokenJWT = tokenJWT;
    }

    @NonNull
    @Override
    public ProductoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_producto, parent, false);
        return new ProductoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductoViewHolder holder, int position) {
        ProductRequest producto = productos.get(position);

        holder.textNombre.setText(producto.getNombre());
        holder.textPrecioCompra.setText("Precio de Compra: $" + producto.getPrecioCompra());
        holder.textPrecioVenta.setText("Precio de Venta: $" + producto.getPrecioVenta());

        holder.btnEditar.setOnClickListener(v -> {
            Log.d("ProductosAdapter", "Editar: " + producto.getNombre());
        });

        holder.btnEliminar.setOnClickListener(v -> {
            Integer idProducto = producto.getIdProducto();
            if (idProducto != null) {
                eliminarProducto(idProducto, position);
            } else {
                Toast.makeText(context, "ID de producto no válido", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void eliminarProducto(int idProducto, int position) {
        // Llamada DELETE a la API para eliminar el producto
        Call<Void> call = api.eliminarProducto(idProducto);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    productos.remove(position);
                    notifyItemRemoved(position);
                    Toast.makeText(context, "Producto eliminado", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Error al eliminar el producto", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(context, "Error en la conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


    @Override
    public int getItemCount() {
        return productos.size();
    }

    public static class ProductoViewHolder extends RecyclerView.ViewHolder {
        TextView textNombre, textPrecioCompra, textPrecioVenta;
        ImageButton btnEditar, btnEliminar;

        public ProductoViewHolder(@NonNull View itemView) {
            super(itemView);
            textNombre = itemView.findViewById(R.id.textNombre);
            textPrecioCompra = itemView.findViewById(R.id.textPrecioCompra);
            textPrecioVenta = itemView.findViewById(R.id.textPrecioVenta);
            btnEditar = itemView.findViewById(R.id.btnEditar);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
        }
    }
}



