package com.example.comicground.activities;

import static com.example.comicground.api.ClienteAPI.comentarioEndpoints;
import static com.example.comicground.api.ClienteAPI.valoracionEndpoints;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.comicground.R;
import com.example.comicground.adapters.AdapterComentarios;
import com.example.comicground.dialogs.DialogoPerfil;
import com.example.comicground.dialogs.DialogoSalir;
import com.example.comicground.models.Comentario;
import com.example.comicground.models.Comic;
import com.example.comicground.models.Usuario;
import com.example.comicground.models.Valoracion;
import com.example.comicground.utils.Constantes;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class ActivityComic extends AppCompatActivity implements View.OnClickListener {

    //Vistas
    ProgressBar progressBar;
    ListView listaComentarios;
    TextView tvTitulo;
    TextView tvMedia;
    ImageView ivPortada;
    EditText etComentar;
    Button btnEnviar;
    Button btnAtras;
    RatingBar valoracionMediaUsuarios;
    RatingBar valoracionUsuario;
    FloatingActionButton btnSalir;
    FloatingActionButton btnPerfil;

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
        Objects.requireNonNull(getSupportActionBar()).hide();
        //Obtener información extra del Intent
        obtenerExtrasIntent();
        //Asignar vistas a los objetos y algunas características
        encontrarVistasPorId();
        new ObtenerValoraciones().execute();
        new ObtenerComentarios().execute();
    }

    /*
     * OnClickListener de Activity
     */
    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.btnEnviar:
                //Obtener el texto a comentar
                texto=etComentar.getText().toString();
                //Si está vacío, no hacer nada
                if(texto.equals(Constantes.VACIO)) {
                    break;
                }
                //Ejecutar la tarea asíncrona para comentar
                new Comentar().execute();
                break;
            case R.id.btnAtras:
                //Terminar esta Activity
                finish();
                break;
            case R.id.btnSalir:
                //Mostrar el diálogo para cerrar sesión
                AlertDialog dialogoSalir=DialogoSalir.crearDialogoSalir(this);
                dialogoSalir.show();
                break;
            case R.id.btnPerfil:
                //Crear instancia de la clase DialogoPerfil
                DialogoPerfil dialogoPerfil=new DialogoPerfil(this, usuario, token);
                //Crear el diálogo de perfil
                AlertDialog alertDialogoPerfil=dialogoPerfil.crearDialogoPerfil();
                //Mostrar el diálogo
                alertDialogoPerfil.show();
                //Actualizar nombre de usuario si este ha sido modificado en el diálogo
                obtenerNombreDeUsuarioDePreferenciasCompartidas();
        }
    }

    /*
     * Tarea asíncrona para obtener comentarios
     */
    @SuppressLint("StaticFieldLeak")
    private class ObtenerComentarios extends AsyncTask<Void, Void, Integer> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Mostrar barra cargando
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            int respuesta;
            try {
                //Realizar llamada para obtener comentarios por id del cómic
                Call<List<Comentario>> call=comentarioEndpoints.obtenerComentariosPorComic(token, comic.getId());
                Response<List<Comentario>> response=call.execute();
                if (response.isSuccessful()) {
                    respuesta=Constantes.OK;
                    comentarios=(ArrayList<Comentario>) response.body();
                    //Ordenar de más nuevo a más viejo (improvisación dado que no he podido usar Date.util para las fechas)
                    if (comentarios!=null) Collections.reverse(comentarios);
                } else if(response.code()== HttpURLConnection.HTTP_NOT_FOUND) {
                    respuesta=Constantes.ERROR_NO_ENCONTRADO;
                } else if(response.code()==HttpURLConnection.HTTP_UNAUTHORIZED) {
                    respuesta=Constantes.ERROR_CREDENCIALES;
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
            //Ocultar barra cargando
            progressBar.setVisibility(View.GONE);
            switch (respuesta) {
                case Constantes.OK:
                    adapterComentarios=new AdapterComentarios(comentarios, getApplicationContext());
                    listaComentarios.setAdapter(adapterComentarios);
                    break;
                case Constantes.ERROR_NO_ENCONTRADO:
                   crearToast(getResources().getString(R.string.errorNotFoundComments));
                    break;
                case Constantes.ERROR_CREDENCIALES:
                    //Ha caducado el token, obligar a salir
                    tokenCaducado();
                    break;
                case Constantes.ERROR_SERVIDOR:
                    crearToast(getResources().getString(R.string.errorServer));
                    break;
                case Constantes.ERROR_GENERICO:
                    crearToast(getResources().getString(R.string.error));
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            //Ocultar barra cargando
            progressBar.setVisibility(View.GONE);
            crearToast(getResources().getString(R.string.cancelled));
        }
    }

    /*
     * Tarea asíncrona obtener las valoraciones
     */
    @SuppressLint("StaticFieldLeak")
    private class ObtenerValoraciones extends AsyncTask<Void, Void, Integer> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Mostrar barra cargando
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            int respuesta;
            try {
                //Realizar llamada para obtener valoraciones por id de cómic
                Call<List<Valoracion>> call=valoracionEndpoints.obtenerValoracionesPorComic(token, comic.getId());
                Response<List<Valoracion>> response=call.execute();
                if (response.isSuccessful()) {
                    respuesta=Constantes.OK;
                    valoraciones=(ArrayList<Valoracion>) response.body();
                } else if(response.code()== HttpURLConnection.HTTP_NOT_FOUND) {
                    respuesta=Constantes.ERROR_NO_ENCONTRADO;
                } else if(response.code()==HttpURLConnection.HTTP_UNAUTHORIZED) {
                    respuesta=Constantes.ERROR_CREDENCIALES;
                } else {
                    respuesta=Constantes.ERROR_GENERICO;
                }
            } catch (IOException e) {
                respuesta=Constantes.ERROR_SERVIDOR;
            }
            return respuesta;
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(Integer respuesta) {
            super.onPostExecute(respuesta);
            //Ocultar barra cargando
            progressBar.setVisibility(View.GONE);
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
                    //Mostrar cuantos usuarios han valorado y media de valoraciones en texto
                    DecimalFormat formatoDecimal=new DecimalFormat(Constantes.FORMATO_UN_DECIMAL);
                    tvMedia.setText(valoraciones.size()+Constantes.ESPACIO+getResources().getString(R.string.ratings)+Constantes.SALTO_LINEA+formatoDecimal.format(mediaValoraciones)+Constantes.ESPACIO+getResources().getString(R.string.stars));
                    break;
                case Constantes.ERROR_CREDENCIALES:
                    //Ha caducado el token, obligar a salir
                    tokenCaducado();
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

        @Override
        protected void onCancelled() {
            super.onCancelled();
            //Ocultar barra cargando
            progressBar.setVisibility(View.GONE);
            crearToast(getResources().getString(R.string.cancelled));
        }
    }

    /*
     * Tarea asíncrona para comentar
     */
    @SuppressLint("StaticFieldLeak")
    private class Comentar extends AsyncTask<Void, Void, Integer> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Mostrar barra cargando
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            int respuesta;
            //Crear objeto comentario
            Comentario comentario=new Comentario(comic, usuario, texto);
            try {
                //Realizar llamada para guardar comentario
                Call<ResponseBody> call=comentarioEndpoints.guardarComentario(token, comentario);
                Response<ResponseBody> response=call.execute();
                if (response.isSuccessful()) {
                    respuesta=Constantes.OK;
                } else if(response.code()==HttpURLConnection.HTTP_UNAUTHORIZED) {
                    respuesta=Constantes.ERROR_CREDENCIALES;
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
            //Ocultar barra cargando
            progressBar.setVisibility(View.GONE);
            switch(respuesta) {
                case Constantes.OK:
                    etComentar.setText(Constantes.VACIO);
                    crearToast(getResources().getString(R.string.commentDone));
                    new ObtenerComentarios().execute();
                    break;
                case Constantes.ERROR_CREDENCIALES:
                    //Ha caducado el token, obligar a salir
                    tokenCaducado();
                    break;
                case Constantes.ERROR_SERVIDOR:
                    crearToast(getResources().getString(R.string.errorServer));
                    break;
                case Constantes.ERROR_GENERICO:
                    crearToast(getResources().getString(R.string.error));
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            //Ocultar barra cargando
            progressBar.setVisibility(View.GONE);
            crearToast(getResources().getString(R.string.cancelled));
        }
    }

    /*
     * Tarea asíncrona para hacer valoración
     */
    @SuppressLint("StaticFieldLeak")
    private class Valorar extends AsyncTask<Void, Void, Integer> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Mostrar barra cargando
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            int respuesta;
            //Crear objeto valoración
            Valoracion valoracion=new Valoracion(comic, usuario, valoracionUsuario.getRating());
            try {
                //Realizar llamada para guardar valoración
                Call<ResponseBody> call=valoracionEndpoints.guardarValoracion(token, valoracion);
                Response<ResponseBody> response=call.execute();
                if (response.isSuccessful()) {
                    respuesta=Constantes.OK;
                } else if(response.code()==HttpURLConnection.HTTP_UNAUTHORIZED) {
                    respuesta=Constantes.ERROR_CREDENCIALES;
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
            //Ocultar barra cargando
            progressBar.setVisibility(View.GONE);
            switch (respuesta) {
                case Constantes.OK:
                    //Actualizar la media de valoraciones
                    new ObtenerValoraciones().execute();
                    break;
                case Constantes.ERROR_CREDENCIALES:
                    //Ha caducado el token, obligar a salir
                    tokenCaducado();
                break;
                case Constantes.ERROR_SERVIDOR:
                    crearToast(getResources().getString(R.string.errorServer));
                    break;
                case Constantes.ERROR_GENERICO:
                    crearToast(getResources().getString(R.string.error));
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            //Ocultar barra cargando
            progressBar.setVisibility(View.GONE);
            crearToast(getResources().getString(R.string.cancelled));
        }
    }


    /*
     * útiles
     */

    @SuppressLint("InflateParams")
    private void encontrarVistasPorId() {
        progressBar=findViewById(R.id.progressBar);
        tvTitulo=findViewById(R.id.titulo);
        tvMedia=findViewById(R.id.media);
        ivPortada=findViewById(R.id.portada);
        listaComentarios=findViewById(R.id.lista_comentarios);
        valoracionMediaUsuarios=findViewById(R.id.valoracionMediaUsuarios);

        //Asignar botones flotantes
        btnSalir=findViewById(R.id.btnSalir);
        btnPerfil=findViewById(R.id.btnPerfil);

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
        //Asignar imagen al ImageView con la librería Picasso, sirve para pasar más fácilmente una URL a un ImageView
        Picasso.get().load(comic.getPortada()).into(ivPortada);

        //Asignar OnClickListener a los botones
        btnEnviar.setOnClickListener(this);
        btnAtras.setOnClickListener(this);
        btnSalir.setOnClickListener(this);
        btnPerfil.setOnClickListener(this);

        //Asignar OnRatingBarChangeListener al RatingBar
        valoracionUsuario.setOnRatingBarChangeListener((ratingBar, v, b) -> new Valorar().execute());
    }

    private void obtenerExtrasIntent() {
        //Obtener los datos pasados por la Activity anterior
        comic=(Comic) getIntent().getSerializableExtra(Constantes.COMIC);
        usuario=(Usuario) getIntent().getSerializableExtra(Constantes.USUARIO);
        token=getIntent().getStringExtra(Constantes.TOKEN);
    }

    private void noHayValoraciones() {
        //Si no se encuentra ninguna valoración hecha
        mediaValoraciones=0;
        valoracionMediaUsuarios.setRating(mediaValoraciones);
        tvMedia.setText(getResources().getString(R.string.noRatings));
    }

    private void obtenerNombreDeUsuarioDePreferenciasCompartidas() {
        //Obtener las preferencias compartidas, solo la variable nombre_de_usuario
        SharedPreferences datosUsuario=getSharedPreferences(Constantes.PREFERENCIAS_COMPARTIDAS, Context.MODE_PRIVATE);
        String nombreDeUsuarioPC=datosUsuario.getString(Constantes.NOMBRE_DE_USUARIO, usuario.getNombreDeUsuario());
        if(!usuario.getNombreDeUsuario().equals(nombreDeUsuarioPC)) {
            usuario.setNombreDeUsuario(nombreDeUsuarioPC);
        }
    }

    //Si ha caducado el token
    private void tokenCaducado() {
        //Mostrar Toast largo para informar
        Toast.makeText(this, getResources().getString(R.string.tokenExpired), Toast.LENGTH_LONG).show();
        //Token caducado, obligar a iniciar sesión de nuevo
        Intent irAInicio=new Intent(getApplicationContext(), ActivityInicio.class);
        irAInicio.putExtra(Constantes.CERRAR_SESION, true);
        //Flags para borrar las Activities anteriores también
        irAInicio.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(irAInicio);
    }

    private void crearToast(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
    }
}