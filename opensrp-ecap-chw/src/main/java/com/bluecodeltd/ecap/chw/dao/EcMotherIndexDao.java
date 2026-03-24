package com.bluecodeltd.ecap.chw.dao;

import com.bluecodeltd.ecap.chw.model.EcMotherIndexModel;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.dao.AbstractDao;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EcMotherIndexDao extends AbstractDao {

    public static List<EcMotherIndexModel> getMothers(String householdId) {
        String sql = "SELECT * FROM ec_pmtct_mother WHERE household_id = '" + householdId + "' " +
                "AND delete_status IS NULL";

        List<EcMotherIndexModel> values = AbstractDao.readData(sql, getMotherIndexMap());
        if (values == null || values.isEmpty()) {
            return new ArrayList<>();
        }
        return values;
    }

    public static EcMotherIndexModel getMotherByBaseEntityId(String baseEntityId) {
        String sql = "SELECT * FROM ec_pmtct_mother WHERE base_entity_id = '" + baseEntityId + "' " +
                "AND delete_status IS NULL";

        List<EcMotherIndexModel> values = AbstractDao.readData(sql, getMotherIndexMap());
        if (values == null || values.isEmpty()) {
            return null;
        }
        return values.get(0);
    }

    public static CommonPersonObjectClient getFirstMotherByHousehold(String householdId) {
        try {
            List<EcMotherIndexModel> mothers = getMothers(householdId);
            if (mothers.isEmpty()) {
                return null;
            }
            EcMotherIndexModel first = mothers.get(0);
            HashMap<String, String> columnMaps = new HashMap<>();
            columnMaps.put("base_entity_id", first.getBase_entity_id());
            columnMaps.put("household_id", first.getHousehold_id());
            columnMaps.put("caregiver_name", first.getCaregiver_name());
            CommonPersonObjectClient client = new CommonPersonObjectClient(first.getBase_entity_id(), columnMaps, null);
            client.setColumnmaps(columnMaps);
            return client;
        } catch (Exception e) {
            return null;
        }
    }

    public static String countAllPmtctMothers() {
        String sql = "SELECT COUNT(*) v FROM ec_pmtct_mother WHERE delete_status IS NULL";
        AbstractDao.DataMap<String> dataMap = c -> getCursorValue(c, "v");
        List<String> values = AbstractDao.readData(sql, dataMap);
        if (values == null || values.isEmpty()) return "0";
        return values.get(0);
    }

    public static String countMothers(String householdId) {
        String sql = "SELECT COUNT(*) v FROM ec_pmtct_mother WHERE household_id = '" + householdId + "' " +
                "AND delete_status IS NULL";

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
            record.setDeleted(getCursorValue(c, "delete_status"));
            record.setDelete_status(getCursorValue(c, "delete_status"));
            record.setLast_interacted_with(getCursorValue(c, "last_interacted_with"));
            record.setBase_entity_id(getCursorValue(c, "base_entity_id"));
            record.setSm_number(getCursorValue(c, "sm_number"));
            record.setHousehold_id(getCursorValue(c, "household_id"));
            record.setCaregiver_name(getCursorValue(c, "caregiver_name"));
            record.setCaregiver_birth_date(getCursorValue(c, "caregiver_birth_date"));
            record.setProvince(getCursorValue(c, "province"));
            record.setDistrict(getCursorValue(c, "district"));
            record.setWard(getCursorValue(c, "ward"));
            record.setFacility(getCursorValue(c, "facility"));
            record.setPartner(getCursorValue(c, "partner"));
            record.setCaseworker_name(getCursorValue(c, "caseworker_name"));
            record.setPmtct_id(getCursorValue(c, "pmtct_id"));
            record.setEcap_id_question(getCursorValue(c, "ecap_id_question"));
            record.setDate_enrolled_ecap(getCursorValue(c, "date_enrolled_ecap"));
            record.setEcap_id(getCursorValue(c, "ecap_id"));
            record.setDate_enrolled_pmtct(getCursorValue(c, "date_enrolled_pmtct"));
            record.setFirst_name(getCursorValue(c, "first_name"));
            record.setLast_name(getCursorValue(c, "last_name"));
            record.setMothers_age(getCursorValue(c, "mothers_age"));
            String homeAddress = getCursorValue(c, "home_address");
            record.setHome_address(homeAddress);
            record.setHomeaddress(homeAddress);
            String nearestLandmark = getCursorValue(c, "nearest_landmark");
            record.setNearest_landmark(nearestLandmark);
            record.setLandmark(nearestLandmark);
            String mothersPhone = getCursorValue(c, "mothers_phone");
            record.setMothers_phone(mothersPhone);
            record.setCaregiver_phone(mothersPhone);
            record.setAgyw_date_1st_visit(getCursorValue(c, "agyw_date_1st_visit"));
            record.setAgyw_gestation_age_in_weeks(getCursorValue(c, "agyw_gestation_age_in_weeks"));
            record.setAgyw_hiv_tested(getCursorValue(c, "agyw_hiv_tested"));
            record.setAgyw_date_tested(getCursorValue(c, "agyw_date_tested"));
            record.setAgyw_result_of_hiv_test(getCursorValue(c, "agyw_result_of_hiv_test"));
            record.setAgyw_recency_test_result(getCursorValue(c, "agyw_recency_test_result"));
            record.setAgyw_applicable_recency_result(getCursorValue(c, "agyw_applicable_recency_result"));
            record.setAgyw_male_hiv_tested(getCursorValue(c, "agyw_male_hiv_tested"));
            record.setAgyw_male_date_tested(getCursorValue(c, "agyw_male_date_tested"));
            record.setAgyw_male_result_of_hiv_test(getCursorValue(c, "agyw_male_result_of_hiv_test"));
            record.setAgyw_positive_male_partner(getCursorValue(c, "agyw_positive_male_partner"));
            record.setAgyw_date_initiated_art(getCursorValue(c, "agyw_date_initiated_art"));
            record.setAgyw_art_number(getCursorValue(c, "agyw_art_number"));
            record.setAgyw_on_treatment_anc_visit(getCursorValue(c, "agyw_on_treatment_anc_visit"));
            record.setAgyw_tb_screening(getCursorValue(c, "agyw_tb_screening"));
            record.setAgyw_syphilis_testing(getCursorValue(c, "agyw_syphilis_testing"));
            record.setAgyw_date_tested_syphilis(getCursorValue(c, "agyw_date_tested_syphilis"));
            record.setAgyw_syphilis_test_result(getCursorValue(c, "agyw_syphilis_test_result"));
            record.setAgyw_hiv_result_1st_trimester(getCursorValue(c, "agyw_hiv_result_1st_trimester"));
            record.setAgyw_hiv_result_2nd_trimester(getCursorValue(c, "agyw_hiv_result_2nd_trimester"));
            record.setAgyw_hiv_result_3rd_trimester(getCursorValue(c, "agyw_hiv_result_3rd_trimester"));
            record.setAgyw_vl_result_1st_trimester(getCursorValue(c, "agyw_vl_result_1st_trimester"));
            record.setAgyw_unsuppressed_vl_1st(getCursorValue(c, "agyw_unsuppressed_vl_1st"));
            record.setAgyw_vl_result_2nd_trimester(getCursorValue(c, "agyw_vl_result_2nd_trimester"));
            record.setAgyw_unsuppressed_vl_2nd(getCursorValue(c, "agyw_unsuppressed_vl_2nd"));
            record.setAgyw_vl_result_3rd_trimester(getCursorValue(c, "agyw_vl_result_3rd_trimester"));
            record.setAgyw_unsuppressed_vl_3rd(getCursorValue(c, "agyw_unsuppressed_vl_3rd"));
            record.setPreventive_services(getCursorValue(c, "preventive_services"));
            record.setOther_preventive_service(getCursorValue(c, "other_preventive_service"));
            record.setDate_initiated_art(getCursorValue(c, "date_initiated_art"));
            record.setArt_number(getCursorValue(c, "art_number"));
            record.setDate_1st_visit(getCursorValue(c, "date_1st_visit"));
            record.setGestation_age_in_weeks(getCursorValue(c, "gestation_age_in_weeks"));
            record.setHiv_tested(getCursorValue(c, "hiv_tested"));
            record.setDate_tested(getCursorValue(c, "date_tested"));
            record.setResult_of_hiv_test(getCursorValue(c, "result_of_hiv_test"));
            record.setRecency_test_result(getCursorValue(c, "recency_test_result"));
            record.setApplicable_recency_result(getCursorValue(c, "applicable_recency_result"));
            record.setMale_hiv_tested(getCursorValue(c, "male_hiv_tested"));
            record.setMale_date_tested(getCursorValue(c, "male_date_tested"));
            record.setMale_result_of_hiv_test(getCursorValue(c, "male_result_of_hiv_test"));
            record.setTreatment_initiated(getCursorValue(c, "treatment_initiated"));
            record.setOn_treatment_anc_visit(getCursorValue(c, "on_treatment_anc_visit"));
            record.setTb_screening(getCursorValue(c, "tb_screening"));
            record.setSyphilis_testing(getCursorValue(c, "syphilis_testing"));
            record.setDate_tested_syphilis(getCursorValue(c, "date_tested_syphilis"));
            record.setSyphilis_test_result(getCursorValue(c, "syphilis_test_result"));
            record.setHiv_result_1st_trimester(getCursorValue(c, "hiv_result_1st_trimester"));
            record.setHiv_result_2nd_trimester(getCursorValue(c, "hiv_result_2nd_trimester"));
            record.setHiv_result_3rd_trimester(getCursorValue(c, "hiv_result_3rd_trimester"));
            record.setVl_result_1st_trimester(getCursorValue(c, "vl_result_1st_trimester"));
            record.setUnsuppressed_vl_1st(getCursorValue(c, "unsuppressed_vl_1st"));
            record.setVl_result_2nd_trimester(getCursorValue(c, "vl_result_2nd_trimester"));
            record.setUnsuppressed_vl_2nd(getCursorValue(c, "unsuppressed_vl_2nd"));
            record.setVl_result_3rd_trimester(getCursorValue(c, "vl_result_3rd_trimester"));
            record.setUnsuppressed_vl_3rd(getCursorValue(c, "unsuppressed_vl_3rd"));
            record.setIs_closed(getCursorValue(c, "is_closed"));
            // legacy/alternate columns retained for backward compatibility
            record.setUser_select_hiv(getCursorValue(c, "user_select_hiv"));
            record.setMother_screening_date(getCursorValue(c, "mother_screening_date"));
            record.setScreening_location_home(getCursorValue(c, "screening_location_home"));
            record.setCaregiver_sex(getCursorValue(c, "caregiver_sex"));
            record.setCaregiver_hiv_status(getCursorValue(c, "caregiver_hiv_status"));
            record.setActive_on_treatment(getCursorValue(c, "active_on_treatment"));
            record.setCaregiver_art_number(getCursorValue(c, "caregiver_art_number"));
            record.setComment(getCursorValue(c, "comment"));
            record.setMother_pregnant(getCursorValue(c, "mother_pregnant"));
            record.setMother_breastfeeding(getCursorValue(c, "mother_breastfeeding"));
            record.setMother_age_range(getCursorValue(c, "mother_age_range"));
            record.setMother_children_age_band(getCursorValue(c, "mother_children_age_band"));
            DaoModelFieldMapper.captureAdditionalFields(c, record);
            return record;
        };
    }
}


