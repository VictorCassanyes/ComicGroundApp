package com.example.comicground;

import static com.example.comicground.api.ClienteAPI.retrofit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import com.example.comicground.api.Constantes;
import com.example.comicground.api.endpoints.UsuarioEndpoints;
import com.example.comicground.api.seguridad.AESEncriptacion;
import com.example.comicground.models.Usuario;
import com.google.android.material.textfield.TextInputLayout;

import java.net.HttpURLConnection;
import java.util.Date;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivitySignup extends AppCompatActivity implements View.OnClickListener {

    TextInputLayout etNombre;
    TextInputLayout etApellidos;
    TextInputLayout etCorreo;
    TextInputLayout etConfirmarCorreo;
    TextInputLayout etNombreDeUsuario;
    TextInputLayout etContraseña;
    AppCompatButton btnRegistro;
    AppCompatButton btnSalir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        getSupportActionBar().hide();

        encontrarVistasPorId();
        //Cargar portadas cómics
        //ImageView imageView= findViewById(R.id.logo);
        //Picasso.get().load("https://comicvine.gamespot.com/a/uploads/scale_medium/0/4/49914-2293-64952-1-marvel-tales.jpg").into(imageView);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.btnRegistro:
                limpiarErrores();
                String[] variables=camposAVariables();
                if(comprobarvariables(variables[0], variables[1], variables[2], variables[3], variables[4], variables[5])) {
                    new intentarRegistro().execute(variables);
                }
                break;
            case R.id.btnSalir:
                finish();
        }
    }

    private boolean comprobarvariables(String nombre, String apellidos, String correo, String confirmarCorreo, String nombreDeUsuario, String contraseña) {
        boolean validado=true;

        if(!comprobarNombre(nombre)) {
            validado=false;
        }
        if(!comprobarApellidos(apellidos)) {
             validado=false;
        }
        if(!comprobarCorreo(correo)) {
            validado=false;
        }
        if(!comprobarConfirmacionCorreo(correo, confirmarCorreo)) {
            validado=false;
        }
        if(!comprobarNombreDeUsuario(nombreDeUsuario)) {
            validado=false;
        }
        if(!comprobarContraseña(contraseña)) {
            validado=false;
        }
        return validado;
    }

    private boolean comprobarNombre(String nombre) {

        if(nombre.equals("")) {
            etNombre.setError("Escriba su nombre");
            return false;
        }
        if(nombre.length()<2) {
            etNombre.setError("El nombre debe tener como mínimo 2 carácteres");
            return false;
        }
        return true;
    }

    private boolean comprobarApellidos(String apellidos) {

        if(apellidos.equals("")) {
            etApellidos.setError("Escriba sus apellidos");
            return false;
        }
        if(apellidos.length()<2) {
            etApellidos.setError("Los apellidos deben tener como mínimo 2 carácteres");
            return false;
        }
        return true;
    }

    private boolean comprobarCorreo(String correo) {
        if(!comprobarFormatoCorreo(correo, true)) {
            return false;
        }
        return true;
    }

    private boolean comprobarConfirmacionCorreo(String correo, String confirmarCorreo) {

        if(!comprobarFormatoCorreo(confirmarCorreo, false)) {
            return false;
        }
        if(!correo.equals(confirmarCorreo)) {
            etConfirmarCorreo.setError("La confirmación no coincide con su correo");
            return false;
        }
        return true;
    }

    private boolean comprobarFormatoCorreo(String correo, boolean original) {

        if (correo.equals("")) {
            if(original) {
                etCorreo.setError("Escriba su correo");
            } else {
                etConfirmarCorreo.setError("Escriba la confirmación del correo");
            }
            return false;
        }
        //Regex para correo
        if (!correo.matches("^[a-zA-Z0-9_!#$%&'\\*+/=?{|}~^.-]+@[a-zA-Z0-9.-]+$")) {
            if(original) {
                etCorreo.setError("Formato del correo no válido");
            } else {
                etConfirmarCorreo.setError("Formato de correo no válido");
            }
            return false;
        }
        return true;
    }

    private boolean comprobarNombreDeUsuario(String nombreDeUsuario) {
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

    private boolean comprobarContraseña(String contraseña) {
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

    private class intentarRegistro extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String... strings) {

            //Encriptar la contraseña (AESEncriptor)
            String contraseñaEncriptada=AESEncriptacion.encriptar(strings[5]);

                                        //correo, nombreDeUsuario, nombre, apellidos y contraseña
            Usuario usuario=new Usuario(strings[2], strings[4], strings[0], strings[1], contraseñaEncriptada);

            UsuarioEndpoints usuarioEndpoints=retrofit.create(UsuarioEndpoints.class);

            //Crear cabecera con credenciales de la aplicación
            String cabeceraAuth=crearCabeceraAuth();

            Call<ResponseBody> call=usuarioEndpoints.registrar(cabeceraAuth, usuario);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(ActivitySignup.this, "Se ha registrado correctamente", Toast.LENGTH_SHORT).show();

                        //Generar OAuthToken

                    } else if(response.code()==HttpURLConnection.HTTP_NOT_ACCEPTABLE) {
                        Toast.makeText(ActivitySignup.this, "Cambie su nombre de usuario", Toast.LENGTH_SHORT).show();
                        etNombreDeUsuario.setError("Ese nombre de usuario ya existe");
                    } else if(response.code()==HttpURLConnection.HTTP_CONFLICT) {
                        Toast.makeText(ActivitySignup.this, "Utilice un correo electrónico distinto", Toast.LENGTH_SHORT).show();
                        etCorreo.setError("Ese correo electrónico ya está en uso");
                    } else {
                        Toast.makeText(ActivitySignup.this, "Error al registrarse", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(ActivitySignup.this, "Error al conectar con el servidor", Toast.LENGTH_SHORT).show();
                }
            });

            //return OAuthToken

            return null;
        }

        @Override
        protected void onPostExecute(String s) {

            //Guardar OAuthToken en SharedPreferences

            super.onPostExecute(s);
            finish();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            Toast.makeText(ActivitySignup.this, "Error al intentar registrarse, el proceso ha sido cancelado", Toast.LENGTH_SHORT).show();
        }
    }

    private String crearCabeceraAuth() {
        //Base64.NO_WRAP sirve como indicador del codificador para omitir todos los terminadores de línea (Se hace así en Android)
        return "Basic "+ Base64.encodeToString(Constantes.CREDENCIALES_APLICACION.getBytes(), Base64.NO_WRAP);
    }

    private void limpiarErrores() {
        etNombre.setError(null);
        etApellidos.setError(null);
        etCorreo.setError(null);
        etConfirmarCorreo.setError(null);
        etNombreDeUsuario.setError(null);
        etContraseña.setError(null);
    }

    private String[] camposAVariables() {
        String nombre=etNombre.getEditText().getText().toString();
        String apellidos=etApellidos.getEditText().getText().toString();
        String correo=etCorreo.getEditText().getText().toString();
        String confirmarCorreo=etConfirmarCorreo.getEditText().getText().toString();
        String nombreDeUsuario=etNombreDeUsuario.getEditText().getText().toString();
        String contraseña=etContraseña.getEditText().getText().toString();

        String[] variables={nombre, apellidos, correo, confirmarCorreo, nombreDeUsuario, contraseña};
        return variables;
    }

    private void encontrarVistasPorId() {
        etNombre=findViewById(R.id.etNombre);
        etApellidos=findViewById(R.id.etApellidos);
        etCorreo=findViewById(R.id.etCorreo);
        etConfirmarCorreo=findViewById(R.id.etConfirmarCorreo);
        etNombreDeUsuario=findViewById(R.id.etNombreDeUsuario);
        etContraseña=findViewById(R.id.etContraseña);
        etContraseña.setErrorIconDrawable(null);

        btnRegistro=findViewById(R.id.btnRegistro);
        btnRegistro.setOnClickListener(this);
        btnSalir=findViewById(R.id.btnSalir);
        btnSalir.setOnClickListener(this);
    }
}