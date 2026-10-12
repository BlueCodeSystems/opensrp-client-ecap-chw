package com.bluecodeltd.ecap.chw.job;

import android.content.Intent;

import androidx.annotation.NonNull;

import org.smartregister.AllConstants;
import org.smartregister.CoreLibrary;
import org.smartregister.Context;
import org.smartregister.job.DocumentConfigurationServiceJob;
import org.smartregister.service.DocumentConfigurationService;
import org.smartregister.service.HTTPAgent;
import org.smartregister.sync.intent.DocumentConfigurationIntentService;

import timber.log.Timber;

/**
 * DocumentConfigurationServiceJob that still runs when the app is in the background. Android 12+
 * rejects startService() from the background, so when that happens the manifest fetch the service
 * would have done runs on this job's own background thread instead.
 */
public class ChwDocumentConfigurationServiceJob extends DocumentConfigurationServiceJob {

    public ChwDocumentConfigurationServiceJob() {
        super(DocumentConfigurationIntentService.class);
    }

    @NonNull
    @Override
    protected Result onRunJob(@NonNull Params params) {
        try {
            getApplicationContext().startService(new Intent(getApplicationContext(), DocumentConfigurationIntentService.class));
        } catch (IllegalStateException e) {
            Timber.i("Background service start not allowed; fetching document configuration inside the job");
            fetchManifestInline();
        } catch (Exception e) {
            Timber.e(e);
        }
        return params != null && params.getExtras().getBoolean(AllConstants.INTENT_KEY.TO_RESCHEDULE, false) ? Result.RESCHEDULE : Result.SUCCESS;
    }

    // Same steps as DocumentConfigurationIntentService.onStartCommand + onHandleIntent.
    private void fetchManifestInline() {
        try {
            CoreLibrary coreLibrary = CoreLibrary.getInstance();
            Context context = coreLibrary.context();
            HTTPAgent httpAgent = context.getHttpAgent();
            httpAgent.setConnectTimeout(coreLibrary.getSyncConfiguration().getConnectTimeout());
            httpAgent.setReadTimeout(coreLibrary.getSyncConfiguration().getReadTimeout());
            new DocumentConfigurationService(httpAgent, context.getManifestRepository(),
                    context.getClientFormRepository(), context.configuration()).fetchManifest();
        } catch (Exception e) {
            Timber.e(e);
        }
    }
}
