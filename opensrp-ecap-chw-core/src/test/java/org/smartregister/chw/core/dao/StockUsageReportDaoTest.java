package org.smartregister.chw.core.dao;

import net.sqlcipher.MatrixCursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.smartregister.chw.core.domain.StockUsage;
import org.smartregister.repository.Repository;

import java.util.List;

public class StockUsageReportDaoTest extends StockUsageReportDao {

    @Mock
    private Repository repository;
    @Mock
    private SQLiteDatabase database;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        setRepository(repository);
    }

    @Test
    public void testGetStockUsage() {
        Mockito.doReturn(database).when(repository).getReadableDatabase();
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{
                "Year", "Month", "StockName", "Usage"
        });
        Mockito.doReturn(matrixCursor).when(database).rawQuery(Mockito.any(), Mockito.any());
        matrixCursor.addRow(new Object[]{"Year", "Month", "StockName", "Usage"});

        List<StockUsage> stockUsageList = StockUsageReportDao.getStockUsage("userName");
        Mockito.verify(database).rawQuery(Mockito.anyString(), Mockito.any());
        Assert.assertEquals(stockUsageList.get(0).getStockName(), "StockName");
        Assert.assertEquals(stockUsageList.get(0).getStockUsage(), "Usage");
        Assert.assertEquals(stockUsageList.get(0).getYear(), "Year");
        Assert.assertEquals(stockUsageList.get(0).getMonth(), "Month");

    }

    @Test
    public void testLastInteractedWithinDay() {
        Mockito.doReturn(database).when(repository).getReadableDatabase();
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{"count"});
        matrixCursor.addRow(new Object[]{"5"});
        Mockito.doReturn(matrixCursor).when(database).rawQuery(Mockito.any(), Mockito.any());

        Boolean count1 = StockUsageReportDao.lastInteractedWithinDay();
        Mockito.verify(database).rawQuery(Mockito.anyString(), Mockito.any());
        Assert.assertTrue(count1);
    }

    @Test
    public void testGetStockUsageForMonth() {
        Mockito.doReturn(database).when(repository).getReadableDatabase();
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{"stock_usage"});
        matrixCursor.addRow(new Object[]{"2"});
        Mockito.doReturn(matrixCursor).when(database).rawQuery(Mockito.any(), Mockito.any());

        String count = StockUsageReportDao.getStockUsageForMonth("12", "COC", "2019", "chwone");
        Mockito.verify(database).rawQuery(Mockito.anyString(), Mockito.any());
        Assert.assertEquals(count, "2");
    }

    @Test
    public void testGetAllStockUsageForMonth() {
        Mockito.doReturn(database).when(repository).getReadableDatabase();
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{"stock_usage"});
        matrixCursor.addRow(new Object[]{"2"});
        Mockito.doReturn(matrixCursor).when(database).rawQuery(Mockito.any(), Mockito.any());

        String count = StockUsageReportDao.getAllStockUsageForMonth("12", "COC", "2019");
        Mockito.verify(database).rawQuery(Mockito.anyString(), Mockito.any());
        Assert.assertEquals(count, "2");
    }
}