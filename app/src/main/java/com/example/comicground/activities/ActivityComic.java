package com.example.comicground.activities;

import static com.example.comicground.api.ClienteAPI.retrofit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.comicground.R;
import com.example.comicground.adapters.AdapterComentarios;
import com.example.comicground.api.endpoints.ComentarioEndpoints;
import com.example.comicground.api.endpoints.ValoracionEndpoints;
import com.example.comicground.models.Comentario;
import com.example.comicground.models.Comic;
import com.example.comicground.models.Usuario;
import com.example.comicground.models.Valoracion;
import com.example.comicground.utils.Constantes;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class ActivityComic extends AppCompatActivity implements View.OnClickListener {

    //Vistas
    ListView listaComentarios;
    TextView tvTitulo;
    TextView tvMedia;
    ImageView ivPortada;
    EditText etComentar;
    Button btnEnviar;
    Button btnAtras;
    RatingBar valoracionMediaUsuarios;
    RatingBar valoracionUsuario;

    //Variables de vistas
    String texto;

    //Para la lista de comentarios
    AdapterComentarios adapterComentarios;
    ArrayList<Comentario> comentarios=new ArrayList<>();
    View pieDeLista;

    //Para la media de valoraciones
    ArrayList<Valoracion> valoraciones=new ArrayList<>();
    float mediaValoraciones=0;

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
        new ObtenerValoraciones().execute();
        new ObtenerComentarios().execute();
    }



    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.btnEnviar:
                texto=etComentar.getText().toString();
                if(texto.equals("")) {
                    return;
                }
                new Comentar().execute();
                break;
            case R.id.btnAtras:
                finish();
        }
    }

    private void encontrarVistasPorId() {
        tvTitulo=findViewById(R.id.titulo);
        tvMedia=findViewById(R.id.media);
        ivPortada=findViewById(R.id.portada);
        listaComentarios=findViewById(R.id.lista_comentarios);
        valoracionMediaUsuarios=findViewById(R.id.valoracionMediaUsuarios);

        //Asignar un Layout al footer de ListView y este al propio ListView
        pieDeLista=((LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.footer_lista_comentarios, null, false);
        listaComentarios.addFooterView(pieDeLista);
        //Para que aparezca la lista desde el principio
        adapterComentarios=new AdapterComentarios(comentarios, getApplicationContext());
        listaComentarios.setAdapter(adapterComentarios);

        //Asignar vistas del pie de lista de comentarios
        valoracionUsuario=pieDeLista.findViewById(R.id.valoracionUsuario);
        btnEnviar=pieDeLista.findViewById(R.id.btnEnviar);
        etComentar=pieDeLista.findViewById(R.id.etComentar);
        btnAtras=pieDeLista.findViewById(R.id.btnAtras);

        //Asignarle el título al TextView
        tvTitulo.setText(comic.getTitulo());
        //Picasso es una librería para pasar más fácilmente de una URL a un ImageView
        Picasso.get().load(comic.getPortada()).into(ivPortada);

        //Asignar OnClickListener a los botones
        btnEnviar.setOnClickListener(this);
        btnAtras.setOnClickListener(this);

        //Asignar OnRatingBarChangeListener al RatingBar
        valoracionUsuario.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                new Valorar().execute();
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
                Call<ResponseBody> call=comentarioEndpoints.guardarComentario(token, comentario);
                Response<ResponseBody> response=call.execute();
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
                    //Ordenar de más nuevo a más viejo (improvisación dado que no he podido usar Date.util para las fechas)
                    Collections.reverse(comentarios);
                } else if(response.code()== HttpURLConnection.HTTP_NOT_FOUND) {
                    respuesta=Constantes.ERROR_NO_ENCONTRADO;
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
                case Constantes.ERROR_NO_ENCONTRADO:
                    Toast.makeText(ActivityComic.this, "Todavía no hay ningún comentario, ¡Sé el primero!", Toast.LENGTH_SHORT).show();
                    break;
                case Constantes.ERROR_SERVIDOR:
                    Toast.makeText(ActivityComic.this, "Error servidor", Toast.LENGTH_SHORT).show();
                    break;
                case Constantes.ERROR_GENERICO:
                    Toast.makeText(ActivityComic.this, "Error genérico", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class ObtenerValoraciones extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... voids) {
            int respuesta;

            ValoracionEndpoints valoracionEndpoints=retrofit.create(ValoracionEndpoints.class);
            try {
                Call<List<Valoracion>> call=valoracionEndpoints.obtenerValoracionesPorComic(token, comic.getId());
                Response<List<Valoracion>> response=call.execute();
                if (response.isSuccessful()) {
                    respuesta=Constantes.OK;
                    valoraciones=(ArrayList<Valoracion>) response.body();
                } else if(response.code()== HttpURLConnection.HTTP_NOT_FOUND) {
                    respuesta=Constantes.ERROR_NO_ENCONTRADO;
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
                    //Calcular la media de todas las valoraciones
                    float totalValoracion=0;
                    for(Valoracion valoracion:valoraciones) {
                        totalValoracion+=valoracion.getPuntuacion();
                    }
                    mediaValoraciones=totalValoracion/valoraciones.size();
                    //Asignar ese valor a la RatingBar
                    valoracionMediaUsuarios.setRating(mediaValoraciones);
                    tvMedia.setText(valoraciones.size()+" "+getResources().getString(R.string.ratings)+"\n"+mediaValoraciones+" "+getResources().getString(R.string.stars));
                    break;
                case Constantes.ERROR_SERVIDOR:
                    noHayValoraciones();
                    crearToast(getResources().getString(R.string.errorServer));
                    break;
                case Constantes.ERROR_NO_ENCONTRADO:
                    noHayValoraciones();
                    break;
                case Constantes.ERROR_GENERICO:
                    noHayValoraciones();
                    crearToast(getResources().getString(R.string.error));
            }
        }
    }

    private class Valorar extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... voids) {
            int respuesta;

            Valoracion valoracion=new Valoracion(comic, usuario, valoracionUsuario.getRating());

            ValoracionEndpoints valoracionEndpoints=retrofit.create(ValoracionEndpoints.class);
            try {
                Call<ResponseBody> call=valoracionEndpoints.guardarValoracion(token, valoracion);
                Response<ResponseBody> response=call.execute();
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
            switch (respuesta) {
                case Constantes.OK:
                    //Actualizar la media de valoraciones
                    new ObtenerValoraciones().execute();
                    break;
                case Constantes.ERROR_SERVIDOR:
                    crearToast(getResources().getString(R.string.errorServer));
                    break;
                case Constantes.ERROR_GENERICO:
                    crearToast(getResources().getString(R.string.error));
            }
        }
    }

    private void noHayValoraciones() {
        //Si no se encuentra ninguna valoración hecha
        mediaValoraciones=0;
        valoracionMediaUsuarios.setRating(mediaValoraciones);
        tvMedia.setText(getResources().getString(R.string.noRatings));
    }

    private void crearToast(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
    }
}