package com.bluecodeltd.ecap.chw.model;

public class AllChildrenHIVStatusModel {
    private String isHivPositive;
    private String birthdate;
    private String infection_correct;
    private String protect_correct;
    private String prevention_correct;


    public AllChildrenHIVStatusModel(String isHivPositive, String birthdate) {
        this.isHivPositive = isHivPositive;
        this.birthdate = birthdate;
    }

    public String getIsHivPositive() {
        return isHivPositive;
    }

    public void setIsHivPositive(String isHivPositive) {
        this.isHivPositive = isHivPositive;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
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

