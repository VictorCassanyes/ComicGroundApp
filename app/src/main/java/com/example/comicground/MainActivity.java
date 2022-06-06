package com.example.comicground;

import static com.example.comicground.api.ClienteAPI.retrofit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import com.example.comicground.api.Constantes;
import com.example.comicground.api.endpoints.UsuarioEndpoints;
import com.example.comicground.api.peticiones.PeticionInicioSesion;
import com.example.comicground.api.respuestas.RespuestaInicioSesion;
import com.example.comicground.models.Usuario;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.HTTP;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    ConstraintLayout constraintLayout;
    TextInputLayout etNombreDeUsuario;
    TextInputLayout etContraseña;
    AppCompatButton btnIniciarSesion;
    AppCompatButton btnRegistro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        encontrarVistasPorId();

        constraintLayout = (ConstraintLayout) findViewById(R.id.main_layout);
        constraintLayout.setOnClickListener(null);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.btnIniciarSesion:
                //iniciar sesion
                limpiarErrores();
                if(comprobarCampos()) {
                    String[] credenciales={etNombreDeUsuario.getEditText().getText().toString(), etContraseña.getEditText().getText().toString()};
                    new InicioSesion().execute(credenciales);
                    Intent iniciarSesion=new Intent(getApplicationContext(), ActivitySearch.class);
                    startActivity(iniciarSesion);
                }
                break;
            case R.id.btnRegistro:
                Intent registro=new Intent(getApplicationContext(), ActivitySignup.class);
                startActivity(registro);
        }
    }

    private boolean comprobarCampos() {
        boolean validado=true;

        if(!comprobarNombreDeUsuario()) {
            validado=false;
        }
        if(!comprobarContraseña()) {
            validado=false;
        }
        return validado;
    }

    private boolean comprobarNombreDeUsuario() {
        String nombreDeUsuario=etNombreDeUsuario.getEditText().getText().toString();
        if(nombreDeUsuario.equals("")) {
            etNombreDeUsuario.setError("Escriba su nombre de usuario");
            return false;
        }
        if(nombreDeUsuario.length()<2) {
            etNombreDeUsuario.setError("El nombre de usuario debe tener como mínimo 2 carácteres");
            return false;
        }
        return true;
    }

    private boolean comprobarContraseña() {
        String contraseña=etContraseña.getEditText().getText().toString();
        if(contraseña.equals("")) {
            etContraseña.setError("Escriba su contraseña");
            return false;
        }
        if (contraseña.length()<8) {
            etContraseña.setError("La contraseña debe tener un mínimo de 8 carácteres");
            return false;
        }
        if(!contraseña.matches("(.*[A-Z].*)")) {
            etContraseña.setError("La contraseña debe contener al menos una letra mayúscula");
            return false;
        }
        if(!contraseña.matches("(.*[a-z].*)")) {
            etContraseña.setError("La contraseña debe contener al menos una letra minúscula");
            return false;
        }
        if(!contraseña.matches("(.*[0-9].*)")) {
            etContraseña.setError("La contraseña debe contener al menos un dígito");
            return false;
        }
        return true;
    }

    private void limpiarErrores() {
        etNombreDeUsuario.setError(null);
        etContraseña.setError(null);
    }

    private void encontrarVistasPorId() {
        etNombreDeUsuario=findViewById(R.id.etNombreDeUsuario);
        etContraseña=findViewById(R.id.etContraseña);
        etContraseña.setErrorIconDrawable(null);

        btnIniciarSesion=findViewById(R.id.btnIniciarSesion);
        btnIniciarSesion.setOnClickListener(this);
        btnRegistro=findViewById(R.id.btnRegistro);
        btnRegistro.setOnClickListener(this);
    }

    //Pruebas OAuth2
   /* public void  saveData(){

        SharedPreferences.Editor sharedPref = getSharedPreferences("authInfo", Context.MODE_PRIVATE).edit();
        sharedPref.putString("AuthCode", AUTHORIZATION_CODE);
        sharedPref.putString("secCode", Authcode);
        sharedPref.putString("refresh", Refreshtoken);
        sharedPref.putLong("expiry", ExpiryTime);
        sharedPref.apply();
    }

    public void loadData(){
        SharedPreferences sharedPref = getSharedPreferences("authInfo",Context.MODE_PRIVATE);
        AUTHORIZATION_CODE = sharedPref.getString("AuthCode", "");
        Authcode = sharedPref.getString("secCode", "");
        Refreshtoken = sharedPref.getString("refresh","");
        ExpiryTime = sharedPref.getLong("expiry",0);
    }

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

                // Toast.makeText(MainActivity.this, "The new Expiry time is: " + ExpiryTime + " and System time is " + System.currentTimeMillis(), Toast.LENGTH_LONG).show();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }*/

    public class InicioSesion extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... strings) {
            PeticionInicioSesion peticion=new PeticionInicioSesion(strings[0], strings[1]);

            UsuarioEndpoints usuarioEndpoints=retrofit.create(UsuarioEndpoints.class);
                                            //Base64.NO_WRAP es un bit indicador del codificador para omitir todos los terminadores de línea (Se hace con esto en Android)

            String cabeceraAuth="Basic "+Base64.encodeToString(Constantes.CREDENCIALES_APLICACION.getBytes(), Base64.NO_WRAP);

            Call<RespuestaInicioSesion> call=usuarioEndpoints.iniciarSesion(cabeceraAuth, peticion);
            call.enqueue(new Callback<RespuestaInicioSesion>() {
                @Override
                public void onResponse(Call<RespuestaInicioSesion> call, Response<RespuestaInicioSesion> response) {
                    if (response.isSuccessful()) {
                    //Toast.makeText(MainActivity.this, response.body().getoAuthToken(), Toast.LENGTH_SHORT).show();
                    //String token=response.body().getoAuthToken();
                    } else {
                        Toast.makeText(MainActivity.this, "Las credenciales no son correctas", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<RespuestaInicioSesion> call, Throwable t) {
                    Toast.makeText(MainActivity.this, "Error al iniciar sesión", Toast.LENGTH_SHORT).show();
                }
            });
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }


}