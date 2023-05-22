package com.bluecodeltd.ecap.chw.model;

import java.io.Serializable;

public class CasePlanModel implements Serializable {

    private String base_entity_id;
    private String unique_id;
    private String case_plan_date;
    private String case_plan_status;
    private String type;
    private String vulnerability;
    private String goal;
    private String services;
    private String service_referred;
    private String institution;
    private String due_date;
    private String quarter;
    private String status;
    private String comment;
    private String delete_status;

    private String household_id;


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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVulnerability() {
        return vulnerability;
    }

    public void setVulnerability(String vulnerability) {
        this.vulnerability = vulnerability;
    }

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public String getServices() {
        return services;
    }

    public void setServices(String services) {
        this.services = services;
    }

    public String getService_referred() {
        return service_referred;
    }

    public void setService_referred(String service_referred) {
        this.service_referred = service_referred;
    }

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public String getDue_date() {
        return due_date;
    }

    public void setDue_date(String due_date) {
        this.due_date = due_date;
    }

    public String getQuarter() {
        return quarter;
    }

    public void setQuarter(String quarter) {
        this.quarter = quarter;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getBase_entity_id() {
        return base_entity_id;
    }

    public void setBase_entity_id(String base_entity_id) {
        this.base_entity_id = base_entity_id;
    }
}
