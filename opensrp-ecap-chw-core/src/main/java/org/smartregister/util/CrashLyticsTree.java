package org.smartregister.util;

import android.util.Log;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.smartregister.view.activity.DrishtiApplication;

import timber.log.Timber;

public class CrashLyticsTree extends Timber.Tree {
    private String userName;

    @Override
    protected void log(int priority, @Nullable String tag, @NotNull String message, @Nullable Throwable t) {
        if (priority == Log.VERBOSE || priority == Log.DEBUG || priority == Log.INFO) {
            return;
        }

        if (userName == null) {
            try {
                userName = DrishtiApplication.getInstance().getUsername();
            } catch (Exception e) {
                userName = "unknown";
            }
        }
        
        // Crashlytics is disabled in build.gradle, so we just log to standard logcat
        if (t != null) {
            Log.e(tag != null ? tag : "CrashLyticsTree", message, t);
        } else {
            Log.e(tag != null ? tag : "CrashLyticsTree", message);
        }
    }
}
