package com.bluecodeltd.ecap.chw.job;

import androidx.annotation.NonNull;

import com.evernote.android.job.Job;

public class RescheduleJob extends Job {
    private final String tag;

    public RescheduleJob(String tag) {
        this.tag = tag;
    }

    @NonNull
    @Override
    protected Result onRunJob(@NonNull Params params) {
        return Result.RESCHEDULE;
    }
}
