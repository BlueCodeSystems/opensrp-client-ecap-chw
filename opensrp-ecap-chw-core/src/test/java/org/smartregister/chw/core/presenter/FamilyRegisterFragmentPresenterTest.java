package org.smartregister.chw.core.presenter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.smartregister.family.contract.FamilyRegisterFragmentContract;

public class FamilyRegisterFragmentPresenterTest {

    private FamilyRegisterFragmentPresenter presenter;

    @Mock
    private FamilyRegisterFragmentContract.View view;

    @Mock
    private FamilyRegisterFragmentContract.Model model;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        presenter = new FamilyRegisterFragmentPresenter(view, model, "123");
    }

    @Test
    public void getDefaultSortQuery() {
        Assert.assertEquals(presenter.getDefaultSortQuery(), "last_interacted_with DESC ");
    }

}
