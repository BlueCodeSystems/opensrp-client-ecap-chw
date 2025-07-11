package org.smartregister.chw.core.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.chw.anc.domain.Visit;
import org.smartregister.chw.anc.domain.VisitDetail;
import org.smartregister.chw.core.application.TestApplication;
import org.smartregister.chw.core.shadows.AncLibraryShadowUtil;
import org.smartregister.chw.core.shadows.ContextShadow;
import org.smartregister.chw.core.shadows.CustomFontTextViewShadowHelper;
import org.smartregister.chw.core.shadows.FamilyLibraryShadowUtil;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.immunization.domain.ServiceRecord;
import org.smartregister.immunization.domain.Vaccine;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author rkodev
 */
@RunWith(RobolectricTestRunner.class)
@Config(application = TestApplication.class, shadows = {ContextShadow.class, FamilyLibraryShadowUtil.class, CustomFontTextViewShadowHelper.class, AncLibraryShadowUtil.class}, sdk = Build.VERSION_CODES.P)
public class DefaultChildMedicalHistoryActivityFlvTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    private DefaultChildMedicalHistoryActivityFlv childMedicalHistoryActivityFlv;

    private Activity activity;
    private ActivityController<Activity> activityController;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        childMedicalHistoryActivityFlv = Mockito.mock(DefaultChildMedicalHistoryActivityFlv.class, Mockito.CALLS_REAL_METHODS);
        activityController = Robolectric.buildActivity(Activity.class).create().start();
        activity = activityController.get();
    }

    @Test
    public void testBindViewsReturnsAViewGroup() {
        View view = childMedicalHistoryActivityFlv.bindViews(activity);
        Assert.assertTrue(view instanceof ViewGroup);
    }

    private List<Visit> getSampleVisits() {
        List<Visit> visits = new ArrayList<>();
        Visit visit = new Visit();
        visits.add(visit);
        return visits;
    }

    @Test
    public void testProcessViewData() {
        List<Visit> visits = getSampleVisits();
        Map<String, List<Vaccine>> vaccineMap = getSampleVaccineMap();
        List<ServiceRecord> serviceTypeListMap = getSampleServiceRecords();
        Context context = RuntimeEnvironment.application;
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        ReflectionHelpers.setField(childMedicalHistoryActivityFlv, "sdf", sdf);

        ViewGroup rootView = (ViewGroup) childMedicalHistoryActivityFlv.bindViews(activity);

        childMedicalHistoryActivityFlv.processViewData(visits, vaccineMap, serviceTypeListMap, context);

        // verify object has kids
        Assert.assertNotNull(rootView);
    }

    @Test
    public void testGetValueReturnsObjectValue() {
        Visit visit = new Visit();
        Map<String, List<VisitDetail>> details = new HashMap<>();
        VisitDetail visitDetail = new VisitDetail();
        visitDetail.setHumanReadable("yes");
        List<VisitDetail> visitDetails = new ArrayList<>();
        visitDetails.add(visitDetail);
        details.put("child_vaccine_card", visitDetails);
        visit.setVisitDetails(details);
        List<Visit> visits = new ArrayList<>();
        visits.add(visit);


        String value = ReflectionHelpers.callInstanceMethod(childMedicalHistoryActivityFlv, "getValue",
                ReflectionHelpers.ClassParameter.from(List.class, visits));

        Assert.assertEquals(value, "yes");
    }

    private List<Visit> getSampleVisits(String key, String value) {
        Visit visit = new Visit();
        visit.setDate(new Date());
        Map<String, List<VisitDetail>> details = new HashMap<>();
        VisitDetail visitDetail = new VisitDetail();
        visitDetail.setHumanReadable(key);
        List<VisitDetail> visitDetails = new ArrayList<>();
        visitDetails.add(visitDetail);
        details.put(value, visitDetails);
        visit.setVisitDetails(details);
        List<Visit> visits = new ArrayList<>();
        visits.add(visit);

        return visits;
    }

    @Test
    public void testEvaluateGrowthAndNutrition() {
        Context context = RuntimeEnvironment.application;
        ReflectionHelpers.setField(childMedicalHistoryActivityFlv, "context", context);
        LinearLayout parentView = Mockito.mock(LinearLayout.class);
        ReflectionHelpers.setField(childMedicalHistoryActivityFlv, "parentView", parentView);
        LayoutInflater inflater = activity.getLayoutInflater();
        ReflectionHelpers.setField(childMedicalHistoryActivityFlv, "inflater", inflater);
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        ReflectionHelpers.setField(childMedicalHistoryActivityFlv, "sdf", sdf);

        Map<String, List<Visit>> visitMap = new HashMap<>();
        visitMap.put(CoreConstants.EventType.EXCLUSIVE_BREASTFEEDING, getSampleVisits("date", "yes"));
        visitMap.put(CoreConstants.EventType.VITAMIN_A, getSampleVisits("VitaminA 1", "done"));
        visitMap.put(CoreConstants.EventType.DEWORMING, getSampleVisits("Deworming 1", "done"));
        visitMap.put(CoreConstants.EventType.MINIMUM_DIETARY_DIVERSITY, getSampleVisits("chk_no_animal_products", "chk_no_animal_products"));
        visitMap.put(CoreConstants.EventType.MUAC, getSampleVisits("chk_red", "chk_red"));
        ReflectionHelpers.setField(childMedicalHistoryActivityFlv, "visitMap", visitMap);

        ReflectionHelpers.callInstanceMethod(childMedicalHistoryActivityFlv, "evaluateGrowthAndNutrition");
        Mockito.verify(parentView).addView(Mockito.any());
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

    private Map<String, List<Vaccine>> getSampleVaccineMap() {
        Map<String, List<Vaccine>> vaccines = new HashMap<>();
        Vaccine vaccine = new Vaccine();
        vaccine.setDate(new Date());
        vaccine.setName("PENTA 1");
        List<Vaccine> list = new ArrayList<>(1);
        list.add(vaccine);

        vaccines.put("6 weeks", list);
        return vaccines;
    }

    private List<ServiceRecord> getSampleServiceRecords() {
        return new ArrayList<>();
    }
}
