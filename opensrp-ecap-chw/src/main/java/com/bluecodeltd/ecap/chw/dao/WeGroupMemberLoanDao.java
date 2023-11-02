package com.bluecodeltd.ecap.chw.dao;

import com.bluecodeltd.ecap.chw.model.MemberLoanModel;

import org.smartregister.dao.AbstractDao;

import java.util.List;

public class WeGroupMemberLoanDao extends AbstractDao {
    public static int getTotalAmount(String memberID) {
        String sql = "SELECT SUM(amount) as amount\n" +
                "FROM ec_we_group_member_loan\n" +
                "WHERE (delete_status IS NULL OR delete_status <> '1') AND unique_id = '" + memberID + "'";

        AbstractDao.DataMap<String> dataMap = c -> getCursorValue(c, "amount");

        List<String> values = AbstractDao.readData(sql, dataMap);

        if (values.get(0) != null) {
            try {
                return Integer.parseInt(values.get(0));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        return 0;
    }

    public static DataMap<MemberLoanModel> getMemberLoanModelMap() {
        return c -> {

            MemberLoanModel  record = new MemberLoanModel();
            record.setLast_interacted_with(getCursorValue(c, "last_interacted_with"));
            record.setBase_entity_id(getCursorValue(c, "base_entity_id"));
            record.setDate_created(getCursorValue(c, "date_created"));
            record.setGroup_id(getCursorValue(c, "group_id"));
            record.setUnique_id(getCursorValue(c, "unique_id"));
            record.setAmount(getCursorValue(c, "amount"));
            record.setDate_of_loan(getCursorValue(c, "date_of_loan"));
            record.setInterest_rate(getCursorValue(c, "interest_rate"));
            record.setDuration(getCursorValue(c, "duration"));
            record.setDue_date(getCursorValue(c, "due_date"));
            record.setLoan_number(getCursorValue(c, "loan_number"));
            record.setStarted_iga(getCursorValue(c, "started_iga"));
            record.setDelete_status(getCursorValue(c, "delete_status"));
            record.setFacilitator_id(getCursorValue(c, "facilitator_id"));


            return record;
        };
    }
}
