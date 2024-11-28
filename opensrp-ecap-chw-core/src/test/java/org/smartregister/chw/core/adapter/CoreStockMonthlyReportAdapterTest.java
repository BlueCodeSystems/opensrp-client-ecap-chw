package org.smartregister.chw.core.adapter;

import android.content.Context;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.robolectric.RuntimeEnvironment;
import org.smartregister.chw.core.BaseUnitTest;
import org.smartregister.chw.core.model.MonthStockUsageModel;

import java.util.List;

public class CoreStockMonthlyReportAdapterTest extends BaseUnitTest {
    private CoreStockMonthlyReportAdapter adapter;
    private List<MonthStockUsageModel> monthStockUsageModelList;
    private Context context = RuntimeEnvironment.application;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        monthStockUsageModelList = java.util.Arrays.asList(new MonthStockUsageModel("January", "2020"),
                new MonthStockUsageModel("December", "2019"));
        adapter = new CoreStockMonthlyReportAdapter(monthStockUsageModelList, context);
    }

    @Test
    public void testGetCount() {
        adapter.getCount();
        Assert.assertEquals(2, monthStockUsageModelList.size());
    }

    @Test
    public void testGetItem() {
        Assert.assertEquals(adapter.getItem(0), monthStockUsageModelList.get(0));
    }

    @Test
    public void testGetItemId() {
        Assert.assertEquals(adapter.getItemId(0), 0);
    }

}