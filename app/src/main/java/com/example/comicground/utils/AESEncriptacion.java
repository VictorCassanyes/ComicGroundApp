package com.example.comicground.utils;

import android.util.Base64;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class AESEncriptacion {

    public static String encriptar(String contrasena) {
        try {
            //Crear semilla con la propia contraseña, así hay una distinta para cada usuario (suponiendo que tienen distintas contraseñas)
            byte[] semilla=contrasena.getBytes(StandardCharsets.UTF_8);
            //Crear hash y dejarlo en 16 Bytes (128 bits), para que cipher lo soporte
            MessageDigest sha=MessageDigest.getInstance(Constantes.TIPO_HASH);
            semilla=sha.digest(semilla);
            semilla= Arrays.copyOf(semilla, 16);
            //Crear la clave con la semilla y el tipo de algoritmo
            SecretKey key=new SecretKeySpec(semilla,Constantes.TIPO_ALGORITMO);

            //Crear cifrador con el tipo de algoritmo e iniciarlo con el modo para cifrar
            Cipher cifrador=Cipher.getInstance(Constantes.TIPO_ALGORITMO);
            cifrador.init(Cipher.ENCRYPT_MODE, key);

            //Obtener la contraseña encriptada
            byte[] contrasenaCifrada=cifrador.doFinal(contrasena.getBytes());

            //Devolver la contraseña encriptada y codificada a String con Base64
            return Base64.encodeToString(contrasenaCifrada, Base64.NO_WRAP);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }
}