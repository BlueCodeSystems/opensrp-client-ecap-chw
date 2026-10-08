package com.bluecodeltd.ecap.chw.util;

import android.app.Activity;
import android.app.Application;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;

import androidx.annotation.NonNull;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import timber.log.Timber;

/**
 * Keeps an Activity's saved-instance-state Bundle under the binder transaction limit.
 * <p>
 * The Bundle is sent to system_server in activityStopped/activitySlept; anything over ~1MB
 * crashes the app with TransactionTooLargeException (or "Could not copy bitmap to parcel blob")
 * from a stack trace that names no app code. This records which Activity saved what, reports
 * oversized state as a non-fatal with a per-key size breakdown, and drops the largest entries so
 * the worst case is a screen that restores without its UI state instead of a crash.
 * <p>
 * On API 29+ the check runs in onActivityPostSaveInstanceState and sees the complete Bundle. On
 * API 28 it runs from Activity.onSaveInstanceState, which covers the view hierarchy (where the
 * oversized SignaturePad bitmap lived) but not state androidx adds afterwards.
 */
public class SavedStateSizeGuard implements Application.ActivityLifecycleCallbacks {

    private static final int MAX_STATE_BYTES = 500 * 1024;

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
        setCustomKey("last_saved_state_activity", activity.getClass().getName());
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            enforceLimit(activity, outState);
        }
    }

    @Override
    public void onActivityPostSaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
        enforceLimit(activity, outState);
    }

    private void enforceLimit(Activity activity, Bundle outState) {
        int totalBytes = sizeOf(outState);
        setCustomKey("last_saved_state_bytes", totalBytes);
        if (totalBytes <= MAX_STATE_BYTES) {
            return;
        }

        List<KeySize> keySizes = new ArrayList<>();
        for (String key : new ArrayList<>(outState.keySet())) {
            keySizes.add(new KeySize(key, sizeOfKey(outState, key)));
        }
        Collections.sort(keySizes, (a, b) -> Integer.compare(b.bytes, a.bytes));

        String message = "Oversized saved state in " + activity.getClass().getName()
                + ": " + totalBytes + " bytes; keys=" + keySizes;
        Timber.w(message);
        try {
            FirebaseCrashlytics.getInstance().recordException(new IllegalStateException(message));
        } catch (Throwable ignored) {
        }

        for (KeySize keySize : keySizes) {
            outState.remove(keySize.key);
            if (sizeOf(outState) <= MAX_STATE_BYTES) {
                return;
            }
        }
        outState.clear();
    }

    private static int sizeOfKey(Bundle bundle, String key) {
        Bundle single = new Bundle(bundle);
        single.keySet().retainAll(Collections.singleton(key));
        return sizeOf(single);
    }

    private static int sizeOf(Bundle bundle) {
        Parcel parcel = Parcel.obtain();
        try {
            parcel.writeBundle(bundle);
            return parcel.dataSize();
        } catch (RuntimeException e) {
            // A value that can't even be parcelled locally (e.g. a huge Bitmap) would crash the real transaction too.
            return Integer.MAX_VALUE;
        } finally {
            parcel.recycle();
        }
    }

    private static void setCustomKey(String key, String value) {
        try {
            FirebaseCrashlytics.getInstance().setCustomKey(key, value);
        } catch (Throwable ignored) {
        }
    }

    private static void setCustomKey(String key, int value) {
        try {
            FirebaseCrashlytics.getInstance().setCustomKey(key, value);
        } catch (Throwable ignored) {
        }
    }

    private static final class KeySize {
        final String key;
        final int bytes;

        KeySize(String key, int bytes) {
            this.key = key;
            this.bytes = bytes;
        }

        @Override
        public String toString() {
            return key + "=" + bytes;
        }
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, Bundle savedInstanceState) { }

    @Override
    public void onActivityStarted(@NonNull Activity activity) { }

    @Override
    public void onActivityResumed(@NonNull Activity activity) { }

    @Override
    public void onActivityPaused(@NonNull Activity activity) { }

    @Override
    public void onActivityStopped(@NonNull Activity activity) { }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) { }
}
