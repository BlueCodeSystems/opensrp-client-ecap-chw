package com.bluecodeltd.ecap.chw.issue;

import androidx.core.content.FileProvider;

/**
 * Hands the camera app a private cache file to write an issue photo into, so photos never land
 * in the phone's shared gallery. A separate provider from the libraries' shared
 * {@code .fileprovider}, whose paths point at external storage.
 */
public class IssuePhotoProvider extends FileProvider {
    public static final String AUTHORITY_SUFFIX = ".issuephotos";
}
