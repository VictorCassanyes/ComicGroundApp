package com.example.comicground.models;

import java.io.Serializable;

public class Usuario implements Serializable {

    private Integer id;

    private String correo;

    private String nombreDeUsuario;

    private String nombre;

    private String apellidos;

    private String contrasena;

    private boolean habilitado;

    public Usuario() {}

    public Usuario(String correo, String nombreDeUsuario, String nombre, String apellidos, String contrasena) {
        this.correo = correo;
        this.nombreDeUsuario = nombreDeUsuario;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.contrasena = contrasena;
        this.habilitado = true;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCorreo() {
        return correo;
    }

    public String getNombreDeUsuario() {
        return nombreDeUsuario;
    }

    public void setNombreDeUsuario(String nombreDeUsuario) {
        this.nombreDeUsuario = nombreDeUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

}
