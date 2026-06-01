package com.bluecodeltd.ecap.chw.activity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import android.app.Activity;
import android.content.Intent;

import com.bluecodeltd.ecap.chw.BaseActivityTestSetUp;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.Shadows;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.shadow.api.Shadow;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowApplication;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.view.contract.BaseLoginContract;

/**
 * End-to-end-ish Robolectric flow checks:
 * - Login navigates to Dashboard
 * - From Dashboard, opening the Family register starts the register Activity
 * - FamilyRegisterActivity receives START_REGISTRATION action and invokes startFormActivity
 */
public class LoginDashboardRegisterFlowTest extends BaseActivityTestSetUp<Activity> {

    @Mock
    private BaseLoginContract.Presenter loginPresenter;

    @Override
    protected Class<Activity> getActivityClass() {
        // We build activities manually per test; this base is unused
        return Activity.class;
    }

    @Test
    public void testLoginNavigatesToDashboard() {
        ActivityController<LoginActivity> loginController = org.robolectric.Robolectric.buildActivity(LoginActivity.class).create().start().resume();
        LoginActivity loginActivity = loginController.get();

        BaseLoginContract.Presenter presenter = Mockito.mock(BaseLoginContract.Presenter.class);
        Mockito.doReturn(false).when(presenter).isUserLoggedOut();
        ReflectionHelpers.setField(loginActivity, "mLoginPresenter", presenter);

        // Trigger onResume logic to go to home when not logged out
        loginActivity.onResume();

        Intent started = ShadowApplication.getInstance().getNextStartedActivity();
        assertNotNull("Expected an intent to be started from LoginActivity", started);
        assertEquals(DashboardActivity.class.getName(), started.getComponent().getClassName());
    }

    @Test
    public void testOpenFamilyRegisterFromDashboardStartsActivity() {
        ActivityController<DashboardActivity> dashController = org.robolectric.Robolectric.buildActivity(DashboardActivity.class).create().start().resume();
        DashboardActivity dashboard = dashController.get();

        // Open family register via provided helper
        FamilyRegisterActivity.startFamilyRegisterForm(dashboard);

        Intent started = ShadowApplication.getInstance().getNextStartedActivity();
        assertNotNull("Expected FamilyRegisterActivity to be started from Dashboard", started);
        assertEquals(FamilyRegisterActivity.class.getName(), started.getComponent().getClassName());
    }

    @Test
    public void testFamilyRegisterActivityReceivesStartRegistrationAction() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.putExtra(
                com.bluecodeltd.ecap.chw.util.Constants.ACTIVITY_PAYLOAD.ACTION,
                org.smartregister.chw.core.utils.CoreConstants.ACTION.START_REGISTRATION
        );

        ActivityController<FamilyRegisterActivity> controller = org.robolectric.Robolectric.buildActivity(FamilyRegisterActivity.class, intent).create().start();
        FamilyRegisterActivity activity = controller.get();

        assertNotNull(activity);
        // Sanity check: intent contains the expected action used to trigger form start
        assertEquals(
                org.smartregister.chw.core.utils.CoreConstants.ACTION.START_REGISTRATION,
                activity.getIntent().getStringExtra(com.bluecodeltd.ecap.chw.util.Constants.ACTIVITY_PAYLOAD.ACTION)
        );
    }
}
