package com.bluecodeltd.ecap.chw.dao;

import android.database.Cursor;
import android.content.SharedPreferences;

import com.bluecodeltd.ecap.chw.model.MonthlyReportModel;
import com.bluecodeltd.ecap.chw.application.ChwApplication;

import androidx.preference.PreferenceManager;

import org.smartregister.dao.AbstractDao;

import java.util.ArrayList;
import java.util.List;

import net.sqlcipher.database.SQLiteDatabase;

public class MonthlyReportDao extends AbstractDao {
    private static final String PREF_CASEWORKER_NAME = "caseworker_name";
    private static final String PREF_LAST_USERNAME = "last_logged_in_username";

    public static List<MonthlyReportModel> getReports(String tableName) {
        String sql = "SELECT * FROM " + tableName + buildMonthlyReportWhereClause(tableName) +
                " ORDER BY last_interacted_with DESC, rowid DESC";
        List<MonthlyReportModel> values = AbstractDao.readData(sql, getModelMap());
        if (values == null || values.isEmpty()) {
            return new ArrayList<>();
        }
        return values;
    }

    public static MonthlyReportModel getLatestReport(String tableName) {
        String sql = "SELECT * FROM " + tableName + buildMonthlyReportWhereClause(tableName) +
                " ORDER BY last_interacted_with DESC, rowid DESC LIMIT 1";
        List<MonthlyReportModel> values = AbstractDao.readData(sql, getModelMap());
        if (values == null || values.isEmpty()) {
            return null;
        }
        return values.get(0);
    }

    public static int getCount(String tableName) {
        String sql = "SELECT COUNT(*) AS c FROM " + tableName + buildMonthlyReportWhereClause(tableName);
        return org.smartregister.chw.core.dao.NavigationDao.getQueryCount(sql);
    }

    public static MonthlyReportModel getReport(String tableName, String baseEntityId) {
        String sql = "SELECT * FROM " + tableName + buildMonthlyReportWhereClause(tableName, "base_entity_id = '" + sanitize(baseEntityId) + "'");
        List<MonthlyReportModel> values = AbstractDao.readData(sql, getModelMap());
        if (values == null || values.isEmpty()) {
            return null;
        }
        return values.get(0);
    }

    private static AbstractDao.DataMap<MonthlyReportModel> getModelMap() {
        return c -> {
            MonthlyReportModel record = new MonthlyReportModel();
            record.setBase_entity_id(getCursorValue(c, "base_entity_id"));
            record.setFormSubmissionId(getCursorValue(c, "formSubmissionId"));
            record.setForm_id(getCursorValue(c, "form_id"));
            record.setReporting_month(getCursorValue(c, "reporting_month"));
            record.setProvince(getCursorValue(c, "province"));
            record.setDistrict(getCursorValue(c, "district"));
            record.setWard(getCursorValue(c, "ward"));
            record.setFacility(getCursorValue(c, "facility"));
            record.setPartner(getCursorValue(c, "partner"));
            record.setCaseworker_name(getCursorValue(c, "caseworker_name"));
            record.setDelete_status(getCursorValue(c, "delete_status"));
            record.setLast_interacted_with(getCursorValue(c, "last_interacted_with"));
            DaoModelFieldMapper.captureAdditionalFields(c, record);
            return record;
        };
    }

    private static String buildCaseworkerClause(String tableName) {
        if (!hasColumn(tableName, "caseworker_name")) {
            return "";
        }
        String caseworkerName = getCurrentCaseworkerName();
        String safeCaseworkerName = sanitize(caseworkerName);
        if (safeCaseworkerName.isEmpty()) {
            return "";
        }
        return " AND caseworker_name = '" + safeCaseworkerName + "'";
    }

    private static String buildMonthlyReportWhereClause(String tableName, String... extraConditions) {
        List<String> conditions = new ArrayList<>();
        if (hasColumn(tableName, "delete_status")) {
            conditions.add("(delete_status IS NULL OR delete_status <> '1')");
        }

        String caseworkerClause = buildCaseworkerClause(tableName);
        if (!caseworkerClause.isEmpty()) {
            conditions.add(caseworkerClause.replaceFirst("^ AND ", ""));
        }

        if (extraConditions != null) {
            for (String condition : extraConditions) {
                if (condition != null && !condition.trim().isEmpty()) {
                    conditions.add(condition.trim());
                }
            }
        }

        if (conditions.isEmpty()) {
            return "";
        }

        StringBuilder builder = new StringBuilder(" WHERE ");
        for (int i = 0; i < conditions.size(); i++) {
            if (i > 0) {
                builder.append(" AND ");
            }
            builder.append(conditions.get(i));
        }
        return builder.toString();
    }

    private static String sanitize(String value) {
        return value == null ? "" : value.trim().replace("'", "''");
    }

    private static String getCurrentCaseworkerName() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ChwApplication.getInstance().getApplicationContext());
        String caseworkerName = prefs.getString(PREF_CASEWORKER_NAME, "");
        if (caseworkerName == null || caseworkerName.trim().isEmpty()) {
            caseworkerName = prefs.getString(PREF_LAST_USERNAME, "");
        }
        return caseworkerName == null ? "" : caseworkerName.trim();
    }

    private static boolean hasColumn(String tableName, String columnName) {
        SQLiteDatabase database = getRepository().getReadableDatabase();
        String pragmaQuery = "PRAGMA table_info(\"" + tableName.replace("\"", "\"\"") + "\")";
        try (Cursor cursor = database.rawQuery(pragmaQuery, null)) {
            int nameIndex = cursor.getColumnIndex("name");
            if (nameIndex < 0) {
                return false;
            }
            while (cursor.moveToNext()) {
                String name = cursor.getString(nameIndex);
                if (columnName.equalsIgnoreCase(name)) {
                    return true;
                }
            }
        } catch (Exception e) {
            timber.log.Timber.w(e, "Unable to inspect schema for %s", tableName);
        }
        return false;
    }
}
