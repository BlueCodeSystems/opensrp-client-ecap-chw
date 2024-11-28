package org.smartregister.chw.core.utils;

import android.os.Build;

import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.smartregister.chw.core.application.TestApplication;
import org.smartregister.chw.core.domain.Hia2Indicator;
import org.smartregister.chw.core.domain.MonthlyTally;
import org.smartregister.chw.core.domain.Report;
import org.smartregister.chw.core.domain.ReportHia2Indicator;
import org.smartregister.chw.core.domain.StockUsage;
import org.smartregister.chw.core.repository.MonthlyTalliesRepository;
import org.smartregister.util.JsonFormUtils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
@Config(application = TestApplication.class, sdk = Build.VERSION_CODES.P)
public class ReportUtilsTest {

    private String providerId = "CHW";
    private String locationId = "54321";
    private String indicatorCode = "10y14y_total_clients";
    private String stockName = "Zinc 10";
    private Date date = new Date();
    private LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

    private MonthlyTally monthlyTally() {
        MonthlyTally monthlyTally = new MonthlyTally();
        Hia2Indicator hia2Indicator = new Hia2Indicator();
        hia2Indicator.setIndicatorCode(indicatorCode);
        monthlyTally.setIndicator(hia2Indicator);
        monthlyTally.setDateSent(date);
        monthlyTally.setMonth(date);
        monthlyTally.setUpdatedAt(date);
        monthlyTally.setEdited(false);
        monthlyTally.setProviderId(providerId);
        monthlyTally.setSubmissionId("some-submission-id");
        monthlyTally.setCreatedAt(date);
        return monthlyTally;
    }

    private StockUsage stockUsage() {
        StockUsage stockUsage = new StockUsage();
        stockUsage.setStockName(stockName);
        stockUsage.setYear(String.valueOf(localDate.getYear()));
        stockUsage.setMonth(String.valueOf(localDate.getMonthValue()));
        stockUsage.setStockUsage("10");
        stockUsage.setProviderId(providerId);
        return stockUsage;
    }

    @Test
    public void testGeValues() {
        Map<String, String> monthlyTallyValues = ReportUtils.geValues(monthlyTally(), null);
        Map<String, String> stockUsageValues = ReportUtils.geValues(null, stockUsage());
        assert monthlyTallyValues != null;
        assertEquals(monthlyTallyValues.get(CoreConstants.JsonAssets.IN_APP_REPORT_MONTH), MonthlyTalliesRepository.DF_YYYYMM.format(date));
        assert stockUsageValues != null;
        assertEquals(stockUsageValues.get(CoreConstants.JsonAssets.STOCK_NAME), stockName);
    }

    @Test
    public void testGetFormSubmissionID() {
        String monthlyTallyFormSubmissionID = providerId + "-" + indicatorCode + "-" + MonthlyTalliesRepository.DF_YYYYMM.format(date);
        String stockUsageTallyFormSubmissionID = providerId + " " + localDate.getYear() + " " + localDate.getMonthValue() + " " + stockName;

        assertEquals(ReportUtils.getFormSubmissionID(stockUsage(), null), stockUsageTallyFormSubmissionID.replaceAll(" ", "-").toUpperCase());
        assertEquals(ReportUtils.getFormSubmissionID(null, monthlyTally()), monthlyTallyFormSubmissionID.replaceAll(" ", "-").toUpperCase());
    }

    @Test
    public void testCreateReport() throws JSONException {
        Date testDate = new DateTime().minusDays(10).toDate();
        String reportType = "monthlyReport";
        List<ReportHia2Indicator> hia2IndicatorsList = new ArrayList<>();
        hia2IndicatorsList.add(new ReportHia2Indicator());
        Calendar calendar = Calendar.getInstance();

        Report report = new Report();
        report.setFormSubmissionId(JsonFormUtils.generateRandomUUIDString());
        report.setHia2Indicators(hia2IndicatorsList);
        report.setLocationId(locationId);
        report.setProviderId(providerId);

        calendar.setTime(testDate);
        report.setReportDate(new DateTime(testDate));
        report.setReportType(reportType);

        JSONObject reportJson = new JSONObject(JsonFormUtils.gson.toJson(report));
        ReportUtils.createReport(hia2IndicatorsList, testDate, reportType);

        assertEquals(reportType, reportJson.getString("reportType"));
        assertEquals(1, reportJson.getJSONArray("hia2Indicators").length());
    }
}