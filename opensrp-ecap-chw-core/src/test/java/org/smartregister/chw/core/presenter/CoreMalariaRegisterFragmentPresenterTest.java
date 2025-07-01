package org.smartregister.chw.core.presenter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.smartregister.chw.malaria.contract.MalariaRegisterFragmentContract;

public class CoreMalariaRegisterFragmentPresenterTest {

    private CoreMalariaRegisterFragmentPresenter presenter;
    private String mainCondition;

    @Mock
    private MalariaRegisterFragmentContract.View view;

    @Mock
    private MalariaRegisterFragmentContract.Model model;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mainCondition = " ec_family_member.date_removed is null AND ec_malaria_confirmation.malaria  = 1 AND datetime('NOW') <= datetime(ec_malaria_confirmation.last_interacted_with/1000, 'unixepoch', 'localtime','+15 days') AND ec_malaria_confirmation.is_closed = 0";
        presenter = new CoreMalariaRegisterFragmentPresenter(view, model, "123");
    }

    @Test
    public void testGetMainTable() {
        Assert.assertEquals(presenter.getMainTable(), "ec_malaria_confirmation");
    }

    @Test
    public void testMainCondition() {
        Assert.assertEquals(presenter.getMainCondition(), mainCondition);
    }

}
