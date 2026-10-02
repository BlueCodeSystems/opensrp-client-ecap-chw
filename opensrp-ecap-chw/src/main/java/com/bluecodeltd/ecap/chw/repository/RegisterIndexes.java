package com.bluecodeltd.ecap.chw.repository;

import android.database.Cursor;

import net.sqlcipher.database.SQLiteDatabase;

import com.bluecodeltd.ecap.chw.application.ChwApplication;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import timber.log.Timber;

/**
 * Indexes for the per-household lookups the registers run for every row
 * (genders, ages, child count, screened). Without them each row does full scans of
 * ec_client_index / ec_household, which starves the UI when sync is also using the DB.
 *
 * The ec_* tables are created by the client processor, not by onCreate, so this is
 * applied both from the DB migration and lazily once the tables exist.
 */
public final class RegisterIndexes {

    private static final String[][] INDEXES = {
            {"ec_client_index", "CREATE INDEX IF NOT EXISTS idx_ec_client_index_household_id ON ec_client_index(household_id)"},
            {"ec_household", "CREATE INDEX IF NOT EXISTS idx_ec_household_household_id ON ec_household(household_id)"},
            // Household graduation checks join service reports to household members.
            {"ec_vca_service_report", "CREATE INDEX IF NOT EXISTS idx_ec_vca_service_report_unique_id ON ec_vca_service_report(unique_id)"},
            {"ec_household_service_report", "CREATE INDEX IF NOT EXISTS idx_ec_household_service_report_household_id ON ec_household_service_report(household_id)"},
            // Household profile tab titles / overview: one per-household lookup per tab on every open.
            {"ec_household_visitation_for_caregiver", "CREATE INDEX IF NOT EXISTS idx_ec_household_visitation_for_caregiver_household_id ON ec_household_visitation_for_caregiver(household_id)"},
            {"ec_caregiver_case_plan", "CREATE INDEX IF NOT EXISTS idx_ec_caregiver_case_plan_household_id ON ec_caregiver_case_plan(household_id)"},
            {"ec_tb_screening_caregiver", "CREATE INDEX IF NOT EXISTS idx_ec_tb_screening_caregiver_household_id ON ec_tb_screening_caregiver(household_id)"},
            {"ec_graduation", "CREATE INDEX IF NOT EXISTS idx_ec_graduation_household_id ON ec_graduation(household_id)"},
            {"ec_mother_index", "CREATE INDEX IF NOT EXISTS idx_ec_mother_index_household_id ON ec_mother_index(household_id)"},
    };

    private static final Pattern SAFE_TABLE_NAME = Pattern.compile("[A-Za-z0-9_]+");

    private static volatile boolean ensured = false;

    private RegisterIndexes() {
    }

    /** Best-effort, once per process. Call from a background thread. */
    public static void ensureOnce() {
        if (ensured) return;
        try {
            ensured = ensure(ChwApplication.getInstance().getRepository().getWritableDatabase());
        } catch (Exception e) {
            Timber.i(e, "Register indexes not created yet");
        }
    }

    /** @return true when every index exists (i.e. all tables were present). */
    public static boolean ensure(SQLiteDatabase db) {
        boolean all = true;
        for (String[] index : INDEXES) {
            if (!tableExists(db, index[0])) {
                all = false;
                continue;
            }
            db.execSQL(index[1]);
        }
        ensureBaseEntityIdIndexes(db);
        return all;
    }

    /**
     * Sync's client processor looks up every incoming record with
     * {@code SELECT * FROM <ec table> WHERE base_entity_id = ?} (CommonRepository.addMissingContentValuesForRecordId).
     * The core library does index base_entity_id, but as {@code COLLATE NOCASE}; the lookup compares with the
     * column's default BINARY collation, so SQLite can't use that index and scans the whole table for each record
     * -- holding the single DB lock long enough to stall any main-thread query during a sync. Add a plain
     * (BINARY) index on every ec_* table that has the column.
     */
    private static void ensureBaseEntityIdIndexes(SQLiteDatabase db) {
        List<String> tables = new ArrayList<>();
        try (Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name LIKE 'ec\\_%' ESCAPE '\\' "
                + "AND sql NOT LIKE 'CREATE VIRTUAL%'", null)) {
            while (c != null && c.moveToNext()) {
                tables.add(c.getString(0));
            }
        }
        for (String table : tables) {
            if (!SAFE_TABLE_NAME.matcher(table).matches() || !hasColumn(db, table, "base_entity_id")) {
                continue;
            }
            db.execSQL("CREATE INDEX IF NOT EXISTS idx_" + table + "_base_entity_id_bin ON " + table + "(base_entity_id)");
        }
    }

    private static boolean hasColumn(SQLiteDatabase db, String table, String column) {
        try (Cursor c = db.rawQuery("PRAGMA table_info(" + table + ")", null)) {
            int nameIndex = c == null ? -1 : c.getColumnIndex("name");
            while (nameIndex >= 0 && c.moveToNext()) {
                if (column.equalsIgnoreCase(c.getString(nameIndex))) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean tableExists(SQLiteDatabase db, String table) {
        try (Cursor c = db.rawQuery("SELECT 1 FROM sqlite_master WHERE type='table' AND name=?", new String[]{table})) {
            return c != null && c.moveToFirst();
        }
    }
}
