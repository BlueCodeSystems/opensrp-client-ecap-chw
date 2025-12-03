package com.bluecodeltd.ecap.chw.util;

import android.os.Environment;

import org.smartregister.chw.core.application.CoreChwApplication;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import timber.log.Timber;

public class FileUtils {

    private FileUtils() {
        // utility
    }

    public static boolean hasExternalDisk() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    public static boolean canWriteToExternalDisk() {
        return hasExternalDisk() && !Environment.MEDIA_MOUNTED_READ_ONLY.equals(Environment.getExternalStorageState());
    }

    public static File createDirectory(String directoryPath, boolean onSdCard) {
        File dir = resolveDirectory(directoryPath);
        if (dir.exists()) {
            return dir;
        }

        if (!dir.mkdirs()) {
            Timber.v("Directory was not created successfully %s", dir.getAbsolutePath());
        }

        return dir;
    }

    public static String getStringFromFile(String folder, String name) throws Exception {
        File base = resolveDirectory(folder);
        File fl = new File(base, name);
        if (!fl.exists()) {
            return null;
        }

        FileInputStream fin = new FileInputStream(fl);
        String ret = convertStreamToString(fin);
        fin.close();
        return ret;
    }

    public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

    public static boolean writeToExternalDisk(String directoryPath, byte[] bytes, String fileName) throws Exception {
        File dir = createDirectory(directoryPath, canWriteToExternalDisk());
        if (dir != null && dir.exists()) {
            File file = new File(dir, fileName);
            FileOutputStream os = new FileOutputStream(file);
            os.write(bytes);
            os.close();
        }
        return true;
    }

    public static File[] getFiles(String folder) {
        File directory = resolveDirectory(folder);
        return directory.listFiles();
    }

    private static File resolveDirectory(String directoryPath) {
        File potential = new File(directoryPath);
        if (potential.isAbsolute()) {
            return potential;
        }

        CoreChwApplication application = CoreChwApplication.getInstance();
        if (application == null || application.getApplicationContext().getExternalFilesDir(null) == null) {
            return potential;
        }

        File base = application.getApplicationContext().getExternalFilesDir(null);
        return new File(base, directoryPath);
    }
}
