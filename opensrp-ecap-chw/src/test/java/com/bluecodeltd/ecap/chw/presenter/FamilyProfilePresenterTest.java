package com.bluecodeltd.ecap.chw.presenter;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.util.ReflectionHelpers;
import com.bluecodeltd.ecap.chw.activity.FamilyProfileActivity;
import com.bluecodeltd.ecap.chw.interactor.FamilyProfileInteractor;

public class FamilyProfilePresenterTest {

    @Mock
    private FamilyProfileActivity activity;

    @Mock
    private FamilyProfileInteractor interactor;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testNotifyHasPhoneUpdatesUI() {

        // when a view is attached argument is called
        FamilyProfilePresenter presenter = new FamilyProfilePresenter(activity, null, null, null, null, null);
        presenter.notifyHasPhone(Mockito.eq(false));
        Mockito.verify(activity).updateHasPhone(Mockito.eq(false));
    }


    @Test
    public void testVerifyHasPhone() {
        String familyID = "123457";
        FamilyProfilePresenter presenter = new FamilyProfilePresenter(activity, null, familyID, null, null, null);
        ReflectionHelpers.setField(presenter, "interactor", interactor);
        presenter.verifyHasPhone();

        Mockito.verify(interactor).verifyHasPhone(familyID, presenter);
    }


}
