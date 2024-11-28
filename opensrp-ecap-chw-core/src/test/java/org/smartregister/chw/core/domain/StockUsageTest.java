package org.smartregister.chw.core.domain;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class StockUsageTest {
    private static SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
    private StockUsage stockUsage = new StockUsage();
    private Date date;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        date = sdf.parse("2020-02-08");
    }

    @Test
    public void testGetAndSetCreatedAt() {
        stockUsage.setCreatedAt(date);
        Assert.assertEquals(stockUsage.getCreatedAt(), date);
    }

    @Test
    public void testGetAndSetUpdatedAt() {
        stockUsage.setUpdatedAt(date);
        Assert.assertEquals(stockUsage.getUpdatedAt(), date);
    }

    @Test
    public void testSetAndGetId() {
        stockUsage.setId("123");
        Assert.assertEquals(stockUsage.getId(), "123");
    }

    @Test
    public void testGetAndSetStockName() {
        stockUsage.setStockName("zinc 10");
        Assert.assertEquals(stockUsage.getStockName(), "zinc 10");
    }

    @Test
    public void testGetAndSetStockUsage() {
        stockUsage.setStockUsage("30");
        Assert.assertEquals(stockUsage.getStockUsage(), "30");
    }

    @Test
    public void testGetAndSetYear() {
        stockUsage.setYear("2020");
        Assert.assertEquals(stockUsage.getYear(), "2020");
    }

    @Test
    public void testSetAndGetMonth() {
        stockUsage.setMonth("January");
        Assert.assertEquals(stockUsage.getMonth(), "January");
    }
}