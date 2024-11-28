package org.smartregister.chw.core.presenter;

import org.apache.commons.lang3.tuple.Triple;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.chw.core.contract.CoreChildRegisterContract;
import org.smartregister.domain.FetchStatus;
import org.smartregister.family.domain.FamilyEventClient;

import java.lang.ref.WeakReference;
import java.util.List;

import timber.log.Timber;

public class CoreChildRegisterPresenterTest {

    @Mock
    private CoreChildRegisterContract.View view;

    @Mock
    private CoreChildRegisterContract.Interactor interactor;

    @Mock
    private CoreChildRegisterContract.Model model;

    @Mock
    private CoreChildRegisterContract.InteractorCallBack callBack;

    @Mock
    private List<String> viewIdentifiers;

    @Mock
    private JSONObject jsonObject;

    @Mock
    private FamilyEventClient familyEventClient;

    private CoreChildRegisterPresenter profilePresenter;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        profilePresenter = Mockito.mock(CoreChildRegisterPresenter.class, Mockito.CALLS_REAL_METHODS);
        ReflectionHelpers.setField(profilePresenter, "viewReference", new WeakReference<>(view));
        ReflectionHelpers.setField(profilePresenter, "interactor", interactor);
        ReflectionHelpers.setField(profilePresenter, "model", model);
    }

    @Test
    public void testRegisterViewConfigurations() {
        profilePresenter.registerViewConfigurations(viewIdentifiers);
        Mockito.verify(model).registerViewConfigurations(viewIdentifiers);
    }

    @Test
    public void testUnregisterViewConfiguration() {
        profilePresenter.unregisterViewConfiguration(viewIdentifiers);
        Mockito.verify(model).unregisterViewConfiguration(viewIdentifiers);
    }

    @Test
    public void testOnDestroy() {
        profilePresenter.onDestroy(true);
        Mockito.verify(interactor).onDestroy(true);
    }

    @Test
    public void testSaveLanguage() {
        profilePresenter.saveLanguage(Mockito.anyString());
        Mockito.verify(view).displayToast(Mockito.anyString());
    }

    @Test
    public void testStartForm() {
        try {
            String entityId = Mockito.anyString();
            String familyId = Mockito.anyString();
            profilePresenter.startForm(Mockito.anyString(), entityId, Mockito.anyString(), Mockito.anyString(), familyId);
            if ("".equals(entityId)) {
                String anyString = Mockito.anyString();
                Triple<String, String, String> triple = Triple.of(anyString, anyString, anyString);
                Mockito.verify(interactor).getNextUniqueId(triple, callBack, Mockito.anyString());
            }
            if ("".equals(familyId)) {
                Mockito.verify(view).startFormActivity(jsonObject);
            } else {
                Mockito.verify(view).startFormActivity(jsonObject);
            }

        } catch (Exception e) {
            Timber.v(e.toString());
        }
    }

    @Test
    public void testOnNoUniqueId() {
        profilePresenter.onNoUniqueId();
        Mockito.verify(view).displayShortToast(Mockito.anyInt());
    }

    @Test
    public void testOnUniqueIdFetched() {
        String anyString = Mockito.anyString();
        Triple<String, String, String> triple = Triple.of(anyString, anyString, anyString);
        profilePresenter.onUniqueIdFetched(triple, Mockito.anyString(), Mockito.anyString());
        Assert.assertNotNull(view);
    }

    @Test
    public void testOnRegistrationSaved() {
        FetchStatus fetchStatus = FetchStatus.fetched;
        Mockito.anyString();
        profilePresenter.onRegistrationSaved(Mockito.anyBoolean(), Mockito.anyBoolean(), familyEventClient);
        Mockito.verify(view).refreshList(fetchStatus);
        Mockito.verify(view).hideProgressDialog();
    }
}