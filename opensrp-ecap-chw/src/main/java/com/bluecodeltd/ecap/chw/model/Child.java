package com.bluecodeltd.ecap.chw.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Child implements Serializable {

    public static final String ENTITY_ID = "entity_id";

    public static String getEntityId() {
        return ENTITY_ID;
    }

    public String getSubpop1() {
        return subpop1;
    }

    public void setSubpop1(String subpop1) {
        this.subpop1 = subpop1;
    }

    public String getSubpop2() {
        return subpop2;
    }

    public void setSubpop2(String subpop2) {
        this.subpop2 = subpop2;
    }

    public String getSubpop3() {
        return subpop3;
    }

    public void setSubpop3(String subpop3) {
        this.subpop3 = subpop3;
    }

    public String getSubpop4() {
        return subpop4;
    }

    public void setSubpop4(String subpop4) {
        this.subpop4 = subpop4;
    }

    public String getSubpop5() {
        return subpop5;
    }

    public void setSubpop5(String subpop5) {
        this.subpop5 = subpop5;
    }

    public String getSubpop6() {
        return subpop6;
    }

    public void setSubpop6(String subpop6) {
        this.subpop6 = subpop6;
    }

    public String getDate_referred() {
        return date_referred;
    }

    public void setDate_referred(String date_referred) {
        this.date_referred = date_referred;
    }

    public String getDate_enrolled() {
        return date_enrolled;
    }

    public void setDate_enrolled(String date_enrolled) {
        this.date_enrolled = date_enrolled;
    }

    public String getArt_check_box() {
        return art_check_box;
    }

    public void setArt_check_box(String art_check_box) {
        this.art_check_box = art_check_box;
    }

    public String getArt_number() {
        return art_number;
    }

    public void setArt_number(String art_number) {
        this.art_number = art_number;
    }

    public String getDate_started_art() {
        return date_started_art;
    }

    public void setDate_started_art(String date_started_art) {
        this.date_started_art = date_started_art;
    }

    public String getDate_last_vl() {
        return date_last_vl;
    }

    public void setDate_last_vl(String date_last_vl) {
        this.date_last_vl = date_last_vl;
    }

    public String getDate_next_vl() {
        return date_next_vl;
    }

    public void setDate_next_vl(String date_next_vl) {
        this.date_next_vl = date_next_vl;
    }

    public String getVl_last_result() {
        return vl_last_result;
    }

    public void setVl_last_result(String vl_last_result) {
        this.vl_last_result = vl_last_result;
    }

    public String getVl_suppressed() {
        return vl_suppressed;
    }

    public void setVl_suppressed(String vl_suppressed) {
        this.vl_suppressed = vl_suppressed;
    }

    public String getChild_mmd() {
        return child_mmd;
    }

    public void setChild_mmd(String child_mmd) {
        this.child_mmd = child_mmd;
    }

    public String getLevel_mmd() {
        return level_mmd;
    }

    public void setLevel_mmd(String level_mmd) {
        this.level_mmd = level_mmd;
    }

    public String getCaregiver_name() {
        return caregiver_name;
    }

    public void setCaregiver_name(String caregiver_name) {
        this.caregiver_name = caregiver_name;
    }

    public String getCaregiver_birth_date() {
        return caregiver_birth_date;
    }

    public void setCaregiver_birth_date(String caregiver_birth_date) {
        this.caregiver_birth_date = caregiver_birth_date;
    }

    public String getCaregiver_sex() {
        return caregiver_sex;
    }

    public void setCaregiver_sex(String caregiver_sex) {
        this.caregiver_sex = caregiver_sex;
    }

    public String getCaregiver_hiv_status() {
        return caregiver_hiv_status;
    }

    public void setCaregiver_hiv_status(String caregiver_hiv_status) {
        this.caregiver_hiv_status = caregiver_hiv_status;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    public String getCaregiver_phone() {
        return caregiver_phone;
    }

    public void setCaregiver_phone(String caregiver_phone) {
        this.caregiver_phone = caregiver_phone;
    }

    public String getHealth_facility() {
        return health_facility;
    }

    public void setHealth_facility(String health_facility) {
        this.health_facility = health_facility;
    }

    public Child() {

    }

    public Child(String entity_id, String first_name, String last_name, String adolescent_birthdate, String subpop1, String subpop2, String subpop3, String subpop4, String subpop5, String subpop6, String date_referred, String date_enrolled, String art_check_box, String art_number, String date_started_art, String date_last_vl, String date_next_vl, String vl_last_result, String vl_suppressed, String child_mmd, String level_mmd, String caregiver_name, String caregiver_birth_date, String caregiver_sex, String caregiver_hiv_status, String relation, String caregiver_phone, String health_facility, String gender) {
        this.entity_id = entity_id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.adolescent_birthdate = adolescent_birthdate;
        this.subpop1 = subpop1;
        this.subpop2 = subpop2;
        this.subpop3 = subpop3;
        this.subpop4 = subpop4;
        this.subpop5 = subpop5;
        this.subpop6 = subpop6;
        this.date_referred = date_referred;
        this.date_enrolled = date_enrolled;
        this.art_check_box = art_check_box;
        this.art_number = art_number;
        this.date_started_art = date_started_art;
        this.date_last_vl = date_last_vl;
        this.date_next_vl = date_next_vl;
        this.vl_last_result = vl_last_result;
        this.vl_suppressed = vl_suppressed;
        this.child_mmd = child_mmd;
        this.level_mmd = level_mmd;
        this.caregiver_name = caregiver_name;
        this.caregiver_birth_date = caregiver_birth_date;
        this.caregiver_sex = caregiver_sex;
        this.caregiver_hiv_status = caregiver_hiv_status;
        this.relation = relation;
        this.caregiver_phone = caregiver_phone;
        this.health_facility = health_facility;
        this.gender = gender;
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

    @SerializedName("subpop1")
    @Expose
    private String subpop1;

    @SerializedName("subpop2")
    @Expose
    private String subpop2;

    @SerializedName("subpop3")
    @Expose
    private String subpop3;

    @SerializedName("subpop4")
    @Expose
    private String subpop4;

    @SerializedName("subpop5")
    @Expose
    private String subpop5;

    @SerializedName("subpop6")
    @Expose
    private String subpop6;

    @SerializedName("date_referred")
    @Expose
    private String date_referred;

    @SerializedName("date_enrolled")
    @Expose
    private String date_enrolled;

    @SerializedName("art_check_box")
    @Expose
    private String art_check_box;

    @SerializedName("art_number")
    @Expose
    private String art_number;

    @SerializedName("date_started_art")
    @Expose
    private String date_started_art;

    @SerializedName("date_last_vl")
    @Expose
    private String date_last_vl;

    @SerializedName("date_next_vl")
    @Expose
    private String date_next_vl;

    @SerializedName("vl_last_result")
    @Expose
    private String vl_last_result;

    @SerializedName("vl_suppressed")
    @Expose
    private String vl_suppressed;

    @SerializedName("child_mmd")
    @Expose
    private String child_mmd;

    @SerializedName("level_mmd")
    @Expose
    private String level_mmd;

    @SerializedName("caregiver_name")
    @Expose
    private String caregiver_name;

    @SerializedName("caregiver_birth_date")
    @Expose
    private String caregiver_birth_date;

    @SerializedName("caregiver_sex")
    @Expose
    private String caregiver_sex;

    @SerializedName("caregiver_hiv_status")
    @Expose
    private String caregiver_hiv_status;

    @SerializedName("relation")
    @Expose
    private String relation;

    @SerializedName("caregiver_phone")
    @Expose
    private String caregiver_phone;

    @SerializedName("health_facility")
    @Expose
    private String health_facility;

    @SerializedName("gender")
    @Expose
    private String gender;

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }



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
