package com.example.comicground;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.squareup.picasso.Picasso;

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
                if(comprobarCampos()) {
                    Toast.makeText(this, "Campos validados correctamente", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btnSalir:
                finish();
        }
    }

    private boolean comprobarCampos() {
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
        if(!comprobarContraseña()) {
            validado=false;
        }
        return validado;
    }

    private boolean comprobarNombre() {
        String nombre=etNombre.getEditText().getText().toString();
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

    private boolean comprobarApellidos() {
        String apellidos=etApellidos.getEditText().getText().toString();
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

    private boolean comprobarCorreo() {
        String correo=etCorreo.getEditText().getText().toString();
        if(!comprobarFormatoCorreo(correo, true)) {
            return false;
        }
        return true;
    }

    private boolean comprobarConfirmacionCorreo() {
        String correo=etCorreo.getEditText().getText().toString();
        String confirmarCorreo=etConfirmarCorreo.getEditText().getText().toString();
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
        etNombre.setError(null);
        etApellidos.setError(null);
        etCorreo.setError(null);
        etConfirmarCorreo.setError(null);
        etNombreDeUsuario.setError(null);
        etContraseña.setError(null);
    }

    private void encontrarVistasPorId() {
        etNombre=findViewById(R.id.etNombre);
        etApellidos=findViewById(R.id.etApellidos);
        etCorreo=findViewById(R.id.etCorreo);
        etConfirmarCorreo=findViewById(R.id.etConfirmarCorreo);
        etNombreDeUsuario=findViewById(R.id.etNombreDeUsuario);
        etContraseña=findViewById(R.id.etContraseña);

        btnRegistro=findViewById(R.id.btnRegistro);
        btnRegistro.setOnClickListener(this);
        btnSalir=findViewById(R.id.btnSalir);
        btnSalir.setOnClickListener(this);
    }
}