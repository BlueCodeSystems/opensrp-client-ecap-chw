package com.bluecodeltd.ecap.chw.model;

import static com.bluecodeltd.ecap.chw.util.JsonFormUtils.METADATA;
import static com.vijay.jsonwizard.utils.FormUtils.fields;
import static com.vijay.jsonwizard.utils.FormUtils.getFieldJSONObject;
import static org.smartregister.util.JsonFormUtils.ENCOUNTER_LOCATION;
import static org.smartregister.util.JsonFormUtils.STEP1;

import androidx.annotation.Nullable;

import com.bluecodeltd.ecap.chw.BuildConfig;
import com.bluecodeltd.ecap.chw.application.ChwApplication;
import com.bluecodeltd.ecap.chw.contract.HouseholdIndexContract;
import com.bluecodeltd.ecap.chw.contract.MotherIndexContract;
import com.bluecodeltd.ecap.chw.util.IndexClientsUtils;
import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.domain.tag.FormTag;
import org.smartregister.family.util.Constants;
import org.smartregister.opd.utils.OpdUtils;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.util.JsonFormUtils;

import java.util.List;

import timber.log.Timber;

public class HouseholdIndexModel implements HouseholdIndexContract.Model{

    @Nullable
    @Override
    public JSONObject getFormAsJson(String formName, String entityId, String currentLocationId) {
        try {
            JSONObject form = OpdUtils.getJsonFormToJsonObject(formName);
            if (form == null) {
                return null;
            }

            form.getJSONObject(METADATA).put(ENCOUNTER_LOCATION, currentLocationId);

            String newEntityId = entityId;
            if (StringUtils.isNotBlank(entityId)) {
                newEntityId = entityId.replace("-", "");
            }

            JSONObject stepOneUniqueId = getFieldJSONObject(fields(form, STEP1), Constants.JSON_FORM_KEY.UNIQUE_ID);

            if (stepOneUniqueId != null) {
                stepOneUniqueId.remove(org.smartregister.family.util.JsonFormUtils.VALUE);
                stepOneUniqueId.put(org.smartregister.family.util.JsonFormUtils.VALUE, newEntityId);
            }

            return form;

        } catch (Exception e) {
            Timber.e(e, "Error loading VCA Screening form");
        }
        return null;
    }

    @Nullable
    @Override
    public List<EventClient> processRegistration(String jsonString, FormTag formTag) {
        return IndexClientsUtils.getEventClients(jsonString);
    }

}
