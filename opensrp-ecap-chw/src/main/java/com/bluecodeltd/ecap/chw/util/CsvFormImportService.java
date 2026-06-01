package com.bluecodeltd.ecap.chw.util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.text.TextUtils;

import com.bluecodeltd.ecap.chw.application.ChwApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.core.application.CoreChwApplication;
import org.smartregister.client.utils.constants.JsonFormConstants;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.domain.db.EventClient;
import org.smartregister.domain.tag.FormTag;
import org.smartregister.sync.helper.ECSyncHelper;

import net.sqlcipher.database.SQLiteDatabase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import timber.log.Timber;

public class CsvFormImportService {

    private CsvFormImportService() {
        // utility
    }

    private static final class ImportMeta {
        final String encounterType;
        final String entityTable;

        ImportMeta(String encounterType, String entityTable) {
            this.encounterType = encounterType;
            this.entityTable = entityTable;
        }
    }

    public static class ImportSummary {
        public int importedRows;
        public int skippedRows;
        public int failedRows;
        public int processedRows;
        public int totalRows;
        public String fileName;
        public boolean timedOutDuringProcessing;
        public boolean processingError;
        public final List<String> errors = new ArrayList<>();

        public int getProcessedPercentage() {
            if (totalRows <= 0) {
                return 100;
            }
            return Math.min(100, (processedRows * 100) / totalRows);
        }

        public int getImportedPercentageOfFile() {
            if (totalRows <= 0) {
                return importedRows > 0 ? 100 : 0;
            }
            return Math.min(100, (importedRows * 100) / totalRows);
        }

        public String toUserMessage() {
            StringBuilder builder = new StringBuilder();
            builder.append("CSV import finished");
            if (!TextUtils.isEmpty(fileName)) {
                builder.append(" (").append(fileName).append(")");
            }
            builder.append(". Processed: ").append(processedRows)
                    .append("/").append(totalRows)
                    .append(" (").append(getProcessedPercentage()).append("%)");
            builder.append(". Imported: ").append(importedRows)
                    .append(" (").append(getImportedPercentageOfFile()).append("% of file)")
                    .append(", Skipped: ").append(skippedRows)
                    .append(", Failed: ").append(failedRows).append(".");

            if (timedOutDuringProcessing) {
                builder.append("\nProcessing status: timed out while finalizing one or more chunks.");
            } else if (processingError) {
                builder.append("\nProcessing status: completed with processing errors.");
            }

            if (!errors.isEmpty()) {
                builder.append("\n\nTop issues:");
                int limit = Math.min(3, errors.size());
                for (int i = 0; i < limit; i++) {
                    builder.append("\n").append(i + 1).append(". ").append(errors.get(i));
                }
            }
            return builder.toString();
        }

        public boolean hasFileFailure() {
            return failedRows > 0 || timedOutDuringProcessing || processingError;
        }
    }

    public interface ImportProgressListener {
        void onProgress(int processedRows, int totalRows, int importedRows, int skippedRows, int failedRows);
    }

    private static final Map<String, ImportMeta> FILE_DEFAULTS = new HashMap<>();
    private static final Map<String, String> ENCOUNTER_TO_TABLE = new HashMap<>();
    private static final Map<String, String> TABLE_TO_DEFAULT_ENCOUNTER = new HashMap<>();
    private static final Map<String, Map<String, String>> TABLE_COLUMN_TO_FIELD_KEY = new HashMap<>();
    private static final Object TABLE_FIELD_MAP_LOCK = new Object();
    private static volatile boolean TABLE_FIELD_MAP_LOADED = false;

    private static final List<String> RESERVED_COLUMNS = Arrays.asList(
            "encounter_type",
            "entity_table",
            "table_name",
            "table",
            "base_entity_id",
            "entity_id",
            "id",
            "form_submission_id",
            "event_date",
            "eventdate",
            "encounter_location"
    );
    private static final int EVENT_PROCESSING_CHUNK_SIZE = 50;
    private static final int EVENT_PROCESSING_TIMEOUT_SECONDS = 300;

    static {
        // CSV files generated in GenerateCSVsModel
        addFileDefault("vcas.csv", "Sub Population", Constants.EcapClientTable.EC_CLIENT_INDEX);
        addFileDefault("households.csv", "Household Screening", Constants.EcapClientTable.EC_HOUSEHOLD);
        addFileDefault("hts.csv", "HIV Testing Service", Constants.EcapClientTable.EC_HIV_TESTING_SERVICE);
        addFileDefault("services_for_vcas.csv", "VCA Service Report", Constants.EcapClientTable.EC_VCA_SERVICE_REPORT);
        addFileDefault("services_for_households.csv", "Household Service Report", Constants.EcapClientTable.EC_HOUSEHOLD_SERVICE);
        addFileDefault("quarterly_assessment_for_vcas.csv", "Household Visitation Form 0-20 years", Constants.EcapClientTable.EC_HOUSEHOLD_VCA);
        addFileDefault("quarterly_assessment_for_households.csv", "Household Visitation For Caregiver", Constants.EcapClientTable.EC_HOUSEHOLD_CAREGIVER);
        addFileDefault("caregiver_hiv_assessment.csv", "Hiv Assessment For Caregiver", Constants.EcapClientTable.EC_CAREGIVER_HIV_ASSESSMENT);
        addFileDefault("case_plans_for_vca.csv", "VCA Case Plan", Constants.EcapClientTable.EC_VCA_CASE_PLAN);
        addFileDefault("case_plans_for_household.csv", "Caregiver Case Plan", Constants.EcapClientTable.EC_CAREGIVER_CASEPLAN);
        addFileDefault("hiv_assessment_for_vca.csv", "HIV Risk Assessment Below 15", Constants.EcapClientTable.EC_HIV_ASSESSMENT_BELOW_15);
        addFileDefault("referrals.csv", "Referral", Constants.EcapClientTable.EC_REFERRAL);

        // Encounter to table fallback map for all form imports
        addEncounterTable("Sub Population", Constants.EcapClientTable.EC_CLIENT_INDEX);
        addEncounterTable("Member Sub Population", Constants.EcapClientTable.EC_CLIENT_INDEX);
        addEncounterTable("Sub Population Edit", Constants.EcapClientTable.EC_CLIENT_INDEX);
        addEncounterTable("Case Record Status", Constants.EcapClientTable.EC_CLIENT_INDEX);
        addEncounterTable("Household Screening", Constants.EcapClientTable.EC_HOUSEHOLD);
        addEncounterTable("Household Screening Edit", Constants.EcapClientTable.EC_HOUSEHOLD);
        addEncounterTable("Household Case Status", Constants.EcapClientTable.EC_HOUSEHOLD);
        addEncounterTable("Mother Register", Constants.EcapClientTable.EC_MOTHER_INDEX);
        addEncounterTable("Mother Register From Service", Constants.EcapClientTable.EC_MOTHER_INDEX);
        addEncounterTable("Mother Pmtct", Constants.EcapClientTable.EC_MOTHER_PMTCT);
        addEncounterTable("Mother PMTCT Register From Service", Constants.EcapClientTable.EC_MOTHER_PMTCT);
        addEncounterTable("HIV Testing Service", Constants.EcapClientTable.EC_HIV_TESTING_SERVICE);
        addEncounterTable("HIV Testing Links", Constants.EcapClientTable.EC_HIV_TESTING_LINKS);
        addEncounterTable("VCA Service Report", Constants.EcapClientTable.EC_VCA_SERVICE_REPORT);
        addEncounterTable("VCA Service Report Edit", Constants.EcapClientTable.EC_VCA_SERVICE_REPORT);
        addEncounterTable("Household Service Report", Constants.EcapClientTable.EC_HOUSEHOLD_SERVICE);
        addEncounterTable("Household Service Report Edit", Constants.EcapClientTable.EC_HOUSEHOLD_SERVICE);
        addEncounterTable("VCA Case Plan", Constants.EcapClientTable.EC_VCA_CASE_PLAN);
        addEncounterTable("Caregiver Case Plan", Constants.EcapClientTable.EC_CAREGIVER_CASEPLAN);
        addEncounterTable("Case_Plan_Index", Constants.EcapClientTable.EC_CAREGIVER_CASEPLAN);
        addEncounterTable("VCA Assessment", Constants.EcapClientTable.EC_ASSESSMENT);
        addEncounterTable("Referral", Constants.EcapClientTable.EC_REFERRAL);
        addEncounterTable("Referral Edit VCA", Constants.EcapClientTable.EC_REFERRAL);
        addEncounterTable("Household Visitation Form 0-20 years", Constants.EcapClientTable.EC_HOUSEHOLD_VCA);
        addEncounterTable("Household Visitation Form 0-20 years Edit", Constants.EcapClientTable.EC_HOUSEHOLD_VCA);
        addEncounterTable("Household Visitation For Caregiver", Constants.EcapClientTable.EC_HOUSEHOLD_CAREGIVER);
        addEncounterTable("Household Visitation For Caregiver Edit", Constants.EcapClientTable.EC_HOUSEHOLD_CAREGIVER);
        addEncounterTable("Household Visitation Form", Constants.EcapClientTable.EC_HOUSEHOLD_CAREGIVER);
        addEncounterTable("Hiv Assessment For Caregiver", Constants.EcapClientTable.EC_CAREGIVER_HIV_ASSESSMENT);
        addEncounterTable("Hiv Assessment For Caregiver Edit", Constants.EcapClientTable.EC_CAREGIVER_HIV_ASSESSMENT);
        addEncounterTable("HIV Risk Assessment Above 15", Constants.EcapClientTable.EC_HIV_ASSESSMENT_ABOVE_15);
        addEncounterTable("hiv_risk_assessment_adults", Constants.EcapClientTable.EC_HIV_ASSESSMENT_ABOVE_15);
        addEncounterTable("HIV Risk Assessment Below 15", Constants.EcapClientTable.EC_HIV_ASSESSMENT_BELOW_15);
        addEncounterTable("Nutrition Assessment and Intervention", Constants.EcapClientTable.EC_NUTRITION_ASSESSMENT_INTERVENTION);
        addEncounterTable("Child Safety Plan", Constants.EcapClientTable.EC_CHILD_SAFETY_PLAN);
        addEncounterTable("Child Safety Actions", Constants.EcapClientTable.EC_CHILD_SAFETY_ACTION);
        addEncounterTable("WE Services VCA", Constants.EcapClientTable.EC_WE_SERVICES_VCA);
        addEncounterTable("WE Services Caregiver", Constants.EcapClientTable.EC_WE_SERVICES_CAREGIVER);
        addEncounterTable("TB Screening", Constants.EcapClientTable.EC_TB_SCREENING);
        addEncounterTable("TB Screening Caregiver", Constants.EcapClientTable.EC_TB_SCREENING_CAREGIVER);
        addEncounterTable("Mother Pmtct ANC", Constants.EcapClientTable.EC_MOTHER_ANC);
        addEncounterTable("Final Outcome of Infant", Constants.EcapClientTable.EC_CHILD_FINAL_OUTCOME);
        addEncounterTable("Infant Longitudinal Follow-up", Constants.EcapClientTable.EC_CHILD_LONGITUDINAL_FOLLOW_UP);
        addEncounterTable("Post Natal Care-Infant", Constants.EcapClientTable.EC_CHILD_POSTNATAL_CARE);
    }

    private static void addFileDefault(String fileName, String encounterType, String table) {
        FILE_DEFAULTS.put(normalize(fileName), new ImportMeta(encounterType, table));
    }

    private static void addEncounterTable(String encounterType, String table) {
        ENCOUNTER_TO_TABLE.put(normalize(encounterType), table);
        String normalizedTable = normalize(table);
        if (!TABLE_TO_DEFAULT_ENCOUNTER.containsKey(normalizedTable)) {
            TABLE_TO_DEFAULT_ENCOUNTER.put(normalizedTable, encounterType);
        }
    }

    public static ImportSummary importFromCsvUri(Context context, Uri csvUri) {
        return importFromCsvUri(context, csvUri, null);
    }

    public static ImportSummary importFromCsvUri(Context context, Uri csvUri, ImportProgressListener progressListener) {
        ImportSummary summary = new ImportSummary();
        summary.fileName = resolveDisplayName(context, csvUri);
        summary.totalRows = countDataRows(context, csvUri);
        notifyProgress(progressListener, summary);

        CoreChwApplication app = ChwApplication.getInstance();
        if (app == null) {
            summary.failedRows++;
            summary.errors.add("Application context unavailable.");
            return summary;
        }

        FormTag formTag = IndexClientsUtils.getFormTag();
        ECSyncHelper ecSyncHelper = app.getEcSyncHelper();
        if (ecSyncHelper == null) {
            summary.failedRows++;
            summary.errors.add("Sync helper unavailable.");
            return summary;
        }

        ensureTableFieldKeyMapLoaded(context);
        ImportMeta fileDefault = resolveFileDefault(summary.fileName);
        List<String> savedFormIds = new ArrayList<>();

        try (InputStream inputStream = context.getContentResolver().openInputStream(csvUri)) {
            if (inputStream == null) {
                summary.failedRows++;
                summary.errors.add("Unable to open selected CSV.");
                return summary;
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

                List<String> header = readCsvRecord(reader);
                if (header == null || header.isEmpty()) {
                    summary.failedRows++;
                    summary.errors.add("CSV has no header row.");
                    return summary;
                }

                Map<Integer, String> normalizedHeader = new LinkedHashMap<>();
                for (int i = 0; i < header.size(); i++) {
                    normalizedHeader.put(i, normalize(header.get(i)));
                }

                int rowNumber = 1;
                List<String> row;
                while ((row = readCsvRecord(reader)) != null) {
                    rowNumber++;
                    try {
                        Map<String, String> rowMap = buildRowMap(normalizedHeader, row);
                        if (isRowBlank(rowMap)) {
                            summary.skippedRows++;
                            continue;
                        }

                        String encounterType = firstNonBlank(
                                rowMap.get("encounter_type"),
                                fileDefault != null ? fileDefault.encounterType : null
                        );

                        String entityTable = firstNonBlank(
                                rowMap.get("entity_table"),
                                rowMap.get("table_name"),
                                rowMap.get("table"),
                                fileDefault != null ? fileDefault.entityTable : null
                        );

                        if (TextUtils.isEmpty(entityTable) && !TextUtils.isEmpty(encounterType)) {
                            entityTable = ENCOUNTER_TO_TABLE.get(normalize(encounterType));
                        }

                        // ec_client_index CSV imports should only create/update ec_client_index.
                        // "Sub Population" creates ec_household too in classification, so force edit encounter.
                        if (Constants.EcapClientTable.EC_CLIENT_INDEX.equalsIgnoreCase(entityTable)
                                && "Sub Population".equalsIgnoreCase(encounterType)) {
                            encounterType = "Sub Population Edit";
                        }
                        if (Constants.EcapClientTable.EC_MOTHER_INDEX.equalsIgnoreCase(entityTable)
                                && "Mother Register".equalsIgnoreCase(encounterType)) {
                            encounterType = "Mother Register From Service";
                        }

                        if (TextUtils.isEmpty(encounterType) || TextUtils.isEmpty(entityTable)) {
                            summary.failedRows++;
                            summary.errors.add("Row " + rowNumber + ": missing encounter/table mapping.");
                            continue;
                        }

                        String entityId = rowMap.get("base_entity_id");
                        if (TextUtils.isEmpty(entityId)) {
                            summary.failedRows++;
                            summary.errors.add("Row " + rowNumber + ": missing base_entity_id.");
                            continue;
                        }

                        if (baseEntityIdExistsInTable(entityTable, entityId)) {
                            summary.skippedRows++;
                            continue;
                        }

                        String formSubmissionId = firstNonBlank(
                                rowMap.get("form_submission_id"),
                                buildDeterministicFormSubmissionId(rowMap, entityId, encounterType, entityTable)
                        );

                        if (isAlreadyImported(ecSyncHelper, formSubmissionId)) {
                            summary.skippedRows++;
                            continue;
                        }

                        JSONArray fields = buildFields(rowMap, entityTable);
                        if (fields.length() == 0) {
                            summary.skippedRows++;
                            continue;
                        }

                        JSONObject metadata = new JSONObject();
                        metadata.put("encounter_location", safeValue(rowMap.get("encounter_location")));

                        Event event = org.smartregister.util.JsonFormUtils.createEvent(
                                fields,
                                metadata,
                                formTag,
                                entityId,
                                encounterType,
                                entityTable
                        );
                        JsonFormUtils.tagSyncMetadata(event);
                        event.setFormSubmissionId(formSubmissionId);

                        Client client = org.smartregister.util.JsonFormUtils.createBaseClient(fields, formTag, entityId);
                        saveClientAndEvent(ecSyncHelper, client, event);
                        savedFormIds.add(formSubmissionId);
                        if (savedFormIds.size() >= EVENT_PROCESSING_CHUNK_SIZE) {
                            ChunkProcessResult chunkResult = processSavedEventsWithFallback(app, ecSyncHelper, savedFormIds);
                            if (chunkResult.failedCount > 0) {
                                summary.failedRows += chunkResult.failedCount;
                                summary.importedRows = Math.max(0, summary.importedRows - chunkResult.failedCount);
                                if (chunkResult.timedOut) {
                                    summary.timedOutDuringProcessing = true;
                                }
                                if (chunkResult.processingError) {
                                    summary.processingError = true;
                                }
                                if (!TextUtils.isEmpty(chunkResult.message)) {
                                    summary.errors.add(chunkResult.message);
                                }
                            }
                            savedFormIds.clear();
                        }
                        summary.importedRows++;
                    } catch (Exception e) {
                        summary.failedRows++;
                        summary.errors.add("Row " + rowNumber + ": " + e.getMessage());
                        Timber.e(e, "CSV import failed at row %s", rowNumber);
                    } finally {
                        summary.processedRows++;
                        notifyProgress(progressListener, summary);
                    }
                }
            }
            if (!savedFormIds.isEmpty()) {
                ChunkProcessResult chunkResult = processSavedEventsWithFallback(app, ecSyncHelper, savedFormIds);
                if (chunkResult.failedCount > 0) {
                    summary.failedRows += chunkResult.failedCount;
                    summary.importedRows = Math.max(0, summary.importedRows - chunkResult.failedCount);
                    if (chunkResult.timedOut) {
                        summary.timedOutDuringProcessing = true;
                    }
                    if (chunkResult.processingError) {
                        summary.processingError = true;
                    }
                    if (!TextUtils.isEmpty(chunkResult.message)) {
                        summary.errors.add(chunkResult.message);
                    }
                }
            }
        } catch (Exception e) {
            summary.failedRows++;
            summary.errors.add("Import failed: " + e.getMessage());
            Timber.e(e, "CSV import failed");
        }

        // Ensure final progress notification reaches 100%.
        if (summary.totalRows > 0 && summary.processedRows < summary.totalRows) {
            summary.processedRows = summary.totalRows;
        }
        notifyProgress(progressListener, summary);
        return summary;
    }

    public static int getDataRowCount(Context context, Uri csvUri) {
        return countDataRows(context, csvUri);
    }

    private static void saveClientAndEvent(ECSyncHelper ecSyncHelper, Client client, Event event) throws JSONException {
        JSONObject newClientJsonObject = new JSONObject(org.smartregister.util.JsonFormUtils.gson.toJson(client));
        JSONObject existingClientJsonObject = ecSyncHelper.getClient(client.getBaseEntityId());

        if (existingClientJsonObject != null) {
            JSONObject mergedClientJsonObject = org.smartregister.util.JsonFormUtils.merge(existingClientJsonObject, newClientJsonObject);
            ecSyncHelper.addClient(client.getBaseEntityId(), mergedClientJsonObject);
        } else {
            ecSyncHelper.addClient(client.getBaseEntityId(), newClientJsonObject);
        }

        JSONObject eventJsonObject = new JSONObject(org.smartregister.util.JsonFormUtils.gson.toJson(event));
        ecSyncHelper.addEvent(event.getBaseEntityId(), eventJsonObject);
    }

    private static final class ProcessResult {
        final boolean success;
        final boolean timedOut;
        final String message;

        ProcessResult(boolean success, boolean timedOut, String message) {
            this.success = success;
            this.timedOut = timedOut;
            this.message = message;
        }
    }

    private static final class ChunkProcessResult {
        final int failedCount;
        final boolean timedOut;
        final boolean processingError;
        final String message;

        ChunkProcessResult(int failedCount, boolean timedOut, boolean processingError, String message) {
            this.failedCount = failedCount;
            this.timedOut = timedOut;
            this.processingError = processingError;
            this.message = message;
        }
    }

    private static ChunkProcessResult processSavedEventsWithFallback(CoreChwApplication app,
                                                                     ECSyncHelper ecSyncHelper,
                                                                     List<String> formSubmissionIds) {
        if (formSubmissionIds == null || formSubmissionIds.isEmpty()) {
            return new ChunkProcessResult(0, false, false, null);
        }

        ProcessResult bulkResult = processSavedEvents(app, ecSyncHelper, formSubmissionIds);
        if (bulkResult.success) {
            return new ChunkProcessResult(0, false, false, null);
        }

        int failedCount = 0;
        boolean timedOut = bulkResult.timedOut;
        boolean processingError = !bulkResult.timedOut;
        StringBuilder issues = new StringBuilder();

        for (String formId : formSubmissionIds) {
            ProcessResult singleResult = processSavedEvents(app, ecSyncHelper, Collections.singletonList(formId));
            if (singleResult.success) {
                continue;
            }

            failedCount++;
            timedOut = timedOut || singleResult.timedOut;
            processingError = processingError || !singleResult.timedOut;

            if (issues.length() == 0) {
                issues.append("Processing issue(s): ");
            }
            if (failedCount <= 3) {
                if (failedCount > 1) {
                    issues.append(" | ");
                }
                issues.append("formSubmissionId=").append(formId).append(" -> ").append(singleResult.message);
            }
        }

        if (failedCount == 0) {
            return new ChunkProcessResult(0, false, false, null);
        }

        if (issues.length() == 0) {
            issues.append(bulkResult.message);
        }

        return new ChunkProcessResult(failedCount, timedOut, processingError, issues.toString());
    }

    private static ProcessResult processSavedEvents(CoreChwApplication app, ECSyncHelper ecSyncHelper, List<String> formSubmissionIds) {
        if (formSubmissionIds == null || formSubmissionIds.isEmpty()) {
            return new ProcessResult(true, false, "No events to process.");
        }
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Boolean> future = null;
        try {
            Callable<Boolean> task = () -> {
                List<EventClient> savedEvents = ecSyncHelper.getEvents(formSubmissionIds);
                app.getClientProcessorForJava().processClient(savedEvents != null ? savedEvents : Collections.emptyList());
                return true;
            };
            future = executor.submit(task);
            future.get(EVENT_PROCESSING_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            return new ProcessResult(true, false, "Processed " + formSubmissionIds.size() + " events.");
        } catch (TimeoutException e) {
            if (future != null) {
                future.cancel(true);
            }
            String message = "Timed out while processing " + formSubmissionIds.size() + " events.";
            Timber.e(e, message);
            return new ProcessResult(false, true, message);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            String message = "Processing interrupted for " + formSubmissionIds.size() + " events.";
            Timber.e(e, message);
            return new ProcessResult(false, false, message);
        } catch (ExecutionException e) {
            String rootMessage = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
            String message = "Processing error for " + formSubmissionIds.size() + " events: " + rootMessage;
            Timber.e(e, message);
            return new ProcessResult(false, false, message);
        } catch (Exception e) {
            String message = "Processing error for " + formSubmissionIds.size() + " events: " + e.getMessage();
            Timber.e(e, message);
            return new ProcessResult(false, false, message);
        } finally {
            executor.shutdownNow();
        }
    }

    private static JSONArray buildFields(Map<String, String> rowMap, String entityTable) throws JSONException {
        JSONArray fields = new JSONArray();
        for (Map.Entry<String, String> entry : rowMap.entrySet()) {
            String key = entry.getKey();
            if (RESERVED_COLUMNS.contains(key)) {
                continue;
            }

            String value = entry.getValue();
            if (TextUtils.isEmpty(key) || TextUtils.isEmpty(value)) {
                continue;
            }

            String fieldKey = resolveFieldKeyForTable(entityTable, key);
            if (shouldSkipFieldForTable(entityTable, fieldKey, value)) {
                continue;
            }

            boolean isPersonNameField = "firstName".equalsIgnoreCase(fieldKey)
                    || "lastName".equalsIgnoreCase(fieldKey);
            String normalizedFieldKey = normalizeImportedFieldKey(fieldKey);

            JSONObject field = new JSONObject();
            field.put(JsonFormConstants.KEY, normalizedFieldKey);
            field.put(JsonFormConstants.VALUE, value);
            field.put("openmrs_entity_parent", "");
            field.put("openmrs_entity", isPersonNameField ? "person" : "person_attribute");
            field.put("openmrs_entity_id", normalizedFieldKey);
            field.put("openmrs_data_type", "text");
            field.put("type", "edit_text");
            fields.put(field);
        }
        return fields;
    }

    private static boolean shouldSkipFieldForTable(String entityTable, String key, String value) {
        if (TextUtils.isEmpty(entityTable) || TextUtils.isEmpty(key)) {
            return false;
        }

        // Household register list is intentionally filtered with `status IS NULL`.
        // CSV exports usually contain status=0, which would hide imported households.
        if (Constants.EcapClientTable.EC_HOUSEHOLD.equalsIgnoreCase(entityTable)
                && "status".equalsIgnoreCase(key)) {
            String normalizedValue = value.trim().toLowerCase(Locale.US);
            return "0".equals(normalizedValue) || "false".equals(normalizedValue);
        }

        return false;
    }

    private static Map<String, String> buildRowMap(Map<Integer, String> header, List<String> row) {
        Map<String, String> map = new LinkedHashMap<>();
        for (int i = 0; i < header.size(); i++) {
            String key = header.get(i);
            String value = i < row.size() ? row.get(i) : "";
            map.put(key, value != null ? value.trim() : "");
        }
        return map;
    }

    private static ImportMeta resolveFileDefault(String fileName) {
        ImportMeta explicit = FILE_DEFAULTS.get(normalize(fileName));
        if (explicit != null) {
            return explicit;
        }

        String tableFromFileName = deriveTableNameFromFileName(fileName);
        if (TextUtils.isEmpty(tableFromFileName)) {
            return null;
        }

        // Prevent ec_household side-effects when importing ec_client_index.csv.
        if (Constants.EcapClientTable.EC_CLIENT_INDEX.equalsIgnoreCase(tableFromFileName)) {
            return new ImportMeta("Sub Population Edit", tableFromFileName);
        }
        // Prevent multi-table side-effects when importing ec_mother_index.csv.
        if (Constants.EcapClientTable.EC_MOTHER_INDEX.equalsIgnoreCase(tableFromFileName)) {
            return new ImportMeta("Mother Register From Service", tableFromFileName);
        }

        String defaultEncounter = TABLE_TO_DEFAULT_ENCOUNTER.get(normalize(tableFromFileName));
        return new ImportMeta(defaultEncounter, tableFromFileName);
    }

    private static String deriveTableNameFromFileName(String fileName) {
        String normalized = normalize(fileName);
        if (TextUtils.isEmpty(normalized)) {
            return null;
        }

        int slash = Math.max(normalized.lastIndexOf('/'), normalized.lastIndexOf('\\'));
        if (slash >= 0 && slash + 1 < normalized.length()) {
            normalized = normalized.substring(slash + 1);
        }

        if (normalized.endsWith(".csv")) {
            normalized = normalized.substring(0, normalized.length() - 4);
        }

        if (normalized.startsWith("ec_")) {
            return normalized;
        }
        return null;
    }

    private static String resolveFieldKeyForTable(String entityTable, String normalizedColumnKey) {
        if (TextUtils.isEmpty(entityTable) || TextUtils.isEmpty(normalizedColumnKey)) {
            return normalizedColumnKey;
        }

        Map<String, String> fieldMap = TABLE_COLUMN_TO_FIELD_KEY.get(normalize(entityTable));
        if (fieldMap == null || fieldMap.isEmpty()) {
            return normalizedColumnKey;
        }

        String mapped = fieldMap.get(normalizedColumnKey);
        return TextUtils.isEmpty(mapped) ? normalizedColumnKey : mapped;
    }

    private static void ensureTableFieldKeyMapLoaded(Context context) {
        if (TABLE_FIELD_MAP_LOADED || context == null) {
            return;
        }

        synchronized (TABLE_FIELD_MAP_LOCK) {
            if (TABLE_FIELD_MAP_LOADED) {
                return;
            }

            try (InputStream inputStream = context.getAssets().open("ec_client_fields.json");
                 BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

                StringBuilder builder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }

                JSONObject root = new JSONObject(builder.toString());
                JSONArray bindObjects = root.optJSONArray("bindobjects");
                if (bindObjects != null) {
                    for (int i = 0; i < bindObjects.length(); i++) {
                        JSONObject tableObject = bindObjects.optJSONObject(i);
                        if (tableObject == null) {
                            continue;
                        }

                        String tableName = normalize(tableObject.optString("name"));
                        if (TextUtils.isEmpty(tableName)) {
                            continue;
                        }

                        JSONArray columns = tableObject.optJSONArray("columns");
                        if (columns == null || columns.length() == 0) {
                            continue;
                        }

                        Map<String, String> mapping = new HashMap<>();
                        for (int j = 0; j < columns.length(); j++) {
                            JSONObject column = columns.optJSONObject(j);
                            if (column == null) {
                                continue;
                            }

                            String columnName = column.optString("column_name");
                            String normalizedColumn = normalize(columnName);
                            if (TextUtils.isEmpty(normalizedColumn)) {
                                continue;
                            }

                            JSONObject jsonMapping = column.optJSONObject("json_mapping");
                            String fieldPath = jsonMapping != null ? jsonMapping.optString("field") : null;
                            String fieldKey = extractFieldLeaf(fieldPath);
                            if (TextUtils.isEmpty(fieldKey)) {
                                continue;
                            }

                            mapping.put(normalizedColumn, fieldKey);
                        }

                        if (!mapping.isEmpty()) {
                            TABLE_COLUMN_TO_FIELD_KEY.put(tableName, mapping);
                        }
                    }
                }
            } catch (Exception e) {
                Timber.w(e, "Unable to load ec_client_fields.json mappings for CSV import.");
            } finally {
                TABLE_FIELD_MAP_LOADED = true;
            }
        }
    }

    private static String extractFieldLeaf(String fieldPath) {
        if (TextUtils.isEmpty(fieldPath)) {
            return null;
        }
        int dot = fieldPath.lastIndexOf('.');
        if (dot < 0 || dot == fieldPath.length() - 1) {
            return fieldPath;
        }
        return fieldPath.substring(dot + 1);
    }

    private static String normalizeImportedFieldKey(String fieldKey) {
        if (TextUtils.isEmpty(fieldKey)) {
            return fieldKey;
        }
        if ("firstName".equalsIgnoreCase(fieldKey)) {
            return "first_name";
        }
        if ("lastName".equalsIgnoreCase(fieldKey)) {
            return "last_name";
        }
        return fieldKey;
    }

    private static boolean isRowBlank(Map<String, String> rowMap) {
        for (Map.Entry<String, String> entry : rowMap.entrySet()) {
            if (RESERVED_COLUMNS.contains(entry.getKey())) {
                continue;
            }
            if (!TextUtils.isEmpty(entry.getValue())) {
                return false;
            }
        }
        return true;
    }

    private static String normalize(String value) {
        if (value == null) {
            return "";
        }
        return value.trim().toLowerCase(Locale.US);
    }

    private static String firstNonBlank(String... values) {
        for (String value : values) {
            if (!TextUtils.isEmpty(value)) {
                return value.trim();
            }
        }
        return null;
    }

    private static String safeValue(String value) {
        return value == null ? "" : value;
    }

    private static boolean baseEntityIdExistsInTable(String entityTable, String baseEntityId) {
        if (TextUtils.isEmpty(entityTable) || TextUtils.isEmpty(baseEntityId)) {
            return false;
        }

        String safeTable = sanitizeTableName(entityTable);
        if (TextUtils.isEmpty(safeTable)) {
            return false;
        }

        try {
            CoreChwApplication app = ChwApplication.getInstance();
            if (app == null || app.getRepository() == null) {
                return false;
            }
            SQLiteDatabase db = app.getRepository().getReadableDatabase();
            if (db == null) {
                return false;
            }

            String sql = "SELECT 1 FROM \"" + safeTable + "\" WHERE base_entity_id = ? LIMIT 1";
            try (Cursor cursor = db.rawQuery(sql, new String[]{baseEntityId})) {
                return cursor != null && cursor.moveToFirst();
            }
        } catch (Exception e) {
            Timber.w(e, "Failed duplicate base_entity_id check for table %s", entityTable);
            return false;
        }
    }

    private static String sanitizeTableName(String tableName) {
        if (TextUtils.isEmpty(tableName)) {
            return null;
        }
        String trimmed = tableName.trim();
        if (!trimmed.matches("[A-Za-z0-9_]+")) {
            return null;
        }
        return trimmed;
    }

    private static int countDataRows(Context context, Uri csvUri) {
        int count = 0;
        try (InputStream inputStream = context.getContentResolver().openInputStream(csvUri)) {
            if (inputStream == null) {
                return 0;
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                // Skip header
                if (readCsvRecord(reader) == null) {
                    return 0;
                }
                while (readCsvRecord(reader) != null) {
                    count++;
                }
            }
        } catch (Exception e) {
            Timber.w(e, "Unable to count CSV rows");
        }
        return count;
    }

    private static void notifyProgress(ImportProgressListener progressListener, ImportSummary summary) {
        if (progressListener == null || summary == null) {
            return;
        }
        progressListener.onProgress(
                summary.processedRows,
                summary.totalRows,
                summary.importedRows,
                summary.skippedRows,
                summary.failedRows
        );
    }

    private static boolean isAlreadyImported(ECSyncHelper ecSyncHelper, String formSubmissionId) {
        if (TextUtils.isEmpty(formSubmissionId)) {
            return false;
        }
        try {
            List<EventClient> existing = ecSyncHelper.getEvents(Collections.singletonList(formSubmissionId));
            return existing != null && !existing.isEmpty();
        } catch (Exception e) {
            Timber.w(e, "Unable to check existing event for formSubmissionId %s", formSubmissionId);
            return false;
        }
    }

    private static String buildDeterministicFormSubmissionId(Map<String, String> rowMap,
                                                             String entityId,
                                                             String encounterType,
                                                             String entityTable) {
        StringBuilder payload = new StringBuilder();
        payload.append(safeValue(entityId)).append('|')
                .append(safeValue(encounterType)).append('|')
                .append(safeValue(entityTable));

        Map<String, String> sorted = new TreeMap<>(rowMap);
        for (Map.Entry<String, String> entry : sorted.entrySet()) {
            String key = entry.getKey();
            if (RESERVED_COLUMNS.contains(key)) {
                continue;
            }
            payload.append('|').append(key).append('=').append(safeValue(entry.getValue()));
        }
        return UUID.nameUUIDFromBytes(payload.toString().getBytes(StandardCharsets.UTF_8)).toString();
    }

    private static String resolveDisplayName(Context context, Uri uri) {
        if (uri == null) {
            return null;
        }
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(uri, new String[]{OpenableColumns.DISPLAY_NAME}, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (index >= 0) {
                    return cursor.getString(index);
                }
            }
        } catch (Exception e) {
            Timber.w(e, "Unable to resolve display name for uri %s", uri);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return uri.getLastPathSegment();
    }

    private static List<String> readCsvRecord(BufferedReader reader) throws IOException {
        List<String> record = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean inQuotes = false;
        boolean hasContent = false;

        while (true) {
            reader.mark(1);
            int read = reader.read();
            if (read == -1) {
                if (!hasContent && field.length() == 0 && record.isEmpty()) {
                    return null;
                }
                record.add(field.toString());
                return record;
            }

            hasContent = true;
            char ch = (char) read;

            if (ch == '"') {
                if (inQuotes) {
                    reader.mark(1);
                    int peek = reader.read();
                    if (peek == '"') {
                        field.append('"');
                    } else {
                        inQuotes = false;
                        if (peek != -1) {
                            reader.reset();
                        }
                    }
                } else if (field.length() == 0) {
                    inQuotes = true;
                } else {
                    field.append(ch);
                }
                continue;
            }

            if (ch == ',' && !inQuotes) {
                record.add(field.toString());
                field.setLength(0);
                continue;
            }

            if ((ch == '\n' || ch == '\r') && !inQuotes) {
                if (ch == '\r') {
                    reader.mark(1);
                    int next = reader.read();
                    if (next != '\n' && next != -1) {
                        reader.reset();
                    }
                }
                record.add(field.toString());
                return record;
            }

            field.append(ch);
        }
    }
}
