package com.bluecodeltd.ecap.chw.model;

import android.net.Uri;

public class SopDocumentModel {
    private final Uri uri;
    private final String displayName;
    private final long sizeBytes;
    private final long dateModifiedSeconds;

    public SopDocumentModel(Uri uri, String displayName, long sizeBytes, long dateModifiedSeconds) {
        this.uri = uri;
        this.displayName = displayName;
        this.sizeBytes = sizeBytes;
        this.dateModifiedSeconds = dateModifiedSeconds;
    }

    public Uri getUri() {
        return uri;
    }

    public String getDisplayName() {
        return displayName;
    }

    public long getSizeBytes() {
        return sizeBytes;
    }

    public long getDateModifiedSeconds() {
        return dateModifiedSeconds;
    }
}

