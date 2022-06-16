package com.example.comicground.api.endpoints;

import com.example.comicground.api.responses.OAuthToken;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface OAuthEndpoints {

    @POST("oauth/token")
    @FormUrlEncoded
    Call<OAuthToken> getAccessToken(@Header("authorization") String credencialesCliente,
                                    @Field("username") String username,
                                    @Field("password") String password,
                                    @Field("grant_type") String grant_type);
}