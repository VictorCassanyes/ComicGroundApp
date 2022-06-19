package com.example.comicground.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.comicground.R;
import com.example.comicground.activities.ActivityInicio;
import com.example.comicground.utils.Constantes;

public class DialogoSalir {

    public static AlertDialog crearDialogoSalir(Context contexto) {
        AlertDialog.Builder builder=new AlertDialog.Builder(contexto);
        builder.setTitle(R.string.app_name)
                .setMessage(R.string.dialogExitMessage)
                .setNegativeButton(R.string.dialogExitNegative, (dialogInterface, i) -> dialogInterface.dismiss())
                .setPositiveButton(R.string.dialogExitPositive, (dialogInterface, i) -> {
                    //Que se cierre la sesión para que no vuelva a entrar
                    SharedPreferences.Editor datosUsuario=contexto.getSharedPreferences(Constantes.PREFERENCIAS_COMPARTIDAS, Context.MODE_PRIVATE).edit();
                    datosUsuario.putBoolean(Constantes.MANTENER_SESION_INICIADA, false);
                    datosUsuario.apply();
                    //Cerrar la sesión
                    Intent irAInicio=new Intent(contexto, ActivityInicio.class);
                    irAInicio.putExtra(Constantes.CERRAR_SESION, true);
                    //Flags para borrar las Activities anteriores también
                    irAInicio.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    contexto.startActivity(irAInicio);
                });
        return builder.create();
    }
}
