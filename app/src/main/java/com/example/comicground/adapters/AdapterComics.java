package com.example.comicground.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.comicground.R;
import com.example.comicground.models.Comic;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterComics extends BaseAdapter {

    private ArrayList<Comic> comics;
    private Context contexto;

    public AdapterComics(ArrayList<Comic> comics, Context contexto) {
        super();
        this.comics = comics;
        this.contexto = contexto;
    }

    @Override
    public int getCount() {
        return comics.size();
    }

    @Override
    public Object getItem(int i) {
        return comics.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater layoutInflater=LayoutInflater.from(contexto);
        View elemento=layoutInflater.inflate(R.layout.element_adapter_comics, null, false);
        ImageView portada;
        TextView titulo;
        if(i%2==0) {
            //Si es par
            portada=elemento.findViewById(R.id.portada);
            titulo=elemento.findViewById(R.id.titulo);
        } else {
            //Si es impar
            portada=elemento.findViewById(R.id.portada2);
            titulo=elemento.findViewById(R.id.titulo2);
        }
        //Picasso es una librería para pasar más fácilmente de una URL a un ImageView
        Picasso.get().load(comics.get(i).getPortada()).into(portada);
        titulo.setText(comics.get(i).getTitulo());
        return elemento;
    }
}
