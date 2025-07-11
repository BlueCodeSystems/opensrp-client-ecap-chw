package org.smartregister.chw.core.adapter;

import android.widget.TextView;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.chw.core.model.StockUsageItemDetailsModel;

import java.util.Arrays;
import java.util.List;

public class CoreStockUsageItemDetailsAdapterTest {

    private CoreStockUsageItemDetailsAdapter adapter;
    private List<StockUsageItemDetailsModel> stockUsageItemDetailsModelList;

    @Mock
    private CoreStockUsageItemDetailsAdapter.CoreStockUsageReportDetailsViewHolder viewHolder;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        stockUsageItemDetailsModelList = Arrays.asList(new StockUsageItemDetailsModel("January", "2020", "30"),
                new StockUsageItemDetailsModel("December", "2019", "20"));
        adapter = new CoreStockUsageItemDetailsAdapter(stockUsageItemDetailsModelList);
    }

    @Test
    public void testOnBindViewHolder() {

        TextView month = Mockito.mock(TextView.class);
        TextView year = Mockito.mock(TextView.class);
        TextView usage = Mockito.mock(TextView.class);

        ReflectionHelpers.setField(viewHolder, "itemDetailsMonth", month);
        ReflectionHelpers.setField(viewHolder, "itemDetailsYear", year);
        ReflectionHelpers.setField(viewHolder, "itemDetailsStockCount", usage);

        StockUsageItemDetailsModel stockUsageItemDetailsModel = stockUsageItemDetailsModelList.get(0);

        adapter.onBindViewHolder(viewHolder, 0);
        Mockito.verify(month).setText(stockUsageItemDetailsModel.getItemDetailsMonth());
        Mockito.verify(year).setText(stockUsageItemDetailsModel.getItemDetailsYear());
        Mockito.verify(usage).setText(stockUsageItemDetailsModel.getItemDetailsStockValue());
    }

    @Test
    public void testGetItemCount() {
        Assert.assertEquals(2, stockUsageItemDetailsModelList.size());

    }
}