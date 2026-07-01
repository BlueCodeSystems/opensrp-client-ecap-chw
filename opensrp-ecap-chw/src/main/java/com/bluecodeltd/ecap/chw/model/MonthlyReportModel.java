package com.bluecodeltd.ecap.chw.model;

import java.util.HashMap;
import java.util.Map;

public class MonthlyReportModel {
    private final Map<String, String> additionalFields = new HashMap<>();
    private String base_entity_id;
    private String formSubmissionId;
    private String form_id;
    private String reporting_month;
    private String province;
    private String district;
    private String ward;
    private String facility;
    private String partner;
    private String caseworker_name;
    private String delete_status;
    private String last_interacted_with;

    public String getBase_entity_id() {
        return base_entity_id;
    }

    public void setBase_entity_id(String base_entity_id) {
        this.base_entity_id = base_entity_id;
    }

    public String getFormSubmissionId() {
        return formSubmissionId;
    }

    public void setFormSubmissionId(String formSubmissionId) {
        this.formSubmissionId = formSubmissionId;
    }

    public String getForm_id() {
        return form_id;
    }

    public void setForm_id(String form_id) {
        this.form_id = form_id;
    }

    public String getReporting_month() {
        return reporting_month;
    }

    public void setReporting_month(String reporting_month) {
        this.reporting_month = reporting_month;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getWard() {
        return ward;
    }

    public void setWard(String ward) {
        this.ward = ward;
    }

    public String getFacility() {
        return facility;
    }

    public void setFacility(String facility) {
        this.facility = facility;
    }

    public String getPartner() {
        return partner;
    }

    public void setPartner(String partner) {
        this.partner = partner;
    }

    public String getCaseworker_name() {
        return caseworker_name;
    }

    public void setCaseworker_name(String caseworker_name) {
        this.caseworker_name = caseworker_name;
    }

    public String getDelete_status() {
        return delete_status;
    }

    public void setDelete_status(String delete_status) {
        this.delete_status = delete_status;
    }

    public String getLast_interacted_with() {
        return last_interacted_with;
    }

    public void setLast_interacted_with(String last_interacted_with) {
        this.last_interacted_with = last_interacted_with;
    }

    public Map<String, String> getAdditionalFields() {
        return additionalFields;
    }

    public String getAdditionalField(String key) {
        return additionalFields.get(key);
    }

    public void setAdditionalField(String key, String value) {
        additionalFields.put(key, value);
    }

    public Map<String, String> toValueMap() {
        Map<String, String> values = new HashMap<>();
        values.put("base_entity_id", base_entity_id);
        values.put("formSubmissionId", formSubmissionId);
        values.put("form_id", form_id);
        values.put("reporting_month", reporting_month);
        values.put("province", province);
        values.put("district", district);
        values.put("ward", ward);
        values.put("facility", facility);
        values.put("partner", partner);
        values.put("caseworker_name", caseworker_name);
        values.put("delete_status", delete_status);
        values.put("last_interacted_with", last_interacted_with);
        values.putAll(additionalFields);
        return values;
    }
}
