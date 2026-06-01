package com.bluecodeltd.ecap.chw.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ItemList {

    @SerializedName("itemList")
    @Expose
    private ArrayList<Items> jobb = null;

    public ArrayList<Items> getItems() {
        return jobb;
    }

    public void setItems(ArrayList<Items> jobb) {
        this.jobb = jobb;
    }

    private final java.util.Map<String, String> additionalFields = new java.util.HashMap<>();

    public java.util.Map<String, String> getAdditionalFields() {
        return additionalFields;
    }

    public String getAdditionalField(String key) {
        if (key == null) return null;
        return additionalFields.get(key);
    }

    public void setAdditionalField(String key, String value) {
        if (key == null) return;
        additionalFields.put(key, value);
    }
}

