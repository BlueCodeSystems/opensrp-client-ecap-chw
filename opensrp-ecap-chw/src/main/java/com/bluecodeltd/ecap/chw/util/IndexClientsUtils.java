package com.bluecodeltd.ecap.chw.util;

import static org.smartregister.opd.utils.OpdJsonFormUtils.tagSyncMetadata;

import com.bluecodeltd.ecap.chw.BuildConfig;
import com.bluecodeltd.ecap.chw.application.ChwApplication;
import com.bluecodeltd.ecap.chw.domain.ChildIndexEventClient;
import com.bluecodeltd.ecap.chw.model.EventClient;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.client.utils.constants.JsonFormConstants;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.domain.tag.FormTag;
import org.smartregister.family.domain.FamilyEventClient;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.family.util.Utils;
import org.smartregister.opd.pojo.OpdEventClient;
import org.smartregister.repository.AllSharedPreferences;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class IndexClientsUtils {

    private static final String REPEATING_GROUP = "repeating_group";

    @NotNull
    public static List<EventClient> getEventClients(String jsonString) {

        List<EventClient> eventClients = new ArrayList<>();


        try {
            JSONObject formJsonObject = new JSONObject(jsonString);
            String entityId = JsonFormUtils.generateRandomUUIDString();
            String encounterType = formJsonObject.getString(JsonFormConstants.ENCOUNTER_TYPE);
            JSONObject metadata = formJsonObject.getJSONObject(Constants.METADATA);


            JSONArray fields = JsonFormUtils.fields(formJsonObject);
            JSONArray clientFields = new JSONArray();

            //Collection<JSONArray> childrenArray = extractRepeatingGroupsFields(fields);

            //Remove Repeat Group from Parent Data

            for (int i = 0; i < fields.length(); i++) {

                JSONObject field = fields.getJSONObject(i);
                if (field.has(JsonFormConstants.TYPE) &&
                        !REPEATING_GROUP.equalsIgnoreCase(field.getString(JsonFormConstants.TYPE))) {
                    clientFields.put(field);
                }
            }

            //Process

            FormTag formTag = getFormTag();

            Event childEvent = JsonFormUtils.createEvent(clientFields, metadata, formTag, entityId,
                    encounterType, Constants.EcapClientTable.EC_MOTHER_INDEX);
            tagSyncMetadata(childEvent);

            Client childClient = JsonFormUtils.createBaseClient(clientFields, formTag, entityId);

            eventClients.add(new EventClient(childEvent, childClient));

            //Process Children (Each Child)

          /*  for (JSONArray childFields : childrenArray) {

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
            }*/


            return eventClients;


        } catch (JSONException e) {
            Timber.e(e);
        }

        return null;
    }

    public static FormTag getFormTag() {
        FormTag formTag = new FormTag();
        AllSharedPreferences allSharedPreferences = getAllSharedPreferences();
        formTag.providerId = allSharedPreferences.fetchRegisteredANM();
        formTag.appVersion = BuildConfig.VERSION_CODE;
        formTag.databaseVersion = BuildConfig.DATABASE_VERSION;
        return formTag;
    }

    public static AllSharedPreferences getAllSharedPreferences() {
        return ChwApplication.getInstance().getContext().allSharedPreferences();
    }

}
