package com.example.decormicasa;

import static android.content.Context.MODE_PRIVATE;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.decormicasa.model.ProductoClienteRequest;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProductoClienteAdapter extends RecyclerView.Adapter<ProductoClienteAdapter.ViewHolder>{
    private Context context;
    private List<ProductoClienteRequest> listaProducto;
    public List<ProductoClienteRequest> carrito = new ArrayList<>();

    public ProductoClienteAdapter(Context context, List<ProductoClienteRequest> listaProducto) {
        this.context = context;
        this.listaProducto = listaProducto;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Enlazar el adaptador con el archivo que contiene el cardview
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_productos, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //Leer la cantidad asignada al producto en caso estuviera agregado en el carrito
        int cantidadCarrito = leerCantidadProductoCarrito(listaProducto.get(position));
        listaProducto.get(position).setCantidad(cantidadCarrito);

        //Mostrar los datos del producto en el cardview
        holder.mostrarDatosProducto(listaProducto.get(position));
    }

    @Override
    public int getItemCount() {
        //Define la cantidad de productos que hay en el catalogo
        return listaProducto.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        //Controles implementados en el cardview: cardview_producto.xml
        TextView txtNombre, txtID, txtPrecio;
        MaterialButton txtCantidad;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //Enlazar los controles del cardview con las variables declaradas en java
            txtNombre = itemView.findViewById(R.id.txtNombre);
            txtID = itemView.findViewById(R.id.txtID);
            txtPrecio = itemView.findViewById(R.id.txtPrecio);
            txtCantidad = itemView.findViewById(R.id.txtCantidad);

            //Implementar el reconocimiento del evento click en el cardview
            itemView.setOnClickListener(this);
        }

        private void mostrarDatosProducto(ProductoClienteRequest producto){
            String nombre = producto.getNombre();
            String stock = String.valueOf(producto.getStock());
            String id = String.valueOf(producto.getIdProducto());
            String precio = String.format("%.2f", producto.getPrecioVenta());

            txtNombre.setText(nombre+" - stock: "+stock);
            txtID.setText("id: "+id);
            txtPrecio.setText("S/ "+precio);

            if (producto.getCantidad()>0) {
                txtCantidad.setText(String.valueOf(producto.getCantidad()));
                txtCantidad.setVisibility(View.VISIBLE);
                //txtNombre.setTextColor(ContextCompat.getColor(context, R.color.RoyalBlue));
            }else{
                txtCantidad.setText("");
                txtCantidad.setVisibility(View.GONE);
                //txtNombre.setTextColor(ContextCompat.getColor(context, R.color.black));
            }
        }

        @Override
        public void onClick(View v) {
            Dialog dialog = new Dialog(context, androidx.appcompat.R.style.Base_Theme_AppCompat_Dialog_Alert);
            dialog.setContentView(R.layout.dialog_producto_cantidad);
            dialog.setCancelable(true);

            //Enlazar los controles del dialog
            MaterialButton btnQuitarProducto, btnCerrar, btnAgregarCarrito,btnFavorito;
            ImageView imgProducto;
            TextView txtProductoNombre;
            TextInputEditText txtProductoCantidad;

            btnQuitarProducto = dialog.findViewById(R.id.btnQuitarProducto);
            btnCerrar = dialog.findViewById(R.id.btnCerrar);
            btnAgregarCarrito = dialog.findViewById(R.id.btnAgregarCarrito);
            txtProductoNombre = dialog.findViewById(R.id.txtProductoNombre);
            txtProductoCantidad = dialog.findViewById(R.id.txtProductoCantidad);
            btnFavorito = dialog.findViewById(R.id.btnFavorito);

            //Mostrar los datos del producto en el dialog
            ProductoClienteRequest producto = listaProducto.get(getAdapterPosition());

            txtProductoNombre.setText(producto.getNombre());

            //Mostrar el botón Quitar producto solo cuando la cantidad de venta sea mayor a cero
            if (producto.getCantidad()>0){
                btnQuitarProducto.setVisibility(View.VISIBLE); //Mostrar
            }else{
                btnQuitarProducto.setVisibility(View.GONE); //Ocultar
            }

            //Enfocar la caja de texto para ingresar la cantidad
            txtProductoCantidad.requestFocus();

            //Mostrar el teclado del móvil para ingresar la cantidad
            //Helper.mostarTeclado((Activity) context);


            //Implementar el botón cerrar
            btnCerrar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            //Implementar el botón Agregar Carrito
            btnAgregarCarrito.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(context, "Agregar a carrito", Toast.LENGTH_SHORT).show();
                    if (txtProductoCantidad.getText().toString().isEmpty()){
                        Toast.makeText(context, "Ingrese una cantidad", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    //Obtener la cantidad ingresada
                    int cantidad = Integer.parseInt(txtProductoCantidad.getText().toString());

                    //Validar que la cantidad no sea menor o igual a cero
                    if (cantidad <= 0){
                        Toast.makeText(context, "Ingrese una cantidad mayor a 0", Toast.LENGTH_LONG).show();
                        return;
                    }

                    //Validar si se dispone de stock
                    if (cantidad > listaProducto.get(getAdapterPosition()).getStock()){
                        Toast.makeText(context, "Stock insuficiente", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    //Asignar la cantidad al producto
                    listaProducto.get(getAdapterPosition()).setCantidad(cantidad);

                    //Agregar el producto con su cantidad al carrito
                    agregarProductoCarrito(listaProducto.get(getAdapterPosition()));

                    //Refrescar los datos en el recyclerView
                    notifyDataSetChanged();

                    //Cerrar el dialog
                    dialog.dismiss();
                }
            });

            //Implementar el botón Quitar producto
            btnQuitarProducto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(context, "Quitar producto", Toast.LENGTH_SHORT).show();
                    quitarProductoCarrito(listaProducto.get(getAdapterPosition()));

                    //Refrescar el recyclerView
                    notifyDataSetChanged();

                    //Cerrar el dialog
                    dialog.dismiss();
                }
            });


            //verificar si está en favoritos
            SharedPreferences prefs = context.getSharedPreferences("FavoritosPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            Set<String> favoritos = prefs.getStringSet("favoritos", new HashSet<>());
            String id =  String.valueOf(listaProducto.get(getAdapterPosition()).getIdProducto());
            String nombre = listaProducto.get(getAdapterPosition()).getNombre();
            String stock = String.valueOf(listaProducto.get(getAdapterPosition()).getStock());
            String precio = String.valueOf(listaProducto.get(getAdapterPosition()).getPrecioVenta());
            String productoFormato = id + ";" + nombre + ";" + stock + ";" + precio;
            boolean fav = favoritos.contains(productoFormato);
            if (fav) {
                btnFavorito.setIcon(ContextCompat.getDrawable(context, R.drawable.ic_favorito));
                btnFavorito.setText("Quitar de favoritos");
            } else {
                btnFavorito.setIcon(ContextCompat.getDrawable(context, R.drawable.ic_no_favorito));
                btnFavorito.setText("Agregar a favoritos");
            }

            btnFavorito.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (fav) {
                        // Si ya existe, eliminarlo
                        favoritos.remove(productoFormato);
                        btnFavorito.setIcon(ContextCompat.getDrawable(context, R.drawable.ic_no_favorito));
                        btnFavorito.setText("Agregar a favoritos");
                    } else {
                        // Si no existe, agregarlo
                        favoritos.add(productoFormato);
                        btnFavorito.setIcon(ContextCompat.getDrawable(context, R.drawable.ic_favorito));
                        btnFavorito.setText("Quitar de favoritos");
                    }

                    // Guardar los cambios en SharedPreferences
                    editor.putStringSet("favoritos", favoritos);
                    editor.apply();

                    // Mostrar mensaje para saber si fue agregado o eliminado
                    String mensaje = favoritos.contains(productoFormato) ? "Agregado a favoritos" : "Eliminado de favoritos";
                    Toast.makeText(v.getContext(), mensaje, Toast.LENGTH_SHORT).show();
                }
            });

            //Mostrar el dialog
            dialog.show();
        }
    }


    private void agregarProductoCarrito(ProductoClienteRequest producto){
        //Validar si el producto que se intenta agregar ya esta cargado al carrito de venta
        for (int i = 0; i < carrito.size(); i++) {
            if (carrito.get(i).getIdProducto() == producto.getIdProducto()){
                //Si el producto que se intenta agregar ya esta cargado al carrito, simplemente se modifica la cantidad
                carrito.get(i).setCantidad(producto.getCantidad());
                return;
            }
        }

        //En caso el producto no este agregado en el carrito, entonces lo agregamos
        carrito.add(producto);

        //contarProductosCarrito();
    }

    private void quitarProductoCarrito(ProductoClienteRequest producto){
        for (int i = 0; i < carrito.size(); i++) {
            if (carrito.get(i).getIdProducto() == producto.getIdProducto()){
                carrito.remove(i);
            }
        }

        //contarProductosCarrito();
    }

    private int leerCantidadProductoCarrito(ProductoClienteRequest producto){
        //Revisar el producto se encuentra cargado en el carrito de venta
        for (ProductoClienteRequest p: carrito){
            if (p.getIdProducto() == producto.getIdProducto()){
                //Si el producto se encuentra cargado en el carrito, entonces retornamos la cantidad asignada
                return p.getCantidad();
            }
        }
        return 0; //Si el producto no esta cargado en el carrrito, entonces retorna cero(0)
    }


//    private void contarProductosCarrito(){
//        if (carrito.size()>0){
//            //Mostrar la cantidad de productos que tenemos cargado en el carrito
//            activity_cliente = (ClienteActivity) context;
//            activity_cliente..setText("Carrito (" + carrito.size() + ")");
//        }else{
//            ProductosFragment.btnEfab.setText("Carrito");
//        }
//    }

    public double[] calcularTotales(){
        //Calcular sub_total, monto_igv y total_neto
        double subTotal=0, montoIgv=0, totalNeto=0;

        //Calcular el total neto en función a los productos que tiene cargado en el carrito
        for (ProductoClienteRequest producto: carrito){
            totalNeto = totalNeto + (producto.getPrecioVenta() * producto.getCantidad());
        }

        //Calcular el sub total
        double porcentajeIgv = 18; //Pendiente de asiganr el porcentaje de igv de la BD
        subTotal = totalNeto / (1 + (porcentajeIgv/100));

        //Calcular el monto del igv
        montoIgv = totalNeto - subTotal;

        return new double[] {subTotal, montoIgv, totalNeto};

    }



}
