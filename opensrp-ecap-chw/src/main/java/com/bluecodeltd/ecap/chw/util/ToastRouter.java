package com.bluecodeltd.ecap.chw.util;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.Toast;

import es.dmoral.toasty.Toasty;

/**
 * Utility to defer success toasts until the user lands on the destination screen.
 * Attach a message to the {@link Intent} used for navigation, then call
 * {@link #maybeShowQueuedToast(Activity)} in the destination activity (typically in {@code onResume()}).
 */
public final class ToastRouter {

    private static final String EXTRA_TOAST_MESSAGE = "com.bluecodeltd.ecap.chw.extra.TOAST_MESSAGE";

    private ToastRouter() {
        // Utility class
    }

    /**
     * Attach a success toast message to the supplied intent. The caller is responsible for invoking
     * {@link #maybeShowQueuedToast(Activity)} in the destination activity.
     */
    public static Intent withSuccessToast(Intent intent, String message) {
        if (intent != null && !TextUtils.isEmpty(message)) {
            intent.putExtra(EXTRA_TOAST_MESSAGE, message);
        }
        return intent;
    }

    /**
     * Display a queued toast (if present) and clear it so it will not repeat on future resumes.
     */
    public static void maybeShowQueuedToast(Activity activity) {
        if (activity == null) {
            return;
        }
        Intent intent = activity.getIntent();
        if (intent == null) {
            return;
        }
        String message = intent.getStringExtra(EXTRA_TOAST_MESSAGE);
        if (!TextUtils.isEmpty(message)) {
            Toasty.success(activity.getApplicationContext(), message, Toast.LENGTH_LONG, true).show();
            intent.removeExtra(EXTRA_TOAST_MESSAGE);
        }
    }
}
