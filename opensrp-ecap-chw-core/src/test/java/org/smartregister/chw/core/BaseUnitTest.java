package org.smartregister.chw.core;


import android.os.Build;

import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.smartregister.chw.core.application.TestApplication;
import org.smartregister.chw.core.shadows.ContextShadow;
import org.smartregister.chw.core.shadows.CustomFontTextViewShadowHelper;
import org.smartregister.chw.core.shadows.FamilyLibraryShadowUtil;

/**
 * @author rkodev
 */
@RunWith(RobolectricTestRunner.class)
@Config(application = TestApplication.class, shadows = {ContextShadow.class, FamilyLibraryShadowUtil.class,
        CustomFontTextViewShadowHelper.class}, sdk = Build.VERSION_CODES.P)
public abstract class BaseUnitTest {

}
