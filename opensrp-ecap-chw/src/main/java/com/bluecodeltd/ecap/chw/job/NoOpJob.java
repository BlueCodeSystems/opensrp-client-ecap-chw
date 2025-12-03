package com.bluecodeltd.ecap.chw.job;

import androidx.annotation.NonNull;

import com.evernote.android.job.Job;
import com.evernote.android.job.Job;

public class NoOpJob extends Job {
    private final String tag;
    public NoOpJob(String tag) { this.tag = tag; }

    @NonNull
    @Override
    protected Result onRunJob(@NonNull Params params) {
        return Result.SUCCESS;
    }
}
