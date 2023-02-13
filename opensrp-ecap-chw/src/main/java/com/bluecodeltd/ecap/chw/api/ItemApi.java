package com.bluecodeltd.ecap.chw.api;

import com.bluecodeltd.ecap.chw.configs.Config;
import com.bluecodeltd.ecap.chw.model.ItemList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ItemApi {

    @GET(Config.ITEMURL)
    Call<ItemList> getItems(@Query("phone") String phone);
}
