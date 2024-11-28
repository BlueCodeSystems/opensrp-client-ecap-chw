package org.smartregister.chw.core.activity;

import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.core.BaseUnitTest;
import org.smartregister.chw.core.R;
import org.smartregister.chw.core.activity.impl.CoreAncMemberProfileActivityImpl;
import org.smartregister.chw.core.presenter.CoreAncMemberProfilePresenter;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.domain.AlertStatus;

import java.util.Date;

import timber.log.Timber;

public class CoreAncMemberProfileActivityTest extends BaseUnitTest {

    private CoreAncMemberProfileActivity activity;

    private ActivityController<CoreAncMemberProfileActivityImpl> controller;

    @Mock
    private MemberObject memberObject;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        Context context = Context.getInstance();
        CoreLibrary.init(context);

        //Auto login by default
        context.session().start(context.session().lengthInMilliseconds());

        controller = Robolectric.buildActivity(CoreAncMemberProfileActivityImpl.class).create().start();
        activity = controller.get();
        memberObject = Mockito.mock(MemberObject.class);
        memberObject.setBaseEntityId("some-base-entity-id");
        memberObject.setFamilyName("Some Family Name");

        ReflectionHelpers.setField(activity, "memberObject", memberObject);
        View viewFamilyRow = new View(RuntimeEnvironment.systemContext);
        ReflectionHelpers.setField(activity, "view_family_row", viewFamilyRow);
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

    @Test
    public void registerPresenter() {
        activity.registerPresenter();
        Assert.assertNotNull(activity.ancMemberProfilePresenter());
    }

    @Test
    public void testOnOptionsItemSelected() {
        activity = Mockito.spy(activity);

        MenuItem menuItem = Mockito.mock(MenuItem.class);
        CoreConstants.JSON_FORM.setLocaleAndAssetManager(activity.getApplicationContext().getResources().getConfiguration().locale, activity.getApplicationContext().getAssets());

        // back pressed
        Mockito.doReturn(android.R.id.home).when(menuItem).getItemId();
        Mockito.doNothing().when(activity).onBackPressed();

        activity.onOptionsItemSelected(menuItem);
        Mockito.verify(activity).onBackPressed();

        // start form
        Mockito.doReturn(R.id.action_anc_member_registration).when(menuItem).getItemId();
        Mockito.doNothing().when(activity).startFormForEdit(Mockito.any(), Mockito.any());

        activity.onOptionsItemSelected(menuItem);
        Mockito.verify(activity).startFormForEdit(Mockito.eq(R.string.edit_member_form_title), Mockito.any());


        // start form
        Mockito.doReturn(R.id.action_anc_registration).when(menuItem).getItemId();
        Mockito.doNothing().when(activity).startFormForEdit(Mockito.any(), Mockito.any());

        activity.onOptionsItemSelected(menuItem);
        Mockito.verify(activity).startFormForEdit(Mockito.eq(R.string.edit_anc_registration_form_title), Mockito.any());

        // start form
        Mockito.doReturn(R.id.anc_danger_signs_outcome).when(menuItem).getItemId();
        CoreAncMemberProfilePresenter presenter = Mockito.mock(CoreAncMemberProfilePresenter.class);
        ReflectionHelpers.setField(activity, "presenter", presenter);

        activity.onOptionsItemSelected(menuItem);
        Mockito.verify(presenter).startAncDangerSignsOutcomeForm(memberObject);
    }

    @Test
    public void testSetFamilyStatus() {
        ReflectionHelpers.setField(activity, "rlFamilyServicesDue", Mockito.mock(RelativeLayout.class));
        ReflectionHelpers.setField(activity, "tvFamilyStatus", Mockito.mock(TextView.class));

        activity.setFamilyStatus(AlertStatus.complete);
        Assert.assertFalse(ReflectionHelpers.getField(activity, "hasDueServices"));

        activity.setFamilyStatus(AlertStatus.normal);
        Assert.assertTrue(ReflectionHelpers.getField(activity, "hasDueServices"));
    }

    @Test
    public void openFamilyLocationStartsAncMemberMapActivity() {
        activity = Mockito.spy(activity);
        activity.openFamilyLocation();
        Mockito.verify(activity).startActivity(Mockito.any());
    }

    @Test
    public void testOnVisitStatusWithin24Hrs() {
        activity = Mockito.spy(activity);

        LinearLayout layoutRecordView = Mockito.mock(LinearLayout.class);
        ReflectionHelpers.setField(activity, "layoutRecordView", layoutRecordView);
        ReflectionHelpers.setField(activity, "tvEdit", Mockito.mock(TextView.class));
        ReflectionHelpers.setField(activity, "textViewNotVisitMonth", Mockito.mock(TextView.class));
        ReflectionHelpers.setField(activity, "imageViewCross", Mockito.mock(ImageView.class));
        ReflectionHelpers.setField(activity, "textViewUndo", Mockito.mock(TextView.class));
        ReflectionHelpers.setField(activity, "textViewAncVisitNot", Mockito.mock(TextView.class));

        activity.onVisitStatusReloaded(CoreConstants.VISIT_STATE.WITHIN_24_HR, new Date());
        Mockito.verify(layoutRecordView).setVisibility(View.GONE);
    }

    @Test
    public void testOnVisitStatusWithinMonth() {
        activity = Mockito.spy(activity);

        LinearLayout bar = Mockito.mock(LinearLayout.class);
        ReflectionHelpers.setField(activity, "record_reccuringvisit_done_bar", bar);
        ReflectionHelpers.setField(activity, "layoutNotRecordView", Mockito.mock(RelativeLayout.class));
        ReflectionHelpers.setField(activity, "textViewUndo", Mockito.mock(TextView.class));
        ReflectionHelpers.setField(activity, "textViewAncVisitNot", Mockito.mock(TextView.class));

        activity.onVisitStatusReloaded(CoreConstants.VISIT_STATE.WITHIN_MONTH, null);
        Mockito.verify(bar).setVisibility(View.VISIBLE);
    }

    @Test
    public void testOnVisitStatusOverDue() {
        activity = Mockito.spy(activity);

        TextView textViewUndo = Mockito.mock(TextView.class);

        ReflectionHelpers.setField(activity, "layoutRecordView", Mockito.mock(LinearLayout.class));
        ReflectionHelpers.setField(activity, "record_reccuringvisit_done_bar", Mockito.mock(LinearLayout.class));
        ReflectionHelpers.setField(activity, "textViewAncVisitNot", Mockito.mock(TextView.class));
        ReflectionHelpers.setField(activity, "layoutNotRecordView", Mockito.mock(RelativeLayout.class));

        ReflectionHelpers.setField(activity, "textViewUndo", textViewUndo);
        ReflectionHelpers.setField(activity, "textview_record_anc_visit", Mockito.mock(TextView.class));

        activity.onVisitStatusReloaded(CoreConstants.VISIT_STATE.OVERDUE, null);
        Mockito.verify(textViewUndo).setVisibility(View.GONE);
    }

    @Test
    public void testOnVisitStatusDue() {
        activity = Mockito.spy(activity);

        TextView textViewUndo = Mockito.mock(TextView.class);

        ReflectionHelpers.setField(activity, "layoutRecordView", Mockito.mock(LinearLayout.class));
        ReflectionHelpers.setField(activity, "record_reccuringvisit_done_bar", Mockito.mock(LinearLayout.class));
        ReflectionHelpers.setField(activity, "textViewAncVisitNot", Mockito.mock(TextView.class));
        ReflectionHelpers.setField(activity, "layoutNotRecordView", Mockito.mock(RelativeLayout.class));

        ReflectionHelpers.setField(activity, "textViewUndo", textViewUndo);
        ReflectionHelpers.setField(activity, "textview_record_anc_visit", Mockito.mock(TextView.class));

        activity.onVisitStatusReloaded(CoreConstants.VISIT_STATE.DUE, null);
        Mockito.verify(textViewUndo).setVisibility(View.GONE);
    }

    @Test
    public void testOnVisitStatusVisitThisMonth() {
        activity = Mockito.spy(activity);

        TextView textViewUndo = Mockito.mock(TextView.class);

        ReflectionHelpers.setField(activity, "layoutRecordView", Mockito.mock(LinearLayout.class));
        ReflectionHelpers.setField(activity, "record_reccuringvisit_done_bar", Mockito.mock(LinearLayout.class));
        ReflectionHelpers.setField(activity, "textViewAncVisitNot", Mockito.mock(TextView.class));
        ReflectionHelpers.setField(activity, "layoutNotRecordView", Mockito.mock(RelativeLayout.class));

        ReflectionHelpers.setField(activity, "textViewUndo", textViewUndo);
        ReflectionHelpers.setField(activity, "textview_record_anc_visit", Mockito.mock(TextView.class));

        activity.onVisitStatusReloaded(CoreConstants.VISIT_STATE.NOT_VISIT_THIS_MONTH, null);
        Mockito.verify(textViewUndo).setVisibility(View.VISIBLE);
    }
}