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
    }



    @Override
    public int getItemCount() {
        return usuarios.size();
    }


    public static class UsuarioViewHolder extends RecyclerView.ViewHolder {
        TextView textNombre, textEmail,textTelefono;
        public UsuarioViewHolder(@NonNull View itemView) {
            super(itemView);
            textNombre = itemView.findViewById(R.id.textNombre);
            textEmail = itemView.findViewById(R.id.textEmail);
            textTelefono = itemView.findViewById(R.id.textTelefono);
        }
    }
}
