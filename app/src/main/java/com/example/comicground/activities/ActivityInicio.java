package com.example.comicground.activities;

import static com.example.comicground.api.ClienteAPI.retrofit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.comicground.R;
import com.example.comicground.api.endpoints.OAuthEndpoints;
import com.example.comicground.api.endpoints.UsuarioEndpoints;
import com.example.comicground.api.peticiones.PeticionInicioSesion;
import com.example.comicground.models.OAuthToken;
import com.example.comicground.models.Usuario;
import com.example.comicground.utils.AESEncriptacion;
import com.example.comicground.utils.Constantes;
import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;
import java.util.Date;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class ActivityInicio extends AppCompatActivity implements View.OnClickListener {

    //Vistas
    ProgressBar progressBar;
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
    String refresh_token;

    //Para mandar al siguiente Intent como extra
    Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);
        //Ocultar barra superior
        getSupportActionBar().hide();
        //Asignar vistas a los objetos y algunas características
        encontrarVistasPorId();
        //Mantener sesión iniciada si el token no ha caducado (y si había datos en las preferencias compartidas)
        //mantenerSesionIniciada();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.btnIniciarSesion:
                //Quitar los errores
                limpiarErrores();
                //Actualizar las variables con los datos de los EditText
                camposAVariables();
                if(comprobarVariables()) {
                    //Mostrar barra cargando
                    progressBar.setVisibility(View.VISIBLE);
                    //Iniciar tarea asíncrona para iniciar sesión
                    new IniciarSesion().execute();
                }
                break;
            case R.id.btnRegistro:
                Intent irARegistrar=new Intent(getApplicationContext(), ActivityRegistro.class);
                startActivity(irARegistrar);
                limpiarCampos();
        }
    }

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
        if(nombreDeUsuario.equals("")) {
            etNombreDeUsuario.setError("Escriba su nombre de usuario");
            return false;
        }
        return true;
    }

    private boolean comprobarContrasena() {
        if(contrasena.equals("")) {
            etContrasena.setError("Escriba su contraseña");
            return false;
        }
        return true;
    }

    private void limpiarErrores() {
        etNombreDeUsuario.setError(null);
        etContrasena.setError(null);
    }

    private void limpiarCampos() {
        etNombreDeUsuario.getEditText().setText("");
        etContrasena.getEditText().setText("");
        limpiarErrores();
    }

    private void encontrarVistasPorId() {
        //Encontrar las vistas de la interfaz gráfica y pasarlas a sus objetos
        progressBar=findViewById(R.id.progressBar);
        etNombreDeUsuario=findViewById(R.id.etNombreDeUsuario);
        etContrasena=findViewById(R.id.etContrasena);
        btnIniciarSesion=findViewById(R.id.btnIniciarSesion);
        btnRegistro=findViewById(R.id.btnRegistro);

        //Asignar el OnClickListener a los botones
        btnIniciarSesion.setOnClickListener(this);
        btnRegistro.setOnClickListener(this);

        //Para que no salga el icono de error en TextInput de la contraseña (ya que inhabilitaría el de mostrar/ocultar contraseña)
        etContrasena.setErrorIconDrawable(null);
    }

    private void camposAVariables() {
        //Pasar los textos de los inputText a variables para mayor facilidad a la hora de manejar los datos
        nombreDeUsuario=etNombreDeUsuario.getEditText().getText().toString();
        contrasena=etContrasena.getEditText().getText().toString();
    }

    //Pruebas OAuth2
   /*
   class ActualizarToken extends AsyncTask<String, Void,String> {

        @Override
        protected String doInBackground(String... strings) {
            final MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
            String jsonStr="client_id=" + CLIENT_ID + "&refresh_token=" + Refreshtoken + "&grant_type=refresh_token";
            OkHttpClient okHttpClient=new OkHttpClient();
            Request peticion=new Request.Builder()
                    .url("https://www.googleapis.com/oauth2/v4/token")
                    .post(RequestBody.create(mediaType, jsonStr))
                    .build();

            try {
                okhttp3.Response response=okHttpClient.newCall(peticion).execute();
                assert response.body() != null;
                //Data Received
                return response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                JSONObject lJSONObject = new JSONObject(s);
                String ReAccessToken = lJSONObject.getString("access_token");
                String ReExpiresIn = lJSONObject.getString("expires_in");

                Authcode = ReAccessToken;
                Expiresin = Long.parseLong(ReExpiresIn);
                ExpiryTime = System.currentTimeMillis() + (Expiresin * 1000);

                saveData();

                // Toast.makeText(ActivityInicio.this, "The new Expiry time is: " + ExpiryTime + " and System time is " + System.currentTimeMillis(), Toast.LENGTH_LONG).show();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }*/

    private class IniciarSesion extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... voids) {
            int respuesta=Constantes.OK;
            //Encriptar la contraseña (AES)
            String contrasenaEncriptada= AESEncriptacion.encriptar(contrasena);

            //Obtener el nombre de usuario, token y momento en el que este expira de las preferencias compartidas
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

            //Si la obtención del token ha sido correcta, entonces se intenta iniciar sesión
            if(respuesta==Constantes.OK) {
                //Crear el objeto petición
                PeticionInicioSesion peticion=new PeticionInicioSesion(nombreDeUsuario, contrasenaEncriptada);
                //Intentar el inicio de sesión llamando a la API a través de Retrofit
                respuesta=intentarIniciarSesion(token, peticion);
            }
            return respuesta;
        }

        @Override
        protected void onPostExecute(Integer respuesta) {
            super.onPostExecute(respuesta);
            //Ocultar la barra de progreso
            ocultarBarraDeProgreso();
            //Switch para accion segun respuesta
            switchRespuesta(respuesta);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            //Ocultar la barra de progreso
            ocultarBarraDeProgreso();

            //Mostrar mensaje error
            Toast.makeText(ActivityInicio.this, "Error al intentar iniciar sesión, el proceso ha sido cancelado", Toast.LENGTH_SHORT).show();
        }
    }

    private int intentarIniciarSesion(String token, PeticionInicioSesion peticion) {
        int respuesta;

        //Crear objeto de endpoints necesario con retrofit
        UsuarioEndpoints usuarioEndpoints=retrofit.create(UsuarioEndpoints.class);

        //Realizar llamada al endpoint y mandar los datos necesarios al servidor
        try {
            Call<Usuario> call=usuarioEndpoints.iniciarSesion("Bearer " + token, peticion);
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

    private int obtenerOAuthToken(String nombreDeUsuario, String contrasenaEncriptada) {
        int respuesta;
        //Crear cabecera con credenciales del cliente (mi aplicación)
        String credencialesCliente=crearCredencialesCliente();
        //Crear objeto de endpoints necesario con retrofit (solo cuenta con un endpoint)
        OAuthEndpoints oAuthEndpoints=retrofit.create(OAuthEndpoints.class);

        try {
            //Realizar llamada al endpoint y mandar las credenciales de cliente y de usuario, junto al grant_type
            Call<OAuthToken> obtenerToken=oAuthEndpoints.getAccessToken(credencialesCliente, nombreDeUsuario, contrasenaEncriptada, Constantes.GRANT_TYPE);
            Response<OAuthToken> response=obtenerToken.execute();
            if(response.isSuccessful()) {
                respuesta=Constantes.OK;
                //Cambiar expires_in por expires_at, fecha en la que expira (en milisegundos)
                Long expiresAt=new Date().getTime()+(response.body().getExpiresIn()*1000);
                //Guardar Token y credenciales del usuario
                guardarDatos(response.body().getAccessToken(), response.body().getRefreshToken(), expiresAt, nombreDeUsuario, contrasenaEncriptada);
            } else {
                respuesta=Constantes.ERROR_CREDENCIALES;
            }
        } catch(IOException e) {
            respuesta=Constantes.ERROR_SERVIDOR;
        }
        return respuesta;
    }

    private void switchRespuesta(int respuesta) {
        switch(respuesta) {
            case Constantes.OK:
                //Ir a la siguiente Activity
                Intent irABuscar=new Intent(getApplicationContext(), ActivityBuscar.class);
                irABuscar.putExtra("usuario", usuario);
                irABuscar.putExtra("token", token);
                startActivity(irABuscar);
                //Terminar esta Activity
                finish();
                break;
            case Constantes.ERROR_CREDENCIALES:
                //El usuario no existe o la contraseña es incorrecta
                etNombreDeUsuario.setError(" ");
                etContrasena.setError("\n\n\nEl nombre de usuario o la contraseña no son correctos");
                break;
            case Constantes.ERROR_SERVIDOR:
                Toast.makeText(getApplicationContext(), "Error al conectar con el servidor", Toast.LENGTH_SHORT).show();
        }
    }

    private void guardarDatos(String token, String refreshToken, Long expiresAt, String nombreDeUsuario, String contrasenaEncriptada){
        //Guardar token y credenciales del usuario en las preferencias compartidas
        SharedPreferences datosUsuario=getApplicationContext().getSharedPreferences(Constantes.PREFERENCIAS_COMPARTIDAS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=datosUsuario.edit();
        editor.putString("access_token", token);
        editor.putString("refresh_token", refreshToken);
        editor.putLong("expires_at", expiresAt);
        editor.putString("nombre_de_usuario", nombreDeUsuario);
        editor.putString("contraseña", contrasenaEncriptada);
        editor.apply();
    }

    private void obtenerDatos() {
        //Obtener los datos de las preferencias compartidas
        SharedPreferences datosUsuario=getSharedPreferences(Constantes.PREFERENCIAS_COMPARTIDAS, Context.MODE_PRIVATE);
        nombreDeUsuarioPrefComp=datosUsuario.getString("nombre_de_usuario", "");
        contrasenaPrefComp=datosUsuario.getString("contraseña", "");
        expiresAt=datosUsuario.getLong("expires_at", 0);
        token=datosUsuario.getString("access_token", "");
        refresh_token=datosUsuario.getString("refresh_token", "");
    }

    private void ocultarBarraDeProgreso() {
        //Ocultar la barra de progreso
        progressBar.setVisibility(View.GONE);
    }

    private String crearCredencialesCliente() {
        //Base64.NO_WRAP sirve como indicador del codificador para omitir todos los terminadores de línea (Se hace así en Android)
        return "Basic "+ Base64.encodeToString(Constantes.CREDENCIALES_APLICACION.getBytes(), Base64.NO_WRAP);
    }

//    private void mantenerSesionIniciada() {
//        obtenerDatos();
//        if(nombreDeUsuarioPrefComp.equals("")) {
//            return;
//        }
//        if((expiresAt-new Date().getTime())<0) {
//            return;
//        }
//        new IntentarMantenerSesionIniciada().execute();
//    }
//
//    private class IntentarMantenerSesionIniciada extends AsyncTask<Void, Void, Integer> {
//
//        @Override
//        protected Integer doInBackground(Void... voids) {
//            PeticionInicioSesion peticion=new PeticionInicioSesion(nombreDeUsuarioPrefComp, contrasenaPrefComp);
//            int respuesta=intentarIniciarSesion(token, peticion);
//            return respuesta;
//        }
//
//        @Override
//        protected void onPostExecute(Integer respuesta) {
//            super.onPostExecute(respuesta);
//            if(respuesta==Constantes.OK) {
//                //Ir a la siguiente Activity
//                Intent irABuscar=new Intent(getApplicationContext(), ActivityBuscar.class);
//                startActivity(irABuscar);
//                //Terminar esta Activity
//                finish();
//            }
//        }
//    }
 }