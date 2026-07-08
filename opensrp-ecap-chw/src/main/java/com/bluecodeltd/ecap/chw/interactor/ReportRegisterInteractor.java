package com.bluecodeltd.ecap.chw.interactor;

import com.bluecodeltd.ecap.chw.application.ChwApplication;
import com.bluecodeltd.ecap.chw.util.Constants;

import org.json.JSONArray;
import org.json.JSONObject;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.domain.db.EventClient;
import org.smartregister.domain.tag.FormTag;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.util.JsonFormUtils;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import timber.log.Timber;

public class ReportRegisterInteractor {

    public void saveForm(String jsonString, String status) {
        try {
            JSONObject jsonForm = new JSONObject(jsonString);
            
            // Flatten all steps into one fields array for Event creation
            JSONArray allFields = new JSONArray();
            int stepCount = jsonForm.optInt("count", 1);
            boolean isDraft = false;

            for (int i = 1; i <= stepCount; i++) {
                String stepName = "step" + i;
                if (jsonForm.has(stepName)) {
                    JSONArray stepFields = jsonForm.getJSONObject(stepName).getJSONArray("fields");
                    for (int j = 0; j < stepFields.length(); j++) {
                        JSONObject field = stepFields.getJSONObject(j);
                        
                        // Ensure all fields are treated as concepts for the Event
                        String entityType = field.optString("openmrs_entity");
                        if (!field.has("openmrs_entity") || entityType.isEmpty() || "person_attribute".equals(entityType)) {
                            field.put("openmrs_entity", "concept");
                            field.put("openmrs_entity_id", field.optString("key"));
                        }

                        allFields.put(field);
                        if ("save_draft".equals(field.optString("key")) && field.optBoolean("value")) {
                            isDraft = true;
                        }
                    }
                }
            }

            // Determine final status
            String finalStatus = isDraft ? "draft" : (status != null ? status : "complete");

            // Add status field to observations
            JSONObject statusField = new JSONObject();
            statusField.put("key", "report_status");
            statusField.put("value", finalStatus);
            allFields.put(statusField);

            JSONObject metadata = jsonForm.getJSONObject(Constants.METADATA);
            String encounterType = jsonForm.getString(com.bluecodeltd.ecap.chw.util.JsonFormUtils.ENCOUNTER_TYPE);
            
            String entityId = jsonForm.optString(com.bluecodeltd.ecap.chw.util.JsonFormUtils.ENTITY_ID);
            if (entityId == null || entityId.isEmpty()) {
                entityId = JsonFormUtils.generateRandomUUIDString();
            }

            AllSharedPreferences sharedPreferences = ChwApplication.getInstance().getContext().allSharedPreferences();
            
            FormTag formTag = new FormTag();
            formTag.providerId = sharedPreferences.fetchRegisteredANM();
            formTag.appVersion = ChwApplication.getInstance().getContext().applicationContext().getPackageManager().getPackageInfo(ChwApplication.getInstance().getContext().applicationContext().getPackageName(), 0).versionCode;
            formTag.databaseVersion = ChwApplication.getInstance().getRepository().getReadableDatabase().getVersion();

            // Create Event with ALL fields from ALL steps
            Event event = JsonFormUtils.createEvent(allFields, metadata, formTag, entityId, encounterType, getTableName(encounterType));
            if (event.getFormSubmissionId() == null) {
                event.setFormSubmissionId(JsonFormUtils.generateRandomUUIDString());
            }
            
            com.bluecodeltd.ecap.chw.util.JsonFormUtils.tagSyncMetadata(sharedPreferences, event);

            if (event != null) {
                JSONObject eventJson = new JSONObject(JsonFormUtils.gson.toJson(event));
                ChwApplication.getInstance().getEcSyncHelper().addEvent(event.getBaseEntityId(), eventJson);
                
                // Create a dummy client for the report to ensure it's processed correctly into the EC table
                org.smartregister.clientandeventmodel.Client client = new org.smartregister.clientandeventmodel.Client(entityId);
                client.setFirstName("Monthly Report");
                client.setLastName(encounterType);
                JSONObject clientJson = new JSONObject(JsonFormUtils.gson.toJson(client));
                ChwApplication.getInstance().getEcSyncHelper().addClient(entityId, clientJson);

                // Process locally to update custom tables immediately
                List<EventClient> eventClients = ChwApplication.getInstance().getEcSyncHelper().getEvents(Collections.singletonList(event.getFormSubmissionId()));
                Timber.d("Processing %d event-clients for formSubmissionId: %s", eventClients.size(), event.getFormSubmissionId());

                ChwApplication.getClientProcessor(ChwApplication.getInstance().getContext().applicationContext())
                        .processClient(eventClients);
            }

        } catch (Exception e) {
            Timber.e(e);
        }
    }

    private String getTableName(String encounterType) {
        if (Constants.EcapEncounterType.MALARIA_MONTHLY_REPORTING.equals(encounterType)) return Constants.EcapClientTable.EC_MONTHLY_MALARIA;
        if (Constants.EcapEncounterType.TB_MONTHLY_REPORTING.equals(encounterType)) return Constants.EcapClientTable.EC_MONTHLY_TB;
        if (Constants.EcapEncounterType.NUTRITION_MONTHLY_REPORTING.equals(encounterType)) return Constants.EcapClientTable.EC_MONTHLY_NUTRITION;
        if (Constants.EcapEncounterType.COMMUNITY_ALERT_REPORT.equals(encounterType)) return Constants.EcapClientTable.EC_COMMUNITY_ALERT;
        return "";
    }
}
