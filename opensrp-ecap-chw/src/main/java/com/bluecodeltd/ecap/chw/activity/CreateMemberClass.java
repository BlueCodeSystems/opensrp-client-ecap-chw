package com.bluecodeltd.ecap.chw.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bluecodeltd.ecap.chw.BuildConfig;
import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.api.MemberApi;
import com.bluecodeltd.ecap.chw.api.VolleyCallback;
import com.bluecodeltd.ecap.chw.application.ChwApplication;
import com.bluecodeltd.ecap.chw.interceptor.AuthInterceptor;
import com.bluecodeltd.ecap.chw.model.Credentials;
import com.bluecodeltd.ecap.chw.model.MemberListModel;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class CreateMemberClass extends AppCompatActivity {
    Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_member_class);

        String username = getIntent().getStringExtra("username");
        String password = getIntent().getStringExtra("password");

        getToken(username, password, BuildConfig.OAUTH_CLIENT_ID, BuildConfig.OAUTH_CLIENT_SECRET, new VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                Log.d("Access Token", result);

            }
        });
        submit = findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postMember();
            }

            public void postMember() {
                getToken(username, password, BuildConfig.OAUTH_CLIENT_ID, BuildConfig.OAUTH_CLIENT_SECRET, new VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {
                        Log.d("Access Token", result);

                        String token = result;

                        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
                        httpClient.addInterceptor(new AuthInterceptor(token));

                        Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl("https://keycloak.zeir.smartregister.org/")
                                .addConverterFactory(ScalarsConverterFactory.create())
                                .addConverterFactory(GsonConverterFactory.create())
                                .client(new OkHttpClient.Builder().addInterceptor(new AuthInterceptor(token)).build())
                                .build();

                        MemberApi memberApi = retrofit.create(MemberApi.class);

                        Map<String, String> attributes = new HashMap<>();
                        attributes.put("gender", "Male");
                        attributes.put("dob", "1990-01-01");
                        attributes.put("height", "180");

                        Credentials credential = new Credentials("password","123456",false);

                        List<Credentials> credentialsList = new ArrayList<>();
                        credentialsList.add(credential);

                        MemberListModel member = new MemberListModel(
                                "Lee",
                                true,
                                false,
                                "Lee",
                                "Lest",
                                "lee1@wegroup.com",
                                attributes,
                                credentialsList
                        );

                        Call<ResponseBody> userCall = memberApi.createUser(member);
                        userCall.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                                if (response.isSuccessful()) {
                                    // Even if the response body is empty, this is a successful response.
                                    Log.d("API Response", "User created successfully with status code: " + response.code());

                                    // You can then do something with the raw response if needed, for example:
                                    if (response.body() != null) {
                                        try {
                                            String responseBodyString = response.body().string(); // Beware, can only be called once!
                                        } catch (IOException e) {
                                            throw new RuntimeException(e);
                                        }
                                        // Log or handle the response
                                    }
                                } else {
                                    // You can extract the error body if needed
                                    Log.e("API Response", "Unsuccessful response: " + response.code());
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                Log.e("API Response", "onFailure", t);
                                // Handle the error, such as a network error or a parsing error
                            }
                        });
                    }
                });
            }
        });
    }

    private void getToken(String username, String password, String clientId, String clientSecret, final VolleyCallback callback) {
        String url = "https://keycloak.zeir.smartregister.org/auth/realms/ecap-stage/protocol/openid-connect/token";

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String accessToken = jsonObject.getString("access_token");
                            callback.onSuccess(accessToken);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                        Log.e("Error", error.toString());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("grant_type", "password");
                params.put("client_id", clientId);
                params.put("client_secret", clientSecret);
                params.put("username", username);
                params.put("password", password);
                return params;
            }
        };
        Volley.newRequestQueue(this).add(stringRequest);
    }
    private void getCreds(String token){

        Log.i("chobela_token ", "chobela_token" + token);

        String tag_string_creds = "req_creds";

        String url = "https://keycloak.zeir.smartregister.org/auth/realms/ecap-stage/protocol/openid-connect/userinfo";
        StringRequest
                stringRequest
                = new StringRequest(
                Request.Method.GET,
                url,
                (Response.Listener<String>) response -> {

                    try {
                        JSONObject jObj = new JSONObject(response);

                        String sub = jObj.getString("sub");
                        String code = jObj.getString("code");
                        String name = jObj.getString("name");
                        String given_name = jObj.getString("given_name");
                        String family_name = jObj.getString("family_name");
                        String province = jObj.getString("province");
                        String partner = jObj.getString("partner");
                        String phone = jObj.getString("phone");
                        String district = jObj.getString("district");
                        String facility = jObj.getString("facility");
                        String email = jObj.getString("email");
                        String nrc = jObj.getString("nrc");

                        // save user data
                        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(CreateMemberClass.this);
                        SharedPreferences.Editor edit = sp.edit();


                        edit.putString("sub", sub);
                        edit.putString("code", code);
                        edit.putString("caseworker_name", name);
                        edit.putString("given_name", given_name);
                        edit.putString("family_name", family_name);
                        edit.putString("province", province);
                        edit.putString("partner", partner);
                        edit.putString("phone", phone);
                        edit.putString("district", district);
                        edit.putString("facility", facility);
                        edit.putString("email", email);
                        edit.putString("nrc", nrc);

                        edit.commit();
                        finish();
                        startActivity(getIntent());

                    } catch (JSONException e){
                        e.printStackTrace();
                    }
                },
                error -> {

                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + token);
                return params;
            }};


        ChwApplication.getApplicationFlavor().chwAppInstance().addToRequestQueue(stringRequest, tag_string_creds);

    }
}