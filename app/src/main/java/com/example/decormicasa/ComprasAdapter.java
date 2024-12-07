package com.example.decormicasa;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.decormicasa.model.ComprasRequest;

import java.util.List;

public class ComprasAdapter extends RecyclerView.Adapter<ComprasAdapter.ComprasViewHolder> {

    private List<ComprasRequest> compras;
    private Context context;

    public ComprasAdapter(List<ComprasRequest> compras, Context context) {
        this.compras = compras;
        this.context = context;
    }

    @NonNull
    @Override
    public ComprasViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_compras, parent, false);
        return new ComprasViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ComprasViewHolder holder, int position) {
        ComprasRequest compra = compras.get(position);


        holder.tvProducto.setText("Producto Comprado: " + compra.getNombre());
        holder.tvProveedor.setText("Proveedor: " + compra.getProveedor());
        holder.tvPrecioCompra.setText("Precio Compra: $" + compra.getPrecioCompra());
        holder.tvDescripcion.setText("Descripción: " + compra.getDescripcionCompra());
    }


    @Override
    public int getItemCount() {
        return compras.size();
    }

    public static class ComprasViewHolder extends RecyclerView.ViewHolder {
        TextView tvProducto, tvProveedor, tvPrecioCompra, tvDescripcion, tvFecha;

        public ComprasViewHolder(View itemView) {
            super(itemView);
            tvProducto = itemView.findViewById(R.id.tvProducto);
            tvProveedor = itemView.findViewById(R.id.tvProveedor);
            tvPrecioCompra = itemView.findViewById(R.id.tvPrecioCompra);
            tvDescripcion = itemView.findViewById(R.id.tvDescripcionCompra);
        }
    }
}
