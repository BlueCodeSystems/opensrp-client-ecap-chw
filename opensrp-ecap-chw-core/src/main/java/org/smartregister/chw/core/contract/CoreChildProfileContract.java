package org.smartregister.chw.core.contract;

import android.content.Context;
import android.util.Pair;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;
import org.smartregister.chw.core.domain.ProfileTask;
import org.smartregister.chw.core.model.ChildVisit;
import org.smartregister.chw.core.utils.CoreChildService;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.FetchStatus;
import org.smartregister.domain.Task;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.view.contract.BaseProfileContract;

import java.util.Set;

public interface CoreChildProfileContract {

    interface View extends BaseProfileContract.View {

        Context getApplicationContext();

        Context getContext();

        String getString(int resourceId);

        void startFormActivity(JSONObject form);

        void refreshProfile(final FetchStatus fetchStatus);

        void displayShortToast(int resourceId);

        void setProfileImage(String baseEntityId);

        void setParentName(String parentName);

        void setGender(String gender);

        void setAddress(String address);

        void setId(String id);

        void setProfileName(String fullName);

        void setAge(String age);

        void setVisitButtonDueStatus();

        void setVisitButtonOverdueStatus();

        void setVisitNotDoneThisMonth(boolean withinEditPeriod);

        void setLastVisitRowView(String days);

        void setServiceNameDue(String name, String dueDate);

        void setServiceNameOverDue(String name, String dueDate);

        void setServiceNameUpcoming(String name, String dueDate);

        void setVisitLessTwentyFourView(String monthName);

        void setVisitAboveTwentyFourView();

        void setFamilyHasNothingDue();

        void setFamilyHasNothingElseDue();

        void setFamilyHasServiceDue();

        void setFamilyHasServiceOverdue();

        void setNoButtonView();

        void setDueTodayServices();

        CoreChildProfileContract.Presenter presenter();

        void updateHasPhone(boolean hasPhone);

        void enableEdit(boolean enable);

        void hideProgressBar();

        void openVisitMonthView();

        void showUndoVisitNotDoneView();

        void updateAfterBackgroundProcessed();

        void setClientTasks(Set<Task> taskList);

        void setProgressBarState(@NotNull Boolean state);

        void onJsonProcessed(String eventType, String taskType, @Nullable ProfileTask profileTask);

        void fetchProfileTasks();

        void onProfileTaskFetched(@NonNull String taskType, @Nullable ProfileTask profileTask);

        void thinkMdAssessmentProcessed();
    }

    interface Flavor {
        void togglePhysicallyDisabled(boolean show);
    }

    interface Presenter extends BaseProfileContract.Presenter {

        void updateChildProfile(String jsonObject);

        CoreChildProfileContract.View getView();

        CoreChildProfileContract.Flavor getFlavor();

        void fetchProfileData();

        void fetchTasks();

        void updateChildCommonPerson(String baseEntityId);

        void updateVisitNotDone(long value);

        void undoVisitNotDone();

        void fetchVisitStatus(String baseEntityId);

        void fetchUpcomingServiceAndFamilyDue(String baseEntityId);

        void processBackGroundEvent();

        void createSickChildEvent(AllSharedPreferences allSharedPreferences, String jsonString) throws Exception;

        void createSickChildFollowUpEvent(AllSharedPreferences allSharedPreferences, String jsonString) throws Exception;

        void fetchProfileTask(@NotNull Context context, @NotNull String baseEntityID);

        void onProfileTaskFetched(@NonNull String taskType, @Nullable ProfileTask profileTask);

        void processJson(@NotNull Context context, String eventType, @Nullable String tableName, String jsonString);

        void onJsonProcessed(String eventType, String taskType, @Nullable ProfileTask profileTask);

        void startFormForEdit(String title, CommonPersonObjectClient client);

        CommonPersonObjectClient getChildClient();

        void startSickChildForm(CommonPersonObjectClient client);

        void createCarePlanEvent(@NotNull Context context, @NotNull String encodedBundle);

        void carePlanEventCreated();

        void launchThinkMDHealthAssessment(@NotNull Context context);

        void showThinkMDCarePlan(@NotNull Context context);
    }

    interface Interactor {
        void updateVisitNotDone(long value, CoreChildProfileContract.InteractorCallBack callback);

        void refreshChildVisitBar(Context context, String baseEntityId, CoreChildProfileContract.InteractorCallBack callback);

        void refreshUpcomingServiceAndFamilyDue(Context context, String familyId, String baseEntityId, CoreChildProfileContract.InteractorCallBack callback);

        void onDestroy(boolean isChangingConfiguration);

        void updateChildCommonPerson(String baseEntityId);

        void refreshProfileView(String baseEntityId, boolean isForEdit, CoreChildProfileContract.InteractorCallBack callback);

        void getClientTasks(String planId, String baseEntityId, CoreChildProfileContract.InteractorCallBack callback);

        void saveRegistration(final Pair<Client, Event> pair, final String jsonString, final boolean isEditMode, final CoreChildProfileContract.InteractorCallBack callBack);

        JSONObject getAutoPopulatedJsonEditFormString(String formName, String title, Context context, CommonPersonObjectClient client);

        void processBackGroundEvent(final CoreChildProfileContract.InteractorCallBack callback);

        String getCurrentLocationID(Context context);

        void createSickChildEvent(AllSharedPreferences allSharedPreferences, String jsonString) throws Exception;

        void createSickChildFollowUpEvent(AllSharedPreferences allSharedPreferences, String jsonString) throws Exception;

        String getChildBaseEntityId();

        void setChildBaseEntityId(String childBaseEntityId);

        void processJson(@NotNull Context context, String eventType, String tableName, String jsonString, @NonNull Presenter presenter);

        void fetchProfileTask(@NotNull Context context, @NotNull String baseEntityID, @Nullable Presenter presenter);

        void createCarePlanEvent(@NotNull Context context, @NotNull String encodedBundle, final CoreChildProfileContract.InteractorCallBack callback);

        void launchThinkMDHealthAssessment(@NotNull Context context);

        void showThinkMDCarePlan(@NotNull Context context, final CoreChildProfileContract.InteractorCallBack callback);
    }

    interface InteractorCallBack {
        void updateChildVisit(ChildVisit childVisit);

        void updateChildService(CoreChildService childService);

        void updateFamilyMemberServiceDue(String serviceDueStatus);

        void startFormForEdit(String title, CommonPersonObjectClient client);

        void startSickChildReferralForm();

        void startSickChildForm(CommonPersonObjectClient client);

        void refreshProfileTopSection(CommonPersonObjectClient client, CommonPersonObject commonPersonObject);

        void hideProgressBar();

        void onRegistrationSaved(boolean isEditMode);

        void setFamilyID(String familyID);

        void setFamilyName(String familyName);

        void setFamilyHeadID(String familyHeadID);

        void setPrimaryCareGiverID(String primaryCareGiverID);

        void updateVisitNotDone();

        void undoVisitNotDone();

        void updateAfterBackGroundProcessed();

        void setClientTasks(Set<Task> taskList);

        void carePlanEventCreated();

        void noThinkMDCarePlanFound();
    }

    interface Model {

        JSONObject getFormAsJson(String formName, String entityId, String currentLocationId, String familyID) throws Exception;
    }

}
