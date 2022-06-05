package com.example.comicground.api.endpoints;

import com.example.comicground.models.Comentario;
import com.example.comicground.models.Valoracion;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ValoracionEndpoints {

    @GET("valoraciones/{idComic}/")
    Call<List<Valoracion>> obtenerValoracionesPorComic(@Path("idComic") Integer idComic);

    @POST("valoraciones/guardarValoracion/")
    Call<Valoracion> guardarValoracion(@Body Valoracion valoracion);
}
