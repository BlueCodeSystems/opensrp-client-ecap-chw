package org.smartregister.chw.core.presenter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.smartregister.chw.anc.domain.Visit;
import org.smartregister.chw.core.contract.CoreChildMedicalHistoryContract;
import org.smartregister.immunization.domain.ServiceRecord;
import org.smartregister.immunization.domain.Vaccine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CoreChildMedicalHistoryPresenterTest {

    @Mock
    private CoreChildMedicalHistoryContract.View view;


    private CoreChildMedicalHistoryPresenter profilePresenter;

    @Mock
    private CoreChildMedicalHistoryContract.Interactor interactor;

    private String memberID = "12345";

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        profilePresenter = new CoreChildMedicalHistoryPresenter(interactor, view, memberID);
    }

    @Test
    public void testGetView() {
        CoreChildMedicalHistoryContract.View testView = profilePresenter.getView();
        Assert.assertEquals(view, testView);
    }

    @Test
    public void testOnDataFetched() {
        List<Visit> visitsList = new ArrayList<>();
        Map<String, List<Vaccine>> vaccines = new HashMap<>();
        List<ServiceRecord> serviceRecords = new ArrayList<>();

        profilePresenter.onDataFetched(visitsList, vaccines, serviceRecords);

        Mockito.verify(view).onDataReceived(visitsList, vaccines, serviceRecords);
    }


}