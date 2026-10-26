package com.bluecodeltd.ecap.chw.issue;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Build;

import androidx.annotation.WorkerThread;

import com.bluecodeltd.ecap.chw.BuildConfig;
import com.bluecodeltd.ecap.chw.application.ChwApplication;

import net.sqlcipher.Cursor;

import org.smartregister.repository.AllSharedPreferences;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

/**
 * Collects the device, user and sync details attached to an issue report. Reads the database,
 * so it must run off the main thread.
 */
public final class DiagnosticsCollector {
    private static final int MAX_LOG_LINES = 150;

    private DiagnosticsCollector() {
    }

    /** Device, app, user and sync state. Keys match the fields of an {@code issue_reports} document. */
    @WorkerThread
    public static Map<String, Object> collect(Context context, String screen) {
        Map<String, Object> d = new LinkedHashMap<>();
        d.put("screen", screen);
        d.put("appId", BuildConfig.APPLICATION_ID);
        d.put("appVersionName", BuildConfig.VERSION_NAME);
        d.put("appVersionCode", BuildConfig.VERSION_CODE);
        d.put("buildFlavor", BuildConfig.FLAVOR);
        d.put("buildType", BuildConfig.BUILD_TYPE);
        d.put("deviceManufacturer", Build.MANUFACTURER);
        d.put("deviceModel", Build.MODEL);
        d.put("androidVersion", Build.VERSION.RELEASE);
        d.put("androidSdkInt", Build.VERSION.SDK_INT);
        d.put("freeStorageMb", context.getFilesDir().getUsableSpace() / (1024 * 1024));
        d.put("onlineAtReport", isOnline(context));

        try {
            AllSharedPreferences prefs = org.smartregister.Context.getInstance().allSharedPreferences();
            String provider = prefs.fetchRegisteredANM();
            d.put("providerId", provider);
            d.put("team", prefs.fetchDefaultTeam(provider));
            d.put("teamId", prefs.fetchDefaultTeamId(provider));
            d.put("localityId", prefs.fetchUserLocalityId(provider));
            d.put("lastSyncServerVersion", prefs.fetchLastUpdatedAtDate(0));
        } catch (Exception e) {
            Timber.w(e, "DiagnosticsCollector: could not read user details");
        }

        d.put("unsyncedEvents", count("SELECT COUNT(*) FROM event WHERE syncStatus <> 'Synced'"));
        d.put("unsyncedClients", count("SELECT COUNT(*) FROM client WHERE syncStatus <> 'Synced'"));
        return d;
    }

    /** The most recent log lines from {@link LogBufferTree}, newest last. */
    public static String recentLogs() {
        List<String> lines = LogBufferTree.snapshot();
        int from = Math.max(0, lines.size() - MAX_LOG_LINES);
        return String.join("\n", lines.subList(from, lines.size()));
    }

    public static boolean isOnline(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkCapabilities caps = cm.getNetworkCapabilities(cm.getActiveNetwork());
            return caps != null && caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
        } catch (Exception e) {
            return false;
        }
    }

    private static int count(String sql) {
        try (Cursor cursor = ChwApplication.getInstance().getRepository().getReadableDatabase().rawQuery(sql, new String[0])) {
            return cursor.moveToFirst() ? cursor.getInt(0) : -1;
        } catch (Exception e) {
            Timber.w(e, "DiagnosticsCollector: count failed");
            return -1;
        }
    }
}
