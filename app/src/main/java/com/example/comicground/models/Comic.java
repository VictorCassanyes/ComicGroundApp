package com.example.comicground.models;

import java.io.Serializable;

public class Comic implements Serializable {

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

    public String getPortada() {
        return portada;
    }

}