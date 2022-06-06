package com.example.comicground.api.peticiones;

import java.util.Date;

public class PeticionRegistro {

    private String nombre;

    private String apellidos;

    private String correo;

    private String nombreDeUsuario;

    private String contraseña;

    private boolean habilitado;

    private Date fechaCreacion;

    public PeticionRegistro(String nombre, String apellidos, String correo, String nombreDeUsuario, String contraseña, boolean habilitado, Date fechaCreacion) {
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.correo = correo;
        this.nombreDeUsuario = nombreDeUsuario;
        this.contraseña = contraseña;
        this.habilitado = habilitado;
        this.fechaCreacion = fechaCreacion;
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

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getNombreDeUsuario() {
        return nombreDeUsuario;
    }

    public void setNombreDeUsuario(String nombreDeUsuario) {
        this.nombreDeUsuario = nombreDeUsuario;
    }

    public String getContraseña() {
        return contraseña;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }

    public boolean isHabilitado() {
        return habilitado;
    }

    public void setHabilitado(boolean habilitado) {
        this.habilitado = habilitado;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

}
