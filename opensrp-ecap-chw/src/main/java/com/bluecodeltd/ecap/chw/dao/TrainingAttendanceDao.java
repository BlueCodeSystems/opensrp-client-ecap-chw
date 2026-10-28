package com.bluecodeltd.ecap.chw.dao;

import com.bluecodeltd.ecap.chw.model.TrainingAttendanceRecord;
import com.bluecodeltd.ecap.chw.util.TrainingAttendance;

import org.smartregister.dao.AbstractDao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import timber.log.Timber;

public class TrainingAttendanceDao extends AbstractDao {

    /** Every logged training that hasn't been deleted, most recent start date first. */
    public static List<TrainingAttendanceRecord> getTrainings() {
        String sql = "SELECT * FROM " + TrainingAttendance.TABLE
                + " WHERE (delete_status IS NULL OR delete_status != '1')";
        AbstractDao.DataMap<TrainingAttendanceRecord> dataMap = c -> {
            TrainingAttendanceRecord record = new TrainingAttendanceRecord();
            record.baseEntityId = getCursorValue(c, "base_entity_id");
            record.trainingTitle = getCursorValue(c, "training_title");
            record.trainingTopic = getCursorValue(c, "training_topic");
            record.startDate = getCursorValue(c, "start_date");
            record.endDate = getCursorValue(c, "end_date");
            record.trainingMode = getCursorValue(c, "training_mode");
            record.venue = getCursorValue(c, "venue");
            record.facilitator = getCursorValue(c, "facilitator");
            record.attendedFull = getCursorValue(c, "attended_full");
            record.certificateReceived = getCursorValue(c, "certificate_received");
            record.comment = getCursorValue(c, "comment");
            record.caseworkerName = getCursorValue(c, "caseworker_name");
            return record;
        };
        List<TrainingAttendanceRecord> records = new ArrayList<>();
        try {
            List<TrainingAttendanceRecord> rows = AbstractDao.readData(sql, dataMap);
            if (rows != null) records.addAll(rows);
        } catch (Exception e) {
            Timber.e(e, "Unable to read %s", TrainingAttendance.TABLE);
        }
        // Dates are stored as dd-MM-yyyy text, so sort after parsing rather than in SQL
        Collections.sort(records, (a, b) -> Long.compare(timeOf(b.startDate), timeOf(a.startDate)));
        return records;
    }

    private static long timeOf(String date) {
        if (date == null || date.trim().isEmpty()) return Long.MIN_VALUE;
        try {
            Date parsed = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).parse(date.trim());
            return parsed != null ? parsed.getTime() : Long.MIN_VALUE;
        } catch (ParseException e) {
            return Long.MIN_VALUE;
        }
    }
}
