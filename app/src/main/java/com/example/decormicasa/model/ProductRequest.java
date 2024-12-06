package com.example.decormicasa.model;

import java.io.Serializable;
import java.util.Date;

public class ProductRequest implements Serializable {
    private Integer idProducto;
    private String nombre;
    private String descripcion;
    private Float precioCompra;
    private Float precioVenta;
    private Integer stock;
    private String imagen;
    private Date fechaRegistro;
    private String estado;
    private String caracteristicas;
    private String usos;
    private Integer idMarca;

    public Integer getIdProducto() {
        return idProducto;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public Float getPrecioCompra() {
        return precioCompra;
    }

    public Float getPrecioVenta() {
        return precioVenta;
    }

    public Integer getStock() {
        return stock;
    }

    public String getImagen() {
        return imagen;
    }

    public Date getFechaRegistro() {
        return fechaRegistro;
    }

    public String getEstado() {
        return estado;
    }

    public String getCaracteristicas() {
        return caracteristicas;
    }

    public String getUsos() {
        return usos;
    }

    public Integer getIdMarca() {
        return idMarca;
    }

    public ProductRequest(String nombre, String descripcion, Float precioCompra, Float precioVenta, Integer stock,
                          String imagen, String estado, String caracteristicas, String usos, Integer idMarca) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precioCompra = precioCompra;
        this.precioVenta = precioVenta;
        this.stock = stock;
        this.imagen = imagen;
        this.estado = estado;
        this.caracteristicas = caracteristicas;
        this.usos = usos;
        this.idMarca = idMarca;
    }

    @Override
    public String toString() {
        return nombre; // Aquí decides cómo quieres representar el producto
    }
}
