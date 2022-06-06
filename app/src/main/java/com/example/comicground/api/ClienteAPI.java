package com.example.comicground.api;

import com.example.comicground.api.endpoints.UsuarioEndpoints;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ClienteAPI {

    private UsuarioEndpoints usuarioEndpoints;

    public static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(Constantes.URL_BASE)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    public UsuarioEndpoints getUsuarioEndpoints() {

        if(usuarioEndpoints!=null) {

            usuarioEndpoints=retrofit.create(UsuarioEndpoints.class);
        }
        return usuarioEndpoints;
    }
}
