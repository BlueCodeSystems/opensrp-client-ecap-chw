package org.smartregister.chw.core.utils;

import android.os.Build;

import org.jeasy.rules.api.Rules;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.core.application.CoreChwApplication;
import org.smartregister.chw.core.application.TestApplication;
import org.smartregister.chw.fp.domain.FpMemberObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@RunWith(RobolectricTestRunner.class)
@Config(application = TestApplication.class, sdk = Build.VERSION_CODES.P)
public class FpUtilTest {
    private static SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
    private FpUtil fpUtil = new FpUtil();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void testGetFpRules() {
        String fpMethod = "COC";
        Rules fpRule = CoreChwApplication.getInstance().getRulesEngineHelper().rules(CoreConstants.RULE_FILE.FP_COC_POP_REFILL);
        Rules rules1 = FpUtil.getFpRules(fpMethod);
        Assert.assertEquals(rules1, fpRule);

    }

    @Test
    public void testParseFpStartDate() throws Exception {
        String startDate = "2020-02-28";
        Date date = sdf.parse(startDate);
        Date date1 = FpUtil.parseFpStartDate(startDate);
        Assert.assertEquals(date, date1);
    }

    @Test
    public void testToMember() {
        FpMemberObject memberObject = new FpMemberObject();
        memberObject.setBaseEntityId("12334");
        memberObject.setFirstName("Mira");
        memberObject.setMiddleName("Moa");
        memberObject.setLastName("Pia");

        MemberObject res = FpUtil.toMember(memberObject);
        Assert.assertEquals(res.getBaseEntityId(), "12334");
        Assert.assertEquals(res.getFirstName(), "Mira");
        Assert.assertEquals(res.getMiddleName(), "Moa");
        Assert.assertEquals(res.getLastName(), "Pia");

    }
}
