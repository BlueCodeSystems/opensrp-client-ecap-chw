package com.bluecodeltd.ecap.chw.model;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class DirectusFlagsResponse {
    @SerializedName("data")
    private List<JsonObject> data = new ArrayList<>();

    public List<JsonObject> getData() {
        return data;
    }
}
