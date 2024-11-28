package org.smartregister.chw.core.repository;

import net.sqlcipher.MatrixCursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.chw.core.BaseRobolectricTest;
import org.smartregister.chw.core.application.TestApplication;
import org.smartregister.chw.core.domain.MonthlyTally;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class MonthlyTalliesRepositoryTest extends BaseRobolectricTest {

    private MonthlyTalliesRepository monthlyTalliesRepository;

    @Mock
    private SQLiteDatabase database;

    private DailyTalliesRepository dailyTalliesRepository;

    @Before
    public void setUp() throws Exception {
        monthlyTalliesRepository = Mockito.spy(new MonthlyTalliesRepository());
        dailyTalliesRepository = Mockito.spy(TestApplication.getInstance().dailyTalliesRepository());
        ReflectionHelpers.setField(TestApplication.getInstance(), "dailyTalliesRepository", dailyTalliesRepository);

        Mockito.doReturn(database).when(monthlyTalliesRepository).getReadableDatabase();
        Mockito.doReturn(database).when(monthlyTalliesRepository).getWritableDatabase();
    }

    @Test
    public void createTableShouldMake9CreateAndIndexQueries() {
        SQLiteDatabase database = Mockito.mock(SQLiteDatabase.class);

        MonthlyTalliesRepository.createTable(database);
        ArgumentCaptor<String> queryStringsCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(database, Mockito.times(9)).execSQL(queryStringsCaptor.capture());

        List<String> queryStrings = queryStringsCaptor.getAllValues();
        int countOfCreateTable = 0;
        int countCreateUniqueIndex = 0;
        int countCreateIndex = 0;

        for (String queryString : queryStrings) {
            if (queryString.contains("CREATE TABLE")) {
                countOfCreateTable++;
            }

            if (queryString.contains("CREATE UNIQUE INDEX")) {
                countCreateUniqueIndex++;
            }

            if (queryString.contains("CREATE INDEX")) {
                countCreateIndex++;
            }
        }

        Assert.assertEquals(1, countOfCreateTable);
        Assert.assertEquals(1, countCreateUniqueIndex);
        Assert.assertEquals(7, countCreateIndex);
    }

    @Test
    public void findUneditedDraftMonthsShouldReturnValidMonthsWithinRange() throws ParseException {
        List<String> tallyMonths = new ArrayList<>();
        tallyMonths.add("2020-09");
        tallyMonths.add("2020-08");
        tallyMonths.add("2020-07");
        tallyMonths.add("2020-06");
        tallyMonths.add("2019-06");

        Mockito.doReturn(tallyMonths).when(dailyTalliesRepository).findAllDistinctMonths(Mockito.any(SimpleDateFormat.class)
                , Mockito.any(Date.class), Mockito.any(Date.class));

        MatrixCursor matrixCursor = new MatrixCursor(new String[]{"month"}, 0);
        matrixCursor.addRow(new String[]{"2020-09"});

        Mockito.doReturn(matrixCursor).when(database).query(Mockito.eq("monthly_tallies"), Mockito.any(String[].class)
                , Mockito.anyString(), Mockito.nullable(String[].class), Mockito.nullable(String.class)
                , Mockito.nullable(String.class), Mockito.nullable(String.class));

        List<Date> monthsWithoutDrafts = monthlyTalliesRepository.findUneditedDraftMonths(
                MonthlyTalliesRepository.DF_YYYYMM.parse("2020-01"),
                MonthlyTalliesRepository.DF_YYYYMM.parse("2020-12"));
        Assert.assertEquals(3, monthsWithoutDrafts.size());
        Assert.assertEquals("2020-08", MonthlyTalliesRepository.DF_YYYYMM.format(monthsWithoutDrafts.get(0)));
        Assert.assertEquals("2020-07", MonthlyTalliesRepository.DF_YYYYMM.format(monthsWithoutDrafts.get(1)));
        Assert.assertEquals("2020-06", MonthlyTalliesRepository.DF_YYYYMM.format(monthsWithoutDrafts.get(2)));
    }


    @Test
    public void findUneditedDraftMonthsShouldReturn0WhenDateRangeIsInadequate() {
        List<String> tallyMonths = new ArrayList<>();
        tallyMonths.add("2020-09");
        tallyMonths.add("2020-08");
        tallyMonths.add("2020-07");
        tallyMonths.add("2020-06");
        Mockito.doReturn(tallyMonths).when(dailyTalliesRepository).findAllDistinctMonths(Mockito.any(SimpleDateFormat.class)
                , Mockito.any(Date.class), Mockito.any(Date.class));

        MatrixCursor matrixCursor = new MatrixCursor(new String[]{"month"}, 0);
        matrixCursor.addRow(new String[]{"2020-09"});

        Mockito.doReturn(matrixCursor).when(database).query(Mockito.eq("monthly_tallies"), Mockito.any(String[].class)
                , Mockito.anyString(), Mockito.nullable(String[].class), Mockito.nullable(String.class)
                , Mockito.nullable(String.class), Mockito.nullable(String.class));

        List<Date> monthsWithoutDrafts = monthlyTalliesRepository.findUneditedDraftMonths(new Date(), new Date());
        Assert.assertEquals(0, monthsWithoutDrafts.size());
    }

    @Test
    public void findEditedDraftMonthsShouldReturnResultsWithinDateRange() {
        Date endDate = new Date();
        String submissionId = UUID.randomUUID().toString();
        Calendar calendarStartDate = Calendar.getInstance();
        calendarStartDate.add(Calendar.MONTH, -24);

        MatrixCursor matrixCursor = new MatrixCursor(new String[]{"month", "created_at", "submission_id"}, 0);

        Calendar tallyMonth = (Calendar) calendarStartDate.clone();
        tallyMonth.add(Calendar.MONTH, -12);
        for (int i = 0; i < 36; i++) {
            tallyMonth.add(Calendar.MONTH, 1);
            matrixCursor.addRow(new Object[]{MonthlyTalliesRepository.DF_YYYYMM.format(tallyMonth.getTime()), new Date().getTime(), submissionId});
        }

        Mockito.doReturn(matrixCursor).when(database).query(Mockito.eq("monthly_tallies"), Mockito.any(String[].class), Mockito.anyString(), Mockito.nullable(String[].class), Mockito.eq("month"), Mockito.nullable(String.class), Mockito.nullable(String.class));

        List<MonthlyTally> monthlyTallies = monthlyTalliesRepository.findEditedDraftMonths(calendarStartDate.getTime(), endDate);
        Assert.assertEquals(24, monthlyTallies.size());
    }
}
