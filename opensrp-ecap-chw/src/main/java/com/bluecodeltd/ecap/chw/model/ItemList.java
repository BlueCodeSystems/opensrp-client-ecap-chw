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
}
