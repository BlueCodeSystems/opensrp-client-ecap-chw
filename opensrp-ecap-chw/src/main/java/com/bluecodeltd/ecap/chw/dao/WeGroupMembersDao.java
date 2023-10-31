package com.bluecodeltd.ecap.chw.dao;

import com.bluecodeltd.ecap.chw.model.HivTestingServiceModel;
import com.bluecodeltd.ecap.chw.model.MembersModel;
import com.bluecodeltd.ecap.chw.model.WeGroupModel;

import org.smartregister.dao.AbstractDao;

import java.util.ArrayList;
import java.util.List;

public class WeGroupMembersDao extends AbstractDao {
    public static int getMembersCount(){

        String sql = "SELECT COUNT(*) as members\n" +
                "FROM ec_we_group_member\n" +
                "WHERE (delete_status IS NULL OR delete_status <> '1')";

        AbstractDao.DataMap<String> dataMap = c -> getCursorValue(c, "members");

        List<String> values = AbstractDao.readData(sql, dataMap);

        return Integer.parseInt(values.get(0));

    }
    public static MembersModel getWeGroupMemberById (String memberID) {


        String sql = "SELECT *,\n" +
                "       strftime('%Y-%m-%d', substr(date_created, 7, 4) || '-' || substr(date_created, 4, 2) || '-' || substr(date_created, 1, 2)) as sortable_date\n" +
                "FROM ec_we_group_member\n" +
                "WHERE (delete_status IS NULL OR delete_status <> '1') AND unique_id = '" + memberID + "'\n" +
                "ORDER BY sortable_date DESC";

        List<MembersModel> values = AbstractDao.readData(sql, getWeGroupMembersModelMap());
        if (values.size() == 0) {
            return null;
        }


        return values.get(0);
    }
    public static List<MembersModel> getWeGroupMembers () {


        String sql = "SELECT *,\n" +
                "       strftime('%Y-%m-%d', substr(date_created, 7, 4) || '-' || substr(date_created, 4, 2) || '-' || substr(date_created, 1, 2)) as sortable_date\n" +
                "FROM ec_we_group_member\n" +
                "WHERE (delete_status IS NULL OR delete_status <> '1')\n" +
                "ORDER BY sortable_date DESC";

        List<MembersModel> values = AbstractDao.readData(sql, getWeGroupMembersModelMap());
        if (values == null || values.size() == 0)
            return new ArrayList<>();

        return values;
    }
    public static DataMap<MembersModel> getWeGroupMembersModelMap() {
        return c -> {

            MembersModel record = new MembersModel();

            record.setLast_interacted_with(getCursorValue(c, "last_interacted_with"));
            record.setBase_entity_id(getCursorValue(c, "base_entity_id"));
            record.setDate_created(getCursorValue(c, "date_created"));
            record.setGroup_id(getCursorValue(c, "group_id"));
            record.setUnique_id(getCursorValue(c, "unique_id"));
            record.setFirst_name(getCursorValue(c, "first_name"));
            record.setLast_name(getCursorValue(c, "last_name"));
            record.setGender(getCursorValue(c, "gender"));
            record.setNrc(getCursorValue(c, "nrc"));
            record.setEmail(getCursorValue(c, "email"));
            record.setUsername(getCursorValue(c, "username"));
            record.setAdmission_date(getCursorValue(c, "admission_date"));
            record.setPassword(getCursorValue(c, "password"));
            record.setEcap_hh_ID(getCursorValue(c, "ecap_hh_ID"));
            record.setRole(getCursorValue(c, "role"));
            record.setPhone_number(getCursorValue(c, "phone_number"));
            record.setSingle_female_caregiver(getCursorValue(c, "single_female_caregiver"));
            record.setNext_of_kin(getCursorValue(c, "next_of_kin"));
            record.setNext_of_kin_phone(getCursorValue(c, "next_of_kin_phone"));
            record.setDelete_status(getCursorValue(c, "delete_status"));
            record.setFacilitator_id(getCursorValue(c, "facilitator_id"));


            return record;
        };
    }
}
