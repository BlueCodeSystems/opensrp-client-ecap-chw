package org.smartregister.chw.core.dao;

import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.core.application.CoreChwApplication;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.dao.AbstractDao;
import org.smartregister.sync.helper.ECSyncHelper;

import java.util.List;

import timber.log.Timber;

public class EventDao extends AbstractDao {

    public static List<Event> getEvents(String baseEntityID, String eventType, int limit) {
        String sql = "select json from event where baseEntityId = '" + baseEntityID + "' COLLATE NOCASE and eventType = '" + eventType + "' COLLATE NOCASE order by updatedAt desc limit " + limit;

        DataMap<Event> dataMap = c -> {
            try {
                return getEcSyncHelper().convert(new JSONObject(getCursorValue(c, "json")), Event.class);
            } catch (JSONException e) {
                Timber.e(e);
            }
            return null;
        };
        return AbstractDao.readData(sql, dataMap);
    }

    @Nullable
    public static Event getLatestEvent(String baseEntityID, List<String> eventTypes) {
        StringBuilder types = new StringBuilder();
        for (String eventType : eventTypes) {
            if (types.length() > 0)
                types.append(" , ");

            types.append("'").append(eventType).append("'");
        }

        String sql = "select json from event where baseEntityId = '" + baseEntityID + "' COLLATE NOCASE and eventType in (" + types.toString() + ") COLLATE NOCASE order by updatedAt desc limit 1";

        DataMap<Event> dataMap = c -> {
            try {
                return getEcSyncHelper().convert(new JSONObject(getCursorValue(c, "json")), Event.class);
            } catch (JSONException e) {
                Timber.e(e);
            }
            return null;
        };

        List<Event> res = AbstractDao.readData(sql, dataMap);
        if (res != null && res.size() > 0)
            return res.get(0);

        return null;
    }

    @Nullable
    public static String getLatestJson(String baseEntityID, String... eventTypes) {
        StringBuilder types = new StringBuilder();
        for (String eventType : eventTypes) {
            if (types.length() > 0)
                types.append(" , ");

            types.append("'").append(eventType).append("'");
        }

        String sql = "select json from event where baseEntityId = '" + baseEntityID + "' COLLATE NOCASE and eventType in (" + types.toString() + ") COLLATE NOCASE order by updatedAt desc limit 1";

        DataMap<String> dataMap = cursor -> getCursorValue(cursor, "json");

        return AbstractDao.readSingleValue(sql, dataMap);
    }

    private static ECSyncHelper getEcSyncHelper() {
        return CoreChwApplication.getInstance().getEcSyncHelper();
    }

    public static void deleteVaccineByFormSubmissionId(String submissionId) {
        String sql = "delete from vaccines where formSubmissionId = '" + submissionId + "'";
        updateDB(sql);
    }

    public static void deleteServiceByFormSubmissionId(String submissionId) {
        String sql = "delete from recurring_service_records where formSubmissionId = '" + submissionId + "'";
        updateDB(sql);
    }

    public static void deleteVisitByFormSubmissionId(String submissionId) {
        String sql1 = "delete from visit_details where visit_id in (select visit_id from visits where form_submission_id = '" + submissionId + "')";
        updateDB(sql1);

        String sql2 = "delete from visits where form_submission_id = '" + submissionId + "'";
        updateDB(sql2);
    }

    public static boolean isVoidedEvent(String formSubmissionID) {
        try {
            String sql = "select count(*) count from event" +
                    " where formSubmissionId = '" + formSubmissionID + "'" +
                    " and eventType = 'Void Event'";

            AbstractDao.DataMap<Integer> dataMap = cursor -> getCursorIntValue(cursor, "count");

            List<Integer> res = readData(sql, dataMap);
            return res != null && res.size() != 0 && res.get(0) != 0;
        } catch (Exception e) {
            return false;
        }
    }
}
