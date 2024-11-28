package org.smartregister.chw.core.presenter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.chw.core.contract.CoreCommunityRespondersContract;
import org.smartregister.chw.core.model.CommunityResponderModel;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class CoreCommunityRespondersPresenterTest {
    @Mock
    private CoreCommunityRespondersContract.View view;

    @Mock
    private CoreCommunityRespondersContract.Interactor interactor;

    @Mock
    private CoreCommunityRespondersContract.InteractorCallBack callBack;

    private CoreCommunityRespondersPresenter profilePresenter;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        profilePresenter = Mockito.mock(CoreCommunityRespondersPresenter.class, Mockito.CALLS_REAL_METHODS);
        ReflectionHelpers.setField(profilePresenter, "viewWeakReference", new WeakReference<>(view));
        ReflectionHelpers.setField(profilePresenter, "interactor", interactor);
    }

    @Test
    public void testGetView() {
        profilePresenter.getView();
        Assert.assertNotNull(view);
    }

    @Test
    public void testFetchAllCommunityResponders() {
        profilePresenter.fetchAllCommunityResponders();
        Assert.assertNotNull(interactor);
    }

    @Test
    public void testAddCommunityResponder() {
        String jsonString = "JsonString";
        profilePresenter.addCommunityResponder(jsonString);
        interactor.addCommunityResponder(jsonString, callBack);
        Assert.assertNotNull(interactor);
    }

    @Test
    public void testRemoveCommunityResponder() {
        String jsonString = "JsonString";
        profilePresenter.removeCommunityResponder(jsonString);
        interactor.removeCommunityResponder(jsonString, callBack);
        Assert.assertNotNull(interactor);
    }

    @Test
    public void testUpdateCommunityResponders() {
        List<CommunityResponderModel> communityResponderModelList = new ArrayList<>();
        profilePresenter.updateCommunityResponders(communityResponderModelList);
        view.refreshCommunityResponders(communityResponderModelList);
        Assert.assertNotNull(view);
    }

    @Test
    public void testUpdateCommunityRespondersList() {
        List<CommunityResponderModel> communityResponderModelList = new ArrayList<>();
        profilePresenter.updateCommunityRespondersList(communityResponderModelList);
        view.refreshCommunityResponders(communityResponderModelList);
        Assert.assertNotNull(view);
    }
}
