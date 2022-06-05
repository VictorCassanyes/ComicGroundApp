package com.example.comicground.api.endpoints;


import com.example.comicground.api.peticiones.PeticionRegistro;
import com.example.comicground.models.Usuario;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UsuarioEndpoints {

    @GET("usuarios/")
    Call<List<Usuario>> obtenerUsuarios();

    @POST("usuarios/guardarUsuario/")
    Call<Usuario> guardarUsuario(@Body PeticionRegistro peticionRegistro);

    @GET("usuarios/{id}/")
    Call<Usuario> obtenerUsuario(@Path("id") Integer id);

    @PUT("usuarios/guardarUsuario/{id}/")
    Call<Usuario> editarUsuario(@Body Usuario usuario, @Path("id") Integer id);
}
