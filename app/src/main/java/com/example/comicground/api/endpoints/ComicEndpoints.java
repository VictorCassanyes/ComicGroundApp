package com.example.comicground.api.endpoints;

import com.example.comicground.models.Comic;
import com.example.comicground.models.Usuario;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ComicEndpoints {

    @GET("comics/")
    Call<List<Comic>> obtenerTodosLosComics();

    @GET("comics/{id}/")
    Call<List<Comic>> obtenerComicsPorId(@Path("id") Integer id);

}
