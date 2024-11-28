package org.smartregister.chw.core.rule;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.robolectric.RuntimeEnvironment;
import org.smartregister.chw.core.BaseRobolectricTest;

import java.util.Calendar;
import java.util.Date;

public class AncVisitAlertRuleTest extends BaseRobolectricTest {

    private AncVisitAlertRule ancVisitAlertRule;

    @Before
    public void setUp() {
        ancVisitAlertRule = new AncVisitAlertRule(RuntimeEnvironment.systemContext, "22-09-2019",
                "10-10-2019", null, LocalDate.now());
    }

    @Test
    public void getNumberOfDaysDue() {
        String numberOfDaysDue = ancVisitAlertRule.getNumberOfDaysDue();
        String justNumber = numberOfDaysDue.replace(" days", "");
        Assert.assertTrue(Integer.parseInt(justNumber) > 0);
    }


    @Test
    public void isVisitNotDone() {
        Assert.assertFalse(ancVisitAlertRule.isVisitNotDone());
    }

    @Test
    public void isExpiry() {
        Assert.assertFalse(ancVisitAlertRule.isExpiry());
    }


    @Test
    public void isVisitWithinThisMonth() {
        Assert.assertFalse(ancVisitAlertRule.isVisitWithinThisMonth());
    }

    @Test
    public void getRuleKey() {
        Assert.assertEquals(ancVisitAlertRule.getRuleKey(), "ancVisitAlertRule");
    }

    @Test
    public void getDueDate() {
        Date dueDate = ancVisitAlertRule.getDueDate();
        Assert.assertNotNull(dueDate);
        Calendar cal = Calendar.getInstance();
        cal.setTime(dueDate);

        int dueDateMonth = cal.get(Calendar.MONTH);

        cal.setTime(new Date());
        int todayMonth = cal.get(Calendar.MONTH);

        Assert.assertEquals(dueDateMonth, todayMonth);
    }

    @Test
    public void getButtonStatus() {
        Assert.assertEquals(ancVisitAlertRule.getButtonStatus(), "DUE");
    }
}