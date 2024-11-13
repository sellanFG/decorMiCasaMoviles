package com.example.decormicasa.model;


import org.json.JSONObject;

public class ProductoClienteRequest {
    private int idProducto;
    private String nombre;
    private float precioVenta;
    private int stock, cantidad;

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public float getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(float precioVenta) {
        this.precioVenta = precioVenta;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }


    public JSONObject getJSONObjectProducto(){
        JSONObject json = new JSONObject();
        try{
            json.put("idProducto", this.getIdProducto());
            json.put("cantidad", this.getCantidad());
            json.put("precioUnitario", this.getPrecioVenta());
        }catch (Exception e){
            e.printStackTrace();
        }
        return  json;
    }
}
