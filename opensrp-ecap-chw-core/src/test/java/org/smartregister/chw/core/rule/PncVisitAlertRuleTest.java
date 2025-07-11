package org.smartregister.chw.core.rule;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.smartregister.chw.core.utils.CoreConstants;

import java.util.Date;

public class PncVisitAlertRuleTest {

    private Date lastVisitDate = new DateTime().minusDays(7).toDate();
    private Date deliveryDate = new DateTime().minusDays(30).toDate();
    private int dueDay = 1;
    private int overdueDay = 3;
    private int expiry = 5;

    private PncVisitAlertRule pncVisitAlertRule = new PncVisitAlertRule(lastVisitDate, deliveryDate);
    private PncVisitAlertRule pncVisitAlertRule1 = new PncVisitAlertRule(null, deliveryDate);

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        valid();
    }

    @Test
    public void testGetAndSetVisitID() {
        String visitId = "12345";
        pncVisitAlertRule.setVisitID(visitId);
        Assert.assertEquals("12345", pncVisitAlertRule.getVisitID());
    }

    @Test
    public void testIsValid() {
        Assert.assertFalse(pncVisitAlertRule.isValid(dueDay, overdueDay, expiry));
    }

    @Test
    public void testGetDueDate() {
        Assert.assertEquals(pncVisitAlertRule.getDueDate(), new DateTime(deliveryDate).plusDays(1).toDate());
    }

    @Test
    public void testGetOverDueDate() {
        Assert.assertEquals(pncVisitAlertRule.getOverDueDate(), new DateTime(deliveryDate).plusDays(3).toDate());

    }

    @Test
    public void testGetExpiryDate() {
        Assert.assertEquals(pncVisitAlertRule.getExpiryDate(), new DateTime(deliveryDate).plusDays(5).toDate());
    }

    @Test
    public void testGetCompletionDate() {
        Assert.assertNull(pncVisitAlertRule.getCompletionDate());

        pncVisitAlertRule.isValid(22, 23, 25);
        Assert.assertEquals(pncVisitAlertRule.getCompletionDate(), lastVisitDate);

    }

    @Test
    public void testGetRuleKey() {
        Assert.assertEquals("pncVisitAlertRule", pncVisitAlertRule.getRuleKey());
    }

    private void valid() {
        pncVisitAlertRule.isValid(dueDay, overdueDay, expiry);
    }

    @Test
    public void testGetButtonStatusWhenLastVisitIsNotNull() {
        Assert.assertEquals(CoreConstants.VISIT_STATE.EXPIRED, pncVisitAlertRule.getButtonStatus());
        dueDay = 22;
        overdueDay = 23;
        expiry = 25;
        valid();
        Assert.assertEquals(CoreConstants.VISIT_STATE.VISIT_DONE, pncVisitAlertRule.getButtonStatus());

        dueDay = 29;
        overdueDay = 31;
        expiry = 34;
        valid();
        Assert.assertEquals(CoreConstants.VISIT_STATE.DUE, pncVisitAlertRule.getButtonStatus());

        dueDay = 26;
        overdueDay = 29;
        expiry = 33;
        valid();
        Assert.assertEquals(CoreConstants.VISIT_STATE.OVERDUE, pncVisitAlertRule.getButtonStatus());
    }

    @Test
    public void testGetButtonStatusWhenLastVisitIsNull() {
        dueDay = 29;
        overdueDay = 31;
        expiry = 34;
        pncVisitAlertRule1.isValid(dueDay, overdueDay, expiry);
        Assert.assertEquals(CoreConstants.VISIT_STATE.DUE, pncVisitAlertRule1.getButtonStatus());

        dueDay = 26;
        overdueDay = 29;
        expiry = 33;
        pncVisitAlertRule1.isValid(dueDay, overdueDay, expiry);
        Assert.assertEquals(CoreConstants.VISIT_STATE.OVERDUE, pncVisitAlertRule1.getButtonStatus());
    }
}