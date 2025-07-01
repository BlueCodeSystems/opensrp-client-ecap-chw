package org.smartregister.chw.core.dao;

import org.smartregister.dao.AbstractDao;

public class VaccinesDao extends AbstractDao{

    public static String getVaccineName(String formSubmissionID) {
        try {
            String sql = "SELECT name vaccineName FROM vaccines WHERE formSubmissionId='" + formSubmissionID + "';";

            AbstractDao.DataMap<String> dataMap = cursor -> getCursorValue(cursor, "vaccineName");

            return readSingleValue(sql, dataMap);
        } catch (Exception e) {
            return "";
        }
    }

    public static String getVaccineDate(String formSubmissionID) {
        try {
            String sql = "SELECT date vaccineDate FROM vaccines WHERE formSubmissionId='" + formSubmissionID + "';";

            AbstractDao.DataMap<String> dataMap = cursor -> getCursorValue(cursor, "vaccineDate");

            return readSingleValue(sql, dataMap);
        } catch (Exception e) {
            return "";
        }
    }
}
