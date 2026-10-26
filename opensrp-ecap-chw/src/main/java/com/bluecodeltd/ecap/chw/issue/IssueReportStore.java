package com.bluecodeltd.ecap.chw.issue;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;

import androidx.annotation.WorkerThread;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Photos attached to issue reports, kept in app storage until {@link IssuePhotoUploadWorker}
 * has uploaded them. Each waiting photo is {@code files/issue_reports/<reportId>.jpg}; the report
 * document itself is queued by Firestore's own offline cache.
 */
public final class IssueReportStore {
    private static final int MAX_PHOTO_EDGE = 1600;

    private IssueReportStore() {
    }

    static File root(Context context) {
        return new File(context.getFilesDir(), "issue_reports");
    }

    /** The Storage path a report's photo is uploaded to; also stored on the report document. */
    public static String storagePath(String reportId) {
        return "issue_reports/" + reportId + "/photo.jpg";
    }

    /** Moves a prepared photo into the upload queue for {@code reportId}. */
    public static void queuePhoto(Context context, String reportId, File photo) throws IOException {
        File dir = root(context);
        if (!dir.isDirectory() && !dir.mkdirs()) {
            throw new IOException("Could not create " + dir);
        }
        File target = new File(dir, reportId + ".jpg");
        if (!photo.renameTo(target)) {
            throw new IOException("Could not move photo to " + target);
        }
    }

    /** Photos still waiting to be uploaded, oldest first. */
    static List<File> pendingPhotos(Context context) {
        File[] files = root(context).listFiles(f -> f.getName().endsWith(".jpg"));
        if (files == null) {
            return new ArrayList<>();
        }
        Arrays.sort(files, (a, b) -> Long.compare(a.lastModified(), b.lastModified()));
        return new ArrayList<>(Arrays.asList(files));
    }

    static String reportId(File photo) {
        return photo.getName().substring(0, photo.getName().length() - ".jpg".length());
    }

    /**
     * Decodes an image, scales it so its longest edge is at most 1600px, applies the EXIF
     * rotation, and writes it to {@code out} as a JPEG. Keeps uploads small on field connections.
     */
    @WorkerThread
    public static void preparePhoto(ContentResolver resolver, Uri source, File out) throws IOException {
        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        try (InputStream in = resolver.openInputStream(source)) {
            BitmapFactory.decodeStream(in, null, bounds);
        }
        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) {
            throw new IOException("Not an image: " + source);
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 1;
        while (Math.max(bounds.outWidth, bounds.outHeight) / (options.inSampleSize * 2) >= MAX_PHOTO_EDGE) {
            options.inSampleSize *= 2;
        }
        Bitmap bitmap;
        try (InputStream in = resolver.openInputStream(source)) {
            bitmap = BitmapFactory.decodeStream(in, null, options);
        }
        if (bitmap == null) {
            throw new IOException("Could not decode " + source);
        }

        Matrix matrix = new Matrix();
        float scale = Math.min(1f, (float) MAX_PHOTO_EDGE / Math.max(bitmap.getWidth(), bitmap.getHeight()));
        matrix.postScale(scale, scale);
        matrix.postRotate(rotation(resolver, source));
        Bitmap prepared = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        try (FileOutputStream os = new FileOutputStream(out)) {
            prepared.compress(Bitmap.CompressFormat.JPEG, 80, os);
        } finally {
            if (prepared != bitmap) {
                prepared.recycle();
            }
            bitmap.recycle();
        }
    }

    private static int rotation(ContentResolver resolver, Uri source) {
        try (InputStream in = resolver.openInputStream(source)) {
            if (in == null) {
                return 0;
            }
            switch (new ExifInterface(in).getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
                case ExifInterface.ORIENTATION_ROTATE_90: return 90;
                case ExifInterface.ORIENTATION_ROTATE_180: return 180;
                case ExifInterface.ORIENTATION_ROTATE_270: return 270;
                default: return 0;
            }
        } catch (IOException e) {
            return 0;
        }
    }
}
