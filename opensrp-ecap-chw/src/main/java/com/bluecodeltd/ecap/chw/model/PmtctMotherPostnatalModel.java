package com.bluecodeltd.ecap.chw.model;

public class PmtctMotherPostnatalModel {
    private String base_entity_id;
    private String relational_id;
    private String pmtct_id;
    private String caregiver_name;
    private String household_id;
    private String date_of_st_post_natal_care;
    private String mother_tested_for_hiv;
    private String postnatal_care_visit;
    private String art_initiated;
    private String art_adherence_counselling_support;
    private String vl_result;
    private String family_planning_counselling;
    private String number_of_condoms_distributed;
    private String comments_at_postnatal_care_visit;
    private String tb_screening_symptoms_10plus;
    private String other_tb_symptom_10plus;
    private String comments_tb_10plus;
    private String delete_status;

    // Getters and Setters
    public String getBase_entity_id() {
        return base_entity_id;
    }

    public void setBase_entity_id(String base_entity_id) {
        this.base_entity_id = base_entity_id;
    }

    public String getRelational_id() {
        return relational_id;
    }

    public void setRelational_id(String relational_id) {
        this.relational_id = relational_id;
    }

    public String getPmtct_id() {
        return pmtct_id;
    }

    public void setPmtct_id(String pmtct_id) {
        this.pmtct_id = pmtct_id;
    }

    public String getCaregiver_name() {
        return caregiver_name;
    }

    public void setCaregiver_name(String caregiver_name) {
        this.caregiver_name = caregiver_name;
    }

    public String getHousehold_id() {
        return household_id;
    }

    public void setHousehold_id(String household_id) {
        this.household_id = household_id;
    }

    public String getDate_of_st_post_natal_care() {
        return date_of_st_post_natal_care;
    }

    public void setDate_of_st_post_natal_care(String date_of_st_post_natal_care) {
        this.date_of_st_post_natal_care = date_of_st_post_natal_care;
    }

    public String getMother_tested_for_hiv() {
        return mother_tested_for_hiv;
    }

    public void setMother_tested_for_hiv(String mother_tested_for_hiv) {
        this.mother_tested_for_hiv = mother_tested_for_hiv;
    }

    public String getPostnatal_care_visit() {
        return postnatal_care_visit;
    }

    public void setPostnatal_care_visit(String postnatal_care_visit) {
        this.postnatal_care_visit = postnatal_care_visit;
    }

    public String getArt_initiated() {
        return art_initiated;
    }

    public void setArt_initiated(String art_initiated) {
        this.art_initiated = art_initiated;
    }

    public String getArt_adherence_counselling_support() {
        return art_adherence_counselling_support;
    }

    public void setArt_adherence_counselling_support(String art_adherence_counselling_support) {
        this.art_adherence_counselling_support = art_adherence_counselling_support;
    }

    public String getVl_result() {
        return vl_result;
    }

    public void setVl_result(String vl_result) {
        this.vl_result = vl_result;
    }

    public String getFamily_planning_counselling() {
        return family_planning_counselling;
    }

    public void setFamily_planning_counselling(String family_planning_counselling) {
        this.family_planning_counselling = family_planning_counselling;
    }

    public String getNumber_of_condoms_distributed() {
        return number_of_condoms_distributed;
    }

    public void setNumber_of_condoms_distributed(String number_of_condoms_distributed) {
        this.number_of_condoms_distributed = number_of_condoms_distributed;
    }

    public String getComments_at_postnatal_care_visit() {
        return comments_at_postnatal_care_visit;
    }

    public void setComments_at_postnatal_care_visit(String comments_at_postnatal_care_visit) {
        this.comments_at_postnatal_care_visit = comments_at_postnatal_care_visit;
    }

    public String getTb_screening_symptoms_10plus() {
        return tb_screening_symptoms_10plus;
    }

    public void setTb_screening_symptoms_10plus(String tb_screening_symptoms_10plus) {
        this.tb_screening_symptoms_10plus = tb_screening_symptoms_10plus;
    }

    public String getOther_tb_symptom_10plus() {
        return other_tb_symptom_10plus;
    }

    public void setOther_tb_symptom_10plus(String other_tb_symptom_10plus) {
        this.other_tb_symptom_10plus = other_tb_symptom_10plus;
    }

    public String getComments_tb_10plus() {
        return comments_tb_10plus;
    }

    public void setComments_tb_10plus(String comments_tb_10plus) {
        this.comments_tb_10plus = comments_tb_10plus;
    }

    public String getDelete_status() {
        return delete_status;
    }

    public void setDelete_status(String delete_status) {
        this.delete_status = delete_status;
    }

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

