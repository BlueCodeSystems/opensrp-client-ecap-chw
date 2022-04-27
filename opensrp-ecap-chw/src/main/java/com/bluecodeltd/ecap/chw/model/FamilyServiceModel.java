package com.bluecodeltd.ecap.chw.model;

public class FamilyServiceModel {

    private String services, service_household, service_caregiver,  date,
            household_id, base_entity_id, other_service_caregiver,
            other_service_household;

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

    public String getService_caregiver() {
        return service_caregiver;
    }

    public void setService_caregiver(String service_caregiver) {
        this.service_caregiver = service_caregiver;
    }

    public String getService_household() {
        return service_household;
    }

    public void setService_household(String service_household) {
        this.service_household = service_household;
    }

    public String getOther_service_caregiver() {
        return other_service_caregiver;
    }

    public void setOther_service_caregiver(String other_service_caregiver) {
        this.other_service_caregiver = other_service_caregiver;
    }

    public String getOther_service_household() {
        return other_service_household;
    }

    public void setOther_service_household(String other_service_household) {
        this.other_service_household = other_service_household;
    }


    public String getServices() {
        return services;
    }

    public void setServices(String services) {
        this.services = services;
    }
}
