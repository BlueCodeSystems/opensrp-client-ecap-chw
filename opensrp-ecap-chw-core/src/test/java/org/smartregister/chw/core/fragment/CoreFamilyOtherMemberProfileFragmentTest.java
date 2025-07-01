package org.smartregister.chw.core.fragment;

import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.powermock.reflect.Whitebox;
import org.robolectric.Robolectric;
import org.robolectric.android.controller.ActivityController;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.chw.core.BaseUnitTest;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.family.contract.FamilyOtherMemberProfileFragmentContract;
import org.smartregister.receiver.SyncStatusBroadcastReceiver;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class CoreFamilyOtherMemberProfileFragmentTest extends BaseUnitTest {
    @Rule
    public MockitoRule rule = MockitoJUnit.rule();
    @Mock
    private Context context;
    @Mock
    private View view;
    @Mock
    private CommonRepository commonRepository;
    @Mock
    private FamilyOtherMemberProfileFragmentContract.Presenter presenter;
    private CoreFamilyOtherMemberProfileFragment fragment;
    private FragmentActivity activity;
    private ActivityController<AppCompatActivity> activityController;

    @Before
    public void setUp() {
        Context.bindtypes = new ArrayList<>();
        fragment = Mockito.mock(CoreFamilyOtherMemberProfileFragment.class, Mockito.CALLS_REAL_METHODS);
        CoreLibrary.init(context);
        when(context.commonrepository(anyString())).thenReturn(commonRepository);
        activityController = Robolectric.buildActivity(AppCompatActivity.class).create().resume();
        activity = activityController.get();
        Context.bindtypes = new ArrayList<>();
        Whitebox.setInternalState(fragment, "presenter", presenter);
        SyncStatusBroadcastReceiver.init(activity);
    }

    @Test
    public void testPresenter() {
        FamilyOtherMemberProfileFragmentContract.Presenter presenterInstance = fragment.presenter();
        Assert.assertEquals(presenter, presenterInstance);
    }

    @Test
    public void whenOnViewClickedAnswered() {
        fragment.onViewClicked(view);
        ArgumentCaptor<View> captor = ArgumentCaptor.forClass(View.class);
        Mockito.verify(fragment, Mockito.times(1)).onViewClicked(captor.capture());
        Assert.assertEquals(captor.getValue(), view);
    }

    @After
    public void tearDown() {
        try {
            activityController.pause().stop().destroy();
            activity.finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
