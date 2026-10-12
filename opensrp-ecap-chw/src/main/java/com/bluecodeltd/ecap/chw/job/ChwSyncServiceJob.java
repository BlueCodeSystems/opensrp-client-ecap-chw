package com.bluecodeltd.ecap.chw.job;

import android.content.Intent;

import androidx.annotation.NonNull;

import com.bluecodeltd.ecap.chw.sync.ChwSyncIntentService;

import org.smartregister.AllConstants;
import org.smartregister.job.SyncServiceJob;

import timber.log.Timber;

/**
 * SyncServiceJob that still syncs when the app is in the background. The base job only calls
 * startService(), which Android 12+ rejects from the background (the error is caught and logged
 * in BaseJob), so scheduled syncs never ran unless ECAP was open. When that happens the sync now
 * runs on this job's own background thread.
 */
public class ChwSyncServiceJob extends SyncServiceJob {

    public ChwSyncServiceJob() {
        super(ChwSyncIntentService.class);
    }

    @NonNull
    @Override
    protected Result onRunJob(@NonNull Params params) {
        try {
            getApplicationContext().startService(new Intent(getApplicationContext(), ChwSyncIntentService.class));
        } catch (IllegalStateException e) {
            Timber.i("Background service start not allowed; running sync inside the job");
            try {
                ChwSyncIntentService.runInline(getApplicationContext());
            } catch (Exception syncError) {
                Timber.e(syncError);
            }
        } catch (Exception e) {
            Timber.e(e);
        }
        return params != null && params.getExtras().getBoolean(AllConstants.INTENT_KEY.TO_RESCHEDULE, false) ? Result.RESCHEDULE : Result.SUCCESS;
    }
}
