package com.example.decormicasa;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
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
    private FragmentManager fragmentManager;

    public ProductosAdapter(List<ProductRequest> productos, decorMiCasaApi api, Context context, String token, FragmentManager fragmentManager) {
        this.productos = productos;
        this.api = api;
        this.context = context;
        this.token = token;
        this.fragmentManager = fragmentManager;
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
        holder.textDescripcion.setText(producto.getDescripcion());

        Glide.with(context)
                .load(producto.getImagen())
                .placeholder(R.drawable.loading_image)
                .error(R.drawable.default_image)
                .into(holder.imageProducto);

        holder.btnEliminar.setOnClickListener(v -> eliminarProducto(position, producto.getIdProducto()));
        holder.btnEditar.setOnClickListener(v -> {
            EditarProductoFragment editarFragment = EditarProductoFragment.newInstance(producto);

            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, editarFragment)
                    .addToBackStack(null)
                    .commit();
        });
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

        // Crear el AlertDialog de confirmación
        new android.app.AlertDialog.Builder(context)
                .setTitle("Confirmación")
                .setMessage("¿Estás seguro de que deseas eliminar este producto?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    // Si el usuario confirma, proceder con la eliminación
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
                })
                .setNegativeButton("No", null) // Si el usuario cancela, no hacer nada
                .show();
    }


    public static class ProductoViewHolder extends RecyclerView.ViewHolder {
        TextView textNombre, textPrecioCompra, textPrecioVenta, textDescripcion;
        ImageButton btnEliminar, btnEditar;
        ImageView imageProducto;
        public ProductoViewHolder(@NonNull View itemView) {
            super(itemView);
            textNombre = itemView.findViewById(R.id.textNombre);
            textPrecioCompra = itemView.findViewById(R.id.textPrecioCompra);
            textPrecioVenta = itemView.findViewById(R.id.textPrecioVenta);
            textDescripcion = itemView.findViewById(R.id.textDescripcion);
            imageProducto = itemView.findViewById(R.id.imageProducto);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
            btnEditar = itemView.findViewById(R.id.btnEditar);
        }
    }
}
