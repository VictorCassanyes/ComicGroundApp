package com.example.comicground.api.seguridad;

import com.example.comicground.api.ClienteAPI;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OAuthServer {

    public static IOAuthServer oAuthServer = null;

    public static IOAuthServer getoAuthServer() {

        if (oAuthServer==null) {
            Retrofit retrofit=ClienteAPI.retrofit;
            oAuthServer= retrofit.create(IOAuthServer.class);

        }
        return oAuthServer;
    }
}
