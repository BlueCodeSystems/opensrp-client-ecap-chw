package com.bluecodeltd.ecap.chw.model;

public class PmtctDeliveryDetailsModel {
    private String base_entity_id;
    private String pmtct_id;
    private String date_of_delivery;
    private String place_of_delivery;
    private String on_art_at_time_of_delivery;

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

    public String getOn_art_at_time_of_delivery() {
        return on_art_at_time_of_delivery;
    }

    public void setOn_art_at_time_of_delivery(String on_art_at_time_of_delivery) {
        this.on_art_at_time_of_delivery = on_art_at_time_of_delivery;
    }
}
