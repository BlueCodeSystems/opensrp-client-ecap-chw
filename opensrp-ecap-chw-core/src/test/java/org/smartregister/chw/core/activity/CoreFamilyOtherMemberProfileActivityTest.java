package org.smartregister.chw.core.activity;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.robolectric.Robolectric;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.chw.core.BaseUnitTest;
import org.smartregister.chw.core.R;
import org.smartregister.chw.core.activity.impl.CoreFamilyOtherMemberProfileActivityImpl;
import org.smartregister.chw.core.presenter.CoreFamilyOtherMemberActivityPresenter;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.commonregistry.CommonPersonObjectClient;

public class CoreFamilyOtherMemberProfileActivityTest extends BaseUnitTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    private CoreFamilyOtherMemberProfileActivityImpl activity;
    private ActivityController<CoreFamilyOtherMemberProfileActivityImpl> controller;

    @Mock
    private CommonPersonObjectClient commonPersonObjectClient;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        Context context = Context.getInstance();
        CoreLibrary.init(context);

        //Auto login by default
        context.session().start(context.session().lengthInMilliseconds());

        Intent intent = new Intent();
        intent.putExtra(CoreConstants.INTENT_KEY.CHILD_COMMON_PERSON, commonPersonObjectClient);

        controller = Robolectric.buildActivity(CoreFamilyOtherMemberProfileActivityImpl.class, intent).create().start();
        activity = controller.get();
        activity.setTheme(org.smartregister.R.style.AppTheme); //we need this here
    }

    @After
    public void tearDown() {
        try {
            activity.finish();
            controller.pause().stop().destroy(); //destroy controller if we can
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSetupViewsInitializesViews() {
        activity.setTheme(org.smartregister.R.style.AppTheme); //we need this here
        activity.setContentView(R.layout.activity_family_other_member_profile_chw);

        ReflectionHelpers.setField(activity,"presenter", Mockito.mock(CoreFamilyOtherMemberActivityPresenter.class));
        activity.setupViews();

        Assert.assertNotNull(ReflectionHelpers.getField(activity, "familyFloatingMenu"));
        Assert.assertNotNull(ReflectionHelpers.getField(activity, "textViewFamilyHas"));
        Assert.assertNotNull(ReflectionHelpers.getField(activity, "layoutFamilyHasRow"));
    }

    @Test
    public void testOnCreateOptionsMenuInflatesProfileMenu() {
        activity = Mockito.spy(activity);

        MenuInflater menuInflater = Mockito.mock(MenuInflater.class);
        Mockito.doReturn(menuInflater).when(activity).getMenuInflater();

        Menu menu = Mockito.mock(Menu.class);
        activity.onCreateOptionsMenu(menu);

        Mockito.verify(menuInflater).inflate(R.menu.other_member_menu, menu);
    }

    @Test
    public void settingGenderSetsTranslatedString() {
        String gender = "Female";
        activity.setTheme(org.smartregister.R.style.AppTheme); //we need this here
        activity.setContentView(R.layout.activity_family_other_member_profile_chw);

        ReflectionHelpers.setField(activity,"presenter", Mockito.mock(CoreFamilyOtherMemberActivityPresenter.class));
        activity.setupViews();

        activity.setProfileDetailOne(gender);
        Assert.assertNotNull(ReflectionHelpers.getField(activity, "detailOneView"));
    }

    @Test
    public void testOnOptionsItemSelected() {
        activity = Mockito.spy(activity);
        Mockito.doNothing().when(activity).startActivityForResult(Mockito.any(), Mockito.anyInt());
        Mockito.doNothing().when(activity).startFormForEdit(Mockito.anyInt());

        MenuItem item = Mockito.mock(MenuItem.class);

        Mockito.doReturn(android.R.id.home).when(item).getItemId();
        activity.onOptionsItemSelected(item);
        Mockito.verify(activity).onBackPressed();

        Mockito.doReturn(R.id.action_anc_registration).when(item).getItemId();
        activity.onOptionsItemSelected(item);
        Mockito.verify(activity).startAncRegister();

        Mockito.doReturn(R.id.action_fp_initiation).when(item).getItemId();
        activity.onOptionsItemSelected(item);
        Mockito.verify(activity).startFpRegister();

        Mockito.doReturn(R.id.action_fp_change).when(item).getItemId();
        activity.onOptionsItemSelected(item);
        Mockito.verify(activity).startFpChangeMethod();

        Mockito.doReturn(R.id.action_malaria_registration).when(item).getItemId();
        activity.onOptionsItemSelected(item);
        Mockito.verify(activity).startMalariaRegister();

        Mockito.doReturn(R.id.action_malaria_followup_visit).when(item).getItemId();
        activity.onOptionsItemSelected(item);
        Mockito.verify(activity).startMalariaFollowUpVisit();

        Mockito.doReturn(R.id.action_registration).when(item).getItemId();
        ReflectionHelpers.setField(activity, "isIndependent", true);
        activity.onOptionsItemSelected(item);
        Mockito.verify(activity).startFormForEdit(R.string.edit_all_client_member_form_title);

        Mockito.doReturn(R.id.action_registration).when(item).getItemId();
        ReflectionHelpers.setField(activity, "isIndependent", false);
        activity.onOptionsItemSelected(item);
        Mockito.verify(activity).startFormForEdit(R.string.edit_member_form_title);

        Mockito.doReturn(R.id.action_remove_member).when(item).getItemId();
        activity.onOptionsItemSelected(item);
        Mockito.verify(activity).removeIndividualProfile();

        Mockito.doReturn(R.id.action_malaria_diagnosis).when(item).getItemId();
        activity.onOptionsItemSelected(item);
        Mockito.verify(activity).startHfMalariaFollowupForm();
    }
}
