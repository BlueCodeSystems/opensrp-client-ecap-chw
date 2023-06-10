package com.bluecodeltd.ecap.chw.presenter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.koin.test.AutoCloseKoinTest;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.smartregister.family.contract.FamilyOtherMemberProfileFragmentContract;

/**
 * Created by keyman on 11/03/2019.
 */
public class FamilyOtherMemberProfileFragmentPresenterTest extends AutoCloseKoinTest {

    @Mock
    private FamilyOtherMemberProfileFragmentContract.View view;

    @Mock
    private FamilyOtherMemberProfileFragmentContract.Model model;

    private FamilyOtherMemberProfileFragmentContract.Presenter presenter;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        String viewConfigurationIdentifier = "viewConfigurationIdentifier";
        String familyBaseEntityId = "familyBaseEntityId";
        String baseEntityId = "baseEntityId";

        presenter = new FamilyOtherMemberProfileFragmentPresenter(view, model, viewConfigurationIdentifier, familyBaseEntityId, baseEntityId);
    }

    @Test
    public void testMainCondition() {
        FamilyOtherMemberProfileFragmentPresenter familyOtherMemberProfileFragmentPresenter = (FamilyOtherMemberProfileFragmentPresenter) presenter;
        Assert.assertEquals(" object_id = 'baseEntityId' and date_removed is null ", familyOtherMemberProfileFragmentPresenter.getMainCondition());
    }

}
