package com.bluecodeltd.ecap.chw.push;

import android.content.Context;

import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

/**
 * Schedules {@link FlagsPollWorker} to check Directus for new flags and raise local notifications.
 */
public final class FlagsNotificationScheduler {
    private static final String WORK_NAME = "flags_poll_work";
    private static final String IMMEDIATE_WORK_NAME = "flags_poll_work_immediate";
    private static final String DEVICE_REGISTRATION_WORK_NAME = "flags_device_registration_work";

    private FlagsNotificationScheduler() {}

    public static void schedule(Context context) {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        OneTimeWorkRequest immediateRequest = new OneTimeWorkRequest.Builder(FlagsPollWorker.class)
                .setConstraints(constraints)
                .build();

        WorkManager.getInstance(context).enqueueUniqueWork(
                IMMEDIATE_WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                immediateRequest);

        registerDevice(context);

        // 15 minutes is the minimum interval WorkManager allows for periodic work.
        PeriodicWorkRequest periodicRequest = new PeriodicWorkRequest.Builder(
                FlagsPollWorker.class, 15, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .build();

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.REPLACE,
                periodicRequest);
    }

    /**
     * Enqueues a one-time {@link DeviceRegistrationWorker} to POST this device's FCM token to the
     * PMP API. Safe to call repeatedly: the worker de-duplicates and only registers on change.
     */
    public static void registerDevice(Context context) {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(DeviceRegistrationWorker.class)
                .setConstraints(constraints)
                .build();

        WorkManager.getInstance(context).enqueueUniqueWork(
                DEVICE_REGISTRATION_WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                request);
    }
}
