package com.bluecodeltd.ecap.chw.util;

import android.os.SystemClock;

import timber.log.Timber;

/**
 * Times the steps of Application.onCreate and logs them once startup finishes
 * ("core=420,libs=1830,...").
 */
public final class StartupTrace {

    private final long start = SystemClock.elapsedRealtime();
    private long last = start;
    private final StringBuilder steps = new StringBuilder();

    /** Records the time since the previous step under {@code name}. */
    public void step(String name) {
        long now = SystemClock.elapsedRealtime();
        if (steps.length() > 0) steps.append(',');
        steps.append(name).append('=').append(now - last);
        last = now;
    }

    public void finish() {
        long total = SystemClock.elapsedRealtime() - start;
        Timber.i("Application.onCreate took %d ms (%s)", total, steps);
    }
}
