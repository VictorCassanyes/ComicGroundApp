package com.example.comicground.activities;

import static com.example.comicground.api.ClienteAPI.retrofit;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.comicground.R;
import com.example.comicground.adapters.AdapterComics;
import com.example.comicground.api.endpoints.ComicEndpoints;
import com.example.comicground.models.Comic;
import com.example.comicground.models.Usuario;
import com.example.comicground.utils.Constantes;
import com.getbase.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class ActivityBuscar extends AppCompatActivity implements View.OnClickListener {

    //Vistas
    ProgressBar progressBar;
    TextView noHayComics;
    EditText etBuscar;
    ImageButton btnBuscar;
    FloatingActionButton btnSalir;
    FloatingActionButton btnConfiguracion;
    FloatingActionButton btnPerfil;

    //Variables de vistas
    String titulo;


    //Para la lista de cómics

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
        getSupportActionBar().hide();
        //Asignar vistas a los objetos y algunas características
        encontrarVistasPorId();
        //Obtener extras del Intent
        obtenerExtrasIntent();
        //Cargar últimos cómics
        obtenerComicsRecientes();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnBuscar:
                //Obtener el texto a buscar
                titulo=etBuscar.getText().toString();
                if(titulo.equals("")) {
                    crearToast("¡Escribe algo para que podamos buscarlo!");
                    break;
                }
                //Mostrar barra cargando y ocultar mensaje que es para cuando no se encuentra ningún cómic
                progressBar.setVisibility(View.VISIBLE);
                noHayComics.setVisibility(View.GONE);
                //Añadir si no lo tiene, útil para la primera vez...
                if(listaComics.getFooterViewsCount()<=0) {
                    listaComics.addFooterView(pieDeLista);
                }
                //Iniciar tarea asíncrona para buscar y mostrar los cómics que contengan lo escrito por el usuario en el título
                new BuscarComics().execute();
                break;
            case R.id.btnSalir:
                //Que se cierre la sesión para que no vuelva a entrar
                SharedPreferences.Editor datosUsuario=getApplicationContext().getSharedPreferences(Constantes.PREFERENCIAS_COMPARTIDAS, Context.MODE_PRIVATE).edit();
                datosUsuario.putBoolean("mantener_sesion_iniciada", false);
                datosUsuario.apply();
                //Cerrar la sesión
                Intent irAInicio=new Intent(getApplicationContext(), ActivityInicio.class);
                irAInicio.putExtra("sesionCerrada", true);
                startActivity(irAInicio);
                //Terminar esta Activityty
                finish();
                break;
            case R.id.btnConfiguracion:
                break;
            case R.id.btnPerfil:

        }
    }

    private class BuscarComics extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... voids) {
            int respuesta;
            obtenerTokenDePreferenciasCompartidas();
            ComicEndpoints comicEndpoints=retrofit.create(ComicEndpoints.class);
            try {
                Call<List<Comic>> call=comicEndpoints.obtenerComicsPorTitulo(token, titulo);
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
            //Resetear texto de la cabecera, especialmente me sirve para saber cuando se ha hecho una búsqueda por primera vez
            textoCabecera.setText("");
            //Switch con las acciones a realizar según el tipo de respuesta obtenida
            switchRespuesta(respuesta);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            //Ocultar barra cargando
            progressBar.setVisibility(View.GONE);
            crearToast("Error, se ha cancelado el proceso de búsqueda");
        }
    }


    private void crearToast(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
    }

    private void encontrarVistasPorId() {
        //Encontrar las vistas de la interfaz gráfica y pasarlas a sus objetos
        progressBar=findViewById(R.id.progressBar);
        noHayComics=findViewById(R.id.noHayComics);
        etBuscar=findViewById(R.id.etBuscar);
        btnSalir=findViewById(R.id.btnSalir);
        btnBuscar=findViewById(R.id.btnBuscar);
        btnConfiguracion=findViewById(R.id.btnConfiguracion);
        btnPerfil=findViewById(R.id.btnPerfil);

        fragmentComics=findViewById(R.id.fragmentoComics);

        //Encontrar el ListView del fragmento y asignarle un OnItemClickListener
        listaComics=findViewById(R.id.listView_comics);
        listaComics.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Comic comic=(Comic) adapterComics.getItem(i-1);
                Intent irAComic=new Intent(getApplicationContext(), ActivityComic.class);
                irAComic.putExtra("comic", comic);
                irAComic.putExtra("usuario", usuario);
                irAComic.putExtra("token", token);
                startActivity(irAComic);
            }
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
        btnConfiguracion.setOnClickListener(this);
        btnPerfil.setOnClickListener(this);
    }

    private void obtenerExtrasIntent() {
        token=getIntent().getStringExtra("token");
        usuario=(Usuario) getIntent().getSerializableExtra("usuario");
    }

    private void obtenerComicsRecientes() {
        //Mostrar barra cargando y ocultar mensaje que es para cuando no se encuentra ningún cómic
        progressBar.setVisibility(View.VISIBLE);
        noHayComics.setVisibility(View.GONE);
        //Iniciar la tarea asíncrona para mostrar los cómics recientes
        new ObtenerComicsRecientes().execute();
    }

    private class ObtenerComicsRecientes extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... voids) {
            int respuesta;
            obtenerTokenDePreferenciasCompartidas();
            ComicEndpoints comicEndpoints=retrofit.create(ComicEndpoints.class);
            try {
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
            crearToast("Error, se ha cancelado el proceso de búsqueda");
        }
    }

    private void obtenerTokenDePreferenciasCompartidas() {
        if(token==null || token.equals("")) {
            SharedPreferences datosUsuario=getSharedPreferences("datosUsuario", Context.MODE_PRIVATE);
            token=datosUsuario.getString("access_token", "");
        }
    }

    private void switchRespuesta(int respuesta) {
        switch(respuesta) {
            case Constantes.OK:
                //Mostrar fragmento con la lista de cómics
                fragmentComics.setVisibility(View.VISIBLE);
                //Cambiar el texto del header
                if(textoCabecera.getText().toString().equals("")) {
                    textoCabecera.setText(getResources().getString(R.string.foundComics)+" "+comics.size()+" "+getResources().getString(R.string.comics));
                }
                //Crear el Adapter con los cómics y pasarlo al ListView
                adapterComics=new AdapterComics(comics, ActivityBuscar.this);
                listaComics.setAdapter(adapterComics);
                break;
            case Constantes.ERROR_CREDENCIALES:
                //Refrescar token o salir de la app e informar al usuario ¿?
                cambiarVisibilidad();
                crearToast("Error de credenciales");
                break;
            case Constantes.ERROR_NO_ENCONTRADO:
                //Si no se ha encontrado ningún cómic
                cambiarVisibilidad();
                break;
            case Constantes.ERROR_SERVIDOR:
                cambiarVisibilidad();
                crearToast("Error, no se ha podido conectar al servidor");
                break;
            case Constantes.ERROR_GENERICO:
                cambiarVisibilidad();
                crearToast("Ha ocurrido un error inesperado");
        }
    }

    private void cambiarVisibilidad() {
        //Ocultar el fragmento
        fragmentComics.setVisibility(View.GONE);
        //Mostrar mensaje de que no se ha encontrado ningún cómic
        noHayComics.setVisibility(View.VISIBLE);
    }
}