package com.bluecodeltd.ecap.chw.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.BroadcastReceiver;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;


import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.adapter.ItemAdapter;
import com.bluecodeltd.ecap.chw.api.ItemApi;
import com.bluecodeltd.ecap.chw.configs.Config;
import com.bluecodeltd.ecap.chw.model.ItemList;
import com.bluecodeltd.ecap.chw.model.Items;
import com.bluecodeltd.ecap.chw.model.VcaVisitationModel;

import java.security.cert.CertificateException;
import java.util.ArrayList;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FlagActivity extends AppCompatActivity {

    ProgressBar progressBar;
    RecyclerView recyclerView;
    RecyclerView.Adapter recyclerViewadapter;
    private ArrayList<Items> itemList;
    private static final String TAG = FlagActivity.class.getSimpleName();
    private SwipeRefreshLayout swipeContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flag);

        progressBar = findViewById(R.id.progress);
        showProgressBar();

        loadJSON();

        swipeContainer = findViewById(R.id.swipeContainer);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override

            public void onRefresh() {

                hideProgressBar();
                swipeContainer.setRefreshing(true);
                loadJSON();

            }

        });

        swipeContainer.setColorSchemeResources(
                android.R.color.black,
                android.R.color.holo_green_dark);
    }

    private void loadJSON() {


        Log.e(TAG, "Fetching Data ! ");

        Retrofit.Builder builder = new Retrofit.Builder();

        builder.baseUrl(Config.BASEURL);
        builder.addConverterFactory(GsonConverterFactory.create());
        builder.client(getUnsafeOkHttpClient());
        Retrofit retrofit = builder.build();

        ItemApi api = retrofit.create(ItemApi.class);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(FlagActivity.this);
        String phone = sp.getString("phone", "anonymous");

        Call<ItemList> call = api.getItems("0966111111");


        call.enqueue(new Callback<ItemList>() {
            @Override
            public void onResponse(Call<ItemList> call, Response<ItemList> response) {
                //Dismiss Dialog
                hideProgressBar();
                swipeContainer.setRefreshing(false);

                if (response.isSuccessful()) {
                    /**
                     * Got Successfully
                     */
                    Log.e(TAG, "Response success: " + response.toString());

                    itemList = response.body().getItems();
                    recyclerView = (RecyclerView) findViewById(R.id.flagRecyclerView);
                    RecyclerView.LayoutManager eLayoutManager = new LinearLayoutManager(FlagActivity.this);
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(eLayoutManager);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    recyclerViewadapter = new ItemAdapter(itemList, FlagActivity.this);
                    recyclerView.setAdapter(recyclerViewadapter);
                    recyclerViewadapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<ItemList> call, Throwable t) {
                hideProgressBar();
                Log.e(TAG, "Response failed: " + t.toString());
            }
        });
    }

    private static OkHttpClient getUnsafeOkHttpClient() {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            OkHttpClient okHttpClient = builder.build();
            return okHttpClient;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        if (progressBar.getVisibility() == View.VISIBLE) {
            progressBar.setVisibility(View.INVISIBLE);
        }
    }
}