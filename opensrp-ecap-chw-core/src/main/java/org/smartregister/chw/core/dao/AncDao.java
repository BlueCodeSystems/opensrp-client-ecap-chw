package org.smartregister.chw.core.dao;

import org.jetbrains.annotations.Nullable;
import org.smartregister.chw.anc.domain.MemberObject;

import java.util.ArrayList;
import java.util.List;

public class AncDao extends AlertDao {

    @Nullable
    public static String getAncDateCreated(String baseEntityID) {
        String sql = "select date_created from ec_anc_log where base_entity_id = '" + baseEntityID + "'";

        DataMap<String> dataMap = cursor -> getCursorValue(cursor, "date_created");

        List<String> res = readData(sql, dataMap);
        if (res == null || res.size() != 1)
            return null;

        return res.get(0);
    }

    public static boolean isANCMember(String baseEntityID) {
        String sql = "select count(*) count from ec_anc_register where base_entity_id = '" + baseEntityID + "' and is_closed = 0";

        DataMap<Integer> dataMap = cursor -> getCursorIntValue(cursor, "count");

        List<Integer> res = readData(sql, dataMap);
        if (res == null || res.size() != 1)
            return false;

        return res.get(0) > 0;
    }

    public static MemberObject getMember(String baseEntityID) {
        String sql = "select f.landmark, f.gps, m.base_entity_id , m.unique_id , m.relational_id , m.dob , m.first_name , m.middle_name , m.last_name , m.gender , " +
                "m.phone_number , m.other_phone_number , f.first_name family_name , f.primary_caregiver , f.family_head , " +
                "fh.first_name family_head_first_name , fh.middle_name family_head_middle_name, fh.last_name family_head_last_name, " +
                "fh.phone_number family_head_phone_number , ar.confirmed_visits , f.village_town, al.date_created, ar.* " +
                "from ec_family_member m " +
                "inner join ec_family f on m.relational_id = f.base_entity_id " +
                "inner join ec_anc_register ar on ar.base_entity_id = m.base_entity_id " +
                "inner join ec_anc_log al on al.base_entity_id =ar.base_entity_id " +
                "left join ec_family_member fh on fh.base_entity_id = f.family_head " +
                "where m.base_entity_id = '" + baseEntityID + "' ";

        DataMap<MemberObject> dataMap = cursor -> {
            MemberObject memberObject = new MemberObject();
            memberObject.setLastMenstrualPeriod(getCursorValue(cursor, "last_menstrual_period"));
            memberObject.setGravida(getCursorValue(cursor, "gravida"));
            memberObject.setChwMemberId(getCursorValue(cursor, "unique_id", ""));
            memberObject.setBaseEntityId(getCursorValue(cursor, "base_entity_id", ""));
            memberObject.setFamilyBaseEntityId(getCursorValue(cursor, "relational_id", ""));
            memberObject.setFamilyHead(getCursorValue(cursor, "family_head", ""));

            String familyHeadName = getCursorValue(cursor, "family_head_first_name", "") + " "
                    + getCursorValue(cursor, "family_head_middle_name", "");

            familyHeadName = (familyHeadName.trim() + " " + getCursorValue(cursor, "family_head_last_name", "")).trim();

            memberObject.setFamilyHeadName(familyHeadName);
            memberObject.setFamilyHeadPhoneNumber(getCursorValue(cursor, "family_head_phone_number", ""));
            memberObject.setPrimaryCareGiver(getCursorValue(cursor, "primary_caregiver"));
            memberObject.setFamilyName(getCursorValue(cursor, "family_name", ""));
            memberObject.setLastContactVisit(getCursorValue(cursor, "last_contact_visit"));
            memberObject.setLastInteractedWith(getCursorValue(cursor, "last_interacted_with"));
            memberObject.setFirstName(getCursorValue(cursor, "first_name", ""));
            memberObject.setMiddleName(getCursorValue(cursor, "middle_name", ""));
            memberObject.setLastName(getCursorValue(cursor, "last_name", ""));
            memberObject.setDob(getCursorValue(cursor, "dob"));
            memberObject.setPhoneNumber(getCursorValue(cursor, "phone_number", ""));
            memberObject.setConfirmedContacts(getCursorIntValue(cursor, "confirmed_visits", 0));
            memberObject.setDateCreated(getCursorValue(cursor, "date_created"));
            memberObject.setAddress(getCursorValue(cursor, "village_town"));
            memberObject.setHasAncCard(getCursorValue(cursor, "has_anc_card", ""));
            memberObject.setDeliveryKit(getCursorValue(cursor, "delivery_kit",""));
            memberObject.setGps(getCursorValue(cursor, "gps"));
            memberObject.setLandmark(getCursorValue(cursor, "landmark"));

            return memberObject;
        };

        List<MemberObject> res = readData(sql, dataMap);
        if (res == null || res.size() != 1)
            return null;

        return res.get(0);
    }

    public static int getAncWomenCount(String familyBaseID) {
        String sql = "SELECT count(ear.base_entity_id) count FROM  ec_anc_register as ear " +
                "INNER JOIN ec_family_member efm ON ear.base_entity_id = efm.base_entity_id " +
                "WHERE ear.relational_id = '" + familyBaseID + "' " +
                "AND ear.is_closed = 0 AND efm.is_closed= 0";

        DataMap<Integer> dataMap = cursor -> getCursorIntValue(cursor, "count");

        List<Integer> res = readData(sql, dataMap);
        return (res == null || res.size() < 1) ? 0 : res.get(0);
    }
    public static boolean showTT(String baseEntityID){
        String sql = "SELECT count(*) as count\n" +
                "FROM visit_details vd\n" +
                "INNER JOIN visits v on v.visit_id = vd.visit_id\n" +
                "WHERE vd.visit_key = 'imm_medicine_given'\n" +
                "AND vd.human_readable_details = 'Tetanus toxoid (TT)'\n" +
                "AND v.base_entity_id = '" + baseEntityID + "'";

        DataMap<Integer> dataMap = cursor -> getCursorIntValue(cursor, "count");

        List<Integer> res = readData(sql, dataMap);
        return res == null || res.get(0) < 2;
    }

    public static List<String> getTestDone(String baseEntityID) {
        String sql = "SELECT vd.human_readable_details\n" +
                "FROM visit_details vd\n" +
                "INNER JOIN visits v on v.visit_id = vd.visit_id\n" +
                "WHERE vd.visit_key = 'tests_done'\n" +
                "AND ((vd.human_readable_details != 'Haemoglobin level') AND (vd.human_readable_details != 'None') AND (vd.human_readable_details != 'Other test'))\n" +
                "AND v.base_entity_id = '" + baseEntityID + "'";

        DataMap<String> dataMap = c -> getCursorValue(c, "human_readable_details");

        List<String> humanReadableDetails = readData(sql, dataMap);
        if (humanReadableDetails != null)
            return humanReadableDetails;

        return new ArrayList<>();
    }
}
