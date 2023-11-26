package com.bluecodeltd.ecap.chw.model;

public class WeGroupRegisterModel {
    private String last_interacted_with;
    private String base_entity_id;
    private String date_register_conducted;
    private String attendance;
    private String group_id;

    public WeGroupRegisterModel() {
        this.last_interacted_with = last_interacted_with;
        this.base_entity_id = base_entity_id;
        this.date_register_conducted = date_register_conducted;
        this.attendance = attendance;
        this.group_id = group_id;
    }

    public String getLast_interacted_with() {
        return last_interacted_with;
    }

    public void setLast_interacted_with(String last_interacted_with) {
        this.last_interacted_with = last_interacted_with;
    }

    public String getBase_entity_id() {
        return base_entity_id;
    }

    public void setBase_entity_id(String base_entity_id) {
        this.base_entity_id = base_entity_id;
    }

    public String getDate_register_conducted() {
        return date_register_conducted;
    }

    public void setDate_register_conducted(String date_register_conducted) {
        this.date_register_conducted = date_register_conducted;
    }

    public String getAttendance() {
        return attendance;
    }

    public void setAttendance(String attendance) {
        this.attendance = attendance;
    }

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }
}
