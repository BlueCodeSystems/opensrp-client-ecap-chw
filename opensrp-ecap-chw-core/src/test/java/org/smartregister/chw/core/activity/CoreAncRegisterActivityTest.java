package org.smartregister.chw.core.activity;

import android.content.Intent;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.android.controller.ActivityController;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.chw.core.BaseUnitTest;
import org.smartregister.chw.core.utils.CoreConstants;

import timber.log.Timber;

public class CoreAncRegisterActivityTest extends BaseUnitTest {

    private CoreAncRegisterActivity activity;

    private ActivityController<CoreAncRegisterActivity> controller;

    @Before
    public void setUp() {

        MockitoAnnotations.initMocks(this);

        MockitoAnnotations.initMocks(this);

        Context context = Context.getInstance();
        CoreLibrary.init(context);

        //Auto login by default
        context.session().start(context.session().lengthInMilliseconds());

        Intent intent = new Intent();
        intent.putExtra(CoreConstants.ACTIVITY_PAYLOAD.PHONE_NUMBER, "phone_number");
        intent.putExtra(CoreConstants.ACTIVITY_PAYLOAD.FORM_NAME, "form_name");
        intent.putExtra(CoreConstants.ACTIVITY_PAYLOAD.UNIQUE_ID, "unique_id");
        intent.putExtra(CoreConstants.ACTIVITY_PAYLOAD.FAMILY_BASE_ENTITY_ID, "familyBaseEntityId");
        intent.putExtra(CoreConstants.ACTIVITY_PAYLOAD.FAMILY_NAME, "familyName");
        intent.putExtra(CoreConstants.ACTIVITY_PAYLOAD.LAST_LMP, "lastMenstrualPeriod");

        controller = Robolectric.buildActivity(CoreAncRegisterActivity.class, intent).create().start().resume();
        activity = controller.get();
        activity.onCreation();
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
    public void testGetFamilyBaseEntityId() {
        Assert.assertEquals(activity.getFamilyBaseEntityId(), "familyBaseEntityId");
        Assert.assertEquals(activity.getFormName(), "form_name");
        Assert.assertEquals(activity.getUniqueId(), "unique_id");
        Assert.assertEquals(activity.getFamilyName(), "familyName");
        Assert.assertEquals(activity.getPhoneNumber(), "phone_number");
    }
}
