package org.smartregister.chw.core.presenter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.smartregister.chw.core.contract.BaseReferralRegisterFragmentContract;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.commonregistry.CommonPersonObjectClient;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class BaseReferralFragmentPresenterTest {

    private BaseReferralFragmentPresenter referralFragmentPresenter;

    @Mock
    private BaseReferralRegisterFragmentContract.View view;

    @Mock
    private BaseReferralRegisterFragmentContract.Interactor interactor;

    private String baseEntityId = "5ome-base-ent3-1d";
    private String taskFocus = "Suspected Malaria";

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        referralFragmentPresenter = new BaseReferralFragmentPresenter(view);
        referralFragmentPresenter.setBaseEntityId(baseEntityId);
        referralFragmentPresenter.setTasksFocus(taskFocus);
        referralFragmentPresenter.setInteractor(interactor);
    }

    @Test
    public void testSetClientDetails() {
        Map<String, String> details = new HashMap<>();
        details.put("first_name", "Beth");
        details.put("last_name", "Marion");
        CommonPersonObjectClient client = new CommonPersonObjectClient(baseEntityId, details, "Beth Marion");
        referralFragmentPresenter.clientDetails(client);
        Mockito.verify(view, Mockito.atLeastOnce()).setClient(client);
    }

    @Test
    public void testInitializeQueries() {
        String mainCondition = "is_closed = 0";
        referralFragmentPresenter.initializeQueries(mainCondition);
        Mockito.verify(view, Mockito.atLeastOnce()).initializeQueryParams(CoreConstants.TABLE_NAME.FAMILY_MEMBER,
                "SELECT COUNT(*) FROM task WHERE is_closed = 0 ", "Select task._id as _id , task.focus , task.requester , task.start FROM task WHERE is_closed = 0 ");
        Mockito.verify(view, Mockito.atLeastOnce()).initializeAdapter(new HashSet<>(), CoreConstants.TABLE_NAME.TASK);
        Mockito.verify(view, Mockito.atLeastOnce()).countExecute();
        Mockito.verify(view, Mockito.atLeastOnce()).filterandSortInInitializeQueries();
    }

    @Test
    public void testFetchClient() {
        referralFragmentPresenter.fetchClient();
        Mockito.verify(interactor, Mockito.atLeastOnce()).getClientDetails(baseEntityId, referralFragmentPresenter, taskFocus);
    }

    @Test
    public void testGetBaseEntityIdAndTaskFocus() {
        Assert.assertNotNull(referralFragmentPresenter.getBaseEntityId());
        Assert.assertNotNull(referralFragmentPresenter.getTaskFocus());
    }
}