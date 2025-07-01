package org.smartregister.chw.core.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class StockUsageItemDetailsModelTest {
    private String itemDetailsMonth;
    private String itemDetailsYear;
    private String itemDetailsStockCount;

    @Before
    public void setUp() {
        itemDetailsMonth = "January";
        itemDetailsYear = "2020";
        itemDetailsStockCount = "20";
    }

    @Test
    public void getItemDetailsMonth() {
        StockUsageItemDetailsModel stockUsageItemDetailsModel = new StockUsageItemDetailsModel(itemDetailsMonth, itemDetailsYear, itemDetailsStockCount);
        Assert.assertEquals("January", stockUsageItemDetailsModel.getItemDetailsMonth());

    }

    @Test
    public void getItemDetailsYear() {
        StockUsageItemDetailsModel stockUsageItemDetailsModel = new StockUsageItemDetailsModel(itemDetailsMonth, itemDetailsYear, itemDetailsStockCount);
        Assert.assertEquals("2020", stockUsageItemDetailsModel.getItemDetailsYear());
    }

    @Test
    public void getItemDetailsStockUsage() {
        StockUsageItemDetailsModel stockUsageItemDetailsModel = new StockUsageItemDetailsModel(itemDetailsMonth, itemDetailsYear, itemDetailsStockCount);
        Assert.assertEquals("20", stockUsageItemDetailsModel.getItemDetailsStockValue());
    }
}