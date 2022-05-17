package com.bluecodeltd.ecap.chw.model;

public class FamilyServiceModel {

    private String  services, services_household, services_caregiver,  date,
            household_id, base_entity_id, other_services_caregiver,
            other_services_household,caseworker_name;

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


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


    public String getServices() {
        return services;
    }

    public void setServices(String services) {
        this.services = services;
    }

    public String getServices_household() {
        return services_household;
    }

    public void setServices_household(String services_household) {
        this.services_household = services_household;
    }

    public String getServices_caregiver() {
        return services_caregiver;
    }

    public void setServices_caregiver(String services_caregiver) {
        this.services_caregiver = services_caregiver;
    }

    public String getOther_services_caregiver() {
        return other_services_caregiver;
    }

    public void setOther_services_caregiver(String other_services_caregiver) {
        this.other_services_caregiver = other_services_caregiver;
    }

    public String getOther_services_household() {
        return other_services_household;
    }

    public void setOther_services_household(String other_services_household) {
        this.other_services_household = other_services_household;

    }

    public String getCaseworker_name() {
        return caseworker_name;
    }

    public void setCaseworker_name(String caseworker_name) {
        this.caseworker_name = caseworker_name;
    }
}
