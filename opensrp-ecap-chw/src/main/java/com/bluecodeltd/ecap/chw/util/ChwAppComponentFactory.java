package com.bluecodeltd.ecap.chw.util;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.CoreComponentFactory;

import com.bluecodeltd.ecap.chw.activity.IncompleteInstallActivity;

/**
 * On an install missing its native libraries ({@link IncompleteInstall}), creates
 * {@link IncompleteInstallActivity} in place of every Activity and ignores manifest broadcasts.
 * Swapping the class here, before the Activity exists, is the only point early enough: any of
 * the app's own screens would touch the database in onCreate() and crash before it could
 * redirect.
 */
public class ChwAppComponentFactory extends CoreComponentFactory {

    @NonNull
    @Override
    public Activity instantiateActivity(@NonNull ClassLoader cl, @NonNull String className,
                                        @Nullable Intent intent)
            throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        if (IncompleteInstall.isDatabaseLibraryMissing()) {
            className = IncompleteInstallActivity.class.getName();
        }
        return super.instantiateActivity(cl, className, intent);
    }

    @NonNull
    @Override
    public BroadcastReceiver instantiateReceiver(@NonNull ClassLoader cl, @NonNull String className,
                                                 @Nullable Intent intent)
            throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        if (IncompleteInstall.isDatabaseLibraryMissing()) {
            return new IgnoreReceiver();
        }
        return super.instantiateReceiver(cl, className, intent);
    }

    /** Stands in for the app's receivers (sync, alarms, boot) while the database cannot open. */
    public static class IgnoreReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
        }
    }
}
