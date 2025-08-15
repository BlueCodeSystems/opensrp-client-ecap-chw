package com.bluecodeltd.ecap.chw.application;

import com.bluecodeltd.ecap.chw.util.Constants;

import org.smartregister.chw.core.application.CoreChwApplication;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.family.util.DBConstants;

import java.util.HashMap;
import java.util.Map;

public abstract class DefaultChwApplicationFlv implements ChwApplication.Flavor {
    @Override
    public boolean hasP2P() {
        return true;
    }

    @Override
    public boolean syncUsingPost() {
        return true;
    }

    @Override
    public boolean hasReferrals() {
        return false;
    }

    @Override
    public boolean flvSetFamilyLocation() {
        return false;
    }

    @Override
    public boolean hasANC() {
        return true;
    }

    @Override
    public boolean hasDeliveryKit() {
        return false;
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
    public boolean hasMalaria() {
        return false;
    }

    @Override
    public boolean hasWashCheck() {
        return true;
    }

    @Override
    public boolean hasFamilyKitCheck() {
        return false;
    }

    @Override
    public boolean hasRoutineVisit() {
        return false;
    }

    @Override
    public boolean hasServiceReport() {
        return false;
    }

    @Override
    public boolean hasStockUsageReport() {
        return false;
    }

    @Override
    public boolean hasPinLogin() {
        return false;
    }

    @Override
    public boolean hasReports() {
        return false;
    }

    @Override
    public boolean hasJobAids() {
        return true;
    }

    @Override
    public boolean hasQR() {
        return false;
    }

    @Override
    public boolean hasTasks() {
        return false;
    }

    @Override
    public boolean hasDefaultDueFilterForChildClient() {
        return false;
    }

    public boolean hasJobAidsVitaminAGraph() {
        return true;
    }

    @Override
    public boolean hasJobAidsDewormingGraph() {
        return true;
    }

    @Override
    public boolean hasChildrenMNPSupplementationGraph() {
        return true;
    }

    @Override
    public boolean hasJobAidsBreastfeedingGraph() {
        return true;
    }

    @Override
    public boolean hasJobAidsBirthCertificationGraph() {
        return true;
    }

    @Override
    public boolean hasSurname() {
        return true;
    }

    @Override
    public boolean showMyCommunityActivityReport() {
        return false;
    }

    @Override
    public boolean showChildrenUnder5() {
        return true;
    }

    @Override
    public boolean launchChildClientsAtLogin() {
        return false;
    }

    @Override
    public boolean showNoDueVaccineView() {
        return false;
    }

    @Override
    public boolean useThinkMd() {
        return false;
    }

    @Override
    public boolean hasFamilyLocationRow() {
        return false;
    }

    @Override
    public boolean usesPregnancyRiskProfileLayout() {
        return false;
    }

    @Override
    public boolean getChildFlavorUtil() {
        return false;
    }

    @Override
    public boolean prioritizeChildNameOnChildRegister() {
        return false;
    }

    @Override
    public boolean splitUpcomingServicesView() {
        return false;
    }

    @Override
    public boolean showChildrenUnderFiveAndGirlsAgeNineToEleven() {
        return false;
    }

    @Override
    public boolean dueVaccinesFilterInChildRegister() {
        return false;
    }

    @Override
    public boolean includeCurrentChild() {
        return true;
    }

    @Override
    public boolean saveOnSubmission() {
        return false;
    }

    @Override
    public boolean relaxVisitDateRestrictions() {
        return false;
    }

    @Override
    public boolean showLastNameOnChildProfile() {
        return false;
    }

    @Override
    public boolean showChildrenAboveTwoDueStatus() {
        return true;
    }

    @Override
    public boolean showFamilyServicesScheduleWithChildrenAboveTwo() {
        return true;
    }

    @Override
    public boolean hasForeignData() {
        return false;
    }

    @Override
    public boolean showIconsForChildrenUnderTwoAndGirlsAgeNineToEleven() {
        return false;
    }

    @Override
    public boolean hasMap() {
        return false;
    }

    @Override
    public boolean hasEventDateOnFamilyProfile() {
        return false;
    }

    @Override
    public String[] getFTSTables() {
        return new String[]{CoreConstants.TABLE_NAME.EC_CLIENT_INDEX, Constants.EcapClientTable.EC_MOTHER_INDEX, CoreConstants.TABLE_NAME.EC_HOUSEHOLD, Constants.EcapClientTable.EC_HIV_TESTING_SERVICE,CoreConstants.TABLE_NAME.EC_MOTHER_PMTCT};
    }

    @Override
    public Map<String, String[]> getFTSSearchMap() {
        Map<String, String[]> map = new HashMap<>();


        map.put(Constants.EcapClientTable.EC_CLIENT_INDEX, new String[]{
                DBConstants.KEY.LAST_INTERACTED_WITH,
                DBConstants.KEY.UNIQUE_ID,
                DBConstants.KEY.FIRST_NAME,
                DBConstants.KEY.LAST_NAME,
                "household_id",
                "case_status"
        });

        map.put(Constants.EcapClientTable.EC_MOTHER_INDEX, new String[]{
                DBConstants.KEY.LAST_INTERACTED_WITH,
                "index_check_box",
                "caregiver_name",
                "household_id"
        });

        map.put(Constants.EcapClientTable.EC_HOUSEHOLD, new String[]{
                DBConstants.KEY.LAST_INTERACTED_WITH,
                "index_check_box",
                "caregiver_name",
                "hid",
                "new_caregiver_name"
        });

        map.put(Constants.EcapClientTable.EC_HIV_TESTING_SERVICE, new String[]{
                DBConstants.KEY.LAST_INTERACTED_WITH,
                "first_name",
                "middle_name",
                "last_name",
                "client_number",
                "testing_modality",
                "delete_status"
        });
        map.put(Constants.EcapClientTable.EC_MOTHER_PMTCT, new String[]{
                "mothers_full_name",
                "pmtct_id",
                "household_id",
                "delete_status"
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
                "household_id",
                "case_status"
        });

        map.put(Constants.EcapClientTable.EC_MOTHER_INDEX, new String[]{
                DBConstants.KEY.LAST_INTERACTED_WITH,
                "index_check_box",
                "caregiver_name",
                "household_id"
        });

        map.put(Constants.EcapClientTable.EC_HOUSEHOLD, new String[]{
                DBConstants.KEY.LAST_INTERACTED_WITH,
                "index_check_box",
                "caregiver_name",
                "hid"
        });

        map.put(Constants.EcapClientTable.EC_HIV_TESTING_SERVICE, new String[]{
                DBConstants.KEY.LAST_INTERACTED_WITH,
                "first_name",
                "middle_name",
                "last_name",
                "client_number",
                "testing_modality",
                "delete_status"
        });
        map.put(Constants.EcapClientTable.EC_MOTHER_PMTCT, new String[]{
                "mothers_full_name",
                "pmtct_id",
                "household_id",
                "delete_status"
        });

        return map;
    }

    @Override
    public boolean showsPhysicallyDisabledView() {
        return true;
    }

    @Override
    public ChwApplication chwAppInstance() {
        return (ChwApplication) CoreChwApplication.getInstance();
    }
}
