package com.bluecodeltd.ecap.chw.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Child implements Serializable {

    public static final String ENTITY_ID = "entity_id";
    public static final String FIRSTNAME = "firstname";
    public static final String LASTNAME = "lastname";

    public Child() {

    }

    public Child (String entity_id, String firstname, String lastname){

        this.entity_id = entity_id;
        this.firstname = firstname;
        this.lastname = lastname;
    }

    @SerializedName("entity_id")
    @Expose
    private String entity_id;

    @SerializedName("firstname")
    @Expose
    private String firstname;

    @SerializedName("lastname")
    @Expose
    private String lastname;

    public String getEntity_id() {
        return entity_id;
    }

    public void setEntity_id(String entity_id) {
        this.entity_id = entity_id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }
}
