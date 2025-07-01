package com.bluecodeltd.ecap.chw.actionhelper;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.bluecodeltd.ecap.chw.contract.GenerateCSVContract;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CSVGeneratorHelper {
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public interface CSVGenerationCallback {
        void onCompletion();
    }

    public void generateCSVWithProgress(Context context, GenerateCSVContract.Presenter presenter, CSVGenerationCallback callback) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Generating CSV, please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        executor.execute(() -> {
            presenter.generateCSV();
            mainHandler.post(() -> {
                progressDialog.dismiss();
                callback.onCompletion();
            });
        });
    }
}
