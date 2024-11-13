package com.example.decormicasa.model;

public class AuthResponse {
    private String access_token;
    private String rol;
    private int id;// Agregar campo para el rol

    // Constructor
    public AuthResponse(String access_token, String rol, int id) {
        this.access_token = access_token;
        this.rol = rol;
        this.id = id;
    }

    // Getter para el access_token
    public String getAccess_token() {
        return access_token;
    }

    // Getter para el rol
    public String getRol() {
        return rol; // Devolver el rol
    }

    // Getter para el id
    public int getId() {
        return id; // Devolver el id
    }
}
