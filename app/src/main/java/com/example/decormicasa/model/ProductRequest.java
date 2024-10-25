package com.example.decormicasa.model;

import java.util.Date;

public class ProductRequest {
    private Integer idProducto;
    private String nombre;
    private String descripcion;
    private Float precioCompra;
    private Float precioVenta;
    private Integer stock;
    private String imagen;
    private Date fechaRegistro;
    private boolean estado;
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

    public boolean isEstado() {
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

}
