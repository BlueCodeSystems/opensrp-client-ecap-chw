package org.smartregister.chw.core.job;

import android.content.Intent;

import androidx.annotation.NonNull;

import org.smartregister.chw.core.service.StockUsageReportService;
import org.smartregister.family.util.Constants;
import org.smartregister.job.BaseJob;

import timber.log.Timber;

public class StockUsageReportJob extends BaseJob {
    public static final String TAG = "StockUsageReportServiceJob";

    @NonNull
    @Override
    protected Result onRunJob(@NonNull Params params) {
        Timber.v("%s started", TAG);
        getApplicationContext().startService(new Intent(getApplicationContext(), StockUsageReportService.class));
        return params.getExtras().getBoolean(Constants.INTENT_KEY.TO_RESCHEDULE, false) ? Result.RESCHEDULE : Result.SUCCESS;
    }
}
