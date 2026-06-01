package com.bluecodeltd.ecap.chw.model;

public class NotificationsModel {

    private String  vca_name, visit_date, vca_id,  vca_age;

    public String getVca_name() {
        return vca_name;
    }

    public void setVca_name(String vca_name) {
        this.vca_name = vca_name;
    }

    public String getVisit_date() {
        return visit_date;
    }

    public void setVisit_date(String visit_date) {
        this.visit_date = visit_date;
    }

    public String getVca_id() {
        return vca_id;
    }

    public void setVca_id(String vca_id) {
        this.vca_id = vca_id;
    }

    public String getVca_age() {
        return vca_age;
    }

    public void setVca_age(String vca_age) {
        this.vca_age = vca_age;
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

