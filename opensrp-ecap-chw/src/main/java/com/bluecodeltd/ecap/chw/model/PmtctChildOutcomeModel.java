package com.bluecodeltd.ecap.chw.model;

public class PmtctChildOutcomeModel {
    private String base_entity_id;
    private String pmtct_id;
    private String unique_id;
    private String caregiver_name;
    private String household_id;
    private String child_outcome;


    public String getBase_entity_id() {
        return base_entity_id;
    }

    public void setBase_entity_id(String base_entity_id) {
        this.base_entity_id = base_entity_id;
    }

    public String getPmtct_id() {
        return pmtct_id;
    }

    public void setPmtct_id(String pmtct_id) {
        this.pmtct_id = pmtct_id;
    }

    public String getUnique_id() {
        return unique_id;
    }

    public void setUnique_id(String unique_id) {
        this.unique_id = unique_id;
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

    public String getChild_outcome() {
        return child_outcome;
    }

    public void setChild_outcome(String child_outcome) {
        this.child_outcome = child_outcome;
    }
}
