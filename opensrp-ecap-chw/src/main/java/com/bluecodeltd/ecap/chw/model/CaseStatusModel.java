package com.bluecodeltd.ecap.chw.model;

public class CaseStatusModel {
    String household_id,first_name,last_name,unique_id,case_status;
    String de_registration_date, reason, graduation_benchmark, exited_graduation_reason;
    String date_of_death, district_moved_to, vca_receiving_caseworker, other_reason;
    String ovc_district, ovc_name, location_moved_to;

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

    public String getUnique_id() {
        return unique_id;
    }

    public void setUnique_id(String unique_id) {
        this.unique_id = unique_id;
    }

    public String getCase_status() {
        return case_status;
    }

    public void setCase_status(String case_status) {
        this.case_status = case_status;
    }

    public String getDe_registration_date() {
        return de_registration_date;
    }

    public void setDe_registration_date(String de_registration_date) {
        this.de_registration_date = de_registration_date;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getGraduation_benchmark() {
        return graduation_benchmark;
    }

    public void setGraduation_benchmark(String graduation_benchmark) {
        this.graduation_benchmark = graduation_benchmark;
    }

    public String getExited_graduation_reason() {
        return exited_graduation_reason;
    }

    public void setExited_graduation_reason(String exited_graduation_reason) {
        this.exited_graduation_reason = exited_graduation_reason;
    }

    public String getDate_of_death() {
        return date_of_death;
    }

    public void setDate_of_death(String date_of_death) {
        this.date_of_death = date_of_death;
    }

    public String getDistrict_moved_to() {
        return district_moved_to;
    }

    public void setDistrict_moved_to(String district_moved_to) {
        this.district_moved_to = district_moved_to;
    }

    public String getVca_receiving_caseworker() {
        return vca_receiving_caseworker;
    }

    public void setVca_receiving_caseworker(String vca_receiving_caseworker) {
        this.vca_receiving_caseworker = vca_receiving_caseworker;
    }

    public String getOther_reason() {
        return other_reason;
    }

    public void setOther_reason(String other_reason) {
        this.other_reason = other_reason;
    }

    public String getOvc_district() {
        return ovc_district;
    }

    public void setOvc_district(String ovc_district) {
        this.ovc_district = ovc_district;
    }

    public String getOvc_name() {
        return ovc_name;
    }

    public void setOvc_name(String ovc_name) {
        this.ovc_name = ovc_name;
    }

    public String getLocation_moved_to() {
        return location_moved_to;
    }

    public void setLocation_moved_to(String location_moved_to) {
        this.location_moved_to = location_moved_to;
    }

    private final java.util.Map<String, String> additionalFields = new java.util.HashMap<>();

    public java.util.Map<String, String> getAdditionalFields() {
        return additionalFields;
    }

    public String getAdditionalField(String key) {
        if (key == null) return null;
        return additionalFields.get(key);
    }

    public void setAdditionalField(String key, String value) {
        if (key == null) return;
        additionalFields.put(key, value);
    }
}

