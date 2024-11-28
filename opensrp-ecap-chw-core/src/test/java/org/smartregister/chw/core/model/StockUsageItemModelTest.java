package org.smartregister.chw.core.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class StockUsageItemModelTest {
    private String stockName;
    private String unitsOfMeasure;
    private String stockUsage;
    private String providerName;

    @Before
    public void setUp() {
        stockName = "Zinc 10";
        unitsOfMeasure = "Packets";
        stockUsage = "20";
        providerName = "chwone";
    }

    @Test
    public void getStockName() {
        StockUsageItemModel stockUsageItemModel = new StockUsageItemModel(stockName, unitsOfMeasure, stockUsage, providerName);
        Assert.assertEquals("Zinc 10", stockUsageItemModel.getStockName());
    }

    @Test
    public void getUnitsOfMeasure() {
        StockUsageItemModel stockUsageItemModel = new StockUsageItemModel(stockName, unitsOfMeasure, stockUsage, providerName);
        Assert.assertEquals("Packets", stockUsageItemModel.getUnitsOfMeasure());
    }

    @Test
    public void getStockUsage() {
        StockUsageItemModel stockUsageItemModel = new StockUsageItemModel(stockName, unitsOfMeasure, stockUsage, providerName);
        Assert.assertEquals("20", stockUsageItemModel.getStockValue());
    }
}