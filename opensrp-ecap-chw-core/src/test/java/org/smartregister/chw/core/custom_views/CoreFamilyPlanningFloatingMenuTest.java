package org.smartregister.chw.core.custom_views;

import android.os.Build;
import android.view.View;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.smartregister.Context;
import org.smartregister.chw.core.application.TestApplication;
import org.smartregister.chw.core.listener.OnClickFloatingMenu;

import java.util.ArrayList;

@RunWith(RobolectricTestRunner.class)
@Config(application = TestApplication.class, sdk = Build.VERSION_CODES.P)
public class CoreFamilyPlanningFloatingMenuTest {

    private CoreFamilyPlanningFloatingMenu coreFamilyPlanningFloatingMenu;

    @Mock
    private View view;

    @Mock
    private OnClickFloatingMenu onClickFloatingMenu;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        coreFamilyPlanningFloatingMenu = Mockito.mock(CoreFamilyPlanningFloatingMenu.class, Mockito.CALLS_REAL_METHODS);
        Context.bindtypes = new ArrayList<>();
    }

    @Test
    public void whenSetupViewsAnswered() {
        coreFamilyPlanningFloatingMenu.setFloatingMenuOnClickListener(onClickFloatingMenu);

        ArgumentCaptor<OnClickFloatingMenu> captor = ArgumentCaptor.forClass(OnClickFloatingMenu.class);
        Mockito.verify(coreFamilyPlanningFloatingMenu, Mockito.times(1)).setFloatingMenuOnClickListener(captor.capture());
        Assert.assertEquals(captor.getValue(), onClickFloatingMenu);
    }

    @Test
    public void whenOnClickAnswered() {
        Mockito.doNothing().when(coreFamilyPlanningFloatingMenu).onClick(view);
        coreFamilyPlanningFloatingMenu.onClick(view);

        ArgumentCaptor<View> captor = ArgumentCaptor.forClass(View.class);
        Mockito.verify(coreFamilyPlanningFloatingMenu, Mockito.times(1)).onClick(captor.capture());
        Assert.assertEquals(captor.getValue(), view);
    }

    @Test
    public void whenRedrawAnswered() {
        Mockito.doNothing().when(coreFamilyPlanningFloatingMenu).redraw(true);
        coreFamilyPlanningFloatingMenu.redraw(true);

        ArgumentCaptor<Boolean> captor = ArgumentCaptor.forClass(Boolean.class);
        Mockito.verify(coreFamilyPlanningFloatingMenu, Mockito.times(1)).redraw(captor.capture());
        Assert.assertEquals(captor.getValue(), true);
    }

    @Test
    public void testGetCallLayout() {
        Mockito.when(coreFamilyPlanningFloatingMenu.getCallLayout())
                .thenReturn(view);
        Assert.assertEquals(view, coreFamilyPlanningFloatingMenu.getCallLayout());
    }
}
