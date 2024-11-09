package com.example.decormicasa;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.decormicasa.Interface.decorMiCasaApi;
import com.example.decormicasa.model.ProductRequest;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductosAdapter extends RecyclerView.Adapter<ProductosAdapter.ProductoViewHolder> {

    private List<ProductRequest> productos;
    private decorMiCasaApi api;
    private Context context;
    private String token;

    // Constructor modificado para aceptar api, contexto, y token
    public ProductosAdapter(List<ProductRequest> productos, decorMiCasaApi api, Context context, String token) {
        this.productos = productos;
        this.api = api;
        this.context = context;
        this.token = token;
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

        Log.d("ProductosAdapter", "ID del producto en posición " + position + ": " + producto.getIdProducto());

        holder.textNombre.setText(producto.getNombre());
        holder.textPrecioCompra.setText("Precio de Compra: $" + producto.getPrecioCompra());
        holder.textPrecioVenta.setText("Precio de Venta: $" + producto.getPrecioVenta());

        holder.btnEliminar.setOnClickListener(v -> eliminarProducto(position, producto.getIdProducto()));
    }


    @Override
    public int getItemCount() {
        return productos.size();
    }

    private void eliminarProducto(int position, Integer idProducto) {
        if (idProducto == null) {
            Toast.makeText(context, "ID del producto no válido", Toast.LENGTH_SHORT).show();
            return;
        }

        Call<Void> call = api.eliminarProducto(idProducto);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    productos.remove(position);
                    notifyItemRemoved(position);
                    Toast.makeText(context, "Producto eliminado", Toast.LENGTH_SHORT).show();
                } else {
                    // Imprime detalles de la respuesta
                    Log.d("ProductosAdapter", "Error al eliminar producto: " + response.code() + " - " + response.message());
                    Toast.makeText(context, "Error al eliminar producto", Toast.LENGTH_SHORT).show();
                }
            }


            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(context, "Error en la conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static class ProductoViewHolder extends RecyclerView.ViewHolder {
        TextView textNombre, textPrecioCompra, textPrecioVenta;
        ImageButton btnEliminar;

        public ProductoViewHolder(@NonNull View itemView) {
            super(itemView);
            textNombre = itemView.findViewById(R.id.textNombre);
            textPrecioCompra = itemView.findViewById(R.id.textPrecioCompra);
            textPrecioVenta = itemView.findViewById(R.id.textPrecioVenta);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
        }
    }
}
