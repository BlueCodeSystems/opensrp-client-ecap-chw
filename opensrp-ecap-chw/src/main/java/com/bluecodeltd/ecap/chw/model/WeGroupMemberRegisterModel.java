package com.bluecodeltd.ecap.chw.model;

public class WeGroupMemberRegisterModel {
    private String unique_id;
    private String first_name;
    private String last_name;
    private String gender;


    public WeGroupMemberRegisterModel(String unique_id, String first_name, String last_name, String gender) {
        this.unique_id = unique_id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.gender = gender;
    }

    public String getUnique_id() {
        return unique_id;
    }

    public void setUnique_id(String unique_id) {
        this.unique_id = unique_id;
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
}
