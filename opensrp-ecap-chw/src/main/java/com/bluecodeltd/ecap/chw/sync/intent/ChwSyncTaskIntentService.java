package com.bluecodeltd.ecap.chw.sync.intent;

import android.content.Intent;

import com.bluecodeltd.ecap.chw.application.ChwApplication;
import org.smartregister.chw.core.utils.CoreReferralUtils;
import com.bluecodeltd.ecap.chw.sync.helper.ChwTaskServiceHelper;
import org.smartregister.sync.intent.SyncTaskIntentService;

public class ChwSyncTaskIntentService extends SyncTaskIntentService {
    @Override
    protected void onHandleIntent(Intent intent) {
        ChwApplication.getInstance().awaitSessionRestore();
        ChwTaskServiceHelper taskServiceHelper = ChwTaskServiceHelper.getInstance();
        taskServiceHelper.syncTasks();
        CoreReferralUtils.completeClosedReferralTasks();
    }
}
