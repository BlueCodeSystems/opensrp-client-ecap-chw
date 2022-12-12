package com.bluecodeltd.ecap.chw.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Items implements Serializable {

    @SerializedName("formname")
    @Expose
    private String formname;

    @SerializedName("vcaid")
    @Expose
    private String vcaid;

    @SerializedName("facility")
    @Expose
    private String facility;

    @SerializedName("comment")
    @Expose
    private String comment;

    @SerializedName("user")
    @Expose
    private String user;

    @SerializedName("user_email")
    @Expose
    private String user_email;

    @SerializedName("status")
    @Expose
    private String status;

    public String getFormname() {
        return formname;
    }

    public void setFormname(String formname) {
        this.formname = formname;
    }

    public String getVcaid() {
        return vcaid;
    }

    public void setVcaid(String vcaid) {
        this.vcaid = vcaid;
    }

    public String getFacility() {
        return facility;
    }

    public void setFacility(String facility) {
        this.facility = facility;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
