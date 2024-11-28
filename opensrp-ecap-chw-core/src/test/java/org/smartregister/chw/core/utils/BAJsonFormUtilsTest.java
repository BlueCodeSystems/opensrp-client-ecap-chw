package org.smartregister.chw.core.utils;

import android.content.Context;

import net.sqlcipher.MatrixCursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.smartregister.chw.core.BaseUnitTest;
import org.smartregister.chw.core.application.CoreChwApplication;
import org.smartregister.chw.core.shadows.FormUtilsShadowHelper;
import org.smartregister.chw.core.shadows.LocationHelperShadowHelper;
import org.smartregister.chw.core.shadows.LocationPickerViewShadowHelper;
import org.smartregister.chw.core.shadows.UtilsShadowUtil;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.family.activity.FamilyWizardFormActivity;
import org.smartregister.family.domain.FamilyMetadata;
import org.smartregister.repository.Repository;
import org.smartregister.view.activity.BaseProfileActivity;

import java.util.HashMap;

import static org.smartregister.chw.core.utils.CoreJsonFormUtils.TITLE;
import static org.smartregister.family.util.Constants.JSON_FORM_KEY.ENCOUNTER_LOCATION;
import static org.smartregister.util.JsonFormUtils.STEP1;

@Config(shadows = {UtilsShadowUtil.class, LocationHelperShadowHelper.class, LocationPickerViewShadowHelper.class, FormUtilsShadowHelper.class})
public class BAJsonFormUtilsTest extends BaseUnitTest {

    @Mock
    private SQLiteDatabase database;

    @Mock
    private Repository repository;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getAutoJsonEditMemberFormStringReturnsCorrectJSONObject() throws JSONException {
        Context context = RuntimeEnvironment.application;
        HashMap<String, String> detailsMap = new HashMap<>();
        HashMap<String, String> columnMaps = new HashMap<>();

        CommonPersonObjectClient client = new CommonPersonObjectClient("testId", detailsMap, "tester");
        client.setColumnmaps(columnMaps);

        CoreChwApplication coreChwApplication = Mockito.mock(CoreChwApplication.class, Mockito.CALLS_REAL_METHODS);

        MatrixCursor clientJsonMatrixCursor = new MatrixCursor(new String[]{"json"});
        clientJsonMatrixCursor.addRow(new Object[]{getClientJsonString()});

        MatrixCursor eventJsonMatrixCursor = new MatrixCursor(new String[]{"json"});
        eventJsonMatrixCursor.addRow(new Object[]{getEventJsonString()});

        Mockito.doReturn(repository).when(coreChwApplication).getRepository();
        Mockito.doReturn(database).when(repository).getReadableDatabase();
        Mockito.doReturn(clientJsonMatrixCursor).when(database).rawQuery(Mockito.eq("select json from client where baseEntityId = ? order by updatedAt desc"), Mockito.any());
        Mockito.doReturn(eventJsonMatrixCursor).when(database).rawQuery(Mockito.eq("select json from event where baseEntityId = 'testId' and eventType in ('Update Family Member Registration','Family Member Registration') order by updatedAt desc limit 1;"), Mockito.any());

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
        UtilsShadowUtil.setMetadata(metadata);

        String formTitle = "Sample test registration form";
        String familyName = "Sample Family Name";
        BAJsonFormUtils baJsonFormUtils = new BAJsonFormUtils(coreChwApplication);
        JSONObject resultObject = baJsonFormUtils.getAutoJsonEditMemberFormString(formTitle, "family_register",
                context, client, Utils.metadata().familyMemberRegister.updateEventType, familyName, false);
        Assert.assertNotNull(resultObject);
        JSONObject formMetadata = resultObject.getJSONObject(org.smartregister.family.util.JsonFormUtils.METADATA);
        Assert.assertEquals("test_location_id", formMetadata.getString(ENCOUNTER_LOCATION));
        JSONObject stepOne = resultObject.getJSONObject(STEP1);
        Assert.assertEquals(formTitle, stepOne.getString(TITLE));
    }

    private String getClientJsonString() {
        return "{\"type\":\"Client\",\"dateCreated\":\"2020-06-12T21:53:21.758+03:00\",\"serverVersion\":1591988001743,\"clientApplicationVersion\":6,\"clientDatabaseVersion\":9," +
                "\"baseEntityId\":\"0b83417a-99e2-47ea-bc2a-6cef93fb3584\",\"identifiers\":{\"opensrp_id\":\"7984255\"},\"addresses\":[],\"attributes\":{\"age\":\"54\",\"id_avail\":\"[\\\"chk_none\\\"]\"," +
                "\"Community_Leader\":\"[\\\"chk_traditional\\\",\\\"chk_political\\\",\\\"chk_religious\\\"]\",\"sync_location_id\":\"Kabila Village\",\"Health_Insurance_Type\":\"Jubilee Insurance\"," +
                "\"Health_Insurance_Number\":\"099192309102\"},\"firstName\":\"Babu\",\"middleName\":\"Majaku\",\"lastName\":\"Omwami\",\"birthdate\":\"1966-01-01T03:00:00.000+03:00\",\"birthdateApprox\":true," +
                "\"deathdateApprox\":false,\"gender\":\"Male\",\"relationships\":{\"family\":[\"41a59522-cde3-4ef7-bf80-bd179505fae6\"]},\"_id\":\"0a3d464c-ad5a-477a-8535-ab5ae866dddb\",\"_rev\":\"v1\"}";
    }

    private String getEventJsonString() {
        return "{\"type\":\"Event\",\"dateCreated\":\"2020-06-12T21:53:21.782+03:00\",\"serverVersion\":1591988001766,\"clientApplicationVersion\":6,\"clientDatabaseVersion\":9,\"identifiers\":{}," +
                "\"baseEntityId\":\"0b83417a-99e2-47ea-bc2a-6cef93fb3584\",\"locationId\":\"2c3a0ebd-f79d-4128-a6d3-5dfbffbd01c8\",\"eventDate\":\"2020-06-12T03:00:00.000+03:00\",\"eventType\":\"Family Member Registration\"," +
                "\"formSubmissionId\":\"93cc3da7-bb4b-42d1-b822-f712f432943a\",\"providerId\":\"hfone\",\"duration\":0,\"obs\":[{\"fieldType\":\"formsubmissionField\",\"fieldDataType\":\"text\"," +
                "\"fieldCode\":\"same_as_fam_name\",\"parentCode\":\"\",\"values\":[\"true\"],\"formSubmissionField\":\"same_as_fam_name\",\"humanReadableValues\":[]},{\"fieldType\":\"concept\"," +
                "\"fieldDataType\":\"text\",\"fieldCode\":\"\",\"parentCode\":\"\",\"values\":[\"Omwami\"],\"formSubmissionField\":\"fam_name\",\"humanReadableValues\":[]},{\"fieldType\":\"concept\"," +
                "\"fieldDataType\":\"text\",\"fieldCode\":\"\",\"parentCode\":\"\",\"values\":[\"54\"],\"formSubmissionField\":\"age_calculated\",\"humanReadableValues\":[]},{\"fieldType\":\"formsubmissionField\"," +
                "\"fieldDataType\":\"text\",\"fieldCode\":\"dob_unknown\",\"parentCode\":\"\",\"values\":[\"DOB unknown?\"],\"formSubmissionField\":\"dob_unknown\",\"humanReadableValues\":[]}," +
                "{\"fieldType\":\"formsubmissionField\",\"fieldDataType\":\"text\",\"fieldCode\":\"id_avail\",\"parentCode\":\"\",\"values\":[\"None\"],\"formSubmissionField\":\"id_avail\"," +
                "\"humanReadableValues\":[]},{\"fieldType\":\"concept\",\"fieldDataType\":\"text\",\"fieldCode\":\"\",\"parentCode\":\"\",\"values\":[\"0\"],\"formSubmissionField\":\"wra\"," +
                "\"humanReadableValues\":[]},{\"fieldType\":\"concept\",\"fieldDataType\":\"text\",\"fieldCode\":\"\",\"parentCode\":\"\",\"values\":[\"0\"],\"formSubmissionField\":\"mra\"," +
                "\"humanReadableValues\":[]},{\"fieldType\":\"concept\",\"fieldDataType\":\"text\",\"fieldCode\":\"162558AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"parentCode\":\"\"," +
                "\"values\":[\"1066AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"],\"formSubmissionField\":\"disabilities\",\"humanReadableValues\":[\"No\"]},{\"fieldType\":\"concept\",\"fieldDataType\":\"text\"," +
                "\"fieldCode\":\"\",\"parentCode\":\"\",\"values\":[null],\"formSubmissionField\":\"is_primary_caregiver\",\"humanReadableValues\":[\"Yes\"]},{\"fieldType\":\"concept\",\"fieldDataType\":\"text\"," +
                "\"fieldCode\":\"159635AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"parentCode\":\"\",\"values\":[\"0991929123\"],\"formSubmissionField\":\"phone_number\",\"humanReadableValues\":[]},{\"fieldType\":\"concept\"," +
                "\"fieldDataType\":\"text\",\"fieldCode\":\"5622AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"parentCode\":\"159635AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"values\":[\"0192939123\"],\"formSubmissionField\":\"other_phone_number\"," +
                "\"humanReadableValues\":[]},{\"fieldType\":\"concept\",\"fieldDataType\":\"text\",\"fieldCode\":\"1542AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"parentCode\":\"1542AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"," +
                "\"values\":[\"1577AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"],\"formSubmissionField\":\"service_provider\",\"humanReadableValues\":[\"Nurse\"]},{\"fieldType\":\"concept\",\"fieldDataType\":\"text\"," +
                "\"fieldCode\":\"1542AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"parentCode\":\"1542AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"values\":[\"162946AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"],\"formSubmissionField\":\"service_provider\"," +
                "\"humanReadableValues\":[\"Teacher\"]},{\"fieldType\":\"formsubmissionField\",\"fieldDataType\":\"text\",\"fieldCode\":\"leader\",\"parentCode\":\"\",\"values\":[\"Religious leader\"," +
                "\"Traditional leader\",\"Political leader\"],\"formSubmissionField\":\"leader\",\"humanReadableValues\":[]},{\"fieldType\":\"formsubmissionField\",\"fieldDataType\":\"text\",\"fieldCode\":\"last_interacted_with\"," +
                "\"parentCode\":\"\",\"values\":[\"1591987941244\"],\"formSubmissionField\":\"last_interacted_with\",\"humanReadableValues\":[]},{\"fieldType\":\"concept\",\"fieldDataType\":\"start\"," +
                "\"fieldCode\":\"163137AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"parentCode\":\"\",\"values\":[\"2020-06-12 21:50:22\"],\"formSubmissionField\":\"start\",\"humanReadableValues\":[]}," +
                "{\"fieldType\":\"concept\",\"fieldDataType\":\"end\",\"fieldCode\":\"163138AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"parentCode\":\"\",\"values\":[\"2020-06-12 21:52:21\"],\"formSubmissionField\":\"end\"," +
                "\"humanReadableValues\":[]},{\"fieldType\":\"concept\",\"fieldDataType\":\"deviceid\",\"fieldCode\":\"163149AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"parentCode\":\"\",\"values\":[\"358240051111110\"]," +
                "\"formSubmissionField\":\"deviceid\",\"humanReadableValues\":[]},{\"fieldType\":\"concept\",\"fieldDataType\":\"subscriberid\",\"fieldCode\":\"163150AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"parentCode\":\"\"," +
                "\"values\":[\"310260000000000\"],\"formSubmissionField\":\"subscriberid\",\"humanReadableValues\":[]},{\"fieldType\":\"concept\",\"fieldDataType\":\"simserial\",\"fieldCode\":\"163151AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"," +
                "\"parentCode\":\"\",\"values\":[\"89014103211118510720\"],\"formSubmissionField\":\"simserial\",\"humanReadableValues\":[]},{\"fieldType\":\"concept\",\"fieldDataType\":\"phonenumber\"," +
                "\"fieldCode\":\"163152AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"parentCode\":\"\",\"values\":[\"+15555215556\"],\"formSubmissionField\":\"phonenumber\",\"humanReadableValues\":[]}],\"entityType\":\"ec_family_member\"," +
                "\"version\":1591987941259,\"teamId\":\"d60e1ee9-19e9-4e7d-a949-39f790a0ceda\",\"team\":\"Huruma Dispensary\",\"childLocationId\":\"Kabila\",\"_id\":\"56ff0218-c7dc-4d92-8d46-18cf3ea54f04\",\"_rev\":\"v1\"}";
    }
}
