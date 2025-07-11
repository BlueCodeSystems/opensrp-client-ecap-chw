package org.smartregister.chw.core.presenter;

import android.content.Context;
import android.content.res.Resources;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.chw.core.R;
import org.smartregister.chw.core.contract.CoreChildProfileContract;
import org.smartregister.chw.core.domain.ProfileTask;
import org.smartregister.chw.core.model.ChildVisit;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.dao.AbstractDao;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.Repository;

import java.util.Calendar;
import java.util.Date;

public class CoreChildProfilePresenterTest extends AbstractDao {
    @Mock
    private Repository repository;

    @Mock
    private CoreChildProfileContract.View view;

    @Mock
    private CoreChildProfileContract.Model model;

    private CoreChildProfilePresenter profilePresenter;

    @Mock
    private CoreChildProfileContract.Interactor interactor;

    private String childBaseEntityId = "12345";

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        profilePresenter = new CoreChildProfilePresenter(view, model, "12345");
        setRepository(repository);
    }

    @Test
    public void testRefreshProfileTopSection() {

        Context context = Mockito.mock(Context.class);
        Mockito.doReturn(context).when(view).getContext();

        Resources resources = Mockito.mock(Resources.class);
        Mockito.doReturn(resources).when(context).getResources();

        Mockito.doReturn("12345").when(resources).getString(Mockito.anyInt());
        Mockito.doReturn("String").when(context).getString(Mockito.anyInt());
        Mockito.doReturn("String").when(view).getString(Mockito.anyInt());

        CommonPersonObjectClient client = Mockito.mock(CommonPersonObjectClient.class);
        CommonPersonObject personObject = Mockito.mock(CommonPersonObject.class);
        profilePresenter.refreshProfileTopSection(client, personObject);

        Mockito.verify(view).setParentName(Mockito.any());
        Mockito.verify(view).setProfileName(Mockito.any());
        Mockito.verify(view).setAge(Mockito.any());
        Mockito.verify(view).setAddress(Mockito.any());
        Mockito.verify(view).setGender(Mockito.any());
        Mockito.verify(view).setId(Mockito.any());
        Mockito.verify(view).setProfileImage(Mockito.any());
    }

    @Test
    public void testUpdateChildVisit() {
        ChildVisit childVisit = new ChildVisit();

        childVisit.setVisitStatus(CoreConstants.VisitType.DUE.name());
        profilePresenter.updateChildVisit(childVisit);
        Mockito.verify(view).setVisitButtonDueStatus();

        childVisit.setVisitStatus(CoreConstants.VisitType.OVERDUE.name());
        profilePresenter.updateChildVisit(childVisit);
        Mockito.verify(view).setVisitButtonOverdueStatus();

        childVisit.setVisitStatus(CoreConstants.VisitType.LESS_TWENTY_FOUR.name());
        childVisit.setLastVisitMonthName("Today");
        profilePresenter.updateChildVisit(childVisit);
        Mockito.verify(view).setVisitLessTwentyFourView(childVisit.getLastVisitMonthName());

        childVisit.setVisitStatus(CoreConstants.VisitType.VISIT_THIS_MONTH.name());
        profilePresenter.updateChildVisit(childVisit);
        Mockito.verify(view).setVisitAboveTwentyFourView();

        // multiple conditions
        childVisit.setVisitStatus(CoreConstants.VisitType.NOT_VISIT_THIS_MONTH.name());
        childVisit.setLastVisitTime(new Date().getTime());

        profilePresenter.updateChildVisit(childVisit);

        Mockito.verify(view).setVisitNotDoneThisMonth(Mockito.anyBoolean());
        Mockito.verify(view).setLastVisitRowView(childVisit.getLastVisitDays());

    }

    @Test
    public void testGetView() {
        CoreChildProfileContract.View myView = profilePresenter.getView();
        Assert.assertEquals(view, myView);
    }

    @Test
    public void testIsWithinEditPeriod() {
        Calendar calendarWithInEdit = Calendar.getInstance();
        calendarWithInEdit.add(Calendar.HOUR, -12);
        Assert.assertTrue(profilePresenter.isWithinEditPeriod(calendarWithInEdit.getTimeInMillis()));
    }

    @Test
    public void testIsNotWithinEditPeriod() {
        Calendar calendarNotWithInEdit = Calendar.getInstance();
        calendarNotWithInEdit.add(Calendar.HOUR, -25);
        Assert.assertFalse(profilePresenter.isWithinEditPeriod(calendarNotWithInEdit.getTimeInMillis()));
    }

    @Test
    public void testFetchProfileData() {
        profilePresenter.setInteractor(interactor);
        profilePresenter.fetchProfileData();

        Mockito.verify(interactor).refreshProfileView(childBaseEntityId, false, profilePresenter);
    }

    @Test
    public void testFetchTasks() {
        profilePresenter.setInteractor(interactor);
        profilePresenter.fetchTasks();

        Mockito.verify(interactor).getClientTasks(CoreConstants.REFERRAL_PLAN_ID, childBaseEntityId, profilePresenter);
    }

    @Test
    public void testUpdateChildCommonPerson() {
        profilePresenter.setInteractor(interactor);
        profilePresenter.updateChildCommonPerson(childBaseEntityId);

        Mockito.verify(interactor).updateChildCommonPerson(childBaseEntityId);
    }

    @Test
    public void updateVisitNotDone() {
        profilePresenter.setInteractor(interactor);

        long value = 1234L;
        profilePresenter.updateVisitNotDone(value);
        Mockito.verify(interactor).updateVisitNotDone(value, profilePresenter);
    }


    @Test
    public void testFetchUpcomingServiceAndFamilyDue() {
        profilePresenter.setInteractor(interactor);

        Context context = Mockito.mock(Context.class);
        Mockito.doReturn(context).when(view).getContext();
        ReflectionHelpers.setField(profilePresenter, "familyID", "familyID");

        profilePresenter.fetchUpcomingServiceAndFamilyDue(childBaseEntityId);
        Mockito.verify(interactor).refreshUpcomingServiceAndFamilyDue(context, "familyID", childBaseEntityId, profilePresenter);
    }

    @Test
    public void testProcessBackGroundEvent() {
        profilePresenter.setInteractor(interactor);

        profilePresenter.processBackGroundEvent();
        Mockito.verify(interactor).processBackGroundEvent(profilePresenter);
    }

    @Test
    public void testCreateSickChildEvent() throws Exception {
        profilePresenter.setInteractor(interactor);

        AllSharedPreferences allSharedPreferences = Mockito.mock(AllSharedPreferences.class);
        String jsonString = "{}";

        profilePresenter.createSickChildEvent(allSharedPreferences, jsonString);
        Mockito.verify(interactor).createSickChildEvent(allSharedPreferences, jsonString);
    }

    @Test
    public void testFetchProfileTask() {
        profilePresenter.setInteractor(interactor);
        Context context = Mockito.mock(Context.class);

        profilePresenter.fetchProfileTask(context, childBaseEntityId);
        Mockito.verify(interactor).fetchProfileTask(context, childBaseEntityId, profilePresenter);
    }

    @Test
    public void testOnProfileTaskFetched() {
        ProfileTask profileTask = Mockito.mock(ProfileTask.class);
        String taskType = "taskType";

        profilePresenter.onProfileTaskFetched(taskType, profileTask);
        Mockito.verify(view).onProfileTaskFetched(taskType, profileTask);
    }

    @Test
    public void testProcessJson() {
        profilePresenter.setInteractor(interactor);
        Context context = Mockito.mock(Context.class);
        String eventType = "eventType";
        String tableName = "tableName";
        String jsonString = "jsonString";


        profilePresenter.processJson(context, eventType, tableName, jsonString);

        Mockito.verify(view).setProgressBarState(true);
        Mockito.verify(interactor).processJson(context, eventType, tableName, jsonString, profilePresenter);
    }

    @Test
    public void onJsonProcessed() {
        String eventType = "eventType";
        String taskType = "taskType";
        ProfileTask profileTask = Mockito.mock(ProfileTask.class);

        profilePresenter.onJsonProcessed(eventType, taskType, profileTask);

        Mockito.verify(view).setProgressBarState(false);
        Mockito.verify(view).onJsonProcessed(eventType, taskType, profileTask);
    }

    @Test
    public void testLaunchThinkMDHealthAssessment() {
        profilePresenter.setInteractor(interactor);

        Context context = Mockito.mock(Context.class);
        Mockito.doReturn(context).when(view).getContext();

        profilePresenter.launchThinkMDHealthAssessment(context);
        Mockito.verify(interactor).launchThinkMDHealthAssessment(context);
    }

    @Test
    public void testShowThinkMDCarePlan() {
        profilePresenter.setInteractor(interactor);

        Context context = Mockito.mock(Context.class);
        Mockito.doReturn(context).when(view).getContext();

        profilePresenter.showThinkMDCarePlan(context);
        Mockito.verify(interactor).showThinkMDCarePlan(context, profilePresenter);
    }

    @Test
    public void testCreateCarePlanEvent() {
        profilePresenter.setInteractor(interactor);

        Context context = Mockito.mock(Context.class);
        Mockito.doReturn(context).when(view).getContext();

        String encodedBundle = "";
        profilePresenter.createCarePlanEvent(context, encodedBundle);
        Mockito.verify(interactor).createCarePlanEvent(context, encodedBundle, profilePresenter);
    }

    @Test
    public void onNoThinkMDCarePlanFound() {
        profilePresenter.noThinkMDCarePlanFound();
        Mockito.verify(view).displayToast(R.string.no_thinkmd_care_plan_found);
    }

    @Test
    public void onCarePlanEventCreated() {
        profilePresenter.carePlanEventCreated();
        Mockito.verify(view).displayToast(R.string.thinkmd_assessment_saved);
    }

}
