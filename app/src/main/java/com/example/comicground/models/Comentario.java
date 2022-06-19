package com.example.comicground.models;

import java.util.Date;

public class Comentario {

    private Integer id;

    private Comic comic;

    private Usuario usuario;

    private String texto;

    private String fechaCreacion;

    public Comentario(Comic comic, Usuario usuario, String texto) {
        this.comic = comic;
        this.usuario = usuario;
        this.texto = texto;
        this.fechaCreacion = String.valueOf(new Date());
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

    public String getTexto() {
        return texto;
    }

    public String getFechaCreacion() { return fechaCreacion; }
}
