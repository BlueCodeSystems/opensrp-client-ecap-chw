package org.smartregister.chw.core.repository;

import android.content.ContentValues;
import android.database.Cursor;

import net.sqlcipher.database.SQLiteDatabase;

import org.smartregister.chw.core.domain.StockUsage;
import org.smartregister.repository.BaseRepository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import timber.log.Timber;

public class StockUsageReportRepository extends BaseRepository {
    public static final String TABLE_NAME = "stock_usage_report";
    public static final String ID = "id";
    public static final String STOCK_NAME = "stock_name";
    public static final String STOCK_USAGE = "stock_usage";
    public static final String YEAR = "year";
    public static final String MONTH = "month";
    private static final String PROVIDER_ID = "provider_id";
    private static final String UPDATED_AT = "updated_at";
    private static final String CREATED_AT = "created_at";
    public static final String CREATE_TABLE_SQL = "CREATE TABLE " + TABLE_NAME + "(" +
            ID + "  VARCHAR NOT NULL PRIMARY KEY, " +
            STOCK_NAME + "  VARCHAR, " +
            STOCK_USAGE + "  VARCHAR, " +
            YEAR + "  VARCHAR, " +
            MONTH + " VARCHAR, " +
            PROVIDER_ID  + " VARCHAR NOT NULL," +
            UPDATED_AT + " VARCHAR, " +
            CREATED_AT + " VARCHAR " +
            ")";
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    public static String[] COLUMNS = {STOCK_NAME, STOCK_USAGE, YEAR, MONTH, PROVIDER_ID, UPDATED_AT, CREATED_AT};
    public static final String BASE_ID_INDEX = "CREATE UNIQUE INDEX " + TABLE_NAME + "_" + ID + "_index ON " + TABLE_NAME + "(" + ID + " COLLATE NOCASE " + ")";



    public static void createTable(SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_SQL);
        database.execSQL(BASE_ID_INDEX);
    }

    public void addOrUpdateStockUsage(StockUsage stockUsage) {
        addOrUpdateStockUsage(stockUsage, getWritableDatabase());
    }

    public void addOrUpdateStockUsage(StockUsage stockUsage, SQLiteDatabase database) {
        if (stockUsage == null) {
            return;
        }
        database.replace(TABLE_NAME, null, createValues(stockUsage));

    }

    private ContentValues createValues(StockUsage stockUsage) {
        ContentValues values = new ContentValues();
        values.put(ID, stockUsage.getId());
        values.put(STOCK_NAME, stockUsage.getStockName());
        values.put(STOCK_USAGE, stockUsage.getStockUsage());
        values.put(YEAR, stockUsage.getYear());
        values.put(MONTH, stockUsage.getMonth());
        values.put(PROVIDER_ID, stockUsage.getProviderId());
        values.put(UPDATED_AT, getDateForDB(new Date()));
        values.put(CREATED_AT, getDateForDB(new Date()));
        return values;
    }

    private String getDateForDB(Date date) {
        if (date == null) return null;
        return sdf.format(date);
    }

    public List<StockUsage> getStockUsageByName(String stockName) {
        List<StockUsage> stockUsages = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = getReadableDatabase().query(TABLE_NAME, COLUMNS, STOCK_NAME + " = ? ", new String[]{stockName}, null, null, CREATED_AT + " ASC ", null);
            stockUsages = readStockUsage(cursor);
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return stockUsages;
    }

    public List<StockUsage> getStockUsageByBaseID(String stockId) {
        List<StockUsage> stockUsages = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = getReadableDatabase().query(TABLE_NAME, COLUMNS, ID + " = ? ", new String[]{stockId}, null, null, CREATED_AT + " ASC ", null);
            stockUsages = readStockUsage(cursor);
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return stockUsages;
    }

    private List<StockUsage> readStockUsage(Cursor cursor) {
        List<StockUsage> stockUsages = new ArrayList<>();
        try {
            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {

                    StockUsage stockUsage = new StockUsage();
                    stockUsage.setStockName(cursor.getString(cursor.getColumnIndex(STOCK_NAME)));
                    stockUsage.setStockUsage(cursor.getString(cursor.getColumnIndex(STOCK_USAGE)));
                    stockUsage.setYear(cursor.getString(cursor.getColumnIndex(YEAR)));
                    stockUsage.setMonth(cursor.getString(cursor.getColumnIndex(MONTH)));
                    stockUsage.setProviderId(cursor.getString(cursor.getColumnIndex(PROVIDER_ID)));
                    stockUsage.setCreatedAt(getCursorDate(cursor, CREATED_AT));
                    stockUsage.setUpdatedAt(getCursorDate(cursor, UPDATED_AT));

                    stockUsages.add(stockUsage);
                    cursor.moveToNext();
                }
            }
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return stockUsages;
    }

    private Date getCursorDate(Cursor c, String column_name) {
        String val = c.getType(c.getColumnIndex(column_name)) == Cursor.FIELD_TYPE_NULL ? null : c.getString(c.getColumnIndex(column_name));
        if (val == null)
            return null;

        try {
            return sdf.parse(val);
        } catch (ParseException e) {
            Timber.e(e);
            return null;
        }
    }

    public void deleteUsageReport(String stockName, String year, String month) {
        try {
            getWritableDatabase().delete(TABLE_NAME, STOCK_NAME + "= ? and " + YEAR + "= ? and " + MONTH + " = ?", new String[]{stockName, year, month});
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    public void deleteAll(){
        try {
            getWritableDatabase().execSQL("DELETE * from stock_usage_report)");
        } catch (Exception e) {
            Timber.e(e);
        }
    }
}
