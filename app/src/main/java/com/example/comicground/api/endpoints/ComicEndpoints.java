package com.example.comicground.api.endpoints;

import com.example.comicground.models.Comic;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ComicEndpoints {

    @GET("comicground/comics")
    Call<List<Comic>> obtenerComicsRecientes(@Header("Authorization") String token);

    @POST("comicground/comics")
    Call<List<Comic>> obtenerComicsPorTitulo(@Header("Authorization") String token, @Body String titulo);

}
