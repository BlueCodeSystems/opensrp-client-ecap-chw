package com.bluecodeltd.ecap.chw.model;

public class VCAServiceModel {

    private String base_entity_id, unique_id, date, art_clinic, date_last_vl, vl_last_result, date_next_vl,
            child_mmd, level_mmd, services, other_service;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getArt_clinic() {
        return art_clinic;
    }

    public void setArt_clinic(String art_clinic) {
        this.art_clinic = art_clinic;
    }

    public String getDate_last_vl() {
        return date_last_vl;
    }

    public void setDate_last_vl(String date_last_vl) {
        this.date_last_vl = date_last_vl;
    }

    public String getVl_last_result() {
        return vl_last_result;
    }

    public void setVl_last_result(String vl_last_result) {
        this.vl_last_result = vl_last_result;
    }

    public String getDate_next_vl() {
        return date_next_vl;
    }

    public void setDate_next_vl(String date_next_vl) {
        this.date_next_vl = date_next_vl;
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

    public String getServices() {
        return services;
    }

    public void setServices(String services) {
        this.services = services;
    }

    public String getBase_entity_id() {
        return base_entity_id;
    }

    public void setBase_entity_id(String base_entity_id) {
        this.base_entity_id = base_entity_id;
    }

    public String getOther_service() {
        return other_service;
    }

    public void setOther_service(String other_service) {
        this.other_service = other_service;
    }

    public String getUnique_id() {
        return unique_id;
    }

    public void setUnique_id(String unique_id) {
        this.unique_id = unique_id;
    }
}
