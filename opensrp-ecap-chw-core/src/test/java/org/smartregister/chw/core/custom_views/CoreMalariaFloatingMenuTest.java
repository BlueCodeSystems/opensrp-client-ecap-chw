package org.smartregister.chw.core.custom_views;

import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.chw.core.BaseUnitTest;
import org.smartregister.chw.core.listener.OnClickFloatingMenu;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.receiver.SyncStatusBroadcastReceiver;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class CoreMalariaFloatingMenuTest extends BaseUnitTest {
    @Mock
    private CommonRepository commonRepository;

    @Mock
    private Context context;

    private CoreMalariaFloatingMenu coreMalariaFloatingMenu;

    @Mock
    private View view;

    @Mock
    private OnClickFloatingMenu onClickFloatingMenu;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        coreMalariaFloatingMenu = Mockito.mock(CoreMalariaFloatingMenu.class, Mockito.CALLS_REAL_METHODS);

        CoreLibrary.init(context);
        when(context.commonrepository(anyString())).thenReturn(commonRepository);
        FragmentActivity activity = Robolectric.buildActivity(AppCompatActivity.class).create().resume().get();
        Context.bindtypes = new ArrayList<>();
        SyncStatusBroadcastReceiver.init(activity);

    }

    @Test
    public void whenSetupViewsAnswered() {
        coreMalariaFloatingMenu.setFloatMenuClickListener(onClickFloatingMenu);

        ArgumentCaptor<OnClickFloatingMenu> captor = ArgumentCaptor.forClass(OnClickFloatingMenu.class);
        Mockito.verify(coreMalariaFloatingMenu, Mockito.times(1)).setFloatMenuClickListener(captor.capture());
        Assert.assertEquals(captor.getValue(), onClickFloatingMenu);
    }

    @Test
    public void whenOnClickAnswered() {
        Mockito.doNothing().when(coreMalariaFloatingMenu).onClick(view);
        coreMalariaFloatingMenu.onClick(view);

        ArgumentCaptor<View> captor = ArgumentCaptor.forClass(View.class);
        Mockito.verify(coreMalariaFloatingMenu, Mockito.times(1)).onClick(captor.capture());
        Assert.assertEquals(captor.getValue(), view);
    }

    @Test
    public void whenRedrawAnswered() {
        Mockito.doNothing().when(coreMalariaFloatingMenu).redraw(true);
        coreMalariaFloatingMenu.redraw(true);

        ArgumentCaptor<Boolean> captor = ArgumentCaptor.forClass(Boolean.class);
        Mockito.verify(coreMalariaFloatingMenu, Mockito.times(1)).redraw(captor.capture());
        Assert.assertEquals(captor.getValue(), true);
    }

    @Test
    public void testGetCallLayout() {
        Mockito.when(coreMalariaFloatingMenu.getCallLayout())
                .thenReturn(view);
        Assert.assertEquals(view, coreMalariaFloatingMenu.getCallLayout());
    }

}
