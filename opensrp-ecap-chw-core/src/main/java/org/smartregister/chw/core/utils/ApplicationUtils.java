package org.smartregister.chw.core.utils;

import org.smartregister.commonregistry.CommonFtsObject;
import org.smartregister.family.util.DBConstants;

public class ApplicationUtils {
    public static CommonFtsObject getCommonFtsObject(CommonFtsObject commonFtsObject) {
        CommonFtsObject ftsObject;
        if (commonFtsObject == null) {
            ftsObject = new CommonFtsObject(getFtsTables());
            for (String ftsTable : ftsObject.getTables()) {
                ftsObject.updateSearchFields(ftsTable, getFtsSearchFields(ftsTable));
                ftsObject.updateSortFields(ftsTable, getFtsSortFields(ftsTable));
            }
        } else {
            ftsObject = commonFtsObject;
        }
        return ftsObject;
    }

    private static String[] getFtsTables() {
        return new String[]{CoreConstants.TABLE_NAME.EC_CLIENT_INDEX, CoreConstants.TABLE_NAME.EC_MOTHER_INDEX, CoreConstants.TABLE_NAME.EC_HOUSEHOLD};
    }

    private static String[] getFtsSearchFields(String tableName) {
        switch (tableName) {
            case CoreConstants.TABLE_NAME.EC_CLIENT_INDEX:
                return new String[]{
                        DBConstants.KEY.LAST_INTERACTED_WITH,
                        DBConstants.KEY.FIRST_NAME,
                        DBConstants.KEY.LAST_NAME, DBConstants.KEY.UNIQUE_ID, "case_status","deleted"
                };
            case CoreConstants.TABLE_NAME.EC_MOTHER_INDEX:
                return new String[]{
                        "caregiver_name", "household_id"
                };
            case CoreConstants.TABLE_NAME.EC_HOUSEHOLD:
                return new String[]{
                        "index_check_box", "caregiver_name", "id","status"
                };
            case CoreConstants.TABLE_NAME.EC_HIV_TESTING_SERVICE:
                return new String[]{
                        "delete_status"
                };
            default:
                return null;
        }
    }

    private static String[] getFtsSortFields(String tableName) {
        switch (tableName) {
            case CoreConstants.TABLE_NAME.EC_CLIENT_INDEX:
                return new String[]{"last_interacted_with","case_status"};

            case CoreConstants.TABLE_NAME.EC_MOTHER_INDEX:
                return new String[]{
                        "caregiver_name", "household_id"
                };
            case CoreConstants.TABLE_NAME.EC_HOUSEHOLD:
                return new String[]{"index_check_box","caregiver_name, hid"};
            case CoreConstants.TABLE_NAME.EC_HIV_TESTING_SERVICE:
                return new String[]{"delete_status"};
            default:
                return null;
        }
    }
}
