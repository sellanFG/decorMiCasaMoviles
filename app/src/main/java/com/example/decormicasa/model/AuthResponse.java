package com.example.decormicasa.model;

public class AuthResponse {
    private String access_token;
    private String rol;
    private int id;// Agregar campo para el rol
    private String telefono;

    public AuthResponse(String access_token, String rol, int id, String telefono) {
        this.access_token = access_token;
        this.rol = rol;
        this.id = id;
        this.telefono = telefono;
    }

    public String getAccess_token() {
        return access_token;
    }

    public String getRol() {
        return rol;
    }

    public int getId() {
        return id;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
}
