package com.bluecodeltd.ecap.chw.model;

public class MotherDeliveryModel {

    private String base_entity_id;
    private String household_id;
    private String date_of_delivery;
    private String place_of_delivery;
    private String hiv_status_at_delivery;
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

    public String getDate_of_delivery() {
        return date_of_delivery;
    }

    public void setDate_of_delivery(String date_of_delivery) {
        this.date_of_delivery = date_of_delivery;
    }

    public String getPlace_of_delivery() {
        return place_of_delivery;
    }

    public void setPlace_of_delivery(String place_of_delivery) {
        this.place_of_delivery = place_of_delivery;
    }

    public String getHiv_status_at_delivery() {
        return hiv_status_at_delivery;
    }

    public void setHiv_status_at_delivery(String hiv_status_at_delivery) {
        this.hiv_status_at_delivery = hiv_status_at_delivery;
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
}
