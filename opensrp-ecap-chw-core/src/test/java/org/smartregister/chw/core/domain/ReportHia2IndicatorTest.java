package org.smartregister.chw.core.domain;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

public class ReportHia2IndicatorTest {
    private Hia2Indicator indicator = new Hia2Indicator();
    private ReportHia2Indicator reportHia2Indicator = new ReportHia2Indicator();
    private String IndicatorCode;
    private String Category;
    private String Description;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        IndicatorCode = null;
        Category = "Immunization";
        Description = null;
    }

    @Test
    public void testGetAndSetIndicatorCode() {
        reportHia2Indicator.setIndicatorCode("123");
        Assert.assertEquals(reportHia2Indicator.getIndicatorCode(), "123");
    }

    @Test
    public void testGetAndSetDescription() {
        reportHia2Indicator.setIndicatorCode("123");
        Assert.assertEquals(reportHia2Indicator.getIndicatorCode(), "123");
    }

    @Test
    public void testGetAndSetCategory() {
        reportHia2Indicator.setCategory("123");
        Assert.assertEquals(reportHia2Indicator.getCategory(), "123");
    }


    @Test
    public void testGetAndSetValue() {
        reportHia2Indicator.setValue("1");
        Assert.assertEquals(reportHia2Indicator.getValue(), "1");
    }

    @Test
    public void setHia2Indicator() {
        reportHia2Indicator.setHia2Indicator(indicator);
        Assert.assertEquals(IndicatorCode, reportHia2Indicator.getIndicatorCode());
        Assert.assertEquals(Description, reportHia2Indicator.getDescription());
        Assert.assertEquals(Category, reportHia2Indicator.getCategory());
    }
}