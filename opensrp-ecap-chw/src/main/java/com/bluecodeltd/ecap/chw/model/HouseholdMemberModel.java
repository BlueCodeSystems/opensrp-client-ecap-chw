package com.bluecodeltd.ecap.chw.model;

public class HouseholdMemberModel {
    private String base_entity_id;
    private String unique_id;
    private String household_id;
    private String first_name;
    private String last_name;
    private String member_type;
    private String adolescent_birthdate;
    private String gender;
    private String is_hiv_positive;
    private String is_biological;

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

    public String getHousehold_id() {
        return household_id;
    }

    public void setHousehold_id(String household_id) {
        this.household_id = household_id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getMember_type() {
        return member_type;
    }

    public void setMember_type(String member_type) {
        this.member_type = member_type;
    }

    public String getAdolescent_birthdate() {
        return adolescent_birthdate;
    }

    public void setAdolescent_birthdate(String adolescent_birthdate) {
        this.adolescent_birthdate = adolescent_birthdate;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getIs_hiv_positive() {
        return is_hiv_positive;
    }

    public void setIs_hiv_positive(String is_hiv_positive) {
        this.is_hiv_positive = is_hiv_positive;
    }

    public String getIs_biological() {
        return is_biological;
    }

    public void setIs_biological(String is_biological) {
        this.is_biological = is_biological;
    }
}