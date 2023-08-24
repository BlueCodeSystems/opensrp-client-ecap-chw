package com.bluecodeltd.ecap.chw.model;

public class VcaCasePlanModel {
    private String base_entity_id;
    private String unique_id;
    private String case_plan_date;
    private String case_plan_id;
    private String case_plan_status;

    public String getCase_plan_id() {
        return case_plan_id;
    }

    public void setCase_plan_id(String case_plan_id) {
        this.case_plan_id = case_plan_id;
    }

    public String getBase_entity_id() {
        return base_entity_id;
    }

    public void setBase_entity_id(String base_entity_id) {
        this.base_entity_id = base_entity_id;
    }

    public String getUnique_id() {
        return unique_id;
    }

    public void setUnique_id(String unique_id) {
        this.unique_id = unique_id;
    }

    public String getCase_plan_date() {
        return case_plan_date;
    }

    public void setCase_plan_date(String case_plan_date) {
        this.case_plan_date = case_plan_date;
    }

    public String getCase_plan_status() {
        return case_plan_status;
    }

    public void setCase_plan_status(String case_plan_status) {
        this.case_plan_status = case_plan_status;
    }
}
