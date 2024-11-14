package com.example.decormicasa.model;
import java.io.Serializable;
import java.util.Date;

public class UsuarioRequest implements Serializable {
    private Integer idUsuario;
    private String nombre;
    private String email;
    private String password;
    private String direccion;
    private String telefono;

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getDireccion() {
        return direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public UsuarioRequest(String nombre, String email, String password, String direccion, String telefono) {
        this.nombre = nombre;
        this.email = email;
        this.password = password;
        this.direccion = direccion;
        this.telefono = telefono;
    }

}
