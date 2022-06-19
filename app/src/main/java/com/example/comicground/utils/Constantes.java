package com.example.comicground.utils;

public class Constantes {

    //Información API
    public static final String URL_BASE="http://192.168.0.11:8080/";
    public static final String CREDENCIALES_APLICACION="comicground:contraseñaSecreta";
    public static final String GRANT_TYPE="password";
    public static final String TIPO_TOKEN="Bearer ";
    public static final String TIPO_AUTH="Basic ";

    //Regex correo electrónico y contraseña
    public static final String REGEX_CORREO="^[a-zA-Z0-9_!#$%&'\\*+/=?{|}~^.-]+@[a-zA-Z0-9.-]+$";
    public static final String REGEX_CONTIENE_MAYUSCULA="(.*[A-Z].*)";
    public static final String REGEX_CONTIENE_MINUSCULA="(.*[a-z].*)";
    public static final String REGEX_CONTIENE_NUMERO="(.*[0-9].*)";

    //Encriptación AES
    public static final String TIPO_ALGORITMO="AES";
    public static final String TIPO_HASH="SHA-1";

    //Datos preferencias compartidas
    public static final String PREFERENCIAS_COMPARTIDAS="datosUsuario";
    public static final String ACCESS_TOKEN="access_token";
    public static final String EXPIRES_AT="expires_at";
    public static final String NOMBRE_DE_USUARIO="nombre_de_usuario";
    public static final String CONTRASENA="contraseña";
    public static final String MANTENER_SESION_INICIADA="mantener_sesion_iniciada";

    //Datos extras Intent
    public static final String TOKEN="token";
    public static final String USUARIO="usuario";
    public static final String COMIC="comic";
    public static final String CERRAR_SESION="cerrarSesion";

    //Para algunos Strings
    public static final String VACIO="";
    public static final String ESPACIO=" ";
    public static final String SALTO_LINEA="\n";
    public static final String FORMATO_UN_DECIMAL="#0.0";

    //Códigos de respuesta
    public static final int OK=0;
    public static final int ERROR_CREDENCIALES=1;
    public static final int ERROR_SERVIDOR=2;
    public static final int ERROR_NOMBRE_DE_USUARIO_EXISTENTE=3;
    public static final int ERROR_CORREO_EXISTENTE=4;
    public static final int ERROR_GENERICO=5;
    public static final int ERROR_NO_ENCONTRADO=6;
    public static final int ERROR_NO_MODIFICADO=7;
}
