package com.example.comicground.api.seguridad;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface IOAuthServer {

    // @Headers("Accept: application/json")

    /**
     * The call to request a token
     */

    @POST("oauth/token")
    @FormUrlEncoded
    Call<OAuthToken> getAccessToken(
            @Field("username") String username,
            @Field("password") String password,
            @Field("grant_type") String grant_type
    );
}