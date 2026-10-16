package com.bluecodeltd.ecap.chw.sync;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.bluecodeltd.ecap.chw.application.ChwApplication;

import org.smartregister.sync.intent.SyncIntentService;

import java.util.concurrent.locks.ReentrantLock;

import timber.log.Timber;

public class ChwSyncIntentService extends SyncIntentService {

    // One sync at a time, whether it runs in the service or inline from a background job.
    private static final ReentrantLock SYNC_LOCK = new ReentrantLock();

    @Override
    protected void onHandleIntent(Intent intent) {
        // A sync started straight after a cold start must see the restored session, not the
        // transient "logged out" state while ChwApplication is still restoring it.
        ChwApplication.getInstance().awaitSessionRestore();
        // If a sync is already running (e.g. inline from the background job), skip this one rather
        // than queue a second full sync behind it. runInline holds the lock on this same thread,
        // and the lock is reentrant, so its own call still gets through.
        if (!SYNC_LOCK.tryLock()) {
            Timber.i("Sync already running; skipping requested sync");
            return;
        }
        try {
            super.onHandleIntent(intent);
        } finally {
            SYNC_LOCK.unlock();
        }
    }

    @Override
    public int getEventPullLimit() {
        // The whole response is buffered into one String; 1000 events (many carrying Base64
        // signatures) produced 16MB+ bodies and OutOfMemoryError in HTTPAgent.processResponse.
        return 250;
    }

    /**
     * Runs one sync on the calling thread without starting the service. Android 12+ refuses
     * startService() while the app is in the background, so the periodic sync job silently did
     * nothing whenever ECAP was not on screen; the job calls this instead. Must not be called on
     * the main thread. Skips if a sync is already running.
     */
    public static void runInline(@NonNull Context context) {
        if (!SYNC_LOCK.tryLock()) {
            Timber.i("Sync already running; skipping background sync job");
            return;
        }
        try {
            ChwSyncIntentService service = new ChwSyncIntentService();
            service.attachBaseContext(context.getApplicationContext());
            service.init(context.getApplicationContext());
            service.onHandleIntent(new Intent(context, ChwSyncIntentService.class));
        } finally {
            SYNC_LOCK.unlock();
        }
    }
}
