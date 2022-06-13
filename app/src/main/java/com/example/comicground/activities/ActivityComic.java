package com.example.comicground.activities;

import static com.example.comicground.api.ClienteAPI.retrofit;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.comicground.R;
import com.example.comicground.adapters.AdapterComentarios;
import com.example.comicground.api.endpoints.ComentarioEndpoints;
import com.example.comicground.models.Comentario;
import com.example.comicground.models.Comic;
import com.example.comicground.models.Usuario;
import com.example.comicground.utils.Constantes;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class ActivityComic extends AppCompatActivity {

    //Vistas
    ListView listaComentarios;
    TextView tvTitulo;
    ImageView ivPortada;
    EditText etComentar;
    Button btnEnviar;

    //Variables de vistas
    String texto;

    //Para la lista de comentarios
    AdapterComentarios adapterComentarios;
    ArrayList<Comentario> comentarios=new ArrayList<>();

    //Extras del Intent
    Comic comic;
    Usuario usuario;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comic);
        //Ocultar barra superior
        getSupportActionBar().hide();
        //Obtener información extra del Intent
        obtenerExtrasIntent();
        //Asignar vistas a los objetos y algunas características
        encontrarVistasPorId();
        new ObtenerComentarios().execute();
    }

    private void encontrarVistasPorId() {
        tvTitulo=findViewById(R.id.titulo);
        ivPortada=findViewById(R.id.portada);
        etComentar=findViewById(R.id.etComentar);
        btnEnviar=findViewById(R.id.btnEnviar);
        listaComentarios=findViewById(R.id.lista_comentarios);

        //Asignarle el título al TextView
        tvTitulo.setText(comic.getTitulo());
        //Picasso es una librería para pasar más fácilmente de una URL a un ImageView
        Picasso.get().load(comic.getPortada()).into(ivPortada);

        //Asignar OnClickListener al botón
        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                texto=etComentar.getText().toString();
                if(texto.equals("")) {
                    return;
                }
                new Comentar().execute();
            }
        });
    }

    private void obtenerExtrasIntent() {
        //Obtener los datos pasados por la Activity anterior
        comic=(Comic) getIntent().getSerializableExtra("comic");
        usuario=(Usuario) getIntent().getSerializableExtra("usuario");
        token=getIntent().getStringExtra("token");
    }

    private class Comentar extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... voids) {
            int respuesta;

            Comentario comentario=new Comentario(comic, usuario, texto);

            ComentarioEndpoints comentarioEndpoints=retrofit.create(ComentarioEndpoints.class);
            try {
                Call<Comentario> call=comentarioEndpoints.guardarComentario(token, comentario);
                Response<Comentario> response=call.execute();
                if (response.isSuccessful()) {
                    respuesta=Constantes.OK;
                } else {
                    respuesta=Constantes.ERROR_GENERICO;
                }
            } catch (IOException e) {
                respuesta=Constantes.ERROR_SERVIDOR;
            }
            return respuesta;
        }

        @Override
        protected void onPostExecute(Integer respuesta) {
            super.onPostExecute(respuesta);
            switch(respuesta) {
                case Constantes.OK:
                    etComentar.setText("");
                    Toast.makeText(ActivityComic.this, "¡Comentario realizado!", Toast.LENGTH_SHORT).show();
                    new ObtenerComentarios().execute();
                    break;
                case Constantes.ERROR_SERVIDOR:
                    Toast.makeText(ActivityComic.this, "Error servidor", Toast.LENGTH_SHORT).show();
                    break;
                case Constantes.ERROR_GENERICO:
                    Toast.makeText(ActivityComic.this, "Error genérico", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            Toast.makeText(ActivityComic.this, "El proceso de comentar ha sido cancelado", Toast.LENGTH_SHORT).show();
        }
    }

    private class ObtenerComentarios extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... voids) {
            int respuesta;

            ComentarioEndpoints comentarioEndpoints=retrofit.create(ComentarioEndpoints.class);
            try {
                Call<List<Comentario>> call=comentarioEndpoints.obtenerComentariosPorComic(token, comic.getId());
                Response<List<Comentario>> response=call.execute();
                if (response.isSuccessful()) {
                    respuesta=Constantes.OK;
                    comentarios=(ArrayList<Comentario>) response.body();
                } else {
                    respuesta=Constantes.ERROR_GENERICO;
                }
            } catch (IOException e) {
                respuesta=Constantes.ERROR_SERVIDOR;
            }
            return respuesta;
        }

        @Override
        protected void onPostExecute(Integer respuesta) {
            super.onPostExecute(respuesta);
            switch (respuesta) {
                case Constantes.OK:
                    adapterComentarios=new AdapterComentarios(comentarios, getApplicationContext());
                    listaComentarios.setAdapter(adapterComentarios);
                    break;
                case Constantes.ERROR_SERVIDOR:
                    Toast.makeText(ActivityComic.this, "Error servidor", Toast.LENGTH_SHORT).show();
                    break;
                case Constantes.ERROR_GENERICO:
                    Toast.makeText(ActivityComic.this, "Error genérico", Toast.LENGTH_SHORT).show();
            }
        }
    }
}