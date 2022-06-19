package com.example.comicground.activities;

import static com.example.comicground.api.ClienteAPI.comicEndpoints;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.comicground.R;
import com.example.comicground.adapters.AdapterComics;
import com.example.comicground.dialogs.DialogoPerfil;
import com.example.comicground.dialogs.DialogoSalir;
import com.example.comicground.models.Comic;
import com.example.comicground.models.Usuario;
import com.example.comicground.utils.Constantes;
import com.getbase.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Response;

public class ActivityBuscar extends AppCompatActivity implements View.OnClickListener {

    //Vistas
    ProgressBar progressBar;
    TextView noHayComics;
    EditText etBuscar;
    ImageButton btnBuscar;
    FloatingActionButton btnSalir;
    FloatingActionButton btnPerfil;

    //Variables de vistas
    String titulo;

    //Vistas para la lista de cómics
    ListView listaComics;
    View pieDeLista;
    View cabeceraDeLista;
    View fragmentComics;
    TextView textoCabecera;

    //Adaptador para el ListView
    AdapterComics adapterComics;

    //ArrayList para los cómics
    ArrayList<Comic> comics=new ArrayList<>();

    //Usuario y token, para utilizar y pasar como extras de Intent
    Usuario usuario;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar);
        //Ocultar barra superior
        Objects.requireNonNull(getSupportActionBar()).hide();
        //Asignar vistas a los objetos y algunas características
        encontrarVistasPorId();
        //Obtener extras del Intent
        obtenerExtrasIntent();
        //Cargar últimos cómics
        new ObtenerComicsRecientes().execute();
    }

    /*
     * OnClickListener de Activity
     */
    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnBuscar:
                //Obtener el texto a buscar y si está vacío mostrar Toast y no buscar los cómics
                titulo=etBuscar.getText().toString();
                if(titulo.equals(Constantes.VACIO)) {
                    crearToast(getResources().getString(R.string.writeSearch));
                    break;
                }
                //Iniciar tarea asíncrona para buscar y mostrar los cómics que contengan lo escrito por el usuario en el título
                new BuscarComics().execute();
                break;
            case R.id.btnSalir:
                //Mostrar el diálogo para salir
                AlertDialog dialogoSalir= DialogoSalir.crearDialogoSalir(this);
                dialogoSalir.show();
                break;
            case R.id.btnPerfil:
                //Actualizar nombre de usuario si este ha sido modificado en ActivityComic
                obtenerNombreDeUsuarioDePreferenciasCompartidas();
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
     * Tarea asíncrona para buscar cómics
     */
    @SuppressLint("StaticFieldLeak")
    private class BuscarComics extends AsyncTask<Void, Void, Integer> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Mostrar barra cargando y ocultar mensaje que es para cuando no se encuentra ningún cómic
            progressBar.setVisibility(View.VISIBLE);
            noHayComics.setVisibility(View.GONE);
            //Añadir el footer si no lo tiene, sirve para la primera vez...
            if(listaComics.getFooterViewsCount()<=0) {
                listaComics.addFooterView(pieDeLista);
            }
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            int respuesta;
            //Obtener token de preferencias compartidas por si acaso
            obtenerTokenDePreferenciasCompartidas();
            try {
                //Realizar llamada para obtener los cómics según el texto escrito por el usuario
                Call<List<Comic>> call=comicEndpoints.obtenerComicsPorTitulo(token, titulo);
                Response<List<Comic>> response=call.execute();
                if(response.isSuccessful()) {
                    respuesta=Constantes.OK;
                    comics=(ArrayList<Comic>) response.body();
                } else if(response.code()==HttpURLConnection.HTTP_UNAUTHORIZED) {
                    respuesta=Constantes.ERROR_CREDENCIALES;
                } else if(response.code()==HttpURLConnection.HTTP_NOT_FOUND) {
                    respuesta=Constantes.ERROR_NO_ENCONTRADO;
                    comics.clear();
                } else {
                    respuesta=Constantes.ERROR_GENERICO;
                }
            } catch(IOException e) {
                respuesta=Constantes.ERROR_SERVIDOR;
            }
            return respuesta;
        }

        @Override
        protected void onPostExecute(Integer respuesta) {
            super.onPostExecute(respuesta);
            //Ocultar barra cargando
            progressBar.setVisibility(View.GONE);
            //Resetear texto de la cabecera, especialmente me sirve para saber cuando se ha hecho una búsqueda por primera vez
            textoCabecera.setText(Constantes.VACIO);
            //Switch con las acciones a realizar según el tipo de respuesta obtenida
            switchRespuesta(respuesta);
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
     * Tarea asíncrona obtener cómics recientes
     */
    @SuppressLint("StaticFieldLeak")
    private class ObtenerComicsRecientes extends AsyncTask<Void, Void, Integer> {

        @Override
        protected void onPreExecute() {
            //Mostrar barra cargando y ocultar mensaje que es para cuando no se encuentra ningún cómic
            progressBar.setVisibility(View.VISIBLE);
            noHayComics.setVisibility(View.GONE);
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            int respuesta;
            //Obtener el token de preferencias compartidas por si acaso
            obtenerTokenDePreferenciasCompartidas();
            try {
                //Realizar llamada para obtener los cómics más recientes
                Call<List<Comic>> call=comicEndpoints.obtenerComicsRecientes(token);
                Response<List<Comic>> response=call.execute();
                if(response.isSuccessful()) {
                    respuesta=Constantes.OK;
                    comics=(ArrayList<Comic>) response.body();
                } else if(response.code()== HttpURLConnection.HTTP_UNAUTHORIZED) {
                    respuesta=Constantes.ERROR_CREDENCIALES;
                } else if(response.code()==HttpURLConnection.HTTP_NOT_FOUND) {
                    respuesta=Constantes.ERROR_NO_ENCONTRADO;
                    comics.clear();
                } else {
                    respuesta=Constantes.ERROR_GENERICO;
                }
            } catch(IOException e) {
                respuesta=Constantes.ERROR_SERVIDOR;
            }
            return respuesta;
        }

        @Override
        protected void onPostExecute(Integer respuesta) {
            super.onPostExecute(respuesta);
            //Ocultar barra cargando
            progressBar.setVisibility(View.GONE);
            //Switch con las acciones a realizar según el tipo de respuesta obtenida
            switchRespuesta(respuesta);
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
     * Switch con acciones distintas según la respuesta
     */
    @SuppressLint("SetTextI18n")
    private void switchRespuesta(int respuesta) {
        switch(respuesta) {
            case Constantes.OK:
                //Mostrar fragmento con la lista de cómics
                fragmentComics.setVisibility(View.VISIBLE);
                //Cambiar el texto del header
                if(textoCabecera.getText().toString().equals(Constantes.VACIO)) {
                    textoCabecera.setText(getResources().getString(R.string.foundComics)+Constantes.ESPACIO+comics.size()+Constantes.ESPACIO+getResources().getString(R.string.comics));
                }
                //Crear el Adapter con los cómics y pasarlo al ListView
                adapterComics=new AdapterComics(comics, ActivityBuscar.this);
                listaComics.setAdapter(adapterComics);
                break;
            case Constantes.ERROR_CREDENCIALES:
                //A caducado el token, obligar a salir
                tokenCaducado();
                break;
            case Constantes.ERROR_NO_ENCONTRADO:
                //Si no se ha encontrado ningún cómic
                cambiarVisibilidad();
                break;
            case Constantes.ERROR_SERVIDOR:
                cambiarVisibilidad();
                crearToast(getResources().getString(R.string.errorServer));
                break;
            case Constantes.ERROR_GENERICO:
                cambiarVisibilidad();
                crearToast(getResources().getString(R.string.error));
        }
    }


    /*
     * Útiles
     */

    private void crearToast(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("InflateParams")
    private void encontrarVistasPorId() {
        //Encontrar las vistas de la interfaz gráfica y pasarlas a sus objetos
        progressBar=findViewById(R.id.progressBar);
        noHayComics=findViewById(R.id.noHayComics);
        etBuscar=findViewById(R.id.etBuscar);
        btnBuscar=findViewById(R.id.btnBuscar);

        //Asignar botones flotantes
        btnSalir=findViewById(R.id.btnSalir);
        btnPerfil=findViewById(R.id.btnPerfil);

        //Asignar fragmento que contiene la lista de cómics
        fragmentComics=findViewById(R.id.fragmentoComics);

        //Encontrar el ListView del fragmento y asignarle un OnItemClickListener
        listaComics=findViewById(R.id.listView_comics);
        listaComics.setOnItemClickListener((adapterView, view, i, l) -> {
            Comic comic=(Comic) adapterComics.getItem(i-1);
            Intent irAComic=new Intent(getApplicationContext(), ActivityComic.class);
            irAComic.putExtra(Constantes.COMIC, comic);
            irAComic.putExtra(Constantes.USUARIO, usuario);
            irAComic.putExtra(Constantes.TOKEN, token);
            startActivity(irAComic);
        });

        //Asignar un layout al footer, este se añadirá cuando el usuario busque algo
        pieDeLista=((LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.footer_lista_comics, null, false);

        //Asignar un layout al header y este a la lista de cómics
        cabeceraDeLista=((LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.header_lista_comics, null, false);
        listaComics.addHeaderView(cabeceraDeLista);
        textoCabecera=cabeceraDeLista.findViewById(R.id.textoCabecera);

        //Asignar el OnClickListener a los botones
        btnBuscar.setOnClickListener(this);
        btnSalir.setOnClickListener(this);
        btnPerfil.setOnClickListener(this);
    }

    private void obtenerExtrasIntent() {
        token=getIntent().getStringExtra(Constantes.TOKEN);
        usuario=(Usuario) getIntent().getSerializableExtra(Constantes.USUARIO);
    }

    private void obtenerTokenDePreferenciasCompartidas() {
        if(token==null || token.equals(Constantes.VACIO)) {
            SharedPreferences datosUsuario=getSharedPreferences(Constantes.PREFERENCIAS_COMPARTIDAS, Context.MODE_PRIVATE);
            token=datosUsuario.getString(Constantes.ACCESS_TOKEN, Constantes.VACIO);
        }
    }

    private void obtenerNombreDeUsuarioDePreferenciasCompartidas() {
        //Obtener las preferencias compartidas, solo la variable nombre_de_usuario
        SharedPreferences datosUsuario=getSharedPreferences(Constantes.PREFERENCIAS_COMPARTIDAS, Context.MODE_PRIVATE);
        String nombreDeUsuarioPC=datosUsuario.getString(Constantes.NOMBRE_DE_USUARIO, usuario.getNombreDeUsuario());
        if(!usuario.getNombreDeUsuario().equals(nombreDeUsuarioPC)) {
            usuario.setNombreDeUsuario(nombreDeUsuarioPC);
        }
    }

    private void cambiarVisibilidad() {
        //Ocultar el fragmento
        fragmentComics.setVisibility(View.GONE);
        //Mostrar mensaje de que no se ha encontrado ningún cómic
        noHayComics.setVisibility(View.VISIBLE);
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

    //Override onBackPressed() y quitar el super(), así  el botón para ir hacia atrás del móvil no hará nada
    @Override
    public void onBackPressed() {}
}