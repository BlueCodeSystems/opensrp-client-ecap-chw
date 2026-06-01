package com.bluecodeltd.ecap.chw.model;

public class CaregiverHouseholdvisitationModel extends Caregiver{
    String caregiver_art;
    public String getCaregiver_art() {
        return caregiver_art;
    }

    public void setCaregiver_art(String caregiver_art) {
        this.caregiver_art = caregiver_art;
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

