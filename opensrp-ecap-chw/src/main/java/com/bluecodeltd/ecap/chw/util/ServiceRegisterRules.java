package com.bluecodeltd.ecap.chw.util;

import static com.bluecodeltd.ecap.chw.util.ServiceRegisterValues.date;
import static com.bluecodeltd.ecap.chw.util.ServiceRegisterValues.stored;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Works out the values the AFGHS-PCZ tools derive from the answers (PrEP substantial risk, eligibility,
 * review dates, days late) and writes them into the form's hidden fields before the form is saved.
 */
public final class ServiceRegisterRules {

    private static final List<String> ELIGIBILITY = Arrays.asList(
            "willing_adhere", "attend_3monthly", "other_prevention", "ipv_screened_out");

    private ServiceRegisterRules() {
    }

    /**
     * @param existing the record's current columns for a follow-up form; empty for a new record
     */
    public static void apply(String encounterType, JSONArray fields, Map<String, String> existing) throws JSONException {
        LocalDate today = LocalDate.now();
        switch (encounterType) {
            case "Appointment Outcome":
                appointmentOutcome(fields, existing, today);
                break;
            case "PrEP":
                prepScreening(fields);
                break;
            case "PrEP Follow-up":
                prepFollowUp(fields, existing);
                break;
            default:
                break;
        }
        if (!existing.isEmpty()) set(fields, "date_edited", stored(today));
    }

    private static void appointmentOutcome(JSONArray fields, Map<String, String> existing, LocalDate today) throws JSONException {
        LocalDate appointment = ServiceRegisterValues.date(existing, "appointment_date");
        if (appointment == null || !"No".equals(value(fields, "appointment_kept"))) return;
        LocalDate end = date(value(fields, "outcome_date"));
        long late = ServiceRegisterValues.days(appointment, end != null ? end : today);
        set(fields, "days_late", Long.toString(Math.max(0, late)));
    }

    private static void prepScreening(JSONArray fields) throws JSONException {
        String result = value(fields, "hiv_result");
        List<String> risks = new ArrayList<>(ticked(fields, "risk_factors"));
        boolean noneTicked = risks.remove("none");
        String substantialRisk = !risks.isEmpty() ? "Yes" : noneTicked ? "No" : "";
        set(fields, "substantial_risk", substantialRisk);

        String eligibility;
        if ("Positive".equals(result)) {
            eligibility = "hiv_positive";
        } else if (!"Negative".equals(result)) {
            eligibility = "awaiting_test";
        } else if ("No".equals(substantialRisk)) {
            eligibility = "not_at_risk";
        } else if ("Yes".equals(substantialRisk)) {
            eligibility = ticked(fields, "eligibility_criteria").containsAll(ELIGIBILITY) ? "eligible" : "deferred";
        } else {
            eligibility = "";
        }
        set(fields, "prep_eligibility", eligibility);

        String status = eligibility;
        if ("eligible".equals(eligibility)) {
            String provision = value(fields, "prep_provision");
            if ("community".equals(provision) || "Yes".equals(value(fields, "referral_completed"))) {
                status = "on_prep";
            } else if ("referred".equals(provision)) {
                status = "referred";
            }
        }
        set(fields, "prep_status", status);

        LocalDate start = date(value(fields, "prep_start_date"));
        if (start == null) start = date(value(fields, "facility_prep_start_date"));
        if (start != null) set(fields, "review_1m_date", stored(start.plusMonths(1)));

        LocalDate contact = date(value(fields, "date_first_contact"));
        if (contact != null && "Yes".equals(substantialRisk) && !"hiv_positive".equals(eligibility)) {
            set(fields, "next_reassessment_date", stored(contact.plusMonths(3)));
        }
    }

    private static void prepFollowUp(JSONArray fields, Map<String, String> existing) throws JSONException {
        LocalDate visit = date(value(fields, "last_follow_up_date"));
        if ("review_1m".equals(value(fields, "follow_up_type"))) set(fields, "review_1m_completed", "Yes");

        String status = ServiceRegisterValues.get(existing, "prep_status");
        String eligibility = ServiceRegisterValues.get(existing, "prep_eligibility");
        boolean referralDone = "Yes".equals(value(fields, "referral_completed"));
        if ("Positive".equals(value(fields, "repeat_test_result"))) {
            status = "hiv_positive";
            eligibility = "hiv_positive";
        } else if ("No".equals(value(fields, "still_high_risk"))) {
            status = "no_longer_at_risk";
        } else if ("No".equals(value(fields, "continuing_prep"))) {
            status = "stopped";
        } else if ("referred".equals(status) && !referralDone) {
            status = "referred";
        } else if ("Yes".equals(value(fields, "continuing_prep")) || referralDone) {
            status = "on_prep";
        }
        set(fields, "prep_status", status);
        set(fields, "prep_eligibility", eligibility);

        LocalDate facilityStart = date(value(fields, "facility_prep_start_date"));
        if (facilityStart != null && ServiceRegisterValues.get(existing, "review_1m_date").isEmpty()) {
            set(fields, "review_1m_date", stored(facilityStart.plusMonths(1)));
        }
        if (visit != null && "Yes".equals(value(fields, "still_high_risk")) && !"hiv_positive".equals(status)) {
            set(fields, "next_reassessment_date", stored(visit.plusMonths(3)));
        }
    }

    // ------------------------------------------------------------------ form field access

    private static JSONObject field(JSONArray fields, String key) {
        for (int i = 0; i < fields.length(); i++) {
            JSONObject field = fields.optJSONObject(i);
            if (field != null && key.equals(field.optString("key"))) return field;
        }
        return null;
    }

    static String value(JSONArray fields, String key) {
        JSONObject field = field(fields, key);
        if (field == null) return "";
        Object value = field.opt("value");
        return value == null || value == JSONObject.NULL ? "" : value.toString().trim();
    }

    /** The keys ticked in a check box field, from its value or, failing that, from its options. */
    static List<String> ticked(JSONArray fields, String key) {
        JSONObject field = field(fields, key);
        if (field == null) return Collections.emptyList();
        List<String> keys = new ArrayList<>(ServiceRegisterValues.ticked(field.optString("value")));
        if (keys.isEmpty()) {
            JSONArray options = field.optJSONArray("options");
            for (int i = 0; options != null && i < options.length(); i++) {
                JSONObject option = options.optJSONObject(i);
                if (option != null && "true".equalsIgnoreCase(option.optString("value"))) keys.add(option.optString("key"));
            }
        }
        return keys;
    }

    public static void set(JSONArray fields, String key, String value) throws JSONException {
        JSONObject field = field(fields, key);
        if (field != null) field.put("value", value);
    }
}
