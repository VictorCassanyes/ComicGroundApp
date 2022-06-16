package com.example.comicground.models;

import java.io.Serializable;
import java.util.Date;

public class Comentario implements Serializable {

    private Integer id;

    private Comic comic;

    private Usuario usuario;

    private String texto;

    private String fechaCreacion;

    public Comentario() {}

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

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public String getFechaCreacion() { return fechaCreacion; }

    public void setFechaCreacion(String fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}
