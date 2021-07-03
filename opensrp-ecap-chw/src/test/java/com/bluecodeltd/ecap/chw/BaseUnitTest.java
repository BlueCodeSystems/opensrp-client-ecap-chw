package com.bluecodeltd.ecap.chw;

import org.junit.runner.RunWith;
import org.koin.test.AutoCloseKoinTest;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import com.bluecodeltd.ecap.chw.application.TestChwApplication;
import com.bluecodeltd.ecap.chw.shadows.BaseJobShadow;
import com.bluecodeltd.ecap.chw.shadows.ContextShadow;
import com.bluecodeltd.ecap.chw.shadows.CustomFontTextViewShadow;
import com.bluecodeltd.ecap.chw.shadows.KujakuMapViewShadow;

/**
 * Created by keyman on 11/03/2019.
 */

@RunWith(RobolectricTestRunner.class)
@Config(application = TestChwApplication.class, shadows = {ContextShadow.class,
        BaseJobShadow.class, CustomFontTextViewShadow.class,
        KujakuMapViewShadow.class})
public abstract class BaseUnitTest extends AutoCloseKoinTest {

}
