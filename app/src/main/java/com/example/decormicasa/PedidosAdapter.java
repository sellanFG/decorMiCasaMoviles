package com.example.decormicasa;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.decormicasa.model.PedidoRequest;

import java.util.List;

public class PedidosAdapter extends RecyclerView.Adapter<PedidosAdapter.PedidoViewHolder> {

    private List<PedidoRequest> pedidos;
    private Context context;

    public PedidosAdapter(List<PedidoRequest> pedidos, Context context) {
        this.pedidos = pedidos;
        this.context = context;
    }

    @NonNull
    @Override
    public PedidoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pedido, parent, false);
        return new PedidoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PedidoViewHolder holder, int position) {
        PedidoRequest pedido = pedidos.get(position);

        holder.idPedidoTextView.setText("Pedido #" + pedido.getIdPedido());
        holder.fechaPedidoTextView.setText("Fecha: " + pedido.getFechaPedido());
        holder.totalTextView.setText(String.format("Total: S/%.2f", pedido.getTotal()));
        holder.estadoTextView.setText("Estado: " + pedido.getEstado());
    }

    @Override
    public int getItemCount() {
        return pedidos.size();
    }

    public static class PedidoViewHolder extends RecyclerView.ViewHolder {

        TextView idPedidoTextView, fechaPedidoTextView, totalTextView, estadoTextView;

        public PedidoViewHolder(@NonNull View itemView) {
            super(itemView);

            idPedidoTextView = itemView.findViewById(R.id.textIdPedido);
            fechaPedidoTextView = itemView.findViewById(R.id.textFechaPedido);
            totalTextView = itemView.findViewById(R.id.textTotal);
            estadoTextView = itemView.findViewById(R.id.textEstado);
        }
    }
}
