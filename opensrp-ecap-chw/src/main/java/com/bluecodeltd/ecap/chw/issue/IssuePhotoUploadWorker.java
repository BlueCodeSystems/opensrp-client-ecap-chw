package com.bluecodeltd.ecap.chw.issue;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.work.Constraints;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

import timber.log.Timber;

/**
 * Uploads queued issue-report photos to Firebase Storage at {@link IssueReportStore#storagePath}.
 * The report document already names that path, so nothing else needs updating once a photo lands.
 * A photo is deleted from the phone only after its upload succeeds.
 */
public class IssuePhotoUploadWorker extends Worker {
    private static final String WORK_NAME = "issue_photo_upload";

    public IssuePhotoUploadWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    /** Uploads any waiting photos as soon as the device is online. */
    public static void enqueue(Context context) {
        OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(IssuePhotoUploadWorker.class)
                .setConstraints(new Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build())
                .build();
        WorkManager.getInstance(context).enqueueUniqueWork(WORK_NAME, ExistingWorkPolicy.APPEND_OR_REPLACE, request);
    }

    @NonNull
    @Override
    public Result doWork() {
        List<File> photos = IssueReportStore.pendingPhotos(getApplicationContext());
        boolean allSent = true;
        for (File photo : photos) {
            String reportId = IssueReportStore.reportId(photo);
            try {
                Tasks.await(FirebaseStorage.getInstance().getReference()
                                .child(IssueReportStore.storagePath(reportId))
                                .putFile(Uri.fromFile(photo), new StorageMetadata.Builder()
                                        .setContentType("image/jpeg")
                                        .setCustomMetadata("reportId", reportId)
                                        .build()),
                        2, TimeUnit.MINUTES);
                if (!photo.delete()) {
                    Timber.w("IssuePhotoUploadWorker: could not delete %s", photo);
                }
                Timber.i("IssuePhotoUploadWorker: uploaded photo for report %s", reportId);
            } catch (Exception e) {
                Timber.e(e, "IssuePhotoUploadWorker: upload failed for report %s", reportId);
                allSent = false;
            }
        }
        return allSent ? Result.success() : Result.retry();
    }
}
