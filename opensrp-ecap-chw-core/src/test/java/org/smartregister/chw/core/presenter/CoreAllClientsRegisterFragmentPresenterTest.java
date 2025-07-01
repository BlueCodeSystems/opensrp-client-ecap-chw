package org.smartregister.chw.core.presenter;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.smartregister.chw.core.fragment.CoreAllClientsRegisterFragment;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.opd.model.OpdRegisterFragmentModel;

public class CoreAllClientsRegisterFragmentPresenterTest {

    private CoreAllClientsRegisterFragmentPresenter allClientsRegisterFragmentPresenter;

    @Mock
    private CoreAllClientsRegisterFragment view;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        allClientsRegisterFragmentPresenter = new CoreAllClientsRegisterFragmentPresenter(view, new OpdRegisterFragmentModel());
    }

    @Test
    public void testInitializeQueries() {
        allClientsRegisterFragmentPresenter.initializeQueries("is_closed = 0");
        Mockito.verify(view, Mockito.atLeastOnce()).initializeQueryParams(CoreConstants.TABLE_NAME.FAMILY_MEMBER, null, null);
        Mockito.verify(view, Mockito.atLeastOnce()).initializeAdapter();
        Mockito.verify(view, Mockito.atLeastOnce()).countExecute();
        Mockito.verify(view, Mockito.atLeastOnce()).filterandSortInInitializeQueries();
    }
}