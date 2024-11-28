package org.smartregister.chw.core.utils;


import android.content.Context;
import android.content.Intent;
import android.util.Pair;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.apache.commons.lang3.tuple.Triple;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.smartregister.CoreLibrary;
import org.smartregister.chw.core.BaseUnitTest;
import org.smartregister.chw.core.application.CoreChwApplication;
import org.smartregister.chw.core.application.TestApplication;
import org.smartregister.chw.core.domain.FamilyMember;
import org.smartregister.chw.core.shadows.ContextShadow;
import org.smartregister.chw.core.shadows.EcSyncHelperShadowHelper;
import org.smartregister.chw.core.shadows.FamilyLibraryShadowUtil;
import org.smartregister.chw.core.shadows.FormUtilsShadowHelper;
import org.smartregister.chw.core.shadows.LocationHelperShadowHelper;
import org.smartregister.chw.core.shadows.LocationPickerViewShadowHelper;
import org.smartregister.chw.core.shadows.UtilsShadowUtil;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.Location;
import org.smartregister.domain.LocationProperty;
import org.smartregister.family.activity.FamilyWizardFormActivity;
import org.smartregister.family.domain.FamilyMetadata;
import org.smartregister.family.util.Constants;
import org.smartregister.family.util.DBConstants;
import org.smartregister.view.activity.BaseProfileActivity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.smartregister.chw.core.utils.CoreJsonFormUtils.METADATA;
import static org.smartregister.util.JsonFormUtils.ENCOUNTER_LOCATION;

@Config(application = TestApplication.class, shadows = {ContextShadow.class, FamilyLibraryShadowUtil.class,
        UtilsShadowUtil.class, EcSyncHelperShadowHelper.class, FormUtilsShadowHelper.class, LocationHelperShadowHelper.class, LocationPickerViewShadowHelper.class})
public class CoreJsonFormUtilsTest extends BaseUnitTest {

    private JSONObject jsonForm;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        String jsonString = "{\"count\": \"2\",\"metadata\": {},\"step1\": {\"title\": \"Family Registration Info\",\"next\": \"step2\",\"fields\": [{\"key\":\"sync_location_id\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person_attribute\",\"openmrs_entity_id\":\"sync_location_id\",\"type\":\"spinner\",\"hint\":\"Select CHW Location\",\"v_required\":{\"value\":\"true\",\"err\":\"Please select CHW Location\"},\"values\":[\"Tabata Dampo - Unified\"],\"keys\":[\"Tabata Dampo - Unified\"],\"openmrs_choice_ids\":{\"Tabata Dampo - Unified\":\"Tabata Dampo - Unified\"},\"location_uuids\":{\"Tabata Dampo - Unified\":\"fb7ed5db-138d-4e6f-94d8-bc443b58dadb\"},\"value\":\"Tabata Dampo - Unified\"}] },\"step2\": {\"title\": \"Family Registration Page two\",\"fields\": [] }}";
        try {
            jsonForm = new JSONObject(jsonString);

        } catch (Exception ex) {
            System.err.println(ex);
        }
    }

    @Test
    public void getAncPncStartFormIntentReturnsIntent() {
        Intent ancPncIntent = CoreJsonFormUtils.getAncPncStartFormIntent(jsonForm, RuntimeEnvironment.application);
        Assert.assertNotNull(ancPncIntent);
    }

    @Test
    public void getFormStepsReturnsListOfJsonObjectSteps() throws JSONException {
        List<JSONObject> steps = CoreJsonFormUtils.getFormSteps(jsonForm);
        Assert.assertEquals(steps.get(1).optString("title"), "Family Registration Page two");
    }

    @Test
    public void processRemoveMemberEventReturnsCorrectTriple() throws JSONException {
        String encounterType = "Remove Family Member";
        String baseEntityId = "16847ab9-5cce-4b88-a666-bb9f8291a4bb";
        CoreLibrary.init(org.smartregister.Context.getInstance());
        JSONObject removeMemberJsonObject = new JSONObject(getRemoveMemberJsonString(encounterType, baseEntityId));
        Triple<Pair<Date, String>, String, List<Event>> eventTriple = CoreJsonFormUtils.processRemoveMemberEvent(null, Utils.getAllSharedPreferences(), removeMemberJsonObject, null);
        Assert.assertNotNull(eventTriple);
        Assert.assertEquals(encounterType, eventTriple.getLeft().second);
        Assert.assertEquals(baseEntityId, eventTriple.getMiddle());
        Assert.assertEquals(1, eventTriple.getRight().size());
    }

    @Test
    public void getAutoPopulatedJsonEditFormReturnsCorrectString() throws JSONException {
        Context context = RuntimeEnvironment.application;
        String id = "testCaseId";
        String encounterType = "test_encounter";
        String landMark = "police station";
        String nearestHealthFacility = "makutano health center";

        HashMap<String, String> detailsMap = new HashMap<>();
        HashMap<String, String> columnMaps = new HashMap<>();
        columnMaps.put(DBConstants.KEY.DOB, "01-12-2020");
        columnMaps.put(Constants.JSON_FORM_KEY.DOB_UNKNOWN, "false");
        columnMaps.put(DBConstants.KEY.FIRST_NAME, "Sonkore");
        columnMaps.put(DBConstants.KEY.LANDMARK, landMark);
        columnMaps.put(ChwDBConstants.NEAREST_HEALTH_FACILITY, nearestHealthFacility);
        columnMaps.put(DBConstants.KEY.GPS, "-0.00001, 0.25400");

        CommonPersonObjectClient testClient = new CommonPersonObjectClient(id, detailsMap, "tester");
        testClient.setColumnmaps(columnMaps);

        JSONObject jsonObject = CoreJsonFormUtils.getAutoPopulatedJsonEditFormString("family_register", context, testClient, encounterType);
        assert jsonObject != null;
        JSONObject step1Object = jsonObject.getJSONObject("step1");
        String actualLandMark = null;
        String actualHealthFacility = null;
        JSONArray fields = step1Object.getJSONArray(JsonFormConstants.FIELDS);
        for (int i = 0; i < fields.length(); i++) {
            String key = fields.getJSONObject(i).getString(JsonFormConstants.KEY);
            if (key.equals(DBConstants.KEY.LANDMARK)) {
                actualLandMark = fields.getJSONObject(i).getString(JsonFormConstants.VALUE);
            }
            if (key.equals(ChwDBConstants.NEAREST_HEALTH_FACILITY)) {
                actualHealthFacility = fields.getJSONObject(i).getString(JsonFormConstants.VALUE);
            }
        }
        Assert.assertEquals(landMark, actualLandMark);
        Assert.assertEquals(nearestHealthFacility, actualHealthFacility);
    }


    @Test
    public void processPopulatableFieldsUpdatesJSONOptionsWithCorrectValues() throws JSONException {
        JSONObject optionsObject;
        JSONObject jsonObject;
        String jsonString;

        String id = "testCaseId";
        String name = "tester";
        String dob = "01-12-2020";
        String streetName = "Matopeni";

        HashMap<String, String> detailsMap = new HashMap<>();
        HashMap<String, String> columnMaps = new HashMap<>();
        columnMaps.put(DBConstants.KEY.DOB, dob);
        columnMaps.put("fam_name", "Sonkos");
        columnMaps.put(DBConstants.KEY.STREET, streetName);

        CommonPersonObjectClient testClient = new CommonPersonObjectClient(id, detailsMap, name);
        testClient.setColumnmaps(columnMaps);

        jsonString = "{" + org.smartregister.family.util.JsonFormUtils.KEY + " : " + DBConstants.KEY.DOB + ", \"options\": [{}]}";
        jsonObject = new JSONObject(jsonString);
        CoreJsonFormUtils.processPopulatableFields(testClient, jsonObject);
        optionsObject = jsonObject.getJSONArray(Constants.JSON_FORM_KEY.OPTIONS).getJSONObject(0);
        Assert.assertEquals(dob, optionsObject.getString(org.smartregister.family.util.JsonFormUtils.VALUE));

        jsonString = "{" + org.smartregister.family.util.JsonFormUtils.KEY + " : " + DBConstants.KEY.STREET + "}";
        jsonObject = new JSONObject(jsonString);
        CoreJsonFormUtils.processPopulatableFields(testClient, jsonObject);
        Assert.assertEquals(streetName, jsonObject.getString(org.smartregister.family.util.JsonFormUtils.VALUE));

    }

    @Test
    public void canPopulateJsonFormWithMap() throws JSONException {
        String key = "danger_signs_present";
        String value = "lethargy";
        String step = "step1";
        String jsonString = "{\"" + step + "\": {\"title\": \"ANC Registration\",\"fields\": [{\"key\": \"" + key + "\"}]}}";
        Map<String, String> valueMap = new HashMap<>();
        valueMap.put(key, value);
        JSONObject formObject = new JSONObject(jsonString);
        CoreJsonFormUtils.populateJsonForm(formObject, valueMap);
        JSONObject jsonStepObject = formObject.getJSONObject(step);
        JSONArray fieldsArray = jsonStepObject.getJSONArray(JsonFormConstants.FIELDS);
        JSONObject keyValueObject = fieldsArray.getJSONObject(0); // We only have one field
        Assert.assertEquals(value, keyValueObject.optString(JsonFormConstants.VALUE));
    }

    @Test
    public void processFamilyUpdateRelationsReturnsCorrectPair() throws Exception {
        CoreChwApplication application = (CoreChwApplication) RuntimeEnvironment.application;
        CoreLibrary.init(org.smartregister.Context.getInstance());
        String memberId = "test-member-id-123";
        String familyId = "test-family-id-123";

        FamilyMember testMember = new FamilyMember();
        testMember.setFamilyID(familyId);
        testMember.setMemberID(memberId);
        testMember.setPrimaryCareGiver(true);
        testMember.setFamilyHead(true);

        Client testClient = new Client(memberId, "Mr", "Miyagi", "Sakamoto", new Date(), null,
                false, false, "male");
        testClient.setRelationships(new HashMap<>());
        EcSyncHelperShadowHelper.setTestClient(testClient);

        UtilsShadowUtil.setMetadata(initializeFamilyMetadata());

        Pair<List<Client>, List<Event>> resultPair = CoreJsonFormUtils.processFamilyUpdateRelations(application, RuntimeEnvironment.application, testMember, "Kenya");

        // Client validation
        Assert.assertEquals(1, resultPair.first.size());
        Assert.assertEquals(memberId, Objects.requireNonNull(resultPair.first.get(0).getRelationships().get(CoreConstants.RELATIONSHIP.PRIMARY_CAREGIVER)).get(0));

        // Events validation
        Assert.assertEquals(2, resultPair.second.size());
        Assert.assertEquals(CoreConstants.EventType.UPDATE_FAMILY_RELATIONS, resultPair.second.get(0).getEventType());
        Assert.assertEquals(familyId, resultPair.second.get(0).getBaseEntityId());
        Assert.assertEquals(CoreConstants.EventType.UPDATE_FAMILY_MEMBER_RELATIONS, resultPair.second.get(1).getEventType());
        Assert.assertEquals(memberId, resultPair.second.get(1).getBaseEntityId());
        Assert.assertEquals(3, resultPair.second.get(1).getObs().size());
    }

    @Test
    public void getFormAsJsonReturnsRegistrationFormWithCorrectIds() throws Exception {
        UtilsShadowUtil.setMetadata(initializeFamilyMetadata());
        JSONObject form = getFormAsJson("family_register");
        String formName = "family_register";
        String entityId = "test-entity-id";
        String currentLocationId = "test_location_id";
        String familyID = "test_family_id";
        JSONObject actualJsonObject = CoreJsonFormUtils.getFormAsJson(form, formName, entityId, currentLocationId, familyID);

        Assert.assertNotNull(actualJsonObject);

        String actualEncounterLocation = actualJsonObject.getJSONObject(METADATA).getString(ENCOUNTER_LOCATION);
        JSONObject actualLookupObject = actualJsonObject.getJSONObject(METADATA).getJSONObject("look_up");
        String actualLookupEntityId= actualLookupObject.getString("entity_id");
        String actualLookupEntityValue= actualLookupObject.getString("value");

        Assert.assertEquals(currentLocationId, actualEncounterLocation);
        Assert.assertEquals("family", actualLookupEntityId);
        Assert.assertEquals(familyID, actualLookupEntityValue);

    }

    @Test
    public void testGetSchoolLevels() {
        HashMap<String, String> schoolLevelsTestMap = new HashMap<>();
        schoolLevelsTestMap.put("Not currently attending school or any learning program", "school_level_none");
        schoolLevelsTestMap.put("Early childhood programme", "school_level_early_childhood");
        schoolLevelsTestMap.put("Primary", "school_level_primary");
        schoolLevelsTestMap.put("Lower secondary", "school_level_lower_secondary");
        schoolLevelsTestMap.put("Upper secondary", "school_level_upper_secondary");
        schoolLevelsTestMap.put("Alternative learning program", "school_level_alternative");
        schoolLevelsTestMap.put("Higher", "school_level_Higher");

        Assert.assertEquals(schoolLevelsTestMap, CoreJsonFormUtils.getSchoolLevels(RuntimeEnvironment.application));
    }

    @Test
    public void testGetChoice() {
        HashMap<String, String> choicesTestMap = new HashMap<>();
        choicesTestMap.put("Yes", "1065AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        choicesTestMap.put("No", "1066AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");

        Assert.assertEquals(choicesTestMap, CoreJsonFormUtils.getChoice(RuntimeEnvironment.application));
    }

    @Test
    public void testGetChoiceMuac() {
        HashMap<String, String> choicesMuacTestMap = new HashMap<>();
        choicesMuacTestMap.put("Green", "160909AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        choicesMuacTestMap.put("Yellow", "160910AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        choicesMuacTestMap.put("Red", "127778AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");

        Assert.assertEquals(choicesMuacTestMap, CoreJsonFormUtils.getChoiceMuac(RuntimeEnvironment.application));
    }

    @Test
    public void testGetChoiceDietary() {
        HashMap<String, String> choicesDietaryTestMap = new HashMap<>();
        choicesDietaryTestMap.put("0 - no animal products or fruit", "");
        choicesDietaryTestMap.put("1 - one animal product OR one fruit", "");
        choicesDietaryTestMap.put("2 - one animal product AND one fruit", "");

        Assert.assertEquals(choicesDietaryTestMap, CoreJsonFormUtils.getChoiceDietary(RuntimeEnvironment.application));
    }

    @Test
    public void testGetEverSchoolOptions() {
        HashMap<String, String> everSchoolOptionsTestMap = new HashMap<>();
        everSchoolOptionsTestMap.put("Yes", "key_yes");
        everSchoolOptionsTestMap.put("No", "key_no");

        Assert.assertEquals(everSchoolOptionsTestMap, CoreJsonFormUtils.getEverSchoolOptions(RuntimeEnvironment.application));
    }

    @Test
    public void addLocationsToDropdownField() throws JSONException {
        List<Location> locations = new ArrayList<>();
        Location location1 = new Location();
        location1.setId("uuid1");
        location1.setProperties(new LocationProperty());
        location1.getProperties().setName("Madona");
        location1.getProperties().setUid("uuid1");

        Location location2 = new Location();
        location2.setId("uuid2");
        location2.setProperties(new LocationProperty());
        location2.getProperties().setName("Ebrahim Haji");
        location2.getProperties().setUid("uuid2");

        locations.add(location1);
        locations.add(location2);

        JSONObject dropdownField = new JSONObject("{\"key\":\"sync_location_id\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person_attribute\",\"openmrs_entity_id\":\"sync_location_id\",\"type\":\"spinner\",\"hint\":\"Select CHW Location\",\"v_required\":{\"value\":\"true\",\"err\":\"Please select CHW Location\"}}");
        CoreJsonFormUtils.addLocationsToDropdownField(locations, dropdownField);
        Assert.assertTrue(dropdownField.has(JsonFormConstants.VALUES));
        Assert.assertTrue(dropdownField.has(JsonFormConstants.KEYS));
        Assert.assertTrue(dropdownField.has(JsonFormConstants.OPENMRS_CHOICE_IDS));
    }

    @Test
    public void getSyncLocationUUIDFromDropdown() throws JSONException {
        JSONObject dropdownField = new JSONObject("{\"key\":\"sync_location_id\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person_attribute\",\"openmrs_entity_id\":\"sync_location_id\",\"type\":\"spinner\",\"hint\":\"Select CHW Location\",\"v_required\":{\"value\":\"true\",\"err\":\"Please select CHW Location\"},\"values\":[\"Tabata Dampo - Unified\"],\"keys\":[\"Tabata Dampo - Unified\"],\"openmrs_choice_ids\":{\"Tabata Dampo - Unified\":\"Tabata Dampo - Unified\"},\"location_uuids\":{\"Tabata Dampo - Unified\":\"fb7ed5db-138d-4e6f-94d8-bc443b58dadb\"},\"value\":\"Tabata Dampo - Unified\"}");
        String locationUUID = CoreJsonFormUtils.getSyncLocationUUIDFromDropdown(dropdownField);
        Assert.assertNotNull(locationUUID);
        Assert.assertEquals(locationUUID, "fb7ed5db-138d-4e6f-94d8-bc443b58dadb");
        Assert.assertNull(CoreJsonFormUtils.getSyncLocationUUIDFromDropdown(new JSONObject()));
    }

    @Test
    public void getJsonField() {
        JSONObject locationId = CoreJsonFormUtils.getJsonField(jsonForm, org.smartregister.util.JsonFormUtils.STEP1, "sync_location_id");
        Assert.assertNotNull(locationId);
    }

    private String getRemoveMemberJsonString(String encounterType, String baseEntityId) {
        return "{\"count\":\"1\",\"encounter_type\":\"" + encounterType + "\",\"entity_id\":\"" + baseEntityId + "\",\"relational_id\":\"\",\"metadata\":{},\"step1\":{\"title\":\"RemoveFamilyMember\",\"fields\":[{\"key\":\"details\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"\",\"type\":" +
                "\"label\",\"text\":\"MelissaYoJiwanji,25Female\",\"text_size\":\"25px\"},{\"key\":\"remove_reason\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"160417AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_data_type\":\"selectone\",\"type\":\"spinner\",\"hint\":\"Reasonforremoval\",\"v_required\"" +
                ":{\"value\":\"true\",\"err\":\"Selectthereasonforremovingthefamilymember'srecord\"},\"values\":[\"Death\",\"Movedaway\",\"Other\"],\"keys\":[\"Death\",\"Movedaway\",\"Other\"],\"openmrs_choice_ids\":{\"Died\":\"160034AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"Movedaway\":\"160415AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"Other\"" +
                ":\"5622AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"is-rule-check\":true,\"step\":\"step1\",\"value\":\"Other\"},{\"key\":\"dob\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"\",\"type\":\"spacer\",\"expanded\":false,\"read_only\":\"true\",\"hidden\":\"false\",\"value\":\"01-04-1994\",\"step\"" +
                ":\"step1\",\"is-rule-check\":false},{\"key\":\"date_moved\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"164133AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_data_type\":\"text\",\"type\":\"date_picker\",\"label\":\"Datemovedaway\",\"hint\":\"Datemovedaway\",\"expanded\":false,\"min_date\"" +
                ":\"today-9475d\",\"max_date\":\"today\",\"v_required\":{\"value\":\"true\",\"err\":\"Enterthedatethatthemembermovedaway\"},\"is_visible\":false,\"is-rule-check\":false},{\"key\":\"date_died\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"1543AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_data_type\"" +
                ":\"text\",\"type\":\"date_picker\",\"label\":\"Dateofdeath\",\"hint\":\"Dateofdeath\",\"expanded\":false,\"min_date\":\"today-9475d\",\"max_date\":\"today\",\"v_required\":{\"value\":\"true\",\"err\":\"Enterthedateofdeath\"},\"step\":\"step1\",\"is-rule-check\":false,\"is_visible\":false},{\"key\":\"age_at_death\",\"openmrs_entity_parent\"" +
                ":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"\",\"label\":\"Ageatdeath\",\"hint\":\"Ageatdeath\",\"type\":\"edit_text\",\"read_only\":\"true\",\"is_visible\":false}]},\"invisible_required_fields\":\"[date_died,date_moved]\",\"details\":{\"appVersionName\":\"1.7.23-SNAPSHOT\",\"formVersion\":\"\"}}";
    }

    private JSONObject getFormAsJson(String formName) {
        return Objects.requireNonNull(FormUtils.getFormUtils()).getFormJson(formName);
    }

    private FamilyMetadata initializeFamilyMetadata() {
        FamilyMetadata metadata = new FamilyMetadata(FamilyWizardFormActivity.class, FamilyWizardFormActivity.class,
                BaseProfileActivity.class, CoreConstants.IDENTIFIER.UNIQUE_IDENTIFIER_KEY, false);
        metadata.updateFamilyRegister("family_register",
                "tableName",
                "registerEventType",
                "updateEventType",
                "config",
                "familyHeadRelationKey", "familyCareGiverRelationKey");
        metadata.updateFamilyMemberRegister("family_member_register",
                "tableName",
                "registerEventType",
                "updateEventType",
                "config",
                "familyRelationKey");

        return metadata;
    }
}
