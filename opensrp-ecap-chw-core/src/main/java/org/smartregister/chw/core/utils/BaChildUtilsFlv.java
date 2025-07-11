package org.smartregister.chw.core.utils;

import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;
import org.smartregister.family.util.DBConstants;

import java.util.ArrayList;
import java.util.Arrays;

public class BaChildUtilsFlv implements CoreChildUtils.Flavor {

    public static String mainSelect(String tableName, String familyTableName, String familyMemberTableName, String mainCondition) {
        return mainSelectRegisterWithoutGroupby(tableName, familyTableName, familyMemberTableName, tableName + "." + DBConstants.KEY.BASE_ENTITY_ID + " = '" + mainCondition + "'");
    }

    public static String mainSelectRegisterWithoutGroupby(String tableName, String familyTableName, String familyMemberTableName, String mainCondition) {
        SmartRegisterQueryBuilder queryBuilder = new SmartRegisterQueryBuilder();
        queryBuilder.selectInitiateMainTable(tableName, mainColumns(tableName, familyTableName, familyMemberTableName));
        queryBuilder.customJoin("LEFT JOIN " + familyTableName + " ON  " + tableName + "." + DBConstants.KEY.RELATIONAL_ID + " = " + familyTableName + ".id COLLATE NOCASE ");
        queryBuilder.customJoin("LEFT JOIN " + familyMemberTableName + " ON  " + familyMemberTableName + "." + DBConstants.KEY.BASE_ENTITY_ID + " = " + familyTableName + ".primary_caregiver COLLATE NOCASE ");

        return queryBuilder.mainCondition(mainCondition);
    }

    public static String[] mainColumns(String tableName, String familyTable, String familyMemberTable) {
            ArrayList<String> columnList = new ArrayList<>();

            columnList.add(tableName + "." + ChildDBConstants.KEY.INSURANCE_PROVIDER);
            columnList.add(tableName + "." + ChildDBConstants.KEY.INSURANCE_PROVIDER_NUMBER);
            columnList.add(tableName + "." + ChildDBConstants.KEY.INSURANCE_PROVIDER_OTHER);
            columnList.add(tableName + "." + ChildDBConstants.KEY.TYPE_OF_DISABILITY);
            columnList.add(tableName + "." + ChildDBConstants.KEY.RHC_CARD);
            columnList.add(tableName + "." + ChildDBConstants.KEY.NUTRITION_STATUS);
            columnList.addAll(Arrays.asList(CoreChildUtils.mainColumns(tableName, familyTable, familyMemberTable)));

            return columnList.toArray(new String[columnList.size()]);
        }

        @Override
        public String[] getOneYearVaccines() {
            return new String[]{"bcg", "opv1", "penta1", "pcv1", "rota1", "opv2", "penta2", "pcv2", "rota2", "opv3", "penta3", "pcv3", "ipv", "mr1"};
        }

        @Override
        public String[] getTwoYearVaccines() {
            return new String[]{"bcg", "opv1", "penta1", "pcv1", "rota1", "opv2", "penta2", "pcv2", "rota2", "opv3", "penta3", "pcv3", "ipv", "mr1", "mr2"};
        }
    }

