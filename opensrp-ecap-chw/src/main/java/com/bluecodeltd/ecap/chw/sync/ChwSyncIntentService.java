package com.bluecodeltd.ecap.chw.sync;

import android.content.Intent;

import com.bluecodeltd.ecap.chw.application.ChwApplication;

import org.smartregister.sync.intent.SyncIntentService;

public class ChwSyncIntentService extends SyncIntentService {

    @Override
    protected void onHandleIntent(Intent intent) {
        // A sync started straight after a cold start must see the restored session, not the
        // transient "logged out" state while ChwApplication is still restoring it.
        ChwApplication.getInstance().awaitSessionRestore();
        super.onHandleIntent(intent);
    }

    @Override
    public int getEventPullLimit() {
        return 1000;
    }
}
