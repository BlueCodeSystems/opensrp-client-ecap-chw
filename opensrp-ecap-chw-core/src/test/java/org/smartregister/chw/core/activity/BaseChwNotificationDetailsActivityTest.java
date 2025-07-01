package org.smartregister.chw.core.activity;

import android.widget.LinearLayout;
import android.widget.TextView;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.chw.core.BaseUnitTest;
import org.smartregister.chw.core.contract.ChwNotificationDetailsContract;
import org.smartregister.chw.core.domain.NotificationItem;

import java.util.ArrayList;
import java.util.List;

public class BaseChwNotificationDetailsActivityTest extends BaseUnitTest {

    private ChwNotificationDetailsContract.View view;
    private TextView notificationTitle = new TextView(RuntimeEnvironment.systemContext);
    private TextView markAsDoneTextView = new TextView(RuntimeEnvironment.systemContext);
    private LinearLayout notificationDetails = new LinearLayout(RuntimeEnvironment.systemContext);
    private TestableChwNotificationDetailsActivity activity;

    @Before
    public void setUp() {
        activity = Robolectric.buildActivity(TestableChwNotificationDetailsActivity.class).get();
        view = activity;
        ReflectionHelpers.setField(view, "notificationTitle", notificationTitle);
        ReflectionHelpers.setField(view, "markAsDoneTextView", markAsDoneTextView);
        ReflectionHelpers.setField(view, "notificationDetails", notificationDetails);
    }

    @Test
    public void testInitPresenter() {
        view.initPresenter();
        Assert.assertNotNull(((BaseChwNotificationDetailsActivity) view).getPresenter());
    }

    @Test
    public void testOnReferralDetailsFetched() {
        List<String> details = new ArrayList<>();
        details.add("Village: Mumbai");
        details.add("Referral Successful");
        NotificationItem notificationItem = new NotificationItem("Mathew Lucas visited the facility on 03 Mar 2020.", details);
        view.setNotificationDetails(notificationItem);
        Assert.assertNotNull(notificationTitle.getText());
        Assert.assertEquals(2, notificationDetails.getChildCount());
    }

    @Test
    public void testDisableMarkAsDoneAction() {
        view.disableMarkAsDoneAction(true);
        Assert.assertFalse(markAsDoneTextView.isEnabled());
    }

    @After
    public void tearDown() {
        try {
            activity.finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class TestableChwNotificationDetailsActivity extends BaseChwNotificationDetailsActivity {

        @Override
        public void goToMemberProfile() {
            // Implementation not required at the moment
        }
    }
}