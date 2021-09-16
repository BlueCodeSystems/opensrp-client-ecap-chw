package com.bluecodeltd.ecap.chw.model;

import com.bluecodeltd.ecap.chw.BuildConfig;
import com.bluecodeltd.ecap.chw.application.ChwApplication;
import com.bluecodeltd.ecap.chw.contract.HouseholdIndexContract;
import com.bluecodeltd.ecap.chw.contract.MotherIndexContract;
import com.bluecodeltd.ecap.chw.util.Constants;
import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.domain.tag.FormTag;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.util.JsonFormUtils;

import timber.log.Timber;

public class HouseholdIndexModel implements HouseholdIndexContract.Model{

    @Override
    public HouseholdIndexEventClient processRegistration(String jsonString) {

        try {
            JSONObject formJsonObject = new JSONObject(jsonString);
            String entityId  = JsonFormUtils.generateRandomUUIDString();
            String encounterType = formJsonObject.getString(JsonFormConstants.ENCOUNTER_TYPE);
            JSONObject metadata = formJsonObject.getJSONObject(Constants.METADATA);


            JSONArray fields = JsonFormUtils.fields(formJsonObject);

            if (fields != null) {
                FormTag formTag = getFormTag();
                Event event = JsonFormUtils.createEvent(fields, metadata, formTag, entityId,
                        encounterType, Constants.EcapClientTable.EC_FAMILY);
                tagSyncMetadata(event);

                Client client = JsonFormUtils.createBaseClient(fields, formTag, entityId );

                return new HouseholdIndexEventClient(event, client);
            }

        } catch (JSONException e) {
            Timber.e(e);
        }

        return null;
    }

    public FormTag getFormTag() {
        FormTag formTag = new FormTag();
        AllSharedPreferences allSharedPreferences = getAllSharedPreferences();
        formTag.providerId = allSharedPreferences.fetchRegisteredANM();
        formTag.appVersion = BuildConfig.VERSION_CODE;
        formTag.databaseVersion = BuildConfig.DATABASE_VERSION;
        return formTag;
    }

    public AllSharedPreferences getAllSharedPreferences () {
        return ChwApplication.getInstance().getContext().allSharedPreferences();
    }
    public void tagSyncMetadata(Event event) {
        AllSharedPreferences allSharedPreferences = getAllSharedPreferences();
        String providerId = allSharedPreferences.fetchRegisteredANM();
        event.setProviderId(providerId);
        event.setLocationId(allSharedPreferences.fetchDefaultLocalityId(providerId));
        event.setChildLocationId(allSharedPreferences.fetchCurrentLocality());
        event.setTeam(allSharedPreferences.fetchDefaultTeam(providerId));
        event.setTeamId(allSharedPreferences.fetchDefaultTeamId(providerId));
        event.setClientDatabaseVersion(BuildConfig.DATABASE_VERSION);
        event.setClientApplicationVersion(BuildConfig.VERSION_CODE);
    }
}