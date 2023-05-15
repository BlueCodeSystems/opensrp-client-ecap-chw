package com.bluecodeltd.ecap.chw.model;

public class ChildSafetyPlanModel {
    private String base_entity_id;
    private String unique_id;
    private String caseworker_name;
    private String phone;
    private String initial_date;
    private String completion_date;
    private String safety_threats;
    private String safety_action;
    private String safety_protection;
    private String safety_plans;
    private String household_id;
    private String delete_status;


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

    public String getCaseworker_name() {
        return caseworker_name;
    }

    public void setCaseworker_name(String caseworker_name) {
        this.caseworker_name = caseworker_name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getInitial_date() {
        return initial_date;
    }

    public void setInitial_date(String initial_date) {
        this.initial_date = initial_date;
    }

    public String getCompletion_date() {
        return completion_date;
    }

    public void setCompletion_date(String completion_date) {
        this.completion_date = completion_date;
    }

    public String getSafety_threats() {
        return safety_threats;
    }

    public void setSafety_threats(String safety_threats) {
        this.safety_threats = safety_threats;
    }

    public String getSafety_action() {
        return safety_action;
    }

    public void setSafety_action(String safety_action) {
        this.safety_action = safety_action;
    }

    public String getSafety_protection() {
        return safety_protection;
    }

    public void setSafety_protection(String safety_protection) {
        this.safety_protection = safety_protection;
    }

    public String getSafety_plans() {
        return safety_plans;
    }

    public void setSafety_plans(String safety_plans) {
        this.safety_plans = safety_plans;
    }

    public String getHousehold_id() {
        return household_id;
    }

    public void setHousehold_id(String household_id) {
        this.household_id = household_id;
    }

    public String getDelete_status() {
        return delete_status;
    }

    public void setDelete_status(String delete_status) {
        this.delete_status = delete_status;
    }
}
