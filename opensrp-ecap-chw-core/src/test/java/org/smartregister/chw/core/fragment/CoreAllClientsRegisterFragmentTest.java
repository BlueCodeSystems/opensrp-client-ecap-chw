package org.smartregister.chw.core.fragment;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.chw.core.BaseUnitTest;
import org.smartregister.chw.core.R;
import org.smartregister.chw.core.presenter.CoreAllClientsRegisterFragmentPresenter;

public class CoreAllClientsRegisterFragmentTest extends BaseUnitTest {


    @Mock
    private CoreAllClientsRegisterFragmentPresenter presenter;

    private CoreAllClientsRegisterFragment fragment;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        fragment = Mockito.mock(CoreAllClientsRegisterFragment.class, Mockito.CALLS_REAL_METHODS);
        ReflectionHelpers.setField(fragment, "presenter", presenter);
    }

    @Test
    public void presenterInitializesCorrectly() {
        fragment.initializePresenter();
        Assert.assertNotNull(presenter);
    }

    @Test
    public void testGetToolbarTitle() {
        int title = fragment.getToolBarTitle();
        Assert.assertEquals(R.string.menu_all_clients, title);
    }
}
