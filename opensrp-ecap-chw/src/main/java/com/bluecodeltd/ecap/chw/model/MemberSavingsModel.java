package com.bluecodeltd.ecap.chw.model;

public class MemberSavingsModel {
    private String last_interacted_with;
    private String base_entity_id;
    private String date_created;
    private String group_id;
    private String unique_id;
    private String amount;
    private String contribution_date;
    private String delete_status;
    private String facilitator_id;

    public MemberSavingsModel() {
        this.last_interacted_with = last_interacted_with;
        this.base_entity_id = base_entity_id;
        this.date_created = date_created;
        this.group_id = group_id;
        this.unique_id = unique_id;
        this.amount = amount;
        this.contribution_date = contribution_date;
        this.delete_status = delete_status;
        this.facilitator_id = facilitator_id;
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

    public String getDate_created() {
        return date_created;
    }

    public void setDate_created(String date_created) {
        this.date_created = date_created;
    }

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public String getUnique_id() {
        return unique_id;
    }

    public void setUnique_id(String unique_id) {
        this.unique_id = unique_id;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getContribution_date() {
        return contribution_date;
    }

    public void setContribution_date(String contribution_date) {
        this.contribution_date = contribution_date;
    }

    public String getDelete_status() {
        return delete_status;
    }

    public void setDelete_status(String delete_status) {
        this.delete_status = delete_status;
    }

    public String getFacilitator_id() {
        return facilitator_id;
    }

    public void setFacilitator_id(String facilitator_id) {
        this.facilitator_id = facilitator_id;
    }
}
