package org.smartregister.chw.core.model;

import org.json.JSONArray;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.smartregister.chw.core.BaseUnitTest;
import org.smartregister.configurableviews.model.Field;
import org.smartregister.domain.Response;
import org.smartregister.domain.ResponseStatus;

import java.util.ArrayList;
import java.util.List;

public class CoreChildRegisterFragmentModelTest extends BaseUnitTest {

    private CoreChildRegisterFragmentModel childRegisterFragmentModel;

    @Before
    public void setUp() {
        childRegisterFragmentModel = Mockito.spy(CoreChildRegisterFragmentModel.class);
    }


    @Test
    public void getViewConfiguration() {
        Assert.assertNull(childRegisterFragmentModel.getViewConfiguration(""));
    }


    @Test
    public void countSelect() {
        String countSelect = childRegisterFragmentModel.countSelect("ec_table", "date_removed is null", "ec_family_member");
        Assert.assertNotNull(countSelect);
        Assert.assertEquals("SELECT COUNT(*) FROM ec_table INNER JOIN ec_family_member ON  ec_table.base_entity_id =  ec_family_member.base_entity_id WHERE date_removed is null ", countSelect);
    }

    @Test
    public void mainSelect() {
        String mainSelect = childRegisterFragmentModel.mainSelect("ec_table", "My Family", "My Cool Name", "date_removed is null");
        Assert.assertEquals("Select ec_table.id as _id , ec_table.relational_id as relationalid , ec_table.last_interacted_with , ec_table.base_entity_id , ec_table.first_name , ec_table.middle_name , My Cool Name.first_name as family_first_name , My Cool Name.last_name as family_last_name , My Cool Name.middle_name as family_middle_name , My Cool Name.phone_number as family_member_phone_number , My Cool Name.other_phone_number as family_member_phone_number_other , My Family.village_town as family_home_address , ec_table.last_name , ec_table.unique_id , ec_table.gender , ec_table.dob , ec_table.dob_unknown , ec_table.last_home_visit , ec_table.visit_not_done , ec_table.early_bf_1hr , ec_table.physically_challenged , ec_table.birth_cert , ec_table.birth_cert_issue_date , ec_table.birth_cert_num , ec_table.birth_notification , ec_table.date_of_illness , ec_table.illness_description , ec_table.date_created , ec_table.action_taken , ec_table.vaccine_card FROM ec_table LEFT JOIN My Family ON  ec_table.relational_id = My Family.id COLLATE NOCASE  LEFT JOIN My Cool Name ON  My Cool Name.base_entity_id = ec_table.base_entity_id COLLATE NOCASE  WHERE date_removed is null ", mainSelect);
    }

    @Test
    public void getFilterText() {
        List<Field> fields = new ArrayList<>();
        Field field = new Field();
        field.setDisplayName("am a field");
        fields.add(field);
        Field field2 = new Field();
        field2.setDisplayName("am a field 2");
        fields.add(field2);
        String filterText = childRegisterFragmentModel.getFilterText(fields, "Filter Title");
        Assert.assertEquals("<font color=#727272>Filter Title</font> <font color=#f0ab41>(2)</font>", filterText);
        String filterText2 = childRegisterFragmentModel.getFilterText(null, null);
        Assert.assertEquals("<font color=#727272></font> <font color=#f0ab41>(0)</font>", filterText2);
    }

    @Test
    public void getSortText() {
        Field field = new Field();
        field.setDisplayName("am a field");
        field.setDbAlias("am a db alias");
        String sortText = childRegisterFragmentModel.getSortText(field);
        Assert.assertTrue(sortText.contains("Sort: "));
    }

    @Test
    public void getJsonArray() {
        Response<String> response = new Response<>(ResponseStatus.success, "[{\"ok\": \"true\"}]");
        JSONArray jsonArray = childRegisterFragmentModel.getJsonArray(response);
        Assert.assertEquals(1, jsonArray.length());
    }

    @Test
    public void getJsonThrowsIsNull() {
        Response<String> response = new Response<>(ResponseStatus.success, "{\"ok\": \"true\"}");
        JSONArray jsonArray = childRegisterFragmentModel.getJsonArray(response);
        Assert.assertNull(jsonArray);
    }
}