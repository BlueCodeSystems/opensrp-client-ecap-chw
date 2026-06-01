package com.bluecodeltd.ecap.chw.util;

import android.content.Context;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.os.Environment;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;

import androidx.documentfile.provider.DocumentFile;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import timber.log.Timber;

public class PublicGoogleDriveFolderDownloader {

    private static final Pattern DRIVE_ITEM_ID_FROM_FOLDER_HTML =
            Pattern.compile("/(file|document|spreadsheets|presentation)/d/([a-zA-Z0-9_-]{10,})");
    private static final Pattern CONTENT_DISPOSITION_FILENAME = Pattern.compile("filename\\*?=([^;]+)");
    private static final Pattern DRIVE_CONFIRM_TOKEN = Pattern.compile("confirm=([0-9A-Za-z_]+)");

    private PublicGoogleDriveFolderDownloader() {
    }

    public interface Callback {
        void onProgress(int downloaded, int total);

        void onSuccess(File targetDir, int downloaded);

        void onError(Throwable t);
    }

    public static void downloadPublicFolderToDownloadsDir(Context context, String folderId, String subfolderName, Callback callback) {
        Threading.ioBestEffort(() -> {
            try {
                File targetDir = resolveTargetDir(context, subfolderName);
                if (targetDir == null) throw new IllegalStateException("Target directory unavailable");
                if (!targetDir.exists() && !targetDir.mkdirs()) {
                    throw new IllegalStateException("Failed to create folder: " + targetDir.getAbsolutePath());
                }

                String folderUrl = "https://drive.google.com/drive/folders/" + folderId;
                String html = fetchToString(folderUrl);
                Set<DriveItem> items = extractDriveItems(html);
                if (items.isEmpty()) {
                    throw new IllegalStateException("No downloadable files found (folder empty or access required)");
                }

                int total = items.size();
                int done = 0;
                for (DriveItem item : items) {
                    downloadSingleItem(item, targetDir);
                    done++;
                    int finalDone = done;
                    if (callback != null) Threading.main(() -> callback.onProgress(finalDone, total));
                }

                if (callback != null) {
                    int finalDone = done;
                    Threading.main(() -> callback.onSuccess(targetDir, finalDone));
                }
            } catch (Throwable t) {
                Timber.e(t);
                if (callback != null) Threading.main(() -> callback.onError(t));
            }
        });
    }

    /**
     * Downloads files from a public Google Drive folder into a SAF treeUri (Android 10+).
     * The caller should pass a persisted treeUri (ACTION_OPEN_DOCUMENT_TREE).
     */
    public static void downloadPublicFolderToTreeUri(Context context, String folderId, Uri treeUri, String subfolderName, Callback callback) {
        Threading.ioBestEffort(() -> {
            try {
                if (context == null) throw new IllegalArgumentException("context == null");
                if (treeUri == null) throw new IllegalArgumentException("treeUri == null");

                ContentResolver resolver = context.getContentResolver();
                DocumentFile root = DocumentFile.fromTreeUri(context, treeUri);
                if (root == null || !root.canWrite()) {
                    throw new IllegalStateException("Selected folder is not writable");
                }

                DocumentFile target = root;
                if (!TextUtils.isEmpty(subfolderName)) {
                    DocumentFile existing = findChildDirectory(root, subfolderName);
                    target = existing != null ? existing : root.createDirectory(subfolderName);
                }
                if (target == null || !target.canWrite()) {
                    throw new IllegalStateException("Failed to create/access target folder: " + subfolderName);
                }

                String folderUrl = "https://drive.google.com/drive/folders/" + folderId;
                String html = fetchToString(folderUrl);
                Set<DriveItem> items = extractDriveItems(html);
                if (items.isEmpty()) {
                    throw new IllegalStateException("No downloadable files found (folder empty or access required)");
                }

                int total = items.size();
                int done = 0;
                for (DriveItem item : items) {
                    downloadSingleItemToSaf(resolver, context, item, target);
                    done++;
                    int finalDone = done;
                    if (callback != null) Threading.main(() -> callback.onProgress(finalDone, total));
                }

                if (callback != null) {
                    int finalDone = done;
                    final String targetUriString = target.getUri() != null ? target.getUri().toString() : "";
                    Threading.main(() -> callback.onSuccess(new File(targetUriString), finalDone));
                }
            } catch (Throwable t) {
                Timber.e(t);
                if (callback != null) Threading.main(() -> callback.onError(t));
            }
        });
    }

    /**
     * Downloads files from a public Google Drive folder into the shared public Downloads directory using MediaStore.
     * On Android 10+ this supports creating subfolders via RELATIVE_PATH (e.g. Downloads/ECAP II SOPs).
     */
    public static void downloadPublicFolderToPublicDownloads(Context context, String folderId, String subfolderName, Callback callback) {
        Threading.ioBestEffort(() -> {
            try {
                if (context == null) throw new IllegalArgumentException("context == null");
                ContentResolver resolver = context.getContentResolver();

                String folderUrl = "https://drive.google.com/drive/folders/" + folderId;
                String html = fetchToString(folderUrl);
                Set<DriveItem> items = extractDriveItems(html);
                if (items.isEmpty()) {
                    throw new IllegalStateException("No downloadable files found (folder empty or access required)");
                }

                String relativePath = Environment.DIRECTORY_DOWNLOADS + "/";
                if (!TextUtils.isEmpty(subfolderName)) relativePath = relativePath + subfolderName + "/";

                int total = items.size();
                int done = 0;
                for (DriveItem item : items) {
                    downloadSingleItemToMediaStoreDownloads(resolver, item, relativePath);
                    done++;
                    int finalDone = done;
                    if (callback != null) Threading.main(() -> callback.onProgress(finalDone, total));
                }

                if (callback != null) {
                    int finalDone = done;
                    Threading.main(() -> callback.onSuccess(new File("Downloads/" + (TextUtils.isEmpty(subfolderName) ? "" : subfolderName)), finalDone));
                }
            } catch (Throwable t) {
                Timber.e(t);
                if (callback != null) Threading.main(() -> callback.onError(t));
            }
        });
    }

    private static File resolveTargetDir(Context context, String subfolderName) {
        File downloadsDir = null;
        try {
            downloadsDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        } catch (Exception ignored) {
        }
        if (downloadsDir == null) return null;
        return TextUtils.isEmpty(subfolderName) ? downloadsDir : new File(downloadsDir, subfolderName);
    }

    private static Set<String> extractFileIds(String html) {
        Set<String> ids = new LinkedHashSet<>();
        for (DriveItem item : extractDriveItems(html)) {
            if (item != null && !TextUtils.isEmpty(item.id)) ids.add(item.id);
        }
        return ids;
    }

    private static final class DriveItem {
        final String kind;
        final String id;

        DriveItem(String kind, String id) {
            this.kind = kind;
            this.id = id;
        }
    }

    private static Set<DriveItem> extractDriveItems(String html) {
        Set<DriveItem> items = new LinkedHashSet<>();
        if (TextUtils.isEmpty(html)) return items;
        Matcher matcher = DRIVE_ITEM_ID_FROM_FOLDER_HTML.matcher(html);
        while (matcher.find()) {
            String kind = matcher.group(1);
            String id = matcher.group(2);
            if (!TextUtils.isEmpty(id)) items.add(new DriveItem(kind, id));
        }
        return items;
    }

    private static void downloadSingleItem(DriveItem item, File targetDir) throws Exception {
        if (item == null || TextUtils.isEmpty(item.id)) return;
        if ("file".equalsIgnoreCase(item.kind)) {
            downloadSingleFile(item.id, targetDir);
            return;
        }
        // Google Docs/Sheets/Slides: export to PDF as a reasonable default for SOP distribution.
        downloadGoogleDocAsPdf(item, targetDir);
    }

    private static void downloadSingleFile(String fileId, File targetDir) throws Exception {
        // "uc" endpoint returns a redirect + content-disposition with the filename for many public files.
        HttpURLConnection connection = openDriveDownloadConnection(fileId, null);
        String disposition = connection.getHeaderField("Content-Disposition");
        String filename = sanitizeFilename(extractFilenameFromContentDisposition(disposition));
        if (TextUtils.isEmpty(filename)) filename = fileId;

        // Some large files return an interstitial HTML that includes a confirm token.
        String contentType = connection.getContentType();
        if (contentType != null && contentType.toLowerCase().contains("text/html")) {
            String html = readStreamToString(connection.getInputStream());
            String confirm = extractConfirmToken(html);
            safeDisconnect(connection);
            if (!TextUtils.isEmpty(confirm)) {
                connection = openDriveDownloadConnection(fileId, confirm);
                disposition = connection.getHeaderField("Content-Disposition");
                filename = sanitizeFilename(extractFilenameFromContentDisposition(disposition));
                if (TextUtils.isEmpty(filename)) filename = fileId;
            } else {
                throw new IllegalStateException("Drive download requires confirmation; token not found");
            }
        }

        File out = new File(targetDir, filename);
        try (InputStream is = new BufferedInputStream(connection.getInputStream());
             FileOutputStream fos = new FileOutputStream(out)) {
            copyStream(is, fos);
        } finally {
            safeDisconnect(connection);
        }
    }

    private static void downloadSingleItemToSaf(ContentResolver resolver, Context context, DriveItem item, DocumentFile targetDir) throws Exception {
        if (item == null || TextUtils.isEmpty(item.id)) return;
        if ("file".equalsIgnoreCase(item.kind)) {
            downloadSingleFileToSaf(resolver, item.id, targetDir);
            return;
        }
        downloadGoogleDocAsPdfToSaf(resolver, item, targetDir);
    }

    private static void downloadSingleItemToMediaStoreDownloads(ContentResolver resolver, DriveItem item, String relativePath) throws Exception {
        if (item == null || TextUtils.isEmpty(item.id)) return;

        String filename;
        String mimeType;
        InputStream in = null;
        HttpURLConnection connection = null;
        try {
            if ("file".equalsIgnoreCase(item.kind)) {
                connection = openDriveDownloadConnection(item.id, null);
                String disposition = connection.getHeaderField("Content-Disposition");
                filename = sanitizeFilename(extractFilenameFromContentDisposition(disposition));
                if (TextUtils.isEmpty(filename)) filename = item.id;

                String contentType = connection.getContentType();
                if (contentType != null && contentType.toLowerCase().contains("text/html")) {
                    String html = readStreamToString(connection.getInputStream());
                    String confirm = extractConfirmToken(html);
                    safeDisconnect(connection);
                    if (!TextUtils.isEmpty(confirm)) {
                        connection = openDriveDownloadConnection(item.id, confirm);
                        disposition = connection.getHeaderField("Content-Disposition");
                        filename = sanitizeFilename(extractFilenameFromContentDisposition(disposition));
                        if (TextUtils.isEmpty(filename)) filename = item.id;
                        contentType = connection.getContentType();
                    } else {
                        throw new IllegalStateException("Drive download requires confirmation; token not found");
                    }
                }

                mimeType = guessMimeTypeFromFilename(filename);
                if (TextUtils.isEmpty(mimeType)) mimeType = contentType != null ? contentType : "application/octet-stream";
                in = new BufferedInputStream(connection.getInputStream());
            } else {
                String exportUrl = googleExportPdfUrl(item);
                if (TextUtils.isEmpty(exportUrl)) return;
                connection = (HttpURLConnection) new URL(exportUrl).openConnection();
                connection.setInstanceFollowRedirects(true);
                connection.setConnectTimeout(30000);
                connection.setReadTimeout(60000);
                connection.setRequestProperty("User-Agent", "Mozilla/5.0");
                int code = connection.getResponseCode();
                if (code >= 400) throw new IllegalStateException("HTTP " + code + " for " + exportUrl);

                filename = item.id + ".pdf";
                mimeType = "application/pdf";
                in = new BufferedInputStream(connection.getInputStream());
            }

            // Best-effort: delete any existing item with same name in same relative path to avoid duplicates.
            deleteExistingDownload(resolver, filename, relativePath);

            ContentValues values = new ContentValues();
            values.put(MediaStore.Downloads.DISPLAY_NAME, filename);
            values.put(MediaStore.Downloads.MIME_TYPE, mimeType);
            values.put(MediaStore.Downloads.RELATIVE_PATH, relativePath);
            values.put(MediaStore.Downloads.IS_PENDING, 1);

            Uri uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
            if (uri == null) throw new IllegalStateException("Failed to create download entry for " + filename);

            try (OutputStream os = resolver.openOutputStream(uri, "w")) {
                if (os == null) throw new IllegalStateException("Failed to open output stream for " + filename);
                copyStream(in, os);
            } finally {
                ContentValues done = new ContentValues();
                done.put(MediaStore.Downloads.IS_PENDING, 0);
                try {
                    resolver.update(uri, done, null, null);
                } catch (Exception ignored) {
                }
            }
        } finally {
            try {
                if (in != null) in.close();
            } catch (Exception ignored) {
            }
            safeDisconnect(connection);
        }
    }

    private static void deleteExistingDownload(ContentResolver resolver, String displayName, String relativePath) {
        if (resolver == null || TextUtils.isEmpty(displayName) || TextUtils.isEmpty(relativePath)) return;
        android.database.Cursor c = null;
        try {
            String[] proj = new String[]{MediaStore.Downloads._ID, MediaStore.Downloads.DISPLAY_NAME, MediaStore.Downloads.RELATIVE_PATH};
            String sel = MediaStore.Downloads.DISPLAY_NAME + "=? AND " + MediaStore.Downloads.RELATIVE_PATH + "=?";
            String[] args = new String[]{displayName, relativePath};
            c = resolver.query(MediaStore.Downloads.EXTERNAL_CONTENT_URI, proj, sel, args, null);
            if (c != null) {
                while (c.moveToNext()) {
                    long id = c.getLong(0);
                    Uri uri = Uri.withAppendedPath(MediaStore.Downloads.EXTERNAL_CONTENT_URI, String.valueOf(id));
                    try {
                        resolver.delete(uri, null, null);
                    } catch (Exception ignored) {
                    }
                }
            }
        } catch (Exception ignored) {
        } finally {
            try {
                if (c != null) c.close();
            } catch (Exception ignored) {
            }
        }
    }

    private static void downloadSingleFileToSaf(ContentResolver resolver, String fileId, DocumentFile targetDir) throws Exception {
        HttpURLConnection connection = openDriveDownloadConnection(fileId, null);
        String disposition = connection.getHeaderField("Content-Disposition");
        String filename = sanitizeFilename(extractFilenameFromContentDisposition(disposition));
        if (TextUtils.isEmpty(filename)) filename = fileId;

        String contentType = connection.getContentType();
        if (contentType != null && contentType.toLowerCase().contains("text/html")) {
            String html = readStreamToString(connection.getInputStream());
            String confirm = extractConfirmToken(html);
            safeDisconnect(connection);
            if (!TextUtils.isEmpty(confirm)) {
                connection = openDriveDownloadConnection(fileId, confirm);
                disposition = connection.getHeaderField("Content-Disposition");
                filename = sanitizeFilename(extractFilenameFromContentDisposition(disposition));
                if (TextUtils.isEmpty(filename)) filename = fileId;
            } else {
                throw new IllegalStateException("Drive download requires confirmation; token not found");
            }
        }

        // Overwrite by deleting existing file with same name.
        DocumentFile existing = findChildFile(targetDir, filename);
        if (existing != null) {
            try {
                existing.delete();
            } catch (Exception ignored) {
            }
        }

        String mimeType = guessMimeTypeFromFilename(filename);
        if (TextUtils.isEmpty(mimeType)) mimeType = "application/octet-stream";
        DocumentFile outDoc = targetDir.createFile(mimeType, filename);
        if (outDoc == null) throw new IllegalStateException("Failed to create output file: " + filename);

        Uri outUri = outDoc.getUri();
        try (InputStream is = new BufferedInputStream(connection.getInputStream());
             OutputStream os = resolver.openOutputStream(outUri, "w")) {
            if (os == null) throw new IllegalStateException("Failed to open output stream for: " + outUri);
            copyStream(is, os);
        } finally {
            safeDisconnect(connection);
        }
    }

    private static void downloadGoogleDocAsPdf(DriveItem item, File targetDir) throws Exception {
        String exportUrl = googleExportPdfUrl(item);
        if (TextUtils.isEmpty(exportUrl)) return;

        HttpURLConnection connection = (HttpURLConnection) new URL(exportUrl).openConnection();
        connection.setInstanceFollowRedirects(true);
        connection.setConnectTimeout(30000);
        connection.setReadTimeout(60000);
        connection.setRequestProperty("User-Agent", "Mozilla/5.0");
        int code = connection.getResponseCode();
        if (code >= 400) throw new IllegalStateException("HTTP " + code + " for " + exportUrl);

        String filename = item.id + ".pdf";
        File out = new File(targetDir, filename);
        try (InputStream is = new BufferedInputStream(connection.getInputStream());
             FileOutputStream fos = new FileOutputStream(out)) {
            copyStream(is, fos);
        } finally {
            safeDisconnect(connection);
        }
    }

    private static void downloadGoogleDocAsPdfToSaf(ContentResolver resolver, DriveItem item, DocumentFile targetDir) throws Exception {
        String exportUrl = googleExportPdfUrl(item);
        if (TextUtils.isEmpty(exportUrl)) return;

        HttpURLConnection connection = (HttpURLConnection) new URL(exportUrl).openConnection();
        connection.setInstanceFollowRedirects(true);
        connection.setConnectTimeout(30000);
        connection.setReadTimeout(60000);
        connection.setRequestProperty("User-Agent", "Mozilla/5.0");
        int code = connection.getResponseCode();
        if (code >= 400) throw new IllegalStateException("HTTP " + code + " for " + exportUrl);

        String filename = item.id + ".pdf";
        DocumentFile existing = findChildFile(targetDir, filename);
        if (existing != null) {
            try {
                existing.delete();
            } catch (Exception ignored) {
            }
        }
        DocumentFile outDoc = targetDir.createFile("application/pdf", filename);
        if (outDoc == null) throw new IllegalStateException("Failed to create output file: " + filename);

        try (InputStream is = new BufferedInputStream(connection.getInputStream());
             OutputStream os = resolver.openOutputStream(outDoc.getUri(), "w")) {
            if (os == null) throw new IllegalStateException("Failed to open output stream");
            copyStream(is, os);
        } finally {
            safeDisconnect(connection);
        }
    }

    private static String googleExportPdfUrl(DriveItem item) {
        if (item == null || TextUtils.isEmpty(item.id) || TextUtils.isEmpty(item.kind)) return null;
        String id = item.id;
        String kind = item.kind.toLowerCase();
        switch (kind) {
            case "document":
                return "https://docs.google.com/document/d/" + id + "/export?format=pdf";
            case "spreadsheets":
                return "https://docs.google.com/spreadsheets/d/" + id + "/export?format=pdf";
            case "presentation":
                return "https://docs.google.com/presentation/d/" + id + "/export/pdf";
            default:
                return null;
        }
    }

    private static String extractFilenameFromContentDisposition(String contentDisposition) {
        if (TextUtils.isEmpty(contentDisposition)) return null;
        Matcher m = CONTENT_DISPOSITION_FILENAME.matcher(contentDisposition);
        if (!m.find()) return null;
        String value = m.group(1);
        if (TextUtils.isEmpty(value)) return null;
        value = value.trim();
        // Handle RFC 5987 style: filename*=UTF-8''name.ext
        int utf8Prefix = value.toLowerCase().indexOf("utf-8''");
        if (utf8Prefix >= 0) value = value.substring(utf8Prefix + "utf-8''".length());
        value = value.replace("\"", "").trim();
        try {
            return URLDecoder.decode(value, "UTF-8");
        } catch (Exception ignored) {
            return value;
        }
    }

    private static String fetchToString(String url) throws Exception {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setInstanceFollowRedirects(true);
        connection.setConnectTimeout(30000);
        connection.setReadTimeout(60000);
        connection.setRequestProperty("User-Agent", "Mozilla/5.0");
        int code = connection.getResponseCode();
        if (code >= 400) throw new IllegalStateException("HTTP " + code + " for " + url);
        try (InputStream is = new BufferedInputStream(connection.getInputStream());
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[8192];
            int read;
            while ((read = is.read(buffer)) != -1) {
                baos.write(buffer, 0, read);
            }
            return baos.toString(StandardCharsets.UTF_8.name());
        } finally {
            try {
                connection.disconnect();
            } catch (Exception ignored) {
            }
        }
    }

    private static HttpURLConnection openDriveDownloadConnection(String fileId, String confirmToken) throws Exception {
        String downloadUrl = "https://drive.google.com/uc?export=download&id=" + fileId;
        if (!TextUtils.isEmpty(confirmToken)) {
            downloadUrl = downloadUrl + "&confirm=" + confirmToken;
        }
        HttpURLConnection connection = (HttpURLConnection) new URL(downloadUrl).openConnection();
        connection.setInstanceFollowRedirects(true);
        connection.setConnectTimeout(30000);
        connection.setReadTimeout(60000);
        connection.setRequestProperty("User-Agent", "Mozilla/5.0");

        int code = connection.getResponseCode();
        if (code >= 400) {
            safeDisconnect(connection);
            throw new IllegalStateException("HTTP " + code + " for " + downloadUrl);
        }
        return connection;
    }

    private static String extractConfirmToken(String html) {
        if (TextUtils.isEmpty(html)) return null;
        Matcher matcher = DRIVE_CONFIRM_TOKEN.matcher(html);
        if (matcher.find()) return matcher.group(1);
        return null;
    }

    private static String readStreamToString(InputStream is) throws Exception {
        try (BufferedInputStream bis = new BufferedInputStream(is);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[8192];
            int read;
            while ((read = bis.read(buffer)) != -1) {
                baos.write(buffer, 0, read);
            }
            return baos.toString(StandardCharsets.UTF_8.name());
        }
    }

    private static void copyStream(InputStream is, OutputStream os) throws Exception {
        byte[] buffer = new byte[8192];
        int read;
        while ((read = is.read(buffer)) != -1) {
            os.write(buffer, 0, read);
        }
        os.flush();
    }

    private static void safeDisconnect(HttpURLConnection connection) {
        if (connection == null) return;
        try {
            connection.disconnect();
        } catch (Exception ignored) {
        }
    }

    private static String sanitizeFilename(String filename) {
        if (TextUtils.isEmpty(filename)) return filename;
        // Android file systems are generally fine with these, but SAF/providers may reject path separators.
        String sanitized = filename.replace("\\", "_").replace("/", "_").trim();
        if (sanitized.length() > 180) sanitized = sanitized.substring(0, 180);
        return sanitized;
    }

    private static DocumentFile findChildDirectory(DocumentFile parent, String name) {
        if (parent == null || TextUtils.isEmpty(name)) return null;
        DocumentFile[] files = parent.listFiles();
        if (files == null) return null;
        for (DocumentFile f : files) {
            if (f != null && f.isDirectory() && name.equalsIgnoreCase(String.valueOf(f.getName()))) return f;
        }
        return null;
    }

    private static DocumentFile findChildFile(DocumentFile parent, String name) {
        if (parent == null || TextUtils.isEmpty(name)) return null;
        DocumentFile[] files = parent.listFiles();
        if (files == null) return null;
        for (DocumentFile f : files) {
            if (f != null && f.isFile() && name.equalsIgnoreCase(String.valueOf(f.getName()))) return f;
        }
        return null;
    }

    private static String guessMimeTypeFromFilename(String filename) {
        if (TextUtils.isEmpty(filename)) return null;
        String lower = filename.toLowerCase();
        if (lower.endsWith(".pdf")) return "application/pdf";
        if (lower.endsWith(".doc")) return "application/msword";
        if (lower.endsWith(".docx")) return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        if (lower.endsWith(".xls")) return "application/vnd.ms-excel";
        if (lower.endsWith(".xlsx")) return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        if (lower.endsWith(".ppt")) return "application/vnd.ms-powerpoint";
        if (lower.endsWith(".pptx")) return "application/vnd.openxmlformats-officedocument.presentationml.presentation";
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return "image/jpeg";
        if (lower.endsWith(".png")) return "image/png";
        if (lower.endsWith(".txt")) return "text/plain";
        return null;
    }
}
