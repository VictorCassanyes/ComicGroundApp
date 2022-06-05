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
            @Field("code") String code,
            @Field("client_id") String client_id,
            @Field("redirect_uri") String redirect_uri,
            @Field("grant_type") String grant_type
    );
}