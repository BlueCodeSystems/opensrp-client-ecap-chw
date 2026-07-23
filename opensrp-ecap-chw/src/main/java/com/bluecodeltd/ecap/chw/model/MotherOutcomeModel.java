package com.bluecodeltd.ecap.chw.model;

public class MotherOutcomeModel {

    private String base_entity_id;
    private String household_id;
    private String mother_final_outcome_date;
    private String mother_final_outcome;
    private String mother_exited_ovc_reason;
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

    public String getMother_final_outcome_date() {
        return mother_final_outcome_date;
    }

    public void setMother_final_outcome_date(String mother_final_outcome_date) {
        this.mother_final_outcome_date = mother_final_outcome_date;
    }

    public String getMother_final_outcome() {
        return mother_final_outcome;
    }

    public void setMother_final_outcome(String mother_final_outcome) {
        this.mother_final_outcome = mother_final_outcome;
    }

    public String getMother_exited_ovc_reason() {
        return mother_exited_ovc_reason;
    }

    public void setMother_exited_ovc_reason(String mother_exited_ovc_reason) {
        this.mother_exited_ovc_reason = mother_exited_ovc_reason;
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

