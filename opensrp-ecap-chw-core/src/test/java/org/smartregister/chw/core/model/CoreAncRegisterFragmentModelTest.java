package org.smartregister.chw.core.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.HashSet;
import java.util.Set;

public class CoreAncRegisterFragmentModelTest {

    private final String ancRegister = "anc_register";
    private CoreAncRegisterFragmentModel ancRegisterFragmentModel;

    @Before
    public void setUp() {
        ancRegisterFragmentModel = Mockito.spy(CoreAncRegisterFragmentModel.class);
        CoreAncRegisterFragmentModel.Flavor ancRegisterFragmentFlavor = Mockito.spy(CoreAncRegisterFragmentModel.Flavor.class);
        Set<String> columns = new HashSet<>();
        columns.add("anc_register.column_one");
        columns.add("anc_register.column_two");
        columns.add("anc_register.column_three");
        Mockito.doReturn(columns).when(ancRegisterFragmentFlavor).mainColumns(ancRegister);
        ancRegisterFragmentModel.setFlavor(ancRegisterFragmentFlavor);
    }

    @Test
    public void mainSelect() {
        String mainSelect = ancRegisterFragmentModel.mainSelect("anc_register", "is_closed = null");
        Assert.assertNotNull(mainSelect);
        Assert.assertEquals(mainSelect, "Select anc_register.id as _id , anc_register.confirmed_visits , " +
                "anc_register.last_home_visit , ec_family_member.middle_name , ec_anc_log.date_created ," +
                " ec_family_member.first_name , ec_family.primary_caregiver , anc_register.column_two ," +
                " ec_family_member.last_name , ec_family.village_town , anc_register.last_menstrual_period ," +
                " ec_family_member.dob , anc_register.base_entity_id , ec_family.family_head , " +
                "ec_family.first_name as family_name , ec_family_member.unique_id , anc_register.column_one ," +
                " anc_register.column_three , anc_register.phone_number , anc_register.last_interacted_with ," +
                " ec_family_member.relational_id as relationalid , ec_family_member.relational_id ," +
                " anc_register.visit_not_done FROM anc_register INNER JOIN ec_family_member ON  " +
                "anc_register.base_entity_id = ec_family_member.base_entity_id COLLATE NOCASE  " +
                "INNER JOIN ec_anc_log ON  anc_register.base_entity_id = ec_anc_log.base_entity_id COLLATE NOCASE " +
                " INNER JOIN ec_family ON  ec_family_member.relational_id = ec_family.base_entity_id COLLATE NOCASE  WHERE is_closed = null ");
    }

    @Test
    public void mainColumns() {
        String[] mainColumns = ancRegisterFragmentModel.mainColumns(ancRegister);
        Assert.assertEquals(22, mainColumns.length);
        Assert.assertEquals(mainColumns[0], "anc_register.confirmed_visits");
        Assert.assertEquals(mainColumns[5], "ec_family.primary_caregiver");
        Assert.assertEquals(mainColumns[10], "ec_family_member.dob");
        Assert.assertEquals(mainColumns[15], "anc_register.column_one");
        Assert.assertEquals(mainColumns[20], "ec_family_member.relational_id");
        Assert.assertEquals(mainColumns[21], "anc_register.visit_not_done");

    }

    @Test
    public void getFlavor() {
        Assert.assertNotNull(ancRegisterFragmentModel.getFlavor());
    }
}