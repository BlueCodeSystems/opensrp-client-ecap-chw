package org.smartregister.chw.core.model;

public class StockUsageItemModel {
    private String stockName;
    private String unitsOfMeasure;
    private String stockUsageValue;
    private String providerName;

    public StockUsageItemModel(String stockName, String unitsOfMeasure, String stockUsageValue, String providerName) {
        this.stockName = stockName;
        this.unitsOfMeasure = unitsOfMeasure;
        this.stockUsageValue = stockUsageValue;
        this.providerName = providerName;
    }

    public String getStockName() {
        return stockName;
    }

    public String getUnitsOfMeasure() {
        return unitsOfMeasure;
    }

    public String getStockValue() {
        return stockUsageValue;
    }

    public String getProviderName() {
        return providerName;
    }

}
