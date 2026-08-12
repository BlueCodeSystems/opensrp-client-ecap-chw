package com.bluecodeltd.ecap.chw.util;

import com.bluecodeltd.ecap.chw.BuildConfig;
import com.bluecodeltd.ecap.chw.api.ItemApi;
import com.bluecodeltd.ecap.chw.configs.Config;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class DirectusFlagsRepository {
    private DirectusFlagsRepository() {}

    public interface CountCallback {
        void onResult(int count);
        void onError(Throwable throwable);
    }

    public interface ItemsCallback {
        void onResult(List<JsonObject> items);
        void onError(Throwable throwable);
    }

    public static void fetchFlags(String caseworkerName, String email, String password, String caseworkerField, String collection, ItemsCallback callback) {
        api().login(loginBody(email, password)).enqueue(new retrofit2.Callback<JsonObject>() {
            @Override
            public void onResponse(retrofit2.Call<JsonObject> call, Response<JsonObject> response) {
                String token = accessToken(response);
                if (token.isEmpty()) {
                    callback.onError(new IllegalStateException("Missing access token"));
                    return;
                }
                Map<String, String> query = new HashMap<>();
                query.put("fields", "caseworker_name,caregiver_name,household_id,unique_id,facility,verifier");
                api().getItems(Config.BASEURL + "items/" + collection, "Bearer " + token, query).enqueue(new retrofit2.Callback<JsonObject>() {
                    @Override
                    public void onResponse(retrofit2.Call<JsonObject> call, Response<JsonObject> response) {
                        callback.onResult(extractItems(response));
                    }

                    @Override
                    public void onFailure(retrofit2.Call<JsonObject> call, Throwable t) {
                        callback.onError(t);
                    }
                });
            }

            @Override
            public void onFailure(retrofit2.Call<JsonObject> call, Throwable t) {
                callback.onError(t);
            }
        });
    }

    public static void fetchCount(String caseworkerName, String email, String password, String caseworkerField, String collection, CountCallback callback) {
        fetchFlags(caseworkerName, email, password, caseworkerField, collection, new ItemsCallback() {
            @Override
            public void onResult(List<JsonObject> items) {
                callback.onResult(items == null ? 0 : items.size());
            }

            @Override
            public void onError(Throwable throwable) {
                callback.onError(throwable);
            }
        });
    }

    private static ItemApi api() {
        return new Retrofit.Builder()
                .baseUrl(Config.BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(BuildConfig.DEBUG ? new OkHttpClient.Builder().build() : new OkHttpClient())
                .build()
                .create(ItemApi.class);
    }

    private static JsonObject loginBody(String email, String password) {
        JsonObject body = new JsonObject();
        body.addProperty("email", email == null ? "" : email);
        body.addProperty("password", password == null ? "" : password);
        body.addProperty("mode", "json");
        return body;
    }

    private static String accessToken(Response<JsonObject> response) {
        if (!response.isSuccessful() || response.body() == null) return "";
        JsonObject data = response.body().getAsJsonObject("data");
        return data != null && data.has("access_token") ? data.get("access_token").getAsString() : "";
    }

    private static List<JsonObject> extractItems(Response<JsonObject> response) {
        List<JsonObject> items = new ArrayList<>();
        if (!response.isSuccessful() || response.body() == null) return items;
        JsonArray data = response.body().getAsJsonArray("data");
        if (data == null) return items;
        for (JsonElement element : data) {
            if (element != null && element.isJsonObject()) items.add(element.getAsJsonObject());
        }
        return items;
    }
}
