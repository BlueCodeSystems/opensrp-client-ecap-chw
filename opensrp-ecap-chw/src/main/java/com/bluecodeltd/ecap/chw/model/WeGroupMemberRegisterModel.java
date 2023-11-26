package com.bluecodeltd.ecap.chw.model;

import java.io.Serializable;

public class WeGroupMemberRegisterModel implements Serializable {
    private String first_name;
    private String last_name;
    private String gender;
    private String unique_id;
    boolean active;


    public WeGroupMemberRegisterModel(String first_name, String last_name, String gender, String unique_id, boolean active) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.gender = gender;
        this.unique_id = unique_id;
        this.active = active;
    }

    public WeGroupMemberRegisterModel() {

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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getUnique_id() {
        return unique_id;
    }

    public void setUnique_id(String unique_id) {
        this.unique_id = unique_id;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }


    @Override
    public String toString() {
        return this.getFirst_name() +" "+ this.getLast_name()+" \n ID: "+this.unique_id+" \n ";
    }
}
