package com.bluecodeltd.ecap.chw.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Columns and labels of the Appointment, PrEP and Referral Linkage register forms. Generated from the
 * same definitions as the forms in assets/json.form, so keep them in step when a form changes.
 */
public final class ServiceRegisterFields {

    private ServiceRegisterFields() {
    }

    /** Columns of ec_appointment besides the common ones. */
    public static final String[] APPOINTMENT_COLUMNS = {
            "iccm_id", "sub_population", "caregiver_name", "hiv_status",
            "under_five_card_no", "art_number", "sm_number", "alt_phone",
            "appointment_date", "appointment_time", "appointment_type", "appointment_type_other",
            "reminder_given", "days_late", "appointment_kept", "date_tracking_initiated",
            "tracking_attempt_1", "tracking_attempt_2", "tracking_attempt_3", "date_declared_late",
            "tracking_method", "tracking_outcome", "outcome_date", "comment",
            "appointment_status", "service_point"
    };

    /** Fields shown in the details sheet, as column then label, in form order. */
    public static final String[][] APPOINTMENT_DETAILS = {
            {"iccm_id", "iCCM ID"},
            {"gender", "Sex"},
            {"birthdate", "Date of birth"},
            {"sub_population", "Sub-population"},
            {"caregiver_name", "Mother/caregiver name"},
            {"hiv_status", "HIV status"},
            {"under_five_card_no", "Under-five card number"},
            {"art_number", "ART number"},
            {"sm_number", "SM number"},
            {"phone", "Phone number"},
            {"alt_phone", "Alternative phone number"},
            {"address", "Residential address or landmark"},
            {"appointment_date", "Appointment date"},
            {"appointment_time", "Appointment time"},
            {"appointment_type", "Appointment type"},
            {"appointment_type_other", "Other appointment type"},
            {"reminder_given", "Reminder given"},
            {"appointment_kept", "Appointment kept"},
            {"date_tracking_initiated", "Date tracking initiated"},
            {"tracking_attempt_1", "Tracking attempt 1"},
            {"tracking_attempt_2", "Tracking attempt 2"},
            {"tracking_attempt_3", "Tracking attempt 3"},
            {"date_declared_late", "Date declared late"},
            {"tracking_method", "Tracking method"},
            {"tracking_outcome", "Tracking outcome"},
            {"outcome_date", "Back in care or served on"},
            {"comment", "Comments"}
    };

    /** Columns of ec_prep besides the common ones. */
    public static final String[] PREP_COLUMNS = {
            "substantial_risk", "prep_eligibility", "prep_status", "review_1m_date",
            "next_reassessment_date", "iccm_id", "date_first_contact", "ward",
            "sub_population", "pregnant", "breastfeeding", "risk_assessment_done",
            "high_risk", "hiv_test_date", "hiv_result", "valid_result_3m",
            "linked_to_art", "ahi_symptoms", "risk_factors", "eligibility_criteria",
            "prep_provision", "prep_type", "referral_completed", "facility_prep_type",
            "prep_start_date", "facility_prep_start_date", "comment", "review_1m_completed",
            "follow_up_type", "last_follow_up_date", "ahi_screened", "repeat_test_date",
            "repeat_test_result", "still_high_risk", "continuing_prep", "stop_reason",
            "date_initiated", "prep_regimen", "next_refill_date", "hiv_status",
            "date_tested"
    };

    /** Fields shown in the details sheet, as column then label, in form order. */
    public static final String[][] PREP_DETAILS = {
            {"iccm_id", "iCCM ID"},
            {"gender", "Sex"},
            {"birthdate", "Date of birth"},
            {"date_first_contact", "Date of first contact"},
            {"ward", "Ward"},
            {"address", "Community, compound or address"},
            {"sub_population", "Sub-population"},
            {"pregnant", "Pregnant"},
            {"breastfeeding", "Breastfeeding"},
            {"phone", "Phone number"},
            {"risk_assessment_done", "HIV risk assessment done"},
            {"high_risk", "Individual at high risk"},
            {"hiv_test_date", "HIV test date"},
            {"hiv_result", "HIV result"},
            {"valid_result_3m", "Valid HIV result within the last 3 months"},
            {"linked_to_art", "Linked to ART"},
            {"ahi_symptoms", "Signs of acute HIV infection"},
            {"risk_factors", "Risk factors present"},
            {"eligibility_criteria", "Eligibility criteria met"},
            {"prep_provision", "PrEP provided or referred"},
            {"prep_type", "PrEP type provided"},
            {"referral_completed", "PrEP referral completed"},
            {"facility_prep_type", "PrEP type at the facility"},
            {"prep_start_date", "PrEP start date"},
            {"facility_prep_start_date", "PrEP start date at the facility"},
            {"comment", "Comments"},
            {"follow_up_type", "Follow-up visit"},
            {"last_follow_up_date", "Visit date"},
            {"ahi_screened", "Screened for signs of acute HIV infection"},
            {"repeat_test_date", "Repeat HIV test date"},
            {"repeat_test_result", "Repeat HIV test result"},
            {"still_high_risk", "Still at high risk of HIV"},
            {"continuing_prep", "Continuing PrEP"},
            {"stop_reason", "Reason for stopping PrEP"}
    };

    /** Columns of ec_referral_linkage besides the common ones. */
    public static final String[] REFERRAL_COLUMNS = {
            "date_referral_received", "iccm_id", "sub_population", "sub_population_other",
            "referred_from", "referred_from_other", "referred_to", "referred_to_other",
            "referred_by", "referral_service", "referral_service_other", "facility_service_number",
            "comment", "service_received", "date_service_received", "date_referred",
            "receiving_facility", "linkage_status", "date_linked"
    };

    /** Fields shown in the details sheet, as column then label, in form order. */
    public static final String[][] REFERRAL_DETAILS = {
            {"date_referral_received", "Date referral received"},
            {"iccm_id", "iCCM ID"},
            {"gender", "Sex"},
            {"birthdate", "Date of birth"},
            {"sub_population", "Sub-population"},
            {"sub_population_other", "Other sub-population"},
            {"address", "Address"},
            {"phone", "Phone number"},
            {"referred_from", "Referred from"},
            {"referred_from_other", "Referring institution"},
            {"referred_to", "Referred to"},
            {"referred_to_other", "Receiving institution"},
            {"referred_by", "Referred by"},
            {"referral_service", "Service referred for"},
            {"referral_service_other", "Other service"},
            {"facility_service_number", "Client service number at the facility"},
            {"comment", "Remarks"},
            {"service_received", "Service received"},
            {"date_service_received", "Date service received"}
    };

    /** Display labels of option keys, by field. */
    public static final Map<String, Map<String, String>> OPTION_LABELS;

    static {
        Map<String, Map<String, String>> labels = new HashMap<>();
        labels.put("ahi_screened", map(
                "Yes", "Yes",
                "No", "No"));
        labels.put("ahi_symptoms", map(
                "fever", "Fever",
                "fatigue", "Fatigue",
                "anorexia", "Anorexia",
                "rash", "Rash",
                "pharyngitis", "Pharyngitis",
                "lymphadenopathy", "Generalised lymphadenopathy",
                "ulcerations", "Skin or mucous membrane ulcerations",
                "headache", "Headache",
                "thrush", "Thrush",
                "herpes_zoster", "Herpes zoster",
                "none", "None of these"));
        labels.put("appointment_kept", map(
                "Yes", "Yes",
                "No", "No"));
        labels.put("appointment_type", map(
                "immunisation", "Immunisation",
                "growth_monitoring", "Growth monitoring",
                "maternal_retesting", "Maternal retesting",
                "antenatal", "Antenatal",
                "postnatal", "Post-natal",
                "eid", "EID",
                "tpt", "TPT",
                "prep", "PrEP",
                "art", "ART",
                "cr", "Clinical review",
                "pp", "Pharmacy pick-up",
                "eac", "Enhanced adherence counselling",
                "vl_testing", "Viral load testing",
                "cacx", "CaCx screening",
                "other", "Other"));
        labels.put("breastfeeding", map(
                "Yes", "Yes",
                "No", "No"));
        labels.put("continuing_prep", map(
                "Yes", "Yes",
                "No", "No"));
        labels.put("eligibility_criteria", map(
                "willing_adhere", "Willing to adhere to PrEP",
                "attend_3monthly", "Able to attend 3-monthly visits and HIV testing",
                "other_prevention", "Willing to use other prevention methods",
                "ipv_screened_out", "Screened for IPV and screened out"));
        labels.put("facility_prep_type", map(
                "oral_daily", "Oral PrEP (daily)",
                "oral_ed", "Event-driven oral PrEP",
                "cab_la", "Injectable PrEP (CAB-LA)",
                "dapivirine_ring", "Dapivirine vaginal ring",
                "other", "Other"));
        labels.put("follow_up_type", map(
                "review_1m", "1-month review",
                "reassessment_3m", "3-month reassessment"));
        labels.put("gender", map(
                "Male", "Male",
                "Female", "Female"));
        labels.put("high_risk", map(
                "Yes", "Yes",
                "No", "No"));
        labels.put("hiv_result", map(
                "Negative", "Negative",
                "Positive", "Positive",
                "Unknown", "Not tested / unknown"));
        labels.put("hiv_status", map(
                "Positive", "Positive",
                "Negative", "Negative",
                "Unknown", "Unknown"));
        labels.put("linked_to_art", map(
                "Yes", "Yes",
                "No", "No"));
        labels.put("pregnant", map(
                "Yes", "Yes",
                "No", "No"));
        labels.put("prep_eligibility", map(
                "on_prep", "On PrEP",
                "referred", "Referred for PrEP",
                "eligible", "Eligible for PrEP",
                "deferred", "PrEP deferred",
                "not_at_risk", "Not at substantial risk",
                "hiv_positive", "HIV positive",
                "stopped", "Stopped PrEP",
                "no_longer_at_risk", "No longer at high risk",
                "awaiting_test", "Awaiting HIV test"));
        labels.put("prep_provision", map(
                "community", "Provided PrEP in the community",
                "referred", "Referred to a health facility",
                "not_yet", "Not yet"));
        labels.put("prep_status", map(
                "on_prep", "On PrEP",
                "referred", "Referred for PrEP",
                "eligible", "Eligible, not started",
                "deferred", "PrEP deferred",
                "not_at_risk", "Not at substantial risk",
                "hiv_positive", "HIV positive",
                "stopped", "Stopped PrEP",
                "no_longer_at_risk", "No longer at high risk",
                "awaiting_test", "Awaiting HIV test"));
        labels.put("prep_type", map(
                "oral_daily", "Oral PrEP (daily)",
                "oral_ed", "Event-driven oral PrEP",
                "cab_la", "Injectable PrEP (CAB-LA)",
                "dapivirine_ring", "Dapivirine vaginal ring",
                "other", "Other"));
        labels.put("referral_completed", map(
                "Yes", "Yes",
                "No", "No",
                "pending", "Not yet"));
        labels.put("referral_service", map(
                "vl_testing", "Viral load testing",
                "eac", "Enhanced adherence counselling",
                "drug_modification", "Drug modification",
                "tb_diagnosis", "TB diagnosis",
                "nutrition", "Nutrition counselling/screening",
                "mmd3", "Transition from <3MMD to 3MMD",
                "mmd6", "Transition from <3MMD to 6MMD",
                "community_vl", "Community VL sample collection",
                "community_dbs", "Community DBS collection",
                "itt_traced", "Interruption in treatment traced",
                "prep", "PrEP",
                "pep", "PEP",
                "ahd", "Advanced HIV disease",
                "malaria", "Malaria",
                "other", "Other"));
        labels.put("referred_from", map(
                "community", "Community",
                "facility", "Health facility",
                "other", "Other"));
        labels.put("referred_to", map(
                "community", "Community",
                "facility", "Health facility",
                "other", "Other"));
        labels.put("reminder_given", map(
                "Yes", "Yes",
                "No", "No"));
        labels.put("repeat_test_result", map(
                "Negative", "Negative",
                "Positive", "Positive",
                "Unknown", "Not tested / unknown"));
        labels.put("risk_assessment_done", map(
                "Yes", "Yes",
                "No", "No"));
        labels.put("risk_factors", map(
                "partner_status_unknown", "Partner HIV status unknown",
                "multiple_partners", "Client or partner has more than one sexual partner",
                "migration", "Long-distance or labour migration",
                "discordant", "Discordant relationship",
                "inconsistent_condoms", "Inconsistent condom use",
                "sti_6m", "STI in the past 6 months",
                "none", "None of these"));
        labels.put("service_received", map(
                "Yes", "Yes",
                "No", "No"));
        labels.put("still_high_risk", map(
                "Yes", "Yes",
                "No", "No"));
        labels.put("sub_population", map(
                "calhiv", "C/ALHIV",
                "hei", "HEI",
                "ca_wlhiv", "Child/adolescent of WLHIV",
                "child_plhiv", "Child of PLHIV",
                "ssv", "Survivor of sexual violence",
                "hiv_pos_caregiver", "HIV-positive caregiver",
                "hiv_pos_pbfw", "HIV-positive PBFW",
                "hiv_neg_pbfw", "HIV-negative PBF AGYW 10-25 years",
                "hiv_neg_pbfw_25", "HIV-negative PBFW over 25 at high risk",
                "ayp_high_risk", "AYP at high risk",
                "caregiver", "Primary caregiver",
                "other", "Other"));
        labels.put("tracking_method", map(
                "sms", "SMS",
                "phone", "Phone call",
                "hv", "Home visit",
                "other", "Other"));
        labels.put("tracking_outcome", map(
                "sbf", "Still being followed",
                "bbtc", "Brought back to care",
                "traced_served", "Traced and served",
                "refused", "Traced and refused care"));
        labels.put("valid_result_3m", map(
                "Yes", "Yes",
                "No", "No"));
        OPTION_LABELS = Collections.unmodifiableMap(labels);
    }

    private static Map<String, String> map(String... keysAndLabels) {
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i + 1 < keysAndLabels.length; i += 2) {
            map.put(keysAndLabels[i], keysAndLabels[i + 1]);
        }
        return Collections.unmodifiableMap(map);
    }
}
