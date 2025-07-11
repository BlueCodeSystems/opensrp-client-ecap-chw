package org.smartregister.chw.core.dao;

import org.smartregister.chw.core.domain.AlertState;
import org.smartregister.dao.AbstractDao;
import org.smartregister.domain.Alert;
import org.smartregister.domain.AlertStatus;
import org.smartregister.repository.AlertRepository;

import java.util.ArrayList;
import java.util.List;

public class AlertDao extends AbstractDao {

    private static DataMap<Alert> alertDataMap;

    public static List<Alert> getActiveAlerts(String baseEntityID) {
        String sql = "select (case when status = 'urgent' then 1 else 2 end) state , * from alerts " +
                " where caseID = '" + baseEntityID + "'and status in ('normal','urgent') and expiryDate > date() " +
                " order by state asc , startDate asc  , visitCode asc";
        return AbstractDao.readData(sql, getAlertDataMap());
    }

    private static DataMap<Alert> getAlertDataMap() {
        if (alertDataMap == null) {
            alertDataMap = c -> new Alert(
                    getCursorValue(c, AlertRepository.ALERTS_CASEID_COLUMN),
                    getCursorValue(c, AlertRepository.ALERTS_SCHEDULE_NAME_COLUMN),
                    getCursorValue(c, AlertRepository.ALERTS_VISIT_CODE_COLUMN),
                    AlertStatus.from(getCursorValue(c, AlertRepository.ALERTS_STATUS_COLUMN)),
                    getCursorValue(c, AlertRepository.ALERTS_STARTDATE_COLUMN),
                    getCursorValue(c, AlertRepository.ALERTS_EXPIRYDATE_COLUMN),
                    c.getInt(c.getColumnIndex(AlertRepository.ALERTS_OFFLINE_COLUMN)) == 1)
                    .withCompletionDate(
                            getCursorValue(c, AlertRepository.ALERTS_COMPLETIONDATE_COLUMN));
        }

        return alertDataMap;
    }

    public static List<Alert> getActiveAlertsForVaccines(String baseEntityID) {
        String sql = "SELECT (case when status = 'urgent' then 1 else 2 end) state , alerts.* FROM ec_child" +
                " INNER JOIN  ec_family_member on ec_family_member.base_entity_id = ec_child.mother_entity_id " +
                " INNER JOIN  alerts on alerts.caseID = ec_child.base_entity_id " +
                " WHERE ec_family_member.base_entity_id = '" + baseEntityID + "' " +
                " and scheduleName in ('BCG', 'OPV 0') " +
                " and status in ('normal','urgent') and expiryDate > date() " +
                " order by state asc , startDate asc  , visitCode asc";

        return AbstractDao.readData(sql, getAlertDataMap());
    }

    public static List<Alert> getActiveAlert(String baseEntityID) {
        String sql = "SELECT (case when status = 'urgent' then 1 else 2 end) state , alerts.* FROM alerts" +
                " WHERE alerts.caseID  = '" + baseEntityID + "' " +
                " and status in ('normal','urgent') and expiryDate > date() " +
                " order by state asc , startDate asc  , visitCode asc";

        return AbstractDao.readData(sql, getAlertDataMap());
    }

    public static void updateOfflineVaccineAlerts(String baseEntityID) {
        String sql = "select alerts.caseID , alerts.startDate , alerts.visitCode , " +
                " alerts.completionDate , vaccines.date , strftime('%Y-%m-%d', vaccines.date / 1000, 'unixepoch') dateGiven " +
                " from alerts " +
                " inner join vaccines on vaccines.base_entity_id = alerts.caseID and replace(vaccines.name,'_','') = alerts.visitCode COLLATE NOCASE " +
                " where alerts.caseID = '" + baseEntityID + "' and alerts.status not in ('complete','expired','inProcess') ";

        String pending_alerts = "select alerts.caseID , alerts.startDate  , alerts.visitCode , " +
                "alerts.completionDate , vd.details dateGiven " +
                "from alerts " +
                "inner join visits v on v.base_entity_id = alerts.caseID COLLATE NOCASE " +
                "inner join visit_details vd on vd.visit_id = v.visit_id COLLATE NOCASE and vd.visit_key = replace(lower(alerts.scheduleName),' ', '_') " +
                "and vd.details <> 'Vaccine not given' " +
                "and alerts.status not in ('complete','expired','inProcess') ";

        DataMap<AlertState> dataMap = c -> new AlertState(
                getCursorValue(c, "caseID"),
                getCursorValue(c, "startDate"),
                getCursorValue(c, "visitCode"),
                getCursorValue(c, "dateGiven")
        );

        List<AlertState> states = new ArrayList<>();

        List<AlertState> vaccinated = AbstractDao.readData(sql, dataMap);
        if (vaccinated != null && vaccinated.size() > 0)
            states.addAll(vaccinated);

        List<AlertState> pending_vaccinated = AbstractDao.readData(pending_alerts, dataMap);
        if (pending_vaccinated != null && pending_vaccinated.size() > 0)
            states.addAll(pending_vaccinated);


        if (states.size() == 0) {
            return;
        }

        for (AlertState alertState : states) {
            String alertUpdate =
                    "update alerts set status = 'complete' , completionDate = '" +
                            alertState.getDateGiven() + "' , offline = 1 where caseID = '" + baseEntityID + "' and visitCode = '" + alertState.getVisitCode() + "'";

            updateDB(alertUpdate);
        }
    }

    public static AlertStatus getFamilyAlertStatus(String baseEntityID) {
        String sql = "select max(case when over_due_date <= date('now') then 2 else 1 end) status " +
                "from schedule_service where (base_entity_id = '" + baseEntityID + "' " +
                "or base_entity_id in (select base_entity_id from ec_family_member where relational_id = '" + baseEntityID + "')) " +
                "and due_date <= date('now') and expiry_date > date('now') and completion_date is null ";

        DataMap<Integer> dataMap = c -> getCursorIntValue(c, "status");
        List<Integer> readData = readData(sql, dataMap);

        if (readData == null || readData.size() == 0 || readData.get(0) == null)
            return AlertStatus.complete;

        if (readData.get(0).equals(2))
            return AlertStatus.urgent;

        return AlertStatus.normal;
    }
}
