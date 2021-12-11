package com.bluecodeltd.ecap.chw.dao;

import com.bluecodeltd.ecap.chw.domain.Mother;
import com.bluecodeltd.ecap.chw.model.Household;

import org.smartregister.chw.core.domain.Child;
import org.smartregister.dao.AbstractDao;

import java.util.List;

public class HouseholdDao extends AbstractDao {

    public static String checkIfScreened (String baseEntityID) {

        String sql = "SELECT screened FROM ec_household WHERE base_entity_id = '" + baseEntityID + "'";

        AbstractDao.DataMap<String> dataMap = c -> getCursorValue(c, "screened");

        List<String> values = AbstractDao.readData(sql, dataMap);

        return values.get(0);

    }


    public static Household getHousehold (String baseEntityID) {

        String sql = "SELECT ec_household.*, ec_household.village AS adolescent_village, ec_client_index.* FROM ec_household JOIN ec_client_index ON ec_household.base_entity_id = ec_client_index.base_entity_id WHERE ec_household.base_entity_id = '" + baseEntityID + "' ";


        List<Household> values = AbstractDao.readData(sql, getHouseholdMap());
        if (values == null || values.size() != 1)
            return null;

        return values.get(0);

    }

    public static DataMap<Household> getHouseholdMap() {
        return c -> {

            Household record = new Household();
            record.setBase_entity_id(getCursorValue(c, "base_entity_id"));
            record.setVillage(getCursorValue(c, "adolescent_village"));
            record.setDistrict(getCursorValue(c, "district"));
            record.setScreening_date(getCursorValue(c, "screening_date"));
            record.setViolence_six_months(getCursorValue(c, "violence_six_months"));
            record.setChildren_violence_six_months(getCursorValue(c, "children_violence_six_months"));
            record.setEnrolled_pmtct(getCursorValue(c, "enrolled_pmtct"));
            record.setScreened(getCursorValue(c, "screened"));
            record.setEnrollment_date(getCursorValue(c, "enrollment_date"));
            record.setEntry_type(getCursorValue(c, "entry_type"));
            record.setOther_entry_type(getCursorValue(c, "other_entry_type"));
            record.setMonthly_expenses(getCursorValue(c, "monthly_expenses"));
            record.setMales_less_5(getCursorValue(c, "males_less_5"));
            record.setFemales_less_5(getCursorValue(c, "females_less_5"));
            record.setMales_10_17(getCursorValue(c, "males_10_17"));
            record.setFemales_10_17(getCursorValue(c, "females_10_17"));
            record.setIncome(getCursorValue(c, "monthly_expenses"));
            record.setFam_source_income(getCursorValue(c, "fam_source_income"));
            record.setPregnant_women(getCursorValue(c, "pregnant_women"));
            record.setBeds(getCursorValue(c, "beds"));
            record.setMalaria_itns(getCursorValue(c, "malaria_itns"));
            record.setHousehold_member_had_malaria(getCursorValue(c, "household_member_had_malaria"));

            return record;
        };
    }

}
