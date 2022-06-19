package com.example.comicground.api;

import com.example.comicground.api.endpoints.ComentarioEndpoints;
import com.example.comicground.api.endpoints.ComicEndpoints;
import com.example.comicground.api.endpoints.OAuthEndpoints;
import com.example.comicground.api.endpoints.UsuarioEndpoints;
import com.example.comicground.api.endpoints.ValoracionEndpoints;
import com.example.comicground.utils.Constantes;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ClienteAPI {

    //Crear Retrofit básico para usar de base en las llamadas a la API
    public static final Retrofit retrofit=new Retrofit.Builder()
            .baseUrl(Constantes.URL_BASE)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    //Llamadas a endpoints de usuarios
    public static final UsuarioEndpoints usuarioEndpoints=retrofit.create(UsuarioEndpoints.class);

    //Llamadas a endpoints de OAuth2
    public static final OAuthEndpoints oAuthEndpoints=retrofit.create(OAuthEndpoints.class);

    //Llamadas a endpoints de cómics
    public static final ComicEndpoints comicEndpoints=retrofit.create(ComicEndpoints.class);

    //Llamadas a endpoints de comentarios
    public static final ComentarioEndpoints comentarioEndpoints=retrofit.create(ComentarioEndpoints.class);

    //Llamadas a endpoints de valoraciones
    public static final ValoracionEndpoints valoracionEndpoints=retrofit.create(ValoracionEndpoints.class);
}
