package org.smartregister.chw.core.utils;

import android.util.Log;

import androidx.annotation.Nullable;

import com.google.gson.Gson;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.CoreLibrary;
import org.smartregister.chw.core.application.CoreChwApplication;
import org.smartregister.chw.core.domain.Hia2Indicator;
import org.smartregister.chw.core.domain.MonthlyTally;
import org.smartregister.chw.core.domain.Report;
import org.smartregister.chw.core.domain.ReportHia2Indicator;
import org.smartregister.chw.core.domain.StockUsage;
import org.smartregister.chw.core.repository.MonthlyTalliesRepository;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.domain.Obs;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.util.JsonFormUtils;
import org.smartregister.util.Utils;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.smartregister.chw.anc.util.NCUtils.getSyncHelper;

public class ReportUtils {
    private static final String TAG = ReportUtils.class.getCanonicalName();

    public static void createReport(List<ReportHia2Indicator> hia2Indicators, Date month, String reportType) {
        try {
            String providerId = CoreChwApplication.getInstance().getContext().allSharedPreferences().fetchRegisteredANM();
            String locationId = CoreChwApplication.getInstance().getContext().allSharedPreferences().getPreference(CoreConstants.CURRENT_LOCATION_ID);
            Report report = new Report();
            report.setFormSubmissionId(JsonFormUtils.generateRandomUUIDString());
            report.setHia2Indicators(hia2Indicators);
            report.setLocationId(locationId);
            report.setProviderId(providerId);

            // Get the second last day of the month
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(month);
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH) - 2);

            report.setReportDate(new DateTime(calendar.getTime()));
            report.setReportType(reportType);
            JSONObject reportJson = new JSONObject(JsonFormUtils.gson.toJson(report));
            try {
                CoreChwApplication.getInstance().hia2ReportRepository().addReport(reportJson);
            } catch (Exception e) {
                Log.e(TAG, "Exception", e);
            }

        } catch (Exception e) {
            Log.e(TAG, e.toString(), e);
        }
    }

    public static String getFormSubmissionID(@Nullable StockUsage usage, @Nullable MonthlyTally monthlyTally) {
        String formSubmissionID = null;
        if (usage != null)
            formSubmissionID = (usage.getProviderId() + "-" + usage.getYear() + "-" + usage.getMonth() + "-" + usage.getStockName());
        else if (monthlyTally != null) {
            formSubmissionID = monthlyTally.getProviderId() + "-" + monthlyTally.getIndicator().getIndicatorCode() + "-" + MonthlyTalliesRepository.DF_YYYYMM.format(monthlyTally.getMonth());
        }
        return formSubmissionID != null ? formSubmissionID.replaceAll(" ", "-").toUpperCase() : null;
    }

    public static void addEvent(Map<String, String> values, String formSubmissionId, String formName, String tableName) throws JSONException {
        JSONObject form = FormUtils.getFormUtils().getFormJson(formName);
        JSONObject stepOne = form.getJSONObject(org.smartregister.chw.anc.util.JsonFormUtils.STEP1);
        JSONArray jsonArray = stepOne.getJSONArray(org.smartregister.chw.anc.util.JsonFormUtils.FIELDS);
        AllSharedPreferences allSharedPreferences = Utils.getAllSharedPreferences();
        FormUtils.updateFormField(jsonArray, values);

        String baseEntityID = UUID.randomUUID().toString();
        JSONObject jsonEventStr = CoreLibrary.getInstance().context().getEventClientRepository().getEventsByFormSubmissionId(formSubmissionId);
        if (jsonEventStr != null)
            baseEntityID = (new Gson().fromJson(jsonEventStr.toString(), Event.class)).getBaseEntityId();

        Event baseEvent = org.smartregister.chw.anc.util.JsonFormUtils.processJsonForm(allSharedPreferences, form.toString(), tableName);
        baseEvent.setFormSubmissionId(formSubmissionId);
        baseEvent.setBaseEntityId(baseEntityID);

        CoreJsonFormUtils.tagSyncMetadata(org.smartregister.family.util.Utils.context().allSharedPreferences(), baseEvent);
        JSONObject eventJson = new JSONObject(CoreJsonFormUtils.gson.toJson(baseEvent));
        getSyncHelper().addEvent(baseEntityID, eventJson);
    }

    public static Map<String, String> geValues(@Nullable MonthlyTally monthlyTally, @Nullable StockUsage usage) {
        Map<String, String> values = new HashMap<>();
        if (usage != null) {
            values.put(CoreConstants.JsonAssets.STOCK_NAME, usage.getStockName());
            values.put(CoreConstants.JsonAssets.STOCK_YEAR, usage.getYear());
            values.put(CoreConstants.JsonAssets.STOCK_MONTH, usage.getMonth());
            values.put(CoreConstants.JsonAssets.STOCK_USAGE, usage.getStockUsage());
            values.put(CoreConstants.JsonAssets.STOCK_PROVIDER, usage.getProviderId());
            return values;
        } else if (monthlyTally != null) {
            values.put(CoreConstants.JsonAssets.IN_APP_REPORT_INDICATOR_CODE, monthlyTally.getIndicator().getIndicatorCode());
            values.put(CoreConstants.JsonAssets.IN_APP_REPORT_MONTH, MonthlyTalliesRepository.DF_YYYYMM.format(monthlyTally.getMonth()));
            values.put(CoreConstants.JsonAssets.IN_APP_REPORT_EDITED, monthlyTally.isEdited() ? "1" : "0");
            values.put(CoreConstants.JsonAssets.IN_APP_REPORT_DATE_SENT, String.valueOf(monthlyTally.getDateSent().getTime()));
            values.put(CoreConstants.JsonAssets.IN_APP_REPORT_CREATED_AT, String.valueOf(monthlyTally.getCreatedAt().getTime()));
            values.put(CoreConstants.JsonAssets.IN_APP_REPORT_VALUE, String.valueOf(monthlyTally.getValue()));
            return values;
        }
        return null;
    }

    public static StockUsage getStockUsageFromObs(List<Obs> stockObs) {
        StockUsage usage = new StockUsage();
        for (Obs obs : stockObs) {
            if (obs.getFormSubmissionField().equals(CoreConstants.JsonAssets.STOCK_NAME)) {
                String value = StockUsageReportUtils.getObsValue(obs);
                if (value != null) {
                    usage.setStockName(value);
                    continue;
                } else
                    return null;
            } else if (obs.getFormSubmissionField().equals(CoreConstants.JsonAssets.STOCK_YEAR)) {
                String value = StockUsageReportUtils.getObsValue(obs);
                if (value != null) {
                    usage.setYear(value);
                    continue;
                } else
                    return null;
            } else if (obs.getFormSubmissionField().equals(CoreConstants.JsonAssets.STOCK_MONTH)) {
                String value = StockUsageReportUtils.getObsValue(obs);
                if (value != null) {
                    usage.setMonth(value);
                    continue;
                } else
                    return null;
            } else if (obs.getFormSubmissionField().equals(CoreConstants.JsonAssets.STOCK_USAGE)) {
                String value = StockUsageReportUtils.getObsValue(obs);
                if (value != null) {
                    usage.setStockUsage(value);
                    continue;
                } else
                    return null;
            } else if (obs.getFormSubmissionField().equals(CoreConstants.JsonAssets.STOCK_PROVIDER)) {
                String value = StockUsageReportUtils.getObsValue(obs);
                if (value != null)
                    usage.setProviderId(value);
                else
                    return null;
            }
        }
        return usage;
    }

    public static MonthlyTally getMonthlyTallyFromObs(List<Obs> stockObs) {
        MonthlyTally monthlyTally = new MonthlyTally();
        for (Obs obs : stockObs) {
            if (obs.getFormSubmissionField().equals(CoreConstants.JsonAssets.IN_APP_REPORT_INDICATOR_CODE)) {
                String value = StockUsageReportUtils.getObsValue(obs);
                if (value != null) {
                    Hia2Indicator hia2Indicator = new Hia2Indicator();
                    hia2Indicator.setIndicatorCode(value);
                    monthlyTally.setIndicator(hia2Indicator);
                    continue;
                } else
                    return null;
            } else if (obs.getFormSubmissionField().equals(CoreConstants.JsonAssets.IN_APP_REPORT_MONTH)) {
                String value = StockUsageReportUtils.getObsValue(obs);
                if (value != null) {
                    try {
                        monthlyTally.setMonth(MonthlyTalliesRepository.DF_YYYYMM.parse(value));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    continue;
                } else
                    return null;
            } else if (obs.getFormSubmissionField().equals(CoreConstants.JsonAssets.IN_APP_REPORT_EDITED)) {
                String value = StockUsageReportUtils.getObsValue(obs);
                if (value != null) {
                    monthlyTally.setEdited("1".equals(value));
                    continue;
                } else
                    return null;
            } else if (obs.getFormSubmissionField().equals(CoreConstants.JsonAssets.IN_APP_REPORT_DATE_SENT)) {
                String value = StockUsageReportUtils.getObsValue(obs);
                if (value != null) {
                    monthlyTally.setDateSent(new Date(Long.parseLong(value)));
                    continue;
                } else
                    return null;
            } else if (obs.getFormSubmissionField().equals(CoreConstants.JsonAssets.IN_APP_REPORT_CREATED_AT)) {
                String value = StockUsageReportUtils.getObsValue(obs);
                if (value != null)
                    monthlyTally.setCreatedAt(new Date(Long.parseLong(value)));
                else
                    return null;
            } else if (obs.getFormSubmissionField().equals(CoreConstants.JsonAssets.IN_APP_REPORT_VALUE)) {
                String value = StockUsageReportUtils.getObsValue(obs);
                if (value != null)
                    monthlyTally.setValue(value);
                else
                    return null;
            }
        }
        return monthlyTally;
    }
}
