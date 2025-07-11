package com.bluecodeltd.ecap.chw.application;

import static org.koin.core.context.GlobalContext.getOrNull;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.text.TextUtils;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.bluecodeltd.ecap.chw.BuildConfig;
import com.bluecodeltd.ecap.chw.activity.AllClientsRegisterActivity;
import com.bluecodeltd.ecap.chw.activity.BeneficiariesRegisterActivity;
import com.bluecodeltd.ecap.chw.activity.CasePlanRegisterActivity;
import com.bluecodeltd.ecap.chw.activity.DashboardActivity;
import com.bluecodeltd.ecap.chw.activity.FamilyProfileActivity;
import com.bluecodeltd.ecap.chw.activity.FamilyRegisterActivity;
import com.bluecodeltd.ecap.chw.activity.FpRegisterActivity;
import com.bluecodeltd.ecap.chw.activity.HivTestingServiceActivity;
import com.bluecodeltd.ecap.chw.activity.HouseholdIndexActivity;
import com.bluecodeltd.ecap.chw.activity.IdentificationRegisterActivity;
import com.bluecodeltd.ecap.chw.activity.IndexRegisterActivity;
import com.bluecodeltd.ecap.chw.activity.LoginActivity;
import com.bluecodeltd.ecap.chw.activity.MotherIndexActivity;
import com.bluecodeltd.ecap.chw.activity.PMTCTRegisterActivity;
import com.bluecodeltd.ecap.chw.activity.ReferralRegisterActivity;
import com.bluecodeltd.ecap.chw.activity.UpdatesRegisterActivity;
import com.bluecodeltd.ecap.chw.configs.AllClientsRegisterRowOptions;
import com.bluecodeltd.ecap.chw.custom_view.NavigationMenuFlv;
import com.bluecodeltd.ecap.chw.job.BasePncCloseJob;
import com.bluecodeltd.ecap.chw.job.ChwJobCreator;
import com.bluecodeltd.ecap.chw.job.ScheduleJob;
import com.bluecodeltd.ecap.chw.model.NavigationModelFlv;
import com.bluecodeltd.ecap.chw.repository.ChwRepository;
import com.bluecodeltd.ecap.chw.schedulers.ChwScheduleTaskExecutor;
import com.bluecodeltd.ecap.chw.sync.ChwClientProcessor;
import com.bluecodeltd.ecap.chw.util.ChwLocationBasedClassifier;
import com.bluecodeltd.ecap.chw.util.FailSafeRecalledID;
import com.bluecodeltd.ecap.chw.util.FileUtils;
import com.bluecodeltd.ecap.chw.util.JsonFormUtils;
import com.bluecodeltd.ecap.chw.util.Utils;
import com.evernote.android.job.JobManager;
import com.vijay.jsonwizard.NativeFormLibrary;
import com.vijay.jsonwizard.domain.Form;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.koin.core.context.GlobalContextKt;
import org.smartregister.AllConstants;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.P2POptions;
import org.smartregister.chw.anc.AncLibrary;
import org.smartregister.chw.anc.domain.Visit;
import org.smartregister.chw.core.application.CoreChwApplication;
import org.smartregister.chw.core.custom_views.NavigationMenu;
import org.smartregister.chw.core.provider.CoreAllClientsRegisterQueryProvider;
import org.smartregister.chw.core.service.CoreAuthorizationService;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.FormUtils;
import org.smartregister.chw.fp.FpLibrary;
import org.smartregister.chw.fp.util.FamilyPlanningConstants;
import org.smartregister.chw.malaria.MalariaLibrary;
import org.smartregister.chw.pnc.PncLibrary;
import org.smartregister.chw.referral.ReferralLibrary;
import org.smartregister.commonregistry.CommonFtsObject;
import org.smartregister.configurableviews.ConfigurableViewsLibrary;
import org.smartregister.configurableviews.helper.JsonSpecHelper;
import org.smartregister.domain.FetchStatus;
import org.smartregister.family.FamilyLibrary;
import org.smartregister.family.domain.FamilyMetadata;
import org.smartregister.family.util.AppExecutors;
import org.smartregister.family.util.Constants;
import org.smartregister.growthmonitoring.GrowthMonitoringConfig;
import org.smartregister.growthmonitoring.GrowthMonitoringLibrary;
import org.smartregister.immunization.ImmunizationLibrary;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.opd.OpdLibrary;
import org.smartregister.opd.configuration.OpdConfiguration;
import org.smartregister.receiver.P2pProcessingStatusBroadcastReceiver;
import org.smartregister.receiver.SyncStatusBroadcastReceiver;
import org.smartregister.reporting.ReportingLibrary;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.Repository;
import org.smartregister.sync.P2PClassifier;
import org.smartregister.thinkmd.ThinkMDConfig;
import org.smartregister.thinkmd.ThinkMDLibrary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import timber.log.Timber;

public class ChwApplication extends CoreChwApplication implements SyncStatusBroadcastReceiver.SyncStatusListener, P2pProcessingStatusBroadcastReceiver.StatusUpdate {

    private static Flavor flavor = new ChwApplicationFlv();
    private AppExecutors appExecutors;
    private CommonFtsObject commonFtsObject;
    private P2pProcessingStatusBroadcastReceiver p2pProcessingStatusBroadcastReceiver;
    private boolean isBulkProcessing;
    private boolean fetchedLoad = false;
    private RequestQueue mRequestQueue;

    public static final String TAG = ChwApplication.class.getSimpleName();
    private static final String ONESIGNAL_APP_ID = "a074b7f3-c15f-4838-8fd3-6974c6adee87";


    public static Flavor getApplicationFlavor() {
        return flavor;
    }

    public static void prepareDirectories() {
        prepareGuideBooksFolder();
        prepareCounselingDocsFolder();
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);

        }
    }


    public static void prepareGuideBooksFolder() {
        String rootFolder = getGuideBooksDirectory();
        createFolders(rootFolder, false);
        boolean onSdCard = FileUtils.canWriteToExternalDisk();
        if (onSdCard)
            createFolders(rootFolder, true);
    }

    public static void prepareCounselingDocsFolder() {
        String rootFolder = getCounselingDocsDirectory();
        createFolders(rootFolder, false);
        boolean onSdCard = FileUtils.canWriteToExternalDisk();
        if (onSdCard)
            createFolders(rootFolder, true);
    }

    private static void createFolders(String rootFolder, boolean onSdCard) {
        try {
            FileUtils.createDirectory(rootFolder, onSdCard);
        } catch (Exception e) {
            Timber.v(e);
        }
    }

    public static String getGuideBooksDirectory() {
        String[] packageName = ChwApplication.getInstance().getContext().applicationContext().getPackageName().split("\\.");
        String suffix = packageName[packageName.length - 1];
        return "opensrp_guidebooks_" + (suffix.equalsIgnoreCase("chw") ? "liberia" : suffix);
    }

    public static String getCounselingDocsDirectory() {
        String[] packageName = ChwApplication.getInstance().getContext().applicationContext().getPackageName().split("\\.");
        String suffix = packageName[packageName.length - 1];
        return "opensrp_counseling_docs_" + (suffix.equalsIgnoreCase("chw") ? "liberia" : suffix);
    }

    public CommonFtsObject getCommonFtsObject() {
        if (commonFtsObject == null) {

            String[] tables = flavor.getFTSTables();

            Map<String, String[]> searchMap = flavor.getFTSSearchMap();
            Map<String, String[]> sortMap = flavor.getFTSSortMap();

            commonFtsObject = new CommonFtsObject(tables);
            for (String ftsTable : commonFtsObject.getTables()) {
                commonFtsObject.updateSearchFields(ftsTable, searchMap.get(ftsTable));
                commonFtsObject.updateSortFields(ftsTable, sortMap.get(ftsTable));
            }
        }
        return commonFtsObject;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
        context = Context.getInstance();
        context.updateApplicationContext(getApplicationContext());
        context.updateCommonFtsObject(getCommonFtsObject());

        //TODO set this up afresh - OneSignal is deprecated
     /*   // Enable verbose OneSignal logging to debug issues if needed.
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);

        // OneSignal Initialization
        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);*/

        //Necessary to determine the right form to pick from assets
        CoreConstants.JSON_FORM.setLocaleAndAssetManager(ChwApplication.getCurrentLocale(),
                ChwApplication.getInstance().getApplicationContext().getAssets());

        //Setup Navigation menu. Done only once when app is created
        NavigationMenu.setupNavigationMenu(this, new NavigationMenuFlv(), new NavigationModelFlv(),
                getRegisteredActivities(), flavor.hasP2P());


        initializeLibraries();

        // init json helper
        this.jsonSpecHelper = new JsonSpecHelper(this);

        //init Job Manager
        JobManager.create(this).addJobCreator(new ChwJobCreator());

        initOfflineSchedules();

        setOpenSRPUrl();

        Configuration configuration = getApplicationContext().getResources().getConfiguration();
        String language;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            language = configuration.getLocales().get(0).getLanguage();
        } else {
            language = configuration.locale.getLanguage();
        }

        if (language.equals(Locale.FRENCH.getLanguage())) {
            saveLanguage(Locale.FRENCH.getLanguage());
        }

        // create a folder for guidebooks
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                prepareDirectories();
            }
        } else {
            prepareDirectories();
        }

        EventBus.getDefault().register(this);

        if (getApplicationFlavor().hasMap()) {
            initializeMapBox();
        }

        reloadLanguage();
    }

    protected void initializeMapBox() {
        // Init Kujaku
        /*Mapbox.getInstance(getApplicationContext(), BuildConfig.MAPBOX_SDK_ACCESS_TOKEN);
        KujakuLibrary.init(getApplicationContext());*/
    }

    private void initializeLibraries() {
        //Initialize Modules
        P2POptions p2POptions = new P2POptions(true);
        p2POptions.setAuthorizationService(flavor.hasForeignData() ? new LmhAuthorizationService() : new CoreAuthorizationService(false));
        p2POptions.setRecalledIdentifier(new FailSafeRecalledID());

        CoreLibrary.init(context, new ChwSyncConfiguration(), BuildConfig.BUILD_TIMESTAMP, p2POptions);
        CoreLibrary.getInstance().setEcClientFieldsFile(CoreConstants.EC_CLIENT_FIELDS);

        // init libraries
        ImmunizationLibrary.init(context, getRepository(), null, BuildConfig.VERSION_CODE, BuildConfig.DATABASE_VERSION);
        ImmunizationLibrary.getInstance().setAllowSyncImmediately(flavor.saveOnSubmission());

        ConfigurableViewsLibrary.init(context);
        FamilyLibrary.init(context, getMetadata(), BuildConfig.VERSION_CODE, BuildConfig.DATABASE_VERSION);
        AncLibrary.init(context, getRepository(), BuildConfig.VERSION_CODE, BuildConfig.DATABASE_VERSION);
        AncLibrary.getInstance().setSubmitOnSave(flavor.saveOnSubmission());

        PncLibrary.init(context, getRepository(), BuildConfig.VERSION_CODE, BuildConfig.DATABASE_VERSION);
        MalariaLibrary.init(context, getRepository(), BuildConfig.VERSION_CODE, BuildConfig.DATABASE_VERSION);
        FpLibrary.init(context, getRepository(), BuildConfig.VERSION_CODE, BuildConfig.DATABASE_VERSION);
        // Init Reporting library
        ReportingLibrary.init(context, getRepository(), null, BuildConfig.VERSION_CODE, BuildConfig.DATABASE_VERSION);
        GrowthMonitoringConfig growthMonitoringConfig = new GrowthMonitoringConfig();
        growthMonitoringConfig.setWeightForHeightZScoreFile("weight_for_height.csv");
        GrowthMonitoringLibrary.init(context, getRepository(), BuildConfig.VERSION_CODE, BuildConfig.DATABASE_VERSION, growthMonitoringConfig);

        if (hasReferrals() && getOrNull() == null) {
            //Setup referral library and initialize Koin dependencies once
            ReferralLibrary.init(this);
            ReferralLibrary.getInstance().setAppVersion(BuildConfig.VERSION_CODE);
            ReferralLibrary.getInstance().setDatabaseVersion(BuildConfig.DATABASE_VERSION);
        }

        OpdLibrary.init(context, getRepository(),
                new OpdConfiguration.Builder(CoreAllClientsRegisterQueryProvider.class)
                        .setBottomNavigationEnabled(true)
                        .setOpdRegisterRowOptions(AllClientsRegisterRowOptions.class)
                        .build(),
                BuildConfig.VERSION_CODE, BuildConfig.DATABASE_VERSION
        );

        SyncStatusBroadcastReceiver.init(this);
        SyncStatusBroadcastReceiver.getInstance().addSyncStatusListener(this);

        if (p2pProcessingStatusBroadcastReceiver == null)
            p2pProcessingStatusBroadcastReceiver = new P2pProcessingStatusBroadcastReceiver(this);

        LocalBroadcastManager.getInstance(this)
                .registerReceiver(p2pProcessingStatusBroadcastReceiver
                        , new IntentFilter(AllConstants.PeerToPeer.PROCESSING_ACTION));


        LocationHelper.init(new ArrayList<>(Arrays.asList(BuildConfig.DEBUG ? BuildConfig.ALLOWED_LOCATION_LEVELS_DEBUG : BuildConfig.ALLOWED_LOCATION_LEVELS)), BuildConfig.DEBUG ? BuildConfig.DEFAULT_LOCATION_DEBUG : BuildConfig.DEFAULT_LOCATION);

        // set up processor
        FamilyLibrary.getInstance().setClientProcessorForJava(ChwClientProcessor.getInstance(getApplicationContext()));

        // Set display date format for date pickers in native forms
        Form form = new Form();
        form.setDatePickerDisplayFormat("dd MMM yyyy");

        NativeFormLibrary.getInstance().setPerformFormTranslation(true);
        NativeFormLibrary.getInstance().setClientFormDao(CoreLibrary.getInstance().context().getClientFormRepository());
        // ThinkMD library
        ThinkMDConfig thinkMDConfig = new ThinkMDConfig();
        thinkMDConfig.setThinkmdEndPoint(BuildConfig.THINKMD_BASE_URL);
        thinkMDConfig.setThinkmdBaseUrl(BuildConfig.THINKMD_END_POINT);
        ThinkMDLibrary.init(getApplicationContext(), thinkMDConfig);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        GlobalContextKt.stopKoin();
    }

    @Override
    public void logoutCurrentUser() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        getApplicationContext().startActivity(intent);
        context.userService().logoutSession();
    }

    @Override
    public FamilyMetadata getMetadata() {
        FamilyMetadata metadata = FormUtils.getFamilyMetadata(new FamilyProfileActivity(), getDefaultLocationLevel(), getFacilityHierarchy(), getFamilyLocationFields());

        HashMap<String, String> setting = new HashMap<>();
        setting.put(Constants.CustomConfig.FAMILY_FORM_IMAGE_STEP, JsonFormUtils.STEP1);
        setting.put(Constants.CustomConfig.FAMILY_MEMBER_FORM_IMAGE_STEP, JsonFormUtils.STEP2);
        metadata.setCustomConfigs(setting);
        return metadata;
    }

    @Override
    public ArrayList<String> getAllowedLocationLevels() {
        return new ArrayList<>(Arrays.asList(BuildConfig.DEBUG ? BuildConfig.ALLOWED_LOCATION_LEVELS_DEBUG : BuildConfig.ALLOWED_LOCATION_LEVELS));
    }

    @Override
    public ArrayList<String> getFacilityHierarchy() {
        return new ArrayList<>(Arrays.asList(BuildConfig.LOCATION_HIERACHY));
    }

    @Override
    public String getDefaultLocationLevel() {
        return BuildConfig.DEBUG ? BuildConfig.DEFAULT_LOCATION_DEBUG : BuildConfig.DEFAULT_LOCATION;
    }

    @NotNull
    public Map<String, Class> getRegisteredActivities() {
        Map<String, Class> registeredActivities = new HashMap<>();
        registeredActivities.put(CoreConstants.REGISTERED_ACTIVITIES.HOUSEHOLD_REGISTER_ACTIVITY, HouseholdIndexActivity.class);
        registeredActivities.put(CoreConstants.REGISTERED_ACTIVITIES.FAMILY_REGISTER_ACTIVITY, FamilyRegisterActivity.class);
        registeredActivities.put(CoreConstants.REGISTERED_ACTIVITIES.MOTHER_REGISTER_ACTIVITY, MotherIndexActivity.class);
        registeredActivities.put(CoreConstants.REGISTERED_ACTIVITIES.CASE_PLAN_REGISTER_ACTIVITY, CasePlanRegisterActivity.class );
        registeredActivities.put(CoreConstants.REGISTERED_ACTIVITIES.INDEX_REGISTER_ACTIVITY, IndexRegisterActivity.class);
        registeredActivities.put(CoreConstants.REGISTERED_ACTIVITIES.BENEFICIARIES_REGISTER_ACTIVITY, BeneficiariesRegisterActivity.class);
        registeredActivities.put(CoreConstants.REGISTERED_ACTIVITIES.IDENTIFICATION_REGISTER_ACTIVITY, IdentificationRegisterActivity.class);
        registeredActivities.put(CoreConstants.REGISTERED_ACTIVITIES.DASHBOARD_ACTIVITY, DashboardActivity.class);
        registeredActivities.put(CoreConstants.REGISTERED_ACTIVITIES.HTS_ACTIVITY, HivTestingServiceActivity.class);
        registeredActivities.put(CoreConstants.REGISTERED_ACTIVITIES.PMTCT, PMTCTRegisterActivity.class);
        if (BuildConfig.USE_UNIFIED_REFERRAL_APPROACH) {
            registeredActivities.put(CoreConstants.REGISTERED_ACTIVITIES.REFERRALS_REGISTER_ACTIVITY, ReferralRegisterActivity.class);
        }
        if (BuildConfig.USE_UNIFIED_REFERRAL_APPROACH && BuildConfig.BUILD_FOR_BORESHA_AFYA_SOUTH) {
            registeredActivities.put(CoreConstants.REGISTERED_ACTIVITIES.ALL_CLIENTS_REGISTERED_ACTIVITY, AllClientsRegisterActivity.class);
        }
        registeredActivities.put(CoreConstants.REGISTERED_ACTIVITIES.FP_REGISTER_ACTIVITY, FpRegisterActivity.class);
        registeredActivities.put(CoreConstants.REGISTERED_ACTIVITIES.UPDATES_REGISTER_ACTIVITY, UpdatesRegisterActivity.class);
        return registeredActivities;
    }

    @Override
    public Repository getRepository() {
        try {
            if (repository == null) {
                repository = new ChwRepository(getInstance().getApplicationContext(), context);
            }
        } catch (UnsatisfiedLinkError e) {
            Timber.e(e);
        }
        return repository;
    }

    public void setOpenSRPUrl() {
        AllSharedPreferences preferences = Utils.getAllSharedPreferences();
        preferences.savePreference(AllConstants.DRISHTI_BASE_URL,
                BuildConfig.DEBUG ? BuildConfig.opensrp_url_debug : BuildConfig.opensrp_url
        );
    }

    public boolean hasReferrals() {
        return flavor.hasReferrals();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onVisitEvent(Visit visit) {
        if (visit != null) {
            Timber.v("Visit Submitted re processing Schedule for event ' %s '  : %s", visit.getVisitType(), visit.getBaseEntityId());
            if (CoreLibrary.getInstance().isPeerToPeerProcessing() || SyncStatusBroadcastReceiver.getInstance().isSyncing() || isBulkProcessing())
                return;

            ChwScheduleTaskExecutor.getInstance().execute(visit.getBaseEntityId(), visit.getVisitType(), visit.getDate());

        }
    }

    public AppExecutors getAppExecutors() {
        if (appExecutors == null) {
            appExecutors = new AppExecutors();
        }
        return appExecutors;
    }

    @Override
    public P2PClassifier<JSONObject> getP2PClassifier() {
        return flavor.hasForeignData() ? new ChwLocationBasedClassifier() : null;
    }

    @Override
    public boolean getChildFlavorUtil() {
        return flavor.getChildFlavorUtil();
    }

    @Override
    public void onSyncStart() {
        Timber.v("Sync started");
        setBulkProcessing(false);
        fetchedLoad = false;
    }

    @Override
    public void onSyncInProgress(FetchStatus fetchStatus) {
        if ((fetchStatus == FetchStatus.fetched) || (fetchStatus == FetchStatus.fetchProgress))
            fetchedLoad = true;

        Timber.v("Sync progressing : Status " + FetchStatus.fetched.name());
    }

    @Override
    public boolean allowLazyProcessing() {
        return true;
    }

    @Override
    public String[] lazyProcessedEvents() {
        return new String[]{
                CoreConstants.EventType.CHILD_HOME_VISIT,
                CoreConstants.EventType.FAMILY_KIT,
                CoreConstants.EventType.CHILD_VISIT_NOT_DONE,
                CoreConstants.EventType.WASH_CHECK,
                CoreConstants.EventType.ROUTINE_HOUSEHOLD_VISIT,
                CoreConstants.EventType.MINIMUM_DIETARY_DIVERSITY,
                CoreConstants.EventType.MUAC,
                CoreConstants.EventType.LLITN,
                CoreConstants.EventType.ECD,
                CoreConstants.EventType.DEWORMING,
                CoreConstants.EventType.VITAMIN_A,
                CoreConstants.EventType.EXCLUSIVE_BREASTFEEDING,
                CoreConstants.EventType.MNP,
                CoreConstants.EventType.IPTP_SP,
                CoreConstants.EventType.TT,
                CoreConstants.EventType.VACCINE_CARD_RECEIVED,
                CoreConstants.EventType.DANGER_SIGNS_BABY,
                CoreConstants.EventType.PNC_HEALTH_FACILITY_VISIT,
                CoreConstants.EventType.KANGAROO_CARE,
                CoreConstants.EventType.UMBILICAL_CORD_CARE,
                CoreConstants.EventType.IMMUNIZATION_VISIT,
                CoreConstants.EventType.OBSERVATIONS_AND_ILLNESS,
                CoreConstants.EventType.SICK_CHILD,
                CoreConstants.EventType.ANC_HOME_VISIT,
                org.smartregister.chw.anc.util.Constants.EVENT_TYPE.ANC_HOME_VISIT_NOT_DONE,
                org.smartregister.chw.anc.util.Constants.EVENT_TYPE.ANC_HOME_VISIT_NOT_DONE_UNDO,
                CoreConstants.EventType.PNC_HOME_VISIT,
                CoreConstants.EventType.PNC_HOME_VISIT_NOT_DONE,
                FamilyPlanningConstants.EventType.FP_FOLLOW_UP_VISIT,
                FamilyPlanningConstants.EventType.FAMILY_PLANNING_REGISTRATION,
                org.smartregister.chw.malaria.util.Constants.EVENT_TYPE.MALARIA_FOLLOW_UP_VISIT,
                CoreConstants.EventType.CHILD_VACCINE_CARD_RECEIVED,
                CoreConstants.EventType.BIRTH_CERTIFICATION
        };
    }

    @Override
    public void onSyncComplete(FetchStatus fetchStatus) {
        if (fetchedLoad) {
            Timber.v("Sync complete scheduling");
            //startProcessing();
            fetchedLoad = false;
        }
    }

    private void startProcessing() {
        ScheduleJob.scheduleJobImmediately(ScheduleJob.TAG);
        BasePncCloseJob.scheduleJobImmediately(BasePncCloseJob.TAG);
    }

    @Override
    public void onStatusUpdate(boolean isProcessing) {
        if (!isProcessing)
            startProcessing();
    }

    public boolean isBulkProcessing() {
        return isBulkProcessing;
    }

    public void setBulkProcessing(boolean bulkProcessing) {
        isBulkProcessing = bulkProcessing;
    }

    public interface Flavor {
        boolean hasP2P();

        boolean syncUsingPost();

        boolean hasReferrals();

        boolean flvSetFamilyLocation();

        boolean hasANC();

        boolean hasPNC();

        boolean hasChildSickForm();

        boolean hasFamilyPlanning();

        boolean hasMalaria();

        boolean hasWashCheck();

        boolean hasFamilyKitCheck();

        boolean hasRoutineVisit();

        boolean hasServiceReport();

        boolean hasStockUsageReport();

        boolean hasJobAids();

        boolean hasQR();

        boolean hasPinLogin();

        boolean hasReports();

        boolean hasTasks();

        boolean hasDefaultDueFilterForChildClient();

        boolean launchChildClientsAtLogin();

        boolean hasJobAidsVitaminAGraph();

        boolean hasJobAidsDewormingGraph();

        boolean hasChildrenMNPSupplementationGraph();

        boolean hasJobAidsBreastfeedingGraph();

        boolean hasJobAidsBirthCertificationGraph();

        boolean hasSurname();

        boolean showMyCommunityActivityReport();

        boolean useThinkMd();

        boolean hasDeliveryKit();

        boolean hasFamilyLocationRow();

        boolean usesPregnancyRiskProfileLayout();

        boolean splitUpcomingServicesView();

        boolean getChildFlavorUtil();

        boolean showChildrenUnder5();

        boolean hasForeignData();

        boolean showNoDueVaccineView();

        boolean prioritizeChildNameOnChildRegister();

        boolean showChildrenUnderFiveAndGirlsAgeNineToEleven();

        boolean dueVaccinesFilterInChildRegister();

        boolean includeCurrentChild();

        boolean saveOnSubmission();

        boolean relaxVisitDateRestrictions();

        boolean showLastNameOnChildProfile();

        boolean showChildrenAboveTwoDueStatus();

        boolean showFamilyServicesScheduleWithChildrenAboveTwo();

        boolean showIconsForChildrenUnderTwoAndGirlsAgeNineToEleven();

        boolean hasMap();

        boolean showsPhysicallyDisabledView();

        boolean hasEventDateOnFamilyProfile();

        String[] getFTSTables();

        Map<String, String[]> getFTSSearchMap();

        Map<String, String[]> getFTSSortMap();

        ChwApplication chwAppInstance();
    }

}