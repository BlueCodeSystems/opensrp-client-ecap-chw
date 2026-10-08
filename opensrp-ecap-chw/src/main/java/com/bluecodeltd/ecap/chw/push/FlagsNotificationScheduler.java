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

    /**
     * Called from Application.onCreate, so it runs on every process start -- including the
     * background starts WorkManager itself makes to run these workers. Keep work that is already
     * queued instead of replacing it: REPLACE cancelled a running poll and reset the periodic
     * schedule each time, which only produced more background process starts.
     */
    public static void schedule(Context context) {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        OneTimeWorkRequest immediateRequest = new OneTimeWorkRequest.Builder(FlagsPollWorker.class)
                .setConstraints(constraints)
                .build();

        WorkManager.getInstance(context).enqueueUniqueWork(
                IMMEDIATE_WORK_NAME,
                ExistingWorkPolicy.KEEP,
                immediateRequest);

        enqueueDeviceRegistration(context, ExistingWorkPolicy.KEEP);

        // 15 minutes is the minimum interval WorkManager allows for periodic work.
        PeriodicWorkRequest periodicRequest = new PeriodicWorkRequest.Builder(
                FlagsPollWorker.class, 15, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .build();

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                periodicRequest);
    }

    /**
     * Enqueues a one-time {@link DeviceRegistrationWorker} to POST this device's FCM token to the
     * PMP API. Safe to call repeatedly: the worker de-duplicates and only registers on change.
     */
    public static void registerDevice(Context context) {
        // A new FCM token must replace any registration still queued with the old one.
        enqueueDeviceRegistration(context, ExistingWorkPolicy.REPLACE);
    }

    private static void enqueueDeviceRegistration(Context context, ExistingWorkPolicy policy) {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(DeviceRegistrationWorker.class)
                .setConstraints(constraints)
                .build();

        WorkManager.getInstance(context).enqueueUniqueWork(
                DEVICE_REGISTRATION_WORK_NAME,
                policy,
                request);
    }
}
