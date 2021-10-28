package com.bluecodeltd.ecap.chw.model;

import static org.smartregister.client.utils.constants.JsonFormConstants.TYPE;
import static org.smartregister.util.JsonFormUtils.KEY;
import static org.smartregister.util.JsonFormUtils.REPEATING_GROUP;
import static org.smartregister.util.JsonFormUtils.VALUE;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bluecodeltd.ecap.chw.BuildConfig;
import com.bluecodeltd.ecap.chw.application.ChwApplication;
import com.bluecodeltd.ecap.chw.contract.MotherIndexContract;
import com.bluecodeltd.ecap.chw.util.Constants;
import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.domain.UniqueId;
import org.smartregister.domain.tag.FormTag;
import org.smartregister.family.util.Utils;
import org.smartregister.opd.OpdLibrary;
import org.smartregister.opd.pojo.RegisterParams;
import org.smartregister.opd.utils.OpdJsonFormUtils;
import org.smartregister.opd.utils.OpdUtils;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.UniqueIdRepository;
import org.smartregister.util.JsonFormUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class MotherIndexModel implements MotherIndexContract.Model {


    @Override
    public List<EventClient> processRegistration(String jsonString) {

        List<EventClient> eventClients = new ArrayList<>();

        try {
            JSONObject formJsonObject = new JSONObject(jsonString);
            String entityId = JsonFormUtils.generateRandomUUIDString();
            String encounterType = formJsonObject.getString(JsonFormConstants.ENCOUNTER_TYPE);
            JSONObject metadata = formJsonObject.getJSONObject(Constants.METADATA);


            JSONArray fields = JsonFormUtils.fields(formJsonObject);
            JSONArray motherFields = new JSONArray();

            Collection<JSONArray> childrenArray = extractRepeatingGroupsFields(fields);

            //Remove Repeat Group from Mother

            for (int i = 0; i < fields.length(); i++) {

                JSONObject field = fields.getJSONObject(i);
                if (field.has(JsonFormConstants.TYPE) &&
                        !REPEATING_GROUP.equalsIgnoreCase(field.getString(JsonFormConstants.TYPE))) {
                    motherFields.put(field);
                }

            }

            //Process Mother

           /* FormTag formTag = getFormTag();

            Event motherEvent = JsonFormUtils.createEvent(motherFields, metadata, formTag, entityId,
                    encounterType, Constants.EcapClientTable.EC_MOTHER_INDEX);
            tagSyncMetadata(motherEvent);

            Client motherClient = JsonFormUtils.createBaseClient(motherFields, formTag, entityId);

            eventClients.add(new EventClient(motherEvent, motherClient));*/

            //Process Children (Each Child)

            for (JSONArray childFields : childrenArray) {

                FormTag formTag = getFormTag();

                RegisterParams registerParam = new RegisterParams();
                registerParam.setEditMode(false);
                registerParam.setFormTag(formTag);

                Event childEvent = JsonFormUtils.createEvent(childFields, metadata, formTag, entityId,
                        encounterType, Constants.EcapClientTable.EC_CLIENT_INDEX);
                tagSyncMetadata(childEvent);

                Client childClient = JsonFormUtils.createBaseClient(childFields, formTag, entityId);

                eventClients.add(new EventClient(childEvent, childClient));

                updateOpenSRPId(jsonString, registerParam, childClient);
            }


            return eventClients;


        } catch (JSONException e) {
            Timber.e(e);
        }

        return null;
    }

    private void updateOpenSRPId(String jsonString, RegisterParams params, Client baseClient) {

        UniqueId uniqueId = getUniqueIdRepository().getNextUniqueId();

        if (params.isEditMode()) {
            // UnAssign current OpenSRP ID
            if (baseClient != null) {
                String newOpenSrpId = baseClient.getIdentifier(Utils.metadata().uniqueIdentifierKey).replace("-", "");
                String currentOpenSrpId = org.smartregister.family.util.JsonFormUtils.getString(jsonString, org.smartregister.family.util.JsonFormUtils.CURRENT_OPENSRP_ID).replace("-", "");
                if (!newOpenSrpId.equals(currentOpenSrpId)) {
                    //OpenSRP ID was changed
                    getUniqueIdRepository().open(currentOpenSrpId);
                }
            }

        } else {
            if (baseClient != null) {
                String openSrpId = baseClient.getIdentifier(Utils.metadata().uniqueIdentifierKey);
                if (StringUtils.isNotBlank(openSrpId) && !openSrpId.contains(org.smartregister.family.util.Constants.IDENTIFIER.FAMILY_SUFFIX)) {
                    //Mark OpenSRP ID as used
                    getUniqueIdRepository().close(openSrpId);
                }
            }
        }
    }

    @NonNull
    public UniqueIdRepository getUniqueIdRepository() {
        return OpdLibrary.getInstance().getUniqueIdRepository();
    }


    public static Collection<JSONArray> extractRepeatingGroupsFields(JSONArray fields) {
        Map<String, JSONArray> fieldsMap = new HashMap<>();
        // JSONArray fields = form.getJSONObject("step1").getJSONArray("fields");

        HashSet<String> keys = new HashSet<>();
        for (int i = 0; i < fields.length(); i++) {
            JSONObject field = null;
            try {
                field = fields.getJSONObject(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                if (field.has(TYPE) && REPEATING_GROUP.equalsIgnoreCase(field.getString(TYPE))) {
                    JSONArray repeatingGroupValues = field.getJSONArray(VALUE);
                    for (int item = 0; item < repeatingGroupValues.length(); item++) {
                        keys.add(repeatingGroupValues.getJSONObject(item).getString(KEY));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        JSONArray repeatingGroupFieldValues = obtainRepeatingGroupFieldValues(fields, keys);

        for (int index = 0; index < repeatingGroupFieldValues.length(); index++) {
            JSONObject field = null;
            try {
                field = repeatingGroupFieldValues.getJSONObject(index);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String key = null;
            try {
                key = field.getString(KEY);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String uniqueId = key.substring(key.lastIndexOf("_"));
            fieldsMap.putIfAbsent(uniqueId, new JSONArray());
            try {
                fieldsMap.get(uniqueId).put(field.put(KEY, key.replaceAll(uniqueId, "")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return fieldsMap.values();

    }

    private static JSONArray obtainRepeatingGroupFieldValues(JSONArray fields, HashSet<String> keys) {

        JSONArray repeatingGroupFieldValues = new JSONArray();
        for (int fieldIndex = 0; fieldIndex < fields.length(); fieldIndex++) {
            JSONObject field = null;
            try {
                field = fields.getJSONObject(fieldIndex);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (field.has(KEY) && isRepeatingGroupField(field, keys)) {
                repeatingGroupFieldValues.put(field);
            }
        }
        return repeatingGroupFieldValues;
    }

    private static boolean isRepeatingGroupField(JSONObject field, HashSet<String> keys) {
        for (String key : keys) {
            try {
                if (field.getString(KEY).startsWith(key + "_")) {
                    return true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public FormTag getFormTag() {
        FormTag formTag = new FormTag();
        AllSharedPreferences allSharedPreferences = getAllSharedPreferences();
        formTag.providerId = allSharedPreferences.fetchRegisteredANM();
        formTag.appVersion = BuildConfig.VERSION_CODE;
        formTag.databaseVersion = BuildConfig.DATABASE_VERSION;
        return formTag;
    }

    public AllSharedPreferences getAllSharedPreferences() {
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
