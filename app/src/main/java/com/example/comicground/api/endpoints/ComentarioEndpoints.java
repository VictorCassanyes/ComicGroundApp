package com.example.comicground.api.endpoints;

import com.example.comicground.models.Comentario;
import com.example.comicground.models.Comic;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ComentarioEndpoints {

    @GET("comentarios/{idComic}/")
    Call<List<Comentario>> obtenerComentariosPorComic(@Path("idComic") Integer idComic);

    @POST("comentarios/guardarComentario/")
    Call<Comentario> guardarComentario(@Body Comentario comentario);
}
