package com.example.comicground.api.seguridad;

import static com.example.comicground.api.ClienteAPI.retrofit;

import com.example.comicground.api.ClienteAPI;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OAuthServer {

    public static IOAuthServer oAuthServer = null;

    public static IOAuthServer getoAuthServer() {

        if (oAuthServer==null) {
            oAuthServer= retrofit.create(IOAuthServer.class);

        }
        return oAuthServer;
    }
}
