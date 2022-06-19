package com.example.comicground.dialogs;

import static com.example.comicground.api.ClienteAPI.usuarioEndpoints;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;

import com.google.android.material.textfield.TextInputLayout;

import com.example.comicground.utils.Constantes;
import com.example.comicground.R;
import com.example.comicground.models.Usuario;

import java.io.IOException;
import java.net.HttpURLConnection;

import retrofit2.Call;
import retrofit2.Response;

public class DialogoPerfil {

    TextInputLayout etNombre;
    TextInputLayout etApellidos;
    TextInputLayout etNombreDeUsuario;
    TextInputLayout etCorreo;
    AppCompatButton btnActualizar;
    AppCompatButton btnAtras;

    //Diálogo ya creado
    AlertDialog dialogoPerfil;

    //Datos pasados constructor
    Context contexto;
    Usuario usuario;
    String token;
    ProgressBar progressBar;

    public DialogoPerfil(Context contexto, Usuario usuario, String token) {
        this.contexto=contexto;
        this.usuario=usuario;
        this.token=token;
    }

    //Crear diálogo perfil
    public AlertDialog crearDialogoPerfil() {
        //Crear el Builder
        AlertDialog.Builder builder=new AlertDialog.Builder(contexto);

        //Crear el LayoutInflater
        LayoutInflater layoutInflater=((Activity)contexto).getLayoutInflater();
        //Crear el View con el diálogo personalizado
        View view=layoutInflater.inflate(R.layout.dialog_perfil, null);

        //Asignarle el View anterior al Builder creado
        builder.setView(view);
        //Hacer que no pueda cancelarse
        builder.setCancelable(false);
        
        //Encontrar las vistas por id
        encontrarVistasPorId(view);
        //Asignar textos a los EditText
        asignarTextos(usuario);

        //Crear el diálogo para poder hacer dismiss() aquí en uno de los onClickListener
        dialogoPerfil=builder.create();

        //Asignar onClickListener a ambos botones
        btnActualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //actualizar
                new ActualizarUsuario().execute();
            }
        });
        btnAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Salir del diálogo
                dialogoPerfil.dismiss();
            }
        });

        return dialogoPerfil;
    }

    /*
     * Tarea asíncrona para actualizar usuario
     */
    public class ActualizarUsuario extends AsyncTask<Void, Void, Integer> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Mostrar barra progreso
            progressBar.setVisibility(View.VISIBLE);
            //Limpiar posible error en nombre de usuario
            etNombreDeUsuario.setError(null);
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            int respuesta;
            //Crear objeto usuario a mandar
            Usuario usuarioNuevo=usuario;
            //Asignar los datos de los EditText al usuario
            asignarDatosUsuarioNuevo(usuarioNuevo);
            try {
                //Realizar llamada para actualizar usuario
                Call<Usuario> call=usuarioEndpoints.editarUsuario(token, usuarioNuevo);
                Response<Usuario> response=call.execute();
                if (response.isSuccessful()) {
                    respuesta=Constantes.OK;
                    //Actualizar objeto usuario
                    usuario=response.body();
                } else if (response.code()==HttpURLConnection.HTTP_NOT_MODIFIED) {
                    respuesta=Constantes.ERROR_NO_MODIFICADO;
                } else if(response.code()==HttpURLConnection.HTTP_NOT_ACCEPTABLE) {
                    respuesta=Constantes.ERROR_NOMBRE_DE_USUARIO_EXISTENTE;
                } else {
                    respuesta=Constantes.ERROR_GENERICO;
                }
            } catch (IOException e) {
                respuesta=Constantes.ERROR_SERVIDOR;
            }
            return respuesta;
        }

        @Override
        protected void onPostExecute(Integer respuesta) {
            super.onPostExecute(respuesta);
            //Ocultar barra progreso
            progressBar.setVisibility(View.GONE);
            //Switch con distintas acciones según la respuesta
            switch (respuesta) {
                case Constantes.OK:
                    crearToast(contexto.getResources().getString(R.string.modified));
                    //Guardar nuevo nombre de usuario en las preferencias compartidas
                    guardarNombreDeUsuario();
                    //Cerrar el diálogo
                    dialogoPerfil.dismiss();
                    break;
                case Constantes.ERROR_NOMBRE_DE_USUARIO_EXISTENTE:
                    etNombreDeUsuario.setError(contexto.getResources().getString(R.string.usernameExists));
                    break;
                case Constantes.ERROR_NO_MODIFICADO:
                    crearToast(contexto.getResources().getString(R.string.notModified));
                    break;
                case Constantes.ERROR_SERVIDOR:
                    crearToast(contexto.getResources().getString(R.string.errorServer));
                    break;
                case Constantes.ERROR_GENERICO:
                    crearToast(contexto.getResources().getString(R.string.error));
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            //Ocultar barra progreso
            progressBar.setVisibility(View.GONE);
            crearToast(contexto.getResources().getString(R.string.cancelled));
        }
    }


    /*
     * Útiles
     */

    //Encontrar las vistas
    public void encontrarVistasPorId(View view) {
        progressBar=view.findViewById(R.id.progressBar);
        etNombre=view.findViewById(R.id.etNombre);
        etApellidos=view.findViewById(R.id.etApellidos);
        etNombreDeUsuario=view.findViewById(R.id.etNombreDeUsuario);
        etCorreo=view.findViewById(R.id.etCorreo);
        btnActualizar=view.findViewById(R.id.btnActualizar);
        btnAtras=view.findViewById(R.id.btnAtras);

        //Para que no pueda editar el correo
        etCorreo.setEnabled(false);
    }

    //Asignar los textos
    public void asignarTextos(Usuario usuario) {
        etNombre.getEditText().setText(usuario.getNombre());
        etApellidos.getEditText().setText(usuario.getApellidos());
        etNombreDeUsuario.getEditText().setText(usuario.getNombreDeUsuario());
        etCorreo.getEditText().setText(usuario.getCorreo());
    }

    //Asignar los datos de los EditText al usuario
    public void asignarDatosUsuarioNuevo(Usuario usuarioNuevo) {
        usuarioNuevo.setNombre(etNombre.getEditText().getText().toString());
        usuarioNuevo.setApellidos(etApellidos.getEditText().getText().toString());
        usuarioNuevo.setNombreDeUsuario(etNombreDeUsuario.getEditText().getText().toString());
    }

    //Actualizar nombre de usuario en preferencias compartidas
    public void guardarNombreDeUsuario() {
        //Se actualizan las preferencias compartidas, solo la variable nombre_de_usuario
        SharedPreferences.Editor datosUsuario=contexto.getSharedPreferences(Constantes.PREFERENCIAS_COMPARTIDAS, Context.MODE_PRIVATE).edit();
        datosUsuario.putString(Constantes.NOMBRE_DE_USUARIO, usuario.getNombreDeUsuario());
        datosUsuario.apply();
    }

    public void crearToast(String mensaje) {
        Toast.makeText(contexto, mensaje, Toast.LENGTH_SHORT).show();
    }
}
