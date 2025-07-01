package org.smartregister.chw.core.activity;

import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.RecyclerView;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.robolectric.Robolectric;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.chw.core.BaseUnitTest;
import org.smartregister.chw.core.R;
import org.smartregister.chw.core.activity.impl.CoreMalariaProfileActivityImpl;
import org.smartregister.chw.core.activity.impl.FamilyOtherMemberActivityPresenterImpl;
import org.smartregister.chw.core.presenter.CoreFamilyOtherMemberActivityPresenter;
import org.smartregister.chw.core.presenter.CoreMalariaMemberProfilePresenter;
import org.smartregister.chw.core.shadows.UtilsShadowUtil;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.malaria.domain.MemberObject;
import org.smartregister.chw.malaria.presenter.BaseMalariaProfilePresenter;
import org.smartregister.family.domain.FamilyMetadata;
import org.smartregister.family.util.Constants;
import org.smartregister.family.util.JsonFormUtils;

import timber.log.Timber;


@Config(shadows = {UtilsShadowUtil.class})
public class CoreMalariaProfileActivityTest extends BaseUnitTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    private CoreMalariaProfileActivity activity;
    private ActivityController<CoreMalariaProfileActivityImpl> activityController;

    @Mock
    private BaseMalariaProfilePresenter profilePresenter;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        Context context = Context.getInstance();
        CoreLibrary.init(context);

        activityController = Robolectric.buildActivity(CoreMalariaProfileActivityImpl.class);
        activity = activityController.get();

        MemberObject memberObject = Mockito.mock(MemberObject.class);
        memberObject.setFamilyName("Sample");
        ProgressBar progressBar = Mockito.mock(ProgressBar.class);

        ReflectionHelpers.setField(activity, "memberObject", memberObject);
        ReflectionHelpers.setField(activity, "progressBar", progressBar);
        ReflectionHelpers.setField(activity, "profilePresenter", profilePresenter);

        activityController.create().start();
    }

    @Test
    public void presenterIsInitialized() {
        activity.initializePresenter();
        Assert.assertNotNull(ReflectionHelpers.getField(activity, "profilePresenter"));
    }

    @Test
    public void initNotificationReferralRecyclerViewInitsCorrectLayouts() {
        activity = Mockito.spy(activity);
        Mockito.when(activity.findViewById(R.id.notification_and_referral_row)).thenReturn(Mockito.mock(RelativeLayout.class));
        Mockito.when(activity.findViewById(R.id.notification_and_referral_recycler_view)).thenReturn(Mockito.mock(RecyclerView.class));
        activity.initializeNotificationReferralRecyclerView();
        Assert.assertNotNull(ReflectionHelpers.getField(activity, "notificationAndReferralLayout"));
        Assert.assertNotNull(ReflectionHelpers.getField(activity, "notificationAndReferralRecyclerView"));
    }

    @Test
    public void initialisingPresenterRefreshesProfileBottomSection() {
        activity = Mockito.spy(activity);
        profilePresenter = Mockito.spy(Mockito.mock(CoreMalariaMemberProfilePresenter.class));
        Mockito.when(activity.getProfilePresenter()).thenReturn((CoreMalariaMemberProfilePresenter) profilePresenter);
        activity.initializePresenter();
        Mockito.verify(profilePresenter, Mockito.times(1)).refreshProfileBottom();
    }

    @Test
    public void getJsonActivityResultUpdatesFamilyIfEncounterIsUpdate() {
        activity = Mockito.spy(activity);

        int resultCode = Activity.RESULT_OK;
        int requestCode = JsonFormUtils.REQUEST_CODE_GET_JSON;

        Intent data = Mockito.mock(Intent.class);
        String json = "{\"encounter_type\": \"Update Family Member\"}";
        Mockito.doReturn(json).when(data).getStringExtra(Constants.JSON_FORM_EXTRA.JSON);

        FamilyMetadata metadata = Mockito.mock(FamilyMetadata.class, Mockito.CALLS_REAL_METHODS);
        metadata.updateFamilyMemberRegister("family_member_register",
                "tableName",
                "Register Family Member",
                "Update Family Member",
                "config",
                "familyRelationKey");

        UtilsShadowUtil.setMetadata(metadata);

        CoreFamilyOtherMemberActivityPresenter memberActivityPresenter = Mockito.spy(Mockito.mock(FamilyOtherMemberActivityPresenterImpl.class));
        Mockito.when(activity.presenter()).thenReturn(memberActivityPresenter);

        activity.onActivityResult(requestCode, resultCode, data);
        Mockito.verify(memberActivityPresenter, Mockito.times(1)).
                updateFamilyMember(
                        ArgumentMatchers.eq(activity),
                        ArgumentMatchers.eq(json),
                        ArgumentMatchers.eq(false));
    }

    @Test
    public void menuIsInflatedOnCreateOptionsMenu() {
        activity = Mockito.spy(activity);
        MenuInflater inflater = Mockito.spy(Mockito.mock(MenuInflater.class));
        Mockito.when(activity.getMenuInflater()).thenReturn(inflater);
        Menu menu = Mockito.mock(Menu.class);
        activity.onCreateOptionsMenu(menu);
        Mockito.verify(inflater, Mockito.times(1)).inflate(R.menu.malaria_profile_menu, menu);
    }


    @Test
    public void optionIsHandledOnOptionsItemSelected() {
        activity = Mockito.spy(activity);
        MenuItem menuItem = Mockito.mock(MenuItem.class);

        Mockito.doNothing().when(activity).startFormForEdit(R.string.registration_info,
                CoreConstants.JSON_FORM.FAMILY_MEMBER_REGISTER);

        Mockito.doReturn(R.id.action_registration).when(menuItem).getItemId();
        activity.onOptionsItemSelected(menuItem);
        Mockito.verify(activity).startFormForEdit(R.string.registration_info,
                CoreConstants.JSON_FORM.FAMILY_MEMBER_REGISTER);

    }

    @After
    public void tearDown() {
        try {
            activity.finish();
            activityController.pause().stop().destroy();
        } catch (Exception e) {
            Timber.e(e);
        }
    }
}
