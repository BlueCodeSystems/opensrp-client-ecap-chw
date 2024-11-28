package org.smartregister.chw.core.interactor;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.smartregister.chw.anc.domain.Visit;
import org.smartregister.chw.core.BaseUnitTest;
import org.smartregister.chw.core.application.TestApplication;
import org.smartregister.chw.core.shadows.ContextShadow;
import org.smartregister.chw.core.shadows.VisitDaoShadowHelper;

import java.util.List;

@RunWith(RobolectricTestRunner.class)
@Config(application = TestApplication.class, shadows = {ContextShadow.class, VisitDaoShadowHelper.class})
public class CoreChildMedicalHistoryActivityInteractorTest extends BaseUnitTest {

    private CoreChildMedicalHistoryActivityInteractor interactor;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        interactor = Mockito.mock(CoreChildMedicalHistoryActivityInteractor.class, Mockito.CALLS_REAL_METHODS);
    }

    @Test
    public void getVisitsReturnsCorrectList() {
        List<Visit> visits = interactor.getVisits("memberID");
        Assert.assertEquals(1, visits.size());
        Assert.assertEquals(1, visits.get(0).getVisitDetails().size());
        Assert.assertEquals("test-visit-id", visits.get(0).getVisitId());
        Assert.assertEquals("Exclusive Breastfeeding", visits.get(0).getVisitType());
        Assert.assertEquals("test_details", visits.get(0).getVisitDetails().get("test_visit_key").get(0).getDetails());
    }

}
