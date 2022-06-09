package com.example.comicground.api.respuestas;

import com.example.comicground.models.Usuario;
import com.google.gson.annotations.SerializedName;

public class RespuestaInicioSesion {

    @SerializedName("status_code")
    int codigo;

    Usuario usuario;

    public RespuestaInicioSesion() {}

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }
    
    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
