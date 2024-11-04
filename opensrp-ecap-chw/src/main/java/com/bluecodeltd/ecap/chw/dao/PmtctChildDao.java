package com.bluecodeltd.ecap.chw.dao;

import com.bluecodeltd.ecap.chw.model.PmtctChildModel;

import org.smartregister.dao.AbstractDao;

import java.util.ArrayList;
import java.util.List;

public class PmtctChildDao extends AbstractDao {
    public static List<PmtctChildModel> getPmctChildHei(String pmtctID) {

        String sql = "SELECT * FROM ec_pmtct_child WHERE pmtct_id = '" + pmtctID + "' ";

        List<PmtctChildModel> values = AbstractDao.readData(sql, getPmtctChildModelMap());
        if (values == null || values.size() == 0)
            return new ArrayList<>();

        return values;

    }
    public static PmtctChildModel getPMCTChild(String pmtctID) {

        String sql = "SELECT * FROM ec_pmtct_child WHERE unique_id = '" + pmtctID + "' ";

        List<PmtctChildModel> values = AbstractDao.readData(sql, getPmtctChildModelMap());

        if (values.size() == 0) {
            return null;
        }


        return values.get(0);
    }
    public static String countMotherHei (String pmtctID){

        String sql = "SELECT COUNT(*) v FROM ec_pmtct_child WHERE pmtct_id = '" + pmtctID + "' ";
        AbstractDao.DataMap<String> dataMap = c -> getCursorValue(c, "v");

        List<String> values = AbstractDao.readData(sql, dataMap);

        if (values == null || values.size() == 0)
            return "0";

        return values.get(0);

    }
    public static DataMap<PmtctChildModel> getPmtctChildModelMap() {
        return c -> {

            PmtctChildModel record = new PmtctChildModel();
            record.setBase_entity_id(getCursorValue(c, "base_entity_id"));
            record.setPmtct_id(getCursorValue(c, "pmtct_id"));
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


            return record;
        };
    }
}
