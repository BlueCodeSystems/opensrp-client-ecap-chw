package com.bluecodeltd.ecap.chw.dao;

import com.bluecodeltd.ecap.chw.model.GradModel;

import org.smartregister.dao.AbstractDao;

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
            return false;
        }

        if (householdID.contains("'") || householdID.contains(";") || householdID.contains("--")) {
            return false;
        }

        String ageFilter =
                "date(substr(adolescent_birthdate,7,4)||'-'||substr(adolescent_birthdate,4,2)||'-'||substr(adolescent_birthdate,1,2)) <= date('now','-10 years') " +
                "AND date(substr(adolescent_birthdate,7,4)||'-'||substr(adolescent_birthdate,4,2)||'-'||substr(adolescent_birthdate,1,2)) > date('now','-18 years')";

        // eligible_count: non-deleted children aged 10-17 in this household
        // qualified_count: those children whose LATEST visit (by date) has all 3 fields = yes
        String sql = "SELECT " +
                "(SELECT COUNT(*) FROM ec_client_index " +
                " WHERE household_id = '" + householdID + "' " +
                " AND (deleted IS NULL OR deleted <> '1') " +
                " AND " + ageFilter + ") AS eligible_count, " +

                "(SELECT COUNT(DISTINCT v.unique_id) " +
                " FROM ec_household_visitation_for_vca_0_20_years v " +
                " JOIN ec_client_index idx ON idx.unique_id = v.unique_id " +
                " WHERE idx.household_id = '" + householdID + "' " +
                " AND (idx.deleted IS NULL OR idx.deleted <> '1') " +
                " AND date(substr(idx.adolescent_birthdate,7,4)||'-'||substr(idx.adolescent_birthdate,4,2)||'-'||substr(idx.adolescent_birthdate,1,2)) <= date('now','-10 years') " +
                " AND date(substr(idx.adolescent_birthdate,7,4)||'-'||substr(idx.adolescent_birthdate,4,2)||'-'||substr(idx.adolescent_birthdate,1,2)) > date('now','-18 years') " +
                " AND LOWER(TRIM(COALESCE(v.hiv_infection,''))) = 'yes' " +
                " AND LOWER(TRIM(COALESCE(v.prevention_support,''))) = 'yes' " +
                " AND LOWER(TRIM(COALESCE(v.against_hiv_risk,''))) = 'yes' " +
                " AND strftime('%Y-%m-%d',substr(v.visit_date,7,4)||'-'||substr(v.visit_date,4,2)||'-'||substr(v.visit_date,1,2)) = (" +
                "   SELECT MAX(strftime('%Y-%m-%d',substr(v2.visit_date,7,4)||'-'||substr(v2.visit_date,4,2)||'-'||substr(v2.visit_date,1,2))) " +
                "   FROM ec_household_visitation_for_vca_0_20_years v2 " +
                "   WHERE v2.unique_id = idx.unique_id)" +
                ") AS qualified_count";

        AbstractDao.DataMap<Boolean> dataMap = c1 -> {
            int eligibleCount = getCursorIntValue(c1, "eligible_count");
            int qualifiedCount = getCursorIntValue(c1, "qualified_count");
            return eligibleCount > 0 && eligibleCount == qualifiedCount;
        };

        List<Boolean> values = AbstractDao.readData(sql, dataMap);

        return values != null && !values.isEmpty() && Boolean.TRUE.equals(values.get(0));
    }


    public static boolean hasVCAInAgeRange(String householdID) {
        String sql = "SELECT COUNT(*) AS in_range_count " +
                "FROM ec_client_index " +
                "WHERE (strftime('%Y', 'now') - substr(adolescent_birthdate, 7, 4)) >= 10 " +
                "AND (strftime('%Y', 'now') - substr(adolescent_birthdate, 7, 4)) <= 17 " +
                "AND household_id = '" + householdID + "'";
        DataMap<Integer> dataMap = c -> getCursorIntValue(c, "in_range_count");
        List<Integer> values = AbstractDao.readData(sql, dataMap);
        return values != null && !values.isEmpty() && values.get(0) != null && values.get(0) > 0;
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

            DaoModelFieldMapper.captureAdditionalFields(c, record);
            return record;
        };
    }

}


