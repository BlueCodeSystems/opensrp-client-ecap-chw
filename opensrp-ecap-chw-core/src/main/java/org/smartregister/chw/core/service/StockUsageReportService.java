package org.smartregister.chw.core.service;

import android.app.IntentService;
import android.content.Intent;

import androidx.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.smartregister.chw.core.application.CoreChwApplication;
import org.smartregister.chw.core.dao.StockUsageReportDao;
import org.smartregister.chw.core.domain.StockUsage;
import org.smartregister.chw.core.repository.StockUsageReportRepository;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.FormUtils;
import org.smartregister.chw.core.utils.ReportUtils;

import java.util.List;

public class StockUsageReportService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public StockUsageReportService() {
        super("StockUsageReportService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        if (!StockUsageReportDao.lastInteractedWithinDay()) {
            try {
                StockUsageReportRepository repo = CoreChwApplication.getInstance().getStockUsageRepository();
                List<StockUsage> usages = StockUsageReportDao.getStockUsage();

                for (StockUsage usage : usages) {
                    if (StringUtils.isBlank(usage.getStockName()) || StringUtils.isBlank(usage.getYear()) || StringUtils.isBlank(usage.getMonth()) || StringUtils.isBlank(usage.getStockUsage()))
                        return;

                    String formSubmissionId = ReportUtils.getFormSubmissionID(usage, null);
                    usage.setId(formSubmissionId);
                    repo.addOrUpdateStockUsage(usage);
                    ReportUtils.addEvent(ReportUtils.geValues(null, usage), formSubmissionId, CoreConstants.JSON_FORM.getStockUsageForm(), CoreConstants.TABLE_NAME.STOCK_USAGE_REPORT);
                }

                FormUtils.processEvent();

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
