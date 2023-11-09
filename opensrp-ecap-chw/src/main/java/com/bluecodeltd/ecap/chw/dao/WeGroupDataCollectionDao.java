package com.bluecodeltd.ecap.chw.dao;

import com.bluecodeltd.ecap.chw.model.WeGroupDataCollectionModel;
import com.bluecodeltd.ecap.chw.model.WeGroupModel;


import org.smartregister.dao.AbstractDao;

import java.util.List;

public class WeGroupDataCollectionDao extends AbstractDao{
    public static WeGroupDataCollectionModel getWeGroupDataCollectionId (String groupId) {


        String sql = "SELECT *,\n" +
                "       strftime('%Y-%m-%d', substr(date_created, 7, 4) || '-' || substr(date_created, 4, 2) || '-' || substr(date_created, 1, 2)) as sortable_date\n" +
                "FROM ec_we_group\n" +
                "WHERE (delete_status IS NULL OR delete_status <> '1') AND group_id = '" + groupId + "'\n" +
                "ORDER BY sortable_date DESC";

        List<WeGroupDataCollectionModel> values = AbstractDao.readData(sql, getWeGroupDataCollectionModelMap() );
        if (values.size() == 0) {
            return null;
        }

        return values.get(0);
    }

    public static AbstractDao.DataMap<WeGroupDataCollectionModel> getWeGroupDataCollectionModelMap() {
        return c -> {

            WeGroupDataCollectionModel record = new WeGroupDataCollectionModel();
            record.setBase_entity_id(getCursorValue(c, "base_entity_id"));
            record.setDate_created(getCursorValue(c, "date_created"));
            record.setWe_community_facilitator(getCursorValue(c, "we_community_facilitator"));
            record.setSupervisor_name(getCursorValue(c, "supervisor_name"));
            record.setDate_data_collection(getCursorValue(c, "date_data_collection"));
            record.setCurrent_cycle_number(getCursorValue(c, "current_cycle_number"));
            record.setGroup_name(getCursorValue(c, "group_name"));
            record.setCycle_number(getCursorValue(c, "cycle_number"));
            record.setGroup_id(getCursorValue(c, "group_id"));
            record.setAnnual_interest_rate(getCursorValue(c, "annual_interest_rate"));
            record.setFirst_training_meeting_date(getCursorValue(c, "first_training_meeting_date"));
            record.setDate_savings_started(getCursorValue(c, "date_savings_started"));
            record.setReinvested_savings_cycle_start(getCursorValue(c, "reinvested_savings_cycle_start"));
            record.setRegistered_members_cycle_start(getCursorValue(c, "registered_members_cycle_start"));
            record.setGroup_mgt(getCursorValue(c, "group_mgt"));
            record.setDelete_status(getCursorValue(c, "delete_status"));
            record.setLinkage_external_savings(getCursorValue(c, "linkage_external_savings"));
            record.setLinkage_external_credit(getCursorValue(c, "linkage_external_credit"));
            record.setValue_property_owned_current_cycle(getCursorValue(c, "value_property_owned_current_cycle"));
            record.setRegistered_members_current_cycle(getCursorValue(c, "registered_members_current_cycle"));
            record.setRegistered_members_time_visit(getCursorValue(c, "registered_members_time_visit"));
            record.setRegistered_women_time_visit(getCursorValue(c, "registered_women_time_visit"));
            record.setNumber_members_meeting(getCursorValue(c, "number_members_meeting"));
            record.setNumber_dropouts_current_cycle(getCursorValue(c, "number_dropouts_current_cycle"));
            record.setCumulative_savings_current_cycle(getCursorValue(c, "cumulative_savings_current_cycle"));
            record.setCumulative_loans_current_cycle(getCursorValue(c, "cumulative_loans_current_cycle"));
            record.setNumber_loan_outstanding(getCursorValue(c, "number_loan_outstanding"));
            record.setWrite_offs_current_cycle(getCursorValue(c, "write_offs_current cycle"));
            record.setLoan_fund_cash_box(getCursorValue(c, "loan_fund_cash_box"));
            record.setBank_balance(getCursorValue(c, "bank_balance"));
            record.setSocial_fund_balance(getCursorValue(c, "social_fund_balance"));
            record.setValue_property_currently_owned_group(getCursorValue(c, "value_property_currently_owned_group"));
            record.setValue_debts(getCursorValue(c, "value_debts"));
            record.setDividends_paid_current_cycle(getCursorValue(c, "dividends_paid_current_cycle"));
            record.setEnd_cycle_share_out_meeting(getCursorValue(c, "end_cycle_share_out_meeting"));
            record.setMembers_taken_loan(getCursorValue(c, "members_taken_loan"));
            record.setNumber_member_never_taken_loan(getCursorValue(c, "number_member_never_taken_loan"));
            record.setNumber_members_enrolled_ecapii(getCursorValue(c, "number_members_enrolled_ecapii"));
            record.setNumber_members_enrolled_ecapii_without_loan(getCursorValue(c, "number_members_enrolled_ecapii_without_loan"));
            record.setNumber_members_single_female_caregivers(getCursorValue(c, "number_members_single_female_caregivers"));
            record.setNumber_members_iga(getCursorValue(c, "number_members_iga"));
            record.setNumber_members_iga_individual(getCursorValue(c, "number_members_iga_individual"));
            record.setNumber_members_iga_group(getCursorValue(c, "number_members_iga_group"));
            record.setDelete_status(getCursorValue(c, "delete_status"));
            record.setFacilitator_id(getCursorValue(c, "facilitator_id"));


            return record;
        };
    }
}
