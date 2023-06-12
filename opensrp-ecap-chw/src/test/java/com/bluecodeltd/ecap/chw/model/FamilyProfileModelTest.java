package com.bluecodeltd.ecap.chw.model;

import org.junit.Rule;
import org.junit.Test;
import org.koin.test.AutoCloseKoinTest;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

public class FamilyProfileModelTest extends AutoCloseKoinTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Test
    public void testProcessFamilyRegistrationForm() {
        FamilyProfileModel profileModel = Mockito.mock(FamilyProfileModel.class);
        profileModel.processFamilyRegistrationForm(Mockito.anyString(), Mockito.anyString());
        Mockito.verify(profileModel).processFamilyRegistrationForm(Mockito.anyString(), Mockito.anyString());
    }

    @Test
    public void testUpdateWra() {
        FamilyProfileModel profileModel = Mockito.mock(FamilyProfileModel.class);
        profileModel.updateWra(Mockito.any());
        Mockito.verify(profileModel).updateWra(Mockito.any());
    }

}
