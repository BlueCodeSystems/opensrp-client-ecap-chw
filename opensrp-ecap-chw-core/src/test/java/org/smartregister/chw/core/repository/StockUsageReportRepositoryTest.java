package org.smartregister.chw.core.repository;

import android.content.ContentValues;

import net.sqlcipher.MatrixCursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.powermock.reflect.Whitebox;
import org.smartregister.chw.core.BaseUnitTest;
import org.smartregister.chw.core.domain.StockUsage;
import org.smartregister.repository.Repository;
import org.smartregister.view.activity.DrishtiApplication;

import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class StockUsageReportRepositoryTest extends BaseUnitTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    private StockUsageReportRepository stockUsageReportRepository;

    @Mock
    private static Repository repository;

    @Mock
    private SQLiteDatabase sqLiteDatabase;

    @Captor
    private ArgumentCaptor<ContentValues> contentValuesArgumentCaptor;

    @Captor
    private ArgumentCaptor<String> stringArgumentCaptor;

    @Before
    public void setUp() {
        stockUsageReportRepository = Mockito.spy(new StockUsageReportRepository());
        when(repository.getReadableDatabase()).thenReturn(sqLiteDatabase);
        when(repository.getWritableDatabase()).thenReturn(sqLiteDatabase);
        Whitebox.setInternalState(DrishtiApplication.getInstance(), "repository", repository);
    }

    @Test
    public void testAddOrUpdateShouldAdd() {

        StockUsage stockUsage = new StockUsage();
        stockUsage.setId("1234");
        stockUsage.setStockName("West");
        Mockito.doReturn(sqLiteDatabase).when(stockUsageReportRepository).getWritableDatabase();

        stockUsageReportRepository.addOrUpdateStockUsage(stockUsage);

        verify(sqLiteDatabase).replace(stringArgumentCaptor.capture(), stringArgumentCaptor.capture(), contentValuesArgumentCaptor.capture());
        assertEquals(2, stringArgumentCaptor.getAllValues().size());

        Iterator<String> iterator = stringArgumentCaptor.getAllValues().iterator();
        assertEquals(StockUsageReportRepository.TABLE_NAME, iterator.next());
        assertNull(iterator.next());

        ContentValues contentValues = contentValuesArgumentCaptor.getValue();
        assertEquals(8, contentValues.size());

        assertEquals("1234", contentValues.getAsString("id"));
        assertEquals("West", contentValues.getAsString("stock_name"));
    }

    @Test
    public void testGetStockUsageByName() {
        Mockito.doReturn(sqLiteDatabase).when(stockUsageReportRepository).getReadableDatabase();

        MatrixCursor cursor = new MatrixCursor(StockUsageReportRepository.COLUMNS);
        cursor.addRow(new Object[]{"STOCK_NAME", "STOCK_USAGE", "YEAR", "MONTH", "PROVIDER_ID", null, null});
        Mockito.doReturn(cursor).when(sqLiteDatabase).query(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());

        List<StockUsage> result = stockUsageReportRepository.getStockUsageByBaseID("1234");
        assertEquals(result.size(), 1);
    }

    @Test
    public void testGetStockUsageByBaseID() {
        Mockito.doReturn(sqLiteDatabase).when(stockUsageReportRepository).getReadableDatabase();

        MatrixCursor cursor = new MatrixCursor(StockUsageReportRepository.COLUMNS);
        cursor.addRow(new Object[]{"STOCK_NAME", "STOCK_USAGE", "YEAR", "MONTH", "PROVIDER_ID", null, null});
        Mockito.doReturn(cursor).when(sqLiteDatabase).query(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());

        List<StockUsage> result = stockUsageReportRepository.getStockUsageByName("3456");
        assertEquals(result.size(), 1);
    }

}