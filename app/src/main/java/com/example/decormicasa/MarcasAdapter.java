package com.example.decormicasa;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.decormicasa.Interface.decorMiCasaApi;
import com.example.decormicasa.model.MarcasRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MarcasAdapter extends RecyclerView.Adapter<MarcasAdapter.MarcaViewHolder> {

    private List<MarcasRequest> marca;
    private decorMiCasaApi api;
    private Context context;
    private String token;
    private FragmentManager fragmentManager;

    public MarcasAdapter(List<MarcasRequest> marcas, decorMiCasaApi api, Context context, String token, FragmentManager fragmentManager) {
        this.marca = marcas;
        this.api = api;
        this.context = context;
        this.token = token;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public MarcaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_marca, parent, false);
        return new MarcaViewHolder(view);


    }

    @Override
    public void onBindViewHolder(@NonNull MarcaViewHolder holder, int position) {
        MarcasRequest marca = this.marca.get(position);

        holder.textNombre.setText(marca.getNombre());
        holder.textDescripcion.setText(marca.getDescripcion());
        String imageUrl = marca.getImagen();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.loading_image)
                    .error(R.drawable.default_image)
                    .into(holder.imageMarca);
        } else {
            // Si la URL es nula, cargar una imagen por defecto
            holder.imageMarca.setImageResource(R.drawable.default_image);
        }


        holder.btnEliminar.setOnClickListener(v -> eliminarMarca(position, marca.getIdMarca()));
        holder.btnEditar.setOnClickListener(v -> {
            EditarMarcaFragment editarFragment = EditarMarcaFragment.newInstance(marca);

            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, editarFragment)
                    .addToBackStack(null)
                    .commit();
        });
    }



    @Override
    public int getItemCount() {
        return marca.size();
    }

    private void eliminarMarca(int position, Integer idMarca) {
        if (idMarca == null) {
            Toast.makeText(context, "ID del marca no válido", Toast.LENGTH_SHORT).show();
            return;
        }
        // Obtén el token desde SharedPreferences
        SharedPreferences sharedPreferences = context.getSharedPreferences("decorMiCasa", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("tokenJWT", "");

        if (token == null || token.isEmpty()) {
            Toast.makeText(context, "Token no disponible. Inicie sesión nuevamente.", Toast.LENGTH_LONG).show();
            return;
        }

        // Crear el AlertDialog de confirmación
        new android.app.AlertDialog.Builder(context)
                .setTitle("Confirmación")
                .setMessage("¿Estás seguro de que deseas eliminar esta marca?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    // Si el usuario confirma, proceder con la eliminación
                    Call<Void> call = api.eliminarMarca("JWT " + token, idMarca);
                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                marca.remove(position);
                                notifyItemRemoved(position);
                                Toast.makeText(context, "Marca eliminada", Toast.LENGTH_SHORT).show();
                            } else {
                                // Imprime detalles de la respuesta
                                Log.d("MarcaAdapter", "Error al eliminar Marca: " + response.code() + " - " + response.message());
                                Toast.makeText(context, "Error al eliminar Marca", Toast.LENGTH_SHORT).show();
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


    public static class MarcaViewHolder extends RecyclerView.ViewHolder {
        TextView textNombre, textPrecioCompra, textPrecioVenta, textDescripcion, imgTextView;
        ImageButton btnEliminar, btnEditar;
        ImageView imageMarca;
        public MarcaViewHolder(@NonNull View itemView) {
            super(itemView);
            textNombre = itemView.findViewById(R.id.textNombre);

            textDescripcion = itemView.findViewById(R.id.textDescripcion);
            imgTextView= itemView.findViewById(R.id.textImagen);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
            btnEditar = itemView.findViewById(R.id.btnEditar);
            imageMarca = itemView.findViewById(R.id.imageMarca);
        }
    }
}
