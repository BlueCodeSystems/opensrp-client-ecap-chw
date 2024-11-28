package org.smartregister.chw.core.interactor;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Pair;

import androidx.annotation.VisibleForTesting;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.anc.AncLibrary;
import org.smartregister.chw.anc.domain.Visit;
import org.smartregister.chw.anc.util.NCUtils;
import org.smartregister.chw.core.R;
import org.smartregister.chw.core.activity.DisplayCarePlanActivity;
import org.smartregister.chw.core.application.CoreChwApplication;
import org.smartregister.chw.core.contract.CoreChildProfileContract;
import org.smartregister.chw.core.dao.AlertDao;
import org.smartregister.chw.core.dao.ChwNotificationDao;
import org.smartregister.chw.core.domain.ProfileTask;
import org.smartregister.chw.core.enums.ImmunizationState;
import org.smartregister.chw.core.repository.ChwTaskRepository;
import org.smartregister.chw.core.utils.BaChildUtilsFlv;
import org.smartregister.chw.core.utils.ChildDBConstants;
import org.smartregister.chw.core.utils.ChwServiceSchedule;
import org.smartregister.chw.core.utils.CoreChildService;
import org.smartregister.chw.core.utils.CoreChildUtils;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.CoreJsonFormUtils;
import org.smartregister.chw.core.utils.CoreReferralUtils;
import org.smartregister.chw.core.utils.Utils;
import org.smartregister.chw.core.utils.VaccineScheduleUtil;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.clientandeventmodel.Obs;
import org.smartregister.commonregistry.AllCommonsRepository;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.domain.Alert;
import org.smartregister.domain.AlertStatus;
import org.smartregister.domain.Photo;
import org.smartregister.domain.Task;
import org.smartregister.family.FamilyLibrary;
import org.smartregister.family.util.AppExecutors;
import org.smartregister.family.util.Constants;
import org.smartregister.family.util.DBConstants;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.BaseRepository;
import org.smartregister.repository.TaskRepository;
import org.smartregister.repository.UniqueIdRepository;
import org.smartregister.sync.ClientProcessorForJava;
import org.smartregister.sync.helper.ECSyncHelper;
import org.smartregister.thinkmd.ThinkMDLibrary;
import org.smartregister.util.FormUtils;
import org.smartregister.util.ImageUtils;
import org.smartregister.view.LocationPickerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import io.reactivex.Observable;
import timber.log.Timber;

import static org.smartregister.chw.core.dao.ChildDao.queryColumnWithIdentifier;
import static org.smartregister.chw.core.utils.CoreConstants.DB_CONSTANTS.BASE_ENTITY_ID;
import static org.smartregister.chw.core.utils.CoreConstants.DB_CONSTANTS.THINKMD_ID;
import static org.smartregister.chw.core.utils.CoreConstants.INTENT_KEY.CONTENT_TO_DISPLAY;
import static org.smartregister.chw.core.utils.Utils.getDuration;
import static org.smartregister.chw.core.utils.Utils.getFormTag;
import static org.smartregister.thinkmd.utils.Constants.THINKMD_FHIR_BUNDLE;
import static org.smartregister.thinkmd.utils.Utils.decodeBase64;

public class CoreChildProfileInteractor implements CoreChildProfileContract.Interactor {
    public static final String TAG = CoreChildProfileInteractor.class.getName();
    protected AppExecutors appExecutors;
    public CommonPersonObjectClient pClient;
    private Map<String, Date> vaccineList = new LinkedHashMap<>();
    private String childBaseEntityId;

    public CoreChildProfileInteractor() {
        this(new AppExecutors());
    }

    @VisibleForTesting
    CoreChildProfileInteractor(AppExecutors appExecutors) {
        this.appExecutors = appExecutors;
    }

    public Map<String, Date> getVaccineList() {
        return vaccineList;
    }

    public void setVaccineList(Map<String, Date> vaccineList) {
        this.vaccineList = vaccineList;
    }

    //TODO Child Refactor
    public Observable<CoreChildService> updateUpcomingServices(Context context) {
        return Observable.create(e -> {
            // load all the services pending
            String dobString = org.smartregister.util.Utils.getValue(pClient.getColumnmaps(), DBConstants.KEY.DOB, false);
            DateTime dob = new DateTime(Utils.dobStringToDate(dobString));

            VaccineScheduleUtil.updateOfflineAlerts(childBaseEntityId, dob, CoreConstants.SERVICE_GROUPS.CHILD);
            ChwServiceSchedule.updateOfflineAlerts(childBaseEntityId, dob, CoreConstants.SERVICE_GROUPS.CHILD);

            Map<String, Date> receivedVaccines = VaccineScheduleUtil.getReceivedVaccines(childBaseEntityId);
            setVaccineList(receivedVaccines);

            List<Alert> alertList = AlertDao.getActiveAlerts(childBaseEntityId);
            Alert alert = (alertList.size() > 0) ? alertList.get(0) : null;

            if (alert != null) {
                CoreChildService childService = new CoreChildService();
                childService.setServiceName(getTranslatedService(context, alert.scheduleName()));
                childService.setServiceDate(alert.startDate());
                childService.setServiceStatus(getImmunizationStateFromAlert(alert.status()).name());
                e.onNext(childService);
            } else {
                e.onNext(null);
            }
        });
    }

    private String getTranslatedService(Context context, String _name) {
        String name = _name.toLowerCase();

        String num = name.replaceAll("\\D+", "");
        if (name.contains("breastfeeding")) {
            return context.getString(R.string.exclusive_breastfeeding_months, num);
        } else if (name.contains("deworming")) {
            return context.getString(R.string.deworming_number_dose, num);
        } else if (name.contains("vitamin")) {
            return context.getString(R.string.vitamin_a, num);
        } else if (name.contains("mnp")) {
            return context.getString(R.string.mnp_number_pack, num);
        } else {
            String val = name.replace(" ", "_");
            return Utils.getStringResourceByName(val, context);
        }
    }

    private ImmunizationState getImmunizationStateFromAlert(AlertStatus alertStatus) {
        switch (alertStatus) {
            case normal:
                return ImmunizationState.DUE;
            case urgent:
                return ImmunizationState.OVERDUE;
            case upcoming:
                return ImmunizationState.UPCOMING;
            case expired:
                return ImmunizationState.EXPIRED;
            default:
                return ImmunizationState.NO_ALERT;
        }
    }

    public CommonPersonObjectClient getpClient() {
        return pClient;
    }

    public void setpClient(CommonPersonObjectClient pClient) {
        this.pClient = pClient;
    }

    @Override
    public void updateVisitNotDone(long value, CoreChildProfileContract.InteractorCallBack callback) {
        //// TODO: 02/08/19
    }

    @Override
    public void refreshChildVisitBar(Context context, String baseEntityId, CoreChildProfileContract.InteractorCallBack callback) {
        //// TODO: 02/08/19
    }

    @Override
    public void refreshUpcomingServiceAndFamilyDue(Context context, String familyId, String baseEntityId, CoreChildProfileContract.InteractorCallBack callback) {
        //// TODO: 02/08/19
    }

    @Override
    public void onDestroy(boolean isChangingConfiguration) {        //todo
    }

    private String getQuery(String baseEntityId) {
        if (CoreChwApplication.getInstance().getChildFlavorUtil()) {
            return BaChildUtilsFlv.mainSelect(CoreConstants.TABLE_NAME.CHILD, CoreConstants.TABLE_NAME.FAMILY, CoreConstants.TABLE_NAME.FAMILY_MEMBER, baseEntityId);
        } else {
            return CoreChildUtils.mainSelect(CoreConstants.TABLE_NAME.CHILD, CoreConstants.TABLE_NAME.FAMILY, CoreConstants.TABLE_NAME.FAMILY_MEMBER, baseEntityId);
        }
    }

    @Override
    public void updateChildCommonPerson(String baseEntityId) {
        Cursor cursor = null;
        try {
            cursor = getCommonRepository(CoreConstants.TABLE_NAME.CHILD).rawCustomQueryForAdapter(getQuery(baseEntityId));
            if (cursor != null && cursor.moveToFirst()) {
                CommonPersonObject personObject = getCommonRepository(CoreConstants.TABLE_NAME.CHILD).readAllcommonforCursorAdapter(cursor);
                pClient = new CommonPersonObjectClient(personObject.getCaseId(),
                        personObject.getDetails(), "");
                pClient.setColumnmaps(personObject.getColumnmaps());
            }
        } catch (Exception ex) {
            Timber.e(ex, "CoreChildProfileInteractor --> updateChildCommonPerson");
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public CommonRepository getCommonRepository(String tableName) {
        return Utils.context().commonrepository(tableName);
    }

    /**
     * Refreshes family view based on the child id
     *
     * @param baseEntityId
     * @param isForEdit
     * @param callback
     */
    @Override
    public void refreshProfileView(final String baseEntityId, final boolean isForEdit, final CoreChildProfileContract.InteractorCallBack callback) {
        Runnable runnable = () -> {
            // String query = CoreChildUtils.mainSelect(CoreConstants.TABLE_NAME.CHILD, CoreConstants.TABLE_NAME.FAMILY, CoreConstants.TABLE_NAME.FAMILY_MEMBER, baseEntityId);

            Cursor cursor = null;
            try {
                cursor = getCommonRepository(CoreConstants.TABLE_NAME.CHILD).rawCustomQueryForAdapter(getQuery(baseEntityId));
                if (cursor != null && cursor.moveToFirst()) {
                    CommonPersonObject personObject = getCommonRepository(CoreConstants.TABLE_NAME.CHILD).readAllcommonforCursorAdapter(cursor);
                    pClient = new CommonPersonObjectClient(personObject.getCaseId(),
                            personObject.getDetails(), "");
                    pClient.setColumnmaps(personObject.getColumnmaps());
                    final String familyId = Utils.getValue(pClient.getColumnmaps(), ChildDBConstants.KEY.RELATIONAL_ID, false);

                    final CommonPersonObject familyPersonObject = getCommonRepository(Utils.metadata().familyRegister.tableName).findByBaseEntityId(familyId);
                    final CommonPersonObjectClient client = new CommonPersonObjectClient(familyPersonObject.getCaseId(), familyPersonObject.getDetails(), "");
                    client.setColumnmaps(familyPersonObject.getColumnmaps());

                    final String primaryCaregiverID = Utils.getValue(client.getColumnmaps(), DBConstants.KEY.PRIMARY_CAREGIVER, false);
                    final String familyHeadID = Utils.getValue(client.getColumnmaps(), DBConstants.KEY.FAMILY_HEAD, false);
                    final String familyName = Utils.getValue(client.getColumnmaps(), DBConstants.KEY.FIRST_NAME, false);


                    appExecutors.mainThread().execute(() -> {

                        callback.setFamilyHeadID(familyHeadID);
                        callback.setFamilyID(familyId);
                        callback.setPrimaryCareGiverID(primaryCaregiverID);
                        callback.setFamilyName(familyName);

                        if (isForEdit) {
                            callback.startFormForEdit("", pClient);
                        } else {
                            CommonPersonObject commonPersonObject = getCommonRepository(Utils.metadata().familyMemberRegister.tableName).findByBaseEntityId(primaryCaregiverID);
                            callback.refreshProfileTopSection(pClient, commonPersonObject);
                        }
                    });
                }
            } catch (Exception ex) {
                Timber.e(ex, "CoreChildProfileInteractor --> refreshProfileView");
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }


        };

        appExecutors.diskIO().execute(runnable);
    }

    @Override
    public void getClientTasks(String planId, String baseEntityId, CoreChildProfileContract.InteractorCallBack callback) {
        TaskRepository taskRepository = CoreChwApplication.getInstance().getTaskRepository();
        Set<Task> taskList = ((ChwTaskRepository) taskRepository).getReferralTasksForClientByStatus(planId, baseEntityId, CoreConstants.BUSINESS_STATUS.REFERRED);
        callback.setClientTasks(taskList);
    }

    @Override
    public void saveRegistration(final Pair<Client, Event> pair, final String jsonString, final boolean isEditMode, final CoreChildProfileContract.InteractorCallBack callBack) {
        Runnable runnable = () -> {
            saveRegistration(pair, jsonString, isEditMode);
            appExecutors.mainThread().execute(() -> callBack.onRegistrationSaved(isEditMode));
        };

        appExecutors.diskIO().execute(runnable);
    }

    public void saveRegistration(Pair<Client, Event> pair, String jsonString, boolean isEditMode) {

        try {

            Client baseClient = pair.first;
            Event baseEvent = pair.second;

            if (baseClient != null) {
                JSONObject clientJson = new JSONObject(JsonFormUtils.gson.toJson(baseClient));
                if (isEditMode) {
                    JsonFormUtils.mergeAndSaveClient(getSyncHelper(), baseClient);
                } else {
                    getSyncHelper().addClient(baseClient.getBaseEntityId(), clientJson);
                }
            }

            if (baseEvent != null) {
                JSONObject eventJson = new JSONObject(JsonFormUtils.gson.toJson(baseEvent));
                getSyncHelper().addEvent(baseEvent.getBaseEntityId(), eventJson);
            }

            if (!isEditMode && baseClient != null) {
                String opensrpId = baseClient.getIdentifier(Utils.metadata().uniqueIdentifierKey);
                //mark OPENSRP ID as used
                getUniqueIdRepository().close(opensrpId);
            }

            if (baseClient != null || baseEvent != null) {
                String imageLocation = JsonFormUtils.getFieldValue(jsonString, org.smartregister.family.util.Constants.KEY.PHOTO);
                JsonFormUtils.saveImage(baseEvent.getProviderId(), baseClient.getBaseEntityId(), imageLocation);
            }

            long lastSyncTimeStamp = getAllSharedPreferences().fetchLastUpdatedAtDate(0);
            Date lastSyncDate = new Date(lastSyncTimeStamp);
            getClientProcessorForJava().processClient(getSyncHelper().getEvents(lastSyncDate, BaseRepository.TYPE_Unprocessed));
            getAllSharedPreferences().saveLastUpdatedAtDate(lastSyncDate.getTime());
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    public ECSyncHelper getSyncHelper() {
        return FamilyLibrary.getInstance().getEcSyncHelper();
    }

    public UniqueIdRepository getUniqueIdRepository() {
        return FamilyLibrary.getInstance().getUniqueIdRepository();
    }

    public AllSharedPreferences getAllSharedPreferences() {
        return Utils.context().allSharedPreferences();
    }

    public ClientProcessorForJava getClientProcessorForJava() {
        return FamilyLibrary.getInstance().getClientProcessorForJava();
    }

    @Override
    public JSONObject getAutoPopulatedJsonEditFormString(String formName, String title, Context context, CommonPersonObjectClient client) {
        try {
            JSONObject form = FormUtils.getInstance(context).getFormJson(formName);

            if (form != null) {
                form.put(JsonFormUtils.ENTITY_ID, client.getCaseId());
                form.put(JsonFormUtils.ENCOUNTER_TYPE, CoreConstants.EventType.UPDATE_CHILD_REGISTRATION);

                JSONObject metadata = form.getJSONObject(JsonFormUtils.METADATA);
                String lastLocationId = getCurrentLocationID(context);

                metadata.put(JsonFormUtils.ENCOUNTER_LOCATION, lastLocationId);

                form.put(JsonFormUtils.CURRENT_OPENSRP_ID, Utils.getValue(client.getColumnmaps(), DBConstants.KEY.UNIQUE_ID, false));

                JSONObject stepOne = form.getJSONObject(JsonFormUtils.STEP1);

                if (StringUtils.isNotBlank(title)) {
                    stepOne.put(CoreJsonFormUtils.TITLE, title);
                }
                JSONArray jsonArray = stepOne.getJSONArray(JsonFormUtils.FIELDS);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    processPopulatableFields(client, jsonObject, jsonArray);

                }

                return form;
            }
        } catch (Exception e) {
            Timber.e(e, "CoreChildProfileInteractor --> getAutoPopulatedJsonEditFormString");
        }

        return null;
    }

    @Override
    public String getCurrentLocationID(Context context) {
        LocationPickerView lpv = new LocationPickerView(context);
        lpv.init();
        return LocationHelper.getInstance().getOpenMrsLocationId(lpv.getSelectedItem());
    }

    @Override
    public void processBackGroundEvent(CoreChildProfileContract.InteractorCallBack callback) {
        //todo
    }

    @Override
    public void createSickChildEvent(AllSharedPreferences allSharedPreferences, String jsonString) throws Exception {
        CoreReferralUtils.createReferralEvent(allSharedPreferences, jsonString, CoreConstants.TABLE_NAME.CHILD_REFERRAL, getChildBaseEntityId());
    }

    @Override
    public void createSickChildFollowUpEvent(AllSharedPreferences allSharedPreferences, String jsonString) throws Exception {
        Event baseEvent = org.smartregister.chw.anc.util.JsonFormUtils.processJsonForm(allSharedPreferences, CoreReferralUtils.setEntityId(jsonString, getChildBaseEntityId()), CoreConstants.TABLE_NAME.SICK_CHILD_FOLLOW_UP);
        org.smartregister.chw.anc.util.JsonFormUtils.tagEvent(allSharedPreferences, baseEvent);
        String syncLocationId = ChwNotificationDao.getSyncLocationId(baseEvent.getBaseEntityId());
        if (syncLocationId != null) {
            // Allows setting the ID for sync purposes
            baseEvent.setLocationId(syncLocationId);
        }
        NCUtils.processEvent(baseEvent.getBaseEntityId(), new JSONObject(org.smartregister.chw.anc.util.JsonFormUtils.gson.toJson(baseEvent)));
    }

    @Override
    public String getChildBaseEntityId() {
        return childBaseEntityId;
    }

    @Override
    public void setChildBaseEntityId(String childBaseEntityId) {
        this.childBaseEntityId = childBaseEntityId;
    }

    @Override
    public void processJson(@NotNull Context context, String eventType, String tableName, String jsonString, CoreChildProfileContract.Presenter presenter) {
        // save the event in event table
        try {
            final Event baseEvent = org.smartregister.chw.anc.util.JsonFormUtils.processJsonForm(org.smartregister.family.util.Utils.getAllSharedPreferences(), jsonString, tableName);
            NCUtils.processEvent(baseEvent.getBaseEntityId(), new JSONObject(org.smartregister.chw.anc.util.JsonFormUtils.gson.toJson(baseEvent)));
            presenter.onJsonProcessed(eventType, CoreConstants.EventType.SICK_CHILD, getSickChildVisit(context, baseEvent.getBaseEntityId()));
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    private ProfileTask getSickChildVisit(@NotNull Context context, @NotNull String baseEntityID) {
        Visit visit = AncLibrary.getInstance().visitRepository().getLatestVisit(baseEntityID, CoreConstants.EventType.SICK_CHILD);
        ProfileTask task = null;
        if (visit != null) {
            task = new ProfileTask();
            task.setResourceID(R.drawable.rowicon_sickchild);
            task.setTitle(context.getString(R.string.sick_visit_on, new SimpleDateFormat("dd MMM", Locale.ENGLISH).format(visit.getDate())));
            task.setTaskDate(visit.getDate());
        }

        return task;
    }

    @Override
    public void fetchProfileTask(@NotNull Context context, @NotNull String baseEntityID, CoreChildProfileContract.@Nullable Presenter presenter) {
        Runnable runnable = () -> {

            String taskType = CoreConstants.EventType.SICK_CHILD;
            ProfileTask task = getSickChildVisit(context, baseEntityID);

            if (presenter != null) {
                appExecutors.mainThread().execute(() -> presenter.onProfileTaskFetched(taskType, task));
            }
        };

        appExecutors.diskIO().execute(runnable);
    }

    @Override
    public void createCarePlanEvent(@NotNull Context context, @NotNull String encodedBundle, final CoreChildProfileContract.InteractorCallBack callback) {
        Runnable runnable = () -> {
            try {
                // getting thinkMD id from encoded fhir bundle
                String thinkMdId = ThinkMDLibrary.getInstance().getThinkMDPatientId(encodedBundle);
                // getting the baseEntityId mapped to thinkMD
                String childBaseEntityId = queryColumnWithIdentifier(THINKMD_ID, thinkMdId, BASE_ENTITY_ID);
                // creating the event to sync with server
                if (childBaseEntityId != null) {
                    Event carePlanEvent = ThinkMDLibrary.getInstance().createCarePlanEvent(encodedBundle,
                            getFormTag(getAllSharedPreferences()),
                            childBaseEntityId);
                    updateLocalStorage(childBaseEntityId, THINKMD_FHIR_BUNDLE, encodedBundle);

                    for (Obs obs : carePlanEvent.getObs()) {
                        if (StringUtils.isEmpty(obs.getFormSubmissionField())) continue;
                        if (obs.getFormSubmissionField().equals("carePlanDate")) {
                            updateLocalStorage(childBaseEntityId, "care_plan_date", String.valueOf(obs.getValue()));
                            break;
                        }
                    }

                    JSONObject eventPartialJson = new JSONObject(JsonFormUtils.gson.toJson(carePlanEvent));
                    ECSyncHelper.getInstance(context).addEvent(childBaseEntityId, eventPartialJson);
                    appExecutors.mainThread().execute(callback::carePlanEventCreated);
                }
            } catch (Exception e) {
                Timber.e(e);
            }
        };

        appExecutors.diskIO().execute(runnable);
    }

    private void updateLocalStorage(String childBaseEntityId, String fieldName, String fieldValue) {
        if (getChildAllCommonsRepository() != null) {
            ContentValues values = new ContentValues();
            values.put(fieldName, fieldValue);
            getChildAllCommonsRepository().update("ec_child", values, childBaseEntityId);
        }
    }

    private AllCommonsRepository getChildAllCommonsRepository() {
        return CoreChwApplication.getInstance().getAllCommonsRepository("ec_child");
    }

    @Override
    public void launchThinkMDHealthAssessment(@NotNull Context context) {

    }

    @Override
    public void showThinkMDCarePlan(@NotNull Context context, final CoreChildProfileContract.InteractorCallBack callback) {
        Runnable runnable = () -> {
            try {
                String thinkMDFHIRBundle = queryColumnWithIdentifier(BASE_ENTITY_ID, getChildBaseEntityId(), THINKMD_FHIR_BUNDLE);
                appExecutors.mainThread().execute(() -> {

                    if (StringUtils.isEmpty(thinkMDFHIRBundle))
                        callback.noThinkMDCarePlanFound();
                    else {
                        Intent intent = new Intent(context, DisplayCarePlanActivity.class);
                        intent.putExtra(CONTENT_TO_DISPLAY, decodeBase64(thinkMDFHIRBundle));
                        context.startActivity(intent);
                    }
                });

            } catch (Exception e) {
                Timber.e(e);
            }
        };

        appExecutors.diskIO().execute(runnable);
    }

    public void processPopulatableFields(CommonPersonObjectClient client, JSONObject jsonObject, JSONArray jsonArray) throws JSONException {

        switch (jsonObject.getString(JsonFormUtils.KEY).toLowerCase()) {
            case Constants.JSON_FORM_KEY.DOB_UNKNOWN:
                jsonObject.put(JsonFormUtils.READ_ONLY, false);
                JSONObject optionsObject = jsonObject.getJSONArray(org.smartregister.family.util.Constants.JSON_FORM_KEY.OPTIONS).getJSONObject(0);
                optionsObject.put(JsonFormUtils.VALUE, Utils.getValue(client.getColumnmaps(), org.smartregister.family.util.Constants.JSON_FORM_KEY.DOB_UNKNOWN, false));
                break;
            case "age": {
                getAge(client, jsonObject);
            }
            break;
            case DBConstants.KEY.DOB:
                String dobString = Utils.getValue(client.getColumnmaps(), DBConstants.KEY.DOB, false);
                getDob(jsonObject, dobString);
                break;
            case org.smartregister.family.util.Constants.KEY.PHOTO:
                getPhoto(client, jsonObject);
                break;
            case DBConstants.KEY.UNIQUE_ID:
                String uniqueId = Utils.getValue(client.getColumnmaps(), DBConstants.KEY.UNIQUE_ID, false);
                jsonObject.put(JsonFormUtils.VALUE, uniqueId.replace("-", ""));
                break;
            case CoreConstants.JsonAssets.FAM_NAME:
                getFamilyName(client, jsonObject, jsonArray);
                break;
            case CoreConstants.JsonAssets.INSURANCE_PROVIDER:
                jsonObject.put(JsonFormUtils.VALUE, Utils.getValue(client.getColumnmaps(), ChildDBConstants.KEY.INSURANCE_PROVIDER, false));
                break;
            case CoreConstants.JsonAssets.INSURANCE_PROVIDER_NUMBER:
                jsonObject.put(JsonFormUtils.VALUE, Utils.getValue(client.getColumnmaps(), ChildDBConstants.KEY.INSURANCE_PROVIDER_NUMBER, false));
                break;
            case CoreConstants.JsonAssets.INSURANCE_PROVIDER_OTHER:
                jsonObject.put(JsonFormUtils.VALUE, Utils.getValue(client.getColumnmaps(), ChildDBConstants.KEY.INSURANCE_PROVIDER_OTHER, false));
                break;
            case CoreConstants.JsonAssets.DISABILITIES:
                jsonObject.put(JsonFormUtils.VALUE, Utils.getValue(client.getColumnmaps(), ChildDBConstants.KEY.CHILD_PHYSICAL_CHANGE, false));
                break;
            case CoreConstants.JsonAssets.DISABILITY_TYPE:
                jsonObject.put(JsonFormUtils.VALUE, Utils.getValue(client.getColumnmaps(), ChildDBConstants.KEY.TYPE_OF_DISABILITY, false));
                break;
            case CoreConstants.JsonAssets.BIRTH_CERT_AVAILABLE:
                jsonObject.put(JsonFormUtils.VALUE, Utils.getValue(client.getColumnmaps(), ChildDBConstants.KEY.BIRTH_CERT, false));
                break;
            case CoreConstants.JsonAssets.BIRTH_REGIST_NUMBER:
                jsonObject.put(JsonFormUtils.VALUE, Utils.getValue(client.getColumnmaps(), ChildDBConstants.KEY.BIRTH_CERT_NUMBER, false));
                break;
            case CoreConstants.JsonAssets.RHC_CARD:
                jsonObject.put(JsonFormUtils.VALUE, Utils.getValue(client.getColumnmaps(), ChildDBConstants.KEY.RHC_CARD, false));
                break;
            case CoreConstants.JsonAssets.NUTRITION_STATUS:
                jsonObject.put(JsonFormUtils.VALUE, Utils.getValue(client.getColumnmaps(), ChildDBConstants.KEY.NUTRITION_STATUS, false));
                break;
            case DBConstants.KEY.GPS:
                jsonObject.put(JsonFormUtils.VALUE, Utils.getValue(client.getColumnmaps(), DBConstants.KEY.GPS, false));
                break;
            default:
                jsonObject.put(JsonFormUtils.VALUE, Utils.getValue(client.getColumnmaps(), jsonObject.getString(JsonFormUtils.KEY), false));
                break;

        }
    }

    private void getAge(CommonPersonObjectClient client, JSONObject jsonObject) throws JSONException {
        String dobString = Utils.getValue(client.getColumnmaps(), DBConstants.KEY.DOB, false);
        dobString = getDuration(dobString);
        dobString = dobString.contains("y") ? dobString.substring(0, dobString.indexOf("y")) : "0";
        jsonObject.put(JsonFormUtils.VALUE, Integer.valueOf(dobString));
    }

    private void getDob(JSONObject jsonObject, String dobString) throws JSONException {
        if (StringUtils.isNotBlank(dobString)) {
            Date dob = Utils.dobStringToDate(dobString);
            if (dob != null) {
                jsonObject.put(JsonFormUtils.VALUE, JsonFormUtils.dd_MM_yyyy.format(dob));
            }
        }
    }

    private void getPhoto(CommonPersonObjectClient client, JSONObject jsonObject) throws JSONException {
        Photo photo = ImageUtils.profilePhotoByClientID(client.getCaseId(), Utils.getProfileImageResourceIDentifier());
        if (StringUtils.isNotBlank(photo.getFilePath())) {
            jsonObject.put(JsonFormUtils.VALUE, photo.getFilePath());
        }
    }

    private void getFamilyName(CommonPersonObjectClient client, JSONObject jsonObject, JSONArray jsonArray) throws JSONException {
        final String SAME_AS_FAM_NAME = "same_as_fam_name";
        final String SURNAME = "surname";

        String familyName = Utils.getValue(client.getColumnmaps(), ChildDBConstants.KEY.FAMILY_FIRST_NAME, false);
        jsonObject.put(JsonFormUtils.VALUE, familyName);

        String lastName = Utils.getValue(client.getColumnmaps(), DBConstants.KEY.LAST_NAME, false);

        JSONObject sameAsFamName = org.smartregister.util.JsonFormUtils.getFieldJSONObject(jsonArray, SAME_AS_FAM_NAME);
        JSONObject sameOptions = sameAsFamName.getJSONArray(Constants.JSON_FORM_KEY.OPTIONS).getJSONObject(0);

        if (familyName.equals(lastName)) {
            sameOptions.put(JsonFormUtils.VALUE, true);
        } else {
            sameOptions.put(JsonFormUtils.VALUE, false);
        }

        JSONObject surname = org.smartregister.util.JsonFormUtils.getFieldJSONObject(jsonArray, SURNAME);
        if (!familyName.equals(lastName)) {
            surname.put(JsonFormUtils.VALUE, lastName);
        } else {
            surname.put(JsonFormUtils.VALUE, "");
        }
    }

    public enum VisitType {DUE, OVERDUE, LESS_TWENTY_FOUR, VISIT_THIS_MONTH, NOT_VISIT_THIS_MONTH, EXPIRY, VISIT_DONE}

    public enum ServiceType {DUE, OVERDUE, UPCOMING}

    public enum FamilyServiceType {DUE, OVERDUE, NOTHING}
}