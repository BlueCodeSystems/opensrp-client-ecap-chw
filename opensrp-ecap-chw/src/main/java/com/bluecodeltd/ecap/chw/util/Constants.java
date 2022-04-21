package com.bluecodeltd.ecap.chw.util;

import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.Utils;

public class Constants extends CoreConstants {
    public static final String REFERRAL_TASK_FOCUS = "referral_task_focus";
    public static final String REFERRAL_TYPES = "ReferralTypes";
    public static final String APP_VERSION = "app_version";
    public static final String DB_VERSION = "db_version";
    public static final String MALARIA_REFERRAL_FORM = "malaria_referral_form";
    public static final String ALL_CLIENT_REGISTRATION_FORM = "all_clients_registration_form";
    public static final String JSON = "json";
    public static final String SCREENING = "VCA Screening";
    public static final String SEX_WORKER = "Sex Worker";
    public static final String MOTHER = "Mother";
    public static final String METADATA = "metadata";
    public static String pregnancyOutcome = "preg_outcome";
    public static String INITIAL_LEVEL_FACILITY_FORM ="initial_level_facility_form";
    public static String FAMILY_MEMBER_LOCATION_TABLE = "ec_family_member_location";
    public static String CHILD_OVER_5 = "child_over_5";

    public enum FamilyRegisterOptionsUtil {Miscarriage, Other}

    public enum FamilyMemberType {ANC, PNC, Other}

    public static class FORM_SUBMISSION_FIELD {
        public static String pncHfNextVisitDateFieldType = "pnc_hf_next_visit_date";

    }

    public static class JSON_FORM_KEY {
        public static final String ENTITY_ID = "entity_id";
        public static final String OPTIONS = "options";
        public static final String ENCOUNTER_LOCATION = "encounter_location";
        public static final String ATTRIBUTES = "attributes";
        public static final String DEATH_DATE = "deathdate";
        public static final String DEATH_DATE_APPROX = "deathdateApprox";
        public static final String UNIQUE_ID = "unique_id";
        public static final String FAMILY_NAME = "fam_name";
        public static final String LAST_INTERACTED_WITH = "last_interacted_with";
        public static final String DOB = "dob";
        public static final String DOB_UNKNOWN = "dob_unknown";
        public static final String AGE = "age";

    }

    public static class EncounterType {
        public static final String SICK_CHILD = "Sick Child Referral";
        public static final String PNC_REFERRAL = "PNC Referral";
        public static final String ANC_REFERRAL = "ANC Referral";
        public static final String PNC_CHILD_REGISTRATION = "PNC Child Registration";
    }

    public static class ChildIllnessViewType {
        public static final int RADIO_BUTTON = 0;
        public static final int EDIT_TEXT = 1;
        public static final int CHECK_BOX = 2;
    }

    public static class ReportParameters {
        public static String COMMUNITY = "COMMUNITY";
        public static String COMMUNITY_ID = "COMMUNITY_ID";
        public static String REPORT_DATE = "REPORT_DATE";
        public static String INDICATOR_CODE = "INDICATOR_CODE";
    }

    public static class PeerToPeerUtil {
        public static String COUNTRY_ID = "COUNTRY_ID";
    }

    public static class AncHomeVisitUtil {
        private static final String DELIVERY_KIT_RECEIVED = "anc_woman_delivery_kit_received";

        public static String getDeliveryKitReceived() {
            return Utils.getLocalForm(DELIVERY_KIT_RECEIVED, JSON_FORM.locale, JSON_FORM.assetManager);
        }
    }

    public interface EcapEncounterType {
        String CHILD_INDEX = "Sub Population";
        String FSW = "Female Sex Worker";
        String VCA_ASSESSMENT = "VCA Assessment";
        String CACE_STATUS = "Case Record Status";
        String MOTHER_INDEX = "Mother Register";
        String HOUSEHOLD_INDEX = "Household Screening";
        String IDENTIFICATION = "identification";
        String GRADUATION_ASSESSMENT = "OVC Graduation Assessment";
    }

    public interface EcapClientTable {
        String EC_CLIENT_INDEX = "ec_client_index";
        String EC_FAMILY = "ec_family";
        String EC_HOUSEHOLD = "ec_household";
        String EC_VCA_CASE_PLAN = "ec_vca_case_plan";
        String EC_VCA_CASE_PLAN_DOMAIN = "ec_vca_case_plan_domain";
        String EC_GRAD = "ec_grad";
        String EC_CAREGIVER_PLAN_DOMAIN = "ec_caregiver_case_plan_domain";
        String EC_MOTHER_INDEX = "ec_mother_index";
        String EC_POPULATION = "ec_sub_population";
        String EC_CLIENT_IDENTIFICATION = "ec_client_identification";
        String EC_SERVICE_REPORT = "ec_service_report";
        String EC_REFERRAL = "ec_referral";
        String EC_CAREGIVER_CASEPLAN = "ec_caregiver_case_plan";
        String EC_ASSESSMENT = "ec_vca_assessment";
        String EC_OVC_GRADUATION = "ec_ovc_graduation";
        String EC_HOUSEHOLD_CAREGIVER = "ec_household_visitation_for_caregiver";
        String EC_HOUSEHOLD_VCA = "ec_household_visitation_for_vca_0_20_years";
        String EC_GRADUATION_SUB = "ec_graduation_assessment_sub_form";
        String EC_CAREGIVER_HOUSEHOLD_ASSESSMENT = "ec_caregiver_household_assessment";
        String EC_HIV_ASSESSMENT_ABOVE_15 = "ec_hiv_assessment_above_15";
        String EC_HIV_ASSESSMENT_BELOW_15 = "ec_hiv_assessment_below_15";
        String EC_CAREGIVER_VISITATION = "ec_household_visitation_for_caregiver";
        String EC_CAREGIVER_HIV_ASSESSMENT = "ec_caregiver_hiv_assessment";
    }
}