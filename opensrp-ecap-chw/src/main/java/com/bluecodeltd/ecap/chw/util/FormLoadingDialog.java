package com.bluecodeltd.ecap.chw.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Build;
import android.widget.ProgressBar;

import timber.log.Timber;

public final class FormLoadingDialog {

    private FormLoadingDialog() {
        // Utility
    }

    public static AlertDialog show(Activity activity) {
        if (activity == null) {
            return null;
        }
        if (activity.isFinishing() || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && activity.isDestroyed())) {
            return null;
        }
        int padding = (int) (16 * activity.getResources().getDisplayMetrics().density);
        ProgressBar progressBar = new ProgressBar(activity);
        progressBar.setIndeterminate(true);
        progressBar.setPadding(padding, padding, padding, padding);
        AlertDialog dialog = new AlertDialog.Builder(activity)
                .setView(progressBar)
                .setCancelable(false)
                .create();
        try {
            dialog.show();
        } catch (Exception e) {
            Timber.w(e, "Unable to show form loading dialog");
            return null;
        }
        return dialog;
    }

    public static void dismiss(AlertDialog dialog) {
        if (dialog != null && dialog.isShowing()) {
            try {
                dialog.dismiss();
            } catch (Exception e) {
                Timber.w(e, "Unable to dismiss form loading dialog");
            }
        }
    }
}
