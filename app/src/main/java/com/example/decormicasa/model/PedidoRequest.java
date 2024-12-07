package com.example.decormicasa.model;

public class PedidoRequest {

    private int idPedido;
    private String fechaPedido;
    private double total;
    private Double igv; // Puede ser null
    private String estado;
    private int idUsuarios;
    private String ubicacion;
    private String direccion;

    public PedidoRequest(int idPedido, String fechaPedido, double total, Double igv, String estado, int idUsuarios, String ubicacion) {
        this.idPedido = idPedido;
        this.fechaPedido = fechaPedido;
        this.total = total;
        this.igv = igv;
        this.estado = estado;
        this.idUsuarios = idUsuarios;
        this.ubicacion = ubicacion;
    }

    public PedidoRequest() {
    }

    public int getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(int idPedido) {
        this.idPedido = idPedido;
    }

    public String getFechaPedido() {
        return fechaPedido;
    }

    public void setFechaPedido(String fechaPedido) {
        this.fechaPedido = fechaPedido;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public Double getIgv() {
        return igv;
    }

    public void setIgv(Double igv) {
        this.igv = igv;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public int getIdUsuarios() {
        return idUsuarios;
    }

    public void setIdUsuarios(int idUsuarios) {
        this.idUsuarios = idUsuarios;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    @Override
    public String toString() {
        return "PedidoRequest{" +
                "idPedido=" + idPedido +
                ", fechaPedido='" + fechaPedido + '\'' +
                ", total=" + total +
                ", igv=" + igv +
                ", estado='" + estado + '\'' +
                ", idUsuarios=" + idUsuarios +
                '}';
    }
}
