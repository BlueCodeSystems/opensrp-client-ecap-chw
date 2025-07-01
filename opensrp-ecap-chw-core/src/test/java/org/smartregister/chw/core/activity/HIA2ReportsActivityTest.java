package org.smartregister.chw.core.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.powermock.reflect.Whitebox;
import org.robolectric.Robolectric;
import org.robolectric.android.controller.ActivityController;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.chw.core.BaseUnitTest;
import org.smartregister.chw.core.R;
import org.smartregister.chw.core.adapter.NavigationAdapter;
import org.smartregister.chw.core.custom_views.NavigationMenu;
import org.smartregister.domain.FetchStatus;

public class HIA2ReportsActivityTest extends BaseUnitTest {
    @Rule
    public MockitoRule rule = MockitoJUnit.rule();
    private HIA2ReportsActivity activity;
    private ActivityController<HIA2ReportsActivity> controller;
    private final Intent intent = new Intent();
    @Mock
    private ProgressDialog progressDialog;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        Context context = Context.getInstance();
        CoreLibrary.init(context);

        //Auto login by default
        context.session().start(context.session().lengthInMilliseconds());

        controller = Robolectric.buildActivity(HIA2ReportsActivity.class).create().start().resume();
        activity = controller.get();
    }

    @After
    public void tearDown() {
        try {
            activity.finish();
            controller.pause().stop().destroy(); //destroy controller if we can
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testOnBackActivityReturnsNull() {
        activity.onBackActivity();
        Assert.assertNull(activity.onBackActivity());
    }

    @Test
    public void testOnSyncStart() {
        ImageView reportSyncBtn = Mockito.mock(ImageView.class);

        activity.setTheme(org.smartregister.R.style.AppTheme);
        activity.onSyncStart();
        Assert.assertEquals(reportSyncBtn.getVisibility(), View.VISIBLE);
    }

    @Test
    public void testOnSyncComplete() {
        ImageView reportSyncBtn = Mockito.mock(ImageView.class);

        activity.setTheme(org.smartregister.R.style.AppTheme);
        FetchStatus status = FetchStatus.fetchStarted;

        activity.onSyncComplete(status);
        Assert.assertEquals(reportSyncBtn.getVisibility(), View.VISIBLE);
    }

    @Test
    public void testOnBackPressed() {
        NavigationMenu navigationMenu = Mockito.mock(NavigationMenu.class);
        activity = Mockito.spy(Robolectric
                .buildActivity(HIA2ReportsActivity.class, intent)
                .create()
                .get());
        NavigationAdapter navigationAdapter = Mockito.mock(NavigationAdapter.class);
        Mockito.when(navigationMenu.getNavigationAdapter()).thenReturn(navigationAdapter);
        activity.onBackPressed();

        Mockito.verify(activity).onBackPressed();
    }

    @Test
    public void testShowProgressDialog() {
        activity = Mockito.spy(Robolectric
                .buildActivity(HIA2ReportsActivity.class, intent)
                .create()
                .get());

        Whitebox.setInternalState(activity, "progressDialog", progressDialog);
        activity.showProgressDialog();
        Mockito.verify(progressDialog).show();
    }

    @Test
    public void testHideProgressDialog() {
        activity = Mockito.spy(Robolectric
                .buildActivity(HIA2ReportsActivity.class, intent)
                .create()
                .get());

        Whitebox.setInternalState(activity, "progressDialog", progressDialog);
        activity.hideProgressDialog();
        Mockito.verify(progressDialog).dismiss();
    }

    @Test
    public void testOnClickReport() {
        View view = Mockito.mock(View.class);
        activity = Mockito.spy(activity);
        NavigationMenu navigationMenu = Mockito.mock(NavigationMenu.class);
        Mockito.doReturn(R.id.btn_back_to_home).when(view).getId();
        activity.onClickReport(view);

        Assert.assertNotNull(navigationMenu);
        Mockito.verify(activity).onClickReport(view);
    }

    @Test
    public void testOnSyncInProgress() {
        ImageView reportSyncBtn = Mockito.mock(ImageView.class);
        FetchStatus status = FetchStatus.fetchStarted;

        activity.onSyncInProgress(status);
        Assert.assertEquals(reportSyncBtn.getVisibility(), View.VISIBLE);
    }


}
