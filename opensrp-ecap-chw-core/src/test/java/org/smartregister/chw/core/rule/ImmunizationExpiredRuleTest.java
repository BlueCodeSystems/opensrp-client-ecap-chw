package org.smartregister.chw.core.rule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ImmunizationExpiredRuleTest {

    private ImmunizationExpiredRule immunizationExpiredRule;

    @Before
    public void setUp() {
        immunizationExpiredRule = new ImmunizationExpiredRule("2019-12-08T03:00:00.000+03:00", "opv0");
    }

    @Test
    public void isOpv0Expired() {
        Assert.assertTrue(immunizationExpiredRule.isOpv0Expired(1));
    }

    @Test
    public void isMcv2Expired() {
        immunizationExpiredRule.setVaccineName("mcv2");
        Assert.assertTrue(immunizationExpiredRule.isMcv2Expired(2));
    }

    @Test
    public void isAllVaccineExpired() {
        Assert.assertTrue(immunizationExpiredRule.isAllVaccineExpired(3));
    }

    @Test
    public void getRuleKey() {
        Assert.assertEquals(immunizationExpiredRule.getRuleKey(), "immunizationExpireRule");
    }

    @Test
    public void getButtonStatus() {
        Assert.assertEquals(immunizationExpiredRule.getButtonStatus(), "false");
    }
}