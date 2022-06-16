package com.example.comicground.api;

import com.example.comicground.utils.Constantes;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ClienteAPI {
    //Llamada a la URL base de mi API
    public static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(Constantes.URL_BASE)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
}
