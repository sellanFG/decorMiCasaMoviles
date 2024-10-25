package com.example.decormicasa.model;

import java.util.Date;

public class ProductEntry {
    private static final String TAG = ProductEntry.class.getSimpleName();
    public final String nombre;
    public final String descripcion;
    public final Float precioCompra;
    public final Float precioVenta;
    public final Integer stock;
    public final String imagen;
    public final Date fechaRegistro;
    public final boolean estado;
    public final String caracteristicas;
    public final String usos;
    public final Integer idProducto;


    public ProductEntry(Integer idProducto, String nombre, String descripcion, Float precioCompra, Float precioVenta, Integer stock, String imagen, Date fechaRegistro, boolean estado, String caracteristicas, String usos) {
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precioCompra = precioCompra;
        this.precioVenta = precioVenta;
        this.stock = stock;
        this.imagen = imagen;
        this.fechaRegistro = fechaRegistro;
        this.estado = estado;
        this.caracteristicas = caracteristicas;
        this.usos = usos;
    }

    public ProductEntry(String nombre, String descripcion, Float precioVenta, Integer stock) {
        this.idProducto = null;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precioCompra = null;
        this.precioVenta = precioVenta;
        this.stock = stock;
        this.imagen = null;
        this.fechaRegistro = null;
        this.estado = true;
        this.caracteristicas = null;
        this.usos = null;
    }

}
