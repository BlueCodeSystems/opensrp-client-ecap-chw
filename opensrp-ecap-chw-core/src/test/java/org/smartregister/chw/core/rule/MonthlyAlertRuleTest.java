package org.smartregister.chw.core.rule;

import android.content.Context;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.powermock.reflect.Whitebox;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.chw.core.BaseRobolectricTest;
import org.smartregister.chw.core.implementation.MonthlyAlertRuleImpl;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by Qazi Abubakar
 */

public class MonthlyAlertRuleTest extends BaseRobolectricTest {

    private MonthlyAlertRule monthlyAlertRule;

    @Before
    public void setUp() {
        Context context = RuntimeEnvironment.application;
        monthlyAlertRule = new MonthlyAlertRuleImpl(context, new Date().getTime(), new Date().getTime());
    }

    @Test
    public void lastDueDateIs1stIfLastVisitEarlierThanCreated() throws Exception {
        Date firstDayOfThisMonth = LocalDate.now().withDayOfMonth(1).toDate();
        LocalDate dateCreated = LocalDate.fromDateFields(firstDayOfThisMonth).plusWeeks(2);
        LocalDate lastVisitDate = LocalDate.fromDateFields(firstDayOfThisMonth).plusWeeks(1);

        ReflectionHelpers.setField(monthlyAlertRule, "dateCreated", dateCreated);
        ReflectionHelpers.setField(monthlyAlertRule, "lastVisitDate", lastVisitDate);

        Assert.assertEquals(firstDayOfThisMonth, Whitebox.invokeMethod(monthlyAlertRule, "getLastDueDate"));
    }

    @Test
    public void testGetLastDayOfMonth() {
        DateTime first = new DateTime(new Date()).withDayOfMonth(1);
        Date lastDate = first.plusMonths(1).minusDays(1).toDate();
        Assert.assertNotEquals(first.toDate(), monthlyAlertRule.getLastDayOfMonth(new Date()));
        Assert.assertEquals(lastDate.toString(), monthlyAlertRule.getLastDayOfMonth(new Date()).toString());
    }

    @Test
    public void testGetFirstDayOfMonth() {
        DateTime first = new DateTime(new Date()).withDayOfMonth(1);
        Date lastDate = first.plusMonths(1).minusDays(1).toDate();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Assert.assertEquals(sdf.format(first.toDate()), sdf.format(monthlyAlertRule.getFirstDayOfMonth(new Date())));
        Assert.assertNotEquals(sdf.format(lastDate), sdf.format(monthlyAlertRule.getFirstDayOfMonth(new Date())));
    }
}
