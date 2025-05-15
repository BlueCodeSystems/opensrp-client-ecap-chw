package com.bluecodeltd.ecap.chw.dao;

import com.bluecodeltd.ecap.chw.model.GradModel;
import com.bluecodeltd.ecap.chw.model.VcaGradCorrectAnswers;

import org.smartregister.dao.AbstractDao;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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
                "JOIN (SELECT unique_id, household_id,deleted FROM ec_client_index) ec_client_index ON ec_visit.unique_id = ec_client_index.unique_id\n" +
                "WHERE ec_client_index.household_id = '" + householdID + "' AND ec_visit.is_hiv_positive = 'yes' AND (ec_client_index.deleted IS NULL OR ec_client_index.deleted <> 1) \n" +
                "GROUP BY ec_visit.unique_id\n" +
                "ORDER BY ec_visit.visit_date DESC";
        AbstractDao.DataMap<Boolean> dataMap = c -> {
            String indicateVlResult = getCursorValue(c, "indicate_vl_result");
            String isHivPositive = getCursorValue(c, "is_hiv_positive");
            if (indicateVlResult != null && isHivPositive != null &&
                    Integer.parseInt(indicateVlResult) <= 1000 && "yes".equalsIgnoreCase(isHivPositive)) {
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
    public static boolean doTheVCAsMeetBenchMarkThree(String householdID) {
        if (householdID == null || householdID.trim().isEmpty()) {
            return false; // Handle invalid householdID
        }

        // Basic sanitization to prevent SQL injection
        if (householdID.contains("'") || householdID.contains(";") || householdID.contains("--")) {
            return false; // Reject potentially malicious input
        }

        // Query all VCAs aged 10–17, including those without grad records
        String sql = "SELECT ec_client_index.unique_id, ec_client_index.household_id, ec_client_index.adolescent_birthdate, " +
                "grad.infection_correct, grad.protect_correct, grad.prevention_correct " +
                "FROM ec_client_index " +
                "LEFT JOIN ec_grad grad ON ec_client_index.unique_id = grad.unique_id " +
                "WHERE (ec_client_index.deleted IS NULL OR ec_client_index.deleted <> 1) " +
                "AND ec_client_index.household_id = '" + householdID + "' " +
                "AND strftime('%Y', 'now') - strftime('%Y', substr(ec_client_index.adolescent_birthdate, 7, 4) || '-' || substr(ec_client_index.adolescent_birthdate, 4, 2) || '-' || substr(ec_client_index.adolescent_birthdate, 1, 2)) BETWEEN 10 AND 17";

        AbstractDao.DataMap<VcaGradCorrectAnswers> dataMap = c -> {
            String birthdateString = getCursorValue(c, "adolescent_birthdate");
            String infection_correct = getCursorValue(c, "infection_correct");
            String protect_correct = getCursorValue(c, "protect_correct");
            String prevention_correct = getCursorValue(c, "prevention_correct");

            try {
                LocalDate birthdate = LocalDate.parse(birthdateString, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
                return new VcaGradCorrectAnswers(birthdate.toString(), infection_correct, protect_correct, prevention_correct);
            } catch (DateTimeParseException e) {
                return null; // Skip invalid birthdate records
            }
        };

        List<VcaGradCorrectAnswers> values = AbstractDao.readData(sql, dataMap);

        if (values == null || values.isEmpty()) {
            return false; // No VCAs aged 10–17 in household
        }

        LocalDate today = LocalDate.now();

        for (VcaGradCorrectAnswers value : values) {
            if (value == null) {
                return false;
            }

            try {
                LocalDate birthdate = LocalDate.parse(value.getBirthdate());
                int age = Period.between(birthdate, today).getYears();

                if (age < 10 || age > 17) {
                    return false;
                }


                if (value.getInfection_correct() == null || value.getProtect_correct() == null || value.getPrevention_correct() == null) {
                    return false;
                }


                int infectionCorrect = parseAnswer(value.getInfection_correct());
                int protectCorrect = parseAnswer(value.getProtect_correct());
                int preventionCorrect = parseAnswer(value.getPrevention_correct());


                if (infectionCorrect < 2 || protectCorrect < 1 || preventionCorrect < 1) {
                    return false;
                }
            } catch (DateTimeParseException e) {
                return false;
            }
        }

        return true;
    }


    private static int parseAnswer(String answer) {
        try {
            return answer != null ? Integer.parseInt(answer) : 0;
        } catch (NumberFormatException e) {
            return 0;
        }
    }


    public static boolean hasVCAInAgeRange(String householdID) {

        String sql = "SELECT unique_id, adolescent_birthdate, household_id " +
                "FROM ec_client_index " +
                "WHERE (strftime('%Y', 'now') - substr(adolescent_birthdate, 7, 4)) >= 10 " +
                "AND (strftime('%Y', 'now') - substr(adolescent_birthdate, 7, 4)) <= 17 " +
                "AND household_id = '" + householdID + "'";

        List<String> ids = AbstractDao.readData(sql, c -> getCursorValue(c, "unique_id"));

        return ids != null && !ids.isEmpty();
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
