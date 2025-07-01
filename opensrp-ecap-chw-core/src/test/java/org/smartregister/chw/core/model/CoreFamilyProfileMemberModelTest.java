package org.smartregister.chw.core.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class CoreFamilyProfileMemberModelTest {

    private CoreFamilyProfileMemberModel profileMemberModel;

    @Before
    public void setUp() {
        profileMemberModel = Mockito.spy(CoreFamilyProfileMemberModel.class);
    }

    @Test
    public void mainColumns() {
        String[] mainColumns = profileMemberModel.mainColumns("ec_family_member");
        Assert.assertEquals(12, mainColumns.length);
        Assert.assertEquals(mainColumns[0], "ec_family_member.relationalid");
        Assert.assertEquals(mainColumns[2], "ec_family_member.base_entity_id");
        Assert.assertEquals(mainColumns[5], "ec_family_member.last_name");
        Assert.assertEquals(mainColumns[9], "ec_family_member.dod");
        Assert.assertEquals(mainColumns[11], "ec_family_member.phone_number");
    }
}