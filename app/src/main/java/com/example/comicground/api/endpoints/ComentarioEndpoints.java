package com.example.comicground.api.endpoints;

import com.example.comicground.models.Comentario;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ComentarioEndpoints {

    @GET("comicground/comentarios/{idComic}")
    Call<List<Comentario>> obtenerComentariosPorComic(@Header("Authorization") String token, @Path("idComic") Integer idComic);

    @POST("comicground/comentarios/comentar")
    Call<ResponseBody> guardarComentario(@Header("Authorization") String token, @Body Comentario comentario);
}
