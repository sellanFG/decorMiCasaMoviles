package com.example.decormicasa.model;

public class ComprasRequest {
    private String idProducto; // Opcional para un producto nuevo
    private String nombre; // Solo para un producto nuevo
    private String descripcion; // Solo para un producto nuevo
    private String caracteristicas; // Opcional, solo para un producto nuevo
    private String usos; // Opcional, solo para un producto nuevo
    private int idMarca; // Solo para un producto nuevo
    private int cantidad; // Obligatorio
    private double precioCompra; // Obligatorio
    private String proveedor; // Obligatorio
    private String descripcionCompra; // Opcional

    // Constructor principal
    public ComprasRequest(String idProducto, String nombre, String descripcion, String caracteristicas,
                          String usos, int idMarca, int cantidad, double precioCompra, String proveedor, String descripcionCompra) {
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.caracteristicas = caracteristicas;
        this.usos = usos;
        this.idMarca = idMarca;
        this.cantidad = cantidad;
        this.precioCompra = precioCompra;
        this.proveedor = proveedor;
        this.descripcionCompra = descripcionCompra;
    }

    // Constructor para productos existentes
    public ComprasRequest(String idProducto, int cantidad, double precioCompra, String proveedor, String descripcionCompra) {
        this(idProducto, null, null, null, null, 0, cantidad, precioCompra, proveedor, descripcionCompra);
    }

    // Constructor para productos nuevos
    public ComprasRequest(String nombre, String descripcion, String caracteristicas, String usos,
                          int idMarca, int cantidad, double precioCompra, String proveedor, String descripcionCompra) {
        this(null, nombre, descripcion, caracteristicas, usos, idMarca, cantidad, precioCompra, proveedor, descripcionCompra);
    }

    // Getters y setters
    public String getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(String idProducto) {
        this.idProducto = idProducto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }


    public String getCaracteristicas() {
        return caracteristicas;
    }

    public void setCaracteristicas(String caracteristicas) {
        this.caracteristicas = caracteristicas;
    }

    public String getUsos() {
        return usos;
    }

    public void setUsos(String usos) {
        this.usos = usos;
    }

    public int getIdMarca() {
        return idMarca;
    }

    public void setIdMarca(int idMarca) {
        this.idMarca = idMarca;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getPrecioCompra() {
        return precioCompra;
    }

    public void setPrecioCompra(double precioCompra) {
        this.precioCompra = precioCompra;
    }

    public String getProveedor() {
        return proveedor;
    }

    public void setProveedor(String proveedor) {
        this.proveedor = proveedor;
    }

    public String getDescripcionCompra() {
        return descripcionCompra;
    }

    public void setDescripcionCompra(String descripcionCompra) {
        this.descripcionCompra = descripcionCompra;
    }
}
