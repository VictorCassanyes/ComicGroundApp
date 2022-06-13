package com.example.comicground.api.endpoints;

import com.example.comicground.models.Comentario;
import com.example.comicground.models.Valoracion;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ValoracionEndpoints {

    @GET("comicground/valoraciones/{idComic}")
    Call<List<Valoracion>> obtenerValoracionesPorComic(@Header("Authorization") String token, @Path("idComic") Integer idComic);

    @POST("comicground/valoraciones/valorar")
    Call<Valoracion> guardarValoracion(@Header("Authorization") String token, @Body Valoracion valoracion);
}
