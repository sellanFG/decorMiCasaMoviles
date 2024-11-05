package com.example.decormicasa.model;

public class AuthResponse {
    private String access_token;
    private String rol; // Agregar campo para el rol

    // Constructor
    public AuthResponse(String access_token, String rol) {
        this.access_token = access_token;
        this.rol = rol;
    }

    // Getter para el access_token
    public String getAccess_token() {
        return access_token;
    }

    // Getter para el rol
    public String getRol() {
        return rol; // Devolver el rol
    }
}
