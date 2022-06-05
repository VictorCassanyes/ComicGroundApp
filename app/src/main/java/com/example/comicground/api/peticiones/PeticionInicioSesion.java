package com.example.comicground.api.peticiones;

public class PeticionInicioSesion {

    private String nombreDeUsuario;

    private String contraseña;

    public PeticionInicioSesion(String nombreDeUsuario, String contraseña) {
        this.nombreDeUsuario = nombreDeUsuario;
        this.contraseña = contraseña;
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
}
