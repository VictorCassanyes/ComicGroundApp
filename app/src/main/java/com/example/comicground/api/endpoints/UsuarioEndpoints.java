package com.example.comicground.api.endpoints;


import com.example.comicground.models.PeticionInicioSesion;
import com.example.comicground.models.Usuario;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UsuarioEndpoints {

    @GET("comicground/usuarios/{id}")
    Call<Usuario> obtenerUsuario(@Header("Authorization") String token, @Path("id") Integer id);

    @POST("comicground/usuarios/inicioSesion")
    Call<Usuario> iniciarSesion(@Header("Authorization") String token, @Body PeticionInicioSesion peticionInicioSesion);

    @POST("comicground/usuarios/registro")
    Call<ResponseBody> registrar(@Header("Authorization") String credencialesCliente, @Body Usuario usuario);

    @PUT("comicground/usuarios/actualizar")
    Call<Usuario> editarUsuario(@Header("Authorization") String token, @Body Usuario usuario);
}
