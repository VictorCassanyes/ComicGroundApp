package com.example.comicground.models;

import java.io.Serializable;
import java.util.Date;

public class Comentario implements Serializable {

    private static final long serialVersionUID = 6461102736445356984L;

    private Integer id;

    private Comic comic;

    private Usuario usuario;

    private String texto;

    private Date fechaCreacion;

    public Comentario() {}

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

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

}
