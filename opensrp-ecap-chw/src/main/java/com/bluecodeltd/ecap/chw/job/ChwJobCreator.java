package com.bluecodeltd.ecap.chw.job;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bluecodeltd.ecap.chw.application.ChwApplication;
import com.bluecodeltd.ecap.chw.sync.ChwSyncIntentService;
import com.bluecodeltd.ecap.chw.sync.intent.ChwSyncTaskIntentService;
import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;

import org.smartregister.job.DocumentConfigurationServiceJob;
import org.smartregister.job.ExtendedSyncServiceJob;
import org.smartregister.job.ImageUploadServiceJob;
import org.smartregister.job.PullUniqueIdsServiceJob;
import org.smartregister.job.SyncLocationsByLevelAndTagsServiceJob;
import org.smartregister.job.SyncServiceJob;
import org.smartregister.job.SyncTaskServiceJob;
import org.smartregister.job.ValidateSyncDataServiceJob;
import org.smartregister.sync.intent.DocumentConfigurationIntentService;

import timber.log.Timber;

/**
 * Created by keyman on 27/11/2018.
 */
public class ChwJobCreator implements JobCreator {
    @Nullable
    @Override
    public Job create(@NonNull String tag) {
        switch (tag) {
            case SyncServiceJob.TAG:
                return new SyncServiceJob(ChwSyncIntentService.class);
            case ExtendedSyncServiceJob.TAG:
                return new ExtendedSyncServiceJob();
            case PullUniqueIdsServiceJob.TAG:
                return new PullUniqueIdsServiceJob();
            case ValidateSyncDataServiceJob.TAG:
                return new ValidateSyncDataServiceJob();
            case ImageUploadServiceJob.TAG:
                return new ImageUploadServiceJob();
            case SyncTaskServiceJob.TAG:
                return (ChwApplication.getApplicationFlavor().hasTasks()) ? new SyncTaskServiceJob(ChwSyncTaskIntentService.class) : null;
            case ScheduleJob.TAG:
                return new ScheduleJob();
            case SyncLocationsByLevelAndTagsServiceJob.TAG:
                return new SyncLocationsByLevelAndTagsServiceJob();
            case DocumentConfigurationServiceJob.TAG:
                return new DocumentConfigurationServiceJob(DocumentConfigurationIntentService.class);
            default:
                Timber.d("Looks like you tried to create a job " + tag + " that is not declared in the Chw Job Creator");
                return null;
        }
    }
}
