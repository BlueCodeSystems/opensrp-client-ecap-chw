package com.bluecodeltd.ecap.chw.repository;

import android.content.Context;

import com.bluecodeltd.ecap.chw.application.ChwApplication;
import com.bluecodeltd.ecap.chw.util.ChildDBConstants;
import com.bluecodeltd.ecap.chw.util.ChwDBConstants;
import com.bluecodeltd.ecap.chw.util.RepositoryUtils;
import com.bluecodeltd.ecap.chw.util.RepositoryUtilsFlv;

import net.sqlcipher.database.SQLiteDatabase;

import org.smartregister.chw.anc.repository.VisitDetailsRepository;
import org.smartregister.chw.anc.repository.VisitRepository;
import org.smartregister.chw.core.BuildConfig;
import org.smartregister.chw.core.repository.StockUsageReportRepository;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.family.util.DBConstants;
import org.smartregister.immunization.repository.VaccineRepository;
import org.smartregister.immunization.util.IMDatabaseUtils;
import org.smartregister.reporting.ReportingLibrary;
import org.smartregister.util.DatabaseMigrationUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import timber.log.Timber;

public class ChwRepositoryFlv {
    private static String appVersionCodePref = "APP_VERSION_CODE";

    public static void onUpgrade(Context context, SQLiteDatabase db, int oldVersion, int newVersion) {
        Timber.w(ChwRepository.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        int upgradeTo = oldVersion + 1;
        while (upgradeTo <= newVersion) {
            switch (upgradeTo) {
                case 2:
                    upgradeToVersion2(context, db);
                    break;
                case 3:
                    upgradeToVersion3(db);
                    break;
                case 5:
                    upgradeToVersion5(db);
                    break;
                default:
                    break;
            }
            upgradeTo++;
        }
    }


    private static void upgradeToVersion2(Context context, SQLiteDatabase db) {
        try {
            db.execSQL(VaccineRepository.UPDATE_TABLE_ADD_EVENT_ID_COL);
            db.execSQL(VaccineRepository.EVENT_ID_INDEX);
            db.execSQL(VaccineRepository.UPDATE_TABLE_ADD_FORMSUBMISSION_ID_COL);
            db.execSQL(VaccineRepository.FORMSUBMISSION_INDEX);

            db.execSQL(VaccineRepository.UPDATE_TABLE_ADD_OUT_OF_AREA_COL);
            db.execSQL(VaccineRepository.UPDATE_TABLE_ADD_OUT_OF_AREA_COL_INDEX);

//            EventClientRepository.createTable(db, EventClientRepository.Table.path_reports, EventClientRepository.report_column.values());
            db.execSQL(VaccineRepository.UPDATE_TABLE_ADD_HIA2_STATUS_COL);

        } catch (Exception e) {
            Timber.e(e, "upgradeToVersion2 ");
        }

    }

    private static void upgradeToVersion3(SQLiteDatabase db) {
        try {
            db.execSQL("ALTER TABLE ec_household ADD sub_population VARCHAR");
        } catch (Exception e) {
            Timber.e(e, "upgradeToVersion3 ");
        }
    }

    private static void upgradeToVersion5(SQLiteDatabase db) {
        try {
            db.execSQL("ALTER TABLE ec_household ADD COLUMN last_interacted_with TEXT");

            db.execSQL("ALTER TABLE ec_client_index\n" +
                    "ADD COLUMN abym_years TEXT,\n" +
                    "ADD COLUMN abym_sexually_active TEXT,\n" +
                    "ADD COLUMN abym_preventions TEXT,\n" +
                    "ADD COLUMN abym_preventions_other TEXT,\n" +
                    "ADD COLUMN abym_sex_older_women TEXT,\n" +
                    "ADD COLUMN abym_transactional_sex TEXT,\n" +
                    "ADD COLUMN abym_sex_work TEXT,\n" +
                    "ADD COLUMN abym_economically_insecure TEXT,\n" +
                    "ADD COLUMN abym_violent_partner TEXT,\n" +
                    "ADD COLUMN abym_diagnosed TEXT,\n" +
                    "ADD COLUMN abym_hiv_tested TEXT,\n" +
                    "ADD COLUMN abym_test_positive TEXT,\n" +
                    "ADD COLUMN abym_undergone_vmmc TEXT,\n" +
                    "ADD COLUMN abym_in_school TEXT,\n" +
                    "ADD COLUMN abym_economic_strengthening TEXT");

            db.execSQL("CREATE TABLE IF NOT EXISTS ec_pmtct_mother (\n" +
                    "    base_entity_id TEXT,\n" +
                    "    last_interacted_with TEXT,\n" +
                    "    province TEXT,\n" +
                    "    district TEXT,\n" +
                    "    ward TEXT,\n" +
                    "    facility TEXT,\n" +
                    "    partner TEXT,\n" +
                    "    caseworker_name TEXT,\n" +
                    "    date_enrolled_ecap TEXT,\n" +
                    "    pmtct_id TEXT,\n" +
                    "    ecap_id_question TEXT,\n" +
                    "    household_id TEXT,\n" +
                    "    postnatal_care_visit TEXT,\n" +
                    "    date_enrolled_pmtct TEXT,\n" +
                    "    mothers_full_name TEXT,\n" +
                    "    nick_name TEXT,\n" +
                    "    mothers_age TEXT,\n" +
                    "    date_initiated_on_art TEXT,\n" +
                    "    art_number TEXT,\n" +
                    "    mothers_smh_no TEXT,\n" +
                    "    home_address TEXT,\n" +
                    "    nearest_landmark TEXT,\n" +
                    "    mothers_phone TEXT,\n" +
                    "    date_of_st_contact TEXT,\n" +
                    "    date_of_delivery TEXT,\n" +
                    "    place_of_delivery TEXT,\n" +
                    "    on_art_at_time_of_delivery TEXT,\n" +
                    "    delete_status TEXT\n" +
                    ")");

            db.execSQL("CREATE TABLE IF NOT EXISTS ec_pmtct_mother_child (\n" +
                    "    base_entity_id TEXT,\n" +
                    "    pmtct_id TEXT,\n" +
                    "    unique_id TEXT,\n" +
                    "    infant_first_name TEXT,\n" +
                    "    infant_middle_name TEXT,\n" +
                    "    infant_lastname TEXT,\n" +
                    "    infants_date_of_birth TEXT,\n" +
                    "    infants_sex TEXT,\n" +
                    "    weight_at_birth TEXT,\n" +
                    "    infant_feeding_options TEXT,\n" +
                    "    under_five_clinic_card TEXT,\n" +
                    "    delete_status TEXT\n" +
                    ")");

            db.execSQL("CREATE TABLE IF NOT EXISTS ec_pmtct_mother_postnatal (\n" +
                    "    base_entity_id TEXT,\n" +
                    "    relational_id TEXT,\n" +
                    "    pmtct_id TEXT,\n" +
                    "    date_of_st_post_natal_care TEXT,\n" +
                    "    mother_tested_for_hiv TEXT,\n" +
                    "    postnatal_care_visit TEXT,\n" +
                    "    hiv_test_result_r_nr_at_6_weeks TEXT,\n" +
                    "    art_initiated_at_6_weeks TEXT,\n" +
                    "    art_adherence_counselling_support_at_6_weeks TEXT,\n" +
                    "    family_planning_counselling_at_6_weeks TEXT,\n" +
                    "    comments_at_postnatal_care_visit_6_weeks TEXT,\n" +
                    "    hiv_test_result_r_nr_at_6_months TEXT,\n" +
                    "    art_initiated_at_6_months TEXT,\n" +
                    "    family_planning_counselling_at_6_months TEXT,\n" +
                    "    number_of_condoms_distributed_at_6_months TEXT,\n" +
                    "    comments_at_postnatal_care_visit_6 TEXT,\n" +
                    "    hiv_test_result_r_nr_at_9_weeks TEXT,\n" +
                    "    art_initiated_at_9_weeks TEXT,\n" +
                    "    art_adherence_counselling_support_at_9_weeks TEXT,\n" +
                    "    family_planning_counselling_at_9_weeks TEXT,\n" +
                    "    comments_at_postnatal_care_visit_9_weeks TEXT,\n" +
                    "    hiv_test_result_r_nr_at_9_months TEXT,\n" +
                    "    art_initiated_at_9_months TEXT,\n" +
                    "    family_planning_counselling_at_9_months TEXT,\n" +
                    "    number_of_condoms_distributed_at_9_months TEXT,\n" +
                    "    comments_at_postnatal_care_visit_9 TEXT,\n" +
                    "    hiv_test_result_r_nr_at_12_weeks TEXT,\n" +
                    "    art_initiated_at_12_weeks TEXT,\n" +
                    "    art_adherence_counselling_support_at_12_weeks TEXT,\n" +
                    "    family_planning_counselling_at_12_weeks TEXT,\n" +
                    "    comments_at_postnatal_care_visit_12_weeks TEXT,\n" +
                    "    hiv_test_result_r_nr_at_12_months TEXT,\n" +
                    "    art_initiated_at_12_months TEXT,\n" +
                    "    family_planning_counselling_at_12_months TEXT,\n" +
                    "    number_of_condoms_distributed_at_12_months TEXT,\n" +
                    "    comments_at_postnatal_care_visit_12 TEXT,\n" +
                    "    family_planning_counselling_at_18_months TEXT,\n" +
                    "    number_of_condoms_distributed_at_18_months TEXT,\n" +
                    "    comments_at_postnatal_care_visit_18 TEXT,\n" +
                    "    mothers_outcome TEXT,\n" +
                    "    delete_status TEXT\n" +
                    ")");

            db.execSQL("CREATE TABLE IF NOT EXISTS ec_pmtct_mother_anc (\n" +
                    "    base_entity_id TEXT,\n" +
                    "    relational_id TEXT,\n" +
                    "    pmtct_id TEXT,\n" +
                    "    date_of_st_contact TEXT,\n" +
                    "    gestation_age_in_weeks TEXT,\n" +
                    "    hiv_tested TEXT,\n" +
                    "    date_tested TEXT,\n" +
                    "    result_of_hiv_test TEXT,\n" +
                    "    recency_test_result_if_applicable TEXT,\n" +
                    "    vl_result_at_trimester_1 TEXT,\n" +
                    "    vl_result_at_trimester_2 TEXT,\n" +
                    "    vl_result_at_trimester_3 TEXT,\n" +
                    "    male_partner_tested TEXT,\n" +
                    "    date_male_partner_tested TEXT,\n" +
                    "    result_r_nr TEXT,\n" +
                    "    treatment_initiated TEXT,\n" +
                    "    date_initiated_on_treatment TEXT,\n" +
                    "    on_art_st_anc TEXT,\n" +
                    "    tb_screening TEXT,\n" +
                    "    syphilis_testing TEXT,\n" +
                    "    syphilis_test_type TEXT,\n" +
                    "    syphilis_other TEXT,\n" +
                    "    date_tested_for_syphilis TEXT,\n" +
                    "    syphilis_result TEXT,\n" +
                    "    delete_status TEXT\n" +
                    ")");

        db.execSQL("CREATE TABLE IF NOT EXISTS ec_pmtct_child_monitoring (   \n" +
        "    base_entity_id TEXT,\n" +
        "    pmtct_id TEXT,\n" +
        "    unique_id TEXT,\n" +
        "    child_monitoring_visit TEXT,\n" +
        "    dbs_at_birth_due_date TEXT,\n" +
        "    dbs_at_birth_actual_date TEXT,\n" +
        "    test_result_at_birth TEXT,\n" +
        "    date_tested TEXT,\n" +
        "    nvp_prophylaxis_for_infant TEXT,\n" +
        "    nvp_date_given TEXT,\n" +
        "    _6_weeks_dbs_date TEXT,\n" +
        "    _6_weeks_dbs_ctx TEXT,\n" +
        "    _6_weeks_dbs_hiv_test_p_n TEXT,\n" +
        "    _6_weeks_dbs_iycf_counselling TEXT,\n" +
        "    _6_weeks_infant_feeding_options TEXT,\n" +
        "    _6_weeks_dbs_outcome TEXT,\n" +
        "    _2_months_date TEXT,\n" +
        "    _2_months_hiv_status_p_n TEXT,\n" +
        "    _2_months_ctx TEXT,\n" +
        "    _2_months_iycf_counselling TEXT,\n" +
        "    _2_months_infant_feeding_options TEXT,\n" +
        "    _2_months_outcome TEXT,\n" +
        "    _3_months_date TEXT,\n" +
        "    _3_months_hiv_status_p_n TEXT,\n" +
        "    _3_months_ctx TEXT,\n" +
        "    _3_months_iycf_counselling TEXT,\n" +
        "    _3_months_infant_feeding_options TEXT,\n" +
        "    _3_months_outcome TEXT,\n" +
        "    _4_months_date TEXT,\n" +
        "    _4_months_hiv_status_p_n TEXT,\n" +
        "    _4_months_ctx TEXT,\n" +
        "    _4_months_iycf_counselling TEXT,\n" +
        "    _4_months_infant_feeding_options TEXT,\n" +
        "    _4_months_outcome TEXT,\n" +
        "    _5_months_date TEXT,\n" +
        "    _5_months_hiv_status_p_n TEXT,\n" +
        "    _5_months_ctx TEXT,\n" +
        "    _5_months_iycf_counselling TEXT,\n" +
        "    _5_months_infant_feeding_options TEXT,\n" +
        "    _5_months_outcome TEXT,\n" +
        "    _6_months_date TEXT,\n" +
        "    _6_months_hiv_status_p_n TEXT,\n" +
        "    _6_months_ctx TEXT,\n" +
        "    _6_months_iycf_counselling TEXT,\n" +
        "    _6_months_infant_feeding_options TEXT,\n" +
        "    _6_months_outcome TEXT,\n" +
        "    _7_months_date TEXT,\n" +
        "    _7_months_hiv_status_p_n TEXT,\n" +
        "    _7_months_ctx TEXT,\n" +
        "    _7_months_iycf_counselling TEXT,\n" +
        "    _7_months_infant_feeding_options TEXT,\n" +
        "    _7_months_outcome TEXT,\n" +
        "    _8_months_date TEXT,\n" +
        "    _8_months_hiv_status_p_n TEXT,\n" +
        "    _8_months_ctx TEXT,\n" +
        "    _8_months_iycf_counselling TEXT,\n" +
        "    _8_months_infant_feeding_options TEXT,\n" +
        "    _8_months_outcome TEXT,\n" +
        "    _9_months_date TEXT,\n" +
        "    _9_months_hiv_status_p_n TEXT,\n" +
        "    _9_months_ctx TEXT,\n" +
        "    _9_months_iycf_counselling TEXT,\n" +
        "    _9_months_infant_feeding_options TEXT,\n" +
        "    _9_months_outcome TEXT,\n" +
        "    _10_months_date TEXT,\n" +
        "    _10_months_hiv_status_p_n TEXT,\n" +
        "    _10_months_ctx TEXT,\n" +
        "    _10_months_iycf_counselling TEXT,\n" +
        "    _10_months_infant_feeding_options TEXT,\n" +
        "    _10_months_outcome TEXT,\n" +
        "    _11_months_date TEXT,\n" +
        "    _11_months_hiv_status_p_n TEXT,\n" +
        "    _11_months_ctx TEXT,\n" +
        "    _11_months_iycf_counselling TEXT,\n" +
        "    _11_months_infant_feeding_options TEXT,\n" +
        "    _11_months_outcome TEXT\n" +
        "    _12_months_date TEXT,\n" +
        "    _12_months_hiv_status_p_n TEXT,\n" +
        "    _12_months_ctx TEXT,\n" +
        "    _12_months_iycf_counselling TEXT,\n" +
        "    _12_months_infant_feeding_options TEXT,\n" +
        "    _12_months_outcome TEXT,\n" +
        "    _13_months_date TEXT,\n" +
        "    _13_months_hiv_status_p_n TEXT,\n" +
        "    _13_months_ctx TEXT,\n" +
        "    _13_months_iycf_counselling TEXT,\n" +
        "    _13_months_infant_feeding_options TEXT,\n" +
        "    _13_months_outcome TEXT,\n" +
        "    _14_months_date TEXT,\n" +
        "    _14_months_hiv_status_p_n TEXT,\n" +
        "    _14_months_ctx TEXT,\n" +
        "    _14_months_iycf_counselling TEXT,\n" +
        "    _14_months_infant_feeding_options TEXT,\n" +
        "    _14_months_outcome TEXT,\n" +
        "    _15_months_date TEXT,\n" +
        "    _15_months_hiv_status_p_n TEXT,\n" +
        "    _15_months_ctx TEXT,\n" +
        "    _15_months_iycf_counselling TEXT,\n" +
        "    _15_months_infant_feeding_options TEXT,\n" +
        "    _15_months_outcome TEXT,\n" +
        "    _16_months_date TEXT,\n" +
        "    _16_months_hiv_status_p_n TEXT,\n" +
        "    _16_months_ctx TEXT,\n" +
        "    _16_months_iycf_counselling TEXT,\n" +
        "    _16_months_infant_feeding_options TEXT,\n" +
        "    _16_months_outcome TEXT,\n" +
        "    _17_months_date TEXT,\n" +
        "    _17_months_hiv_status_p_n TEXT,\n" +
        "    _17_months_ctx TEXT,\n" +
        "    _17_months_iycf_counselling TEXT,\n" +
        "    _17_months_infant_feeding_options TEXT,\n" +
        "    _17_months_outcome TEXT,\n" +
        "    _18_months_date TEXT,\n" +
        "    _18_months_hiv_status_p_n TEXT,\n" +
        "    _18_months_ctx TEXT,\n" +
        "    _18_months_iycf_counselling TEXT,\n" +
        "    _18_months_infant_feeding_options TEXT,\n" +
        "    _18_months_outcome TEXT,\n" +
        "    final_outcome TEXT,\n" +
        "    date_referred_for_art_if_hiv_positive TEXT,\n" +
        "    date_enrolled_in_art TEXT,\n" +
        "    delete_status TEXT )\n" +
        "    ");


        } catch (Exception e) {
            Timber.e(e, "upgradeToVersion4 ");
        }
    }



    private static void upgradeToVersion7(SQLiteDatabase db) {
        try {
            for (String query : RepositoryUtilsFlv.UPGRADE_V8) {
                db.execSQL(query);
            }
        } catch (Exception e) {
            Timber.e(e, "upgradeToVersion7 ");
        }
    }

    private static void upgradeToVersion8(SQLiteDatabase db) {
        try {
            for (String query : RepositoryUtilsFlv.UPGRADE_V9) {
                db.execSQL(query);
            }
        } catch (Exception e) {
            Timber.e(e, "upgradeToVersion8 ");
        }
    }

    private static void upgradeToVersion9(SQLiteDatabase db) {
        try {
            VisitRepository.createTable(db);
            VisitDetailsRepository.createTable(db);
        } catch (Exception e) {
            Timber.e(e, "upgradeToVersion9 ");
        }
    }

    private static void upgradeToVersion10(SQLiteDatabase db) {
        try {
            for (String query : RepositoryUtils.UPGRADE_V10) {
                db.execSQL(query);
            }
        } catch (Exception e) {
            Timber.e(e, "upgradeToVersion10 ");
        }
    }

    private static void upgradeToVersion12(SQLiteDatabase db) {
        try {
            // add missing columns
            List<String> columns = new ArrayList<>();
            columns.add(ChildDBConstants.KEY.RELATIONAL_ID);
            DatabaseMigrationUtils.addFieldsToFTSTable(db, ChwApplication.getApplicationFlavor().chwAppInstance().getCommonFtsObject(), CoreConstants.TABLE_NAME.FAMILY_MEMBER, columns);

            // add missing columns
            List<String> child_columns = new ArrayList<>();
            child_columns.add(DBConstants.KEY.DOB);
            child_columns.add(DBConstants.KEY.DATE_REMOVED);
            DatabaseMigrationUtils.addFieldsToFTSTable(db, ChwApplication.getApplicationFlavor().chwAppInstance().getCommonFtsObject(), CoreConstants.TABLE_NAME.CHILD, child_columns);

        } catch (Exception e) {
            Timber.e(e, "upgradeToVersion12 ");
        }
    }

    private static void upgradeToVersion13(SQLiteDatabase db) {
        try {
            // delete possible duplication
            db.execSQL(RepositoryUtils.ADD_MISSING_REPORTING_COLUMN);
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    private static boolean checkIfAppUpdated() {
        String savedAppVersion = ReportingLibrary.getInstance().getContext().allSharedPreferences().getPreference(appVersionCodePref);
        if (savedAppVersion.isEmpty()) {
            return true;
        } else {
            int savedVersion = Integer.parseInt(savedAppVersion);
            return (BuildConfig.VERSION_CODE > savedVersion);
        }
    }

    private static void upgradeToVersion14(SQLiteDatabase db) {
        try {
            StockUsageReportRepository.createTable(db);
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    private static void upgradeToVersion15(SQLiteDatabase db) {
        try {
            String indicatorsConfigFile = "config/indicator-definitions.yml";
            String indicatorDataInitialisedPref = "INDICATOR_DATA_INITIALISED";
            ReportingLibrary reportingLibraryInstance = ReportingLibrary.getInstance();

            boolean indicatorDataInitialised = Boolean.parseBoolean(reportingLibraryInstance.getContext().allSharedPreferences().getPreference(indicatorDataInitialisedPref));
            boolean isUpdated = checkIfAppUpdated();
            if (!indicatorDataInitialised || isUpdated) {
                reportingLibraryInstance.readConfigFile(indicatorsConfigFile, db);
                reportingLibraryInstance.initIndicatorData(indicatorsConfigFile, db); // This will persist the data in the DB
                reportingLibraryInstance.getContext().allSharedPreferences().savePreference(indicatorDataInitialisedPref, "true");
                reportingLibraryInstance.getContext().allSharedPreferences().savePreference(appVersionCodePref, String.valueOf(BuildConfig.VERSION_CODE));
            }

            for (String query : RepositoryUtilsFlv.UPGRADE_V15) {
                db.execSQL(query);
            }
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    private static void upgradeToVersion16(SQLiteDatabase db) {
        try {
            db.execSQL(RepositoryUtils.FAMILY_MEMBER_ADD_REASON_FOR_REGISTRATION);
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    private static void upgradeToVersion17(SQLiteDatabase db) {
        try {
            RepositoryUtils.addDetailsColumnToFamilySearchTable(db);
            String addMissingColumnsQuery = "ALTER TABLE ec_family_member\n" +
                    "    ADD COLUMN has_primary_caregiver VARCHAR;\n" +
                    "ALTER TABLE ec_family_member\n" +
                    "    ADD COLUMN primary_caregiver_name VARCHAR;";
            db.execSQL(addMissingColumnsQuery);
        } catch (Exception e) {
            Timber.e(e, "upgradeToVersion17 ");
        }
    }

    private static void upgradeToVersion18(SQLiteDatabase db) {
        try {
            DatabaseMigrationUtils.createAddedECTables(db,
                    new HashSet<>(Arrays.asList("ec_client_index", "ec_not_yet_done_referral", "ec_family_planning", "ec_sick_child_followup", "ec_malaria_followup_hf", "ec_pnc_danger_signs_outcome", "ec_anc_danger_signs_outcome", "ec_referral", "ec_family_planning_update")),
                    ChwApplication.createCommonFtsObject());
        } catch (Exception e) {
            Timber.e(e, "upgradeToVersion18");
        }
    }

    private static void upgradeToVersion19(SQLiteDatabase db) {
        try {
            RepositoryUtils.addDetailsColumnToFamilySearchTable(db);
            String addMissingColumnsQuery = "ALTER TABLE ec_family_member\n" +
                    " ADD COLUMN primary_caregiver_name VARCHAR;\n";
            db.execSQL(addMissingColumnsQuery);
        } catch (Exception e) {
            Timber.e(e, "upgradeToVersion19");
        }
    }

    private static void upgradeToVersion20(SQLiteDatabase db) {
        try {
            db.execSQL(RepositoryUtils.EC_REFERRAL_ADD_FP_METHOD_COLUMN);
        } catch (Exception e) {
            Timber.e(e, "upgradeToVersion20");
        }
    }

    private static void upgradeToVersion21(SQLiteDatabase db) {
        try {
            db.execSQL("ALTER TABLE ec_family ADD COLUMN event_date VARCHAR; ");
            // add missing columns
        } catch (Exception e) {
            Timber.e(e, "upgradeToVersion21 ");
        }

        try {
            db.execSQL("UPDATE ec_family SET event_date = (select min(eventDate) from event where event.baseEntityId = ec_family.base_entity_id and event.eventType = 'Family Registration') where event_date is null;");
        } catch (Exception e) {
            Timber.e(e, "upgradeToVersion21 ");
        }

    }

    private static void upgradeToVersion22(SQLiteDatabase db) {
        try {
            List<String> columns = new ArrayList<>();
            columns.add(DBConstants.KEY.VILLAGE_TOWN);
            columns.add(ChwDBConstants.NEAREST_HEALTH_FACILITY);
            DatabaseMigrationUtils.addFieldsToFTSTable(db, ChwApplication.getApplicationFlavor().chwAppInstance().getCommonFtsObject(), CoreConstants.TABLE_NAME.FAMILY, columns);

        } catch (Exception e) {
            Timber.e(e, "upgradeToVersion22 ");
        }
    }

    private static void upgradeToVersion23(Context context, SQLiteDatabase db) {
        try {
            db.execSQL(VaccineRepository.UPDATE_TABLE_ADD_IS_VOIDED_COL);
            db.execSQL(VaccineRepository.UPDATE_TABLE_ADD_IS_VOIDED_COL_INDEX);

            IMDatabaseUtils.accessAssetsAndFillDataBaseForVaccineTypes(context, db);
        } catch (Exception e) {
            Timber.e(e);
        }
    }

}
