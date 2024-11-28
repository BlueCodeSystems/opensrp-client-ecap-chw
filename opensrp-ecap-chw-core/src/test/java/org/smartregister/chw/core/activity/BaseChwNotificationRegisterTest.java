package org.smartregister.chw.core.activity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.powermock.reflect.Whitebox;
import org.robolectric.Robolectric;
import org.robolectric.android.controller.ActivityController;
import org.smartregister.chw.core.BaseUnitTest;
import org.smartregister.chw.core.R;
import org.smartregister.chw.core.activity.mock.BaseChwNotificationRegisterMock;
import org.smartregister.view.contract.BaseRegisterContract;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class BaseChwNotificationRegisterTest extends BaseUnitTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();
    protected BaseRegisterContract.Presenter presenter;
    private BaseChwNotificationRegister activity;
    private ActivityController<BaseChwNotificationRegisterMock> controller;

    @Before
    public void setUp() {
        controller = Robolectric.buildActivity(BaseChwNotificationRegisterMock.class).create().start().resume();
        activity = controller.get();
    }

    @Test
    public void testOnCreate() {
        assertNotNull(Whitebox.getInternalState(activity, "mPagerAdapter"));
    }

    @Test
    public void testRegisterBottomNavigation() {
        BottomNavigationView bottomNavigationView = activity.findViewById(R.id.bottom_navigation);
        assertNotNull(bottomNavigationView);
        assertEquals(4, bottomNavigationView.getMenu().size());
    }

    @After
    public void tearDown() {
        try {
            controller.pause().stop().destroy();
            activity.finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
