package com.bluecodeltd.ecap.chw.dao;

import com.bluecodeltd.ecap.chw.model.PtmctMotherMonitoringModel;

import org.smartregister.dao.AbstractDao;

import java.util.ArrayList;
import java.util.List;

public class PtmctMotherMonitoringDao extends AbstractDao {
    public static PtmctMotherMonitoringModel getPMCTMother(String pmtctID) {

        String sql = "SELECT * FROM ec_pmtct_mother_monitoring WHERE pmtct_id = '" + pmtctID + "' ";

        List<PtmctMotherMonitoringModel> values = AbstractDao.readData(sql, getPtmctMotherMonitoringModelMap());

        if (values.size() == 0) {
            return null;
        }


        return values.get(0);
    }
    public static List<PtmctMotherMonitoringModel> getPmctMotherHei(String pmtctID) {

        String sql = "SELECT * FROM ec_pmtct_mother_child WHERE pmtct_id = '" + pmtctID + "' ";

        List<PtmctMotherMonitoringModel> values = AbstractDao.readData(sql, getPtmctMotherMonitoringModelMap());
        if (values == null || values.size() == 0)
            return new ArrayList<>();

        return values;

    }
    public static String countMotherHei (String pmtctID){

        String sql = "SELECT COUNT(*) v FROM ec_pmtct_mother_child WHERE pmtct_id = '" + pmtctID + "' ";
        AbstractDao.DataMap<String> dataMap = c -> getCursorValue(c, "v");

        List<String> values = AbstractDao.readData(sql, dataMap);

        if (values == null || values.size() == 0)
            return "0";

        return values.get(0);

    }
    public static PtmctMotherMonitoringModel getPMCTChildHei(String uniqueID) {

        String sql = "SELECT * FROM ec_pmtct_mother_child WHERE unique_id = '" + uniqueID + "' ";

        List<PtmctMotherMonitoringModel> values = AbstractDao.readData(sql, getPtmctMotherMonitoringModelMap());

        if (values.size() == 0) {
            return null;
        }


        return values.get(0);
    }
    public static List<PtmctMotherMonitoringModel> getChildMonitoring(String uniqueID) {

        String sql = "SELECT *,strftime('%Y-%m-%d', substr(dbs_at_birth_due_date,7,4) || '-' || substr(dbs_at_birth_due_date,4,2) || '-' || substr(dbs_at_birth_due_date,1,2)) as sortable_date  FROM ec_pmtct_child_monitoring WHERE unique_id = '" + uniqueID + "'  ORDER BY sortable_date DESC";

        List<PtmctMotherMonitoringModel> values = AbstractDao.readData(sql, getPtmctMotherMonitoringModelMap());

        if (values == null || values.size() == 0)
            return new ArrayList<>();

        return values;

    }
    public static String countChildMonitoring (String uniqueId){

        String sql = "SELECT COUNT(*) v FROM ec_pmtct_child_monitoring WHERE unique_id = '" + uniqueId + "' ";
        AbstractDao.DataMap<String> dataMap = c -> getCursorValue(c, "v");

        List<String> values = AbstractDao.readData(sql, dataMap);

        if (values == null || values.size() == 0)
            return "0";

        return values.get(0);

    }

    public static DataMap<PtmctMotherMonitoringModel> getPtmctMotherMonitoringModelMap() {
        return c -> {

            PtmctMotherMonitoringModel record = new PtmctMotherMonitoringModel();
            record.setBase_entity_id(getCursorValue(c, "base_entity_id"));
            record.setPmtct_id(getCursorValue(c, "pmtct_id"));
            record.setUnique_id(getCursorValue(c,"unique_id"));
            record.setChild_monitoring_visit(getCursorValue(c,"child_monitoring_visit"));
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
            record.setNvp_prophylaxis_for_infant(getCursorValue(c, "nvp_prophylaxis_for_infant"));
            record.setNvp_date_given(getCursorValue(c, "nvp_date_given"));
            record.set_6_weeks_dbs_date(getCursorValue(c, "_6_weeks_dbs_date"));
            record.set_6_weeks_dbs_hiv_test_p_n(getCursorValue(c, "_6_weeks_dbs_hiv_test_p_n"));
            record.set_6_weeks_dbs_iycf_counselling(getCursorValue(c, "_6_weeks_dbs_iycf_counselling"));
            record.set_6_weeks_infant_feeding_options(getCursorValue(c, "_6_weeks_infant_feeding_options"));
            record.set_6_weeks_dbs_outcome(getCursorValue(c, "_6_weeks_dbs_outcome"));
            record.set_2_months_date(getCursorValue(c, "_2_months_date"));
            record.set_2_months_hiv_status_p_n(getCursorValue(c, "_2_months_hiv_status_p_n"));
            record.set_2_months_ctx(getCursorValue(c, "_2_months_ctx"));
            record.set_6_weeks_dbs_ctx(getCursorValue(c,"_6_weeks_dbs_ctx"));
            record.set_2_months_iycf_counselling(getCursorValue(c, "_2_months_iycf_counselling"));
            record.set_2_months_infant_feeding_options(getCursorValue(c, "_2_months_infant_feeding_options"));
            record.set_2_months_outcome(getCursorValue(c, "_2_months_outcome"));
            record.set_3_months_date(getCursorValue(c, "_3_months_date"));
            record.set_3_months_hiv_status_p_n(getCursorValue(c, "_3_months_hiv_status_p_n"));
            record.set_3_months_ctx(getCursorValue(c, "_3_months_ctx"));
            record.set_3_months_iycf_counselling(getCursorValue(c, "_3_months_iycf_counselling"));
            record.set_3_months_infant_feeding_options(getCursorValue(c, "_3_months_infant_feeding_options"));
            record.set_3_months_outcome(getCursorValue(c, "_3_months_outcome"));
            record.set_4_months_date(getCursorValue(c, "_4_months_date"));
            record.set_4_months_hiv_status_p_n(getCursorValue(c, "_4_months_hiv_status_p_n"));
            record.set_4_months_ctx(getCursorValue(c, "_4_months_ctx"));
            record.set_4_months_iycf_counselling(getCursorValue(c, "_4_months_iycf_counselling"));
            record.set_4_months_infant_feeding_options(getCursorValue(c, "_4_months_infant_feeding_options"));
            record.set_4_months_outcome(getCursorValue(c, "_4_months_outcome"));
            record.set_5_months_date(getCursorValue(c, "_5_months_date"));
            record.set_5_months_hiv_status_p_n(getCursorValue(c, "_5_months_hiv_status_p_n"));
            record.set_5_months_ctx(getCursorValue(c, "_5_months_ctx"));
            record.set_5_months_iycf_counselling(getCursorValue(c, "_5_months_iycf_counselling"));
            record.set_5_months_infant_feeding_options(getCursorValue(c, "_5_months_infant_feeding_options"));
            record.set_5_months_outcome(getCursorValue(c, "_5_months_outcome"));
            record.set_6_months_date(getCursorValue(c, "_6_months_date"));
            record.set_6_months_hiv_status_p_n(getCursorValue(c, "_6_months_hiv_status_p_n"));
            record.set_6_months_ctx(getCursorValue(c, "_6_months_ctx"));
            record.set_6_months_iycf_counselling(getCursorValue(c, "_6_months_iycf_counselling"));
            record.set_6_months_infant_feeding_options(getCursorValue(c, "_6_months_infant_feeding_options"));
            record.set_6_months_outcome(getCursorValue(c, "_6_months_outcome"));
            record.set_7_months_date(getCursorValue(c, "_7_months_date"));
            record.set_7_months_hiv_status_p_n(getCursorValue(c, "_7_months_hiv_status_p_n"));
            record.set_7_months_ctx(getCursorValue(c, "_7_months_ctx"));
            record.set_7_months_iycf_counselling(getCursorValue(c, "_7_months_iycf_counselling"));
            record.set_7_months_infant_feeding_options(getCursorValue(c, "_7_months_infant_feeding_options"));
            record.set_7_months_outcome(getCursorValue(c, "_7_months_outcome"));
            record.set_8_months_date(getCursorValue(c, "_8_months_date"));
            record.set_8_months_hiv_status_p_n(getCursorValue(c, "_8_months_hiv_status_p_n"));
            record.set_8_months_ctx(getCursorValue(c, "_8_months_ctx"));
            record.set_8_months_iycf_counselling(getCursorValue(c, "_8_months_iycf_counselling"));
            record.set_8_months_infant_feeding_options(getCursorValue(c, "_8_months_infant_feeding_options"));
            record.set_8_months_outcome(getCursorValue(c, "_8_months_outcome"));
            record.set_9_months_date(getCursorValue(c, "_9_months_date"));
            record.set_9_months_hiv_status_p_n(getCursorValue(c, "_9_months_hiv_status_p_n"));
            record.set_9_months_ctx(getCursorValue(c, "_9_months_ctx"));
            record.set_9_months_iycf_counselling(getCursorValue(c, "_9_months_iycf_counselling"));
            record.set_9_months_infant_feeding_options(getCursorValue(c, "_9_months_infant_feeding_options"));
            record.set_9_months_outcome(getCursorValue(c, "_9_months_outcome"));
            record.set_10_months_date(getCursorValue(c, "_10_months_date"));
            record.set_10_months_hiv_status_p_n(getCursorValue(c, "_10_months_hiv_status_p_n"));
            record.set_10_months_ctx(getCursorValue(c, "_10_months_ctx"));
            record.set_10_months_iycf_counselling(getCursorValue(c, "_10_months_iycf_counselling"));
            record.set_10_months_infant_feeding_options(getCursorValue(c, "_10_months_infant_feeding_options"));
            record.set_10_months_outcome(getCursorValue(c, "_10_months_outcome"));
            record.set_11_months_date(getCursorValue(c, "_11_months_date"));
            record.set_11_months_hiv_status_p_n(getCursorValue(c, "_11_months_hiv_status_p_n"));
            record.set_11_months_ctx(getCursorValue(c, "_11_months_ctx"));
            record.set_11_months_iycf_counselling(getCursorValue(c, "_11_months_iycf_counselling"));
            record.set_11_months_infant_feeding_options(getCursorValue(c, "_11_months_infant_feeding_options"));
            record.set_11_months_outcome(getCursorValue(c, "_11_months_outcome"));
            record.set_12_months_date(getCursorValue(c, "_12_months_date"));
            record.set_12_months_hiv_status_p_n(getCursorValue(c, "_12_months_hiv_status_p_n"));
            record.set_12_months_ctx(getCursorValue(c, "_12_months_ctx"));
            record.set_12_months_iycf_counselling(getCursorValue(c, "_12_months_iycf_counselling"));
            record.set_12_months_infant_feeding_options(getCursorValue(c, "_12_months_infant_feeding_options"));
            record.set_12_months_outcome(getCursorValue(c, "_12_months_outcome"));
            record.set_13_months_date(getCursorValue(c, "_13_months_date"));
            record.set_13_months_hiv_status_p_n(getCursorValue(c, "_13_months_hiv_status_p_n"));
            record.set_13_months_ctx(getCursorValue(c, "_13_months_ctx"));
            record.set_13_months_iycf_counselling(getCursorValue(c, "_13_months_iycf_counselling"));
            record.set_13_months_infant_feeding_options(getCursorValue(c, "_13_months_infant_feeding_options"));
            record.set_13_months_outcome(getCursorValue(c, "_13_months_outcome"));
            record.set_14_months_date(getCursorValue(c, "_14_months_date"));
            record.set_14_months_hiv_status_p_n(getCursorValue(c, "_14_months_hiv_status_p_n"));
            record.set_14_months_ctx(getCursorValue(c, "_14_months_ctx"));
            record.set_14_months_iycf_counselling(getCursorValue(c, "_14_months_iycf_counselling"));
            record.set_14_months_infant_feeding_options(getCursorValue(c, "_14_months_infant_feeding_options"));
            record.set_14_months_outcome(getCursorValue(c, "_14_months_outcome"));
            record.set_15_months_date(getCursorValue(c, "_15_months_date"));
            record.set_15_months_hiv_status_p_n(getCursorValue(c, "_15_months_hiv_status_p_n"));
            record.set_15_months_ctx(getCursorValue(c, "_15_months_ctx"));
            record.set_15_months_iycf_counselling(getCursorValue(c, "_15_months_iycf_counselling"));
            record.set_15_months_infant_feeding_options(getCursorValue(c, "_15_months_infant_feeding_options"));
            record.set_15_months_outcome(getCursorValue(c, "_15_months_outcome"));
            record.set_16_months_date(getCursorValue(c, "_16_months_date"));
            record.set_16_months_hiv_status_p_n(getCursorValue(c, "_16_months_hiv_status_p_n"));
            record.set_16_months_ctx(getCursorValue(c, "_16_months_ctx"));
            record.set_16_months_iycf_counselling(getCursorValue(c, "_16_months_iycf_counselling"));
            record.set_16_months_infant_feeding_options(getCursorValue(c, "_16_months_infant_feeding_options"));
            record.set_16_months_outcome(getCursorValue(c, "_16_months_outcome"));
            record.set_17_months_date(getCursorValue(c, "_17_months_date"));
            record.set_17_months_hiv_status_p_n(getCursorValue(c, "_17_months_hiv_status_p_n"));
            record.set_17_months_ctx(getCursorValue(c, "_17_months_ctx"));
            record.set_17_months_iycf_counselling(getCursorValue(c, "_17_months_iycf_counselling"));
            record.set_17_months_infant_feeding_options(getCursorValue(c, "_17_months_infant_feeding_options"));
            record.set_17_months_outcome(getCursorValue(c, "_17_months_outcome"));
            record.set_18_months_date(getCursorValue(c, "_18_months_date"));
            record.set_18_months_hiv_status_p_n(getCursorValue(c, "_18_months_hiv_status_p_n"));
            record.set_18_months_ctx(getCursorValue(c, "_18_months_ctx"));
            record.set_18_months_iycf_counselling(getCursorValue(c, "_18_months_iycf_counselling"));
            record.set_18_months_infant_feeding_options(getCursorValue(c, "_18_months_infant_feeding_options"));
            record.set_18_months_outcome(getCursorValue(c, "_18_months_outcome"));
            record.setFinal_outcome(getCursorValue(c, "final_outcome"));
            record.setDate_referred_for_art_if_hiv_positive(getCursorValue(c, "date_referred_for_art_if_hiv_positive"));
            record.setDate_enrolled_in_art(getCursorValue(c, "date_enrolled_in_art"));
            record.setDelete_status(getCursorValue(c, "delete_status"));


            return record;
        };
    }
}
