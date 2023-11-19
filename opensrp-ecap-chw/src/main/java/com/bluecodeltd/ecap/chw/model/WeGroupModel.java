package com.bluecodeltd.ecap.chw.model;

public class WeGroupModel {
    private String last_interacted_with;
    private String base_entity_id;
    private String date_client_created;
    private String group_name;
    private String cycle_number;
    private String group_id;
    private String group_number;
    private String annual_interest_rate;
    private String first_training_meeting_date;
    private String date_savings_started;
    private String reinvested_savings_cycle_start;
    private String registered_members_cycle_start;
    private String group_mgt;
    private String delete_status;

    public WeGroupModel() {
        this.last_interacted_with = last_interacted_with;
        this.base_entity_id = base_entity_id;
        this.date_client_created = date_client_created;
        this.group_name = group_name;
        this.group_number = group_number;
        this.cycle_number = cycle_number;
        this.group_id = group_id;
        this.annual_interest_rate = annual_interest_rate;
        this.first_training_meeting_date = first_training_meeting_date;
        this.date_savings_started = date_savings_started;
        this.reinvested_savings_cycle_start = reinvested_savings_cycle_start;
        this.registered_members_cycle_start = registered_members_cycle_start;
        this.group_mgt = group_mgt;
        this.delete_status = delete_status;
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

    public String getDate_client_created() {
        return date_client_created;
    }

    public String getGroup_number() {
        return group_number;
    }

    public void setGroup_number(String group_number) {
        this.group_number = group_number;
    }

    public void setDate_client_created(String date_client_created) {
        this.date_client_created = date_client_created;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public String getCycle_number() {
        return cycle_number;
    }

    public void setCycle_number(String cycle_number) {
        this.cycle_number = cycle_number;
    }

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public String getAnnual_interest_rate() {
        return annual_interest_rate;
    }

    public void setAnnual_interest_rate(String annual_interest_rate) {
        this.annual_interest_rate = annual_interest_rate;
    }

    public String getFirst_training_meeting_date() {
        return first_training_meeting_date;
    }

    public void setFirst_training_meeting_date(String first_training_meeting_date) {
        this.first_training_meeting_date = first_training_meeting_date;
    }

    public String getDate_savings_started() {
        return date_savings_started;
    }

    public void setDate_savings_started(String date_savings_started) {
        this.date_savings_started = date_savings_started;
    }

    public String getReinvested_savings_cycle_start() {
        return reinvested_savings_cycle_start;
    }

    public void setReinvested_savings_cycle_start(String reinvested_savings_cycle_start) {
        this.reinvested_savings_cycle_start = reinvested_savings_cycle_start;
    }

    public String getRegistered_members_cycle_start() {
        return registered_members_cycle_start;
    }

    public void setRegistered_members_cycle_start(String registered_members_cycle_start) {
        this.registered_members_cycle_start = registered_members_cycle_start;
    }

    public String getGroup_mgt() {
        return group_mgt;
    }

    public void setGroup_mgt(String group_mgt) {
        this.group_mgt = group_mgt;
    }

    public String getDelete_status() {
        return delete_status;
    }

    public void setDelete_status(String delete_status) {
        this.delete_status = delete_status;
    }
}
