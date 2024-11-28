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

public class FamilyFloatingMenuTest extends BaseUnitTest {
    @Mock
    private CommonRepository commonRepository;

    @Mock
    private Context context;

    private FamilyFloatingMenu familyFloatingMenu;

    @Mock
    private View view;

    @Mock
    private OnClickFloatingMenu onClickFloatingMenu;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        familyFloatingMenu = Mockito.mock(FamilyFloatingMenu.class, Mockito.CALLS_REAL_METHODS);

        CoreLibrary.init(context);
        when(context.commonrepository(anyString())).thenReturn(commonRepository);
        FragmentActivity activity = Robolectric.buildActivity(AppCompatActivity.class).create().resume().get();
        Context.bindtypes = new ArrayList<>();
        SyncStatusBroadcastReceiver.init(activity);

    }

    @Test
    public void whenSetupViewsAnswered() {
        familyFloatingMenu.setClickListener(onClickFloatingMenu);

        ArgumentCaptor<OnClickFloatingMenu> captor = ArgumentCaptor.forClass(OnClickFloatingMenu.class);
        Mockito.verify(familyFloatingMenu, Mockito.times(1)).setClickListener(captor.capture());
        Assert.assertEquals(captor.getValue(), onClickFloatingMenu);
    }
    @Test
    public void whenOnClickAnswered() {
        Mockito.doNothing().when(familyFloatingMenu).onClick(view);
        familyFloatingMenu.onClick(view);

        ArgumentCaptor<View> captor = ArgumentCaptor.forClass(View.class);
        Mockito.verify(familyFloatingMenu, Mockito.times(1)).onClick(captor.capture());
        Assert.assertEquals(captor.getValue(), view);
    }

    @Test
    public void whenRedrawAnswered() {
        Mockito.doNothing().when(familyFloatingMenu).reDraw(true);
        familyFloatingMenu.reDraw(true);

        ArgumentCaptor<Boolean> captor = ArgumentCaptor.forClass(Boolean.class);
        Mockito.verify(familyFloatingMenu, Mockito.times(1)).reDraw(captor.capture());
        Assert.assertEquals(captor.getValue(), true);
    }

}
