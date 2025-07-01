package org.smartregister.chw.core.activity;

import android.content.Intent;
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
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.core.BaseUnitTest;
import org.smartregister.chw.core.R;
import org.smartregister.chw.core.implementation.CorePncMemberProfileActivityImpl;
import org.smartregister.chw.core.interactor.CorePncMemberProfileInteractor;
import org.smartregister.chw.core.presenter.CorePncMemberProfilePresenter;
import org.smartregister.family.util.JsonFormUtils;

public class CorePncMemberProfileActivityTest extends BaseUnitTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    private CorePncMemberProfileActivityImpl activity;
    private ActivityController<CorePncMemberProfileActivityImpl> controller;

    @Mock
    private CorePncMemberProfilePresenter presenter;

    private MemberObject memberObject = new MemberObject();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        Context context = Context.getInstance();
        CoreLibrary.init(context);

        //Auto login by default
        //String password = "pwd";
        context.session().start(context.session().lengthInMilliseconds());
        //context.configuration().getDrishtiApplication().setPassword(password);
        //context.session().setPassword(password);

        Intent intent = new Intent();
        intent.putExtra("MemberObject", memberObject);
        controller = Robolectric.buildActivity(CorePncMemberProfileActivityImpl.class, intent).create().start();
        activity = controller.get();
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
    public void testOnOptionsItemSelected() {
        activity = Mockito.spy(activity);
        Mockito.doNothing().when(activity).startActivityForResult(Mockito.any(), Mockito.anyInt());
        Mockito.doReturn(presenter).when(activity).getPncMemberProfilePresenter();

        MenuItem item = Mockito.mock(MenuItem.class);

        Mockito.doReturn(android.R.id.home).when(item).getItemId();
        activity.onOptionsItemSelected(item);
        Mockito.verify(activity).onBackPressed();

        Mockito.doReturn(R.id.action_pnc_member_registration).when(item).getItemId();
        activity.onOptionsItemSelected(item);
        Mockito.verify(activity).startActivityForResult(Mockito.any(), Mockito.eq(JsonFormUtils.REQUEST_CODE_GET_JSON));

        Mockito.doReturn(R.id.action_malaria_registration).when(item).getItemId();
        activity.onOptionsItemSelected(item);
        Mockito.verify(activity).startMalariaRegister();

        Mockito.doReturn(R.id.action_malaria_followup_visit).when(item).getItemId();
        activity.onOptionsItemSelected(item);
        Mockito.verify(activity).startMalariaFollowUpVisit();

        Mockito.doReturn(R.id.action_fp_initiation_pnc).when(item).getItemId();
        activity.onOptionsItemSelected(item);
        Mockito.verify(activity).startFpRegister();

        Mockito.doReturn(R.id.action_fp_change).when(item).getItemId();
        activity.onOptionsItemSelected(item);
        Mockito.verify(activity).startFpChangeMethod();

        Mockito.doReturn(R.id.action__pnc_remove_member).when(item).getItemId();
        activity.onOptionsItemSelected(item);
        Mockito.verify(activity).removePncMember();

        Mockito.doReturn(R.id.action_pnc_remove_baby).when(item).getItemId();
        activity.onOptionsItemSelected(item);
        Mockito.verify(activity).getRemoveBabyMenuItem(item);

        Mockito.doReturn(R.id.action__pnc_danger_sign_outcome).when(item).getItemId();
        activity.onOptionsItemSelected(item);
        Mockito.verify(presenter).startPncDangerSignsOutcomeForm();

        Mockito.doReturn(R.id.action_malaria_diagnosis).when(item).getItemId();
        activity.onOptionsItemSelected(item);
        Mockito.verify(activity).startHfMalariaFollowupForm();
    }

    @Test
    public void presenterIsInitialisedCorrectly() {
        activity.registerPresenter();
        Assert.assertNotNull(activity.getPncMemberProfilePresenter());
    }

    @Test
    public void getChildrenInvokesInteractorGetChildrenUnder29() {
        CorePncMemberProfileInteractor interactor = Mockito.mock(CorePncMemberProfileInteractor.class);
        ReflectionHelpers.setField(activity, "pncMemberProfileInteractor", interactor);
        MemberObject memberObject = Mockito.mock(MemberObject.class);
        activity.getChildren(memberObject);
        Mockito.verify(interactor).pncChildrenUnder29Days(memberObject.getBaseEntityId());
    }

}
