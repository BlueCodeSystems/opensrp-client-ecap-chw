package com.bluecodeltd.ecap.chw.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Build;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.util.Arrays;

/**
 * Detects an install that is missing libsqlcipher.so.
 * <p>
 * Play installs the app as a base APK plus a split carrying the native libraries for the
 * device's ABI. A base.apk copied to another phone (ShareIt, Xender, Bluetooth) installs and
 * starts, but the database cannot open, and every screen fails. When that happens the app
 * skips its normal startup and {@link ChwAppComponentFactory} shows
 * {@link com.bluecodeltd.ecap.chw.activity.IncompleteInstallActivity} instead of any screen.
 */
public final class IncompleteInstall {

    private static volatile Boolean missing;
    private static Throwable loadError;

    private IncompleteInstall() {
    }

    /** True when the SQLCipher native library cannot be loaded. The result is computed once. */
    public static boolean isDatabaseLibraryMissing() {
        if (missing == null) {
            synchronized (IncompleteInstall.class) {
                if (missing == null) {
                    try {
                        // What net.sqlcipher.database.SQLiteDatabase.loadLibs() does.
                        System.loadLibrary("sqlcipher");
                        missing = false;
                    } catch (UnsatisfiedLinkError e) {
                        loadError = e;
                        missing = true;
                    }
                }
            }
        }
        return missing;
    }

    /** Records a Crashlytics non-fatal with what is needed to confirm how the app was installed. */
    public static void report(Context context) {
        try {
            String installer = installerOf(context);
            ApplicationInfo info = context.getApplicationInfo();
            String splits = info.splitNames == null ? "none" : Arrays.toString(info.splitNames);
            FirebaseCrashlytics crashlytics = FirebaseCrashlytics.getInstance();
            crashlytics.setCustomKey("install_source", installer);
            crashlytics.setCustomKey("install_splits", splits);
            crashlytics.setCustomKey("device_abis", Arrays.toString(Build.SUPPORTED_ABIS));
            crashlytics.recordException(new IllegalStateException(
                    "Incomplete install: libsqlcipher.so missing (installer=" + installer
                            + ", splits=" + splits + ")", loadError));
        } catch (Throwable ignored) {
            // Reporting must never stop the reinstall screen from showing.
        }
    }

    @SuppressWarnings("deprecation")
    private static String installerOf(Context context) {
        try {
            String pkg = context.getPackageName();
            String installer = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
                    ? context.getPackageManager().getInstallSourceInfo(pkg).getInstallingPackageName()
                    : context.getPackageManager().getInstallerPackageName(pkg);
            return installer == null ? "unknown" : installer;
        } catch (Exception e) {
            return "unknown";
        }
    }
}
