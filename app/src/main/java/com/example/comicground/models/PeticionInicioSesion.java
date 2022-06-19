package com.example.comicground.models;

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
}
