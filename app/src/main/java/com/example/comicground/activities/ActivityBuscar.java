package com.example.comicground.activities;

import static com.example.comicground.api.ClienteAPI.retrofit;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
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
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class ActivityBuscar extends AppCompatActivity implements View.OnClickListener {

    //Vistas
    ListView listaComics;
    EditText etBuscar;
    ImageButton btnBuscar;
    FloatingActionButton btnSalir;
    FloatingActionButton btnConfiguracion;
    FloatingActionButton btnPerfil;

    //Variables de vistas
    String titulo;

    //Para la lista de cómics
    AdapterComics adapterComics;
    ArrayList<Comic> comics=new ArrayList<>();

    //Para pasar al siguiente Intent como extra
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
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnBuscar:
                titulo=etBuscar.getText().toString();
                new BuscarComics().execute();
                break;
            case R.id.btnSalir:
                Intent irAInicio=new Intent(getApplicationContext(), ActivityInicio.class);
                startActivity(irAInicio);
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
            int respuesta=Constantes.OK;
            SharedPreferences datosUsuario=getSharedPreferences("datosUsuario", Context.MODE_PRIVATE);
            String token=datosUsuario.getString("access_token", "");
            ComicEndpoints comicEndpoints=retrofit.create(ComicEndpoints.class);
            try {
                Call<List<Comic>> call=comicEndpoints.obtenerComicsPorTitulo("Bearer "+token, titulo);
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
            switch(respuesta) {
                case Constantes.OK:
                    //Crear el Adapter con los cómics y pasarlo al ListView
                    adapterComics=new AdapterComics(comics, ActivityBuscar.this);
                    listaComics.setAdapter(adapterComics);
                    break;
                case Constantes.ERROR_CREDENCIALES:
                    //Refrescar token o salir de la app e informar al usuario ¿?
                    Toast.makeText(ActivityBuscar.this, "Error de credenciales", Toast.LENGTH_SHORT).show();
                    break;
                case Constantes.ERROR_NO_ENCONTRADO:
                    //Si no se ha encontrado ningún cómic
                    Toast.makeText(ActivityBuscar.this, "Oops... No se ha encontrado nada", Toast.LENGTH_SHORT).show();
                    break;
                case Constantes.ERROR_SERVIDOR:
                    Toast.makeText(ActivityBuscar.this, "Error, no se ha podido conectar al servidor", Toast.LENGTH_SHORT).show();
                    break;
                case Constantes.ERROR_GENERICO:
                    Toast.makeText(ActivityBuscar.this, "Ha ocurrido un error inesperado", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            Toast.makeText(ActivityBuscar.this, "Error, se ha cancelado el proceso de búsqueda", Toast.LENGTH_SHORT).show();
        }
    }

    private void encontrarVistasPorId() {
        //Encontrar las vistas de la interfaz gráfica y pasarlas a sus objetos
        etBuscar=findViewById(R.id.etBuscar);
        btnSalir=findViewById(R.id.btnSalir);
        btnBuscar=findViewById(R.id.btnBuscar);
        btnConfiguracion=findViewById(R.id.btnConfiguracion);
        btnPerfil=findViewById(R.id.btnPerfil);

        //Encontrar el ListView del fragmento y asignarle un OnItemClickListener
        listaComics=findViewById(R.id.listView_comics);
        listaComics.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Comic comic=(Comic) adapterComics.getItem(i);
                Intent irAComic=new Intent(getApplicationContext(), ActivityComic.class);
                irAComic.putExtra("comic", comic);
                irAComic.putExtra("usuario", usuario);
                irAComic.putExtra("token", token);
                startActivity(irAComic);
            }
        });

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
}