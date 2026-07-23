package com.bluecodeltd.ecap.chw.model;

import java.util.HashSet;
import java.util.Set;

public abstract class DefaultAncRegisterFragmentModelFlv implements AncRegisterFragmentModel.Flavor {
    @Override
    public Set<String> mainColumns(String tableName) {
        Set<String> columnList = new HashSet<>();
        columnList.add(tableName + "." + org.smartregister.chw.anc.util.DBConstants.KEY.HAS_ANC_CARD);
        return columnList;
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

