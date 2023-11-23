package com.bluecodeltd.ecap.chw.dao;

import com.bluecodeltd.ecap.chw.adapter.WeGroupMemberFinesAdapter;
import com.bluecodeltd.ecap.chw.model.MemberFineModel;
import com.bluecodeltd.ecap.chw.model.MemberLoanModel;
import com.bluecodeltd.ecap.chw.model.WeGroupMemberSavings;

import org.smartregister.dao.AbstractDao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class WeGroupMemberFineDao extends AbstractDao{

    public static List<MemberFineModel> getWeGroupMembersFinesByGroupId (String groupId) {


        String sql = "SELECT *,\n" +
                "       strftime('%Y-%m-%d', substr(date_created, 7, 4) || '-' || substr(date_created, 4, 2) || '-' || substr(date_created, 1, 2)) as sortable_date\n" +
                "FROM ec_we_group_member_fine\n" +
                "WHERE (delete_status IS NULL OR delete_status <> '1') AND group_id = '" + groupId + "'\n" +
                "ORDER BY sortable_date DESC";

        List<MemberFineModel> values = AbstractDao.readData(sql, getMemberFineModelMap());
        if (values == null || values.size() == 0)
            return new ArrayList<>();

        return values;
    }

    public static int getTotalAmount(String memberID) {
        String sql = "SELECT SUM(amount) as amount\n" +
                "FROM ec_we_group_member_fine\n" +
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
    public static int getTotalGroupAmount(String groupID) {
        String sql = "SELECT SUM(amount) as amount\n" +
                "FROM ec_we_group_member_fine\n" +
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


    public static List<MemberFineModel> getWeGroupMembersLoansByGroupId (String groupId) {


        String sql = "SELECT *,\n" +
                "       strftime('%Y-%m-%d', substr(date_created, 7, 4) || '-' || substr(date_created, 4, 2) || '-' || substr(date_created, 1, 2)) as sortable_date\n" +
                "FROM ec_we_group_member_fine\n" +
                "WHERE (delete_status IS NULL OR delete_status <> '1') AND group_id = '" + groupId + "'\n" +
                "ORDER BY sortable_date DESC";

        List<MemberFineModel> values = AbstractDao.readData(sql, getMemberFineModelMap());
        if (values == null || values.size() == 0)
            return new ArrayList<>();

        return values;
    }

    public static List<MemberFineModel> getWeGroupMembersLoansByUniqueId (String uniqueId) {


        String sql = "SELECT *,\n" +
                "       strftime('%Y-%m-%d', substr(date_created, 7, 4) || '-' || substr(date_created, 4, 2) || '-' || substr(date_created, 1, 2)) as sortable_date\n" +
                "FROM ec_we_group_member_fine\n" +
                "WHERE (delete_status IS NULL OR delete_status <> '1') AND unique_id = '" + uniqueId + "'\n" +
                "ORDER BY sortable_date DESC";

        List<MemberFineModel> values = AbstractDao.readData(sql, getMemberFineModelMap());
        if (values == null || values.size() == 0)
            return new ArrayList<>();

        return values;
    }


    public static AbstractDao.DataMap<MemberFineModel> getMemberFineModelMap() {
        return c -> {

            MemberFineModel  record = new MemberFineModel();
            record.setLast_interacted_with(getCursorValue(c, "last_interacted_with"));
            record.setBase_entity_id(getCursorValue(c, "base_entity_id"));
            record.setDate_created(getCursorValue(c, "date_created"));
            record.setGroup_id(getCursorValue(c, "group_id"));
            record.setUnique_id(getCursorValue(c, "unique_id"));
            record.setAmount(getCursorValue(c, "amount"));
            record.setDate_of_fine(getCursorValue(c, "date_of_fine"));
            record.setReason(getCursorValue(c, "reason"));
            record.setDelete_status(getCursorValue(c, "delete_status"));
            record.setFacilitator_id(getCursorValue(c, "facilitator_id"));


            return record;
        };
    }


}
