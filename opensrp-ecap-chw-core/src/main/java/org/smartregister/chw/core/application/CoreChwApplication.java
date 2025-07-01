package org.smartregister.chw.core.application;

import android.content.Intent;
import android.util.Pair;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.Context;
import org.smartregister.chw.core.contract.CoreApplication;
import org.smartregister.chw.core.helper.RulesEngineHelper;
import org.smartregister.chw.core.repository.AncRegisterRepository;
import org.smartregister.chw.core.repository.ChwTaskRepository;
import org.smartregister.chw.core.repository.CommunityResponderRepository;
import org.smartregister.chw.core.repository.DailyTalliesRepository;
import org.smartregister.chw.core.repository.HIA2IndicatorsRepository;
import org.smartregister.chw.core.repository.MalariaRegisterRepository;
import org.smartregister.chw.core.repository.MonthlyTalliesRepository;
import org.smartregister.chw.core.repository.PncRegisterRepository;
import org.smartregister.chw.core.repository.ScheduleRepository;
import org.smartregister.chw.core.repository.StockUsageReportRepository;
import org.smartregister.chw.core.sync.CoreClientProcessor;
import org.smartregister.chw.core.utils.ApplicationUtils;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.commonregistry.AllCommonsRepository;
import org.smartregister.commonregistry.CommonFtsObject;
import org.smartregister.configurableviews.helper.JsonSpecHelper;
import org.smartregister.family.FamilyLibrary;
import org.smartregister.immunization.ImmunizationLibrary;
import org.smartregister.immunization.domain.VaccineSchedule;
import org.smartregister.immunization.domain.jsonmapping.Vaccine;
import org.smartregister.immunization.domain.jsonmapping.VaccineGroup;
import org.smartregister.immunization.repository.VaccineRepository;
import org.smartregister.immunization.util.VaccinatorUtils;
import org.smartregister.repository.Hia2ReportRepository;
import org.smartregister.repository.LocationRepository;
import org.smartregister.repository.PlanDefinitionRepository;
import org.smartregister.repository.TaskNotesRepository;
import org.smartregister.repository.TaskRepository;
import org.smartregister.repository.UniqueIdRepository;
import org.smartregister.sync.ClientProcessorForJava;
import org.smartregister.sync.helper.ECSyncHelper;
import org.smartregister.util.CallableInteractor;
import org.smartregister.util.GenericInteractor;
import org.smartregister.util.LangUtils;
import org.smartregister.view.activity.BaseLoginActivity;
import org.smartregister.view.activity.DrishtiApplication;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import timber.log.Timber;

public abstract class CoreChwApplication extends DrishtiApplication implements CoreApplication {

    protected static TaskRepository taskRepository;
    private static ClientProcessorForJava clientProcessor;
    private static CommonFtsObject commonFtsObject = null;
    private static AncRegisterRepository ancRegisterRepository;
    private static PncRegisterRepository pncRegisterRepository;
    private static PlanDefinitionRepository planDefinitionRepository;
    private static ScheduleRepository scheduleRepository;
    private static MalariaRegisterRepository malariaRegisterRepository;
    private static StockUsageReportRepository stockUsageReportRepository;
    public JsonSpecHelper jsonSpecHelper;
    protected ClientProcessorForJava clientProcessorForJava;
    private LocationRepository locationRepository;
    private ECSyncHelper ecSyncHelper;
    private UniqueIdRepository uniqueIdRepository;
    private HIA2IndicatorsRepository hIA2IndicatorsRepository;
    private DailyTalliesRepository dailyTalliesRepository;
    private MonthlyTalliesRepository monthlyTalliesRepository;
    private Hia2ReportRepository hia2ReportRepository;
    private CommunityResponderRepository communityResponderRepository;
    private RulesEngineHelper rulesEngineHelper;
    private ExecutorService executor;
    private CallableInteractor interactor;

    public static JsonSpecHelper getJsonSpecHelper() {
        return getInstance().jsonSpecHelper;
    }

    public static synchronized CoreChwApplication getInstance() {
        return (CoreChwApplication) mInstance;
    }

    public static CommonFtsObject createCommonFtsObject() {
        return ApplicationUtils.getCommonFtsObject(commonFtsObject);
    }

    public static AncRegisterRepository ancRegisterRepository() {
        if (ancRegisterRepository == null) {
            ancRegisterRepository = new AncRegisterRepository();
        }
        return ancRegisterRepository;
    }

    public static PncRegisterRepository pncRegisterRepository() {
        if (pncRegisterRepository == null) {
            pncRegisterRepository = new PncRegisterRepository();
        }
        return pncRegisterRepository;
    }

    public static MalariaRegisterRepository malariaRegisterRepository() {
        if (malariaRegisterRepository == null) {
            malariaRegisterRepository = new MalariaRegisterRepository();
        }

        return malariaRegisterRepository;
    }

    /**
     * Update application contants to fit current context
     */
    public static Locale getCurrentLocale() {
        return mInstance == null ? Locale.getDefault() : mInstance.getResources().getConfiguration().locale;
    }

    public static ClientProcessorForJava getClientProcessor(android.content.Context context) {
        if (clientProcessor == null) {
            clientProcessor = CoreClientProcessor.getInstance(context);
        }
        return clientProcessor;
    }

    public TaskRepository getTaskRepository() {
        if (taskRepository == null) {
            taskRepository = new ChwTaskRepository(new TaskNotesRepository());
        }
        return taskRepository;
    }

    public PlanDefinitionRepository getPlanDefinitionRepository() {
        if (planDefinitionRepository == null) {
            planDefinitionRepository = new PlanDefinitionRepository();
        }
        return planDefinitionRepository;
    }

    public ScheduleRepository getScheduleRepository() {
        if (scheduleRepository == null) {
            scheduleRepository = new ScheduleRepository();
        }
        return scheduleRepository;
    }

    public StockUsageReportRepository getStockUsageRepository() {
        if (stockUsageReportRepository == null) {
            stockUsageReportRepository = new StockUsageReportRepository();
        }
        return stockUsageReportRepository;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void logoutCurrentUser() {
        Intent intent = new Intent(getApplicationContext(), BaseLoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        getApplicationContext().startActivity(intent);
        context.userService().logoutSession();
    }

    @Override
    public ClientProcessorForJava getClientProcessor() {
        return CoreChwApplication.getClientProcessor(CoreChwApplication.getInstance().getApplicationContext());
    }

    public VaccineRepository vaccineRepository() {
        return ImmunizationLibrary.getInstance().vaccineRepository();
    }

    public LocationRepository getLocationRepository() {
        if (locationRepository == null) {
            locationRepository = new LocationRepository();
        }
        return locationRepository;
    }

    public void initOfflineSchedules() {
        try {
            // child schedules
            List<VaccineGroup> childVaccines = VaccinatorUtils.getSupportedVaccines(this);
            List<Vaccine> specialVaccines = VaccinatorUtils.getSpecialVaccines(this);
            VaccineSchedule.init(childVaccines, specialVaccines, CoreConstants.SERVICE_GROUPS.CHILD);
        } catch (Exception e) {
            Timber.e(e);
        }

        try {
            // mother vaccines
            List<VaccineGroup> womanVaccines = VaccinatorUtils.getSupportedWomanVaccines(this);
            VaccineSchedule.init(womanVaccines, null, CoreConstants.SERVICE_GROUPS.WOMAN);
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    public AllCommonsRepository getAllCommonsRepository(String table) {
        return CoreChwApplication.getInstance().getContext().allCommonsRepositoryobjects(table);
    }

    public ClientProcessorForJava getClientProcessorForJava() {
        if (this.clientProcessorForJava == null) {
            this.clientProcessorForJava = ClientProcessorForJava.getInstance(getContext().applicationContext());
        }

        return this.clientProcessorForJava;
    }

    public UniqueIdRepository getUniqueIdRepository() {
        if (this.uniqueIdRepository == null) {
            this.uniqueIdRepository = new UniqueIdRepository();
        }

        return this.uniqueIdRepository;
    }

    @Override
    public void saveLanguage(String language) {
        CoreChwApplication.getInstance().getContext().allSharedPreferences().saveLanguagePreference(language);
    }

    @Override
    public Context getContext() {
        return context;
    }


    @Override
    public ECSyncHelper getEcSyncHelper() {
        if (ecSyncHelper == null) {
            ecSyncHelper = ECSyncHelper.getInstance(getApplicationContext());
        }
        return ecSyncHelper;
    }

    @Override
    public void notifyAppContextChange() {
        Locale current = getApplicationContext().getResources().getConfiguration().locale;
        saveLanguage(current.getLanguage());
        CoreConstants.JSON_FORM.setLocaleAndAssetManager(current, getAssets());
        FamilyLibrary.getInstance().setMetadata(getMetadata());
    }

    @Override
    public RulesEngineHelper getRulesEngineHelper() {
        if (rulesEngineHelper == null) {
            rulesEngineHelper = new RulesEngineHelper(getApplicationContext());
        }
        return rulesEngineHelper;
    }

    @Override
    public ArrayList<Pair<String, String>> getFamilyLocationFields() {
        ArrayList<Pair<String, String>> list = new ArrayList<>();
        list.add(Pair.create(JsonFormConstants.STEP1, "nearest_facility"));
        return list;
    }


    public DailyTalliesRepository dailyTalliesRepository() {
        if (dailyTalliesRepository == null) {
            dailyTalliesRepository = new DailyTalliesRepository();
        }
        return dailyTalliesRepository;
    }

    public HIA2IndicatorsRepository hIA2IndicatorsRepository() {
        if (hIA2IndicatorsRepository == null) {
            hIA2IndicatorsRepository = new HIA2IndicatorsRepository();
        }
        return hIA2IndicatorsRepository;
    }

    public MonthlyTalliesRepository monthlyTalliesRepository() {
        if (monthlyTalliesRepository == null) {
            monthlyTalliesRepository = new MonthlyTalliesRepository();
        }

        return monthlyTalliesRepository;
    }

    public Hia2ReportRepository hia2ReportRepository() {
        if (hia2ReportRepository == null) {
            hia2ReportRepository = new Hia2ReportRepository();
        }
        return hia2ReportRepository;
    }

    public CommunityResponderRepository communityResponderRepository() {
        if (communityResponderRepository == null) {
            communityResponderRepository = new CommunityResponderRepository();
        }
        return communityResponderRepository;
    }

    public boolean getChildFlavorUtil() {
        return false;
    }

    @Override
    public boolean allowLazyProcessing() {
        return false;
    }

    @Override
    public String[] lazyProcessedEvents() {
        return new String[0];
    }

    @Override
    public void persistLanguage(String language) {
        getExecutorService().execute(() -> Context.getInstance().allSettings().put(CoreConstants.PERSISTED_LANGUAGE, language));
    }

    @Override
    public void reloadLanguage() {
        getExecutorService().execute(() -> {
            String language = Context.getInstance().allSettings().get(CoreConstants.PERSISTED_LANGUAGE);
            if (StringUtils.isNotBlank(language))
                LangUtils.saveLanguage(getApplicationContext(), language);
        });
    }


    @Override
    public ExecutorService getExecutorService() {
        if (executor == null) {
            int value = 2;
            try {
                value = Runtime.getRuntime().availableProcessors();
            } catch (Exception e) {
                Timber.e(e);
            }
            executor = Executors.newFixedThreadPool(Math.max(2, value));
        }

        return executor;
    }

    @Override
    public CallableInteractor getCallableInteractor() {
        if (interactor == null)
            interactor = new GenericInteractor();
        return interactor;
    }
}
