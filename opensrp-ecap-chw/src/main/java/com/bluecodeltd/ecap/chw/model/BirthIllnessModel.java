package com.bluecodeltd.ecap.chw.model;

public class BirthIllnessModel {
    private String lastBirthCertData;
    private String lastIllnessData;

    public String getLastBirthCertData() {
        return lastBirthCertData;
    }

    public void setLastBirthCertData(String lastBirthCertData) {
        this.lastBirthCertData = lastBirthCertData;
    }

    public String getLastIllnessData() {
        return lastIllnessData;
    }

    public void setLastIllnessData(String lastIllnessData) {
        this.lastIllnessData = lastIllnessData;
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

