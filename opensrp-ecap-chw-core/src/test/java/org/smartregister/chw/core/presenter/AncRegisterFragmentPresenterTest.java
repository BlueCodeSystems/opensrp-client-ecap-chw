package org.smartregister.chw.core.presenter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.smartregister.chw.anc.contract.BaseAncRegisterFragmentContract;

public class AncRegisterFragmentPresenterTest {

    private AncRegisterFragmentPresenter presenter;

    @Mock
    private BaseAncRegisterFragmentContract.View view;

    @Mock
    private BaseAncRegisterFragmentContract.Model model;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        presenter = new AncRegisterFragmentPresenter(view, model, "123");
    }

    @Test
    public void testGetMainCondition() {
        Assert.assertEquals(presenter.getDefaultSortQuery(), "ec_anc_register.last_interacted_with DESC ");
    }

    @Test
    public void testGetMainTable() {
        Assert.assertEquals(presenter.getMainTable(), "ec_anc_register");
    }

}
