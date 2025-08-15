package com.bluecodeltd.ecap.chw.model;



public class ConsentFormModel {


    private String childs_name;
    private String art_number;
    private String caregiver_relationship;
    private String guardian_nrc;
    private String date;
    private String check_box;


    public ConsentFormModel(String childs_name, String art_number, String caregiver_relationship, String guardian_nrc, String date, String check_box) {
        this.childs_name = childs_name;
        this.art_number = art_number;
        this.caregiver_relationship = caregiver_relationship;
        this.guardian_nrc = guardian_nrc;
        this.date = date;
        this.check_box = check_box;
    }

    public String getChilds_name() {
        return childs_name;
    }

    public void setChilds_name(String childs_name) {
        this.childs_name = childs_name;
    }

    public String getArt_number() {
        return art_number;
    }

    public void setArt_number(String art_number) {
        this.art_number = art_number;
    }

    public String getCaregiver_relationship() {
        return caregiver_relationship;
    }

    public void setCaregiver_relationship(String caregiver_relationship) {
        this.caregiver_relationship = caregiver_relationship;
    }

    public String getGuardian_nrc() {
        return guardian_nrc;
    }

    public void setGuardian_nrc(String guardian_nrc) {
        this.guardian_nrc = guardian_nrc;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCheck_box() {
        return check_box;
    }

    public void setCheck_box(String check_box) {
        this.check_box = check_box;
    }
}
