package com.bluecodeltd.ecap.chw.util;

import android.os.SystemClock;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

import timber.log.Timber;

/**
 * Times the steps of Application.onCreate and keeps them in Crashlytics custom keys as it goes,
 * so an ANR during startup shows which step was running and how long the earlier ones took
 * ("startup_steps" = "core=420,libs=1830,..."; "startup_done" stays false until the end).
 */
public final class StartupTrace {

    private final long start = SystemClock.elapsedRealtime();
    private long last = start;
    private final StringBuilder steps = new StringBuilder();

    public StartupTrace() {
        setKey("startup_done", "false");
        setKey("startup_steps", "");
    }

    /** Records the time since the previous step under {@code name}. */
    public void step(String name) {
        long now = SystemClock.elapsedRealtime();
        if (steps.length() > 0) steps.append(',');
        steps.append(name).append('=').append(now - last);
        last = now;
        setKey("startup_steps", steps.toString());
    }

    public void finish() {
        long total = SystemClock.elapsedRealtime() - start;
        setKey("startup_total_ms", String.valueOf(total));
        setKey("startup_done", "true");
        Timber.i("Application.onCreate took %d ms (%s)", total, steps);
    }

    private static void setKey(String key, String value) {
        try {
            FirebaseCrashlytics.getInstance().setCustomKey(key, value);
        } catch (Throwable ignored) {
            // Crashlytics unavailable; timing is diagnostic only.
        }
    }
}
