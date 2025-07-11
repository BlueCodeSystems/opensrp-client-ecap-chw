package org.smartregister.chw.core.utils;

import android.os.Build;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.smartregister.chw.core.activity.CoreStockInventoryReportActivity;
import org.smartregister.chw.core.application.TestApplication;

import java.util.LinkedHashMap;
import java.util.Map;

@RunWith(RobolectricTestRunner.class)
@Config(application = TestApplication.class, sdk = Build.VERSION_CODES.P)
public class StockUsageReportUtilsTest {

    private LocalDate testDate = new LocalDate();
    private Map<String, String> monthsAndYearsMap = new LinkedHashMap<>();
    private CoreStockInventoryReportActivity reportActivity;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        reportActivity = Robolectric.buildActivity(CoreStockInventoryReportActivity.class).get();
    }

    @Test
    public void testGetPreviousMonths() {
        for (int i = 0; i < 12; i++) {
            String month = StockUsageReportUtils.monthConverter(testDate.minusMonths(i).getMonthOfYear(), reportActivity);
            String year = String.valueOf(testDate.minusMonths(i).getYear());
            monthsAndYearsMap.put(month, year);
        }
        Assert.assertEquals(StockUsageReportUtils.getPreviousMonths(reportActivity), monthsAndYearsMap);
    }

    @Test
    public void testGetMonthNumber() {
        Assert.assertEquals(StockUsageReportUtils.getMonthNumber("January".substring(0, 3)), "01");
        Assert.assertEquals(StockUsageReportUtils.getMonthNumber("February".substring(0, 3)), "02");
        Assert.assertEquals(StockUsageReportUtils.getMonthNumber("March".substring(0, 3)), "03");
        Assert.assertEquals(StockUsageReportUtils.getMonthNumber("April".substring(0, 3)), "04");
        Assert.assertEquals(StockUsageReportUtils.getMonthNumber("May".substring(0, 3)), "05");
        Assert.assertEquals(StockUsageReportUtils.getMonthNumber("June".substring(0, 3)), "06");
        Assert.assertEquals(StockUsageReportUtils.getMonthNumber("July".substring(0, 3)), "07");
        Assert.assertEquals(StockUsageReportUtils.getMonthNumber("August".substring(0, 3)), "08");
        Assert.assertEquals(StockUsageReportUtils.getMonthNumber("September".substring(0, 3)), "09");
        Assert.assertEquals(StockUsageReportUtils.getMonthNumber("October".substring(0, 3)), "10");
        Assert.assertEquals(StockUsageReportUtils.getMonthNumber("November".substring(0, 3)), "11");
        Assert.assertEquals(StockUsageReportUtils.getMonthNumber("December".substring(0, 3)), "12");
    }

    @Test
    public void testGetFormattedItem() {
        Assert.assertEquals(StockUsageReportUtils.getFormattedItem("Panadol", reportActivity), "Paracetamol");
        Assert.assertEquals(StockUsageReportUtils.getFormattedItem("ORS 5", reportActivity), "ORS 5");
        Assert.assertEquals(StockUsageReportUtils.getFormattedItem("Zinc 10", reportActivity), "Zinc 10");
        Assert.assertEquals(StockUsageReportUtils.getFormattedItem("ALU 6", reportActivity), "ALU 6");
        Assert.assertEquals(StockUsageReportUtils.getFormattedItem("ALU 12", reportActivity), "ALU 12");
        Assert.assertEquals(StockUsageReportUtils.getFormattedItem("ALU 24", reportActivity), "ALU 24");
        Assert.assertEquals(StockUsageReportUtils.getFormattedItem("ALU 18", reportActivity), "ALU 18");
        Assert.assertEquals(StockUsageReportUtils.getFormattedItem("COC", reportActivity), "COC");
        Assert.assertEquals(StockUsageReportUtils.getFormattedItem("POP", reportActivity), "POP");
        Assert.assertEquals(StockUsageReportUtils.getFormattedItem("Emergency contraceptive", reportActivity), "Emergency contraceptive");
        Assert.assertEquals(StockUsageReportUtils.getFormattedItem("RDTs", reportActivity), "RDTs");
        Assert.assertEquals(StockUsageReportUtils.getFormattedItem("Male condom", reportActivity), "Male Condoms");
        Assert.assertEquals(StockUsageReportUtils.getFormattedItem("Female condom", reportActivity), "Female Condoms");
        Assert.assertEquals(StockUsageReportUtils.getFormattedItem("Standard day method", reportActivity), "Cycle beads (Standard day method)");

    }

    @Test
    public void testGetUnitOfMeasure() {
        Assert.assertEquals(StockUsageReportUtils.getUnitOfMeasure("ORS 5", reportActivity), "Packets");
        Assert.assertEquals(StockUsageReportUtils.getUnitOfMeasure("Zinc 10", reportActivity), "Tablets");
        Assert.assertEquals(StockUsageReportUtils.getUnitOfMeasure("COC", reportActivity), "Packs");
        Assert.assertEquals(StockUsageReportUtils.getUnitOfMeasure("RDTs", reportActivity), "Tests");
        Assert.assertEquals(StockUsageReportUtils.getUnitOfMeasure("Male condom", reportActivity), "Pieces");
    }

    @Test
    public void testMonthConverter() {
        Assert.assertEquals(StockUsageReportUtils.monthConverter(1, reportActivity), "January");
        Assert.assertEquals(StockUsageReportUtils.monthConverter(2, reportActivity), "February");
        Assert.assertEquals(StockUsageReportUtils.monthConverter(3, reportActivity), "March");
        Assert.assertEquals(StockUsageReportUtils.monthConverter(4, reportActivity), "April");
        Assert.assertEquals(StockUsageReportUtils.monthConverter(5, reportActivity), "May");
        Assert.assertEquals(StockUsageReportUtils.monthConverter(6, reportActivity), "June");
        Assert.assertEquals(StockUsageReportUtils.monthConverter(7, reportActivity), "July");
        Assert.assertEquals(StockUsageReportUtils.monthConverter(8, reportActivity), "August");
        Assert.assertEquals(StockUsageReportUtils.monthConverter(9, reportActivity), "September");
        Assert.assertEquals(StockUsageReportUtils.monthConverter(10, reportActivity), "October");
        Assert.assertEquals(StockUsageReportUtils.monthConverter(11, reportActivity), "November");
        Assert.assertEquals(StockUsageReportUtils.monthConverter(12, reportActivity), "December");
    }
}