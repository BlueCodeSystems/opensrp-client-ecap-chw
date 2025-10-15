package com.bluecodeltd.ecap.chw.util;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.github.javiersantos.appupdater.AppUpdater;

import java.util.concurrent.atomic.AtomicBoolean;

public final class UpdateManager {
    private static final AtomicBoolean started = new AtomicBoolean(false);
    private static final long START_DELAY_MS = 2000L;

    private UpdateManager() {}

    public static void startOnce(Context context) {
        if (context == null) return;
        if (started.getAndSet(true)) return;
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (context instanceof Activity) {
                Activity a = (Activity) context;
                if (a.isFinishing()) return;
            }
            // Default configuration uses Google Play. Consider switching to JSON/GitHub if needed.
            new AppUpdater(context).start();
        }, START_DELAY_MS);
    }
}

