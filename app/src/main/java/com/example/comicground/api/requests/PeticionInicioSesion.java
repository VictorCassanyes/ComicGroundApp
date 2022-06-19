package com.example.comicground.api.requests;

/*
 * Petición específica a mandar al iniciar sesión
 */
public class PeticionInicioSesion {

    private String nombreDeUsuario;

    private String contrasena;

    public PeticionInicioSesion(String nombreDeUsuario, String contrasena) {
        this.nombreDeUsuario = nombreDeUsuario;
        this.contrasena = contrasena;
    }

    public String getNombreDeUsuario() {
        return nombreDeUsuario;
    }

    public void setNombreDeUsuario(String nombreDeUsuario) {
        this.nombreDeUsuario = nombreDeUsuario;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }
}
