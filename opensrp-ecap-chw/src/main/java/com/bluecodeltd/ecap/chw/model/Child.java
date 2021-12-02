package com.bluecodeltd.ecap.chw.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Child implements Serializable {

    public static final String ENTITY_ID = "entity_id";


    public Child() {

    }

    public Child (String entity_id, String first_name, String last_name, String adolescent_birthdate){

        this.entity_id = entity_id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.adolescent_birthdate = adolescent_birthdate;
    }

    @SerializedName("entity_id")
    @Expose
    private String entity_id;

    @SerializedName("first_name")
    @Expose
    private String first_name;

    @SerializedName("last_name")
    @Expose
    private String last_name;

    @SerializedName("adolescent_birthdate")
    @Expose
    private String adolescent_birthdate;

    public String getEntity_id() {
        return entity_id;
    }

    public void setEntity_id(String entity_id) {
        this.entity_id = entity_id;
    }


    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getAdolescent_birthdate() {
        return adolescent_birthdate;
    }

    public void setAdolescent_birthdate(String adolescent_birthdate) {
        this.adolescent_birthdate = adolescent_birthdate;
    }
}
