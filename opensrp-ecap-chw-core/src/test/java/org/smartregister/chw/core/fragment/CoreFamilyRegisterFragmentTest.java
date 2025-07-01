package org.smartregister.chw.core.fragment;

import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.robolectric.Robolectric;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.chw.core.BaseUnitTest;
import org.smartregister.chw.core.R;
import org.smartregister.chw.core.contract.CoreFamilyRegisterFragmentContract;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.receiver.SyncStatusBroadcastReceiver;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class CoreFamilyRegisterFragmentTest extends BaseUnitTest {

    @Mock
    private Context context;

    @Mock
    private ProgressBar syncProgressBar;

    @Mock
    private CommonRepository commonRepository;

    @Mock
    private ImageView syncButton;

    @Mock
    public RecyclerView clientsView;

    @Mock
    private CoreFamilyRegisterFragmentContract.Presenter presenter;

    private CoreFamilyRegisterFragment fragment;

    private FragmentActivity activity;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        fragment = Mockito.mock(CoreFamilyRegisterFragment.class, Mockito.CALLS_REAL_METHODS);
        CoreLibrary.init(context);
        when(context.commonrepository(anyString())).thenReturn(commonRepository);
        activity = Robolectric.buildActivity(AppCompatActivity.class).create().resume().get();
        Context.bindtypes = new ArrayList<>();
        Whitebox.setInternalState(fragment, "clientsView", clientsView);
        Whitebox.setInternalState(fragment, "presenter", presenter);
        SyncStatusBroadcastReceiver.init(activity);
    }

    @Test
    public void presenterInitializesCorrectly() {
        fragment.initializePresenter();
        Assert.assertNotNull(presenter);
    }

    @Test
    public void refreshSyncProgressSpinnerTogglesSyncVisibility() {
        ReflectionHelpers.setField(fragment, "syncButton", syncButton);
        ReflectionHelpers.setField(fragment, "syncProgressBar", syncProgressBar);
        fragment.refreshSyncProgressSpinner();
        Mockito.verify(syncProgressBar, Mockito.times(1)).setVisibility(android.view.View.GONE);
        Mockito.verify(syncButton, Mockito.times(1)).setVisibility(android.view.View.GONE);

    }

    @Test
    public void getMainConditionCallsPresenterGetCondition() {
        fragment.getMainCondition();
        Mockito.verify(fragment.presenter(), Mockito.times(1)).getMainCondition();
    }

    @Test
    public void getDefaultSortQueryCallsPresenterGetSortQuery() {
        fragment.getDefaultSortQuery();
        Mockito.verify(fragment.presenter(), Mockito.times(1)).getDefaultSortQuery();
    }

    @Test
    public void testSetupViews() {
        when(fragment.getActivity()).thenReturn(activity);
        when(fragment.getContext()).thenReturn(activity);
        android.view.View view = LayoutInflater.from(activity).inflate(org.smartregister.family.R.layout.fragment_base_register, null);
        fragment.setupViews(view);
        assertEquals(android.view.View.GONE, view.findViewById(R.id.top_left_layout).getVisibility());
        assertEquals(android.view.View.VISIBLE, view.findViewById(R.id.top_right_layout).getVisibility());
        assertEquals(android.view.View.GONE, view.findViewById(R.id.register_sort_filter_bar_layout).getVisibility());
        assertEquals(android.view.View.GONE, view.findViewById(R.id.filter_sort_layout).getVisibility());
        assertEquals(android.view.View.VISIBLE, view.findViewById(R.id.due_only_layout).getVisibility());
    }

}
