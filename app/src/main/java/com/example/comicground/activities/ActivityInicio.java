package com.example.comicground.activities;

import static com.example.comicground.api.ClienteAPI.oAuthEndpoints;
import static com.example.comicground.api.ClienteAPI.usuarioEndpoints;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.comicground.R;
import com.example.comicground.models.PeticionInicioSesion;
import com.example.comicground.models.OAuthToken;
import com.example.comicground.models.Usuario;
import com.example.comicground.utils.AESEncriptacion;
import com.example.comicground.utils.Constantes;
import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;
import java.util.Date;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Response;

public class ActivityInicio extends AppCompatActivity implements View.OnClickListener {

    //Vistas
    ProgressBar progressBar;
    CheckBox cbMantenerSesion;
    TextInputLayout etNombreDeUsuario;
    TextInputLayout etContrasena;
    AppCompatButton btnIniciarSesion;
    AppCompatButton btnRegistro;

    //Variables de vistas
    String nombreDeUsuario;
    String contrasena;

    //Datos del usuario guardados en preferencias compartidas
    String nombreDeUsuarioPrefComp;
    String contrasenaPrefComp;
    Long expiresAt;
    String token;
    boolean mantenerSesion;

    //Datos de Intent
    boolean sesionCerrada;

    //Para mandar al siguiente Intent como extra
    Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Asignar Layout inicial
        setContentView(R.layout.layout_cargando);
        //Ocultar barra superior
        Objects.requireNonNull(getSupportActionBar()).hide();
        //Obtener si la sesi??n ha sido cerrada de los extras del Intent
        sesionCerrada=getIntent().getBooleanExtra(Constantes.CERRAR_SESION, false);
        //Si la sesi??n ha sido cerrada
        if(sesionCerrada) {
            //Asignar Layout, vistas a los objetos y algunas caracter??sticas
            asignarLayoutYVistas();
        //Si no ha sido cerrada se intenta mantener la sesi??n iniciada
        } else {
            //Mantener sesi??n iniciada si as?? lo desea el usuario y si el token no est?? caducado (y si existen preferencias compartidas)
            mantenerSesionIniciada();
        }
    }

    /*
     * OnClickListener de Activity
     */
    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.btnIniciarSesion:
                //Quitar los errores
                limpiarErrores();
                //Actualizar las variables con los datos de los EditText
                camposAVariables();
                if(comprobarVariables()) {
                    //Iniciar tarea as??ncrona para iniciar sesi??n
                    new IniciarSesion().execute();
                }
                break;
            case R.id.btnRegistro:
                Intent irARegistrar=new Intent(getApplicationContext(), ActivityRegistro.class);
                startActivity(irARegistrar);
                limpiarCampos();
        }
    }


    /*
     * Comprobaciones
     */

    private boolean comprobarVariables() {
        boolean validado=true;

        if(!comprobarNombreDeUsuario()) {
            validado=false;
        }
        if(!comprobarContrasena()) {
            validado=false;
        }
        return validado;
    }

    private boolean comprobarNombreDeUsuario() {
        if(nombreDeUsuario.equals(Constantes.VACIO)) {
            etNombreDeUsuario.setError(getResources().getString(R.string.writeUsername));
            return false;
        }
        if(nombreDeUsuario.length()>20) {
            etNombreDeUsuario.setError(getResources().getString(R.string.usernameTooLong));
            return false;
        }
        return true;
    }

    private boolean comprobarContrasena() {
        if(contrasena.equals(Constantes.VACIO)) {
            etContrasena.setError(getResources().getString(R.string.writePass));
            return false;
        }

        if (contrasena.length()<8) {
            etContrasena.setError(getResources().getString(R.string.passwordTooShort));
            return false;
        }

        return true;
    }


    /*
     * Tarea as??ncrona para iniciar sesi??n
     */

    @SuppressLint("StaticFieldLeak")
    private class IniciarSesion extends AsyncTask<Void, Void, Integer> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Mostrar barra cargando
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            int respuesta=Constantes.OK;
            //Encriptar la contrase??a (AES)
            String contrasenaEncriptada= AESEncriptacion.encriptar(contrasena);

            //Obtener los datos de las preferencias compartidas
            obtenerDatos();

            //Comprobar si las preferencias compartidas son de este usuario (o si existen preferencias compartidas)
            if(!nombreDeUsuarioPrefComp.equals(nombreDeUsuario)) {
               //Llamar de nuevo a /oauth/token y guardar los datos en preferencias compartidas
                respuesta=obtenerOAuthToken(nombreDeUsuario, contrasenaEncriptada);
            //Comprobar si ya ha expirado el token
            } else if((expiresAt-new Date().getTime())<0) {
                //Llamar de nuevo a /oauth/token y guardar los datos en preferencias compartidas
                respuesta=obtenerOAuthToken(nombreDeUsuario, contrasenaEncriptada);
            }
            //Si la obtenci??n del token ha sido correcta o no se ha necesitado obtener uno nuevo
            if(respuesta==Constantes.OK) {
                //Actualizar la variable mantener_sesion_iniciada de las preferencias compartidas
                guardarMantenerSesion();
                //Crear el objeto petici??n
                PeticionInicioSesion peticion=new PeticionInicioSesion(nombreDeUsuario, contrasenaEncriptada);
                //Intentar el inicio de sesi??n llamando a la API a trav??s de Retrofit
                respuesta=intentarIniciarSesion(token, peticion);
            }
            return respuesta;
        }

        @Override
        protected void onPostExecute(Integer respuesta) {
            super.onPostExecute(respuesta);
            //Ocultar la barra de progreso
            progressBar.setVisibility(View.GONE);
            //Switch para accion segun respuesta
            switchRespuesta(respuesta);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            //Ocultar la barra de progreso
            progressBar.setVisibility(View.GONE);

            //Mostrar mensaje error
            Toast.makeText(ActivityInicio.this, getResources().getString(R.string.cancelled), Toast.LENGTH_SHORT).show();
        }
    }

    //M??todo que realiza la llamada a la API para iniciar sesi??n
    private int intentarIniciarSesion(String token, PeticionInicioSesion peticion) {
        int respuesta;
        try {
            //Realizar llamada para inciar sesi??n
            Call<Usuario> call=usuarioEndpoints.iniciarSesion(token, peticion);
            Response<Usuario> response=call.execute();
            if(response.isSuccessful()) {
                respuesta=Constantes.OK;
                usuario=response.body();
            } else {
                respuesta=Constantes.ERROR_CREDENCIALES;
            }
        } catch (IOException e) {
            respuesta=Constantes.ERROR_SERVIDOR;
        }
        return respuesta;
    }

    //Obtener el token
    private int obtenerOAuthToken(String nombreDeUsuario, String contrasenaEncriptada) {
        int respuesta;
        //Crear cabecera con credenciales del cliente (mi aplicaci??n)
        String credencialesCliente=crearCredencialesCliente();
        try {
            //Realizar llamada para obtener token
            Call<OAuthToken> obtenerToken=oAuthEndpoints.getAccessToken(credencialesCliente, nombreDeUsuario, contrasenaEncriptada, Constantes.GRANT_TYPE);
            Response<OAuthToken> response=obtenerToken.execute();
            if(response.isSuccessful()) {
                respuesta=Constantes.OK;
                //Cambiar expires_in por expires_at, fecha en la que expira (en milisegundos)
                Long expiresAt=new Date().getTime()+(response.body().getExpiresIn()*1000);
                //Guardar Token y credenciales del usuario
                guardarDatos(response.body().getAccessToken(), expiresAt, nombreDeUsuario, contrasenaEncriptada);
                //Actualizar las variables con los nuevos datos de preferencias compartidas
                obtenerDatos();
            } else {
                respuesta=Constantes.ERROR_CREDENCIALES;
            }
        } catch(IOException e) {
            respuesta=Constantes.ERROR_SERVIDOR;
        }
        return respuesta;
    }

    //Switch con distintas acciones seg??n la respuesta
    private void switchRespuesta(int respuesta) {
        switch(respuesta) {
            case Constantes.OK:
                //Ir a la siguiente Activity
                crearIntent();
                //Terminar esta Activity
                finish();
                break;
            case Constantes.ERROR_CREDENCIALES:
                //El usuario no existe o la contrase??a es incorrecta
                etNombreDeUsuario.setError(Constantes.ESPACIO);
                etContrasena.setError(Constantes.SALTO_LINEA+getResources().getString(R.string.errorUser));
                break;
            case Constantes.ERROR_SERVIDOR:
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.errorServer), Toast.LENGTH_SHORT).show();
        }
    }


    /*
     * M??todo y tarea as??ncrona para intentar mantener sesi??n iniciada
     */

    //M??todo para intentar mantener sesi??n iniciada
    private void mantenerSesionIniciada() {
        //Obtener preferencias compartidas
        obtenerDatos();
        obtenerMantenerSesion();
        //Si no hay preferencias compartidas, o si no se quiere mantener la sesi??n iniciada o si el token ha expirado
        if(nombreDeUsuarioPrefComp.equals(Constantes.VACIO) || !mantenerSesion) {
            //Asignar Layout, vistas a los objetos y algunas caracter??sticas
            asignarLayoutYVistas();
            return;
        } else if((expiresAt-new Date().getTime())<0) {
            asignarLayoutYVistas();
            Toast.makeText(this, getResources().getString(R.string.tokenExpired), Toast.LENGTH_LONG).show();
            return;
        }
        //Iniciar tarea as??ncrona para iniciar la sesi??n
        new IntentarMantenerSesionIniciada().execute();
    }

    //Tarea as??ncrona para intentar mantener sesi??n iniciada
    @SuppressLint("StaticFieldLeak")
    private class IntentarMantenerSesionIniciada extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... voids) {
            //Crear objeto petici??n de inicio de sesi??n
            PeticionInicioSesion peticion=new PeticionInicioSesion(nombreDeUsuarioPrefComp, contrasenaPrefComp);
            //Llamada a API para intentar iniciar sesi??n
            return intentarIniciarSesion(token, peticion);
        }

        @Override
        protected void onPostExecute(Integer respuesta) {
            super.onPostExecute(respuesta);
            switch(respuesta) {
                case Constantes.OK:
                    //Ir a la siguiente Activity
                    crearIntent();
                    //Terminar esta Activity
                    finish();
                    break;
                case Constantes.ERROR_SERVIDOR:
                    //Mensaje error servidor
                    Toast.makeText(ActivityInicio.this, getResources().getString(R.string.errorServer), Toast.LENGTH_SHORT).show();
                default:
                    //Asignar Layout, vistas a los objetos y algunas caracter??sticas
                    asignarLayoutYVistas();
            }
        }
    }


    /*
     * ??tiles
     */

    private void asignarLayoutYVistas() {
        //Asignar el Layout
        setContentView(R.layout.activity_inicio);
        //Encontrar las vistas de la interfaz gr??fica y pasarlas a sus objetos
        progressBar=findViewById(R.id.progressBar);
        cbMantenerSesion=findViewById(R.id.cbMantenerSesion);
        etNombreDeUsuario=findViewById(R.id.etNombreDeUsuario);
        etContrasena=findViewById(R.id.etContrasena);
        btnIniciarSesion=findViewById(R.id.btnIniciarSesion);
        btnRegistro=findViewById(R.id.btnRegistro);

        //Asignar OnCheckedChanged al CheckBox
        cbMantenerSesion.setOnCheckedChangeListener((compoundButton, checked) -> mantenerSesion=checked);

        //Asignar el OnClickListener a los botones
        btnIniciarSesion.setOnClickListener(this);
        btnRegistro.setOnClickListener(this);

        //Para que no salga el icono de error en TextInput de la contrase??a (ya que inhabilitar??a el de mostrar/ocultar contrase??a)
        etContrasena.setErrorIconDrawable(null);
    }

    private void limpiarErrores() {
        etNombreDeUsuario.setError(null);
        etContrasena.setError(null);
    }

    private void limpiarCampos() {
        Objects.requireNonNull(etNombreDeUsuario.getEditText()).setText(Constantes.VACIO);
        Objects.requireNonNull(etContrasena.getEditText()).setText(Constantes.VACIO);
        limpiarErrores();
    }

    private void camposAVariables() {
        //Pasar los textos de los inputText a variables para mayor facilidad a la hora de manejar los datos
        nombreDeUsuario=Objects.requireNonNull(etNombreDeUsuario.getEditText()).getText().toString();
        contrasena=Objects.requireNonNull(etContrasena.getEditText()).getText().toString();
    }

    private String crearCredencialesCliente() {
        //Base64.NO_WRAP sirve como indicador del codificador para omitir todos los terminadores de l??nea (Se hace as?? en Android)
        return Constantes.TIPO_AUTH+Base64.encodeToString(Constantes.CREDENCIALES_APLICACION.getBytes(), Base64.NO_WRAP);
    }

    private void crearIntent() {
        Intent irABuscar=new Intent(getApplicationContext(), ActivityBuscar.class);
        irABuscar.putExtra(Constantes.USUARIO, usuario);
        irABuscar.putExtra(Constantes.TOKEN, token);
        startActivity(irABuscar);
    }


    /*
     * ??tiles preferencias compartidas
     */

    private void guardarDatos(String token, Long expiresAt, String nombreDeUsuario, String contrasenaEncriptada){
        //Guardar token y credenciales del usuario en las preferencias compartidas
        SharedPreferences datosUsuario=getApplicationContext().getSharedPreferences(Constantes.PREFERENCIAS_COMPARTIDAS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=datosUsuario.edit();
        editor.putString(Constantes.ACCESS_TOKEN,Constantes.TIPO_TOKEN+token);
        editor.putLong(Constantes.EXPIRES_AT, expiresAt);
        editor.putString(Constantes.NOMBRE_DE_USUARIO, nombreDeUsuario);
        editor.putString(Constantes.CONTRASENA, contrasenaEncriptada);
        editor.apply();
    }

    private void obtenerDatos() {
        //Obtener los datos de las preferencias compartidas
        SharedPreferences datosUsuario=getSharedPreferences(Constantes.PREFERENCIAS_COMPARTIDAS, Context.MODE_PRIVATE);
        nombreDeUsuarioPrefComp=datosUsuario.getString(Constantes.NOMBRE_DE_USUARIO, Constantes.VACIO);
        contrasenaPrefComp=datosUsuario.getString(Constantes.CONTRASENA, Constantes.VACIO);
        expiresAt=datosUsuario.getLong(Constantes.EXPIRES_AT, 0);
        token=datosUsuario.getString(Constantes.ACCESS_TOKEN, Constantes.VACIO);
    }

    private void obtenerMantenerSesion() {
        //Obtener las preferencias compartidas, solo la variable mantener_sesion_iniciada
        SharedPreferences datosUsuario=getSharedPreferences(Constantes.PREFERENCIAS_COMPARTIDAS, Context.MODE_PRIVATE);
        mantenerSesion=datosUsuario.getBoolean(Constantes.MANTENER_SESION_INICIADA, false);
    }

    private void guardarMantenerSesion() {
        //Se actualizan las preferencias compartidas, solo la variable mantener_sesion_iniciada
        SharedPreferences.Editor datosUsuario=getApplicationContext().getSharedPreferences(Constantes.PREFERENCIAS_COMPARTIDAS, Context.MODE_PRIVATE).edit();
        datosUsuario.putBoolean(Constantes.MANTENER_SESION_INICIADA, mantenerSesion);
        datosUsuario.apply();
    }
 }