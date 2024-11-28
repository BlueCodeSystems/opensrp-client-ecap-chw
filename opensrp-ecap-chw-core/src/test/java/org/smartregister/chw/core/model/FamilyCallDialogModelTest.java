package org.smartregister.chw.core.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class FamilyCallDialogModelTest {
    private FamilyCallDialogModel familyCallDialogModel;
    @Before
    public void setUp(){
        familyCallDialogModel = Mockito.spy(FamilyCallDialogModel.class);
    }
    @Test
    public void getNameTest(){
        String name = "Other";
        familyCallDialogModel.setName(name);
        Assert.assertEquals(familyCallDialogModel.getName(), name);
    }
    @Test
    public void getRoleTest(){
        String role = "Care Giver";
        familyCallDialogModel.setRole(role);
        Assert.assertEquals(familyCallDialogModel.getRole(), role);
    }

    @Test
    public void getPhoneNumberTest(){
        String phoneNumber = "0732674589";
        familyCallDialogModel.setPhoneNumber(phoneNumber);
        Assert.assertEquals(familyCallDialogModel.getPhoneNumber(), phoneNumber);
    }
}
