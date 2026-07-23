package com.bluecodeltd.ecap.chw.model;

public class TbScreeningOutcomeModel {
    private String base_entity_id;
    private String unique_tb_id;
    private String followup_date;
    private String facility_referral_completed;
    private String date_screened_at_facility;
    private String tb_diagnosis_at_facility;
    private String initiated_tb_treatment;
    private String initiated_tpt;
    private String section_c_comments;
    private String treatment_followup_date;
    private String tb_treatment_outcome;
    private String tb_treatment_outcome_comment;
    private String last_interacted_with;

    public String getBase_entity_id() { return base_entity_id; }
    public void setBase_entity_id(String base_entity_id) { this.base_entity_id = base_entity_id; }
    public String getUnique_tb_id() { return unique_tb_id; }
    public void setUnique_tb_id(String unique_tb_id) { this.unique_tb_id = unique_tb_id; }
    public String getFollowup_date() { return followup_date; }
    public void setFollowup_date(String followup_date) { this.followup_date = followup_date; }
    public String getFacility_referral_completed() { return facility_referral_completed; }
    public void setFacility_referral_completed(String facility_referral_completed) { this.facility_referral_completed = facility_referral_completed; }
    public String getDate_screened_at_facility() { return date_screened_at_facility; }
    public void setDate_screened_at_facility(String date_screened_at_facility) { this.date_screened_at_facility = date_screened_at_facility; }
    public String getTb_diagnosis_at_facility() { return tb_diagnosis_at_facility; }
    public void setTb_diagnosis_at_facility(String tb_diagnosis_at_facility) { this.tb_diagnosis_at_facility = tb_diagnosis_at_facility; }
    public String getInitiated_tb_treatment() { return initiated_tb_treatment; }
    public void setInitiated_tb_treatment(String initiated_tb_treatment) { this.initiated_tb_treatment = initiated_tb_treatment; }
    public String getInitiated_tpt() { return initiated_tpt; }
    public void setInitiated_tpt(String initiated_tpt) { this.initiated_tpt = initiated_tpt; }
    public String getSection_c_comments() { return section_c_comments; }
    public void setSection_c_comments(String section_c_comments) { this.section_c_comments = section_c_comments; }
    public String getTreatment_followup_date() { return treatment_followup_date; }
    public void setTreatment_followup_date(String treatment_followup_date) { this.treatment_followup_date = treatment_followup_date; }
    public String getTb_treatment_outcome() { return tb_treatment_outcome; }
    public void setTb_treatment_outcome(String tb_treatment_outcome) { this.tb_treatment_outcome = tb_treatment_outcome; }
    public String getTb_treatment_outcome_comment() { return tb_treatment_outcome_comment; }
    public void setTb_treatment_outcome_comment(String tb_treatment_outcome_comment) { this.tb_treatment_outcome_comment = tb_treatment_outcome_comment; }

    public String getLast_interacted_with() { return last_interacted_with; }
    public void setLast_interacted_with(String last_interacted_with) { this.last_interacted_with = last_interacted_with; }

    private final java.util.Map<String, String> additionalFields = new java.util.HashMap<>();

    public java.util.Map<String, String> getAdditionalFields() {
        return additionalFields;
    }

    public String getAdditionalField(String key) {
        if (key == null) return null;
        return additionalFields.get(key);
    }

    public void setAdditionalField(String key, String value) {
        if (key == null) return;
        additionalFields.put(key, value);
    }
}

