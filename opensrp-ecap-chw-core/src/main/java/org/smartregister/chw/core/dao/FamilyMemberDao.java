package org.smartregister.chw.core.dao;

import org.smartregister.chw.core.model.CoreFamilyMemberModel;
import org.smartregister.dao.AbstractDao;

import java.util.List;

public class FamilyMemberDao extends AbstractDao {

    public static List<CoreFamilyMemberModel> familyMembersToUpdateLastName(String baseEntityId) {
        String sql = String.format("SELECT DISTINCT fm.entity_type, fm.last_name, fm.base_entity_id,fm.relational_id FROM ec_family_member fm " +
                "INNER JOIN ec_family f on '" + baseEntityId + "' = fm.relational_id " +
                "WHERE f.first_name = fm.last_name");

        DataMap<CoreFamilyMemberModel> dataMap = cursor ->
                new CoreFamilyMemberModel(getCursorValue(cursor, "last_name"),
                        getCursorValue(cursor, "base_entity_id"),
                        getCursorValue(cursor, "relational_id"),
                        getCursorValue(cursor, "entity_type"));

        return readData(sql, dataMap);
    }
}
