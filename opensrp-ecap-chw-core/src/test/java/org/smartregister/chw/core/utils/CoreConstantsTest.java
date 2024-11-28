package org.smartregister.chw.core.utils;

import android.content.res.AssetManager;
import android.os.Build;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.chw.core.application.CoreChwApplication;
import org.smartregister.chw.core.application.TestApplication;

import java.util.Locale;

@RunWith(RobolectricTestRunner.class)
@Config(application = TestApplication.class, sdk = Build.VERSION_CODES.P)
public class CoreConstantsTest {

    @Mock
    private CoreConstants.JSON_FORM jsonForm;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getUnifiedReferralForms() {
        Assert.assertEquals(CoreConstants.JSON_FORM.getAncUnifiedReferralForm(), "referrals/anc_referral_form");
        Assert.assertEquals(CoreConstants.JSON_FORM.getChildUnifiedReferralForm(), "referrals/child_referral_form");
        Assert.assertEquals(CoreConstants.JSON_FORM.getPncUnifiedReferralForm(), "referrals/pnc_referral_form");
    }

    @Test
    public void getReferralForms() {
        Assert.assertEquals(CoreConstants.JSON_FORM.getChildReferralForm(), "child_referral_form");
        Assert.assertEquals(CoreConstants.JSON_FORM.getAncReferralForm(), "anc_referral_form");
        Assert.assertEquals(CoreConstants.JSON_FORM.getPncReferralForm(), "pnc_referral_form");
    }

    @Test
    public void getMenuType() {
        Assert.assertEquals(CoreConstants.MenuType.ChangeHead, "ChangeHead");
        Assert.assertEquals(CoreConstants.MenuType.ChangePrimaryCare, "ChangePrimaryCare");
    }

    @Test
    public void testGetFamilyKit() {
        Locale locale = CoreChwApplication.getInstance().getResources().getConfiguration().locale;
        AssetManager assetManager = CoreChwApplication.getInstance().getAssets();
        ReflectionHelpers.setField(jsonForm, "assetManager", assetManager);
        ReflectionHelpers.setField(jsonForm, "locale", locale);
        Assert.assertEquals(Utils.getLocalForm("family_kit", locale, assetManager), CoreConstants.JSON_FORM.getFamilyKit());
    }

    @Test
    public void testGetWashCheck() {
        Locale locale = CoreChwApplication.getInstance().getResources().getConfiguration().locale;
        AssetManager assetManager = CoreChwApplication.getInstance().getAssets();
        ReflectionHelpers.setField(jsonForm, "assetManager", assetManager);
        ReflectionHelpers.setField(jsonForm, "locale", locale);
        Assert.assertEquals(Utils.getLocalForm("wash_check", locale, assetManager), CoreConstants.JSON_FORM.getWashCheck());
    }

    @Test
    public void testGetFamilyRegister() {
        Locale locale = CoreChwApplication.getInstance().getResources().getConfiguration().locale;
        AssetManager assetManager = CoreChwApplication.getInstance().getAssets();
        ReflectionHelpers.setField(jsonForm, "assetManager", assetManager);
        ReflectionHelpers.setField(jsonForm, "locale", locale);
        Assert.assertEquals(Utils.getLocalForm("family_register", locale, assetManager), CoreConstants.JSON_FORM.getFamilyRegister());
    }

    @Test
    public void testGetFamilyMemberRegister() {
        Locale locale = CoreChwApplication.getInstance().getResources().getConfiguration().locale;
        AssetManager assetManager = CoreChwApplication.getInstance().getAssets();
        ReflectionHelpers.setField(jsonForm, "assetManager", assetManager);
        ReflectionHelpers.setField(jsonForm, "locale", locale);
        Assert.assertEquals(Utils.getLocalForm("family_member_register", locale, assetManager), CoreConstants.JSON_FORM.getFamilyMemberRegister());
    }

    @Test
    public void testGetChildRegister() {
        Locale locale = CoreChwApplication.getInstance().getResources().getConfiguration().locale;
        AssetManager assetManager = CoreChwApplication.getInstance().getAssets();
        ReflectionHelpers.setField(jsonForm, "assetManager", assetManager);
        ReflectionHelpers.setField(jsonForm, "locale", locale);
        Assert.assertEquals(Utils.getLocalForm("child_enrollment", locale, assetManager), CoreConstants.JSON_FORM.getChildRegister());
    }

    @Test
    public void testGetStockUsageForm() {
        Assert.assertEquals("stock_usage_report", CoreConstants.JSON_FORM.getStockUsageForm());
    }

    @Test
    public void testGetMalariaReferralForm() {
        Assert.assertEquals("referrals/malaria_referral_form", CoreConstants.JSON_FORM.getMalariaReferralForm());
    }

    @Test
    public void testGetHivReferralForm() {
        Assert.assertEquals("referrals/hiv_referral_form", CoreConstants.JSON_FORM.getHivReferralForm());
    }
}