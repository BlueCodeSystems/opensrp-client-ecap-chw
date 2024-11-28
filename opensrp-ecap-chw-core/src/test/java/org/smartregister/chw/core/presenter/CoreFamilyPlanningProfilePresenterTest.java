package org.smartregister.chw.core.presenter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.chw.core.contract.CoreFamilyPlanningMemberProfileContract;
import org.smartregister.chw.fp.domain.FpMemberObject;
import org.smartregister.family.domain.FamilyEventClient;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.util.FormUtils;

import java.util.UUID;

public class CoreFamilyPlanningProfilePresenterTest {

    @Mock
    private CoreFamilyPlanningMemberProfileContract.View view;

    private CoreFamilyPlanningProfilePresenter profilePresenter;

    @Mock
    private CoreFamilyPlanningMemberProfileContract.Interactor interactor;

    private FpMemberObject fpMemberObject = new FpMemberObject();

    @Mock
    private FormUtils formUtils;

    @Mock
    private AllSharedPreferences allSharedPreferences;

    @Mock
    private FamilyEventClient familyEventClient;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        fpMemberObject.setGender("Male");
        fpMemberObject.setBaseEntityId(UUID.randomUUID().toString());
        profilePresenter = Mockito.spy(new CoreFamilyPlanningProfilePresenter(view, interactor, fpMemberObject));
    }

    @Test
    public void testGetView() {
        CoreFamilyPlanningMemberProfileContract.View myView = profilePresenter.getView();
        Assert.assertEquals(view, myView);
    }

    @Test
    public void startFamilyPlanningReferralStartsReferralForm() {
        ReflectionHelpers.setField(profilePresenter, "formUtils", formUtils);
        profilePresenter.startFamilyPlanningReferral();
        Mockito.verify(profilePresenter.getView()).startFormActivity(Mockito.any(), Mockito.any());
    }

    @Test
    public void createReferralEventInitiatesInteractorCreateEvent() throws Exception {
        String jsonString = "{}";
        profilePresenter.createReferralEvent(allSharedPreferences, jsonString);
        Mockito.verify(interactor).createReferralEvent(allSharedPreferences, jsonString, fpMemberObject.getBaseEntityId());
    }

    @Test
    public void onRegistrationSavedRefreshesProfileData() {
        profilePresenter.onRegistrationSaved(false, true, familyEventClient);
        Mockito.verify(profilePresenter, Mockito.times(1)).refreshProfileData();
    }

}
