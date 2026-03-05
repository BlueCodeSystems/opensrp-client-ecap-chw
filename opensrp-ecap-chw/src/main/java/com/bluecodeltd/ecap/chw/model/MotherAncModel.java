package com.bluecodeltd.ecap.chw.model;

public class MotherAncModel {

    private String base_entity_id;
    private String household_id;
    private String date_1st_visit;
    private String gestation_age_in_weeks;
    private String hiv_tested;
    private String date_tested;
    private String result_of_hiv_test;
    private String male_hiv_tested;
    private String male_result_of_hiv_test;
    private String gravida;
    private String parity;
    private String lmp_date;
    private String edd_date;
    private String tt_previous_doses;
    private String delete_status;
    private String entity_type;
    private String last_interacted_with;

    public String getBase_entity_id() {
        return base_entity_id;
    }

    public void setBase_entity_id(String base_entity_id) {
        this.base_entity_id = base_entity_id;
    }

    public String getHousehold_id() {
        return household_id;
    }

    public void setHousehold_id(String household_id) {
        this.household_id = household_id;
    }

    public String getDate_1st_visit() {
        return date_1st_visit;
    }

    public void setDate_1st_visit(String date_1st_visit) {
        this.date_1st_visit = date_1st_visit;
    }

    public String getGestation_age_in_weeks() {
        return gestation_age_in_weeks;
    }

    public void setGestation_age_in_weeks(String gestation_age_in_weeks) {
        this.gestation_age_in_weeks = gestation_age_in_weeks;
    }

    public String getHiv_tested() {
        return hiv_tested;
    }

    public void setHiv_tested(String hiv_tested) {
        this.hiv_tested = hiv_tested;
    }

    public String getDate_tested() {
        return date_tested;
    }

    public void setDate_tested(String date_tested) {
        this.date_tested = date_tested;
    }

    public String getResult_of_hiv_test() {
        return result_of_hiv_test;
    }

    public void setResult_of_hiv_test(String result_of_hiv_test) {
        this.result_of_hiv_test = result_of_hiv_test;
    }

    public String getMale_hiv_tested() {
        return male_hiv_tested;
    }

    public void setMale_hiv_tested(String male_hiv_tested) {
        this.male_hiv_tested = male_hiv_tested;
    }

    public String getMale_result_of_hiv_test() {
        return male_result_of_hiv_test;
    }

    public void setMale_result_of_hiv_test(String male_result_of_hiv_test) {
        this.male_result_of_hiv_test = male_result_of_hiv_test;
    }

    public String getGravida() {
        return gravida;
    }

    public void setGravida(String gravida) {
        this.gravida = gravida;
    }

    public String getParity() {
        return parity;
    }

    public void setParity(String parity) {
        this.parity = parity;
    }

    public String getLmp_date() {
        return lmp_date;
    }

    public void setLmp_date(String lmp_date) {
        this.lmp_date = lmp_date;
    }

    public String getEdd_date() {
        return edd_date;
    }

    public void setEdd_date(String edd_date) {
        this.edd_date = edd_date;
    }

    public String getTt_previous_doses() {
        return tt_previous_doses;
    }

    public void setTt_previous_doses(String tt_previous_doses) {
        this.tt_previous_doses = tt_previous_doses;
    }

    public String getDelete_status() {
        return delete_status;
    }

    public void setDelete_status(String delete_status) {
        this.delete_status = delete_status;
    }

    public String getEntity_type() {
        return entity_type;
    }

    public void setEntity_type(String entity_type) {
        this.entity_type = entity_type;
    }

    public String getLast_interacted_with() {
        return last_interacted_with;
    }

    public void setLast_interacted_with(String last_interacted_with) {
        this.last_interacted_with = last_interacted_with;
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

