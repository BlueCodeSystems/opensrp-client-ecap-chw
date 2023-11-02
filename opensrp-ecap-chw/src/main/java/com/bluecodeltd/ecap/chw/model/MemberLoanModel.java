package com.bluecodeltd.ecap.chw.model;

public class MemberLoanModel {
    private String last_interacted_with;
    private String base_entity_id;
    private String date_created;
    private String group_id;
    private String unique_id;
    private String amount;
    private String date_of_loan;
    private String interest_rate;
    private String duration;
    private String due_date;
    private String loan_number;
    private String started_iga;
    private String delete_status;
    private String facilitator_id;

    public MemberLoanModel() {
        this.last_interacted_with = last_interacted_with;
        this.base_entity_id = base_entity_id;
        this.date_created = date_created;
        this.group_id = group_id;
        this.unique_id = unique_id;
        this.amount = amount;
        this.date_of_loan = date_of_loan;
        this.interest_rate = interest_rate;
        this.duration = duration;
        this.due_date = due_date;
        this.loan_number = loan_number;
        this.started_iga = started_iga;
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

    public String getDate_of_loan() {
        return date_of_loan;
    }

    public void setDate_of_loan(String date_of_loan) {
        this.date_of_loan = date_of_loan;
    }

    public String getInterest_rate() {
        return interest_rate;
    }

    public void setInterest_rate(String interest_rate) {
        this.interest_rate = interest_rate;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDue_date() {
        return due_date;
    }

    public void setDue_date(String due_date) {
        this.due_date = due_date;
    }

    public String getLoan_number() {
        return loan_number;
    }

    public void setLoan_number(String loan_number) {
        this.loan_number = loan_number;
    }

    public String getStarted_iga() {
        return started_iga;
    }

    public void setStarted_iga(String started_iga) {
        this.started_iga = started_iga;
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
