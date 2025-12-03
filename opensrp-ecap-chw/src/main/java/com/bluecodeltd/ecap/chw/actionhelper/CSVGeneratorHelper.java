package com.bluecodeltd.ecap.chw.actionhelper;

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
                progressDialog.dismiss();
                callback.onCompletion();
            });
        });
    }
}
