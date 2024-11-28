package org.smartregister.chw.core.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.core.util.Consumer;

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
import org.powermock.reflect.Whitebox;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.chw.anc.domain.GroupedVisit;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.anc.domain.VisitDetail;
import org.smartregister.chw.core.application.TestApplication;
import org.smartregister.chw.core.domain.MedicalHistory;
import org.smartregister.chw.core.shadows.ContextShadow;
import org.smartregister.chw.core.shadows.CustomFontTextViewShadowHelper;
import org.smartregister.chw.core.shadows.FamilyLibraryShadowUtil;
import org.smartregister.chw.core.shadows.PncLibraryShadowHelper;
import org.smartregister.chw.core.shadows.PncMedicalHistoryViewBuilderShadow;
import org.smartregister.chw.pnc.PncLibrary;
import org.smartregister.chw.pnc.repository.ProfileRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author rkodev
 */
@RunWith(RobolectricTestRunner.class)
@Config(application = TestApplication.class, shadows = {ContextShadow.class, FamilyLibraryShadowUtil.class, CustomFontTextViewShadowHelper.class, PncMedicalHistoryViewBuilderShadow.class, PncLibraryShadowHelper.class}, sdk = Build.VERSION_CODES.P)
public class DefaultPncMedicalHistoryActivityFlvTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    private DefaultPncMedicalHistoryActivityFlv pncMedicalHistoryActivityFlv;

    private Activity activity;
    private ActivityController<Activity> activityController;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        pncMedicalHistoryActivityFlv = Mockito.mock(DefaultPncMedicalHistoryActivityFlv.class, Mockito.CALLS_REAL_METHODS);
        activityController = Robolectric.buildActivity(Activity.class).create().start();
        activity = activityController.get();
    }


    @Test
    public void testToCSV() throws Exception {
        List<String> strings = new ArrayList<>();
        strings.add("A");
        strings.add("B");
        strings.add("C");

        String result = Whitebox.invokeMethod(pncMedicalHistoryActivityFlv, "toCSV", strings);
        Assert.assertEquals("A,B,C", result);
    }

    private <T> T initialize(T t, Consumer<T> consumer){
        consumer.accept(t);
        return t;
    }

    @Test
    public void testGetTextReturnsCSVValue() throws Exception {
        List<VisitDetail> strings = new ArrayList<>();
        strings.add(initialize(new VisitDetail(), visitDetail -> visitDetail.setHumanReadable("A")));
        strings.add(initialize(new VisitDetail(), visitDetail -> visitDetail.setHumanReadable("B")));
        strings.add(initialize(new VisitDetail(), visitDetail -> visitDetail.setHumanReadable("C")));

        String result = Whitebox.invokeMethod(pncMedicalHistoryActivityFlv, "getText", strings);
        Assert.assertEquals("A,B,C", result);
    }

    @Test
    public void testProcessHealthFacilityVisit() throws Exception {
        List<MedicalHistory> medicalHistories = new ArrayList<>();
        Whitebox.setInternalState(pncMedicalHistoryActivityFlv,"medicalHistories", medicalHistories);
        Whitebox.setInternalState(pncMedicalHistoryActivityFlv,"context", RuntimeEnvironment.application);

        Map<String, Map<String, String>> healthFacilityVisit = new HashMap<>();

        Map<String,String> baby1 = new HashMap<>();
        baby1.put("pnc_hf_visit_date","1");
        baby1.put("baby_weight","1234");
        baby1.put("baby_temp","36");
        healthFacilityVisit.put("baby1",baby1);

        Map<String,String> baby2 = new HashMap<>();
        baby1.put("pnc_hf_visit_date","2");
        baby2.put("baby_weight","1234");
        baby2.put("baby_temp","36");
        healthFacilityVisit.put("baby2",baby2);

        Whitebox.invokeMethod(pncMedicalHistoryActivityFlv, "processHealthFacilityVisit", healthFacilityVisit);

        Assert.assertEquals(1, medicalHistories.size());

        MedicalHistory medicalHistory = medicalHistories.get(0);
        Assert.assertEquals(7, medicalHistory.getText().size());
    }

    @Test
    public void testBindViews() {
        View view = pncMedicalHistoryActivityFlv.bindViews(activity);
        Assert.assertTrue(view instanceof ViewGroup);
    }

    @Test
    public void testProcessViewData() {
        List<GroupedVisit> groupedVisits = getSampleGroups();

        Context context = RuntimeEnvironment.application;
        MemberObject memberObject = new MemberObject();
        memberObject.setBaseEntityId("23456");
        memberObject.setFirstName("fname");
        memberObject.setMiddleName("mname");
        memberObject.setLastName("mname");

        LinearLayout parentView = Mockito.mock(LinearLayout.class);
        LayoutInflater inflater = LayoutInflater.from(context);


        PncLibrary instance = Mockito.mock(PncLibrary.class);
        ProfileRepository repository = Mockito.mock(ProfileRepository.class);
        Mockito.doReturn(repository).when(instance).profileRepository();
        Mockito.doReturn(new Date().getTime()).when(repository).getLastVisit(Mockito.anyString());

        PncLibraryShadowHelper.setInstance(instance);


        ReflectionHelpers.setField(pncMedicalHistoryActivityFlv, "parentView", parentView);
        ReflectionHelpers.setField(pncMedicalHistoryActivityFlv, "context", context);
        ReflectionHelpers.setField(pncMedicalHistoryActivityFlv, "inflater", inflater);
        pncMedicalHistoryActivityFlv.processViewData(groupedVisits, context, memberObject);
        Mockito.verify(parentView, Mockito.times(5)).addView(Mockito.any(View.class));
    }

    private List<GroupedVisit> getSampleGroups() {
        List<GroupedVisit> groupedVisits = new ArrayList<>();

        groupedVisits.add(new GroupedVisit("12345", "Mum", new ArrayList<>()));
        groupedVisits.add(new GroupedVisit("23456", "Baby", new ArrayList<>()));
        return groupedVisits;
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
