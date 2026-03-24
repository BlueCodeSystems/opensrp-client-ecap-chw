package com.bluecodeltd.ecap.chw.model;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.annotation.RequiresApi;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.application.ChwApplication;

import net.sqlcipher.database.SQLiteDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.core.application.CoreChwApplication;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import timber.log.Timber;

public class GenerateCSVsModel {

    public interface CSVCallback {
        void onSuccess(String filePath);

        void onError(String error);
    }

    public void createAllEcClientFieldTablesCSVFiles(CSVCallback callback) {
        try {
            CoreChwApplication app = CoreChwApplication.getInstance();
            if (app == null || ChwApplication.getInstance() == null || ChwApplication.getInstance().getRepository() == null) {
                callback.onError("Application repository unavailable.");
                return;
            }

            SQLiteDatabase database = ChwApplication.getInstance().getRepository().getReadableDatabase();
            if (database == null) {
                callback.onError("Database unavailable.");
                return;
            }

            List<String> tableNames = loadTableNamesFromAssets();
            if (tableNames.isEmpty()) {
                callback.onError("No tables found in ec_client_fields.json.");
                return;
            }

            int generatedCount = 0;
            int skippedCount = 0;

            for (String tableName : tableNames) {
                List<String> existingColumns = getExistingTableColumns(database, tableName);
                if (existingColumns.isEmpty()) {
                    skippedCount++;
                    continue;
                }

                File file = resolveCsvFile(tableName + ".csv");
                String query = "SELECT * FROM " + quoteIdentifier(tableName);

                try (FileWriter fileWriter = new FileWriter(file);
                     Cursor cursor = database.rawQuery(query, null)) {

                    fileWriter.append(String.join(",", existingColumns)).append("\n");

                    int[] columnIndexes = new int[existingColumns.size()];
                    for (int i = 0; i < existingColumns.size(); i++) {
                        columnIndexes[i] = cursor.getColumnIndex(existingColumns.get(i));
                    }

                    while (cursor.moveToNext()) {
                        for (int i = 0; i < existingColumns.size(); i++) {
                            String value = "";
                            int idx = columnIndexes[i];
                            if (idx >= 0 && !cursor.isNull(idx)) {
                                value = cursor.getString(idx);
                            }
                            fileWriter.append(escapeCsvValue(value));
                            if (i < existingColumns.size() - 1) {
                                fileWriter.append(",");
                            }
                        }
                        fileWriter.append("\n");
                    }
                    fileWriter.flush();
                }

                publishCsv(file);
                generatedCount++;
            }

            if (generatedCount == 0) {
                callback.onError("No CSV files generated from ec_client_fields.json.");
                return;
            }

            callback.onSuccess("Generated " + generatedCount + " CSV file(s). Skipped " + skippedCount + " table(s).");
        } catch (Exception e) {
            Timber.e(e, "Failed generating CSV files from ec_client_fields.json");
            callback.onError(e.getMessage() != null ? e.getMessage() : "Failed to generate CSV files.");
        }
    }

    private List<String> loadTableNamesFromAssets() throws IOException, JSONException {
        CoreChwApplication application = CoreChwApplication.getInstance();
        if (application == null) {
            return new ArrayList<>();
        }

        Set<String> orderedNames = new LinkedHashSet<>();
        try (InputStream inputStream = application.getApplicationContext().getAssets().open("ec_client_fields.json")) {
            String json = readAllText(inputStream);
            JSONObject root = new JSONObject(json);
            JSONArray bindObjects = root.optJSONArray("bindobjects");
            if (bindObjects == null) {
                return new ArrayList<>();
            }

            for (int i = 0; i < bindObjects.length(); i++) {
                JSONObject tableObject = bindObjects.optJSONObject(i);
                if (tableObject == null) {
                    continue;
                }
                String tableName = tableObject.optString("name", "").trim();
                if (!tableName.isEmpty()) {
                    orderedNames.add(tableName);
                }
            }
        }

        return new ArrayList<>(orderedNames);
    }

    private List<String> getExistingTableColumns(SQLiteDatabase database, String tableName) {
        List<String> columns = new ArrayList<>();
        String pragmaQuery = "PRAGMA table_info(" + quoteIdentifier(tableName) + ")";
        try (Cursor cursor = database.rawQuery(pragmaQuery, null)) {
            int nameIndex = cursor.getColumnIndex("name");
            while (cursor.moveToNext()) {
                if (nameIndex < 0) {
                    continue;
                }
                String columnName = cursor.getString(nameIndex);
                if (columnName != null && !columnName.trim().isEmpty()) {
                    columns.add(columnName.trim());
                }
            }
        } catch (Exception e) {
            Timber.w(e, "Skipping table export for %s", tableName);
        }
        return columns;
    }

    private String quoteIdentifier(String identifier) {
        return "\"" + identifier.replace("\"", "\"\"") + "\"";
    }

    private String readAllText(InputStream inputStream) throws IOException {
        StringBuilder builder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
        }
        return builder.toString();
    }

    private String escapeCsvValue(String value) {
        if (value == null) {
            return "";
        }
        String escapedValue = value.replace("\"", "\"\"");
        if (escapedValue.contains(",") || escapedValue.contains("\n") || escapedValue.contains("\"")) {
            escapedValue = "\"" + escapedValue + "\"";
        }
        return escapedValue;
    }

    private File resolveCsvFile(String fileName) throws IOException {
        File directory = getCsvDirectory();
        if (directory == null) {
            throw new IOException("External files directory unavailable");
        }
        if (!directory.exists() && !directory.mkdirs()) {
            throw new IOException("Unable to create directory " + directory.getAbsolutePath());
        }
        return new File(directory, fileName);
    }

    private File getCsvDirectory() {
        CoreChwApplication application = CoreChwApplication.getInstance();
        if (application == null) {
            return null;
        }
        File directory = application.getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        if (directory == null) {
            directory = application.getApplicationContext().getExternalFilesDir(null);
        }
        if (directory == null) {
            directory = application.getApplicationContext().getFilesDir();
        }
        return directory;
    }

    private String publishCsv(File file) {
        if (file == null) {
            return "CSV generated";
        }

        CoreChwApplication application = CoreChwApplication.getInstance();
        if (application == null) {
            return file.getAbsolutePath();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            try {
                return copyToDownloads(application, file);
            } catch (IOException e) {
                Timber.e(e, "Failed to copy CSV to downloads");
                return file.getAbsolutePath();
            }
        }

        MediaScannerConnection.scanFile(application.getApplicationContext(),
                new String[]{file.getAbsolutePath()},
                new String[]{"text/csv"},
                null);
        return file.getAbsolutePath();
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private String copyToDownloads(CoreChwApplication application, File file) throws IOException {
        ContentResolver resolver = application.getContentResolver();
        String relativePath = Environment.DIRECTORY_DOWNLOADS + "/" + application.getString(R.string.app_name);

        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, file.getName());
        values.put(MediaStore.MediaColumns.MIME_TYPE, "text/csv");
        values.put(MediaStore.MediaColumns.RELATIVE_PATH, relativePath);
        values.put(MediaStore.MediaColumns.IS_PENDING, 1);

        Uri uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
        if (uri == null) {
            throw new IOException("Failed to create MediaStore entry for " + file.getName());
        }

        try (OutputStream outputStream = resolver.openOutputStream(uri);
             InputStream inputStream = new FileInputStream(file)) {
            if (outputStream == null) {
                throw new IOException("Unable to open output stream for " + uri);
            }
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();
        } catch (IOException ioe) {
            resolver.delete(uri, null, null);
            throw ioe;
        }

        ContentValues completed = new ContentValues();
        completed.put(MediaStore.MediaColumns.IS_PENDING, 0);
        resolver.update(uri, completed, null, null);

        return relativePath + "/" + file.getName();
    }
}
