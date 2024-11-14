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
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.decormicasa.Interface.decorMiCasaApi;
import com.example.decormicasa.model.CategoriaRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoriaAdapter extends RecyclerView.Adapter<CategoriaAdapter.CategoriaViewHolder> {
    private List<CategoriaRequest> categorias;
    private decorMiCasaApi api;
    private Context context;  // Agregamos el contexto para mostrar mensajes Toast
    private String tokenJWT;
    private FragmentManager fragmentManager;

    public CategoriaAdapter(List<CategoriaRequest> categorias, decorMiCasaApi api, Context context, String tokenJWT, FragmentManager fragmentManager) {
        this.categorias = categorias;
        this.api = api;
        this.context = context;
        this.tokenJWT = tokenJWT;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public CategoriaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_categoria, parent, false);
        return new CategoriaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoriaViewHolder holder, int position) {
        CategoriaRequest categoria = categorias.get(position);
        holder.nombreTextView.setText(categoria.getNombre());
        holder.descripcionTextView.setText(categoria.getDescripcion());
        holder.imgTextView.setText(categoria.getImagen());
        holder.btnEditar.setOnClickListener(v -> {
            EditarCategoriaFragment editarFragment = EditarCategoriaFragment.newInstance(categoria);

            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, editarFragment)
                    .addToBackStack(null)
                    .commit();
        });
        System.out.println(categoria.getNombre());
        holder.btnEliminar.setOnClickListener(v -> {
            Integer idProducto = categoria.getIdCategoria();
            if (idProducto != null) {
                eliminarCategoria(idProducto, position);
            } else {
                Toast.makeText(context, "ID de categoria no válido", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void eliminarCategoria(Integer idProducto, int position) {
        if (idProducto == null) {
            Toast.makeText(context, "ID del categoria no válido", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear el AlertDialog de confirmación
        new android.app.AlertDialog.Builder(context)
                .setTitle("Confirmación")
                .setMessage("¿Estás seguro de que deseas eliminar esta categoria?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    // Si el usuario confirma, proceder con la eliminación
                    Call<Void> call = api.eliminarCategoria(idProducto);
                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                categorias.remove(position);
                                notifyItemRemoved(position);
                                Toast.makeText(context, "Categoria eliminado", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "Error al eliminar la Categoria", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(context, "Error en la conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                })
                .setNegativeButton("No", null) // Si el usuario cancela, no hacer nada
                .show();
    }
    @Override
    public int getItemCount() {
        return categorias.size();
    }

    public static class CategoriaViewHolder extends RecyclerView.ViewHolder {
        TextView nombreTextView, descripcionTextView, imgTextView;
        ImageButton btnEditar, btnEliminar;

        public CategoriaViewHolder(@NonNull View itemView) {
            super(itemView);
            nombreTextView = itemView.findViewById(R.id.textNombre);
            descripcionTextView = itemView.findViewById(R.id.textDescripcion);
            imgTextView= itemView.findViewById(R.id.textImagen);
            btnEditar = itemView.findViewById(R.id.btnEditar);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
        }
    }
}

