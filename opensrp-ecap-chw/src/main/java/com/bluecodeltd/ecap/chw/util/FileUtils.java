package com.bluecodeltd.ecap.chw.util;

import android.content.Context;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import timber.log.Timber;

public class FileUtils {

    public static boolean hasExternalDisk() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    public static boolean canWriteToExternalDisk() {
        if (!hasExternalDisk())
            return false;

        return !Environment.MEDIA_MOUNTED_READ_ONLY.equals(Environment.getExternalStorageState());
    }

    public static File createDirectory(String directoryPath, boolean onSdCard) {
        Context ctx = com.bluecodeltd.ecap.chw.application.ChwApplication.getInstance().getApplicationContext();
        File base = onSdCard ? ctx.getExternalFilesDir(null) : ctx.getFilesDir();
        File dir = new File((base != null ? base.getAbsolutePath() : ctx.getFilesDir().getAbsolutePath()) + File.separator + directoryPath);
        if (dir.exists())
            return dir;

        if (!dir.mkdirs())
            Timber.v("Directory was not created successfully %s", dir.getAbsolutePath());

        return dir;
    }

    public static String getStringFromFile(String folder, String name) throws Exception {
        File fl = new File(folder, name);
        if (!fl.exists()) return null;

        FileInputStream fin = new FileInputStream(fl);
        String ret = convertStreamToString(fin);
        //Make sure you close all streams.
        fin.close();
        return ret;
    }

    public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

    public static boolean writeToExternalDisk(String directoryPath, byte[] bytes, String fileName) throws Exception {
        File dir = createDirectory(directoryPath, true);
        if (dir != null && dir.exists()) {
            File file = new File(dir, fileName);
            FileOutputStream os = new FileOutputStream(file);
            os.write(bytes);
            os.close();
        }
        return true;
    }

    public static File[] getFiles(String folder) {
        Context ctx = com.bluecodeltd.ecap.chw.application.ChwApplication.getInstance().getApplicationContext();
        File external = ctx.getExternalFilesDir(null);
        File directory = new File((external != null ? external : ctx.getFilesDir()) + File.separator + folder);
        File[] files = directory.listFiles();
        return files != null ? files : new File[]{};
    }

}
