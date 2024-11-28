package org.smartregister.chw.core.model;

public class MonthStockUsageModel {
    private String month;
    private String year;

    public MonthStockUsageModel(String month, String year) {
        this.year = year;
        this.month = month;
    }

    public String getMonth() {
        return month;
    }

    public String getYear() {
        return year;
    }
}