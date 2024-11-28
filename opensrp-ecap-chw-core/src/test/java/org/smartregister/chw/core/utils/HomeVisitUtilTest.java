package org.smartregister.chw.core.utils;

import android.content.Context;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.smartregister.chw.core.rule.AncVisitAlertRule;
import org.smartregister.chw.core.rule.MalariaFollowUpRule;

import java.util.Date;

public class HomeVisitUtilTest {

    private Context context;
    @Mock
    private MalariaFollowUpRule malariaFollowUpRule;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        Date testDate = new DateTime().minusDays(10).toDate();
        malariaFollowUpRule = new MalariaFollowUpRule(testDate, null);
    }

    @Test
    public void canGetVisitSummaryFromVisitAlert() {
        String visitDate = "15-01-2020";
        String visitNotDoneDate = "05-01-2020";

        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MMM-dd");
        LocalDate dateCreated = formatter.parseDateTime("2020-jan-01").toLocalDate();

        formatter = DateTimeFormat.forPattern("dd-MM-yyyy");
        LocalDate lastVisitDate = formatter.parseDateTime(visitDate).toLocalDate();

        int noOfDaysDue = Days.daysBetween(lastVisitDate, new LocalDate()).getDays();

        AncVisitAlertRule ancVisitAlertRule = new AncVisitAlertRule(context, DateTimeFormat.forPattern("dd-MM-yyyy").print(dateCreated), visitDate, visitNotDoneDate, dateCreated);
        VisitSummary summary = HomeVisitUtil.getAncVisitStatus(ancVisitAlertRule, null);
        Assert.assertNotNull(summary);
        Assert.assertEquals(noOfDaysDue + " days", summary.getNoOfDaysDue());
    }

    @Test
    public void testGetButtonStatus() {
        Assert.assertEquals("", malariaFollowUpRule.getButtonStatus());
    }

    @Test
    public void testGetDatesDiff() {
        Assert.assertEquals(10, malariaFollowUpRule.getDatesDiff());
    }

    @Test
    public void testIsExpired() {
        Assert.assertEquals(false, malariaFollowUpRule.isExpired());
    }

    @Test
    public void testGetRuleKey() {
        Assert.assertEquals("malariaFollowUpRule", malariaFollowUpRule.getRuleKey());
    }
}