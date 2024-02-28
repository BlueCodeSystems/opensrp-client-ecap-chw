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
import org.smartregister.immunization.repository.RecurringServiceRecordRepository;
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

    private static void upgradeToVersion4(SQLiteDatabase db) {
        try {
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
        } catch (Exception e) {
            Timber.e(e, "upgradeToVersion4 ");
        }
    }

    private static void upgradeToVersion5(SQLiteDatabase db) {
        try {
            db.execSQL(VaccineRepository.UPDATE_TABLE_ADD_CHILD_LOCATION_ID_COL);
            db.execSQL(RecurringServiceRecordRepository.UPDATE_TABLE_ADD_CHILD_LOCATION_ID_COL);
        } catch (Exception e) {
            Timber.e(e, "upgradeToVersion5 ");
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
