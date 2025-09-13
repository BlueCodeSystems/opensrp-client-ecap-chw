package org.smartregister.util;

import net.sqlcipher.database.SQLiteDatabase;

import org.smartregister.commonregistry.CommonFtsObject;

import java.util.List;
import java.util.Set;

/**
 * Lightweight stubs to satisfy compile-time references in flavor code without
 * bringing in androidx.sqlite dependencies. These methods are intentionally no-ops.
 */
public class DatabaseMigrationUtils {

    public static void addFieldsToFTSTable(SQLiteDatabase db,
                                           CommonFtsObject commonFtsObject,
                                           String tableName,
                                           List<String> columns) {
        // no-op: avoid hard dependency on SupportSQLiteDatabase
    }

    public static void createAddedECTables(SQLiteDatabase db,
                                           Set<String> tableNames,
                                           CommonFtsObject commonFtsObject) {
        // no-op: avoid hard dependency on SupportSQLiteDatabase
    }

    /**
     * Attempt SQLCipher v3 -> v4 migration if needed. Best-effort: execute PRAGMA cipher_migrate.
     * Returns true if command executed without throwing, false otherwise.
     */
    public static boolean performCipherMigrationToV4(SQLiteDatabase db) {
        try {
            if (db != null && db.isOpen()) {
                db.rawExecSQL("PRAGMA cipher_migrate;");
                return true;
            }
        } catch (Throwable ignored) { }
        return false;
    }
}
