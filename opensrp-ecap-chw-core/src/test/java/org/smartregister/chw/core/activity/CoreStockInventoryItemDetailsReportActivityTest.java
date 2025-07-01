package org.smartregister.chw.core.activity;

import android.content.Intent;
import android.view.Menu;

import androidx.recyclerview.widget.RecyclerView;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.chw.core.BaseUnitTest;
import org.smartregister.chw.core.adapter.CoreStockUsageItemDetailsAdapter;
import org.smartregister.chw.core.utils.CoreConstants;


@RunWith(RobolectricTestRunner.class)
public class CoreStockInventoryItemDetailsReportActivityTest extends BaseUnitTest {
    @Rule
    public MockitoRule rule = MockitoJUnit.rule();
    @Mock
    private RecyclerView recyclerView;
    @Mock
    private Menu menu;
    private CoreStockInventoryItemDetailsReportActivity activity;

    @Mock
    private CoreStockUsageItemDetailsAdapter coreStockUsageItemDetailsAdapter;

    private ActivityController<CoreStockInventoryItemDetailsReportActivity> controller;

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

        String stockItemName = "Male Condoms";
        String providerName = "All-CHWs";
        Intent intent = new Intent();
        intent.putExtra(CoreConstants.HfStockUsageUtil.STOCK_NAME, stockItemName);
        intent.putExtra(CoreConstants.HfStockUsageUtil.PROVIDER_NAME, providerName);
        controller = Robolectric.buildActivity(CoreStockInventoryItemDetailsReportActivity.class, intent).create().start();
        activity = controller.get();
    }

    @Test
    public void testOnCreateOptionsMenu() {
        Assert.assertFalse(activity.onCreateOptionsMenu(menu));
    }

    @Test
    public void testOnCreation() {
        // check if created views are found
        activity.onCreation();
        Assert.assertNotNull(ReflectionHelpers.getField(activity, "toolBarTextView"));
        Assert.assertNotNull(ReflectionHelpers.getField(activity, "appBarLayout"));

        recyclerView.setAdapter(coreStockUsageItemDetailsAdapter);
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