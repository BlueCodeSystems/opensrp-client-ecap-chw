package com.bluecodeltd.ecap.chw.dao;

import com.bluecodeltd.ecap.chw.domain.Mother;
import com.bluecodeltd.ecap.chw.model.Household;

import org.smartregister.chw.core.domain.Child;
import org.smartregister.dao.AbstractDao;

import java.util.List;

public class HouseholdDao extends AbstractDao {

    public static String checkIfScreened (String household_id) {

        String sql = "SELECT screened FROM ec_household WHERE household_id = '" + household_id + "'";

        AbstractDao.DataMap<String> dataMap = c -> getCursorValue(c, "screened");

        List<String> values = AbstractDao.readData(sql, dataMap);

        return values.get(0);

    }


    public static Household getHousehold (String householdID) {

        String sql = "SELECT ec_household.*, ec_household.village AS adolescent_village, ec_client_index.* FROM ec_household JOIN ec_client_index ON ec_household.household_id = ec_client_index.household_id WHERE ec_household.household_id = '" + householdID + "' ";

                List<Household> values = AbstractDao.readData(sql, getHouseholdMap());

        return values.get(0);

    }

    public static DataMap<Household> getHouseholdMap() {
        return c -> {

            Household record = new Household();
            record.setUnique_id(getCursorValue(c, "unique_id"));
            record.setFirst_name(getCursorValue(c, "first_name"));
            record.setLast_name(getCursorValue(c, "last_name"));
            record.setGender(getCursorValue(c, "gender"));
            record.setAdolescent_birthdate(getCursorValue(c, "adolescent_birthdate"));
            record.setSubpop1(getCursorValue(c, "subpop1"));
            record.setSubpop2(getCursorValue(c, "subpop2"));
            record.setSubpop3(getCursorValue(c, "subpop3"));
            record.setSubpop4(getCursorValue(c, "subpop4"));
            record.setSubpop5(getCursorValue(c, "subpop5"));
            record.setSubpop6(getCursorValue(c, "subpop6"));
            record.setSubpop6(getCursorValue(c, "subpop6"));
            record.setCaregiver_name(getCursorValue(c, "caregiver_name"));
            record.setCaregiver_sex(getCursorValue(c, "caregiver_sex"));
            record.setCaregiver_birth_date(getCursorValue(c, "caregiver_birth_date"));
            record.setPhysical_address(getCursorValue(c, "physical_address"));
            record.setCaregiver_phone(getCursorValue(c, "caregiver_phone"));
            record.setCaregiver_hiv_status(getCursorValue(c, "caregiver_hiv_status"));
            record.setActive_on_treatment(getCursorValue(c, "active_on_treatment"));
            record.setCaregiver_art_number(getCursorValue(c, "caregiver_art_number"));
            record.setViral_load_results(getCursorValue(c, "viral_load_results"));
            record.setDate_of_last_viral_load(getCursorValue(c, "date_of_last_viral_load"));
            record.setCaseworker_name(getCursorValue(c, "caseworker_name"));
            record.setBase_entity_id(getCursorValue(c, "base_entity_id"));
            record.setHousehold_id(getCursorValue(c, "household_id"));
            record.setVillage(getCursorValue(c, "village"));
            record.setUser_gps(getCursorValue(c, "user_gps"));
            record.setDistrict(getCursorValue(c, "district"));
            record.setScreening_date(getCursorValue(c, "screening_date"));
            record.setScreening_location(getCursorValue(c, "screening_location"));
            record.setViolence_six_months(getCursorValue(c, "violence_six_months"));
            record.setChildren_violence_six_months(getCursorValue(c, "children_violence_six_months"));
            record.setBiological_children(getCursorValue(c, "biological_children"));
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
            record.setEmergency_name(getCursorValue(c, "emergency_name"));
            record.setE_relationship(getCursorValue(c, "e_relationship"));
            record.setContact_address(getCursorValue(c, "contact_address"));
            record.setContact_number(getCursorValue(c, "contact_number"));


            return record;
        };
    }

}
