package org.smartregister.chw.core.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class Tally implements Serializable {
    private Hia2Indicator indicator;
    @JsonProperty
    private long id;
    @JsonProperty
    private String value;

    public Hia2Indicator getIndicator() {
        return indicator;
    }

    public void setIndicator(Hia2Indicator indicator) {
        this.indicator = indicator;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public ReportHia2Indicator getReportHia2Indicator() {
        ReportHia2Indicator reportHia2Indicator = new ReportHia2Indicator();
        reportHia2Indicator.setValue(value);
        reportHia2Indicator.setHia2Indicator(indicator);
        return reportHia2Indicator;
    }

    @Override
    public boolean equals(Object o) {
        if (o != null && o instanceof Tally) {
            Tally tally = (Tally) o;
            if (getIndicator().getIndicatorCode().equals(tally.getIndicator().getIndicatorCode())) {
                return true;
            }
        }
        return false;
    }
}

