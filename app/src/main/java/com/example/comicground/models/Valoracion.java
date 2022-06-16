package com.example.comicground.models;

import java.io.Serializable;
import java.util.Date;

public class Valoracion implements Serializable {

    private Integer id;

    private Comic comic;

    private Usuario usuario;

    private float puntuacion;

    public Valoracion() {}

    public Valoracion(Comic comic, Usuario usuario, float puntuacion) {
        this.comic = comic;
        this.usuario = usuario;
        this.puntuacion = puntuacion;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Comic getComic() {
        return comic;
    }

    public void setComic(Comic comic) {
        this.comic = comic;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public float getPuntuacion() {
        return puntuacion;
    }

    public void setPuntuacion(float puntuacion) {
        this.puntuacion = puntuacion;
    }

}

