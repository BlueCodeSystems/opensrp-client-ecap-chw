package org.smartregister.chw.core.model;

public class StockUsageItemDetailsModel {
    private String itemDetailsMonth;
    private String itemDetailsYear;
    private String itemDetailsStockValue;

    public StockUsageItemDetailsModel(String itemDetailsMonth, String itemDetailsYear, String itemDetailsStockValue) {
        this.itemDetailsYear = itemDetailsYear;
        this.itemDetailsMonth = itemDetailsMonth;
        this.itemDetailsStockValue = itemDetailsStockValue;
    }

    public String getItemDetailsMonth() {
        return itemDetailsMonth;
    }

    public String getItemDetailsYear() {
        return itemDetailsYear;
    }

    public String getItemDetailsStockValue() {
        return itemDetailsStockValue;
    }
}
