package com.example.comicground.models;

import java.io.Serializable;

public class Comic implements Serializable {

    private static final long serialVersionUID = 559674395863669429L;

    private Integer id;

    private String titulo;

    private String portada;

    public Comic() {}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }


    public String getPortada() {
        return portada;
    }

    public void setPortada(String portada) {
        this.portada = portada;
    }

}