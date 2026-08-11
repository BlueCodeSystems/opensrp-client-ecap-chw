package com.bluecodeltd.ecap.chw.api;

import com.bluecodeltd.ecap.chw.configs.Config;
import com.google.gson.JsonObject;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

public interface ItemApi {

    @POST("auth/login")
    Call<JsonObject> login(@Body JsonObject body);

    @GET
    Call<JsonObject> getItems(@Url String url, @Header("Authorization") String authorization, @QueryMap Map<String, String> query);

    @POST
    Call<JsonObject> registerDevice(@Url String url, @Header("Authorization") String authorization, @Body JsonObject body);
}
