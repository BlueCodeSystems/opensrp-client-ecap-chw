package org.smartregister.chw.core.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.joda.time.DateTime;

import java.util.List;

public class Report {
    @JsonProperty
    private String baseEntityId;

    @JsonProperty
    private String locationId;

    @JsonProperty
    private DateTime reportDate;

    @JsonProperty
    private String reportType;

    @JsonProperty
    private String formSubmissionId;

    @JsonProperty
    private String providerId;

    @JsonProperty
    private String status;

    @JsonProperty
    private Long version;

    @JsonProperty
    private int duration = 0;

    @JsonProperty
    private List<ReportHia2Indicator> hia2Indicators;


    public String getLocationId() {
        return locationId;
    }


    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }


    public DateTime getReportDate() {
        return reportDate;
    }


    public void setReportDate(DateTime reportDate) {
        this.reportDate = reportDate;
    }


    public String getReportType() {
        return reportType;
    }


    public void setReportType(String reportType) {
        this.reportType = reportType;
    }


    public String getFormSubmissionId() {
        return formSubmissionId;
    }


    public void setFormSubmissionId(String formSubmissionId) {
        this.formSubmissionId = formSubmissionId;
    }


    public String getProviderId() {
        return providerId;
    }


    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }


    public String getStatus() {
        return status;
    }


    public void setStatus(String status) {
        this.status = status;
    }


    public Long getVersion() {
        return version;
    }


    public void setVersion(Long version) {
        this.version = version;
    }


    public int getDuration() {
        return duration;
    }


    public void setDuration(int duration) {
        this.duration = duration;
    }


    public List<ReportHia2Indicator> getHia2Indicators() {
        return hia2Indicators;
    }


    public void setHia2Indicators(List<ReportHia2Indicator> hia2Indicators) {
        this.hia2Indicators = hia2Indicators;
    }

    public String getBaseEntityId() {
        return baseEntityId;
    }

    public void setBaseEntityId(String baseEntityId) {
        this.baseEntityId = baseEntityId;
    }
}

