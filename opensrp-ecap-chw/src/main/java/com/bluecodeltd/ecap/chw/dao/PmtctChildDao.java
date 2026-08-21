package com.bluecodeltd.ecap.chw.dao;

import com.bluecodeltd.ecap.chw.model.PmtctChildModel;

import org.smartregister.dao.AbstractDao;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class PmtctChildDao extends AbstractDao {
    public static List<PmtctChildModel> getPmctChildHeiByHouseholdId(String householdId) {
        if (householdId == null || householdId.trim().isEmpty()) {
            return new ArrayList<>();
        }
        String escaped = escapeSql(householdId.trim());
        String sql = "SELECT * FROM ec_pmtct_child WHERE household_id = '" + escaped + "' AND " + activeRecordClause();

        List<PmtctChildModel> values = AbstractDao.readData(sql, getPmtctChildModelMap());
        if (values == null || values.isEmpty()) {
            return new ArrayList<>();
        }
        return values;
    }

    public static String countMotherHeiByHouseholdId(String householdId) {
        if (householdId == null || householdId.trim().isEmpty()) {
            return "0";
        }
        String escaped = escapeSql(householdId.trim());
        String sql = "SELECT COUNT(*) v FROM ec_pmtct_child WHERE household_id = '" + escaped + "' AND (delete_status IS NULL OR delete_status <> '1')";
        DataMap<String> dataMap = c -> getCursorValue(c, "v");
        List<String> values = AbstractDao.readData(sql, dataMap);
        if (values == null || values.isEmpty()) {
            return "0";
        }
        return values.get(0);
    }

    public static List<PmtctChildModel> getPmctChildHei(String pmtctID) {
        return getPmctChildHei(pmtctID, null);
    }

    public static List<PmtctChildModel> getPmctChildHei(String primaryId, String secondaryId) {
        String idClause = buildPmtctOrHouseholdIdClause(primaryId, secondaryId);
        if (idClause == null) {
            return new ArrayList<>();
        }
        String sql = "SELECT * FROM ec_pmtct_child WHERE " + idClause + " AND " + activeRecordClause();

        List<PmtctChildModel> values = AbstractDao.readData(sql, getPmtctChildModelMap());
        if (values == null || values.size() == 0)
            return new ArrayList<>();

        return values;

    }
    public static PmtctChildModel getPMCTChild(String pmtctID) {

        String sql = "SELECT * FROM ec_pmtct_child WHERE unique_id = '" + pmtctID + "' AND " + activeRecordClause();

        List<PmtctChildModel> values = AbstractDao.readData(sql, getPmtctChildModelMap());

        if (values.size() == 0) {
            return null;
        }


        return values.get(0);
    }
    public static String countMotherHei (String pmtctID){
        return countMotherHei(pmtctID, null);
    }

    public static String countMotherHei (String primaryId, String secondaryId){
        String idClause = buildPmtctOrHouseholdIdClause(primaryId, secondaryId);
        if (idClause == null) {
            return "0";
        }
        String sql = "SELECT COUNT(*) v FROM ec_pmtct_child WHERE " + idClause + " AND (delete_status IS NULL OR delete_status <> '1')";
        DataMap<String> dataMap = c -> getCursorValue(c, "v");

        List<String> values = AbstractDao.readData(sql, dataMap);

        if (values == null || values.size() == 0)
            return "0";

        return values.get(0);

    }

    public static Boolean hasDeletedHei(String pmtctID) {
        return hasDeletedHei(pmtctID, null);
    }

    public static Boolean hasDeletedHei(String primaryId, String secondaryId) {
        try {
            String idClause = buildPmtctOrHouseholdIdClause(primaryId, secondaryId);
            if (idClause == null) {
                return false;
            }
            String sql = "SELECT COUNT(*) v FROM ec_pmtct_child WHERE " + idClause + " AND delete_status = '1'";
            DataMap<String> dataMap = c -> getCursorValue(c, "v");
            List<String> values = AbstractDao.readData(sql, dataMap);
            if (values == null || values.size() == 0) {
                return false;
            }
            return Integer.parseInt(values.get(0)) > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public static DataMap<PmtctChildModel> getPmtctChildModelMap() {
        return c -> {

            PmtctChildModel record = new PmtctChildModel();
            record.setBase_entity_id(getCursorValue(c, "base_entity_id"));
            record.setPmtct_id(getCursorValue(c, "pmtct_id"));
            record.setHousehold_id(getCursorValue(c, "household_id"));
            record.setUnique_id(getCursorValue(c, "unique_id"));
            record.setInfant_first_name(getCursorValue(c, "infant_first_name"));
            record.setInfant_middle_name(getCursorValue(c, "infant_middle_name"));
            record.setInfant_lastname(getCursorValue(c, "infant_lastname"));
            record.setInfants_date_of_birth(getCursorValue(c, "infants_date_of_birth"));
            record.setInfants_sex(getCursorValue(c, "infants_sex"));
            record.setWeight_at_birth(getCursorValue(c, "weight_at_birth"));
            record.setInfant_feeding_options(getCursorValue(c, "infant_feeding_options"));
            record.setUnder_five_clinic_card(getCursorValue(c, "under_five_clinic_card"));
            record.setDbs_at_birth_due_date(getCursorValue(c, "dbs_at_birth_due_date"));
            record.setDbs_at_birth_actual_date(getCursorValue(c, "dbs_at_birth_actual_date"));
            record.setTest_result_at_birth(getCursorValue(c, "test_result_at_birth"));
            record.setDate_tested(getCursorValue(c, "date_tested"));
            record.setAzt_3tc_npv(getCursorValue(c, "azt_3tc_npv"));
            record.setAzt_3tc_npv_date(getCursorValue(c, "azt_3tc_npv_date"));
            record.setChild_outcome(getCursorValue(c, "child_outcome"));
            record.setWard(getCursorValue(c, "ward"));
            record.setIs_closed(getCursorValue(c, "is_closed"));
            record.setProvince(getCursorValue(c, "province"));
            record.setDistrict(getCursorValue(c, "district"));
            record.setFacility(getCursorValue(c, "facility"));
            record.setPartner(getCursorValue(c, "partner"));

            return record;
        };
    }

    private static String buildPmtctOrHouseholdIdClause(String primaryId, String secondaryId) {
        Set<String> identifiers = new LinkedHashSet<>();
        addIdentifier(identifiers, primaryId);
        addIdentifier(identifiers, secondaryId);
        if (identifiers.isEmpty()) {
            return null;
        }
        StringBuilder where = new StringBuilder();
        boolean first = true;
        for (String identifier : identifiers) {
            if (!first) {
                where.append(" OR ");
            }
            where.append("(pmtct_id = '")
                    .append(escapeSql(identifier))
                    .append("' OR household_id = '")
                    .append(escapeSql(identifier))
                    .append("')");
            first = false;
        }
        return "(" + where + ")";
    }

    private static void addIdentifier(Set<String> identifiers, String value) {
        if (value == null) {
            return;
        }
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            return;
        }
        identifiers.add(trimmed);
    }

    private static String escapeSql(String value) {
        return value == null ? "" : value.replace("'", "''");
    }

    private static String activeRecordClause() {
        // ec_pmtct_child is consistently mapped with delete_status in ec_client_fields.json.
        // Avoid referencing deleted_status because some deployments/tables don't have that column.
        return "(delete_status IS NULL OR delete_status <> '1')";
    }

    public static void deleteHeiByPmtctOrHouseholdId(String id) {
        deleteHeiByPmtctOrHouseholdId(id, null);
    }

    public static void deleteHeiByPmtctOrHouseholdId(String primaryId, String secondaryId) {
        String idClause = buildPmtctOrHouseholdIdClause(primaryId, secondaryId);
        if (idClause == null) {
            return;
        }
        // Prefer delete_status; some schemas may also have deleted_status.
        try {
            String sql = "UPDATE ec_pmtct_child SET delete_status = '1', deleted_status = '1' WHERE " + idClause;
            updateDB(sql);
        } catch (Exception ignored) {
            String sql = "UPDATE ec_pmtct_child SET delete_status = '1' WHERE " + idClause;
            updateDB(sql);
        }
    }
}
