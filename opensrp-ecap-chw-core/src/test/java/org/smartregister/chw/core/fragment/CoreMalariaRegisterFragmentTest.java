package org.smartregister.chw.core.fragment;

import android.os.Bundle;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.powermock.reflect.Whitebox;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.chw.core.BaseUnitTest;
import org.smartregister.chw.core.R;
import org.smartregister.chw.malaria.presenter.BaseMalariaRegisterFragmentPresenter;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.cursoradapter.RecyclerViewPaginatedAdapter;
import org.smartregister.receiver.SyncStatusBroadcastReceiver;

import java.util.ArrayList;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CoreMalariaRegisterFragmentTest extends BaseUnitTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();
    @Mock
    public RecyclerView clientsView;
    @Mock
    private Context context;

    @Mock
    private TextView headerTextDisplay;

    @Mock
    private RelativeLayout filterRelativeLayout;

    @Captor
    private ArgumentCaptor<RecyclerViewPaginatedAdapter> adapterArgumentCaptor;

    @Mock
    private CoreMalariaRegisterFragment coreMalariaRegisterFragment;

    @Mock
    private CommonRepository commonRepository;

    @Mock
    private View view;

    private FragmentActivity activity;

    private ActivityController<AppCompatActivity> activityController;

    @Mock
    private ImageView imageView;

    @Mock
    private ProgressBar clientsProgressView;

    @Mock
    private TextView textView;

    @Mock
    private RelativeLayout relativeLayout;

    @Mock
    private EditText editText;

    @Mock
    private TextWatcher textWatcher;

    @Mock
    private View.OnKeyListener hideKeyboard;

    @Mock
    private ProgressBar syncProgressBar;

    @Mock
    private BaseMalariaRegisterFragmentPresenter presenter;

    @Mock
    private LayoutInflater layoutInflater;

    @Mock
    private ViewGroup container;

    @Mock
    private Bundle bundle;

    @Mock
    private ActionBar actionBar;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        coreMalariaRegisterFragment = Mockito.mock(CoreMalariaRegisterFragment.class, Mockito.CALLS_REAL_METHODS);
        ReflectionHelpers.setField(coreMalariaRegisterFragment, "presenter", presenter);
        ReflectionHelpers.setField(coreMalariaRegisterFragment, "view", view);
        ReflectionHelpers.setField(coreMalariaRegisterFragment, "dueOnlyLayout", view);
        ReflectionHelpers.setField(coreMalariaRegisterFragment, "clientsProgressView", clientsProgressView);
        ReflectionHelpers.setField(coreMalariaRegisterFragment, "dueFilterActive", true);
        ReflectionHelpers.setField(coreMalariaRegisterFragment, "qrCodeScanImageView", imageView);
        ReflectionHelpers.setField(coreMalariaRegisterFragment, "headerTextDisplay", textView);
        ReflectionHelpers.setField(coreMalariaRegisterFragment, "filterStatus", textView);
        ReflectionHelpers.setField(coreMalariaRegisterFragment, "filterRelativeLayout", relativeLayout);
        ReflectionHelpers.setField(coreMalariaRegisterFragment, "searchView", editText);
        ReflectionHelpers.setField(coreMalariaRegisterFragment, "textWatcher", textWatcher);
        ReflectionHelpers.setField(coreMalariaRegisterFragment, "hideKeyboard", hideKeyboard);
        ReflectionHelpers.setField(coreMalariaRegisterFragment, "syncProgressBar", syncProgressBar);
        ReflectionHelpers.setField(coreMalariaRegisterFragment, "syncButton", imageView);
        ReflectionHelpers.setField(coreMalariaRegisterFragment, "globalQrSearch", false);
        ReflectionHelpers.setField(coreMalariaRegisterFragment, "headerTextDisplay", headerTextDisplay);
        ReflectionHelpers.setField(coreMalariaRegisterFragment, "filterRelativeLayout", filterRelativeLayout);

        CoreLibrary.init(context);
        when(context.commonrepository(anyString())).thenReturn(commonRepository);
        activityController = Robolectric.buildActivity(AppCompatActivity.class).create().resume();
        activity = activityController.get();
        Context.bindtypes = new ArrayList<>();
        Whitebox.setInternalState(coreMalariaRegisterFragment, "clientsView", clientsView);
        Whitebox.setInternalState(coreMalariaRegisterFragment, "presenter", presenter);
        SyncStatusBroadcastReceiver.init(activity);
    }

    @Test
    public void testInitializeAdapter() {
        when(coreMalariaRegisterFragment.getActivity()).thenReturn(activity);
        coreMalariaRegisterFragment.initializeAdapter(new HashSet<>());
        verify(clientsView).setAdapter(adapterArgumentCaptor.capture());
        assertNotNull(adapterArgumentCaptor.getValue());
        assertEquals(20, adapterArgumentCaptor.getValue().currentlimit);
    }

    @Test
    public void testSetupViewsInitializesViews() {
        when(coreMalariaRegisterFragment.getActivity()).thenReturn(activity);
        when(coreMalariaRegisterFragment.getContext()).thenReturn(activity);
        View parentLayout = LayoutInflater.from(RuntimeEnvironment.application.getApplicationContext()).inflate(org.smartregister.R.layout.fragment_base_register, null, false);
        Mockito.doReturn(parentLayout).when(layoutInflater).inflate(org.smartregister.R.layout.fragment_base_register, container, false);
        Mockito.doReturn(imageView).when(view).findViewById(R.id.scanQrCode);

        AppCompatActivity activitySpy = (AppCompatActivity) Mockito.spy(activity);
        Mockito.doReturn(activitySpy).when(coreMalariaRegisterFragment).getActivity();

        Mockito.doReturn(actionBar).when(activitySpy).getSupportActionBar();

        coreMalariaRegisterFragment.onCreateView(layoutInflater, container, bundle);

        verify(coreMalariaRegisterFragment).setupViews(parentLayout);
    }


    @Test
    public void testOnViewClick() {
        coreMalariaRegisterFragment.onViewClicked(view);
        ArgumentCaptor<View> captor = ArgumentCaptor.forClass(View.class);
        verify(coreMalariaRegisterFragment, Mockito.times(1)).onViewClicked(captor.capture());
        assertEquals(captor.getValue(), view);
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
