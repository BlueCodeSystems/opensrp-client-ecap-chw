package org.smartregister.chw.core.rule;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import java.util.Date;

public class MalariaFollowUpRuleTest {
    private String RULE_KEY;
    private Date testDate = new DateTime().minusDays(10).toDate();
    private Date latestFollowUpDate = new DateTime().minusDays(7).toDate();
    private MalariaFollowUpRule malariaFollowUpRule = new MalariaFollowUpRule(testDate, latestFollowUpDate);
    private String buttonStatus;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        RULE_KEY = "malariaFollowUpRule";
        buttonStatus = "buttonStatus";
    }

    @Test
    public void testGetDatesDiff() {
        Assert.assertEquals(10, malariaFollowUpRule.getDatesDiff());
    }

    @Test
    public void testSetAndGetButtonStatus() {
        malariaFollowUpRule.setButtonStatus(buttonStatus);
        Assert.assertEquals(buttonStatus, malariaFollowUpRule.getButtonStatus());
    }

    @Test
    public void testIsExpired() {
        Assert.assertTrue(malariaFollowUpRule.isExpired());
    }

    @Test
    public void testGetRuleKey() {
        Assert.assertEquals(RULE_KEY, malariaFollowUpRule.getRuleKey());
    }

}