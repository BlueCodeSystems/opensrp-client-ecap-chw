package com.bluecodeltd.ecap.chw.util;

import android.content.Context;
import android.text.TextUtils;

import com.bluecodeltd.ecap.chw.model.Household;
import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.json.JSONArray;
import org.json.JSONObject;
import org.smartregister.util.FormUtils;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

import timber.log.Timber;

public final class MotherIndexEnrollmentUtils {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private MotherIndexEnrollmentUtils() {
    }

    public static boolean isEligible(JSONObject serviceForm, Household household, String serviceDate) {
        if (serviceForm == null || household == null) {
            return false;
        }
        String hivStatus = PmtctEnrollmentUtils.getFieldValue(serviceForm, "is_hiv_positive");
        if (!"negative".equalsIgnoreCase(safe(hivStatus))) {
            return false;
        }
        String caregiverSex = safe(household.getCaregiver_sex());
        if (!"female".equalsIgnoreCase(caregiverSex)) {
            return false;
        }
        LocalDate birthDate = parseDateSafe(household.getCaregiver_birth_date());
        if (birthDate == null) {
            return false;
        }
        LocalDate referenceDate = parseDateSafe(serviceDate);
        if (referenceDate == null) {
            referenceDate = LocalDate.now();
        }
        int age = Period.between(birthDate, referenceDate).getYears();
        return age >= 10 && age <= 25;
    }

    public static JSONObject buildMotherIndexForm(Context context, Household household, String serviceDate) {
        return buildMotherIndexForm(context, household, serviceDate, "negative");
    }

    public static JSONObject buildMotherIndexForm(Context context, Household household, String serviceDate, String caregiverHivStatus) {
        if (context == null || household == null) {
            return null;
        }
        try {
            FormUtils formUtils = new FormUtils(context);
            JSONObject form = formUtils.getFormJson("mother_index");
            if (form == null) {
                return null;
            }

            String householdId = safe(household.getHousehold_id());
            if (!TextUtils.isEmpty(householdId)) {
                setFieldValue(form, "household_id", householdId);
            }

            setFieldValue(form, "caregiver_name", household.getCaregiver_name());
            setFieldValue(form, "caregiver_birth_date", household.getCaregiver_birth_date());
            setFieldValue(form, "caregiver_sex", "female");
            String hivStatus = safe(caregiverHivStatus);
            if (TextUtils.isEmpty(hivStatus)) {
                hivStatus = safe(household.getCaregiver_hiv_status());
            }
            if (TextUtils.isEmpty(hivStatus)) {
                hivStatus = "negative";
            }
            setFieldValue(form, "caregiver_hiv_status", hivStatus);
            setFieldValue(form, "caregiver_phone", safe(household.getCaregiver_phone()));

            setFieldValue(form, "homeaddress", household.getHomeaddress());
            setFieldValue(form, "landmark", household.getLandmark());
            setFieldValue(form, "district", household.getDistrict());
            setFieldValue(form, "ward", household.getWard());
            setFieldValue(form, "facility", household.getFacility());
            setFieldValue(form, "partner", household.getPartner());
            setFieldValue(form, "province", household.getProvince());
            setFieldValue(form, "caseworker_name", household.getCaseworker_name());

            String screeningDate = safe(serviceDate);
            if (TextUtils.isEmpty(screeningDate)) {
                screeningDate = LocalDate.now().format(DATE_FORMAT);
            }
            setFieldValue(form, "mother_screening_date", screeningDate);
            setFieldValue(form, "mother_age_range", "yes");

            return form;
        } catch (Exception e) {
            Timber.e(e);
            return null;
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

    private static LocalDate parseDateSafe(String value) {
        if (TextUtils.isEmpty(value)) {
            return null;
        }
        try {
            return LocalDate.parse(value.trim(), DATE_FORMAT);
        } catch (Exception e) {
            return null;
        }
    }

    private static String safe(String value) {
        return value == null ? "" : value.trim();
    }
}
