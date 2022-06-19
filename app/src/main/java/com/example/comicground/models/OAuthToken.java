package com.example.comicground.models;

import com.google.gson.annotations.SerializedName;

/*
 * Respuesta específica a recibir cuando se pide un nuevo token a la API
 */
public class OAuthToken {

    @SerializedName("access_token")
    private String accessToken;

    @SerializedName("token_type")
    private String tokenType;

    @SerializedName("expires_in")
    private Long expiresIn;

    private String scope;

    private String jti;

    public String getAccessToken() {
        return accessToken;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }
}
