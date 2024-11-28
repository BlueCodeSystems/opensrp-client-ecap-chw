package org.smartregister.chw.core.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class CoreMalariaRegisterFragmentModelTest {

    private CoreMalariaRegisterFragmentModel malariaRegisterFragmentModel;

    @Before
    public void setUp() {
        malariaRegisterFragmentModel = Mockito.spy(CoreMalariaRegisterFragmentModel.class);
    }

    @Test
    public void mainSelect() {
        String mainSelect = malariaRegisterFragmentModel.mainSelect("ec_malaria", "date_removed is null");
        Assert.assertEquals("Select ec_malaria.id as _id , ec_malaria.malaria_test_date , ec_malaria.last_interacted_with , " +
                "ec_pregnancy_outcome.is_closed as is_pnc_closed , ec_family_member.middle_name , ec_anc_register.phone_number , " +
                "ec_malaria.base_entity_id , ec_anc_register.is_closed as is_anc_closed , ec_family_member.first_name , " +
                "ec_family_member.gender , ec_family.primary_caregiver , ec_family_member.last_name , ec_family.village_town , " +
                "ec_family_member.dob , ec_family.family_head , ec_family.first_name as family_name , ec_family_member.unique_id , " +
                "ec_family_member.entity_type , ec_family_member.relational_id as relationalid , ec_family_member.relational_id , " +
                "ec_pregnancy_outcome.delivery_date FROM ec_malaria " +
                "INNER JOIN ec_family_member ON  ec_malaria.base_entity_id = ec_family_member.base_entity_id COLLATE NOCASE  " +
                "INNER JOIN ec_family ON  ec_family_member.relational_id = ec_family.base_entity_id " +
                "LEFT JOIN ec_anc_register ON  ec_malaria.base_entity_id = ec_anc_register.base_entity_id COLLATE NOCASE  " +
                "LEFT JOIN ec_pregnancy_outcome ON  ec_malaria.base_entity_id = ec_pregnancy_outcome.base_entity_id COLLATE NOCASE  WHERE date_removed is null ", mainSelect);
    }

    @Test
    public void mainColumns() {
        String[] mainColumns = malariaRegisterFragmentModel.mainColumns("ec_malaria");
        Assert.assertEquals(20, mainColumns.length);
        Assert.assertEquals("ec_malaria.malaria_test_date", mainColumns[0]);
        Assert.assertEquals("ec_malaria.base_entity_id", mainColumns[5]);
        Assert.assertEquals("ec_family_member.last_name", mainColumns[10]);
        Assert.assertEquals("ec_family_member.unique_id", mainColumns[15]);
        Assert.assertEquals("ec_pregnancy_outcome.delivery_date", mainColumns[19]);
    }
}