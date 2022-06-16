package com.example.comicground.activities;

import static com.example.comicground.api.ClienteAPI.retrofit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.comicground.R;
import com.example.comicground.api.endpoints.UsuarioEndpoints;
import com.example.comicground.utils.AESEncriptacion;
import com.example.comicground.models.Usuario;
import com.example.comicground.utils.Constantes;
import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;
import java.net.HttpURLConnection;

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
    AppCompatButton btnSalir;

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
        getSupportActionBar().hide();
        //Asignar vistas a los objetos y algunas características
        encontrarVistasPorId();
    }

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
                    //Mostrar barra cargando
                    progressBar.setVisibility(View.VISIBLE);
                    //Ejecutar tarea asíncrona para registrar
                    new Registrar().execute();
                }
                break;
            case R.id.btnSalir:
                //Terminar la Activity
                finish();
        }
    }

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

        if(nombre.equals("")) {
            etNombre.setError("Escriba su nombre");
            return false;
        }
        return true;
    }

    private boolean comprobarApellidos() {

        if(apellidos.equals("")) {
            etApellidos.setError("Escriba sus apellidos");
            return false;
        }
        return true;
    }

    private boolean comprobarCorreo() {
        if(!comprobarFormatoCorreo(correo, true)) {
            return false;
        }
        return true;
    }

    private boolean comprobarConfirmacionCorreo() {

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
                etConfirmarCorreo.setError("Formato del correo no válido");
            }
            return false;
        }
        return true;
    }

    private boolean comprobarNombreDeUsuario() {
        if(nombreDeUsuario.equals("")) {
            etNombreDeUsuario.setError("Escriba su nombre de usuario");
            return false;
        }
        if(nombreDeUsuario.length()>20) {
            etNombreDeUsuario.setError("El nombre de usuario no puede ser de más de 20 carácteres");
            return false;
        }
        return true;
    }

    private boolean comprobarContrasena() {
        if(contrasena.equals("")) {
            etContrasena.setError("Escriba su contraseña");
            return false;
        }

        if (contrasena.length()<8) {
            etContrasena.setError("La contraseña debe tener un mínimo de 8 carácteres");
            return false;
        }

        //Regex para contraseña y con un error distinto por cada uno
        if(!contrasena.matches("(.*[A-Z].*)")) {
            etContrasena.setError("La contraseña debe contener al menos una letra mayúscula");
            return false;
        }
        if(!contrasena.matches("(.*[a-z].*)")) {
            etContrasena.setError("La contraseña debe contener al menos una letra minúscula");
            return false;
        }
        if(!contrasena.matches("(.*[0-9].*)")) {
            etContrasena.setError("La contraseña debe contener al menos un dígito");
            return false;
        }
        return true;
    }

    private class Registrar extends AsyncTask<Void, Void, Integer> {

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
            ocultarBarraDeProgreso();
            //Mostrar en UI según respuesta
            switch(respuesta) {
                case Constantes.OK:
                    crearToast("Se ha registrado correctamente");
                    //Terminar la Activity
                    finish();
                    break;
                case Constantes.ERROR_SERVIDOR:
                    crearToast("Error al conectar con el servidor");
                    break;
                case Constantes.ERROR_NOMBRE_DE_USUARIO_EXISTENTE:
                    etNombreDeUsuario.setError("Ese nombre de usuario ya está en uso");
                    break;
                case Constantes.ERROR_CORREO_EXISTENTE:
                    etCorreo.setError("Ese correo electrónico ya está en uso");
                    break;
                case Constantes.ERROR_GENERICO:
                    crearToast("Error al registrarse");

            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            //Ocultar la barra de progreso
            ocultarBarraDeProgreso();
            //Mostrar el mensaje de error
            Toast.makeText(ActivityRegistro.this, "Error al intentar registrarse, el proceso ha sido cancelado", Toast.LENGTH_SHORT).show();
        }
    }

    private int intentarRegistro(Usuario usuario) {
        int respuesta;

        //Crear cabecera con credenciales del cliente (mi aplicación)
        String credencialesCliente=crearCredencialesCliente();

        //Crear objeto de endpoints necesario con retrofit
        UsuarioEndpoints usuarioEndpoints=retrofit.create(UsuarioEndpoints.class);

        //Realizar llamada al endpoint y mandar los datos necesarios al servidor
        try {
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
        nombre=etNombre.getEditText().getText().toString();
        apellidos=etApellidos.getEditText().getText().toString();
        correo=etCorreo.getEditText().getText().toString();
        confirmarCorreo=etConfirmarCorreo.getEditText().getText().toString();
        nombreDeUsuario=etNombreDeUsuario.getEditText().getText().toString();
        contrasena=etContrasena.getEditText().getText().toString();
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
        btnSalir=findViewById(R.id.btnSalir);
        //Asignar el OnClickListener a los botones
        btnRegistro.setOnClickListener(this);
        btnSalir.setOnClickListener(this);

        //Para que no salga el icono de error en TextInput de la contraseña (ya que inhabilitaría el de mostrar/ocultar contraseña)
        etContrasena.setErrorIconDrawable(null);
    }

    private void ocultarBarraDeProgreso() {
        //Ocultar la barra de progreso
        progressBar.setVisibility(View.GONE);
    }

    private String crearCredencialesCliente() {
        //Base64.NO_WRAP sirve como indicador del codificador para omitir todos los terminadores de línea (Se hace así en Android)
        return "Basic "+ Base64.encodeToString(Constantes.CREDENCIALES_APLICACION.getBytes(), Base64.NO_WRAP);
    }
}