package com.bluecodeltd.ecap.chw.dao;

import com.bluecodeltd.ecap.chw.model.IndexMotherModel;

import org.smartregister.dao.AbstractDao;

import java.util.ArrayList;
import java.util.List;

public class IndexMotherDao extends AbstractDao {

    public static IndexMotherModel getIndexMotherByHouseholdId(String householdId) {
        String sql = "SELECT * FROM ec_mother_index WHERE household_id = '" + householdId + "'";
        List<IndexMotherModel> values = AbstractDao.readData(sql, getIndexMotherModelMap());
        if (values == null || values.size() == 0) return null;
        return values.get(0);
    }

    public static boolean hasIndexMother(String householdId) {
        if (householdId == null || householdId.trim().isEmpty()) {
            return false;
        }
        String sql = "SELECT COUNT(*) v FROM ec_mother_index WHERE household_id = '" + householdId + "' " +
                "AND (deleted IS NULL OR deleted <> '1')";
        AbstractDao.DataMap<String> dataMap = c -> getCursorValue(c, "v");
        List<String> values = AbstractDao.readData(sql, dataMap);
        if (values == null || values.isEmpty()) return false;
        return !"0".equals(values.get(0));
    }

    public static List<IndexMotherModel> getIndexMothersByHouseholdId(String householdId) {
        String sql = "SELECT * FROM ec_mother_index WHERE household_id = '" + householdId + "' AND (deleted IS NULL OR deleted <> '1')";
        List<IndexMotherModel> values = AbstractDao.readData(sql, getIndexMotherModelMap());
        if (values == null || values.size() == 0) return new ArrayList<>();
        return values;
    }

    public static DataMap<IndexMotherModel> getIndexMotherModelMap() {
        return c -> {
            IndexMotherModel record = new IndexMotherModel();
            record.setDeleted(getCursorValue(c, "deleted"));
            record.setLast_interacted_with(getCursorValue(c, "last_interacted_with"));
            record.setBase_entity_id(getCursorValue(c, "base_entity_id"));
            record.setHousehold_id(getCursorValue(c, "household_id"));
            record.setUser_select_hiv(getCursorValue(c, "user_select_hiv"));
            record.setPartner(getCursorValue(c, "partner"));
            record.setHomeaddress(getCursorValue(c, "homeaddress"));
            record.setProvince(getCursorValue(c, "province"));
            record.setDistrict(getCursorValue(c, "district"));
            record.setWard(getCursorValue(c, "ward"));
            record.setLandmark(getCursorValue(c, "landmark"));
            record.setFacility(getCursorValue(c, "facility"));
            record.setMother_screening_date(getCursorValue(c, "mother_screening_date"));
            record.setScreening_location_home(getCursorValue(c, "screening_location_home"));
            record.setCaregiver_name(getCursorValue(c, "caregiver_name"));
            record.setCaregiver_sex(getCursorValue(c, "caregiver_sex"));
            record.setCaregiver_birth_date(getCursorValue(c, "caregiver_birth_date"));
            record.setCaregiver_hiv_status(getCursorValue(c, "caregiver_hiv_status"));
            record.setActive_on_treatment(getCursorValue(c, "active_on_treatment"));
            record.setCaregiver_art_number(getCursorValue(c, "caregiver_art_number"));
            record.setCaregiver_phone(getCursorValue(c, "caregiver_phone"));
            record.setComment(getCursorValue(c, "comment"));
            record.setMother_children_age_band(getCursorValue(c,"mother_children_age_band"));
            // Optional/legacy columns; AbstractDao.getCursorValue safely returns null if absent
            record.setMother_breastfeeding(getCursorValue(c, "mother_breastfeeding"));
            record.setPregnant_mother(getCursorValue(c, "pregnant_mother"));
            record.setMother_age_range(getCursorValue(c, "mother_age_range"));
            DaoModelFieldMapper.captureAdditionalFields(c, record);
            return record;
        };
    }
}



