package com.bluecodeltd.ecap.chw.repository;

import android.content.Context;

import com.bluecodeltd.ecap.chw.BuildConfig;
import com.bluecodeltd.ecap.chw.application.ChwApplication;
import com.bluecodeltd.ecap.chw.util.ChildDBConstants;
import com.bluecodeltd.ecap.chw.util.ChwDBConstants;
import com.bluecodeltd.ecap.chw.util.RepositoryUtils;
import com.bluecodeltd.ecap.chw.util.RepositoryUtilsFlv;

import net.sqlcipher.database.SQLiteDatabase;

import org.smartregister.chw.anc.repository.VisitDetailsRepository;
import org.smartregister.chw.anc.repository.VisitRepository;
import org.smartregister.chw.core.repository.StockUsageReportRepository;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.family.util.DBConstants;
import org.smartregister.immunization.repository.VaccineRepository;
import org.smartregister.immunization.util.IMDatabaseUtils;
import org.smartregister.reporting.ReportingLibrary;
import org.smartregister.util.DatabaseMigrationUtils;

import java.io.File;
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
                    upgradeToVersion2(db);
                    break;
                case 3:
                    upgradeToVersion3(db);
                    break;
                case 4:
                    upgradeToVersion4(db);
                    break;
                case 5:
                    upgradeToVersion5(db);
                    break;
                case 6:
                    upgradeToVersion6(db);
                    break;
                case 7:
                    upgradeToVersion7(db);
                    break;
                case 8:
                    upgradeToVersion8(db);
                    break;
                case 9:
                    upgradeToVersion9(db);
                    break;
                case 10:
                    upgradeToVersion10(db);
                    break;
                case 12:
                    upgradeToVersion12(db);
                    break;
                case 13:
                    upgradeToVersion13(db);
                    break;
                case 14:
                    upgradeToVersion14(db);
                    break;
                case 15:
                    upgradeToVersion15(db);
                    break;
                case 16:
                    upgradeToVersion16(db);
                    break;
                case 17:
                    upgradeToVersion17(db);
                    break;
                case 18:
                    upgradeToVersion18(db);
                    break;
                case 19:
                    upgradeToVersion19(db);
                    break;
                case 20:
                    upgradeToVersion20(db);
                    break;
                case 21:
                    upgradeToVersion21(db);
                    break;
                case 22:
                    upgradeToVersion22(db);
                    break;
                case 23:
                    upgradeToVersion23(context, db);
                    break;
                case 24:
                    upgradeToVersion24(db);
                    break;
                case 25:
                    upgradeToVersion25(db);
                    break;
                default:
                    break;
            }
            upgradeTo++;
        }
    }


    private static void upgradeToVersion2(SQLiteDatabase db) {
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
    private static void upgradeToVersion4(SQLiteDatabase db) {
        try {
            String sqlCreateTableHivTestingService = "CREATE TABLE IF NOT EXISTS ec_hiv_testing_service (" +
                    "base_entity_id TEXT, " +
                    "relational_id TEXT, " +
                    "last_interacted_with TEXT, " +
                    "caseworker_name TEXT, " +
                    "phone TEXT, " +
                    "implementing_partner TEXT, " +
                    "health_facility TEXT, " +
                    "district TEXT, " +
                    "province TEXT, " +
                    "client_number TEXT, " +
                    "testing_modality TEXT, " +
                    "first_name TEXT, " +
                    "middle_name TEXT, " +
                    "last_name TEXT, " +
                    "art_number TEXT, " +
                    "gender TEXT, " +
                    "birthdate TEXT, " +
                    "entry_point TEXT, " +
                    "ecap_id TEXT, " +
                    "sub_population TEXT, " +
                    "address TEXT, " +
                    "landmark TEXT, " +
                    "contact_phone TEXT, " +
                    "hiv_status TEXT, " +
                    "date_tested TEXT, " +
                    "hiv_result TEXT, " +
                    "test_done_hf TEXT, " +
                    "hiv_recent_test TEXT, " +
                    "art_date TEXT, " +
                    "art_date_initiated TEXT, " +
                    "comment TEXT, " +
                    "checked_by TEXT, " +
                    "delete_status TEXT, " +
                    "date_client_created TEXT, " +
                    "date_edited TEXT" +
                    ")";

            db.execSQL(sqlCreateTableHivTestingService);

            String sqlCreateTableHivTestingLinks = "CREATE TABLE IF NOT EXISTS ec_hiv_testing_links (" +
                    "base_entity_id TEXT, " +
                    "relational_id TEXT, " +
                    "client_number TEXT, " +
                    "date_linked TEXT, " +
                    "first_name TEXT, " +
                    "middle_name TEXT, " +
                    "last_name TEXT, " +
                    "ecap_id TEXT, " +
                    "sub_population TEXT, " +
                    "birthdate TEXT, " +
                    "relationship TEXT, " +
                    "other_relationship TEXT, " +
                    "address TEXT, " +
                    "landmark TEXT, " +
                    "phone TEXT, " +
                    "hiv_status TEXT, " +
                    "date_tested TEXT, " +
                    "hiv_result TEXT, " +
                    "test_done_hf TEXT, " +
                    "hiv_recent_test TEXT, " +
                    "art_date TEXT, " +
                    "art_date_initiated TEXT, " +
                    "comment TEXT, " +
                    "caseworker_name TEXT, " +
                    "checked_by TEXT, " +
                    "delete_status TEXT" +
                    ")";

            db.execSQL(sqlCreateTableHivTestingLinks);



        } catch (Exception e) {
            Timber.e(e, "upgradeToVersion4 ");
        }
    }

    private static void upgradeToVersion5(SQLiteDatabase db) {
        try {
            db.execSQL("ALTER TABLE ec_household ADD COLUMN last_interacted_with TEXT");
            db.execSQL("ALTER TABLE ec_client_index ADD COLUMN household_location");
            db.execSQL("ALTER TABLE ec_household_visitation_for_vca_0_20_years ADD COLUMN vca_visit_location");
            db.execSQL("ALTER TABLE ec_household_visitation_for_caregiver ADD COLUMN visit_location");
            db.execSQL("ALTER TABLE ec_referral ADD COLUMN referral_location");

            db.execSQL("ALTER TABLE ec_client_index ADD COLUMN abym_years TEXT");
            db.execSQL("ALTER TABLE ec_client_index ADD COLUMN abym_sexually_active TEXT");
            db.execSQL("ALTER TABLE ec_client_index ADD COLUMN abym_preventions TEXT");
            db.execSQL("ALTER TABLE ec_client_index ADD COLUMN abym_preventions_other TEXT");
            db.execSQL("ALTER TABLE ec_client_index ADD COLUMN abym_sex_older_women TEXT");
            db.execSQL("ALTER TABLE ec_client_index ADD COLUMN abym_transactional_sex TEXT");
            db.execSQL("ALTER TABLE ec_client_index ADD COLUMN abym_sex_work TEXT");
            db.execSQL("ALTER TABLE ec_client_index ADD COLUMN abym_economically_insecure TEXT");
            db.execSQL("ALTER TABLE ec_client_index ADD COLUMN abym_violent_partner TEXT");
            db.execSQL("ALTER TABLE ec_client_index ADD COLUMN abym_diagnosed TEXT");
            db.execSQL("ALTER TABLE ec_client_index ADD COLUMN abym_hiv_tested TEXT");
            db.execSQL("ALTER TABLE ec_client_index ADD COLUMN abym_test_positive TEXT");
            db.execSQL("ALTER TABLE ec_client_index ADD COLUMN abym_undergone_vmmc TEXT");
            db.execSQL("ALTER TABLE ec_client_index ADD COLUMN abym_in_school TEXT");
            db.execSQL("ALTER TABLE ec_client_index ADD COLUMN abym_economic_strengthening TEXT");

            db.execSQL("ALTER TABLE ec_graduation ADD COLUMN enrollment_date TEXT");
            db.execSQL("ALTER TABLE ec_graduation ADD COLUMN asmt TEXT");
            db.execSQL("ALTER TABLE ec_graduation ADD COLUMN caregiver_name TEXT");
            db.execSQL("ALTER TABLE ec_graduation ADD COLUMN caregiver_sex TEXT");
            db.execSQL("ALTER TABLE ec_graduation ADD COLUMN caregiver_birth_date TEXT");
            db.execSQL("ALTER TABLE ec_graduation ADD COLUMN enrollment_date TEXT");

            String sql = "CREATE TABLE IF NOT EXISTS ec_pmtct_mother (" +
                    "base_entity_id TEXT, " +
                    "last_interacted_with TEXT, " +
                    "province TEXT, " +
                    "district TEXT, " +
                    "ward TEXT, " +
                    "facility TEXT, " +
                    "partner TEXT, " +
                    "caseworker_name TEXT, " +
                    "date_enrolled_ecap TEXT, " +
                    "pmtct_id TEXT, " +
                    "ecap_id_question TEXT, " +
                    "household_id TEXT, " +
                    "postnatal_care_visit TEXT, " +
                    "date_enrolled_pmtct TEXT, " +
                    "mothers_full_name TEXT, " +
                    "nick_name TEXT, " +
                    "mothers_age TEXT, " +
                    "date_initiated_on_art TEXT, " +
                    "art_number TEXT, " +
                    "mothers_smh_no TEXT, " +
                    "home_address TEXT, " +
                    "nearest_landmark TEXT, " +
                    "mothers_phone TEXT, " +
                    "date_of_st_contact TEXT, " +
                    "date_of_delivery TEXT, " +
                    "place_of_delivery TEXT, " +
                    "on_art_at_time_of_delivery TEXT, " +
                    "delete_status TEXT)";
            db.execSQL(sql);

            String sqlCreateTable = "CREATE TABLE IF NOT EXISTS ec_pmtct_mother_child (" +
                    "base_entity_id TEXT, " +
                    "pmtct_id TEXT, " +
                    "unique_id TEXT, " +
                    "infant_first_name TEXT, " +
                    "infant_middle_name TEXT, " +
                    "infant_lastname TEXT, " +
                    "infants_date_of_birth TEXT, " +
                    "infants_sex TEXT, " +
                    "weight_at_birth TEXT, " +
                    "infant_feeding_options TEXT, " +
                    "under_five_clinic_card TEXT, " +
                    "delete_status TEXT)";

            db.execSQL(sqlCreateTable);

            String sqlCreatePostnatal = "CREATE TABLE IF NOT EXISTS ec_pmtct_mother_postnatal (" +
                    "base_entity_id TEXT, " +
                    "relational_id TEXT, " +
                    "pmtct_id TEXT, " +
                    "date_of_st_post_natal_care TEXT, " +
                    "mother_tested_for_hiv TEXT, " +
                    "postnatal_care_visit TEXT, " +
                    "hiv_test_result_r_nr_at_6_weeks TEXT, " +
                    "art_initiated_at_6_weeks TEXT, " +
                    "art_adherence_counselling_support_at_6_weeks TEXT, " +
                    "family_planning_counselling_at_6_weeks TEXT, " +
                    "comments_at_postnatal_care_visit_6_weeks TEXT, " +
                    "hiv_test_result_r_nr_at_6_months TEXT, " +
                    "art_initiated_at_6_months TEXT, " +
                    "family_planning_counselling_at_6_months TEXT, " +
                    "number_of_condoms_distributed_at_6_months TEXT, " +
                    "comments_at_postnatal_care_visit_6 TEXT, " +
                    "hiv_test_result_r_nr_at_9_weeks TEXT, " +
                    "art_initiated_at_9_weeks TEXT, " +
                    "art_adherence_counselling_support_at_9_weeks TEXT, " +
                    "family_planning_counselling_at_9_weeks TEXT, " +
                    "comments_at_postnatal_care_visit_9_weeks TEXT, " +
                    "hiv_test_result_r_nr_at_9_months TEXT, " +
                    "art_initiated_at_9_months TEXT, " +
                    "family_planning_counselling_at_9_months TEXT, " +
                    "number_of_condoms_distributed_at_9_months TEXT, " +
                    "comments_at_postnatal_care_visit_9 TEXT, " +
                    "hiv_test_result_r_nr_at_12_weeks TEXT, " +
                    "art_initiated_at_12_weeks TEXT, " +
                    "art_adherence_counselling_support_at_12_weeks TEXT, " +
                    "family_planning_counselling_at_12_weeks TEXT, " +
                    "comments_at_postnatal_care_visit_12_weeks TEXT, " +
                    "hiv_test_result_r_nr_at_12_months TEXT, " +
                    "art_initiated_at_12_months TEXT, " +
                    "family_planning_counselling_at_12_months TEXT, " +
                    "number_of_condoms_distributed_at_12_months TEXT, " +
                    "comments_at_postnatal_care_visit_12 TEXT, " +
                    "family_planning_counselling_at_18_months TEXT, " +
                    "number_of_condoms_distributed_at_18_months TEXT, " +
                    "comments_at_postnatal_care_visit_18 TEXT, " +
                    "mothers_outcome TEXT, " +
                    "delete_status TEXT)";

            db.execSQL(sqlCreatePostnatal);

            String sqlCreateTableANC = "CREATE TABLE IF NOT EXISTS ec_pmtct_mother_anc (" +
                    "base_entity_id TEXT, " +
                    "relational_id TEXT, " +
                    "pmtct_id TEXT, " +
                    "date_of_st_contact TEXT, " +
                    "gestation_age_in_weeks TEXT, " +
                    "hiv_tested TEXT, " +
                    "date_tested TEXT, " +
                    "result_of_hiv_test TEXT, " +
                    "recency_test_result_if_applicable TEXT, " +
                    "vl_result_at_trimester_1 TEXT, " +
                    "vl_result_at_trimester_2 TEXT, " +
                    "vl_result_at_trimester_3 TEXT, " +
                    "male_partner_tested TEXT, " +
                    "date_male_partner_tested TEXT, " +
                    "result_r_nr TEXT, " +
                    "treatment_initiated TEXT, " +
                    "date_initiated_on_treatment TEXT, " +
                    "on_art_st_anc TEXT, " +
                    "tb_screening TEXT, " +
                    "syphilis_testing TEXT, " +
                    "syphilis_test_type TEXT, " +
                    "syphilis_other TEXT, " +
                    "date_tested_for_syphilis TEXT, " +
                    "syphilis_result TEXT, " +
                    "delete_status TEXT)";

            db.execSQL(sqlCreateTableANC);


            String sqlCreateTableChildMonitoring = "CREATE TABLE IF NOT EXISTS ec_pmtct_child_monitoring (" +
                    "base_entity_id TEXT, " +
                    "pmtct_id TEXT, " +
                    "unique_id TEXT, " +
                    "child_monitoring_visit TEXT, " +
                    "dbs_at_birth_due_date TEXT, " +
                    "dbs_at_birth_actual_date TEXT, " +
                    "test_result_at_birth TEXT, " +
                    "date_tested TEXT, " +
                    "nvp_prophylaxis_for_infant TEXT, " +
                    "nvp_date_given TEXT, " +
                    "_6_weeks_dbs_date TEXT, " +
                    "_6_weeks_dbs_ctx TEXT, " +
                    "_6_weeks_dbs_hiv_test_p_n TEXT, " +
                    "_6_weeks_dbs_iycf_counselling TEXT, " +
                    "_6_weeks_infant_feeding_options TEXT, " +
                    "_6_weeks_dbs_outcome TEXT, " +
                    "_2_months_date TEXT, " +
                    "_2_months_hiv_status_p_n TEXT, " +
                    "_2_months_ctx TEXT, " +
                    "_2_months_iycf_counselling TEXT, " +
                    "_2_months_infant_feeding_options TEXT, " +
                    "_2_months_outcome TEXT, " +
                    "_3_months_date TEXT, " +
                    "_3_months_hiv_status_p_n TEXT, " +
                    "_3_months_ctx TEXT, " +
                    "_3_months_iycf_counselling TEXT, " +
                    "_3_months_infant_feeding_options TEXT, " +
                    "_3_months_outcome TEXT, " +
                    "_4_months_date TEXT, " +
                    "_4_months_hiv_status_p_n TEXT, " +
                    "_4_months_ctx TEXT, " +
                    "_4_months_iycf_counselling TEXT, " +
                    "_4_months_infant_feeding_options TEXT, " +
                    "_4_months_outcome TEXT, " +

                    "_5_months_date TEXT, " +
                    "_5_months_hiv_status_p_n TEXT, " +
                    "_5_months_ctx TEXT, " +
                    "_5_months_iycf_counselling TEXT, " +
                    "_5_months_infant_feeding_options TEXT, " +
                    "_5_months_outcome TEXT, " +

                    "_6_months_date TEXT, " +
                    "_6_months_hiv_status_p_n TEXT, " +
                    "_6_months_ctx TEXT, " +
                    "_6_months_iycf_counselling TEXT, " +
                    "_6_months_infant_feeding_options TEXT, " +
                    "_6_months_outcome TEXT, " +

                    "_7_months_date TEXT, " +
                    "_7_months_hiv_status_p_n TEXT, " +
                    "_7_months_ctx TEXT, " +
                    "_7_months_iycf_counselling TEXT, " +
                    "_7_months_infant_feeding_options TEXT, " +
                    "_7_months_outcome TEXT, " +

                    "_8_months_date TEXT, " +
                    "_8_months_hiv_status_p_n TEXT, " +
                    "_8_months_ctx TEXT, " +
                    "_8_months_iycf_counselling TEXT, " +
                    "_8_months_infant_feeding_options TEXT, " +
                    "_8_months_outcome TEXT, " +

                    "_9_months_date TEXT, " +
                    "_9_months_hiv_status_p_n TEXT, " +
                    "_9_months_ctx TEXT, " +
                    "_9_months_iycf_counselling TEXT, " +
                    "_9_months_infant_feeding_options TEXT, " +
                    "_9_months_outcome TEXT, " +

                    "_10_months_date TEXT, " +
                    "_10_months_hiv_status_p_n TEXT, " +
                    "_10_months_ctx TEXT, " +
                    "_10_months_iycf_counselling TEXT, " +
                    "_10_months_infant_feeding_options TEXT, " +
                    "_10_months_outcome TEXT, " +

                    "_11_months_date TEXT, " +
                    "_11_months_hiv_status_p_n TEXT, " +
                    "_11_months_ctx TEXT, " +
                    "_11_months_iycf_counselling TEXT, " +
                    "_11_months_infant_feeding_options TEXT, " +
                    "_11_months_outcome TEXT, " +

                    "_12_months_date TEXT, " +
                    "_12_months_hiv_status_p_n TEXT, " +
                    "_12_months_ctx TEXT, " +
                    "_12_months_iycf_counselling TEXT, " +
                    "_12_months_infant_feeding_options TEXT, " +
                    "_12_months_outcome TEXT, " +

                    "_13_months_date TEXT, " +
                    "_13_months_hiv_status_p_n TEXT, " +
                    "_13_months_ctx TEXT, " +
                    "_13_months_iycf_counselling TEXT, " +
                    "_13_months_infant_feeding_options TEXT, " +
                    "_13_months_outcome TEXT, " +

                    "_14_months_date TEXT, " +
                    "_14_months_hiv_status_p_n TEXT, " +
                    "_14_months_ctx TEXT, " +
                    "_14_months_iycf_counselling TEXT, " +
                    "_14_months_infant_feeding_options TEXT, " +
                    "_14_months_outcome TEXT, " +

                    "_15_months_date TEXT, " +
                    "_15_months_hiv_status_p_n TEXT, " +
                    "_15_months_ctx TEXT, " +
                    "_15_months_iycf_counselling TEXT, " +
                    "_15_months_infant_feeding_options TEXT, " +
                    "_15_months_outcome TEXT, " +

                    "_16_months_date TEXT, " +
                    "_16_months_hiv_status_p_n TEXT, " +
                    "_16_months_ctx TEXT, " +
                    "_16_months_iycf_counselling TEXT, " +
                    "_16_months_infant_feeding_options TEXT, " +
                    "_16_months_outcome TEXT, " +

                    "_17_months_date TEXT, " +
                    "_17_months_hiv_status_p_n TEXT, " +
                    "_17_months_ctx TEXT, " +
                    "_17_months_iycf_counselling TEXT, " +
                    "_17_months_infant_feeding_options TEXT, " +
                    "_17_months_outcome TEXT, " +

                    "_18_months_date TEXT, " +
                    "_18_months_hiv_status_p_n TEXT, " +
                    "_18_months_ctx TEXT, " +
                    "_18_months_iycf_counselling TEXT, " +
                    "_18_months_infant_feeding_options TEXT, " +
                    "_18_months_outcome TEXT, " +

                    "final_outcome TEXT, " +
                    "date_referred_for_art_if_hiv_positive TEXT, " +
                    "date_enrolled_in_art TEXT, " +
                    "delete_status TEXT)";



            db.execSQL(sqlCreateTableChildMonitoring);

           // clearAppData(context);

        } catch (Exception e) {
            Timber.e(e, "upgradeToVersion5 ");
        }
    }
    private static void upgradeToVersion6(SQLiteDatabase db) {
        try {

            db.execSQL("ALTER TABLE ec_graduation ADD COLUMN enrollment_date TEXT");
            db.execSQL("ALTER TABLE ec_graduation ADD COLUMN asmt TEXT");
            db.execSQL("ALTER TABLE ec_graduation ADD COLUMN caregiver_name TEXT");
            db.execSQL("ALTER TABLE ec_graduation ADD COLUMN caregiver_sex TEXT");
            db.execSQL("ALTER TABLE ec_graduation ADD COLUMN caregiver_birth_date TEXT");
            db.execSQL("ALTER TABLE ec_graduation ADD COLUMN enrollment_date TEXT");
            db.execSQL("ALTER TABLE ec_hiv_assessment_below_15 ADD COLUMN assessment_date");
            db.execSQL("ALTER TABLE ec_hiv_assessment_above_15 ADD COLUMN assessment_date");


        } catch (Exception e) {
            Timber.e(e, "upgradeToVersion6 ");
        }
    }
    private static void upgradeToVersion7(SQLiteDatabase db) {
        try {

            db.execSQL("ALTER TABLE ec_vca_service_report ADD COLUMN signature TEXT");
            db.execSQL("ALTER TABLE ec_household_service_report ADD COLUMN signature TEXT");
            db.execSQL("ALTER TABLE ec_household_visitation_for_caregiver ADD COLUMN signature TEXT");
            db.execSQL("ALTER TABLE ec_household_visitation_for_vca_0_20_years ADD COLUMN signature TEXT");
            db.execSQL("ALTER TABLE ec_referral ADD COLUMN signature TEXT");
            db.execSQL("ALTER TABLE ec_household ADD COLUMN signature TEXT");



        } catch (Exception e) {
            Timber.e(e, "upgradeToVersion7 ");
        }
    }

    private static void clearAppData(Context context) {
        // Clear cache
        clearAppCache(context);

        // Clear internal storage files
        clearInternalStorage(context);
    }

    private static void clearAppCache(Context context) {
        try {
            File cacheDirectory = context.getCacheDir();
            deleteDir(cacheDirectory);
        } catch (Exception e) {
            Timber.e(e, "clearAppCache: Error clearing app cache");
        }
    }

    private static void clearInternalStorage(Context context) {
        try {
            File filesDir = context.getFilesDir();
            deleteDir(filesDir);

            File databaseDir = context.getDatabasePath("dummy").getParentFile();
            deleteDir(databaseDir);

            File sharedPrefsDir = new File(context.getFilesDir(), "../shared_prefs");
            deleteDir(sharedPrefsDir);
        } catch (Exception e) {
            Timber.e(e, "clearInternalStorage: Error clearing app internal storage");
        }
    }

    // Recursive method to delete a directory and its contents
    private static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String child : children) {
                boolean success = deleteDir(new File(dir, child));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }



    private static void upgradeToVersion8(SQLiteDatabase db) {
        try {
            db.execSQL("ALTER TABLE ec_household ADD COLUMN district_moved_to TEXT");
            db.execSQL("ALTER TABLE ec_household ADD COLUMN household_receiving_caseworker TEXT");
            db.execSQL("ALTER TABLE ec_client_index ADD COLUMN district_moved_to TEXT");
            db.execSQL("ALTER TABLE ec_client_index ADD COLUMN vca_receiving_caseworker TEXT");
            db.execSQL("ALTER TABLE ec_pmtct_mother ADD COLUMN first_name TEXT");
            db.execSQL("ALTER TABLE ec_pmtct_mother ADD COLUMN last_name TEXT");
            db.execSQL("ALTER TABLE ec_pmtct_mother ADD COLUMN sm_number TEXT");


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

    private static void upgradeToVersion24(SQLiteDatabase db) {
        try {
            com.bluecodeltd.ecap.chw.util.DatabaseMigrationUtils.fillFamilyMemberLocationTableWithProviderIds(db);
            db.execSQL(RepositoryUtils.EC_FAMILY_MEMBER_LOCATION_PROVIDER_ID_INDEX);
        } catch (Exception e) {
            Timber.e(e, "upgradeToVersion24");
        }
    }

    private static void upgradeToVersion25(SQLiteDatabase db) {
        try {
            db.execSQL("CREATE TABLE IF NOT EXISTS ec_mother_anc (" +
                    "base_entity_id TEXT, " +
                    "household_id TEXT, " +
                    "date_1st_visit TEXT, " +
                    "gestation_age_in_weeks TEXT, " +
                    "hiv_tested TEXT, " +
                    "date_tested TEXT, " +
                    "result_of_hiv_test TEXT, " +
                    "male_hiv_tested TEXT, " +
                    "male_result_of_hiv_test TEXT, " +
                    "gravida TEXT, " +
                    "parity TEXT, " +
                    "lmp_date TEXT, " +
                    "edd_date TEXT, " +
                    "tt_previous_doses TEXT, " +
                    "delete_status TEXT, " +
                    "entity_type TEXT, " +
                    "last_interacted_with TEXT" +
                    ")");

            db.execSQL("CREATE TABLE IF NOT EXISTS ec_child_final_outcome (" +
                    "base_entity_id TEXT, " +
                    "household_id TEXT, " +
                    "unique_id TEXT, " +
                    "infant_final_outcome_date TEXT, " +
                    "infant_final_hiv_status TEXT, " +
                    "infant_discharged_hiv_negative TEXT, " +
                    "infant_hiv_positive_on_art TEXT, " +
                    "infant_final_outcome TEXT, " +
                    "infant_exited_ovc_reason TEXT, " +
                    "infant_final_outcome_comments TEXT, " +
                    "delete_status TEXT, " +
                    "entity_type TEXT, " +
                    "last_interacted_with TEXT" +
                    ")");

            db.execSQL("CREATE TABLE IF NOT EXISTS ec_child_longitudinal_follow_up (" +
                    "base_entity_id TEXT, " +
                    "household_id TEXT, " +
                    "unique_id TEXT, " +
                    "infant_visit_number TEXT, " +
                    "infant_date_of_visit TEXT, " +
                    "infant_age TEXT, " +
                    "infant_vaccinations_given TEXT, " +
                    "infant_muac_reading TEXT, " +
                    "infant_oedema_present TEXT, " +
                    "infant_breastfeeding_status TEXT, " +
                    "infant_vitamin_a_given TEXT, " +
                    "infant_growth_monitoring_done TEXT, " +
                    "infant_deworming_given TEXT, " +
                    "infant_followup_comments TEXT, " +
                    "delete_status TEXT, " +
                    "entity_type TEXT, " +
                    "last_interacted_with TEXT" +
                    ")");

            db.execSQL("CREATE TABLE IF NOT EXISTS ec_child_postnatal_care (" +
                    "base_entity_id TEXT, " +
                    "household_id TEXT, " +
                    "unique_id TEXT, " +
                    "pnc_infant_visit_type TEXT, " +
                    "pnc_infant_feeding_type TEXT, " +
                    "pnc_infant_hiv_test_done TEXT, " +
                    "pnc_infant_on_art_if_positive TEXT, " +
                    "pnc_infant_ctx_given TEXT, " +
                    "pnc_infant_immunization_up_to_date TEXT, " +
                    "pnc_infant_growth_monitoring_done TEXT, " +
                    "pnc_infant_growth_normal TEXT, " +
                    "pnc_infant_referred_for_complications TEXT, " +
                    "weight_at_birth TEXT, " +
                    "under_five_card_number TEXT, " +
                    "pnc_infant_comments TEXT, " +
                    "delete_status TEXT, " +
                    "entity_type TEXT, " +
                    "last_interacted_with TEXT" +
                    ")");

            db.execSQL("CREATE TABLE IF NOT EXISTS ec_nutrition_assessment_intervention (" +
                    "base_entity_id TEXT, " +
                    "unique_id TEXT, " +
                    "date_of_assessment TEXT, " +
                    "muac_category TEXT, " +
                    "oedema_stage TEXT, " +
                    "wfa_category TEXT, " +
                    "intervention_status TEXT, " +
                    "referral_services TEXT, " +
                    "other_referral_services TEXT, " +
                    "why_not_referred TEXT, " +
                    "referral_completed TEXT, " +
                    "nutrition_counselling_services TEXT, " +
                    "other_nutrition_counselling_services TEXT, " +
                    "good_practices_services TEXT, " +
                    "other_good_practices_services TEXT, " +
                    "last_interacted_with TEXT" +
                    ")");

            db.execSQL("CREATE TABLE IF NOT EXISTS ec_mother_longitudinal_follow_up (" +
                    "base_entity_id TEXT, " +
                    "household_id TEXT, " +
                    "contact_count_number TEXT, " +
                    "lfu_date_of_visit TEXT, " +
                    "lfu_gestation_weeks TEXT, " +
                    "lfu_weight_kg TEXT, " +
                    "lfu_deworming TEXT, " +
                    "lfu_current_tt_doses TEXT, " +
                    "lfu_folate TEXT, " +
                    "lfu_iron TEXT, " +
                    "lfu_hiv_subsequent_test_result TEXT, " +
                    "lfu_enrolled_community_pmtct_if_positive TEXT, " +
                    "lfu_started_prep_if_negative TEXT, " +
                    "lfu_syphilis_test_result TEXT, " +
                    "lfu_syphilis_treatment_regimen TEXT, " +
                    "lfu_hepb_test_result TEXT, " +
                    "lfu_hepb_on_treatment TEXT, " +
                    "lfu_ipt_given_dose TEXT, " +
                    "lfu_received_itn TEXT, " +
                    "lfu_anc_as_couple TEXT, " +
                    "lfu_partner_tested_hiv TEXT, " +
                    "lfu_partner_test_result TEXT, " +
                    "lfu_discordant TEXT, " +
                    "lfu_partner_on_art TEXT, " +
                    "lfu_partner_syphilis_test TEXT, " +
                    "lfu_partner_hepb_screen TEXT, " +
                    "lfu_partner_started_prep TEXT, " +
                    "lfu_breast_cancer_screening TEXT, " +
                    "lfu_suspected_breast_cancer TEXT, " +
                    "lfu_tb_status TEXT, " +
                    "lfu_tpt_status TEXT, " +
                    "lfu_special_conditions TEXT, " +
                    "delete_status TEXT, " +
                    "entity_type TEXT, " +
                    "last_interacted_with TEXT" +
                    ")");

            db.execSQL("CREATE TABLE IF NOT EXISTS ec_mother_postnatal_care (" +
                    "base_entity_id TEXT, " +
                    "household_id TEXT, " +
                    "pnc_visit_type TEXT, " +
                    "pnc_type_of_feeding TEXT, " +
                    "pnc_hiv_test_done TEXT, " +
                    "pnc_on_prep TEXT, " +
                    "pnc_fp_counselling TEXT, " +
                    "pnc_cervical_cancer_screening TEXT, " +
                    "pnc_sti_screening TEXT, " +
                    "pnc_comments TEXT, " +
                    "delete_status TEXT, " +
                    "entity_type TEXT, " +
                    "last_interacted_with TEXT" +
                    ")");

            db.execSQL("CREATE TABLE IF NOT EXISTS ec_mother_outcome (" +
                    "base_entity_id TEXT, " +
                    "household_id TEXT, " +
                    "mother_final_outcome_date TEXT, " +
                    "mother_final_outcome TEXT, " +
                    "mother_exited_ovc_reason TEXT, " +
                    "delete_status TEXT, " +
                    "entity_type TEXT, " +
                    "last_interacted_with TEXT" +
                    ")");

            db.execSQL("CREATE TABLE IF NOT EXISTS ec_tb_screening (" +
                    "base_entity_id TEXT, " +
                    "unique_id TEXT, " +
                    "unique_tb_id TEXT, " +
                    "history_close_tb_contact TEXT, " +
                    "history_close_tb_contact_year TEXT, " +
                    "tb_symptoms_child_lt10 TEXT, " +
                    "tb_symptoms_child_lt10_other TEXT, " +
                    "tb_symptoms_10plus TEXT, " +
                    "tb_symptoms_10plus_other TEXT, " +
                    "referred_for_tb_evaluation TEXT, " +
                    "tb_referral_comment TEXT, " +
                    "followup_date TEXT, " +
                    "facility_referral_completed TEXT, " +
                    "date_screened_at_facility TEXT, " +
                    "tb_diagnosis_at_facility TEXT, " +
                    "initiated_tb_treatment TEXT, " +
                    "initiated_tpt TEXT, " +
                    "section_c_comments TEXT, " +
                    "treatment_followup_date TEXT, " +
                    "tb_treatment_outcome TEXT, " +
                    "tb_treatment_outcome_comment TEXT, " +
                    "tb_treatment_outcome_other_comment TEXT, " +
                    "previous_tb_treatment TEXT, " +
                    "treatment_year TEXT, " +
                    "treatment_duration_months TEXT, " +
                    "entity_type TEXT, " +
                    "last_interacted_with TEXT" +
                    ")");

            db.execSQL("CREATE TABLE IF NOT EXISTS ec_tb_screening_caregiver (" +
                    "base_entity_id TEXT, " +
                    "unique_id TEXT, " +
                    "household_id TEXT, " +
                    "unique_tb_id TEXT, " +
                    "history_close_tb_contact TEXT, " +
                    "history_close_tb_contact_year TEXT, " +
                    "tb_symptoms_child_lt10 TEXT, " +
                    "tb_symptoms_child_lt10_other TEXT, " +
                    "tb_symptoms_10plus TEXT, " +
                    "tb_symptoms_10plus_other TEXT, " +
                    "referred_for_tb_evaluation TEXT, " +
                    "tb_referral_comment TEXT, " +
                    "followup_date TEXT, " +
                    "facility_referral_completed TEXT, " +
                    "date_screened_at_facility TEXT, " +
                    "tb_diagnosis_at_facility TEXT, " +
                    "initiated_tb_treatment TEXT, " +
                    "initiated_tpt TEXT, " +
                    "section_c_comments TEXT, " +
                    "treatment_followup_date TEXT, " +
                    "tb_treatment_outcome TEXT, " +
                    "tb_treatment_outcome_comment TEXT, " +
                    "tb_treatment_outcome_other_comment TEXT, " +
                    "previous_tb_treatment TEXT, " +
                    "treatment_year TEXT, " +
                    "treatment_duration_months TEXT, " +
                    "entity_type TEXT, " +
                    "last_interacted_with TEXT" +
                    ")");

            db.execSQL("CREATE TABLE IF NOT EXISTS ec_tb_screening_outcome (" +
                    "base_entity_id TEXT, " +
                    "unique_tb_id TEXT, " +
                    "followup_date TEXT, " +
                    "facility_referral_completed TEXT, " +
                    "date_screened_at_facility TEXT, " +
                    "tb_diagnosis_at_facility TEXT, " +
                    "initiated_tb_treatment TEXT, " +
                    "initiated_tpt TEXT, " +
                    "section_c_comments TEXT, " +
                    "treatment_followup_date TEXT, " +
                    "tb_treatment_outcome TEXT, " +
                    "tb_treatment_outcome_comment TEXT, " +
                    "entity_type TEXT, " +
                    "last_interacted_with TEXT" +
                    ")");

            db.execSQL("CREATE TABLE IF NOT EXISTS ec_tb_screening_outcome_caregiver (" +
                    "base_entity_id TEXT, " +
                    "unique_id TEXT, " +
                    "household_id TEXT, " +
                    "unique_tb_id TEXT, " +
                    "history_close_tb_contact TEXT, " +
                    "history_close_tb_contact_year TEXT, " +
                    "tb_symptoms_child_lt10 TEXT, " +
                    "tb_symptoms_child_lt10_other TEXT, " +
                    "tb_symptoms_10plus TEXT, " +
                    "tb_symptoms_10plus_other TEXT, " +
                    "referred_for_tb_evaluation TEXT, " +
                    "tb_referral_comment TEXT, " +
                    "followup_date TEXT, " +
                    "facility_referral_completed TEXT, " +
                    "date_screened_at_facility TEXT, " +
                    "tb_diagnosis_at_facility TEXT, " +
                    "initiated_tb_treatment TEXT, " +
                    "initiated_tpt TEXT, " +
                    "section_c_comments TEXT, " +
                    "treatment_followup_date TEXT, " +
                    "tb_treatment_outcome TEXT, " +
                    "tb_treatment_outcome_comment TEXT, " +
                    "tb_treatment_outcome_other_comment TEXT, " +
                    "previous_tb_treatment TEXT, " +
                    "treatment_year TEXT, " +
                    "treatment_duration_months TEXT, " +
                    "entity_type TEXT, " +
                    "last_interacted_with TEXT" +
                    ")");

            List<String> alterStatements = Arrays.asList(
                    "ALTER TABLE ec_mother_index ADD COLUMN mother_pregnant TEXT",
                    "ALTER TABLE ec_mother_index ADD COLUMN mother_breastfeeding TEXT",
                    "ALTER TABLE ec_mother_index ADD COLUMN mother_age_range TEXT",
                    "ALTER TABLE ec_mother_index ADD COLUMN mother_children_age_band TEXT",
                    "ALTER TABLE ec_pmtct_mother ADD COLUMN household_id TEXT",
                    "ALTER TABLE ec_pmtct_mother ADD COLUMN caregiver_name TEXT",
                    "ALTER TABLE ec_pmtct_mother ADD COLUMN caregiver_birth_date TEXT",
                    "ALTER TABLE ec_pmtct_mother ADD COLUMN province TEXT",
                    "ALTER TABLE ec_pmtct_mother_child ADD COLUMN household_id TEXT",
                    "ALTER TABLE ec_pmtct_child_monitoring ADD COLUMN household_id TEXT",
                    "ALTER TABLE ec_pmtct_child_monitoring ADD COLUMN nutrition_status TEXT",
                    "ALTER TABLE ec_pmtct_child_monitoring ADD COLUMN medical_complications TEXT",
                    "ALTER TABLE ec_pmtct_child_monitoring ADD COLUMN child_oedema TEXT",
                    "ALTER TABLE ec_pmtct_child_monitoring ADD COLUMN oedema_stage TEXT",
                    "ALTER TABLE ec_pmtct_child_monitoring ADD COLUMN tb_screening_symptoms TEXT",
                    "ALTER TABLE ec_pmtct_child_monitoring ADD COLUMN other_tb_symptom TEXT",
                    "ALTER TABLE ec_pmtct_child_monitoring ADD COLUMN tb_referral TEXT",
                    "ALTER TABLE ec_pmtct_child_monitoring ADD COLUMN comments_tb TEXT",
                    "ALTER TABLE ec_pmtct_child ADD COLUMN household_id TEXT",
                    "ALTER TABLE ec_pmtct_child_outcome ADD COLUMN household_id TEXT",
                    "ALTER TABLE ec_pmtct_mother_outcome ADD COLUMN household_id TEXT",
                    "ALTER TABLE ec_pmtct_mother_postnatal ADD COLUMN household_id TEXT",
                    "ALTER TABLE ec_pmtct_mother_postnatal ADD COLUMN tb_screening_symptoms_10plus TEXT",
                    "ALTER TABLE ec_pmtct_mother_postnatal ADD COLUMN other_tb_symptom_10plus TEXT",
                    "ALTER TABLE ec_pmtct_mother_postnatal ADD COLUMN comments_tb_10plus TEXT",
                    "ALTER TABLE ec_pmtct_mother_anc ADD COLUMN household_id TEXT",
                    "ALTER TABLE ec_pmtct_delivery_details ADD COLUMN household_id TEXT"
            );

            for (String statement : alterStatements) {
                db.execSQL(statement);
            }

            db.execSQL("CREATE TABLE IF NOT EXISTS ec_mother_delivery (" +
                    "base_entity_id TEXT, " +
                    "household_id TEXT, " +
                    "date_of_delivery TEXT, " +
                    "place_of_delivery TEXT, " +
                    "hiv_status_at_delivery TEXT, " +
                    "delete_status TEXT, " +
                    "entity_type TEXT, " +
                    "last_interacted_with TEXT" +
                    ")");

        } catch (Exception e) {
            Timber.e(e, "upgradeToVersion25");
        }
    }

}
