package org.smartregister.chw.core.activity;

import android.content.Intent;
import android.content.pm.PackageManager;

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
import org.robolectric.Robolectric;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.chw.core.BaseUnitTest;
import org.smartregister.chw.core.activity.impl.CoreFamilyRegisterActivityTestImpl;
import org.smartregister.chw.core.adapter.NavigationAdapter;
import org.smartregister.chw.core.custom_views.NavigationMenu;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.family.presenter.BaseFamilyRegisterPresenter;

public class CoreFamilyRegisterActivityTest extends BaseUnitTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    private CoreFamilyRegisterActivityTestImpl activity;
    private ActivityController<CoreFamilyRegisterActivityTestImpl> controller;

    @Mock
    private BaseFamilyRegisterPresenter presenter;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        Context context = Context.getInstance();
        CoreLibrary.init(context);

        //Auto login by default
        //String password = "pwd";
        context.session().start(context.session().lengthInMilliseconds());
        //context.configuration().getDrishtiApplication().setPassword(password);
        //context.session().setPassword(password);

        controller = Robolectric.buildActivity(CoreFamilyRegisterActivityTestImpl.class, new Intent()).create().start();
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
    public void testPresenterIsSetUp() {
        BaseFamilyRegisterPresenter presenter = ReflectionHelpers.getField(activity, "presenter");
        Assert.assertTrue(presenter instanceof BaseFamilyRegisterPresenter);
    }

    @Test
    public void testOnResumption() {
        NavigationMenu menu = Mockito.mock(NavigationMenu.class);
        NavigationAdapter adapter = Mockito.mock(NavigationAdapter.class);

        Mockito.doReturn(adapter).when(menu).getNavigationAdapter();
        ReflectionHelpers.setStaticField(NavigationMenu.class, "instance", menu);

        ReflectionHelpers.setField(activity, "presenter", presenter);
        activity.onResumption();

        Mockito.verify(adapter).setSelectedView(Mockito.anyString());
    }

    @Test
    public void testOnRequestPermissionsResult() {
        NavigationMenu menu = Mockito.mock(NavigationMenu.class);
        ReflectionHelpers.setStaticField(NavigationMenu.class, "instance", menu);

        activity.onRequestPermissionsResult(CoreConstants.RQ_CODE.STORAGE_PERMISIONS, new String[]{}, new int[]{PackageManager.PERMISSION_GRANTED});
        Mockito.verify(menu).startP2PActivity(Mockito.any());
    }
}
