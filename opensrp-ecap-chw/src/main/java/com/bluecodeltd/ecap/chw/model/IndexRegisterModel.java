package com.bluecodeltd.ecap.chw.model;

import com.bluecodeltd.ecap.chw.BuildConfig;
import com.bluecodeltd.ecap.chw.application.ChwApplication;
import com.bluecodeltd.ecap.chw.contract.IndexRegisterContract;
import com.bluecodeltd.ecap.chw.domain.ChildIndexEventClient;
import com.bluecodeltd.ecap.chw.util.Constants;
import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.domain.tag.FormTag;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.util.JsonFormUtils;

import timber.log.Timber;

public class IndexRegisterModel implements IndexRegisterContract.Model {
    @Override
    public ChildIndexEventClient processRegistration(String jsonString) {

        try {
            JSONObject formJsonObject = new JSONObject(jsonString);
            String entityId = formJsonObject.getString(JsonFormUtils.ENTITY_ID);
            String encounterType = formJsonObject.getString(JsonFormConstants.ENCOUNTER_TYPE);
            JSONObject metadata = formJsonObject.getJSONObject(Constants.METADATA);

            if (StringUtils.isBlank(entityId)) {
                entityId = JsonFormUtils.generateRandomUUIDString();
            }
            JSONArray fields = JsonFormUtils.fields(formJsonObject);
            //TODO Separate child fields from mother fields and create new events for both but process data in the same table - ec_client_index.
            // Remember to include mother details fields in ec_client_fields table
            if (fields != null) {
                FormTag formTag = getFormTag();
                Event event = JsonFormUtils.createEvent(fields, metadata, formTag, entityId,
                        encounterType, Constants.EcapClientTable.EC_CLIENT_INDEX);
                tagSyncMetadata(event);
                Client client = JsonFormUtils.createBaseClient(fields, formTag, entityId );
                return new ChildIndexEventClient(event, client);
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
