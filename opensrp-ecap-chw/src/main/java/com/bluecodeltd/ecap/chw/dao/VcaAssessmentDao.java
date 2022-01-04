package com.bluecodeltd.ecap.chw.dao;

import com.bluecodeltd.ecap.chw.model.Household;
import com.bluecodeltd.ecap.chw.model.VcaAssessmentModel;

import org.smartregister.dao.AbstractDao;

import java.util.List;

public class VcaAssessmentDao extends AbstractDao {
    public static VcaAssessmentModel getVcaAssessmentModel (String baseEntityID) {

        String sql = "SELECT ec_vca_assessment.* WHERE ec_vca_assessment.baseEntityID = '" + baseEntityID + "' ";
        //String sql = "SELECT * FROM ec_household WHERE household_id = '" + householdID + "' ";


        return null;
    }

}
