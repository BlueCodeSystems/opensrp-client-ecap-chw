package com.bluecodeltd.ecap.chw.util;

import android.content.Context;

import com.bluecodeltd.ecap.chw.BuildConfig;
import com.bluecodeltd.ecap.chw.contract.GuideBooksFragmentContract;

import java.io.File;

import timber.log.Timber;

public class DownloadGuideBooksUtils extends DownloadUtil {

    public DownloadGuideBooksUtils(GuideBooksFragmentContract.DownloadListener downloadListener, String fileName, String directory, Context context) {
        this.fileName = fileName;
        File baseDirectory = context.getExternalFilesDir(directory);
        if (baseDirectory == null) {
            throw new IllegalStateException("Unable to resolve external files directory for guidebooks");
        }
        File localeDirectory = new File(baseDirectory, context.getResources().getConfiguration().locale.getLanguage());
        if (!localeDirectory.exists() && !localeDirectory.mkdirs()) {
            Timber.v("Directory was not created successfully %s", localeDirectory.getAbsolutePath());
        }
        folder = localeDirectory.getAbsolutePath() + File.separator;
        this.downloadListener = downloadListener;
        this.serverUrl = getDownloadUrl(fileName, context);
    }

    public static String getDownloadUrl(String fileName, Context context) {
        return BuildConfig.guidebooks_url + context.getResources().getConfiguration().locale + "/" + fileName;
    }

    public void cancelDownload() {
        this.cancel(true);
        // delete the file in the device
        try {
            File file = new File(folder + fileName);
            if (file.exists())
                file.delete();
        } catch (Exception e) {
            Timber.v(e);
        }
    }
}
