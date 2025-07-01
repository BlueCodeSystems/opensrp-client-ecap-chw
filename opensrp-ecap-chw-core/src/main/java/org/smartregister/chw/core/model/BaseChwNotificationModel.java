package org.smartregister.chw.core.model;

import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.family.util.DBConstants;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public abstract class BaseChwNotificationModel {

    protected String[] mainColumns(String tableName, String entityTable) {
        Set<String> columns =
                new HashSet<>(Arrays.asList(tableName + "." + CoreConstants.DB_CONSTANTS.FOCUS, tableName + "." +
                        CoreConstants.DB_CONSTANTS.REQUESTER, tableName + "." + CoreConstants.DB_CONSTANTS.START, tableName + "." + CoreConstants.DB_CONSTANTS.LAST_MODIFIED));
        addClientDetails(entityTable, columns);
        addTaskDetails(columns);
        return columns.toArray(new String[]{});
    }

    private void addClientDetails(String table, Set<String> columns) {
        columns.add(table + "." + "relational_id as relationalid");
        columns.add(table + "." + DBConstants.KEY.BASE_ENTITY_ID);
        columns.add(table + "." + DBConstants.KEY.FIRST_NAME);
        columns.add(table + "." + DBConstants.KEY.MIDDLE_NAME);
        columns.add(table + "." + DBConstants.KEY.LAST_NAME);
        columns.add(table + "." + DBConstants.KEY.DOB);
        columns.add(table + "." + DBConstants.KEY.GENDER);
        columns.add(CoreConstants.TABLE_NAME.FAMILY + "." + DBConstants.KEY.FAMILY_HEAD);

    }

    private void addTaskDetails(Set<String> columns) {
        columns.add(CoreConstants.TABLE_NAME.TASK + "." + CoreConstants.DB_CONSTANTS.FOCUS);
        columns.add(CoreConstants.TABLE_NAME.TASK + "." + CoreConstants.DB_CONSTANTS.OWNER);
        columns.add(CoreConstants.TABLE_NAME.TASK + "." + CoreConstants.DB_CONSTANTS.REQUESTER);
        columns.add(CoreConstants.TABLE_NAME.TASK + "." + CoreConstants.DB_CONSTANTS.START);
        columns.add(CoreConstants.TABLE_NAME.TASK + "." + CoreConstants.DB_CONSTANTS.LAST_MODIFIED);
    }
}

