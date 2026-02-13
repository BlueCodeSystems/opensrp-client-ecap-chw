package com.bluecodeltd.ecap.chw.util;

import android.content.Context;
import android.text.TextUtils;

import com.bluecodeltd.ecap.chw.model.Household;
import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.json.JSONArray;
import org.json.JSONObject;
import org.smartregister.util.FormUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import timber.log.Timber;

public final class PmtctEnrollmentUtils {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private PmtctEnrollmentUtils() {
    }

    public static boolean isEligible(JSONObject serviceForm) {
        String hivStatus = getFieldValue(serviceForm, "is_hiv_positive");
        String breastfeeding = getFieldValue(serviceForm, "pregnant_breastfeeding");
        return "positive".equalsIgnoreCase(safe(hivStatus)) && "yes".equalsIgnoreCase(safe(breastfeeding));
    }

    public static String resolveServiceDate(JSONObject serviceForm) {
        return getFieldValue(serviceForm, "date");
    }

    public static JSONObject buildMotherPmtctForm(Context context, Household household, String serviceDate) {
        if (context == null || household == null) {
            return null;
        }
        try {
            FormUtils formUtils = new FormUtils(context);
            JSONObject form = formUtils.getFormJson("mother_pmtct");
            if (form == null) {
                return null;
            }

            String householdId = safe(household.getHousehold_id());
            if (!TextUtils.isEmpty(householdId)) {
                setFieldValue(form, "household_id", householdId);
                setFieldValue(form, "pmtct_id", householdId);
            }

            setFieldValue(form, "caregiver_name", household.getCaregiver_name());
            setFieldValue(form, "caregiver_birth_date", household.getCaregiver_birth_date());
            setFieldValue(form, "first_name", household.getFirst_name());
            setFieldValue(form, "last_name", household.getLast_name());
            setFieldValue(form, "district", household.getDistrict());
            setFieldValue(form, "ward", household.getWard());
            setFieldValue(form, "facility", household.getFacility());
            setFieldValue(form, "partner", household.getPartner());
            setFieldValue(form, "caseworker_name", household.getCaseworker_name());
            setFieldValue(form, "home_address", household.getHomeaddress());
            setFieldValue(form, "nearest_landmark", household.getLandmark());

            String phone = safe(household.getCaregiver_phone());
            if (TextUtils.isEmpty(phone)) {
                phone = safe(household.getPhone());
            }
            setFieldValue(form, "mothers_phone", phone);

            String enrolledDate = safe(serviceDate);
            if (TextUtils.isEmpty(enrolledDate)) {
                enrolledDate = LocalDate.now().format(DATE_FORMAT);
            }
            setFieldValue(form, "date_enrolled_pmtct", enrolledDate);

            return form;
        } catch (Exception e) {
            Timber.e(e);
            return null;
        }
    }

    public static void alignPmtctIdWithHouseholdId(JSONArray fields) {
        if (fields == null) {
            return;
        }
        try {
            String householdId = getFieldValue(fields, "household_id");
            if (TextUtils.isEmpty(householdId)) {
                return;
            }
            setFieldValue(fields, "pmtct_id", householdId);
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    public static String getFieldValue(JSONObject form, String key) {
        if (form == null || TextUtils.isEmpty(key)) {
            return null;
        }
        try {
            JSONObject step = form.optJSONObject(JsonFormConstants.STEP1);
            if (step == null) {
                return null;
            }
            JSONArray fields = step.optJSONArray(JsonFormConstants.FIELDS);
            if (fields == null) {
                return null;
            }
            for (int i = 0; i < fields.length(); i++) {
                JSONObject field = fields.optJSONObject(i);
                if (field != null && key.equals(field.optString(JsonFormConstants.KEY))) {
                    return field.optString(JsonFormConstants.VALUE, null);
                }
            }
        } catch (Exception e) {
            Timber.e(e);
        }
        return null;
    }

    private static String getFieldValue(JSONArray fields, String key) {
        if (fields == null || TextUtils.isEmpty(key)) {
            return null;
        }
        try {
            for (int i = 0; i < fields.length(); i++) {
                JSONObject field = fields.optJSONObject(i);
                if (field != null && key.equals(field.optString(JsonFormConstants.KEY))) {
                    return field.optString(JsonFormConstants.VALUE, null);
                }
            }
        } catch (Exception e) {
            Timber.e(e);
        }
        return null;
    }

    private static void setFieldValue(JSONObject form, String key, String value) {
        if (form == null || TextUtils.isEmpty(key) || TextUtils.isEmpty(value)) {
            return;
        }
        try {
            JSONObject step = form.optJSONObject(JsonFormConstants.STEP1);
            if (step == null) {
                return;
            }
            JSONArray fields = step.optJSONArray(JsonFormConstants.FIELDS);
            if (fields == null) {
                return;
            }
            for (int i = 0; i < fields.length(); i++) {
                JSONObject field = fields.optJSONObject(i);
                if (field != null && key.equals(field.optString(JsonFormConstants.KEY))) {
                    field.put(JsonFormConstants.VALUE, value);
                    return;
                }
            }
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    private static void setFieldValue(JSONArray fields, String key, String value) {
        if (fields == null || TextUtils.isEmpty(key) || TextUtils.isEmpty(value)) {
            return;
        }
        try {
            for (int i = 0; i < fields.length(); i++) {
                JSONObject field = fields.optJSONObject(i);
                if (field != null && key.equals(field.optString(JsonFormConstants.KEY))) {
                    field.put(JsonFormConstants.VALUE, value);
                    return;
                }
            }
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    private static String safe(String value) {
        return value == null ? "" : value.trim();
    }
}
