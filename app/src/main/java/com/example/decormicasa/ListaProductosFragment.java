package com.example.decormicasa;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.decormicasa.Interface.decorMiCasaApi;
import com.example.decormicasa.databinding.FragmentListaProductosBinding;
import com.example.decormicasa.model.ProductEntry;
import com.example.decormicasa.model.ProductRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ListaProductosFragment extends Fragment {
    private FragmentListaProductosBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Leyendo data desde el Shared Preferences
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("decorMiCasa", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("tokenJWT", "");

        // Inflate the layout for this fragment
        obtenerProductos(token);

        //return inflater.inflate(R.layout.fragment_lista_productos, container, false);
        binding = FragmentListaProductosBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    public void obtenerProductos(String tokenJWT){
        List<ProductEntry> listaMostrar = new ArrayList<>();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.dominioservidor))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        decorMiCasaApi decorMiCasaApi = retrofit.create(decorMiCasaApi.class);
        Call<List<ProductRequest>> call = decorMiCasaApi.obtenerproductos("JWT " + tokenJWT);
        call.enqueue(new Callback<List<ProductRequest>>() {
            @Override
            public void onResponse(Call<List<ProductRequest>> call, Response<List<ProductRequest>> response) {
                if(response.code()!=200){
                    // Ocurrió el problema
                }else{
                    List<ProductRequest> listaProductos = response.body();
                    for(ProductRequest elemento:listaProductos){
                        listaMostrar.add(new ProductEntry(elemento.getNombre(), elemento.getPrecioVenta()));
                    }
                    // RecyclerView recyclerView = getView().findViewById(R.id.recycler_view);
                    RecyclerView recyclerView = binding.recyclerView;
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false));
                    ProductCardRecyclerViewAdapter adapter = null;

                    Gson gson = new Gson();
                    Type productListType = new TypeToken<ArrayList<ProductEntry>>() {
                    }.getType();

                    adapter = new ProductCardRecyclerViewAdapter(listaMostrar);
                    recyclerView.setAdapter(adapter);
                    recyclerView.addItemDecoration(new ListaProductosItemDecoration(16,16));
                }
            }

            @Override
            public void onFailure(Call<List<ProductRequest>> call, Throwable t) {

            }
        });
    }
}
