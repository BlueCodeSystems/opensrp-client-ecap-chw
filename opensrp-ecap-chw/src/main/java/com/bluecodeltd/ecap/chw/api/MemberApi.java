package com.bluecodeltd.ecap.chw.api;

import com.bluecodeltd.ecap.chw.model.MemberListModel;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface MemberApi {
    @POST("auth/admin/realms/ecap-stage/users") // replace with your endpoint
    Call<MemberListModel> createUser(@Body MemberListModel user);
}