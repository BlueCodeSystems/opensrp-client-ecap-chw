package org.smartregister.chw.core.utils;

import android.content.Context;
import android.content.Intent;
import android.util.Pair;

import androidx.annotation.NonNull;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.Form;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.Years;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.anc.util.JsonFormUtils;
import org.smartregister.chw.anc.util.NCUtils;
import org.smartregister.chw.core.R;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.clientandeventmodel.Obs;
import org.smartregister.family.activity.FamilyWizardFormActivity;
import org.smartregister.family.domain.FamilyEventClient;
import org.smartregister.family.domain.FamilyMetadata;
import org.smartregister.family.util.Utils;
import org.smartregister.repository.BaseRepository;
import org.smartregister.view.activity.BaseProfileActivity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

import static org.smartregister.chw.anc.util.NCUtils.getSyncHelper;
import static org.smartregister.chw.core.utils.CoreConstants.JSON_FORM.isMultiPartForm;
import static org.smartregister.util.JsonFormUtils.getFieldJSONObject;
import static org.smartregister.util.Utils.getAllSharedPreferences;

public class FormUtils {
    public static FamilyMetadata getFamilyMetadata(BaseProfileActivity baseProfileActivity, String defaultLocation, ArrayList<String> locationHierarchy, ArrayList<Pair<String, String>> locationFields) {
        FamilyMetadata metadata = new FamilyMetadata(FamilyWizardFormActivity.class, FamilyWizardFormActivity.class,
                baseProfileActivity.getClass(), CoreConstants.IDENTIFIER.UNIQUE_IDENTIFIER_KEY, false);
        metadata.updateFamilyRegister(CoreConstants.JSON_FORM.getFamilyRegister(), CoreConstants.TABLE_NAME.FAMILY,
                CoreConstants.EventType.FAMILY_REGISTRATION, CoreConstants.EventType.UPDATE_FAMILY_REGISTRATION,
                CoreConstants.CONFIGURATION.FAMILY_REGISTER, CoreConstants.RELATIONSHIP.FAMILY_HEAD, CoreConstants.RELATIONSHIP.PRIMARY_CAREGIVER);
        metadata.updateFamilyMemberRegister(CoreConstants.JSON_FORM.getFamilyMemberRegister(),
                CoreConstants.TABLE_NAME.FAMILY_MEMBER, CoreConstants.EventType.FAMILY_MEMBER_REGISTRATION,
                CoreConstants.EventType.UPDATE_FAMILY_MEMBER_REGISTRATION, CoreConstants.CONFIGURATION.FAMILY_MEMBER_REGISTER, CoreConstants.RELATIONSHIP.FAMILY);

        metadata.updateFamilyDueRegister(CoreConstants.TABLE_NAME.CHILD, Integer.MAX_VALUE, false);
        metadata.updateFamilyActivityRegister(CoreConstants.TABLE_NAME.CHILD_ACTIVITY, Integer.MAX_VALUE, false);
        metadata.updateFamilyOtherMemberRegister(CoreConstants.TABLE_NAME.FAMILY_MEMBER, Integer.MAX_VALUE, false);
        metadata.setDefaultLocation(defaultLocation);
        metadata.setLocationHierarchy(locationHierarchy);
        metadata.setLocationFields(locationFields);
        return metadata;
    }

    public static Intent getStartFormActivity(JSONObject jsonForm, String title, Context context) {
        Intent intent = new Intent(context, Utils.metadata().familyMemberFormActivity);
        intent.putExtra(org.smartregister.family.util.Constants.JSON_FORM_EXTRA.JSON, jsonForm.toString());
        Form form = new Form();
        form.setActionBarBackground(R.color.family_actionbar);
        form.setWizard(false);
        form.setHomeAsUpIndicator(R.mipmap.ic_cross_white);
        form.setSaveLabel(context.getResources().getString(R.string.submit));

        if (isMultiPartForm(jsonForm)) {
            form.setWizard(true);
            form.setNavigationBackground(R.color.family_navigation);
            form.setName(title);
            form.setNextLabel(context.getResources().getString(R.string.next));
            form.setPreviousLabel(context.getResources().getString(R.string.back));
        }
        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, form);
        return intent;

    }

    public static void updateWraForBA(FamilyEventClient familyEventClient) {
        Client client = familyEventClient.getClient();
        Event event = familyEventClient.getEvent();
        if (client != null && event != null && client.getGender().equalsIgnoreCase("female") && client.getBirthdate() != null) {
            DateTime date = new DateTime(client.getBirthdate());
            Years years = Years.yearsBetween(date.toLocalDate(), LocalDate.now());
            int age = years.getYears();
            if (age >= 10 && age <= 49) {
                List<Object> list = new ArrayList<>();
                list.add("true");
                event.addObs(new Obs("concept", "text", "162849AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "",
                        list, new ArrayList<>(), null, "wra"));
            }

        }
    }

    public static JSONObject populateStockEventForm(JSONObject form, JSONArray fields) {
        try {
            if (form != null) {
                JSONObject stepOne = form.getJSONObject(JsonFormUtils.STEP1);
                JSONArray jsonArray = stepOne.getJSONArray(JsonFormUtils.FIELDS);

                JSONObject preLoadObject;
                JSONObject jsonObject;

                for (int i = 0; i < jsonArray.length(); i++) {
                    jsonObject = jsonArray.getJSONObject(i);
                    preLoadObject = getFieldJSONObject(fields, jsonObject.optString(JsonFormUtils.KEY));
                    if (preLoadObject != null)
                        jsonObject.put(JsonFormUtils.VALUE, preLoadObject.opt(JsonFormUtils.VALUE));
                }

                return form;
            }
        } catch (Exception e) {
            Timber.e(e);
        }

        return null;
    }

    public static org.smartregister.util.FormUtils getFormUtils() {
        try {
            return org.smartregister.util.FormUtils.getInstance(Utils.context().applicationContext());
        } catch (Exception e) {
            Timber.e(e);
        }
        return null;
    }

    public static void updateFormField(JSONArray formFieldArrays, String formFieldKey, String updateValue) {
        if (updateValue != null) {
            JSONObject formObject = getFieldJSONObject(formFieldArrays, formFieldKey);
            if (formObject != null) {
                try {
                    formObject.remove(org.smartregister.util.JsonFormUtils.VALUE);
                    formObject.put(org.smartregister.util.JsonFormUtils.VALUE, updateValue);
                } catch (JSONException e) {
                    Timber.e(e);
                }
            }
        }
    }

    public static void updateFormField(@NonNull JSONArray jsonArray, @NonNull Map<String, String> values) throws JSONException {
        if (jsonArray == null || values == null)
            throw new IllegalArgumentException();

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject currJsonObject = JsonFormUtils.getJSONObject(jsonArray, i);
            String keyVal = JsonFormUtils.getString(currJsonObject, JsonFormUtils.KEY);

            if (keyVal != null && values.containsKey(keyVal)) {
                String updateValue = values.get(keyVal);

                currJsonObject.remove(org.smartregister.util.JsonFormUtils.VALUE);
                currJsonObject.put(org.smartregister.util.JsonFormUtils.VALUE, updateValue);
            }
        }
    }

    public static void processEvent() {
        try {
            long lastSyncTimeStamp = getAllSharedPreferences().fetchLastUpdatedAtDate(0);
            Date lastSyncDate = new Date(lastSyncTimeStamp);
            NCUtils.getClientProcessorForJava().processClient(getSyncHelper().getEvents(lastSyncDate, BaseRepository.TYPE_Unprocessed));
            getAllSharedPreferences().saveLastUpdatedAtDate(lastSyncDate.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
