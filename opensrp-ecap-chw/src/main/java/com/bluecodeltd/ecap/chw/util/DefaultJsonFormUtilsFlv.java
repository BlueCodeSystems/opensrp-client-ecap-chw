package com.bluecodeltd.ecap.chw.util;

import android.content.Context;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.bluecodeltd.ecap.chw.application.ChwApplication;
import org.smartregister.chw.core.form_data.NativeFormsDataBinder;
import org.smartregister.chw.core.utils.ChwDBConstants;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.clientandeventmodel.Obs;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.Photo;
import org.smartregister.family.util.DBConstants;
import org.smartregister.util.ImageUtils;

import java.util.Date;
import java.util.HashMap;

import timber.log.Timber;

public abstract class DefaultJsonFormUtilsFlv implements JsonFormUtils.Flavor {
    public static final String TITLE = "title";
    private HashMap<String, String> JSON_DB_MAP;

    public DefaultJsonFormUtilsFlv() {
        JSON_DB_MAP = new HashMap<>();
        JSON_DB_MAP.put(Constants.JsonAssets.SEX, DBConstants.KEY.GENDER);
        JSON_DB_MAP.put(Constants.JsonAssets.NATIONAL_ID, Constants.JsonAssets.NATIONAL_ID);
        JSON_DB_MAP.put(Constants.JsonAssets.VOTER_ID, ChwDBConstants.VOTER_ID);
        JSON_DB_MAP.put(Constants.JsonAssets.DRIVER_LICENSE, ChwDBConstants.DRIVER_LICENSE);
        JSON_DB_MAP.put(Constants.JsonAssets.PASSPORT, ChwDBConstants.PASSPORT);
        JSON_DB_MAP.put(Constants.JsonAssets.INSURANCE_PROVIDER, ChwDBConstants.INSURANCE_PROVIDER);
        JSON_DB_MAP.put(Constants.JsonAssets.INSURANCE_PROVIDER_OTHER, ChwDBConstants.INSURANCE_PROVIDER_OTHER);
        JSON_DB_MAP.put(Constants.JsonAssets.INSURANCE_PROVIDER_NUMBER, ChwDBConstants.INSURANCE_PROVIDER_NUMBER);
        JSON_DB_MAP.put(Constants.JsonAssets.DISABILITIES, ChwDBConstants.DISABILITIES);
        JSON_DB_MAP.put(Constants.JsonAssets.DISABILITY_TYPE, ChwDBConstants.DISABILITY_TYPE);
        JSON_DB_MAP.put(Constants.JsonAssets.OTHER_LEADER, ChwDBConstants.OTHER_LEADER);
    }

    @Override
    public JSONObject getAutoJsonEditMemberFormString(String title, String formName, Context context, CommonPersonObjectClient client, String eventType, String familyName, boolean isPrimaryCaregiver) {
        try {

            NativeFormsDataBinder binder = new NativeFormsDataBinder(context, client.getCaseId());
            //FamilyMemberDataLoader dataLoader = new FamilyMemberDataLoader(familyName, isPrimaryCaregiver);
            // binder.setDataLoader(dataLoader);

            JSONObject jsonObject = binder.getPrePopulatedForm(CoreConstants.JSON_FORM.getFamilyMemberRegister());
            if (jsonObject != null)
                //dataLoader.bindFormData(jsonObject, client, eventType, title);

                return jsonObject;
        } catch (Exception e) {
            Timber.e(e);
        }

        return null;
    }

    @Override
    public void processFieldsForMemberEdit(CommonPersonObjectClient client, JSONObject jsonObject, JSONArray jsonArray, String familyName, boolean isPrimaryCaregiver, Event ecEvent, Client ecClient) throws JSONException {


        switch (jsonObject.getString(org.smartregister.family.util.JsonFormUtils.KEY).toLowerCase()) {
            case org.smartregister.family.util.Constants.JSON_FORM_KEY.DOB_UNKNOWN:
                computeDOBUnknown(jsonObject, client);
                break;

            case Constants.JsonAssets.AGE:
                computeAge(jsonObject, client);
                break;

            case DBConstants.KEY.DOB:
                computeDOB(jsonObject, client);
                break;

            case org.smartregister.family.util.Constants.KEY.PHOTO:
                computePhoto(jsonObject, client);
                break;

            case DBConstants.KEY.UNIQUE_ID:
                computeID(jsonObject, client);
                break;

            case Constants.JsonAssets.PREGNANT_1_YR:
                computePregnantOneYr(jsonObject, ecEvent);
                break;

            case Constants.JsonAssets.FAM_NAME:
                computeFamName(client, jsonObject, jsonArray, familyName);
                break;

            case Constants.JsonAssets.PRIMARY_CARE_GIVER:
            case Constants.JsonAssets.IS_PRIMARY_CARE_GIVER:
                computePrimaryCareGiver(jsonObject, isPrimaryCaregiver);
                break;

            case Constants.JsonAssets.SERVICE_PROVIDER:
                computeServiceProvider(jsonObject, ecEvent);
                break;

            default:
                String db_key = JSON_DB_MAP.get(jsonObject.getString(org.smartregister.family.util.JsonFormUtils.KEY).toLowerCase());
                if (StringUtils.isNotBlank(db_key)) {
                    jsonObject.put(org.smartregister.family.util.JsonFormUtils.VALUE, Utils.getValue(client.getColumnmaps(), db_key, false));
                } else {
                    jsonObject.put(org.smartregister.family.util.JsonFormUtils.VALUE, Utils.getValue(client.getColumnmaps(), jsonObject.getString(org.smartregister.family.util.JsonFormUtils.KEY), false));
                }

                break;

        }
    }

    private void computeDOBUnknown(JSONObject jsonObject, CommonPersonObjectClient client) throws JSONException {
        jsonObject.put(org.smartregister.family.util.JsonFormUtils.READ_ONLY, false);
        JSONObject optionsObject = jsonObject.getJSONArray(org.smartregister.family.util.Constants.JSON_FORM_KEY.OPTIONS).getJSONObject(0);
        optionsObject.put(org.smartregister.family.util.JsonFormUtils.VALUE, Utils.getValue(client.getColumnmaps(), org.smartregister.family.util.Constants.JSON_FORM_KEY.DOB_UNKNOWN, false));
    }

    private void computeAge(JSONObject jsonObject, CommonPersonObjectClient client) throws JSONException {
        String dobString = Utils.getValue(client.getColumnmaps(), DBConstants.KEY.DOB, false);
        dobString = Utils.getDuration(dobString);
        dobString = dobString.contains("y") ? dobString.substring(0, dobString.indexOf("y")) : "0";
        jsonObject.put(org.smartregister.family.util.JsonFormUtils.VALUE, Integer.valueOf(dobString));
    }

    private void computeDOB(JSONObject jsonObject, CommonPersonObjectClient client) throws JSONException {
        String dobString = Utils.getValue(client.getColumnmaps(), DBConstants.KEY.DOB, false);
        if (StringUtils.isNotBlank(dobString)) {
            Date dob = Utils.dobStringToDate(dobString);
            if (dob != null) {
                jsonObject.put(org.smartregister.family.util.JsonFormUtils.VALUE, JsonFormUtils.dd_MM_yyyy.format(dob));
            }
        }
    }

    private void computePhoto(JSONObject jsonObject, CommonPersonObjectClient client) throws JSONException {
        Photo photo = ImageUtils.profilePhotoByClientID(client.getCaseId(), Utils.getProfileImageResourceIDentifier());
        if (StringUtils.isNotBlank(photo.getFilePath())) {
            jsonObject.put(org.smartregister.family.util.JsonFormUtils.VALUE, photo.getFilePath());
        }
    }

    private void computeID(JSONObject jsonObject, CommonPersonObjectClient client) throws JSONException {
        String uniqueId = Utils.getValue(client.getColumnmaps(), DBConstants.KEY.UNIQUE_ID, false);
        jsonObject.put(org.smartregister.family.util.JsonFormUtils.VALUE, uniqueId.replace("-", ""));
    }

    private void computePregnantOneYr(JSONObject jsonObject, Event ecEvent) throws JSONException {
        if (ecEvent != null) {
            String id = jsonObject.getString(JsonFormConstants.OPENMRS_ENTITY_ID);
            for (Obs obs : ecEvent.getObs()) {
                if (obs.getValues() != null && obs.getFieldCode().contains(id)) {
                    jsonObject.put(org.smartregister.family.util.JsonFormUtils.VALUE, obs.getHumanReadableValues().get(0));
                }
            }
        }
    }

    private void computeFamName(CommonPersonObjectClient client, JSONObject jsonObject, JSONArray jsonArray, String familyName) throws JSONException {
        final String SAME_AS_FAM_NAME = "same_as_fam_name";
        final String SURNAME = "surname";

        jsonObject.put(org.smartregister.family.util.JsonFormUtils.VALUE, familyName);

        if (ChwApplication.getApplicationFlavor().hasSurname()) {
            String lastName = Utils.getValue(client.getColumnmaps(), DBConstants.KEY.LAST_NAME, false);

            JSONObject sameAsFamName = org.smartregister.util.JsonFormUtils.getFieldJSONObject(jsonArray, SAME_AS_FAM_NAME);
            JSONObject sameOptions = sameAsFamName.getJSONArray(org.smartregister.family.util.Constants.JSON_FORM_KEY.OPTIONS).getJSONObject(0);

            if (familyName.equals(lastName)) {
                sameOptions.put(org.smartregister.family.util.JsonFormUtils.VALUE, true);
            } else {
                sameOptions.put(org.smartregister.family.util.JsonFormUtils.VALUE, false);
            }

            JSONObject surname = org.smartregister.util.JsonFormUtils.getFieldJSONObject(jsonArray, SURNAME);
            if (!familyName.equals(lastName)) {
                surname.put(org.smartregister.family.util.JsonFormUtils.VALUE, lastName);
            } else {
                surname.put(org.smartregister.family.util.JsonFormUtils.VALUE, "");
            }
        }
    }

    private void computePrimaryCareGiver(JSONObject jsonObject, boolean isPrimaryCaregiver) throws JSONException {
        jsonObject.put(org.smartregister.family.util.JsonFormUtils.READ_ONLY, true);
        if (isPrimaryCaregiver) {
            jsonObject.put(org.smartregister.family.util.JsonFormUtils.VALUE, "Yes");
        } else {
            jsonObject.put(org.smartregister.family.util.JsonFormUtils.VALUE, "No");
        }
    }

    private void computeServiceProvider(JSONObject jsonObject, Event ecEvent) throws JSONException {

        // iterate and update all options wit the values from ec object
        if (ecEvent != null) {
            for (int i = 0; i < jsonObject.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME).length(); i++) {
                JSONObject obj = jsonObject.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME).getJSONObject(i);
                String id = obj.getString(JsonFormConstants.OPENMRS_ENTITY_ID);
                for (Obs obs : ecEvent.getObs()) {
                    if (obs.getValues() != null && obs.getValues().contains(id)) {
                        obj.put(JsonFormConstants.VALUE, true);
                    }
                }
            }
        }
    }

}
