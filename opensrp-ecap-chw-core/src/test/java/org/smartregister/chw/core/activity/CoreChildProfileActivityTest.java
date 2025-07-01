package org.smartregister.chw.core.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.appbar.AppBarLayout;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.robolectric.Robolectric;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.core.BaseUnitTest;
import org.smartregister.chw.core.R;
import org.smartregister.chw.core.contract.CoreChildProfileContract;
import org.smartregister.chw.core.domain.ProfileTask;
import org.smartregister.chw.core.presenter.CoreChildProfilePresenter;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.family.util.Constants;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.helper.ImageRenderHelper;

import de.hdodenhof.circleimageview.CircleImageView;
import timber.log.Timber;

/**
 * @author rkodev
 */
public class CoreChildProfileActivityTest extends BaseUnitTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    private CoreChildProfileActivity activity;
    private ActivityController<CoreChildProfileActivity> controller;
    private MemberObject memberObject;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        Context context = Context.getInstance();
        CoreLibrary.init(context);

        //Auto login by default
        context.session().start(context.session().lengthInMilliseconds());

        controller = Robolectric.buildActivity(CoreChildProfileActivity.class).create().start();
        activity = controller.get();
        memberObject = Mockito.mock(MemberObject.class);
        memberObject.setBaseEntityId("some-base-entity-id");
        memberObject.setFamilyName("Some Family Name");
    }

    @Test
    public void testStartMe() {
        Activity activity = Mockito.mock(Activity.class);
        CoreChildProfileActivity.startMe(activity, memberObject, activity.getClass());
        Mockito.verify(activity).startActivity(Mockito.any());
    }

    @Test
    public void testsIntentFilterAccesors() {
        IntentFilter sIntentFilter = new IntentFilter();

        CoreChildProfileActivity.setsIntentFilter(sIntentFilter);
        Assert.assertEquals(sIntentFilter, CoreChildProfileActivity.getsIntentFilter());
    }

    @Test
    public void testOnCreation() {
        // check if created views are found
        activity.onCreation();
        Assert.assertNotNull(ReflectionHelpers.getField(activity, "textViewTitle"));
        Assert.assertNotNull(ReflectionHelpers.getField(activity, "appBarLayout"));
        Assert.assertNotNull(ReflectionHelpers.getField(activity, "imageRenderHelper"));
    }

    @Test
    public void testOnClick() {
        View view = Mockito.mock(View.class);
        ReflectionHelpers.setField(activity, "progressBar", Mockito.mock(ProgressBar.class));
        ReflectionHelpers.setField(activity, "tvEdit", Mockito.mock(TextView.class));

        activity = Mockito.spy(activity);
        CoreChildProfileContract.Presenter presenter = Mockito.mock(CoreChildProfileContract.Presenter.class);
        Mockito.doReturn(presenter).when(activity).presenter();

        // visit not done
        Mockito.doReturn(R.id.textview_visit_not).when(view).getId();
        activity.onClick(view);
        Mockito.verify(presenter).updateVisitNotDone(Mockito.anyLong());

        // visit done
        Mockito.doReturn(R.id.textview_undo).when(view).getId();
        activity.onClick(view);
        Mockito.verify(presenter).updateVisitNotDone(0);
    }

    @Test
    public void testInitializePresenter() {
        activity.initializePresenter();
        Assert.assertNotNull(ReflectionHelpers.getField(activity, "presenter"));
    }

    @Test
    public void testSetUpViews() {
        activity = Mockito.spy(activity);
        activity.setTheme(org.smartregister.R.style.AppTheme); //we need this here
        activity.setContentView(R.layout.activity_child_profile);

        activity.memberObject = memberObject;
        CoreChildProfileContract.Presenter presenter = Mockito.mock(CoreChildProfileContract.Presenter.class);
        Mockito.doReturn(presenter).when(activity).presenter();
        activity.setupViews();

        String[] views = {
                "textViewChildName", "textViewGender", "textViewAddress", "textViewId", "tvEdit", "imageViewProfile",
                "recordVisitPanel", "textViewRecord", "textViewVisitNot", "textViewNotVisitMonth", "textViewLastVisit",
                "textViewUndo", "imageViewCrossChild", "imageViewCross", "layoutRecordView", "layoutNotRecordView", "layoutLastVisitRow",
                "textViewMedicalHistory", "layoutMostDueOverdue", "textViewNameDue", "layoutFamilyHasRow", "textViewFamilyHas",
                "layoutRecordButtonDone", "viewLastVisitRow", "viewMostDueRow", "viewFamilyRow", "progressBar", "viewDividerSickRow",
                "layoutSickVisit", "textViewSickChild"
        };
        for (String view : views) {
            Assert.assertNotNull(ReflectionHelpers.getField(activity, view));
        }
    }

    @Test
    public void testSetupViewPager() {
        Assert.assertNull(activity.setupViewPager(null));
    }

    @Test
    public void testFetchProfileData() {
        activity = Mockito.spy(activity);
        CoreChildProfileContract.Presenter presenter = Mockito.mock(CoreChildProfileContract.Presenter.class);
        Mockito.doReturn(presenter).when(activity).presenter();

        activity.fetchProfileData();
        Mockito.verify(presenter).fetchProfileData();
    }

    @Test
    public void testOnOffsetChanged() {
        activity = Mockito.spy(activity);
        activity.memberObject = memberObject;
        TextView textViewTitle = Mockito.mock(TextView.class);
        ReflectionHelpers.setField(activity, "textViewTitle", textViewTitle);

        AppBarLayout appBarLayout = Mockito.mock(AppBarLayout.class);
        ReflectionHelpers.setField(appBarLayout, "totalScrollRange", 5);

        activity.onOffsetChanged(appBarLayout, -5);
        Mockito.verify(textViewTitle).setText(Mockito.any());
        activity.onOffsetChanged(appBarLayout, 1);

        ReflectionHelpers.setField(appBarLayout, "totalScrollRange", 5);
        activity.onOffsetChanged(appBarLayout, -2);
        Mockito.verify(activity).setUpToolbar();
    }

    @Test
    public void testProcessBackgroundEvent() {
        activity = Mockito.spy(activity);
        CoreChildProfileContract.Presenter presenter = Mockito.mock(CoreChildProfileContract.Presenter.class);
        Mockito.doReturn(presenter).when(activity).presenter();

        RelativeLayout layoutMostDueOverdue = Mockito.mock(RelativeLayout.class);
        View viewMostDueRow = Mockito.mock(View.class);
        ReflectionHelpers.setField(activity, "layoutMostDueOverdue", layoutMostDueOverdue);
        ReflectionHelpers.setField(activity, "viewMostDueRow", viewMostDueRow);

        activity.processBackgroundEvent();
        Mockito.verify(layoutMostDueOverdue).setVisibility(View.GONE);
        Mockito.verify(viewMostDueRow).setVisibility(View.GONE);
        Mockito.verify(presenter).fetchVisitStatus(Mockito.any());
        Mockito.verify(presenter).fetchUpcomingServiceAndFamilyDue(Mockito.any());
        Mockito.verify(presenter).updateChildCommonPerson(Mockito.any());
        Mockito.verify(presenter).processBackGroundEvent();
    }

    @Test
    public void testGetContext() {
        Assert.assertEquals(activity, activity.getContext());
    }

    @Test
    public void testSetProfileImageRendersImage() {
        ImageRenderHelper imageRenderHelper = Mockito.mock(ImageRenderHelper.class);
        ReflectionHelpers.setField(activity, "imageRenderHelper", imageRenderHelper);

        CircleImageView imageViewProfile = Mockito.mock(CircleImageView.class);
        ReflectionHelpers.setField(activity, "imageViewProfile", imageViewProfile);


        activity.setProfileImage("12345");
        Mockito.verify(imageRenderHelper).refreshProfileImage("12345", imageViewProfile, R.drawable.rowavatar_child);
    }

    @Test
    public void testSetParentNameUpdatesUI() {
        TextView textViewParentName = Mockito.mock(TextView.class);
        ReflectionHelpers.setField(activity, "textViewParentName", textViewParentName);

        activity.setParentName("rkodev");
        Mockito.verify(textViewParentName).setText("rkodev");
    }

    @Test
    public void testSetGender() {
        TextView textViewGender = Mockito.mock(TextView.class);
        ReflectionHelpers.setField(activity, "textViewGender", textViewGender);
        ReflectionHelpers.setField(activity, "imageViewProfile", Mockito.mock(CircleImageView.class));

        activity.setGender("MALE");
        Mockito.verify(textViewGender).setText("Male");


        activity.setGender("FEMALE");
        Mockito.verify(textViewGender).setText("Female");
    }

    @Test
    public void testSetAddress() {
        TextView textViewAddress = Mockito.mock(TextView.class);
        ReflectionHelpers.setField(activity, "textViewAddress", textViewAddress);

        activity.setAddress("lavington");
        Mockito.verify(textViewAddress).setText("lavington");
    }

    @Test
    public void testSetID() {
        TextView textViewId = Mockito.mock(TextView.class);
        ReflectionHelpers.setField(activity, "textViewId", textViewId);

        activity.setId("12345");
        Mockito.verify(textViewId).setText("12345");
    }

    @Test
    public void testSetProfileName() {
        TextView textViewChildName = Mockito.mock(TextView.class);
        ReflectionHelpers.setField(activity, "textViewChildName", textViewChildName);

        activity.setProfileName("Jayden");
        Mockito.verify(textViewChildName).setText("Jayden");
    }

    @Test
    public void testSetAge() {
        TextView textViewChildName = Mockito.mock(TextView.class);
        ReflectionHelpers.setField(activity, "textViewChildName", textViewChildName);

        activity.setAge("5w");
        Mockito.verify(textViewChildName).append(", 5w");
    }

    @Test
    public void testSetVisitButtonDueStatusMutatesViewsDesign() {
        activity = Mockito.spy(activity);
        activity.setTheme(org.smartregister.R.style.AppTheme); //we need this here
        activity.setContentView(R.layout.activity_child_profile);

        CoreChildProfileContract.Presenter presenter = Mockito.mock(CoreChildProfileContract.Presenter.class);
        Mockito.doReturn(presenter).when(activity).presenter();
        activity.setupViews();

        TextView textViewRecord = Mockito.mock(TextView.class);
        ReflectionHelpers.setField(activity, "textViewRecord", textViewRecord);

        activity.setVisitButtonDueStatus();
        Mockito.verify(textViewRecord).setBackgroundResource(R.drawable.record_btn_selector_due);
        Mockito.verify(textViewRecord).setTextColor(activity.getResources().getColor(R.color.white));
    }

    @Test
    public void testSetVisitButtonOverdueStatus() {
        activity = Mockito.spy(activity);
        activity.setTheme(org.smartregister.R.style.AppTheme); //we need this here
        activity.setContentView(R.layout.activity_child_profile);

        CoreChildProfileContract.Presenter presenter = Mockito.mock(CoreChildProfileContract.Presenter.class);
        Mockito.doReturn(presenter).when(activity).presenter();
        activity.setupViews();

        TextView textViewRecord = Mockito.mock(TextView.class);
        ReflectionHelpers.setField(activity, "textViewRecord", textViewRecord);

        activity.setVisitButtonOverdueStatus();
        Mockito.verify(textViewRecord).setBackgroundResource(R.drawable.record_btn_selector_overdue);
        Mockito.verify(textViewRecord).setTextColor(activity.getResources().getColor(R.color.white));
    }

    @Test
    public void testSetVisitNotDoneThisMonth() {
        activity = Mockito.spy(activity);
        activity.setTheme(org.smartregister.R.style.AppTheme); //we need this here
        activity.setContentView(R.layout.activity_child_profile);

        CoreChildProfileContract.Presenter presenter = Mockito.mock(CoreChildProfileContract.Presenter.class);
        Mockito.doReturn(presenter).when(activity).presenter();
        activity.setupViews();

        TextView textViewNotVisitMonth = Mockito.mock(TextView.class);
        ReflectionHelpers.setField(activity, "textViewNotVisitMonth", textViewNotVisitMonth);

        TextView textViewUndo = Mockito.mock(TextView.class);
        ReflectionHelpers.setField(activity, "textViewUndo", textViewUndo);

        ImageView imageViewCrossChild = Mockito.mock(ImageView.class);
        ReflectionHelpers.setField(activity, "imageViewCrossChild", imageViewCrossChild);

        activity.setVisitNotDoneThisMonth(false);
        Mockito.verify(textViewNotVisitMonth).setText(R.string.not_visiting_this_month);
        Mockito.verify(textViewUndo).setText(R.string.undo);
        Mockito.verify(textViewUndo).setVisibility(View.GONE);
        Mockito.verify(imageViewCrossChild).setImageResource(R.drawable.activityrow_notvisited);
    }

    @Test
    public void testSetLastVisitRowView() {
        RelativeLayout layoutLastVisitRow = Mockito.mock(RelativeLayout.class);
        ReflectionHelpers.setField(activity, "layoutLastVisitRow", layoutLastVisitRow);

        View viewLastVisitRow = Mockito.mock(View.class);
        ReflectionHelpers.setField(activity, "viewLastVisitRow", viewLastVisitRow);

        TextView textViewLastVisit = Mockito.mock(TextView.class);
        ReflectionHelpers.setField(activity, "textViewLastVisit", textViewLastVisit);

        activity.setLastVisitRowView("2");
        Mockito.verify(layoutLastVisitRow).setVisibility(View.VISIBLE);
        Mockito.verify(viewLastVisitRow).setVisibility(View.VISIBLE);
        Mockito.verify(textViewLastVisit).setText(Mockito.any());

        activity.setLastVisitRowView(null);
        Mockito.verify(layoutLastVisitRow).setVisibility(View.GONE);
        Mockito.verify(viewLastVisitRow).setVisibility(View.GONE);
    }

    @Test
    public void testSetServiceNameDue() {
        activity = Mockito.spy(activity);
        activity.setTheme(org.smartregister.R.style.AppTheme); //we need this here
        activity.setContentView(R.layout.activity_child_profile);

        CoreChildProfileContract.Presenter presenter = Mockito.mock(CoreChildProfileContract.Presenter.class);
        Mockito.doReturn(presenter).when(activity).presenter();
        activity.setupViews();

        activity.setServiceNameDue("Vaccine", "2018-01-01");

        Assert.assertEquals(((View) ReflectionHelpers.getField(activity, "viewMostDueRow")).getVisibility(), View.VISIBLE);
        Assert.assertEquals(((View) ReflectionHelpers.getField(activity, "layoutMostDueOverdue")).getVisibility(), View.VISIBLE);


        activity.setServiceNameDue(null, "2018-01-01");
        Assert.assertEquals(((View) ReflectionHelpers.getField(activity, "viewMostDueRow")).getVisibility(), View.GONE);
        Assert.assertEquals(((View) ReflectionHelpers.getField(activity, "layoutMostDueOverdue")).getVisibility(), View.GONE);
    }

    @Test
    public void testSetServiceNameOverDue() {
        activity = Mockito.spy(activity);
        activity.setTheme(org.smartregister.R.style.AppTheme); //we need this here
        activity.setContentView(R.layout.activity_child_profile);

        CoreChildProfileContract.Presenter presenter = Mockito.mock(CoreChildProfileContract.Presenter.class);
        Mockito.doReturn(presenter).when(activity).presenter();
        activity.setupViews();

        activity.setServiceNameOverDue("Vaccine", "2018-01-01");
        Assert.assertEquals(((View) ReflectionHelpers.getField(activity, "layoutMostDueOverdue")).getVisibility(), View.VISIBLE);
        Assert.assertEquals(((View) ReflectionHelpers.getField(activity, "viewMostDueRow")).getVisibility(), View.VISIBLE);
    }

    @Test
    public void testSetServiceNameUpcoming() {
        activity = Mockito.spy(activity);
        activity.setTheme(org.smartregister.R.style.AppTheme); //we need this here
        activity.setContentView(R.layout.activity_child_profile);

        CoreChildProfileContract.Presenter presenter = Mockito.mock(CoreChildProfileContract.Presenter.class);
        Mockito.doReturn(presenter).when(activity).presenter();
        activity.setupViews();

        activity.setServiceNameUpcoming("Vaccine", "2018-01-01");
        Assert.assertEquals(((View) ReflectionHelpers.getField(activity, "layoutMostDueOverdue")).getVisibility(), View.VISIBLE);
        Assert.assertEquals(((View) ReflectionHelpers.getField(activity, "viewMostDueRow")).getVisibility(), View.VISIBLE);
    }

    @Test
    public void testSetVisitLessTwentyFourView() {
        activity = Mockito.spy(activity);
        activity.setTheme(org.smartregister.R.style.AppTheme); //we need this here
        activity.setContentView(R.layout.activity_child_profile);

        CoreChildProfileContract.Presenter presenter = Mockito.mock(CoreChildProfileContract.Presenter.class);
        Mockito.doReturn(presenter).when(activity).presenter();
        activity.setupViews();

        activity.setVisitLessTwentyFourView("Jan");
        Assert.assertEquals(((View) ReflectionHelpers.getField(activity, "textViewUndo")).getVisibility(), View.GONE);

        TextView textViewUndo = ReflectionHelpers.getField(activity, "textViewUndo");
        Assert.assertTrue(textViewUndo.getText().toString().equalsIgnoreCase(activity.getString(R.string.edit)));

        TextView textViewNotVisitMonth = ReflectionHelpers.getField(activity, "textViewNotVisitMonth");
        Assert.assertTrue(textViewNotVisitMonth.getText().toString().equalsIgnoreCase(activity.getString(R.string.visit_month, "Jan")));
    }

    @Test
    public void testSetVisitAboveTwentyFourView() {
        activity = Mockito.spy(activity);
        activity.setTheme(org.smartregister.R.style.AppTheme); //we need this here
        activity.setContentView(R.layout.activity_child_profile);

        CoreChildProfileContract.Presenter presenter = Mockito.mock(CoreChildProfileContract.Presenter.class);
        Mockito.doReturn(presenter).when(activity).presenter();
        activity.setupViews();

        activity.setVisitAboveTwentyFourView();
        Assert.assertEquals(((View) ReflectionHelpers.getField(activity, "textViewVisitNot")).getVisibility(), View.GONE);
    }

    @Test
    public void testSetFamilyHasNothingDue() {
        activity = Mockito.spy(activity);
        activity.setTheme(org.smartregister.R.style.AppTheme); //we need this here
        activity.setContentView(R.layout.activity_child_profile);

        CoreChildProfileContract.Presenter presenter = Mockito.mock(CoreChildProfileContract.Presenter.class);
        Mockito.doReturn(presenter).when(activity).presenter();
        activity.setupViews();

        activity.setFamilyHasNothingDue();
        Assert.assertEquals(((View) ReflectionHelpers.getField(activity, "layoutFamilyHasRow")).getVisibility(), View.VISIBLE);
        Assert.assertEquals(((View) ReflectionHelpers.getField(activity, "viewFamilyRow")).getVisibility(), View.VISIBLE);
    }

    @Test
    public void testSetFamilyHasServiceDueDue() {
        activity = Mockito.spy(activity);
        activity.setTheme(org.smartregister.R.style.AppTheme); //we need this here
        activity.setContentView(R.layout.activity_child_profile);

        CoreChildProfileContract.Presenter presenter = Mockito.mock(CoreChildProfileContract.Presenter.class);
        Mockito.doReturn(presenter).when(activity).presenter();
        activity.setupViews();

        activity.setFamilyHasServiceDue();
        Assert.assertEquals(((View) ReflectionHelpers.getField(activity, "layoutFamilyHasRow")).getVisibility(), View.VISIBLE);
        Assert.assertEquals(((View) ReflectionHelpers.getField(activity, "viewFamilyRow")).getVisibility(), View.VISIBLE);
    }

    @Test
    public void testSetFamilyHasServiceOverdue() {
        activity = Mockito.spy(activity);
        activity.setTheme(org.smartregister.R.style.AppTheme); //we need this here
        activity.setContentView(R.layout.activity_child_profile);

        CoreChildProfileContract.Presenter presenter = Mockito.mock(CoreChildProfileContract.Presenter.class);
        Mockito.doReturn(presenter).when(activity).presenter();
        activity.setupViews();

        activity.setFamilyHasServiceDue();
        Assert.assertEquals(((View) ReflectionHelpers.getField(activity, "layoutFamilyHasRow")).getVisibility(), View.VISIBLE);
        Assert.assertEquals(((View) ReflectionHelpers.getField(activity, "viewFamilyRow")).getVisibility(), View.VISIBLE);
    }

    @Test
    public void testEnableEdit() {
        TextView tvEdit = Mockito.mock(TextView.class);
        ReflectionHelpers.setField(activity, "tvEdit", tvEdit);

        activity.enableEdit(true);
        Mockito.verify(tvEdit).setVisibility(View.VISIBLE);
        Mockito.verify(tvEdit).setOnClickListener(activity);


        activity.enableEdit(false);
        Mockito.verify(tvEdit).setVisibility(View.GONE);
        Mockito.verify(tvEdit).setOnClickListener(null);
    }

    @Test
    public void testHideProgressBar() {
        ProgressBar progressBar = Mockito.mock(ProgressBar.class);
        ReflectionHelpers.setField(activity, "progressBar", progressBar);

        activity.hideProgressBar();
        Mockito.verify(progressBar).setVisibility(View.GONE);
    }

    @Test
    public void testShowUndoVisitNotDoneView() {
        activity = Mockito.spy(activity);
        CoreChildProfileContract.Presenter presenter = Mockito.mock(CoreChildProfileContract.Presenter.class);
        Mockito.doReturn(presenter).when(activity).presenter();

        activity.showUndoVisitNotDoneView();
        Mockito.verify(presenter).fetchVisitStatus(Mockito.any());
    }

    @Test
    public void testUpdateAfterBackgroundProcessed() {
        activity = Mockito.spy(activity);
        CoreChildProfileContract.Presenter presenter = Mockito.mock(CoreChildProfileContract.Presenter.class);
        Mockito.doReturn(presenter).when(activity).presenter();

        activity.updateAfterBackgroundProcessed();
        Mockito.verify(presenter).updateChildCommonPerson(Mockito.any());
    }

    @Test
    public void testSetProgressBarState() {
        ProgressBar progressBar = Mockito.mock(ProgressBar.class);
        ReflectionHelpers.setField(activity, "progressBar", progressBar);

        activity.setProgressBarState(true);
        Mockito.verify(progressBar).setVisibility(View.VISIBLE);

        activity.setProgressBarState(false);
        Mockito.verify(progressBar).setVisibility(View.GONE);
    }

    @Test
    public void testOnJsonProcessed() {
        activity = Mockito.spy(activity);
        Mockito.doNothing().when(activity).onProfileTaskFetched(Mockito.any(), Mockito.any());
        activity.onJsonProcessed("eventType", "taskType", null);
        Mockito.verify(activity).onProfileTaskFetched("taskType", null);
    }

    @Test
    public void testonProfileTaskFetched() {
        activity = Mockito.spy(activity);
        activity.setTheme(org.smartregister.R.style.AppTheme); //we need this here
        activity.setContentView(R.layout.activity_child_profile);

        CoreChildProfileContract.Presenter presenter = Mockito.mock(CoreChildProfileContract.Presenter.class);
        Mockito.doReturn(presenter).when(activity).presenter();
        activity.setupViews();

        String taskType = "service";
        ProfileTask profileTask = Mockito.mock(ProfileTask.class);

        activity.onProfileTaskFetched(taskType, profileTask);
        Assert.assertEquals(((View) ReflectionHelpers.getField(activity, "layoutSickVisit")).getVisibility(), View.VISIBLE);
        Assert.assertEquals(((View) ReflectionHelpers.getField(activity, "viewDividerSickRow")).getVisibility(), View.VISIBLE);
    }

    @Test
    public void testOnOptionsItemSelected() {
        activity = Mockito.spy(activity);
        CoreChildProfilePresenter presenter = Mockito.mock(CoreChildProfilePresenter.class);
        Mockito.doReturn(presenter).when(activity).presenter();
        ReflectionHelpers.setField(activity, "memberObject", Mockito.mock(MemberObject.class));


        MenuItem item = Mockito.mock(MenuItem.class);

        Mockito.doReturn(android.R.id.home).when(item).getItemId();
        activity.onOptionsItemSelected(item);
        Mockito.verify(activity).onBackPressed();

        Mockito.doReturn(R.id.action_registration).when(item).getItemId();
        activity.onOptionsItemSelected(item);
        Mockito.verify(presenter).startFormForEdit(Mockito.any(), Mockito.any());

        Mockito.doReturn(R.id.action_sick_child_form).when(item).getItemId();
        activity.onOptionsItemSelected(item);
        Mockito.verify(presenter).startSickChildForm(Mockito.any());
    }

    @Test
    public void testOnCreateOptionsMenuHidesOptionalItems() {
        activity = Mockito.spy(activity);

        Menu menu = Mockito.mock(Menu.class);
        MenuItem menuItem = Mockito.mock(MenuItem.class);
        Mockito.doReturn(menuItem).when(menu).findItem(R.id.action_sick_child_form);
        Mockito.doReturn(menuItem).when(menu).findItem(R.id.action_anc_registration);
        Mockito.doReturn(menuItem).when(menu).findItem(R.id.action_sick_child_follow_up);
        Mockito.doReturn(menuItem).when(menu).findItem(R.id.action_malaria_diagnosis);

        MenuInflater menuInflater = Mockito.mock(MenuInflater.class);
        Mockito.doReturn(menuInflater).when(activity).getMenuInflater();

        activity.onCreateOptionsMenu(menu);
        Mockito.verify(menuInflater).inflate(R.menu.child_profile_menu, menu);
        Mockito.verify(menuItem, Mockito.times(4)).setVisible(false);
    }

    @Test
    public void testOnActivityResult() throws Exception {
        activity = Mockito.spy(activity);
        CoreChildProfileContract.Presenter presenter = Mockito.mock(CoreChildProfileContract.Presenter.class);
        Mockito.doReturn(presenter).when(activity).presenter();

        int resultCode = Activity.RESULT_OK;

        activity.onActivityResult(CoreConstants.ProfileActivityResults.CHANGE_COMPLETED, resultCode, null);
        Mockito.verify(activity, Mockito.times(2)).finish();

        Intent data = Mockito.mock(Intent.class);
        String json = "{\"encounter_type\": \"Update Child Registration\"}";
        Mockito.doReturn(json).when(data).getStringExtra(Constants.JSON_FORM_EXTRA.JSON);

        activity.onActivityResult(JsonFormUtils.REQUEST_CODE_GET_JSON, resultCode, data);
        Mockito.verify(presenter).updateChildProfile(json);


        json = "{\"encounter_type\": \"Sick Child Referral\"}";
        Mockito.doReturn(json).when(data).getStringExtra(Constants.JSON_FORM_EXTRA.JSON);

        activity.onActivityResult(JsonFormUtils.REQUEST_CODE_GET_JSON, resultCode, data);
        Mockito.verify(presenter).createSickChildEvent(Mockito.any(), Mockito.any());


        json = "{\"encounter_type\": \"Random\"}";
        Mockito.doReturn(json).when(data).getStringExtra(Constants.JSON_FORM_EXTRA.JSON);

        activity.onActivityResult(JsonFormUtils.REQUEST_CODE_GET_JSON, resultCode, data);
        Mockito.verify(presenter).processJson(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
    }

    @After
    public void tearDown() {
        try {
            activity.finish();
            controller.pause().stop().destroy(); //destroy controller if we can
        } catch (Exception e) {
            Timber.e(e);
        }
    }
}
