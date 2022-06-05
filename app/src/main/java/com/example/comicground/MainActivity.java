package com.example.comicground;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

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
        if(!contraseña.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,}$")) {
            etContraseña.setError("La contraseña debe tener una longitud mínima de 8 carácteres, contener un dígito, una mayúscula y una minúscula. No debe tener ningún espacio en blanco");
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
}