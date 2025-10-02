package org.smartregister.chw.anc.dao;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.anc.AncLibrary;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.dao.AbstractDao;
import org.smartregister.immunization.domain.ServiceRecord;
import org.smartregister.immunization.domain.Vaccine;
import org.smartregister.repository.EventClientRepository;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import timber.log.Timber;

import static org.smartregister.immunization.util.VaccinateActionUtils.removeHyphen;

public class HomeVisitDao extends AbstractDao {

    public static Event getEventByFormSubmissionId(String formSubmissionId) {
        String sql = "select json from event where formSubmissionId = '" + formSubmissionId + "'";

        DataMap<Event> dataMap = c -> {
            try {
                return AncLibrary.getInstance().getEcSyncHelper().convert(new JSONObject(getCursorValue(c, "json")), Event.class);
            } catch (JSONException e) {
                Timber.e(e);
            }
            return null;
        };
        return AbstractDao.readSingleValue(sql, dataMap);
    }

    public static List<Vaccine> fetchVaccinesSubmissionIdByProgramId(String programClientId) {
        String sql = "select * from vaccines where program_client_id = '" + programClientId + "'";

        DataMap<Vaccine> dataMap = cursor -> {
            Date createdAt = null;
            String dateCreatedString = cursor.getString(cursor.getColumnIndex("created_at"));
            if (StringUtils.isNotBlank(dateCreatedString)) {
                try {
                    createdAt = EventClientRepository.dateFormat.parse(dateCreatedString);
                } catch (ParseException e) {
                    Timber.e(e);
                }
            }

            String vaccineName = cursor.getString(cursor.getColumnIndex("name"));
            if (vaccineName != null) {
                vaccineName = removeHyphen(vaccineName);
            }

            return new Vaccine(cursor.getLong(cursor.getColumnIndex("_id")),
                    cursor.getString(cursor.getColumnIndex("base_entity_id")),
                    cursor.getString(cursor.getColumnIndex("program_client_id")),
                    vaccineName,
                    cursor.getInt(cursor.getColumnIndex("calculation")),
                    new Date(cursor.getLong(cursor.getColumnIndex("date"))),
                    cursor.getString(cursor.getColumnIndex("anmid")),
                    cursor.getString(cursor.getColumnIndex("location_id")),
                    cursor.getString(cursor.getColumnIndex("sync_status")),
                    cursor.getString(cursor.getColumnIndex("hia2_status")),
                    cursor.getLong(cursor.getColumnIndex("updated_at")),
                    cursor.getString(cursor.getColumnIndex("event_id")),
                    cursor.getString(cursor.getColumnIndex("formSubmissionId")),
                    cursor.getInt(cursor.getColumnIndex("out_of_area")),
                    createdAt,
                    cursor.getInt(cursor.getColumnIndex("is_voided"))
            );
        };

        return AbstractDao.readData(sql, dataMap);
    }

    public static List<ServiceRecord> fetchServicesSubmissionIdByProgramId(String programClientId) {
        String sql = "select * from recurring_service_records where program_client_id = '" + programClientId + "'";

        DataMap<ServiceRecord> dataMap = cursor -> {
            Date createdAt = null;
            String dateCreatedString = cursor.getString(cursor.getColumnIndex("created_at"));
            if (StringUtils.isNotBlank(dateCreatedString)) {
                try {
                    createdAt = EventClientRepository.dateFormat.parse(dateCreatedString);
                } catch (ParseException e) {
                    Timber.e(e);
                }
            }

            return new ServiceRecord(cursor.getLong(cursor.getColumnIndex("_id")),
                    cursor.getString(cursor.getColumnIndex("base_entity_id")),
                    cursor.getString(cursor.getColumnIndex("program_client_id")),
                    cursor.getLong(cursor.getColumnIndex("recurring_service_id")),
                    cursor.getString(cursor.getColumnIndex("value")),
                    new Date(cursor.getLong(cursor.getColumnIndex("date"))),
                    cursor.getString(cursor.getColumnIndex("anmid")),
                    cursor.getString(cursor.getColumnIndex("location_id")),
                    cursor.getString(cursor.getColumnIndex("sync_status")),
                    cursor.getString(cursor.getColumnIndex("event_id")),
                    cursor.getString(cursor.getColumnIndex("formSubmissionId")),
                    cursor.getLong(cursor.getColumnIndex("updated_at")),
                    createdAt);
        };

        return AbstractDao.readData(sql, dataMap);
    }
}
