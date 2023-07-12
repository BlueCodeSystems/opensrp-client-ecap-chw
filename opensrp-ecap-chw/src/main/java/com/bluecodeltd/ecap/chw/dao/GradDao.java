package com.bluecodeltd.ecap.chw.dao;

import com.bluecodeltd.ecap.chw.model.GradModel;
import com.bluecodeltd.ecap.chw.model.VcaGradCorrectAnswers;

import org.smartregister.dao.AbstractDao;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class GradDao extends AbstractDao {

    public static GradModel getGrad (String vcaID) {

        String sql = "SELECT ec_grad.*, ec_client_index.adolescent_birthdate, ec_client_index.is_hiv_positive, ec_client_index.art_number, ec_client_index.facility, ec_client_index.date_last_vl, ec_client_index.vl_last_result FROM ec_grad JOIN ec_client_index ON ec_grad.unique_id = ec_client_index.unique_id WHERE ec_grad.unique_id = '" + vcaID + "'";

        List<GradModel> values = AbstractDao.readData(sql, getGradModelMap());

        if (values.size() == 0) {
            return null;
        }

        return values.get(0);
    }
    public static boolean returnTrueForBenchmark4(String householdID) {
        String sql = "SELECT infection_correct, protect_correct, prevention_correct FROM ec_grad WHERE household_id = '" + householdID + "'";
        List<GradModel> values = AbstractDao.readData(sql, getGradModelMap());

        if (values.size() == 0) {
            return false;
        }

        for (GradModel model : values) {
            if (Integer.parseInt(model.getInfection_correct()) >= 2 && Integer.parseInt(model.getProtect_correct()) >= 1 && Integer.parseInt(model.getPrevention_correct()) >= 1) {
                return true;
            }
        }

        return false;
    }
    public static boolean areAllPositiveSuppressedChildren(String householdID) {
        String sql = "SELECT DISTINCT ec_visit.unique_id, ec_visit.indicate_vl_result, ec_visit.is_hiv_positive, ec_visit.visit_date, ec_client_index.household_id\n" +
                "FROM ec_household_visitation_for_vca_0_20_years ec_visit\n" +
                "JOIN (SELECT unique_id, household_id FROM ec_client_index) ec_client_index ON ec_visit.unique_id = ec_client_index.unique_id\n" +
                "WHERE ec_client_index.household_id = '" + householdID + "' AND ec_visit.is_hiv_positive = 'yes' \n" +
                "GROUP BY ec_visit.unique_id\n" +
                "ORDER BY ec_visit.visit_date DESC";
        AbstractDao.DataMap<Boolean> dataMap = c -> {
            String indicateVlResult = getCursorValue(c, "indicate_vl_result");
            String isHivPositive = getCursorValue(c, "is_hiv_positive");
            if (indicateVlResult != null && isHivPositive != null &&
                    Integer.parseInt(indicateVlResult) < 1000 && "yes".equalsIgnoreCase(isHivPositive)) {
                return true;
            } else {
                return false;
            }
        };

        List<Boolean> values = AbstractDao.readData(sql, dataMap);

        if (values == null || values.isEmpty()) {
            return false;
        }

        for (Boolean value : values) {
            if (value == null || !value) {
                return false;
            }
        }

        return true;
    }
    public static boolean isEveryVCAKnowledgeableAboutHIVPrevention(String householdID) {
        String sql = "SELECT grad.unique_id, grad.household_id, grad.infection_correct, grad.protect_correct, grad.prevention_correct, ec_client_index.adolescent_birthdate\n" +
                " FROM ec_grad grad\n" +
                " JOIN (SELECT unique_id, adolescent_birthdate FROM ec_client_index) ec_client_index\n" +
                " ON grad.unique_id = ec_client_index.unique_id WHERE grad.household_id = '" + householdID + "'";

        AbstractDao.DataMap<VcaGradCorrectAnswers> dataMap = c -> {
            String birthdateString = getCursorValue(c, "adolescent_birthdate");
            String infection_correct = getCursorValue(c, "infection_correct");
            String protect_correct = getCursorValue(c, "protect_correct");
            String prevention_correct = getCursorValue(c, "prevention_correct");
            String birthdate = String.valueOf(LocalDate.parse(birthdateString, DateTimeFormatter.ofPattern("dd-MM-yyyy")));
            return new VcaGradCorrectAnswers(birthdate, infection_correct, protect_correct, prevention_correct);
        };

        List<VcaGradCorrectAnswers> values = AbstractDao.readData(sql, dataMap);

        if (values == null || values.isEmpty()) {
            return false;
        }

        LocalDate today = LocalDate.now();

        for (VcaGradCorrectAnswers value : values) {
            LocalDate birthdate = LocalDate.parse(value.getBirthdate());

            int infectionCorrect = Integer.parseInt(value.getInfection_correct() != null ? value.getInfection_correct() : "0");
            int protectCorrect = Integer.parseInt(value.getProtect_correct() != null ? value.getProtect_correct() : "0");
            int preventionCorrect = Integer.parseInt(value.getPrevention_correct() != null ? value.getPrevention_correct() : "0");

            if (infectionCorrect < 2 || protectCorrect < 1 || preventionCorrect < 1) {
                return false;
            }

            Period age = Period.between(birthdate, today);
            int years = age.getYears();

            if (years < 12 || years > 17) {
                return false;
            }
        }

        return true;
    }


    public static boolean hasVCAInAgeRange(String householdID) {
        String sql = "SELECT grad.unique_id, grad.household_id, grad.infection_correct, grad.protect_correct, grad.prevention_correct, ec_client_index.adolescent_birthdate\n" +
                " FROM ec_grad grad\n" +
                " JOIN (SELECT unique_id, adolescent_birthdate FROM ec_client_index) ec_client_index\n" +
                " ON grad.unique_id = ec_client_index.unique_id WHERE grad.household_id = '" + householdID + "'";

        AbstractDao.DataMap<VcaGradCorrectAnswers> dataMap = c -> {
            String birthdateString = getCursorValue(c, "adolescent_birthdate");
            String infection_correct = getCursorValue(c, "infection_correct");
            String protect_correct = getCursorValue(c, "protect_correct");
            String prevention_correct = getCursorValue(c, "prevention_correct");
            String birthdate = String.valueOf(LocalDate.parse(birthdateString, DateTimeFormatter.ofPattern("dd-MM-yyyy")));
            return new VcaGradCorrectAnswers(birthdate, infection_correct, protect_correct, prevention_correct);
        };

        List<VcaGradCorrectAnswers> values = AbstractDao.readData(sql, dataMap);

        if (values == null || values.isEmpty()) {
            return false;
        }

        LocalDate today = LocalDate.now();
        boolean hasChildInRange = false;

        for (VcaGradCorrectAnswers value : values) {
            LocalDate birthdate = LocalDate.parse(value.getBirthdate());
            Period age = Period.between(birthdate, today);
            int years = age.getYears();

            if (years >= 12 && years <= 17) {
                hasChildInRange = true;
                break;
            }
        }

        return hasChildInRange;
    }


    public static String bench3Answers(String householdID){

        String sql = "SELECT COUNT(*) AS childrenCount FROM ec_grad WHERE household_id = '" + householdID + "' AND CAST(correct as integer) = 1";

        AbstractDao.DataMap<String> dataMap = c -> getCursorValue(c, "childrenCount");

        List<String> values = AbstractDao.readData(sql, dataMap);

        if (values == null)
            return null;

        return values.get(0);

    }


    public static DataMap<GradModel> getGradModelMap() {
        return c -> {

            GradModel record = new GradModel();
            record.setBase_entity_id(getCursorValue(c, "base_entity_id"));
            record.setUnique_id(getCursorValue(c, "unique_id"));
            record.setHousehold_id(getCursorValue(c, "household_id"));
            record.setAdolescent_birthdate(getCursorValue(c, "adolescent_birthdate"));
            record.setIs_hiv_positive(getCursorValue(c, "is_hiv_positive"));
            record.setArt_number(getCursorValue(c, "art_number"));
            record.setFacility(getCursorValue(c, "facility"));
            record.setDate_last_vl(getCursorValue(c, "date_last_vl"));
            record.setVl_last_result(getCursorValue(c, "vl_last_result"));
            record.setInfected_community(getCursorValue(c, "infected_community"));
            record.setInfection_correct(getCursorValue(c, "infection_correct"));
            record.setProtect_infection(getCursorValue(c, "protect_infection"));
            record.setPrevention_support(getCursorValue(c, "prevention_support"));
            record.setPrevention_correct(getCursorValue(c, "prevention_correct"));
            record.setSign_malnutrition(getCursorValue(c, "sign_malnutrition"));
            record.setProtect_correct(getCursorValue(c, "protect_correct"));

            return record;
        };
    }

}
