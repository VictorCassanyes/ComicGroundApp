package com.example.comicground.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ClienteAPI {

    public static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://192.168.0.11:8080/comicground/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
}
