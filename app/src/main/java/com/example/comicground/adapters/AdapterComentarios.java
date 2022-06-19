package com.example.comicground.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.comicground.R;
import com.example.comicground.models.Comentario;
import java.util.ArrayList;

public class AdapterComentarios extends BaseAdapter {

    private ArrayList<Comentario> comentarios;
    private Context contexto;

    public AdapterComentarios(ArrayList<Comentario> comentarios, Context contexto) {
        super();
        this.comentarios = comentarios;
        this.contexto = contexto;
    }

    @Override
    public int getCount() {
        return comentarios.size();
    }

    @Override
    public Object getItem(int i) {
        return comentarios.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater layoutInflater=LayoutInflater.from(contexto);

        @SuppressLint({"ViewHolder", "InflateParams"})
        View elemento=layoutInflater.inflate(R.layout.element_adapter_comentarios, null, false);
        //Nombre del usuario que comentó
        TextView nombreDeUsuario=elemento.findViewById(R.id.tvNombreDeUsuario);
        nombreDeUsuario.setText(comentarios.get(i).getUsuario().getNombreDeUsuario());
        //El comentario realizado
        TextView texto=elemento.findViewById(R.id.tvTexto);
        texto.setText(comentarios.get(i).getTexto());
        //Fecha en la que se publicó el comentario
        TextView fechaComentario=elemento.findViewById(R.id.tvFechaComentario);
        fechaComentario.setText(comentarios.get(i).getFechaCreacion());
        return elemento;
    }
}
