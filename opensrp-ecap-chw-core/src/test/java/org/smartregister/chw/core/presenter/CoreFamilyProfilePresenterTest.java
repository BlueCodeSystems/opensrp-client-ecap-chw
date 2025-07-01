package org.smartregister.chw.core.presenter;

import android.util.Pair;

import org.apache.commons.lang3.tuple.Triple;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.chw.core.contract.CoreChildRegisterContract;
import org.smartregister.chw.core.contract.FamilyProfileExtendedContract;
import org.smartregister.chw.core.interactor.CoreChildRegisterInteractor;
import org.smartregister.chw.core.model.CoreChildProfileModel;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;

import java.lang.ref.WeakReference;

public class CoreFamilyProfilePresenterTest {

    @Mock
    private FamilyProfileExtendedContract.View view;
    @Mock
    private CoreChildProfileModel childProfileModel;
    @Mock
    private CoreChildRegisterInteractor childRegisterInteractor;

    private CoreFamilyProfilePresenter profilePresenter;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        profilePresenter = Mockito.mock(CoreFamilyProfilePresenter.class, Mockito.CALLS_REAL_METHODS);
        ReflectionHelpers.setField(profilePresenter, "viewReference", new WeakReference<>(view));
        ReflectionHelpers.setField(profilePresenter, "childProfileModel", childProfileModel);
        ReflectionHelpers.setField(profilePresenter, "childRegisterInteractor", childRegisterInteractor);
    }

    @Test
    public void testGetView() {
        Assert.assertEquals(profilePresenter.getView(), view);
    }

    @Test
    public void testNotifyHasPhone() {
        boolean status = Math.random() % 2 == 0;
        profilePresenter.notifyHasPhone(status);
        Mockito.verify(view).updateHasPhone(status);
    }

    @Test
    public void testStartChildForm() throws Exception {
        profilePresenter.startChildForm("formName", "", "metadata", "currentLocationId");
        Mockito.verify(childRegisterInteractor).getNextUniqueId(Mockito.any(), Mockito.any(), Mockito.any());

        profilePresenter.startChildForm("formName", "entityID", "metadata", "currentLocationId");
        Mockito.verify(view).startFormActivity(Mockito.any());
    }

    @Test
    public void testSaveChildRegistration() throws Exception {
        Pair<Client, Event> pair = Pair.create(new Client("baseID"), new Event());
        String jsonString = "jsonString";

        CoreChildRegisterContract.InteractorCallBack callBack = Mockito.mock(CoreChildRegisterContract.InteractorCallBack.class);

        profilePresenter.saveChildRegistration(pair, jsonString, false, callBack);
        Mockito.verify(childRegisterInteractor).saveRegistration(pair, jsonString, false, profilePresenter);
    }

    @Test
    public void testOnUniqueIdFetched() {
        String anyString = Mockito.anyString();
        Triple<String, String, String> triple = Triple.of(anyString, anyString, anyString);
        profilePresenter.onUniqueIdFetched(triple, Mockito.anyString(), Mockito.anyString());
        Assert.assertNotNull(view);
    }
}
