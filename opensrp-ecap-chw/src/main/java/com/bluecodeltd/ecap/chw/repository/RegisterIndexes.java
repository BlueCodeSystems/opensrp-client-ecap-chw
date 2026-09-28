package com.bluecodeltd.ecap.chw.repository;

import android.database.Cursor;

import net.sqlcipher.database.SQLiteDatabase;

import com.bluecodeltd.ecap.chw.application.ChwApplication;

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
    };

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
        return all;
    }

    private static boolean tableExists(SQLiteDatabase db, String table) {
        try (Cursor c = db.rawQuery("SELECT 1 FROM sqlite_master WHERE type='table' AND name=?", new String[]{table})) {
            return c != null && c.moveToFirst();
        }
    }
}
