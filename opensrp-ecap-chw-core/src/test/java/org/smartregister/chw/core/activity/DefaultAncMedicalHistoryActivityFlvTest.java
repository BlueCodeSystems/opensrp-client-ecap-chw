package org.smartregister.chw.core.activity;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.chw.anc.domain.Visit;
import org.smartregister.chw.anc.domain.VisitDetail;
import org.smartregister.chw.core.BaseUnitTest;
import org.smartregister.chw.core.utils.CoreConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * @author rkodev
 */
public class DefaultAncMedicalHistoryActivityFlvTest extends BaseUnitTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    private DefaultAncMedicalHistoryActivityFlv ancMedicalHistoryActivityFlv;

    private Activity activity;
    private ActivityController<Activity> activityController;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        ancMedicalHistoryActivityFlv = Mockito.mock(DefaultAncMedicalHistoryActivityFlv.class, Mockito.CALLS_REAL_METHODS);
        activityController = Robolectric.buildActivity(Activity.class).create().start();
        activity = activityController.get();
        //activity.setContentView(R.layout.activity_base);
    }

    @Test
    public void testBindViewsInitializesAllView() {
        View view = ancMedicalHistoryActivityFlv.bindViews(activity);

        Assert.assertNotNull(ReflectionHelpers.getField(ancMedicalHistoryActivityFlv, "linearLayoutLastVisit"));
        Assert.assertNotNull(ReflectionHelpers.getField(ancMedicalHistoryActivityFlv, "linearLayoutAncCard"));
        Assert.assertNotNull(ReflectionHelpers.getField(ancMedicalHistoryActivityFlv, "linearLayoutHealthFacilityVisit"));
        Assert.assertNotNull(ReflectionHelpers.getField(ancMedicalHistoryActivityFlv, "linearLayoutHealthFacilityVisitDetails"));
        Assert.assertNotNull(ReflectionHelpers.getField(ancMedicalHistoryActivityFlv, "linearLayoutTTImmunization"));
        Assert.assertNotNull(ReflectionHelpers.getField(ancMedicalHistoryActivityFlv, "linearLayoutTTImmunizationDetails"));
        Assert.assertNotNull(ReflectionHelpers.getField(ancMedicalHistoryActivityFlv, "customFontTextViewLastVisit"));
        Assert.assertNotNull(ReflectionHelpers.getField(ancMedicalHistoryActivityFlv, "customFontTextViewAncCard"));

        Assert.assertNotNull(view);
    }

    @Test
    public void testProcessViewData() {
        Context context = RuntimeEnvironment.application;
        List<Visit> visits = createSampleVisits();

        // prepare views
        ancMedicalHistoryActivityFlv.bindViews(activity);
        LinearLayout linearLayoutLastVisit = Mockito.mock(LinearLayout.class);
        ReflectionHelpers.setField(ancMedicalHistoryActivityFlv, "linearLayoutLastVisit", linearLayoutLastVisit);

        // test
        ancMedicalHistoryActivityFlv.processViewData(visits, context);
        Mockito.verify(linearLayoutLastVisit).setVisibility(View.VISIBLE); // anc details are update
    }

    @Test
    public void testGetTexts() {
        Context context = RuntimeEnvironment.application;
        List<VisitDetail> visitDetails = new ArrayList<>();

        VisitDetail visitDetail = new VisitDetail();
        visitDetail.setHumanReadable("Mike");
        visitDetails.add(visitDetail);

        VisitDetail visitDetail1 = new VisitDetail();
        visitDetail1.setHumanReadable("Leno");
        visitDetails.add(visitDetail1);

        String content = ancMedicalHistoryActivityFlv.getTexts(context, visitDetails);
        Assert.assertEquals(content, "Mike, Leno");
    }

    private List<Visit> createSampleVisits() {
        List<Visit> visits = new ArrayList<>();

        // ecd visit
        Visit visit = new Visit();
        visit.setVisitType(CoreConstants.EventType.ECD);

        visits.add(visit);
        return visits;
    }

    @After
    public void tearDown() {
        try {
            activityController.pause().stop().destroy();
            activity.finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
