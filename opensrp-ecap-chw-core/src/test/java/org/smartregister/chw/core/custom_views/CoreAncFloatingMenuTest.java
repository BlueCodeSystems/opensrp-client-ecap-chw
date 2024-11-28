package org.smartregister.chw.core.custom_views;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.chw.core.BaseUnitTest;
import org.smartregister.chw.core.listener.OnClickFloatingMenu;

import static org.junit.Assert.assertEquals;

public class CoreAncFloatingMenuTest extends BaseUnitTest {

    @Mock
    private CoreAncFloatingMenu coreAncFloatingMenu;

    @Mock
    private View view;

    @Mock
    private OnClickFloatingMenu onClickFloatingMenu;

    @Mock
    private RelativeLayout relativeLayout;

    @Mock
    private LinearLayout linearLayout;

    @Mock
    private FloatingActionButton floatingActionButton;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        coreAncFloatingMenu = Mockito.mock(CoreAncFloatingMenu.class, Mockito.CALLS_REAL_METHODS);

        ReflectionHelpers.setField(coreAncFloatingMenu, "onClickFloatingMenu", onClickFloatingMenu);
        ReflectionHelpers.setField(coreAncFloatingMenu, "callLayout", view);
        ReflectionHelpers.setField(coreAncFloatingMenu, "activityMain", relativeLayout);
        ReflectionHelpers.setField(coreAncFloatingMenu, "fab", floatingActionButton);
        ReflectionHelpers.setField(coreAncFloatingMenu, "menuBar", linearLayout);
        ReflectionHelpers.setField(coreAncFloatingMenu, "referLayout", view);
        ReflectionHelpers.setField(coreAncFloatingMenu, "isFabMenuOpen", true);
    }

    @Test
    public void whenSetupViewsAnswered() {
        coreAncFloatingMenu.setFloatMenuClickListener(onClickFloatingMenu);

        ArgumentCaptor<OnClickFloatingMenu> captor = ArgumentCaptor.forClass(OnClickFloatingMenu.class);
        Mockito.verify(coreAncFloatingMenu, Mockito.times(1)).setFloatMenuClickListener(captor.capture());
        Assert.assertEquals(captor.getValue(), onClickFloatingMenu);
    }

    @Test
    public void whenOnClickAnswered() {
        coreAncFloatingMenu.onClick(view);

        ArgumentCaptor<View> captor = ArgumentCaptor.forClass(View.class);
        Mockito.verify(coreAncFloatingMenu, Mockito.times(1)).onClick(captor.capture());
        Assert.assertEquals(captor.getValue(), view);
    }

    @Test
    public void testGetCallLayout() {
        Mockito.when(coreAncFloatingMenu.getCallLayout())
                .thenReturn(view);
        Assert.assertEquals(view, coreAncFloatingMenu.getCallLayout());
    }


    @Test
    public void testAnimateFABWhenMenuBarVisible() {
        coreAncFloatingMenu.animateFAB();
        assertEquals(View.VISIBLE, coreAncFloatingMenu.menuBar.getVisibility());
    }

    @Test
    public void testAnimateFABWhenMenuBarNotVisible() {
        ReflectionHelpers.setField(coreAncFloatingMenu, "isFabMenuOpen", false);
        coreAncFloatingMenu.animateFAB();
        assertEquals(View.VISIBLE, coreAncFloatingMenu.menuBar.getVisibility());
    }

}
