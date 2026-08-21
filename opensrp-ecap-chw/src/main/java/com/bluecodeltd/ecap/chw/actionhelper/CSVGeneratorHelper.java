package com.bluecodeltd.ecap.chw.actionhelper;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import com.bluecodeltd.ecap.chw.util.Threading;

import com.bluecodeltd.ecap.chw.contract.GenerateCSVContract;

public class CSVGeneratorHelper {
    // Use centralized Threading for background CSV generation

    public interface CSVGenerationCallback {
        void onCompletion();
    }

    public void generateCSVWithProgress(Context context, GenerateCSVContract.Presenter presenter, CSVGenerationCallback callback) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Generating CSV, please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        Threading.io(() -> {
            presenter.generateCSV();
            Threading.main(() -> {
                // The activity/dialog's window may already be gone by the time this background
                // work finishes (user navigated away, rotated, or the activity was destroyed),
                // in which case dismiss() throws IllegalArgumentException: "not attached to
                // window manager". Guard on the activity's lifecycle state where we can tell,
                // and swallow the race otherwise rather than crashing.
                boolean activityGone = context instanceof Activity
                        && (((Activity) context).isFinishing() || ((Activity) context).isDestroyed());
                if (activityGone) {
                    // Skip the completion callback too: callers use it to show further UI
                    // (e.g. a "CSV generated" dialog), which would hit the same problem.
                    return;
                }
                if (progressDialog.isShowing()) {
                    try {
                        progressDialog.dismiss();
                    } catch (IllegalArgumentException ignored) {
                        // Dialog's window was already torn down; nothing to clean up.
                    }
                }
                callback.onCompletion();
            });
        });
    }
}
