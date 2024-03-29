package com.bluecodeltd.ecap.chw.dao;

import com.bluecodeltd.ecap.chw.model.WeServiceCaregiverModel;

import org.smartregister.dao.AbstractDao;

import java.util.List;

public class WeServiceCaregiverDoa extends AbstractDao {
    public static WeServiceCaregiverModel getWeServiceCaregiver (String householdID) {

        String sql = "SELECT * FROM ec_we_services_caregiver WHERE household_id = '" + householdID + "' ORDER BY date_joined_we LIMIT 1";

        List<WeServiceCaregiverModel> values = AbstractDao.readData(sql, getWeServiceCaregiverMap());

        if (values.size() == 0) {
            return null;
        }

        return values.get(0);
    }

    public static DataMap<WeServiceCaregiverModel> getWeServiceCaregiverMap() {
        return c -> {

            WeServiceCaregiverModel record = new WeServiceCaregiverModel();
            record.setBase_entity_id(getCursorValue(c, "base_entity_id"));
            record.setData_collection(getCursorValue(c, "data_collection"));
            record.setHousehold_id(getCursorValue(c, "household_id"));
            record.setParticipant_name(getCursorValue(c, "participant_name"));
            record.setWe_group_no(getCursorValue(c, "we_group_no"));
            record.setWe_group_name(getCursorValue(c, "we_group_name"));
            record.setDate_joined_we(getCursorValue(c, "date_joined_we"));
            record.setReporting_month(getCursorValue(c, "reporting_month"));
            record.setWe_member_in_period(getCursorValue(c, "we_member_in_period"));
            record.setWe_loan_in_period(getCursorValue(c, "we_loan_in_period"));
            record.setWe_loans_taken_in_period(getCursorValue(c, "we_loans_taken_in_period"));
            record.setWe_loan_value1(getCursorValue(c, "we_loan_value1"));


            record.setWe_loans1_on_school(getCursorValue(c, "we_loans1_on_school"));
            record.setWe_loans2_on_school(getCursorValue(c, "we_loans2_on_school"));
            record.setWe_loans1_on_medical(getCursorValue(c, "we_loans1_on_medical"));
            record.setWe_loans2_on_medical(getCursorValue(c, "we_loans2_on_medical"));
            record.setWe_loan1_on_iga(getCursorValue(c, "we_loan1_on_iga"));
            record.setWe_loan2_on_iga(getCursorValue(c, "we_loan2_on_iga"));
            record.setWe_loan1_iga_type(getCursorValue(c, "we_loan1_iga_type"));
            record.setWe_loan2_iga_type(getCursorValue(c, "we_loan2_iga_type"));
            record.setWe_loan1_use(getCursorValue(c, "we_loan1_use"));
            record.setWe_loan2_use(getCursorValue(c, "we_loan2_use"));
            record.setSold_items(getCursorValue(c, "sold_items"));
            record.setOther_sold_item(getCursorValue(c, "other_sold_item"));
            return record;
        };
    }
}
