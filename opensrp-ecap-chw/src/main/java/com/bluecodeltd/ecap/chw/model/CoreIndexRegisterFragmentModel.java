package com.bluecodeltd.ecap.chw.model;


import com.bluecodeltd.ecap.chw.contract.CoreIndexRegisterFragmentContract;

public class CoreIndexRegisterFragmentModel implements CoreIndexRegisterFragmentContract {


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

