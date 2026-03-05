package com.bluecodeltd.ecap.chw.model;

import org.smartregister.chw.core.utils.CoreJsonFormUtils;
import org.smartregister.family.FamilyLibrary;
import org.smartregister.family.domain.FamilyEventClient;
import org.smartregister.family.model.BaseFamilyProfileModel;

public class FamilyProfileModel extends BaseFamilyProfileModel {
    public FamilyProfileModel(String familyName) {
        super(familyName);
    }

    @Override
    public void updateWra(FamilyEventClient familyEventClient) {
        new FamilyProfileModelFlv().updateWra(familyEventClient);
    }

    public interface Flavor {
        void updateWra(FamilyEventClient familyEventClient);
    }

    public FamilyEventClient processFamilyRegistrationForm(String jsonString, String familyBaseEntityId) {
        return CoreJsonFormUtils.processFamilyUpdateForm(FamilyLibrary.getInstance().context().allSharedPreferences(), jsonString, familyBaseEntityId);
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

