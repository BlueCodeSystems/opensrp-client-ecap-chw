package com.bluecodeltd.ecap.chw.application;

import org.smartregister.chw.core.utils.ChildDBConstants;
import org.smartregister.chw.core.utils.CoreConstants;
import com.bluecodeltd.ecap.chw.util.ChwDBConstants;
import com.bluecodeltd.ecap.chw.util.Constants;

import org.smartregister.family.util.DBConstants;

import java.util.HashMap;
import java.util.Map;

public class ChwApplicationFlv extends DefaultChwApplicationFlv {
    @Override
    public boolean hasP2P() {
        return false;
    }

    @Override
    public boolean hasReferrals() {
        return false;
    }

    @Override
    public boolean flvSetFamilyLocation() {
        return true;
    }

    @Override
    public boolean hasANC() {
        return true;
    }

    @Override
    public boolean hasPNC() {
        return true;
    }

    @Override
    public boolean hasChildSickForm() {
        return false;
    }

    @Override
    public boolean hasFamilyPlanning() {
        return false;
    }

    @Override
    public boolean hasWashCheck() {
        return false;
    }

    @Override
    public boolean hasMalaria() {
        return false;
    }

    @Override
    public boolean hasServiceReport() {
        return false;
    }

    public boolean hasQR() {
        return false;
    }

    @Override
    public boolean hasJobAids() {
        return false;
    }

    @Override
    public boolean hasTasks() {
        return false;
    }

    @Override
    public boolean hasStockUsageReport() {
        return false;
    }

    @Override
    public boolean hasFamilyLocationRow() {
        return false;
    }

    @Override
    public boolean usesPregnancyRiskProfileLayout() {
        return true;
    }

    public boolean getChildFlavorUtil() {
        return true;
    }

    @Override
    public boolean includeCurrentChild() {
        return true;
    }

    @Override
    public boolean hasMap() {
        return true;
    }

    @Override
    public boolean hasEventDateOnFamilyProfile() {
        return true;
    }

    @Override
    public String[] getFTSTables() {
        return new String[]{CoreConstants.TABLE_NAME.EC_CLIENT_INDEX, CoreConstants.TABLE_NAME.EC_MOTHER_INDEX, Constants.EcapClientTable.EC_HOUSEHOLD};
    }

    @Override
    public Map<String, String[]> getFTSSearchMap() {
        Map<String, String[]> map = new HashMap<>();

        map.put(Constants.EcapClientTable.EC_CLIENT_INDEX, new String[]{
                DBConstants.KEY.LAST_INTERACTED_WITH,
                DBConstants.KEY.FIRST_NAME,
                DBConstants.KEY.LAST_NAME,
                DBConstants.KEY.UNIQUE_ID,
                "household_id"
        });

        map.put(Constants.EcapClientTable.EC_MOTHER_INDEX, new String[]{
                "caregiver_name",
                "ec_household.household_id"

        });

        map.put(Constants.EcapClientTable.EC_HOUSEHOLD, new String[]{
                "hid",
                "caregiver_name",
                "household_id"
        });



        return map;
    }

    @Override
    public Map<String, String[]> getFTSSortMap() {
        Map<String, String[]> map = new HashMap<>();

        map.put(Constants.EcapClientTable.EC_CLIENT_INDEX, new String[]{
                DBConstants.KEY.LAST_INTERACTED_WITH,
                DBConstants.KEY.UNIQUE_ID,
                DBConstants.KEY.FIRST_NAME,
                DBConstants.KEY.LAST_NAME,
                "household_id"
        });

        map.put(Constants.EcapClientTable.EC_MOTHER_INDEX, new String[]{
                DBConstants.KEY.LAST_INTERACTED_WITH,
                "caregiver_name",
                "household_id"
        });

        map.put(Constants.EcapClientTable.EC_HOUSEHOLD, new String[]{
                DBConstants.KEY.LAST_INTERACTED_WITH,
                "caregiver_name",
                "household_id"
        });
        return map;
    }

    @Override
    public boolean showsPhysicallyDisabledView() { return false; }
}
