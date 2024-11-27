package com.example.decormicasa;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.decormicasa.Interface.decorMiCasaApi;
import com.example.decormicasa.model.ProductRequest;
import com.example.decormicasa.model.UsuarioRequest;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UsuariosAdapter extends RecyclerView.Adapter<UsuariosAdapter.UsuarioViewHolder> {

    private List<UsuarioRequest> usuarios;
    private decorMiCasaApi api;
    private Context context;
    private String token;
    private FragmentManager fragmentManager;

    public UsuariosAdapter(List<UsuarioRequest> usuarios, decorMiCasaApi api, Context context, String token, FragmentManager fragmentManager) {
        this.usuarios = usuarios;
        this.api = api;
        this.context = context;
        this.token = token;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public UsuarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_usuario, parent, false);
        return new UsuarioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsuarioViewHolder holder, int position) {
        UsuarioRequest usuario = usuarios.get(position);

        holder.textNombre.setText(usuario.getNombre());
        holder.textEmail.setText(usuario.getEmail());
        holder.textTelefono.setText(usuario.getTelefono());
        holder.btnEliminar.setOnClickListener(v -> eliminarEmpleado(position, usuario.getIdUsuario()));
        holder.btnEditar.setOnClickListener(v -> {
            EditarEmpleadoFragment editarFragment = EditarEmpleadoFragment.newInstance(usuario);

            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, editarFragment)
                    .addToBackStack(null)
                    .commit();
        });
    }



    @Override
    public int getItemCount() {
        return usuarios.size();
    }

   private void eliminarEmpleado(int position, Integer idUsuarios) {
    if (idUsuarios == null) {
        Toast.makeText(context, "ID del Empleado no válido", Toast.LENGTH_SHORT).show();
        return;
    }

    // Crear el AlertDialog de confirmación
    new android.app.AlertDialog.Builder(context)
            .setTitle("Confirmación")
            .setMessage("¿Estás seguro de que deseas eliminar este empleado?")
            .setPositiveButton("Sí", (dialog, which) -> {
                // Si el usuario confirma, proceder con la eliminación
                Call<Void> call = api.eliminarEmpleado("JWT " + token, idUsuarios);
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            usuarios.remove(position);
                            notifyItemRemoved(position);
                            Toast.makeText(context, "Empleado eliminado", Toast.LENGTH_SHORT).show();
                        } else {
                            // Imprime detalles de la respuesta
                            try {
                                String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                                Log.d("UsuariosAdapter", "Error al eliminar empleado: " + response.code() + " - " + response.message() + " - " + errorBody + " - ID: " + idUsuarios + " - URL: " + call.request().url());
                            } catch (IOException e) {
                                Log.e("UsuariosAdapter", "Error al leer el cuerpo de la respuesta", e);
                            }
                            Toast.makeText(context, "Error al eliminar empleado", Toast.LENGTH_SHORT).show();
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

    public static class UsuarioViewHolder extends RecyclerView.ViewHolder {
        TextView textNombre, textEmail,textTelefono;
        ImageButton btnEliminar, btnEditar;
        public UsuarioViewHolder(@NonNull View itemView) {
            super(itemView);
            textNombre = itemView.findViewById(R.id.textNombre);
            textEmail = itemView.findViewById(R.id.textEmail);
            textTelefono = itemView.findViewById(R.id.textTelefono);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
            btnEditar = itemView.findViewById(R.id.btnEditar);
        }
    }
}
