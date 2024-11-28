package org.smartregister.chw.core.fragment;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.robolectric.Robolectric;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.chw.core.BaseUnitTest;
import org.smartregister.chw.core.R;
import org.smartregister.chw.core.activity.CoreChildRegisterActivity;
import org.smartregister.chw.core.mock.MockCoreChildRegisterFragment;
import org.smartregister.chw.core.presenter.CoreChildRegisterFragmentPresenter;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.domain.FetchStatus;
import org.smartregister.family.fragment.NoMatchDialogFragment;
import org.smartregister.receiver.SyncStatusBroadcastReceiver;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.robolectric.Shadows.shadowOf;
import static org.smartregister.family.fragment.BaseFamilyRegisterFragment.CLICK_VIEW_NORMAL;
import static org.smartregister.view.fragment.SecuredNativeSmartRegisterFragment.DIALOG_TAG;

public class CoreChildRegisterFragmentTest extends BaseUnitTest {
    @Mock
    public RecyclerView clientsView;
    @Mock
    private CommonRepository commonRepository;
    @Mock
    private Context context;
    @Mock
    private ProgressBar syncProgressBar;
    @Mock
    private ImageView syncButton;
    @Mock
    private CoreChildRegisterFragmentPresenter presenter;
    private CoreChildRegisterFragment fragment;
    @Mock
    private View view;
    @Mock
    private TextView textView;

    private FragmentActivity activity;

    private ActivityController controller;

    @Mock
    private CoreChildRegisterActivity coreChildRegisterActivity;

    @Mock
    private FragmentManager fragmentManager;

    @Mock
    private FragmentTransaction fragmentTransaction;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        fragment = Mockito.mock(CoreChildRegisterFragment.class, Mockito.CALLS_REAL_METHODS);
        ReflectionHelpers.setField(fragment, "presenter", presenter);
        ReflectionHelpers.setField(fragment, "view", view);
        ReflectionHelpers.setField(fragment, "dueOnlyLayout", view);
        ReflectionHelpers.setField(fragment, "dueFilterActive", true);

        CoreLibrary.init(context);
        when(context.commonrepository(anyString())).thenReturn(commonRepository);
        controller = Robolectric.buildActivity(AppCompatActivity.class).create().resume();
        activity = (FragmentActivity) controller.get();
        Context.bindtypes = new ArrayList<>();
        Whitebox.setInternalState(fragment, "clientsView", clientsView);
        Whitebox.setInternalState(fragment, "presenter", presenter);
        SyncStatusBroadcastReceiver.init(activity);
    }

    @Test
    public void testInitializePresenter() {
        fragment.initializePresenter();
        assertNotNull(presenter);
    }

    @Test
    public void testGetMainCondition() {
        assertEquals(fragment.getMainCondition(), presenter.getMainCondition());
    }

    @Test
    public void testGetDefaultSortQuery() {
        assertEquals(fragment.getDefaultSortQuery(), presenter.getDefaultSortQuery());
    }

    private void getDueFilterCondition() {
        Mockito.doReturn("").when(fragment).getDueFilterCondition();
    }

    @Test
    public void testOnResumption() {
        Mockito.doNothing().when(fragment).filterAndSortExecute();
        getDueFilterCondition();
        Mockito.doReturn(textView).when(fragment).getDueOnlyTextView(fragment.dueOnlyLayout);
        fragment.onResumption();
        assertEquals(fragment.dueOnlyLayout, view);
    }

    @Test
    public void testGetToolBarTitle() {
        int title = R.string.child_register_title;
        assertEquals(fragment.getToolBarTitle(), title);
    }

    @Test
    public void testSetUniqueID() {
        when(fragment.getSearchView()).thenReturn(new EditText(activity));
        fragment.setUniqueID("unique");
        assertEquals("unique", fragment.getSearchView().getText().toString());
    }

    @Test
    public void refreshSyncProgressSpinnerTogglesSyncVisibility() {
        ReflectionHelpers.setField(fragment, "syncButton", syncButton);
        ReflectionHelpers.setField(fragment, "syncProgressBar", syncProgressBar);
        fragment.refreshSyncProgressSpinner();
        verify(syncProgressBar, Mockito.times(1)).setVisibility(View.GONE);
        verify(syncButton, Mockito.times(1)).setVisibility(View.GONE);
    }


    @Test
    public void getMainConditionCallsPresenterGetCondition() {
        fragment.getMainCondition();
        verify(fragment.presenter(), Mockito.times(1)).getMainCondition();
    }

    @Test
    public void getDefaultSortQueryCallsPresenterGetSortQuery() {
        fragment.getDefaultSortQuery();
        verify(fragment.presenter(), Mockito.times(1)).getDefaultSortQuery();
    }

    @Test
    public void getDueFilterConditionCallsPresenterGetSortQuery() {
        fragment.getDueFilterCondition();
        verify(fragment.presenter(), Mockito.times(1)).getDueFilterCondition();
    }

    @Test
    public void onSyncCompleteTogglesSyncVisibility() {
        FetchStatus fetchStatus = Mockito.anyObject();
        ReflectionHelpers.setField(fragment, "syncButton", syncButton);
        ReflectionHelpers.setField(fragment, "syncProgressBar", syncProgressBar);
        fragment.onSyncComplete(fetchStatus);
        verify(syncProgressBar, Mockito.times(2)).setVisibility(View.GONE);
        verify(syncButton, Mockito.times(2)).setVisibility(View.GONE);
    }


    @Test
    public void testOnViewClickedDoesNothing() {
        when(fragment.getActivity()).thenReturn(null);
        fragment.onViewClicked(view);
        verifyZeroInteractions(view);
    }

    @Test
    public void testOnViewClickedOpensProfile() {
        FragmentActivity childRegisterActivity = Robolectric.buildActivity(AppCompatActivity.class).create().resume().get();
        fragment = new MockCoreChildRegisterFragment();
        Context.bindtypes = new ArrayList<>();
        Whitebox.setInternalState(fragment, "clientsView", clientsView);
        Whitebox.setInternalState(fragment, "presenter", presenter);
        childRegisterActivity.getSupportFragmentManager().beginTransaction().add(0, fragment).commit();
        when(view.getTag(org.smartregister.family.R.id.VIEW_ID)).thenReturn(CLICK_VIEW_NORMAL);
        CommonPersonObjectClient client = new CommonPersonObjectClient("12", null, "");
        client.setColumnmaps(new HashMap<String, String>());
        when(view.getTag()).thenReturn(client);
        fragment.onViewClicked(view);
        Intent intent = shadowOf(childRegisterActivity).getNextStartedActivity();
        assertNotNull(intent);
    }

    @Test
    public void testShowNotFoundPopupShowsDialog() {
        when(fragment.getActivity()).thenReturn(coreChildRegisterActivity);
        when(coreChildRegisterActivity.getFragmentManager()).thenReturn(fragmentManager);
        when(fragmentManager.beginTransaction()).thenReturn(fragmentTransaction);
        fragment.showNotFoundPopup("1234");
        verify(fragmentManager).beginTransaction();
        verify(fragmentTransaction).addToBackStack(null);
        verify(fragmentTransaction).add(any(NoMatchDialogFragment.class), eq(DIALOG_TAG));
    }

    @Test
    public void testRefreshSyncProgressSpinner() {
        Whitebox.setInternalState(fragment, "syncProgressBar", syncProgressBar);
        Whitebox.setInternalState(fragment, "syncButton", syncButton);
        fragment.refreshSyncProgressSpinner();
        verify(syncButton).setVisibility(View.GONE);
    }

    @Test
    public void testSetupViews() {
        when(fragment.getActivity()).thenReturn(activity);
        when(fragment.getContext()).thenReturn(activity);
        View view = LayoutInflater.from(activity).inflate(R.layout.fragment_base_register, null);
        fragment.setupViews(view);

        View dueOnlyLayout = view.findViewById(R.id.due_only_layout);
        dueOnlyLayout.setVisibility(View.VISIBLE);
        assertEquals(View.VISIBLE, dueOnlyLayout.getVisibility());
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