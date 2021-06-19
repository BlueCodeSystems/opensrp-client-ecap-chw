package ecap.smartregister.chw;

import org.junit.runner.RunWith;
import org.koin.test.AutoCloseKoinTest;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import ecap.smartregister.chw.application.TestChwApplication;
import ecap.smartregister.chw.shadows.BaseJobShadow;
import ecap.smartregister.chw.shadows.ContextShadow;
import ecap.smartregister.chw.shadows.CustomFontTextViewShadow;
import ecap.smartregister.chw.shadows.KujakuMapViewShadow;

/**
 * Created by keyman on 11/03/2019.
 */

@RunWith(RobolectricTestRunner.class)
@Config(application = TestChwApplication.class, shadows = {ContextShadow.class,
        BaseJobShadow.class, CustomFontTextViewShadow.class,
        KujakuMapViewShadow.class})
public abstract class BaseUnitTest extends AutoCloseKoinTest {

}
