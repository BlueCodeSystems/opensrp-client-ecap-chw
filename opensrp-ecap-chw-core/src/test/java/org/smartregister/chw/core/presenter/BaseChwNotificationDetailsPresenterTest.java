package org.smartregister.chw.core.presenter;

import android.util.Pair;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.chw.core.BaseUnitTest;
import org.smartregister.chw.core.activity.BaseChwNotificationDetailsActivity;
import org.smartregister.chw.core.contract.ChwNotificationDetailsContract;
import org.smartregister.chw.core.domain.NotificationItem;
import org.smartregister.commonregistry.CommonPersonObjectClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

public class BaseChwNotificationDetailsPresenterTest extends BaseUnitTest {

    @Mock
    private ChwNotificationDetailsContract.Interactor interactor;

    private BaseChwNotificationDetailsPresenter notificationDetailsPresenter;
    private TextView notificationTitle = new TextView(RuntimeEnvironment.systemContext);
    private LinearLayout notificationDetails = new LinearLayout(RuntimeEnvironment.systemContext);
    private String baseEntityId = "some-base-entity-id";
    private BaseChwNotificationDetailsActivity activityView;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        activityView = Robolectric.buildActivity(TestableChwNotificationDetailsActivity.class).get();
        ReflectionHelpers.setField(activityView, "notificationTitle", notificationTitle);
        ReflectionHelpers.setField(activityView, "notificationDetails", notificationDetails);
        Map<String, String> detailsMap = new HashMap<>();
        CommonPersonObjectClient client = new CommonPersonObjectClient(baseEntityId, detailsMap, "Patient 0");
        activityView.setCommonPersonsObjectClient(client);
        notificationDetailsPresenter = new BaseChwNotificationDetailsPresenter(activityView);
        notificationDetailsPresenter.setInteractor(interactor);
        notificationDetailsPresenter.setNotificationDates(Pair.create("2020-04-28", "2020-05-01"));
    }

    @Test
    public void testGetReferralDetails() {
        String notificationId = "notificationId-123-456";
        String notificationType = "PNC Danger Signs";
        notificationDetailsPresenter.getNotificationDetails(notificationId, notificationType);
        verify(interactor, atLeastOnce()).fetchNotificationDetails(notificationId, notificationType);
    }

    @Test
    public void testOnReferralDetailsFetched() {
        List<String> details = new ArrayList<>();
        details.add("Facility Visit Today");
        NotificationItem notificationItem = new NotificationItem("some-title", details);
        notificationDetailsPresenter.onNotificationDetailsFetched(notificationItem);
        Assert.assertNotNull(notificationTitle.getText());
        Assert.assertEquals(1, notificationDetails.getChildCount());
    }

    @Test
    public void testGetView() {
        Assert.assertNotNull(notificationDetailsPresenter.getView());
    }

    @Test
    public void testGetBaseEntityId() {
        Assert.assertEquals(baseEntityId, notificationDetailsPresenter.getClientBaseEntityId());
    }

    @Test
    public void testGetNotificationDates() {
        Assert.assertNotNull(notificationDetailsPresenter.getNotificationDates());
    }

    @After
    public void tearDown() {
        try {
            activityView.finish();
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