package com.example.comicground.activities;

import static com.example.comicground.api.ClienteAPI.usuarioEndpoints;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.comicground.R;
import com.example.comicground.utils.AESEncriptacion;
import com.example.comicground.models.Usuario;
import com.example.comicground.utils.Constantes;
import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class ActivityRegistro extends AppCompatActivity implements View.OnClickListener {

    //Vistas
    ProgressBar progressBar;
    TextInputLayout etNombre;
    TextInputLayout etApellidos;
    TextInputLayout etCorreo;
    TextInputLayout etConfirmarCorreo;
    TextInputLayout etNombreDeUsuario;
    TextInputLayout etContrasena;
    AppCompatButton btnRegistro;
    AppCompatButton btnAtras;

    //Variables de vistas
    String nombre;
    String apellidos;
    String correo;
    String confirmarCorreo;
    String nombreDeUsuario;
    String contrasena;
    String contrasenaEncriptada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        //Ocultar barra superior
        Objects.requireNonNull(getSupportActionBar()).hide();
        //Asignar vistas a los objetos y algunas características
        encontrarVistasPorId();
    }

    /*
     * OnClickListener de Activity
     */
    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.btnRegistro:
                //Quitar los errores
                limpiarErrores();
                //Actualizar las variables con los datos de los EditText
                camposAVariables();
                //Si está correcto iniciar proceso de registro
                if(comprobarVariables()) {
                    //Ejecutar tarea asíncrona para registrar
                    new Registrar().execute();
                }
                break;
            case R.id.btnAtras:
                //Terminar la Activity
                finish();
        }
    }


    /*
     * Comprobaciones
     */

    private boolean comprobarVariables() {
        boolean validado=true;

        if(!comprobarNombre()) {
            validado=false;
        }
        if(!comprobarApellidos()) {
             validado=false;
        }
        if(!comprobarCorreo()) {
            validado=false;
        }
        if(!comprobarConfirmacionCorreo()) {
            validado=false;
        }
        if(!comprobarNombreDeUsuario()) {
            validado=false;
        }
        if(!comprobarContrasena()) {
            validado=false;
        }
        return validado;
    }

    private boolean comprobarNombre() {

        if(nombre.equals(Constantes.VACIO)) {
            etNombre.setError(getResources().getString(R.string.writeName));
            return false;
        }
        return true;
    }

    private boolean comprobarApellidos() {

        if(apellidos.equals(Constantes.VACIO)) {
            etApellidos.setError(getResources().getString(R.string.writeLastname));
            return false;
        }
        return true;
    }

    private boolean comprobarCorreo() {
        return comprobarFormatoCorreo(correo, true);
    }

    private boolean comprobarConfirmacionCorreo() {

        if(!comprobarFormatoCorreo(confirmarCorreo, false)) {
            return false;
        }
        if(!correo.equals(confirmarCorreo)) {
            etConfirmarCorreo.setError(getResources().getString(R.string.emailConfirmNotSameAsEmail));
            return false;
        }
        return true;
    }

    private boolean comprobarFormatoCorreo(String correo, boolean original) {

        if (correo.equals(Constantes.VACIO)) {
            if(original) {
                etCorreo.setError(getResources().getString(R.string.writeEmail));
            } else {
                etConfirmarCorreo.setError(getResources().getString(R.string.writeEmailConfirm));
            }
            return false;
        }
        //Regex para correo
        if (!correo.matches(Constantes.REGEX_CORREO)) {
            if(original) {
                etCorreo.setError(getResources().getString(R.string.emailWrong));
            } else {
                etConfirmarCorreo.setError(getResources().getString(R.string.emailWrong));
            }
            return false;
        }
        return true;
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

        //Regex para contraseña y con un error distinto por cada uno
        if(!contrasena.matches(Constantes.REGEX_CONTIENE_MAYUSCULA)) {
            etContrasena.setError(getResources().getString(R.string.passNoMayus));
            return false;
        }
        if(!contrasena.matches(Constantes.REGEX_CONTIENE_MINUSCULA)) {
            etContrasena.setError(getResources().getString(R.string.passNoMinus));
            return false;
        }
        if(!contrasena.matches(Constantes.REGEX_CONTIENE_NUMERO)) {
            etContrasena.setError(getResources().getString(R.string.passNoNumber));
            return false;
        }
        return true;
    }


    /*
     * Tarea asíncrona para el registro
     */

    @SuppressLint("StaticFieldLeak")
    private class Registrar extends AsyncTask<Void, Void, Integer> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Mostrar barra cargando
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            //Encriptar la contraseña (AES)
            contrasenaEncriptada=AESEncriptacion.encriptar(contrasena);
            //Crear objeto Usuario
            Usuario usuario=new Usuario(correo, nombreDeUsuario, nombre, apellidos, contrasenaEncriptada);
            //Realizar llamada a la API y devolver un int como respuesta
            return intentarRegistro(usuario);
        }

        @Override
        protected void onPostExecute(Integer respuesta) {
            super.onPostExecute(respuesta);
            //Ocultar la barra de progreso
            progressBar.setVisibility(View.GONE);
            //Mostrar en UI según respuesta
            switch(respuesta) {
                case Constantes.OK:
                    crearToast(getResources().getString(R.string.signedUp));
                    //Terminar la Activity
                    finish();
                    break;
                case Constantes.ERROR_NOMBRE_DE_USUARIO_EXISTENTE:
                    etNombreDeUsuario.setError(getResources().getString(R.string.usernameExists));
                    break;
                case Constantes.ERROR_CORREO_EXISTENTE:
                    etCorreo.setError(getResources().getString(R.string.emailExists));
                    break;
                case Constantes.ERROR_SERVIDOR:
                    crearToast(getResources().getString(R.string.errorServer));
                    break;
                default:
                    crearToast(getResources().getString(R.string.error));

            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            //Ocultar la barra de progreso
            progressBar.setVisibility(View.GONE);
            //Mostrar el mensaje de error
            Toast.makeText(ActivityRegistro.this, getResources().getString(R.string.cancelled), Toast.LENGTH_SHORT).show();
        }
    }

    //Llamada a la API realizada en la tarea asíncrona
    private int intentarRegistro(Usuario usuario) {
        int respuesta;
        //Crear cabecera con credenciales del cliente (mi aplicación)
        String credencialesCliente=crearCredencialesCliente();
        try {
            //Realizar llamada para registrar usuario
            Call<ResponseBody> call=usuarioEndpoints.registrar(credencialesCliente, usuario);
            Response<ResponseBody> response=call.execute();
            if(response.isSuccessful()) {
                respuesta=Constantes.OK;
            //Obtiene código distinto según el error ocurrido
            } else if(response.code()==HttpURLConnection.HTTP_NOT_ACCEPTABLE) {
                respuesta=Constantes.ERROR_NOMBRE_DE_USUARIO_EXISTENTE;
            } else if(response.code()==HttpURLConnection.HTTP_CONFLICT) {
                respuesta=Constantes.ERROR_CORREO_EXISTENTE;
            } else {
                //Si no es ninguno de los anteriores, error por defecto
                respuesta=Constantes.ERROR_GENERICO;
            }
        } catch (IOException e) {
            respuesta=Constantes.ERROR_SERVIDOR;
        }
        return respuesta;
    }


    /*
     *  Útiles
     */

    private void crearToast(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
    }

    private void limpiarErrores() {
        //Settear errores a null para quitarlos todos
        etNombre.setError(null);
        etApellidos.setError(null);
        etCorreo.setError(null);
        etConfirmarCorreo.setError(null);
        etNombreDeUsuario.setError(null);
        etContrasena.setError(null);
    }

    private void camposAVariables() {
        //Pasar los textos de los inputText a variables para mayor facilidad a la hora de manejar los datos
        nombre= Objects.requireNonNull(etNombre.getEditText()).getText().toString();
        apellidos= Objects.requireNonNull(etApellidos.getEditText()).getText().toString();
        correo= Objects.requireNonNull(etCorreo.getEditText()).getText().toString();
        confirmarCorreo= Objects.requireNonNull(etConfirmarCorreo.getEditText()).getText().toString();
        nombreDeUsuario= Objects.requireNonNull(etNombreDeUsuario.getEditText()).getText().toString();
        contrasena= Objects.requireNonNull(etContrasena.getEditText()).getText().toString();
    }

    private void encontrarVistasPorId() {
        //Encontrar las vistas de la interfaz gráfica y pasarlas a sus objetos
        progressBar=findViewById(R.id.progressBar);
        etNombre=findViewById(R.id.etNombre);
        etApellidos=findViewById(R.id.etApellidos);
        etCorreo=findViewById(R.id.etCorreo);
        etConfirmarCorreo=findViewById(R.id.etConfirmarCorreo);
        etNombreDeUsuario=findViewById(R.id.etNombreDeUsuario);
        etContrasena=findViewById(R.id.etContrasena);
        btnRegistro=findViewById(R.id.btnRegistro);
        btnAtras=findViewById(R.id.btnAtras);

        //Asignar el OnClickListener a los botones
        btnRegistro.setOnClickListener(this);
        btnAtras.setOnClickListener(this);

        //Para que no salga el icono de error en TextInput de la contraseña (ya que inhabilitaría el de mostrar/ocultar contraseña)
        etContrasena.setErrorIconDrawable(null);
    }

    private String crearCredencialesCliente() {
        //Base64.NO_WRAP sirve como indicador del codificador para omitir todos los terminadores de línea (Se hace así en Android)
        return Constantes.TIPO_AUTH+Base64.encodeToString(Constantes.CREDENCIALES_APLICACION.getBytes(), Base64.NO_WRAP);
    }
}