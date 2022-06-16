package com.example.comicground.utils;

public class Constantes {

    //Información API
    public static final String URL_BASE="http://192.168.0.11:8080/";
    public static final String CREDENCIALES_APLICACION="comicground:contraseñaSecreta";
    public static final String GRANT_TYPE="password";
    public static final String TIPO_TOKEN="Bearer ";

    //Encriptación AES
    public static final String TIPO_ALGORITMO="AES";
    public static final String TIPO_HASH="SHA-1";

    //Archivo preferencias compartidas
    public static final String PREFERENCIAS_COMPARTIDAS="datosUsuario";

    //Códigos de respuesta
    public static final int OK=0;
    public static final int ERROR_CREDENCIALES=1;
    public static final int ERROR_SERVIDOR=2;
    public static final int ERROR_NOMBRE_DE_USUARIO_EXISTENTE=3;
    public static final int ERROR_CORREO_EXISTENTE=4;
    public static final int ERROR_GENERICO=5;
    public static final int ERROR_NO_ENCONTRADO=6;
}
