package org.smartregister.chw.core.rule;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.chw.core.BaseRobolectricTest;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.fp.util.FamilyPlanningConstants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FpAlertRuleTest extends BaseRobolectricTest {

    private FpAlertRule fpAlertRule;
    private DateTime lastVisitDate;
    private DateTime dueDate;
    private DateTime expiryDate;
    private DateTime overDueDate;

    @Before
    public void setUp() throws ParseException {
        Date fpDate = new SimpleDateFormat("dd-MM-yyyy").parse("17-11-2020");
        Date lastVisit = new SimpleDateFormat("dd-MM-yyyy").parse("17-10-2020");
        fpAlertRule = new FpAlertRule(fpDate, lastVisit, 10, FamilyPlanningConstants.DBConstants.FP_COC);
        ReflectionHelpers.setField(fpAlertRule, "visitID", "test-visit-id");
    }


    @Test
    public void canGetVisitId() {
        Assert.assertEquals("test-visit-id", fpAlertRule.getVisitID());
    }


    @Test
    public void canGetIfFemaleSterilizationFollowUpOneValid() {
        ReflectionHelpers.setField(fpAlertRule, "fpDifference", 5);
        Assert.assertFalse(fpAlertRule.isFemaleSterilizationFollowUpOneValid(10, 15, 20));
        Assert.assertTrue(fpAlertRule.isFemaleSterilizationFollowUpOneValid(2, 6, 10));
    }

    @Test
    public void buttonStatusDoneIfVisitBeforeExpiry() {
        lastVisitDate = LocalDate.now().toDateTime(LocalTime.MIDNIGHT);
        dueDate = LocalDate.now().minusDays(5).toDateTime(LocalTime.MIDNIGHT);
        expiryDate = LocalDate.now().plusDays(10).toDateTime(LocalTime.MIDNIGHT);

        ReflectionHelpers.setField(fpAlertRule, "dueDate", dueDate);
        ReflectionHelpers.setField(fpAlertRule, "expiryDate", expiryDate);
        ReflectionHelpers.setField(fpAlertRule, "lastVisitDate", lastVisitDate);
        Assert.assertEquals(CoreConstants.VISIT_STATE.VISIT_DONE, fpAlertRule.getButtonStatus());
    }

    @Test
    public void buttonStatusDueIfVisitBeforeOverdue() {
        lastVisitDate = LocalDate.now().minusDays(10).toDateTime(LocalTime.MIDNIGHT);
        dueDate = LocalDate.now().minusDays(5).toDateTime(LocalTime.MIDNIGHT);
        overDueDate = LocalDate.now().plusDays(10).toDateTime(LocalTime.MIDNIGHT);

        ReflectionHelpers.setField(fpAlertRule, "dueDate", dueDate);
        ReflectionHelpers.setField(fpAlertRule, "overDueDate", overDueDate);
        ReflectionHelpers.setField(fpAlertRule, "lastVisitDate", lastVisitDate);
        Assert.assertEquals(CoreConstants.VISIT_STATE.DUE, fpAlertRule.getButtonStatus());
    }


    @Test
    public void buttonStatusOverDueIfVisitAfterOverdue() {
        lastVisitDate = LocalDate.now().minusDays(10).toDateTime(LocalTime.MIDNIGHT);
        dueDate = LocalDate.now().minusDays(5).toDateTime(LocalTime.MIDNIGHT);
        overDueDate = LocalDate.now().minusDays(1).toDateTime(LocalTime.MIDNIGHT);
        expiryDate = LocalDate.now().plusDays(10).toDateTime(LocalTime.MIDNIGHT);

        ReflectionHelpers.setField(fpAlertRule, "dueDate", dueDate);
        ReflectionHelpers.setField(fpAlertRule, "expiryDate", expiryDate);
        ReflectionHelpers.setField(fpAlertRule, "overDueDate", overDueDate);
        ReflectionHelpers.setField(fpAlertRule, "lastVisitDate", lastVisitDate);
        Assert.assertEquals(CoreConstants.VISIT_STATE.OVERDUE, fpAlertRule.getButtonStatus());
    }


    @Test
    public void buttonStatusNotDueIfDateBeforeDueAndExpiry() {
        lastVisitDate = LocalDate.now().toDateTime(LocalTime.MIDNIGHT);
        dueDate = LocalDate.now().plusDays(10).toDateTime(LocalTime.MIDNIGHT);
        expiryDate = LocalDate.now().plusDays(20).toDateTime(LocalTime.MIDNIGHT);

        ReflectionHelpers.setField(fpAlertRule, "dueDate", dueDate);
        ReflectionHelpers.setField(fpAlertRule, "expiryDate", expiryDate);
        ReflectionHelpers.setField(fpAlertRule, "lastVisitDate", lastVisitDate);
        Assert.assertEquals(CoreConstants.VISIT_STATE.NOT_DUE_YET, fpAlertRule.getButtonStatus());
    }


    @Test
    public void canGetDueDate() {
        dueDate = LocalDate.now().plusDays(10).toDateTime(LocalTime.MIDNIGHT);
        ReflectionHelpers.setField(fpAlertRule, "dueDate", dueDate);
        Assert.assertEquals(dueDate.toDate(), fpAlertRule.getDueDate());
    }

    @Test
    public void canGetOverdueDate() {
        overDueDate = LocalDate.now().minusDays(1).toDateTime(LocalTime.MIDNIGHT);
        ReflectionHelpers.setField(fpAlertRule, "overDueDate", overDueDate);
        Assert.assertEquals(overDueDate.toDate(), fpAlertRule.getOverDueDate());
    }

    @Test
    public void canGetExpiryDate() {
        expiryDate = LocalDate.now().plusDays(10).toDateTime(LocalTime.MIDNIGHT);
        ReflectionHelpers.setField(fpAlertRule, "expiryDate", expiryDate);
        Assert.assertEquals(expiryDate.toDate(), fpAlertRule.getExpiryDate());
    }


}