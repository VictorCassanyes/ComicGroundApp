package com.example.comicground.models;

import java.io.Serializable;
import java.util.Date;

public class Valoracion implements Serializable {

    private static final long serialVersionUID = 1700429166736660996L;

    private Integer id;

    private Comic comic;

    private Usuario usuario;

    private int puntuacion;

    private Date fechaCreacion;

    public Valoracion() {}

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

    public int getPuntuacion() {
        return puntuacion;
    }

    public void setPuntuacion(int puntuacion) {
        this.puntuacion = puntuacion;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

}

