package com.bluecodeltd.ecap.chw.dao;

import com.bluecodeltd.ecap.chw.model.EcMotherIndexModel;

import org.smartregister.dao.AbstractDao;

import java.util.ArrayList;
import java.util.List;

public class EcMotherIndexDao extends AbstractDao {

    public static List<EcMotherIndexModel> getMothers(String householdId) {
        String sql = "SELECT * FROM ec_mother_index WHERE household_id = '" + householdId + "' " +
                "AND (deleted IS NULL OR deleted <> '1')";

        List<EcMotherIndexModel> values = AbstractDao.readData(sql, getMotherIndexMap());
        if (values == null || values.isEmpty()) {
            return new ArrayList<>();
        }
        return values;
    }

    public static EcMotherIndexModel getMotherByBaseEntityId(String baseEntityId) {
        String sql = "SELECT * FROM ec_mother_index WHERE base_entity_id = '" + baseEntityId + "' " +
                "AND (deleted IS NULL OR deleted <> '1')";

        List<EcMotherIndexModel> values = AbstractDao.readData(sql, getMotherIndexMap());
        if (values == null || values.isEmpty()) {
            return null;
        }
        return values.get(0);
    }

    public static String countMothers(String householdId) {
        String sql = "SELECT COUNT(*) v FROM ec_mother_index WHERE household_id = '" + householdId + "' " +
                "AND (deleted IS NULL OR deleted <> '1')";

        AbstractDao.DataMap<String> dataMap = c -> getCursorValue(c, "v");
        List<String> values = AbstractDao.readData(sql, dataMap);
        if (values == null || values.isEmpty()) {
            return "0";
        }
        return values.get(0);
    }

    private static DataMap<EcMotherIndexModel> getMotherIndexMap() {
        return c -> {
            EcMotherIndexModel record = new EcMotherIndexModel();
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
            record.setMother_pregnant(getCursorValue(c, "mother_pregnant"));
            record.setMother_breastfeeding(getCursorValue(c, "mother_breastfeeding"));
            record.setMother_age_range(getCursorValue(c, "mother_age_range"));
            record.setMother_children_age_band(getCursorValue(c, "mother_children_age_band"));
            return record;
        };
    }
}
