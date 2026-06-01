package com.bluecodeltd.ecap.chw.model;

import com.bluecodeltd.ecap.chw.contract.AllClientsMemberContract;
import org.smartregister.family.FamilyLibrary;
import org.smartregister.family.domain.FamilyEventClient;
import org.smartregister.family.util.JsonFormUtils;

public class AllClientsMemberModel implements AllClientsMemberContract.Model {
    @Override
    public FamilyEventClient processJsonForm(String jsonString, String familyBaseEntityId) {
        return JsonFormUtils.processFamilyUpdateForm(FamilyLibrary.getInstance().context().allSharedPreferences(), jsonString, familyBaseEntityId);
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

