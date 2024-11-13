package com.example.decormicasa.model;

public class User {
    private String nombre;  // Cambié 'name' por 'nombre' para que coincida con tu base de datos y flujo
    private String email;
    private String password;
    private String direccion;  // Cambié 'address' por 'direccion' para mayor consistencia
    private String telefono;   // Cambié 'phone' por 'telefono' para mayor consistencia

    // Constructor
    public User(String nombre, String email, String password, String direccion, String telefono) {
        this.nombre = nombre;
        this.email = email;
        this.password = password;
        this.direccion = direccion;
        this.telefono = telefono;
    }

    // Getters y setters
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
}
