package org.smartregister.chw.core.activity;

import android.view.Menu;

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
import org.smartregister.chw.core.adapter.CoreStockUsageItemAdapter;
import org.smartregister.chw.core.domain.StockUsage;
import org.smartregister.chw.core.model.MonthStockUsageModel;
import org.smartregister.chw.core.model.StockUsageItemModel;
import org.smartregister.chw.core.utils.StockUsageReportUtils;

import java.util.ArrayList;
import java.util.List;

public class CoreStockInventoryReportActivityTest extends BaseUnitTest {
    @Rule
    public MockitoRule rule = MockitoJUnit.rule();
    private CoreStockInventoryReportActivity activity;
    private List<StockUsageItemModel> stockUsageItemModelsList = new ArrayList<>();
    private ActivityController<CoreStockInventoryReportActivity> controller;

    @Mock
    private Menu menu;

    private String stockName;
    private String unitsOfMeasure;
    private String month;
    private String year;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        Context context = Context.getInstance();
        CoreLibrary.init(context);

        //setUp
        stockName = "COC";
        unitsOfMeasure = "Packets";
        month = "12";
        year = "2019";

        //Auto login by default
        context.session().start(context.session().lengthInMilliseconds());

        controller = Robolectric.buildActivity(CoreStockInventoryReportActivity.class).create().start();
        activity = controller.get();
    }

    @Test
    public void testGetItems() {
        int size = CoreStockInventoryReportActivity.getItems().size();
        Assert.assertEquals(14, size);
    }

    private void getProvider() {
        activity = Mockito.spy(activity);
        Mockito.doReturn("provider").when(activity).getProviderName();
        Mockito.doReturn("usage").when(activity).getStockUsageForMonth(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
    }

    @Test
    public void testGetStockUsageItemReportList() {

        StockUsage stockUsage = new StockUsage();
        stockUsage.setProviderId("chwone");
        List<StockUsage> providerName = new ArrayList<>();
        providerName.add(stockUsage);

        getProvider();
        stockUsageItemModelsList.add(new StockUsageItemModel(stockName, unitsOfMeasure, "20", providerName.get(0).getProviderId()));
        activity.getStockUsageItemReportList(month, year);
        Assert.assertEquals(1, stockUsageItemModelsList.size());
    }

    @Test
    public void testReloadRecycler() {
        MonthStockUsageModel selected = activity.getMonthStockUsageReportList().get(0);
        Assert.assertNotNull(selected);
        StockUsageReportUtils stockUsageReportUtils = new StockUsageReportUtils();
        String stockMonth = stockUsageReportUtils.getMonthNumber(selected.getMonth().substring(0, 3));
        String stockYear = selected.getYear();

        getProvider();
        List<StockUsageItemModel> stockUsageItemReportList = activity.getStockUsageItemReportList(stockMonth, stockYear);
        CoreStockUsageItemAdapter coreStockUsageItemAdapter = new CoreStockUsageItemAdapter(stockUsageItemReportList, activity);

        activity.onCreation();
        activity.recyclerView.setAdapter(coreStockUsageItemAdapter);

        activity.reloadRecycler(selected);
        Assert.assertNotNull(activity.recyclerView);
    }

    @Test
    public void testOnCreation() {
        // check if created views are found
        activity.onCreation();
        Assert.assertNotNull(ReflectionHelpers.getField(activity, "toolBarTextView"));
        Assert.assertNotNull(ReflectionHelpers.getField(activity, "appBarLayout"));

    }

    @Test
    public void testOnCreateOptionsMenu() {
        Assert.assertFalse(activity.onCreateOptionsMenu(menu));
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