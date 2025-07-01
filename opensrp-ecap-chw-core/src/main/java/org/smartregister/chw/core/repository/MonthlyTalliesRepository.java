package org.smartregister.chw.core.repository;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;

import net.sqlcipher.SQLException;
import net.sqlcipher.database.SQLiteDatabase;

import org.joda.time.LocalDate;
import org.smartregister.chw.core.application.CoreChwApplication;
import org.smartregister.chw.core.domain.DailyTally;
import org.smartregister.chw.core.domain.Hia2Indicator;
import org.smartregister.chw.core.domain.MonthlyTally;
import org.smartregister.chw.core.utils.ReportUtils;
import org.smartregister.repository.BaseRepository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MonthlyTalliesRepository extends BaseRepository {
    public static final SimpleDateFormat DF_YYYYMM = new SimpleDateFormat("yyyy-MM", Locale.ENGLISH);
    private static final String TAG = MonthlyTalliesRepository.class.getCanonicalName();
    private static final String TABLE_NAME = "monthly_tallies";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_SUBMISSION_ID = "submission_id";
    private static final String COLUMN_PROVIDER_ID = "provider_id";
    private static final String COLUMN_INDICATOR_CODE = "indicator_code";
    private static final String COLUMN_VALUE = "value";
    private static final String COLUMN_MONTH = "month";
    public static final String INDEX_UNIQUE = "CREATE UNIQUE INDEX " + TABLE_NAME + "_" + COLUMN_INDICATOR_CODE + "_" + COLUMN_MONTH + "_index" +
            " ON " + TABLE_NAME + "(" + COLUMN_INDICATOR_CODE + "," + COLUMN_MONTH + ");";
    private static final String COLUMN_EDITED = "edited";
    private static final String COLUMN_DATE_SENT = "date_sent";
    private static final String COLUMN_UPDATED_AT = "updated_at";
    private static final String COLUMN_CREATED_AT = "created_at";
    private static final String[] TABLE_COLUMNS = {
            COLUMN_ID, COLUMN_SUBMISSION_ID, COLUMN_INDICATOR_CODE, COLUMN_PROVIDER_ID,
            COLUMN_VALUE, COLUMN_MONTH, COLUMN_EDITED, COLUMN_DATE_SENT, COLUMN_CREATED_AT, COLUMN_UPDATED_AT};
    private static final String CREATE_TABLE_QUERY = "CREATE TABLE " + TABLE_NAME + "(" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_SUBMISSION_ID + " VARCHAR NOT NULL, " +
            COLUMN_INDICATOR_CODE + " VARCHAR NOT NULL," +
            COLUMN_PROVIDER_ID + " VARCHAR NOT NULL," +
            COLUMN_VALUE + " VARCHAR NOT NULL," +
            COLUMN_MONTH + " VARCHAR NOT NULL," +
            COLUMN_EDITED + " INTEGER NOT NULL DEFAULT 0," +
            COLUMN_DATE_SENT + " DATETIME NULL," +
            COLUMN_CREATED_AT + " DATETIME NULL," +
            COLUMN_UPDATED_AT + " TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP," +
            "CONSTRAINT " + COLUMN_SUBMISSION_ID + "_unique UNIQUE (" + COLUMN_SUBMISSION_ID + "))";
    private static final String INDEX_PROVIDER_ID = "CREATE INDEX " + TABLE_NAME + "_" + COLUMN_PROVIDER_ID + "_index" +
            " ON " + TABLE_NAME + "(" + COLUMN_PROVIDER_ID + " COLLATE NOCASE);";
    private static final String INDEX_SUBMISSION_ID = "CREATE INDEX " + TABLE_NAME + "_" + COLUMN_SUBMISSION_ID + "_index" +
            " ON " + TABLE_NAME + "(" + COLUMN_SUBMISSION_ID + " COLLATE NOCASE);";
    private static final String INDEX_INDICATOR_ID = "CREATE INDEX " + TABLE_NAME + "_" + COLUMN_INDICATOR_CODE + "_index" +
            " ON " + TABLE_NAME + "(" + COLUMN_INDICATOR_CODE + " COLLATE NOCASE);";
    private static final String INDEX_UPDATED_AT = "CREATE INDEX " + TABLE_NAME + "_" + COLUMN_UPDATED_AT + "_index" +
            " ON " + TABLE_NAME + "(" + COLUMN_UPDATED_AT + ");";
    private static final String INDEX_MONTH = "CREATE INDEX " + TABLE_NAME + "_" + COLUMN_MONTH + "_index" +
            " ON " + TABLE_NAME + "(" + COLUMN_MONTH + ");";
    private static final String INDEX_EDITED = "CREATE INDEX " + TABLE_NAME + "_" + COLUMN_EDITED + "_index" +
            " ON " + TABLE_NAME + "(" + COLUMN_EDITED + ");";
    private static final String INDEX_DATE_SENT = "CREATE INDEX " + TABLE_NAME + "_" + COLUMN_DATE_SENT + "_index" +
            " ON " + TABLE_NAME + "(" + COLUMN_DATE_SENT + ");";

    private Date maxDate = LocalDate.now().plusDays(1).toDate();
    private Date minDate = LocalDate.now().minusYears(10).toDate();

    public static void createTable(SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_QUERY);
        database.execSQL(INDEX_PROVIDER_ID);
        database.execSQL(INDEX_SUBMISSION_ID);
        database.execSQL(INDEX_INDICATOR_ID);
        database.execSQL(INDEX_UPDATED_AT);
        database.execSQL(INDEX_MONTH);
        database.execSQL(INDEX_EDITED);
        database.execSQL(INDEX_DATE_SENT);
        database.execSQL(INDEX_UNIQUE);
    }

    /**
     * Returns a list of all months that have corresponding daily tallies by unsent monthly tallies
     *
     * @param startDate The earliest date for the draft reports' month. Set argument to null if you
     *                  don't want this enforced
     * @param endDate   The latest date for the draft reports' month. Set argument to null if you
     *                  don't want this enforced
     * @return List of months with unsent monthly tallies
     */
    public List<Date> findUneditedDraftMonths(Date startDate, Date endDate) {
        List<String> allTallyMonths = CoreChwApplication.getInstance().dailyTalliesRepository()
                .findAllDistinctMonths(DF_YYYYMM, startDate, endDate);
        Cursor cursor = null;
        try {
            if (allTallyMonths != null) {
                String monthsString = "";
                for (String curMonthString : allTallyMonths) {
                    if (!TextUtils.isEmpty(monthsString)) {
                        monthsString = monthsString + ", ";
                    }

                    monthsString = monthsString + "'" + curMonthString + "'";
                }

                cursor = getReadableDatabase().query(
                        TABLE_NAME,
                        new String[]{COLUMN_MONTH}, COLUMN_MONTH + " IN(" + monthsString + ")",
                        null, null, null, null);

                if (cursor != null && cursor.getCount() > 0) {
                    for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                        String curMonth = cursor.getString(cursor.getColumnIndex(COLUMN_MONTH));
                        allTallyMonths.remove(curMonth);
                    }
                }

                List<Date> unsentMonths = new ArrayList<>();
                for (String curMonthString : allTallyMonths) {
                    Date curMonth = DF_YYYYMM.parse(curMonthString);
                    if ((startDate != null && curMonth.getTime() < startDate.getTime())
                            || (endDate != null && curMonth.getTime() > endDate.getTime())) {
                        continue;
                    }
                    unsentMonths.add(curMonth);
                }

                return unsentMonths;
            }
        } catch (SQLException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        } catch (ParseException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return new ArrayList<>();
    }

    /**
     * Returns a list of draft monthly tallies corresponding to the provided month
     *
     * @param month The month to get the draft tallies for
     * @return
     */
    public List<MonthlyTally> findDrafts(String month) {
        // Check if there exists any sent tally in the database for the month provided
        Cursor cursor = null;
        List<MonthlyTally> monthlyTallies = new ArrayList<>();
        try {
            cursor = getReadableDatabase().query(TABLE_NAME, TABLE_COLUMNS,
                    COLUMN_MONTH + " = '" + month +
                            "' AND " + COLUMN_DATE_SENT + " IS NULL",
                    null, null, null, null, null);
            monthlyTallies = readAllDataElements(cursor);

            if (monthlyTallies.size() == 0) { // No tallies generated yet
                Map<Long, List<DailyTally>> dailyTallies = CoreChwApplication.getInstance().dailyTalliesRepository().findTalliesInMonth(DF_YYYYMM.parse(month));
                for (List<DailyTally> curList : dailyTallies.values()) {
                    MonthlyTally curTally = addUpDailyTallies(curList);
                    if (curTally != null) {
                        monthlyTallies.add(curTally);
                    }
                }
            }
        } catch (SQLException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        } catch (ParseException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return monthlyTallies;
    }

    /**
     * Returns a list of all monthly tallies corresponding to the provided month
     *
     * @param month The month to get the draft tallies for
     * @return
     */
    public List<MonthlyTally> find(String month) {
        // Check if there exists any sent tally in the database for the month provided
        Cursor cursor = null;
        List<MonthlyTally> monthlyTallies = new ArrayList<>();
        try {
            cursor = getReadableDatabase().query(TABLE_NAME, TABLE_COLUMNS,
                    COLUMN_MONTH + " = '" + month + "'",
                    null, null, null, null, null);
            monthlyTallies = readAllDataElements(cursor);
        } catch (SQLException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return monthlyTallies;
    }

    private MonthlyTally addUpDailyTallies(List<DailyTally> dailyTallies) {
        String userName = CoreChwApplication.getInstance().getContext().allSharedPreferences().fetchRegisteredANM();
        MonthlyTally monthlyTally = null;
        double value = 0d;
        for (int i = 0; i < dailyTallies.size(); i++) {
            if (i == 0) {
                monthlyTally = new MonthlyTally();
                monthlyTally.setIndicator(dailyTallies.get(i).getIndicator());
            }
            try {
                value = value + Double.valueOf(dailyTallies.get(i).getValue());
            } catch (NumberFormatException e) {
                Log.w(TAG, Log.getStackTraceString(e));
            }
        }

        if (monthlyTally != null) {
            monthlyTally.setUpdatedAt(Calendar.getInstance().getTime());
            monthlyTally.setValue(String.valueOf(Math.round(value)));
            monthlyTally.setProviderId(userName);
        }

        return monthlyTally;
    }

    public HashMap<String, ArrayList<MonthlyTally>> findAllSent(SimpleDateFormat dateFormat) {
        HashMap<String, ArrayList<MonthlyTally>> tallies = new HashMap<>();
        Cursor cursor = null;
        try {
            HashMap<String, Hia2Indicator> indicatorMap = CoreChwApplication.getInstance()
                    .hIA2IndicatorsRepository().findAll();
            cursor = getReadableDatabase()
                    .query(TABLE_NAME, TABLE_COLUMNS,
                            COLUMN_DATE_SENT + " IS NOT NULL", null, null, null, null, null);
            if (cursor != null && cursor.getCount() > 0) {
                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                    MonthlyTally curTally = extractMonthlyTally(indicatorMap, cursor);
                    if (curTally != null) {
                        if (!tallies.containsKey(dateFormat.format(curTally.getMonth()))) {
                            tallies.put(dateFormat.format(curTally.getMonth()),
                                    new ArrayList<MonthlyTally>());
                        }
                        if (curTally.getMonth().getTime() > minDate.getTime() && curTally.getMonth().getTime() < maxDate.getTime())
                        tallies.get(dateFormat.format(curTally.getMonth())).add(curTally);
                    }
                }
            }
        } catch (SQLException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        } catch (ParseException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return tallies;
    }

    public boolean save(MonthlyTally tally) {
        SQLiteDatabase database = getWritableDatabase();
        try {
            database.beginTransaction();
            if (tally != null) {
                ContentValues cv = new ContentValues();
                cv.put(COLUMN_INDICATOR_CODE, tally.getIndicator().getIndicatorCode());
                cv.put(COLUMN_VALUE, tally.getValue());
                cv.put(COLUMN_PROVIDER_ID, tally.getProviderId());
                cv.put(COLUMN_SUBMISSION_ID, ReportUtils.getFormSubmissionID(null, tally));
                cv.put(COLUMN_MONTH, DF_YYYYMM.format(tally.getMonth()));
                cv.put(COLUMN_DATE_SENT, tally.getDateSent() != null ? tally.getDateSent().getTime() : null);
                cv.put(COLUMN_EDITED, tally.isEdited() ? 1 : 0);
                cv.put(COLUMN_CREATED_AT, tally.getCreatedAt().getTime());
                database.insertWithOnConflict(TABLE_NAME, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
                database.setTransactionSuccessful();

                return true;
            }
        } catch (SQLException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        } finally {
            try {
                database.endTransaction();
            } catch (IllegalStateException e) {
                Log.e(TAG, Log.getStackTraceString(e));
            }
        }

        return false;
    }

    /**
     * save data from the monthly draft form whereby in the map the key is indicator_id and value is the form value
     * assumption here is that the data is edited..probably find a way to confirm this
     *
     * @param draftFormValues
     * @param month
     * @return
     */
    public boolean save(Map<String, String> draftFormValues, Date month) {
        SQLiteDatabase database = getWritableDatabase();
        try {
            database.beginTransaction();
            if (draftFormValues != null && !draftFormValues.isEmpty() && month != null) {
                String userName = CoreChwApplication.getInstance().getContext().allSharedPreferences().fetchRegisteredANM();

                for (String key : draftFormValues.keySet()) {
                    String value = draftFormValues.get(key);
                    ContentValues cv = new ContentValues();
                    cv.put(COLUMN_INDICATOR_CODE, key);

                    if (!(value == null || value.isEmpty()))
                        cv.put(COLUMN_VALUE, Integer.valueOf(value));
                    else
                        cv.put(COLUMN_VALUE, "");

                    cv.put(COLUMN_MONTH, DF_YYYYMM.format(month));
                    cv.put(COLUMN_EDITED, 1);
                    cv.put(COLUMN_PROVIDER_ID, userName);
                    cv.put(COLUMN_CREATED_AT, Calendar.getInstance().getTimeInMillis());
                    cv.put(COLUMN_SUBMISSION_ID, userName + "-" + key + "-" + DF_YYYYMM.format(month));

                    Log.d("SAVING MONTHLy..", key + " & " + value + " & " + userName + " & " + month);

                    database.insertWithOnConflict(TABLE_NAME, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
                }
                database.setTransactionSuccessful();

                return true;
            }
        } catch (SQLException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        } finally {
            try {
                database.endTransaction();
            } catch (IllegalStateException e) {
                Log.e(TAG, Log.getStackTraceString(e));
            }
        }

        return false;
    }

    private List<MonthlyTally> readAllDataElements(Cursor cursor) {
        List<MonthlyTally> tallies = new ArrayList<>();
        HashMap<String, Hia2Indicator> indicatorMap = CoreChwApplication.getInstance()
                .hIA2IndicatorsRepository().findAll();

        try {
            if (cursor != null && cursor.getCount() > 0) {
                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                    MonthlyTally curTally = extractMonthlyTally(indicatorMap, cursor);
                    if (curTally != null) {
                        tallies.add(curTally);
                    }
                }
            }
        } catch (SQLException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        } catch (ParseException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return tallies;
    }

    private MonthlyTally extractMonthlyTally(HashMap<String, Hia2Indicator> indicatorMap, Cursor cursor) throws ParseException {
        String indicatorId = cursor.getString(cursor.getColumnIndex(COLUMN_INDICATOR_CODE));
        if (indicatorMap.containsKey(indicatorId)) {
            MonthlyTally curTally = new MonthlyTally();
            curTally.setId(cursor.getLong(cursor.getColumnIndex(COLUMN_ID)));
            curTally.setProviderId(
                    cursor.getString(cursor.getColumnIndex(COLUMN_PROVIDER_ID)));
            curTally.setIndicator(indicatorMap.get(indicatorId));
            curTally.setSubmissionId(cursor.getString(cursor.getColumnIndex(COLUMN_SUBMISSION_ID)));
            curTally.setValue(cursor.getString(cursor.getColumnIndex(COLUMN_VALUE)));
            curTally.setMonth(DF_YYYYMM.parse(cursor.getString(cursor.getColumnIndex(COLUMN_MONTH))));
            curTally.setEdited(
                    cursor.getInt(cursor.getColumnIndex(COLUMN_EDITED)) != 0
            );
            curTally.setDateSent(
                    cursor.getString(cursor.getColumnIndex(COLUMN_DATE_SENT)) == null ?
                            null : new Date(cursor.getLong(cursor.getColumnIndex(COLUMN_DATE_SENT)))
            );
            curTally.setUpdatedAt(
                    new Date(cursor.getLong(cursor.getColumnIndex(COLUMN_UPDATED_AT)))
            );
                return curTally;
        }

        return null;
    }

    /**
     * Returns a list of dates for monthly reports that have been edited, but not sent
     *
     * @param startDate The earliest date for the monthly reports. Set argument to null if you
     *                  don't want this enforced
     * @param endDate   The latest date for the monthly reports. Set argument to null if you
     *                  don't want this enforced
     * @return The list of monthly reports that have been edited, but not sent
     */
    public List<MonthlyTally> findEditedDraftMonths(Date startDate, Date endDate) {
        Cursor cursor = null;
        List<MonthlyTally> tallies = new ArrayList<>();

        try {
            cursor = getReadableDatabase().query(
                    TABLE_NAME, new String[]{COLUMN_MONTH, COLUMN_CREATED_AT, COLUMN_SUBMISSION_ID},
                    COLUMN_DATE_SENT + " IS NULL AND " + COLUMN_EDITED + " = 1",
                    null, COLUMN_MONTH, null, null);

            if (cursor != null && cursor.getCount() > 0) {
                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                    String curMonth = cursor.getString(cursor.getColumnIndex(COLUMN_MONTH));
                    Date month = DF_YYYYMM.parse(curMonth);

                    if ((startDate != null && month.getTime() < startDate.getTime())
                            || (endDate != null && month.getTime() > endDate.getTime())) {
                        continue;
                    }

                    Long dateStarted = cursor.getLong(cursor.getColumnIndex(COLUMN_CREATED_AT));
                    MonthlyTally tally = new MonthlyTally();
                    tally.setSubmissionId(cursor.getString(cursor.getColumnIndex(COLUMN_SUBMISSION_ID)));
                    tally.setMonth(month);
                    tally.setCreatedAt(new Date(dateStarted));
                    tallies.add(tally);
                }
            }
        } catch (SQLException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        } catch (ParseException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return tallies;
    }
}
