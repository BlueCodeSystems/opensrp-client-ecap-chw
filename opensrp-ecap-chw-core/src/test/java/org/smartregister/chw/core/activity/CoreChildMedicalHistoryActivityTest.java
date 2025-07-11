package org.smartregister.chw.core.activity;

import android.view.View;
import android.widget.LinearLayout;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.anc.domain.Visit;
import org.smartregister.chw.core.presenter.CoreChildMedicalHistoryPresenter;
import org.smartregister.immunization.domain.ServiceRecord;
import org.smartregister.immunization.domain.Vaccine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author rkodev
 */
public class CoreChildMedicalHistoryActivityTest {

    private CoreChildMedicalHistoryActivity coreChildMedicalHistoryActivity;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        coreChildMedicalHistoryActivity = Mockito.mock(CoreChildMedicalHistoryActivity.class, Mockito.CALLS_REAL_METHODS);
    }

    @Test
    public void testStartMeInitiatesActivity() {
        MemberObject memberObject = Mockito.mock(MemberObject.class);
        CoreChildMedicalHistoryActivity.startMe(coreChildMedicalHistoryActivity, memberObject);
        Mockito.verify(coreChildMedicalHistoryActivity).startActivity(Mockito.any());
    }

    @Test
    public void testInitializePresenter() {
        MemberObject memberObject = Mockito.mock(MemberObject.class);
        ReflectionHelpers.setField(coreChildMedicalHistoryActivity, "memberObject", memberObject);
        coreChildMedicalHistoryActivity.initializePresenter();
        Assert.assertTrue(ReflectionHelpers.getField(coreChildMedicalHistoryActivity, "presenter") instanceof CoreChildMedicalHistoryPresenter);
    }

    @Test
    public void testOnDataReceivedUpdatesViews() {
        CoreChildMedicalHistoryActivity.Flavor flavor = Mockito.mock(CoreChildMedicalHistoryActivity.Flavor.class);
        coreChildMedicalHistoryActivity = Mockito.spy(coreChildMedicalHistoryActivity);
        Mockito.doNothing().when(coreChildMedicalHistoryActivity).displayLoadingState(Mockito.anyBoolean());

        LinearLayout linearLayout = Mockito.mock(LinearLayout.class);
        ReflectionHelpers.setField(coreChildMedicalHistoryActivity, "linearLayout", linearLayout);
        Mockito.doReturn(flavor).when(coreChildMedicalHistoryActivity).getFlavor();

        Mockito.doReturn(Mockito.mock(View.class)).when(flavor).bindViews(coreChildMedicalHistoryActivity);

        List<Visit> visits = new ArrayList<>();
        Map<String, List<Vaccine>> vaccines = new HashMap<>();
        List<ServiceRecord> serviceRecords = new ArrayList<>();

        coreChildMedicalHistoryActivity.onDataReceived(visits, vaccines, serviceRecords);

        Mockito.verify(flavor).processViewData(visits, vaccines, serviceRecords, coreChildMedicalHistoryActivity);
        Mockito.verify(linearLayout).addView(Mockito.any(View.class), Mockito.eq(0));
    }
}
