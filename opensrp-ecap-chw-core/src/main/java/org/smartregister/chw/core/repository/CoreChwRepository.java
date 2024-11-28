package org.smartregister.chw.core.repository;

import android.content.Context;

import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.chw.anc.repository.VisitDetailsRepository;
import org.smartregister.chw.anc.repository.VisitRepository;
import org.smartregister.chw.core.application.CoreChwApplication;
import org.smartregister.commonregistry.CommonFtsObject;
import org.smartregister.configurableviews.repository.ConfigurableViewsRepository;
import org.smartregister.growthmonitoring.repository.WeightForHeightRepository;
import org.smartregister.immunization.ImmunizationLibrary;
import org.smartregister.immunization.repository.RecurringServiceRecordRepository;
import org.smartregister.immunization.repository.RecurringServiceTypeRepository;
import org.smartregister.immunization.repository.VaccineNameRepository;
import org.smartregister.immunization.repository.VaccineRepository;
import org.smartregister.immunization.repository.VaccineTypeRepository;
import org.smartregister.immunization.util.IMDatabaseUtils;
import org.smartregister.reporting.repository.DailyIndicatorCountRepository;
import org.smartregister.reporting.repository.IndicatorQueryRepository;
import org.smartregister.reporting.repository.IndicatorRepository;
import org.smartregister.repository.ClientFormRepository;
import org.smartregister.repository.DrishtiRepository;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.repository.Hia2ReportRepository;
import org.smartregister.repository.LocationRepository;
import org.smartregister.repository.LocationTagRepository;
import org.smartregister.repository.ManifestRepository;
import org.smartregister.repository.PlanDefinitionRepository;
import org.smartregister.repository.PlanDefinitionSearchRepository;
import org.smartregister.repository.Repository;
import org.smartregister.repository.SettingsRepository;
import org.smartregister.repository.TaskRepository;
import org.smartregister.repository.UniqueIdRepository;
import org.smartregister.util.Session;

import timber.log.Timber;

public class CoreChwRepository extends Repository {

    protected SQLiteDatabase readableDatabase;
    protected SQLiteDatabase writableDatabase;
    private Context context;
    private int databaseVersion;

    public CoreChwRepository(Context context, String databaseName, int databaseVersion, Session session, CommonFtsObject commonFtsObject, DrishtiRepository... repositories) {
        super(context, databaseName, databaseVersion, session, commonFtsObject, repositories);
        this.context = context;
        this.databaseVersion = databaseVersion;
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        super.onCreate(database);
        EventClientRepository.createTable(database, EventClientRepository.Table.client, EventClientRepository.client_column.values());
        EventClientRepository.createTable(database, EventClientRepository.Table.event, EventClientRepository.event_column.values());

        VaccineRepository.createTable(database);
        VaccineNameRepository.createTable(database);
        VaccineTypeRepository.createTable(database);
        ConfigurableViewsRepository.createTable(database);
        LocationRepository.createTable(database);

        UniqueIdRepository.createTable(database);
        SettingsRepository.onUpgrade(database);

        RecurringServiceTypeRepository.createTable(database);
        RecurringServiceRecordRepository.createTable(database);

        IndicatorRepository.createTable(database);
        IndicatorQueryRepository.createTable(database);
        DailyIndicatorCountRepository.createTable(database);
        MonthlyTalliesRepository.createTable(database);


        VisitRepository.createTable(database);
        VisitDetailsRepository.createTable(database);

        PlanDefinitionRepository.createTable(database);
        PlanDefinitionSearchRepository.createTable(database);
        TaskRepository.createTable(database);
        //LocationRepository.createTable(database);    //TODO verify why this causes a break in code
        LocationTagRepository.createTable(database);

        ScheduleRepository.createTable(database);
        RecurringServiceTypeRepository recurringServiceTypeRepository = ImmunizationLibrary.getInstance().recurringServiceTypeRepository();
        IMDatabaseUtils.populateRecurringServices(context, database, recurringServiceTypeRepository);

        WeightForHeightRepository.createTable(database);

        ManifestRepository.createTable(database);
        ClientFormRepository.createTable(database);
        CommunityResponderRepository.createTable(database);

        onUpgrade(database, 1, databaseVersion);

        // initialize from yml file

        // Check if indicator data initialised

        //hia2
        EventClientRepository.createTable(database, Hia2ReportRepository.Table.hia2_report, Hia2ReportRepository.report_column.values());

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onUpgrade(db, oldVersion, newVersion);
    }

    @Override
    public synchronized SQLiteDatabase getWritableDatabase(String password) {
        if (writableDatabase == null || !writableDatabase.isOpen()) {
            if (writableDatabase != null) {
                writableDatabase.close();
            }
            writableDatabase = super.getWritableDatabase(password);
        }
        return writableDatabase;
    }

    @Override
    public synchronized SQLiteDatabase getReadableDatabase(String password) {
        try {
            if (readableDatabase == null || !readableDatabase.isOpen()) {
                if (readableDatabase != null) {
                    readableDatabase.close();
                }
                readableDatabase = super.getReadableDatabase(password);
            }
            return readableDatabase;
        } catch (Exception e) {
            Timber.e("Database Error. %s", e.getMessage());
            return null;
        }

    }

    @Override
    public synchronized void close() {
        if (readableDatabase != null) {
            readableDatabase.close();
        }

        if (writableDatabase != null) {
            writableDatabase.close();
        }
        super.close();
    }
}