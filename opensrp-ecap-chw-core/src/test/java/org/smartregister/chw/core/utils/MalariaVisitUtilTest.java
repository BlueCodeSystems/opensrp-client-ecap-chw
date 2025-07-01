package org.smartregister.chw.core.utils;

import android.os.Build;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.smartregister.chw.core.application.CoreChwApplication;
import org.smartregister.chw.core.application.TestApplication;
import org.smartregister.chw.core.rule.MalariaFollowUpRule;

import java.util.Date;

@RunWith(RobolectricTestRunner.class)
@Config(application = TestApplication.class, sdk = Build.VERSION_CODES.P)
public class MalariaVisitUtilTest {
    private Date malariaTestDate = new DateTime().minusDays(14).toDate();
    private Date followUpDate = new DateTime().minusDays(7).toDate();
    private MalariaFollowUpRule malariaFollowUpRule;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetMalariaStatus() {
        CoreChwApplication.getInstance().getRulesEngineHelper().getMalariaRule(malariaFollowUpRule, CoreConstants.RULE_FILE.MALARIA_FOLLOW_UP_VISIT);
        malariaFollowUpRule = MalariaVisitUtil.getMalariaStatus(malariaTestDate, followUpDate);
        Assert.assertNotNull(malariaFollowUpRule);
    }
}
