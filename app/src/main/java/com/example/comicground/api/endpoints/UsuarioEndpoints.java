package com.example.comicground.api.endpoints;


import com.example.comicground.api.peticiones.PeticionInicioSesion;
import com.example.comicground.api.respuestas.RespuestaInicioSesion;
import com.example.comicground.models.Usuario;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UsuarioEndpoints {

    @GET("comicground/usuarios/")
    Call<List<Usuario>> obtenerUsuarios();

    @GET("comicground/usuarios/{id}/")
    Call<Usuario> obtenerUsuario(@Path("id") Integer id);

    @POST("comicground/usuarios/login/")
    @FormUrlEncoded
    Call<RespuestaInicioSesion> iniciarSesion(@Header("Authorization") String cabeceraAuth, @Body PeticionInicioSesion peticionInicioSesion);

    @POST("comicground/usuarios/registro")
    Call<ResponseBody> registrar(@Header("Authorization") String cabeceraAuth, @Body Usuario usuario);

    @PUT("comicground/usuarios/actualizar/")
    Call<Usuario> editarUsuario(@Body Usuario usuario, @Path("id") Integer id);
}
