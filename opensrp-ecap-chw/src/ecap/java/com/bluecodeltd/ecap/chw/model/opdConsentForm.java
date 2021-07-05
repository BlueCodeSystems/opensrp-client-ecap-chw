package org.smartregister.chw.model;

import androidx.annotation.Nullable;

import org.json.JSONObject;
import org.smartregister.domain.tag.FormTag;
import org.smartregister.opd.pojo.OpdEventClient;

import java.util.List;

public abstract class opdConsentForm {
    @Nullable
    public abstract JSONObject getFormAsJson(String formName, String entityId, String currentLocationId);

    @Nullable
    public abstract List<OpdEventClient> processRegistration(String jsonString, FormTag formTag);
}
