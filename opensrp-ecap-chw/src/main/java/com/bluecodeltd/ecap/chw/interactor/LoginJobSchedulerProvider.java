package com.bluecodeltd.ecap.chw.interactor;

import com.bluecodeltd.ecap.chw.BuildConfig;
import com.bluecodeltd.ecap.chw.contract.LoginJobScheduler;
import com.bluecodeltd.ecap.chw.job.ScheduleJob;

import org.smartregister.job.DocumentConfigurationServiceJob;
import org.smartregister.job.ImageUploadServiceJob;
import org.smartregister.job.PullUniqueIdsServiceJob;
import org.smartregister.job.SyncLocationsByLevelAndTagsServiceJob;
import org.smartregister.job.SyncServiceJob;

import java.util.concurrent.TimeUnit;

public class LoginJobSchedulerProvider implements LoginJobScheduler {

    private static final int MINIMUM_JOB_FLEX_VALUE = 5;

    @Override
    public void scheduleJobsPeriodically() {
        SyncServiceJob.scheduleJob(SyncServiceJob.TAG, TimeUnit.MINUTES.toMinutes(BuildConfig.DATA_SYNC_DURATION_MINUTES), getFlexValue(BuildConfig
                .DATA_SYNC_DURATION_MINUTES));

        ImageUploadServiceJob.scheduleJob(ImageUploadServiceJob.TAG, TimeUnit.MINUTES.toMinutes(BuildConfig.IMAGE_UPLOAD_MINUTES), getFlexValue(BuildConfig.IMAGE_UPLOAD_MINUTES));

        PullUniqueIdsServiceJob.scheduleJob(PullUniqueIdsServiceJob.TAG, TimeUnit.MINUTES.toMinutes(BuildConfig.PULL_UNIQUE_IDS_MINUTES), getFlexValue(BuildConfig.PULL_UNIQUE_IDS_MINUTES));

        ScheduleJob.scheduleJob(ScheduleJob.TAG, TimeUnit.MINUTES.toMinutes(BuildConfig.SCHEDULE_SERVICE_MINUTES), getFlexValue(BuildConfig.SCHEDULE_SERVICE_MINUTES));

        if (BuildConfig.USE_UNIFIED_REFERRAL_APPROACH)
            DocumentConfigurationServiceJob.scheduleJob(DocumentConfigurationServiceJob.TAG,TimeUnit.MINUTES.toMinutes(BuildConfig.DATA_SYNC_DURATION_MINUTES), getFlexValue(BuildConfig.DATA_SYNC_DURATION_MINUTES));
    }

    @Override
    public void scheduleJobsImmediately() {
        // Run initial job immediately on log in since the job will run a bit later (~ 15 mins +)
        ScheduleJob.scheduleJobImmediately(ScheduleJob.TAG);
        SyncServiceJob.scheduleJobImmediately(SyncServiceJob.TAG);
        SyncLocationsByLevelAndTagsServiceJob.scheduleJobImmediately(SyncLocationsByLevelAndTagsServiceJob.TAG);

        if (BuildConfig.USE_UNIFIED_REFERRAL_APPROACH)
            DocumentConfigurationServiceJob.scheduleJobImmediately(DocumentConfigurationServiceJob.TAG);

    }

    @Override
    public long getFlexValue(int value) {
        int minutes = MINIMUM_JOB_FLEX_VALUE;

        if (value > MINIMUM_JOB_FLEX_VALUE) {

            minutes = (int) Math.ceil(value / 3);
        }

        return Math.max(minutes, MINIMUM_JOB_FLEX_VALUE);
    }
}
