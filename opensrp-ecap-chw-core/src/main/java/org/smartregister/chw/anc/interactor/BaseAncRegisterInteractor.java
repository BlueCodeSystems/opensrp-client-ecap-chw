package org.smartregister.chw.anc.interactor;

import static org.smartregister.AllConstants.Immunizations.BCG;
import static org.smartregister.AllConstants.Immunizations.OPV_0;
import static org.smartregister.chw.anc.util.Constants.JsonFormKey.BCG_DATE;
import static org.smartregister.chw.anc.util.Constants.JsonFormKey.CHK_BCG;
import static org.smartregister.chw.anc.util.Constants.JsonFormKey.OPV_0_DATE;
import static org.smartregister.chw.anc.util.Constants.TABLES.EC_CHILD;
import static org.smartregister.util.JsonFormUtils.VALUE;
import static org.smartregister.util.JsonFormUtils.getFieldJSONObject;

import androidx.annotation.VisibleForTesting;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.AllConstants;
import org.smartregister.Context;
import org.smartregister.chw.anc.AncLibrary;
import org.smartregister.chw.anc.contract.BaseAncRegisterContract;
import org.smartregister.chw.anc.model.BaseAncRegisterModel;
import org.smartregister.chw.anc.util.AppExecutors;
import org.smartregister.chw.anc.util.Constants;
import org.smartregister.chw.anc.util.DBConstants;
import org.smartregister.chw.anc.util.JsonFormUtils;
import org.smartregister.chw.anc.util.NCUtils;
import org.smartregister.chw.anc.util.VisitUtils;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.immunization.ImmunizationLibrary;
import org.smartregister.repository.AllSharedPreferences;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import timber.log.Timber;

public class BaseAncRegisterInteractor implements BaseAncRegisterContract.Interactor {

    protected AppExecutors appExecutors;
    protected BaseAncRegisterContract.Model model;

    @VisibleForTesting
    BaseAncRegisterInteractor(AppExecutors appExecutors) {
        this.appExecutors = appExecutors;
    }

    public BaseAncRegisterInteractor() {
        this(new AppExecutors());
    }

    public static void saveVaccineEvents(JSONArray fields, String baseId, String dob) {

        JSONObject vaccinesAtBirthObject = org.smartregister.util.JsonFormUtils.getFieldJSONObject(fields, DBConstants.KEY.VACCINES_AT_BIRTH);
        JSONArray vaccinesAtBirthArray = vaccinesAtBirthObject != null ? vaccinesAtBirthObject.optJSONArray(DBConstants.KEY.OPTIONS) : null;

        if (vaccinesAtBirthArray != null) {
            for (int i = 0; i < vaccinesAtBirthArray.length(); i++) {
                JSONObject currentVaccine = vaccinesAtBirthArray.optJSONObject(i);
                if (currentVaccine != null && currentVaccine.optBoolean(JsonFormUtils.VALUE) && !currentVaccine.optString(JsonFormUtils.KEY).equals("chk_none")) {
                    String vaccineName = currentVaccine.optString(JsonFormUtils.KEY).equals(CHK_BCG) ? BCG : OPV_0;
                    VisitUtils.savePncChildVaccines(vaccineName, baseId, vaccinationDate(dob));
                }
            }

        } else {
            saveVaccines(fields, baseId);
        }
    }

    public static Date vaccinationDate(String vaccineDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        try {
            return formatter.parse(vaccineDate);
        } catch (ParseException e) {
            Timber.e(e);
        }
        return null;
    }

    private static void saveVaccines(JSONArray fields, String baseId) {
        String[] vaccineKeys = {BCG_DATE, OPV_0_DATE};
        for (int i = 0; i < vaccineKeys.length; i++) {
            try {
                String vaccineDate = getFieldJSONObject(fields, vaccineKeys[i]).optString(VALUE);
                if (StringUtils.isNotBlank(vaccineDate)) {
                    Date dateVaccinated = vaccinationDate(vaccineDate);
                    String vaccineName = vaccineKeys[i].equals(BCG_DATE) ? BCG : OPV_0;
                    VisitUtils.savePncChildVaccines(vaccineName, baseId, dateVaccinated);
                }
            } catch (NullPointerException e) {
                Timber.e(e);
            }
        }
    }

    public BaseAncRegisterContract.Model getModel() {
        if (model == null)
            model = new BaseAncRegisterModel();
        return model;
    }

    @Override
    public void setModel(BaseAncRegisterContract.Model model) {
        if (model != null)
            this.model = model;
    }

    @Override
    public void onDestroy(boolean isChangingConfiguration) {
        Timber.v("Empty onDestroy");
    }

    @Override
    public void saveRegistration(final String jsonString, final boolean isEditMode, final BaseAncRegisterContract.InteractorCallBack callBack, final String table) {

        Runnable runnable = () -> {
            // save it
            String encounterType = "";
            boolean hasChildren = false;

            try {
                JSONObject form = new JSONObject(jsonString);
                encounterType = form.optString(Constants.JSON_FORM_EXTRA.ENCOUNTER_TYPE);

                if (encounterType.equalsIgnoreCase(Constants.EVENT_TYPE.PREGNANCY_OUTCOME)) {

                    saveRegistration(form.toString(), table, Constants.EVENT_TYPE.PREGNANCY_OUTCOME);

                    String motherBaseId = form.optString(Constants.JSON_FORM_EXTRA.ENTITY_TYPE);
                    JSONArray fields = org.smartregister.util.JsonFormUtils.fields(form);
                    JSONObject deliveryDate = org.smartregister.util.JsonFormUtils.getFieldJSONObject(fields, DBConstants.KEY.DELIVERY_DATE);
                    JSONObject famNameObject = org.smartregister.util.JsonFormUtils.getFieldJSONObject(fields, DBConstants.KEY.FAM_NAME);

                    String familyName = famNameObject != null ? famNameObject.optString(JsonFormUtils.VALUE) : "";
                    String dob = deliveryDate.optString(JsonFormUtils.VALUE);
                    hasChildren = StringUtils.isNotBlank(deliveryDate.optString(JsonFormUtils.VALUE));

                    JSONObject familyIdObject = org.smartregister.util.JsonFormUtils.getFieldJSONObject(fields, DBConstants.KEY.RELATIONAL_ID);
                    String familyBaseEntityId = familyIdObject.getString(JsonFormUtils.VALUE);

                    Map<String, List<JSONObject>> jsonObjectMap = getChildFieldMaps(fields);

                    generateAndSaveFormsForEachChild(jsonObjectMap, motherBaseId, familyBaseEntityId, dob, familyName);

                } else if (encounterType.equalsIgnoreCase(Constants.EVENT_TYPE.ANC_REGISTRATION)) {

                    JSONArray fields = org.smartregister.util.JsonFormUtils.fields(form);
                    JSONObject lmp = org.smartregister.util.JsonFormUtils.getFieldJSONObject(fields, DBConstants.KEY.LAST_MENSTRUAL_PERIOD);
                    boolean hasLmp = StringUtils.isNotBlank(lmp.optString(JsonFormUtils.VALUE));

                    if (!hasLmp) {
                        JSONObject eddJson = org.smartregister.util.JsonFormUtils.getFieldJSONObject(fields, DBConstants.KEY.EDD);
                        DateTimeFormatter dateTimeFormat = DateTimeFormat.forPattern("dd-MM-yyyy");

                        LocalDate lmpDate = dateTimeFormat.parseLocalDate(eddJson.optString(JsonFormUtils.VALUE)).plusDays(-280);
                        lmp.put(JsonFormUtils.VALUE, dateTimeFormat.print(lmpDate));
                    }

                    saveRegistration(form.toString(), table);
                } else {
                    saveRegistration(jsonString, table);
                }
            } catch (Exception e) {
                Timber.e(e);
            }

            String finalEncounterType = encounterType;
            boolean finalHasChildren = hasChildren;
            appExecutors.mainThread().execute(() -> {
                try {
                    callBack.onRegistrationSaved(finalEncounterType, isEditMode, finalHasChildren);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        };
        appExecutors.diskIO().execute(runnable);
    }

    protected void generateAndSaveFormsForEachChild(Map<String, List<JSONObject>> jsonObjectMap, String motherBaseId, String familyBaseEntityId, String dob, String familyName) {

        AllSharedPreferences allSharedPreferences = ImmunizationLibrary.getInstance().context().allSharedPreferences();

        JSONArray childFields;
        for (Map.Entry<String, List<JSONObject>> entry : jsonObjectMap.entrySet()) {
            if (entry.getValue().size() > 1) {
                childFields = new JSONArray();
                for (JSONObject jsonObject : entry.getValue()) {
                    try {
                        String replaceString = jsonObject.getString(JsonFormUtils.KEY);

                        JSONObject childField = new JSONObject(jsonObject.toString().replaceAll(replaceString, replaceString.substring(0, replaceString.lastIndexOf("_"))));

                        childFields.put(childField);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                saveChild(childFields, motherBaseId, allSharedPreferences, familyBaseEntityId, dob, familyName);
            }
        }
    }

    private Map<String, List<JSONObject>> getChildFieldMaps(JSONArray fields) {
        Map<String, List<JSONObject>> jsonObjectMap = new HashMap();

        for (int i = 0; i < fields.length(); i++) {
            try {
                JSONObject jsonObject = fields.getJSONObject(i);
                String key = jsonObject.getString(JsonFormUtils.KEY);
                String keySplit = key.substring(key.lastIndexOf("_"));
                if (keySplit.matches(".*\\d.*")) {

                    String formattedKey = keySplit.replaceAll("[^\\d.]", "");
                    if (formattedKey.length() < 10)
                        continue;
                    List<JSONObject> jsonObjectList = jsonObjectMap.get(formattedKey);

                    if (jsonObjectList == null)
                        jsonObjectList = new ArrayList<>();

                    jsonObjectList.add(jsonObject);
                    jsonObjectMap.put(formattedKey, jsonObjectList);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jsonObjectMap;
    }

    private void saveChild(JSONArray childFields, String motherBaseId, AllSharedPreferences
            allSharedPreferences, String familyBaseEntityId, String dob, String familyName) {
        String uniqueChildID = AncLibrary.getInstance().getUniqueIdRepository().getNextUniqueId().getOpenmrsId();

        if (StringUtils.isNotBlank(uniqueChildID)) {
            String childBaseEntityId = JsonFormUtils.generateRandomUUIDString();
            try {

                JSONObject surNameObject = org.smartregister.util.JsonFormUtils.getFieldJSONObject(childFields, DBConstants.KEY.SUR_NAME);
                String surName = surNameObject != null ? surNameObject.optString(JsonFormUtils.VALUE) : null;

                String lastName = sameASFamilyNameCheck(childFields) ? familyName : surName;
                JSONObject pncForm = getModel().getFormAsJson(
                        AncLibrary.getInstance().context().applicationContext(),
                        Constants.FORMS.PNC_CHILD_REGISTRATION,
                        childBaseEntityId,
                        getLocationID()
                );
                pncForm = JsonFormUtils.populatePNCForm(pncForm, childFields, familyBaseEntityId, motherBaseId, uniqueChildID, dob, lastName);
                processPncChild(childFields, allSharedPreferences, childBaseEntityId, familyBaseEntityId, motherBaseId, uniqueChildID, lastName, dob);
                if (pncForm != null) {
                    saveRegistration(pncForm.toString(), EC_CHILD);
                    saveVaccineEvents(childFields, childBaseEntityId, dob);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private boolean sameASFamilyNameCheck(JSONArray childFields) {
        if (childFields.length() > 0) {
            JSONObject sameAsFamNameCheck = org.smartregister.util.JsonFormUtils.getFieldJSONObject(childFields, DBConstants.KEY.SAME_AS_FAM_NAME_CHK);
            sameAsFamNameCheck = sameAsFamNameCheck != null ? sameAsFamNameCheck : org.smartregister.util.JsonFormUtils.getFieldJSONObject(childFields, DBConstants.KEY.SAME_AS_FAM_NAME);
            JSONObject sameAsFamNameObject = sameAsFamNameCheck.optJSONArray(DBConstants.KEY.OPTIONS).optJSONObject(0);
            if (sameAsFamNameCheck != null) {
                return sameAsFamNameObject.optBoolean(JsonFormUtils.VALUE);
            }
        }
        return false;
    }

    protected String getLocationID() {
        return Context.getInstance().allSharedPreferences().getPreference(AllConstants.CURRENT_LOCATION_ID);
    }

    private void saveRegistration(final String jsonString, String table) throws Exception {
        saveRegistration(jsonString, table, null);
    }

    private void saveRegistration(final String jsonString, String table, String encounterType) throws Exception {
        AllSharedPreferences allSharedPreferences = AncLibrary.getInstance().context().allSharedPreferences();
        Event baseEvent = JsonFormUtils.processJsonForm(allSharedPreferences, jsonString, table);

        if (Constants.EVENT_TYPE.PREGNANCY_OUTCOME.equals(encounterType)) {
            JsonFormUtils.updatePregnancyOutcomeEventObs(jsonString, baseEvent);
        }
        NCUtils.addEvent(allSharedPreferences, baseEvent);
        NCUtils.startClientProcessing();
    }

    public void processPncChild(JSONArray fields, AllSharedPreferences allSharedPreferences, String entityId, String familyBaseEntityId, String motherBaseId, String uniqueChildID, String lastName, String dob) {
        try {
            Client pncChild = org.smartregister.util.JsonFormUtils.createBaseClient(fields, JsonFormUtils.formTag(allSharedPreferences), entityId);
            Map<String, String> identifiers = new HashMap<>();
            identifiers.put(Constants.JSON_FORM_EXTRA.OPENSPR_ID, uniqueChildID.replace("-", ""));
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
            Date date = formatter.parse(dob);
            pncChild.setLastName(lastName);
            pncChild.setBirthdate(date);
            pncChild.setIdentifiers(identifiers);
            pncChild.addRelationship(Constants.RELATIONSHIP.FAMILY, familyBaseEntityId);
            pncChild.addRelationship(Constants.RELATIONSHIP.MOTHER, motherBaseId);

            JSONObject eventJson = new JSONObject(JsonFormUtils.gson.toJson(pncChild));
            AncLibrary.getInstance().getUniqueIdRepository().close(pncChild.getIdentifier(Constants.JSON_FORM_EXTRA.OPENSPR_ID));

            NCUtils.getSyncHelper().addClient(pncChild.getBaseEntityId(), eventJson);

        } catch (Exception e) {
            Timber.e(e);
        }
    }
}




