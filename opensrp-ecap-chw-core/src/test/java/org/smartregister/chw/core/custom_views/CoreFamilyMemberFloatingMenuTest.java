package org.smartregister.chw.core.custom_views;

import android.view.View;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.smartregister.Context;
import org.smartregister.chw.core.listener.OnClickFloatingMenu;
import java.util.ArrayList;

import static org.mockito.Mockito.when;

public class CoreFamilyMemberFloatingMenuTest {


    private CoreFamilyMemberFloatingMenu coreFamilyMemberFloatingMenuTest;

    @Mock
    private View view;

    @Mock
    private OnClickFloatingMenu onClickFloatingMenu;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        coreFamilyMemberFloatingMenuTest = Mockito.mock(CoreFamilyMemberFloatingMenu.class, Mockito.CALLS_REAL_METHODS);
        Context.bindtypes = new ArrayList<>();
    }

    @Test
    public void testSetupViewsAnswered() {
        coreFamilyMemberFloatingMenuTest.setClickListener(onClickFloatingMenu);

        ArgumentCaptor<OnClickFloatingMenu> captor = ArgumentCaptor.forClass(OnClickFloatingMenu.class);
        Mockito.verify(coreFamilyMemberFloatingMenuTest, Mockito.times(1)).setClickListener(captor.capture());
        Assert.assertEquals(captor.getValue(), onClickFloatingMenu);
    }

    @Test
    public void testOnClickAnswered() {
        Mockito.doNothing().when(coreFamilyMemberFloatingMenuTest).onClick(view);
        coreFamilyMemberFloatingMenuTest.onClick(view);

        ArgumentCaptor<View> captor = ArgumentCaptor.forClass(View.class);
        Mockito.verify(coreFamilyMemberFloatingMenuTest, Mockito.times(1)).onClick(captor.capture());
        Assert.assertEquals(captor.getValue(), view);
    }

    @Test
    public void testRedrawAnswered() {
        Mockito.doNothing().when(coreFamilyMemberFloatingMenuTest).reDraw(true);
        coreFamilyMemberFloatingMenuTest.reDraw(true);

        ArgumentCaptor<Boolean> captor = ArgumentCaptor.forClass(Boolean.class);
        Mockito.verify(coreFamilyMemberFloatingMenuTest, Mockito.times(1)).reDraw(captor.capture());
        Assert.assertEquals(captor.getValue(), true);
    }

    @Test
    public void testGetCallLayout() {
        when(coreFamilyMemberFloatingMenuTest.getCallLayout())
                .thenReturn(view);
        Assert.assertEquals(view, coreFamilyMemberFloatingMenuTest.getCallLayout());
    }
}
