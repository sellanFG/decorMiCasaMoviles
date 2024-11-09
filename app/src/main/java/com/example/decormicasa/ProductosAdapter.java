package com.example.decormicasa;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.decormicasa.model.ProductRequest;
import java.util.List;

public class ProductosAdapter extends RecyclerView.Adapter<ProductosAdapter.ProductoViewHolder> {

    private List<ProductRequest> productos;

    public ProductosAdapter(List<ProductRequest> productos) {
        this.productos = productos;
    }

    @Override
    public ProductoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_producto, parent, false);
        return new ProductoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ProductoViewHolder holder, int position) {
        ProductRequest producto = productos.get(position);

        holder.nombreTextView.setText(producto.getNombre());
        holder.precioCompraTextView.setText("Precio de Compra: $" + producto.getPrecioCompra());
        holder.precioVentaTextView.setText("Precio de Venta: $" + producto.getPrecioVenta());
    }

    @Override
    public int getItemCount() {
        return productos.size();
    }

    public static class ProductoViewHolder extends RecyclerView.ViewHolder {

        TextView nombreTextView;
        TextView precioCompraTextView;
        TextView precioVentaTextView;

        public ProductoViewHolder(View itemView) {
            super(itemView);
            nombreTextView = itemView.findViewById(R.id.textNombre);
            precioCompraTextView = itemView.findViewById(R.id.textPrecioCompra);
            precioVentaTextView = itemView.findViewById(R.id.textPrecioVenta);
        }
    }
}

