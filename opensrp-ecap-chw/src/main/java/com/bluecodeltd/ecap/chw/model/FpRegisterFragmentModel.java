package com.bluecodeltd.ecap.chw.model;

import androidx.annotation.NonNull;

import org.smartregister.chw.fp.model.BaseFpRegisterFragmentModel;
import org.smartregister.chw.fp.util.FamilyPlanningConstants;
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;
import org.smartregister.family.util.DBConstants;

public class FpRegisterFragmentModel extends BaseFpRegisterFragmentModel {

    @NonNull
    @Override
    public String mainSelect(@NonNull String tableName, @NonNull String mainCondition) {
        SmartRegisterQueryBuilder queryBuilder = new SmartRegisterQueryBuilder();
        queryBuilder.selectInitiateMainTable(tableName, mainColumns(tableName));
        queryBuilder.customJoin("INNER JOIN " + FamilyPlanningConstants.DBConstants.FAMILY_MEMBER + " ON  " + tableName + "." + FamilyPlanningConstants.DBConstants.BASE_ENTITY_ID + " = " + FamilyPlanningConstants.DBConstants.FAMILY_MEMBER + "." + FamilyPlanningConstants.DBConstants.BASE_ENTITY_ID + " COLLATE NOCASE ");
        queryBuilder.customJoin("INNER JOIN " + FamilyPlanningConstants.DBConstants.FAMILY + " ON  " + FamilyPlanningConstants.DBConstants.FAMILY_MEMBER + "." + FamilyPlanningConstants.DBConstants.RELATIONAL_ID + " = " + FamilyPlanningConstants.DBConstants.FAMILY + "." + FamilyPlanningConstants.DBConstants.BASE_ENTITY_ID);
        queryBuilder.customJoin("LEFT JOIN (select base_entity_id , max(visit_date) visit_date from visits GROUP by base_entity_id) VISIT_SUMMARY ON VISIT_SUMMARY.base_entity_id = " + tableName + "." + DBConstants.KEY.BASE_ENTITY_ID);

        return queryBuilder.mainCondition(mainCondition);
    }

    private final java.util.Map<String, String> additionalFields = new java.util.HashMap<>();

    public java.util.Map<String, String> getAdditionalFields() {
        return additionalFields;
    }

    public String getAdditionalField(String key) {
        if (key == null) return null;
        return additionalFields.get(key);
    }

    public void setAdditionalField(String key, String value) {
        if (key == null) return;
        additionalFields.put(key, value);
    }
}

