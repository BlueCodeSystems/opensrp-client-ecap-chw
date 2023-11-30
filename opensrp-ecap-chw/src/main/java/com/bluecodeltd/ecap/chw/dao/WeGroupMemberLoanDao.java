package com.bluecodeltd.ecap.chw.dao;

import com.bluecodeltd.ecap.chw.model.MemberLoanModel;
import com.bluecodeltd.ecap.chw.model.MembersModel;
import com.bluecodeltd.ecap.chw.model.WeGroupMemberSavings;

import org.smartregister.dao.AbstractDao;

import java.util.ArrayList;
import java.util.List;

public class WeGroupMemberLoanDao extends AbstractDao {

    public static List<MemberLoanModel> getWeGroupMembersLoansByGroupId (String groupId) {


        String sql = "SELECT *,\n" +
                "       strftime('%Y-%m-%d', substr(date_created, 7, 4) || '-' || substr(date_created, 4, 2) || '-' || substr(date_created, 1, 2)) as sortable_date\n" +
                "FROM ec_we_group_member_loan\n" +
                "WHERE (delete_status IS NULL OR delete_status <> '1') AND group_id = '" + groupId + "'\n" +
                "ORDER BY sortable_date DESC";

        List<MemberLoanModel> values = AbstractDao.readData(sql, getMemberLoanModelMap());
        if (values == null || values.size() == 0)
            return new ArrayList<>();

        return values;
    }
    public static List<MemberLoanModel> getWeGroupMembersLoansByUniqueId (String uniqueId) {


        String sql = "SELECT *,\n" +
                "       strftime('%Y-%m-%d', substr(date_created, 7, 4) || '-' || substr(date_created, 4, 2) || '-' || substr(date_created, 1, 2)) as sortable_date\n" +
                "FROM ec_we_group_member_loan\n" +
                "WHERE (delete_status IS NULL OR delete_status <> '1') AND unique_id = '" + uniqueId + "'\n" +
                "ORDER BY sortable_date DESC";

        List<MemberLoanModel> values = AbstractDao.readData(sql, getMemberLoanModelMap());
        if (values == null || values.size() == 0)
            return new ArrayList<>();

        return values;
    }


    public static MemberLoanModel getWeGroupMembersLoanNumberByUniqueId (String uniqueId) {


        String sql = "SELECT *,\n" +
                "       strftime('%Y-%m-%d', substr(date_created, 7, 4) || '-' || substr(date_created, 4, 2) || '-' || substr(date_created, 1, 2)) as sortable_date\n" +
                "FROM ec_we_group_member_loan\n" +
                "WHERE (delete_status IS NULL OR delete_status <> '1') AND unique_id = '" + uniqueId + "'\n" +
                "ORDER BY CAST(loan_number AS SIGNED) DESC";

        List<MemberLoanModel> values = AbstractDao.readData(sql, getMemberLoanModelMap());
        if (values.size() == 0) {
            return null;
        }


        return values.get(0);
    }
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

    public static int getTotalGroupMemberWithLoan(String groupID) {

        String sql = "SELECT COUNT(*) as total\n" +
                "FROM ec_we_group_member_loan\n" +
                "WHERE (delete_status IS NULL OR delete_status <> '1') AND  group_id = '" + groupID + "'AND amount IS NOT NULL";

        AbstractDao.DataMap<String> dataMap = c -> getCursorValue(c, "total");

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

    public static int getTotalGroupMemberWithoutLoan(String groupID) {

        String sql = "SELECT COUNT(*) as total\n" +
                "FROM ec_we_group_member_loan\n" +
                "WHERE (delete_status IS NULL OR delete_status <> '1') AND  group_id = '" + groupID + "'AND amount IS NULL";

        AbstractDao.DataMap<String> dataMap = c -> getCursorValue(c, "total");

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

    public static int getTotalGroupAmount(String groupID) {
        String sql = "SELECT SUM(amount) as amount\n" +
                "FROM ec_we_group_member_loan\n" +
                "WHERE (delete_status IS NULL OR delete_status <> '1') AND group_id = '" + groupID + "'";

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
