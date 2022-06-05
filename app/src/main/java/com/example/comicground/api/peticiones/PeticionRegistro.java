package com.example.comicground.api.peticiones;

public class PeticionRegistro {

    private String correo;

    private String nombreDeUsuario;

    private String nombre;

    private String apellidos;

    private String contraseña;

    private boolean habilitado;

    public PeticionRegistro(String correo, String nombreDeUsuario, String nombre, String apellidos, String contraseña, boolean habilitado) {
        this.correo = correo;
        this.nombreDeUsuario = nombreDeUsuario;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.contraseña = contraseña;
        this.habilitado = habilitado;
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
}
