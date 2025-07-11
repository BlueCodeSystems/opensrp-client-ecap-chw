package org.smartregister.chw.core.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Build;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.smartregister.chw.core.R;
import org.smartregister.chw.core.application.CoreChwApplication;
import org.smartregister.chw.core.application.TestApplication;
import org.smartregister.chw.referral.domain.MemberObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.tag.FormTag;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


@RunWith(RobolectricTestRunner.class)
@Config(application = TestApplication.class, sdk = Build.VERSION_CODES.P)
public class UtilsTest {

    private Map<String, String> details;
    private Map<String, String> columnMap;
    private CommonPersonObjectClient client;
    private Context context;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Before
    public void setUp() {
        details = new HashMap<>();
        columnMap = new HashMap<>();
        context = RuntimeEnvironment.application.getApplicationContext();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetAnCWomanImageResourceIdentifier() {
        Assert.assertEquals(R.drawable.anc_woman, Utils.getAnCWomanImageResourceIdentifier());
    }

    @Test
    public void testGetPnCWomanImageResourceIdentifier() {
        Assert.assertEquals(R.drawable.pnc_woman, Utils.getPnCWomanImageResourceIdentifier());
    }

    @Test
    public void testGetMemberImageResourceIdentifier() {
        Assert.assertEquals(R.mipmap.ic_member, Utils.getMemberImageResourceIdentifier());
    }

    @Test
    public void testGetYesNoAsLanguageSpecific() {
        Assert.assertEquals(context.getString(R.string.yes), Utils.getYesNoAsLanguageSpecific(context, "Yes"));
        Assert.assertEquals(context.getString(R.string.yes), Utils.getYesNoAsLanguageSpecific(context, "YES"));
        Assert.assertEquals("", Utils.getYesNoAsLanguageSpecific(context, ""));
        Assert.assertEquals(context.getString(R.string.no), Utils.getYesNoAsLanguageSpecific(context, "No"));
        Assert.assertEquals(context.getString(R.string.no), Utils.getYesNoAsLanguageSpecific(context, "NO"));
        Assert.assertEquals("other", Utils.getYesNoAsLanguageSpecific(context, "other"));
    }

    @Test
    public void testFirstCharacterUppercase() {
        Assert.assertEquals(Utils.firstCharacterUppercase(" "), " ");
        Assert.assertEquals(Utils.firstCharacterUppercase(""), "");
        Assert.assertEquals(Utils.firstCharacterUppercase("mike"), "Mike");
        Assert.assertEquals(Utils.firstCharacterUppercase("s"), "S");
        Assert.assertEquals(Utils.firstCharacterUppercase("kevin hart"), "Kevin hart");
    }

    @Test
    public void testConvertToDateFormateString() {
        Assert.assertEquals("2018-08-12", Utils.convertToDateFormateString("12-08-2018", new SimpleDateFormat("yyyy-MM-dd")));
        Assert.assertEquals("", Utils.convertToDateFormateString("me", new SimpleDateFormat("yyyy-MM-dd")));
    }

    @Test
    public void testGetOverDueProfileImageResourceIDentifier() {
        Assert.assertEquals(R.color.visit_status_over_due, Utils.getOverDueProfileImageResourceIDentifier());
    }

    @Test
    public void testGetDueProfileImageResourceIDentifier() {
        Assert.assertEquals(R.color.due_profile_blue, Utils.getDueProfileImageResourceIDentifier());
    }

    /*
    public void testProcessOldEvents(){
        Gson gson = new Gson();
        String oldIllnessInfo = "{\"identifiers\":{},\"baseEntityId\":\"d0da64ab-ab8c-4cd8-ab18-e2661009e436\",\"locationId\":\"402ecf03-af72-4c93-b099-e1ce327d815b\",\"eventDate\":\"2019-08-30T22:21:26.660Z\",\"eventType\":\"Child Home Visit\",\"formSubmissionId\":\"2735394b-b799-4cd0-9d2b-6d84d34448d9\",\"providerId\":\"chaone\",\"duration\":0,\"obs\":[{\"fieldType\":\"formsubmissionField\",\"fieldDataType\":\"text\",\"fieldCode\":\"home_visit_id\",\"parentCode\":\"\",\"values\":[\"6f28e369-0db3-44a1-a61f-5b907a98f13e\"],\"set\":[],\"formSubmissionField\":\"home_visit_id\",\"humanReadableValues\":[]},{\"fieldType\":\"formsubmissionField\",\"fieldDataType\":\"text\",\"fieldCode\":\"last_home_visit\",\"parentCode\":\"\",\"values\":[\"1567174886622\"],\"set\":[],\"formSubmissionField\":\"last_home_visit\",\"humanReadableValues\":[]},{\"fieldType\":\"formsubmissionField\",\"fieldDataType\":\"text\",\"fieldCode\":\"singleVaccine\",\"parentCode\":\"\",\"values\":[\"{\\\"singleVaccinesGiven\\\":[]}\"],\"set\":[],\"formSubmissionField\":\"singleVaccine\",\"humanReadableValues\":[]},{\"fieldType\":\"formsubmissionField\",\"fieldDataType\":\"text\",\"fieldCode\":\"groupVaccine\",\"parentCode\":\"\",\"values\":[\"{\\\"groupVaccinesGiven\\\":[]}\"],\"set\":[],\"formSubmissionField\":\"groupVaccine\",\"humanReadableValues\":[]},{\"fieldType\":\"formsubmissionField\",\"fieldDataType\":\"text\",\"fieldCode\":\"vaccineNotGiven\",\"parentCode\":\"\",\"values\":[\"{\\\"vaccineNotGiven\\\":[]}\"],\"set\":[],\"formSubmissionField\":\"vaccineNotGiven\",\"humanReadableValues\":[]},{\"fieldType\":\"formsubmissionField\",\"fieldDataType\":\"text\",\"fieldCode\":\"service\",\"parentCode\":\"\",\"values\":[\"{\\\"Vitamin A\\\":{\\\"alert\\\":{\\\"caseID\\\":\\\"d0da64ab-ab8c-4cd8-ab18-e2661009e436\\\",\\\"completionDate\\\":null,\\\"expiryDate\\\":\\\"2019-09-23\\\",\\\"offline\\\":true,\\\"scheduleName\\\":\\\"Vitamin A1\\\",\\\"startDate\\\":\\\"2015-03-23\\\",\\\"status\\\":\\\"normal\\\",\\\"visitCode\\\":\\\"vitamina1\\\"},\\\"color\\\":null,\\\"createdAt\\\":null,\\\"dbKey\\\":1,\\\"defaultName\\\":\\\"Vitamin A\\\",\\\"dob\\\":\\\"2014-09-23T08:00:00.000+08:00\\\",\\\"gender\\\":\\\"Male\\\",\\\"id\\\":\\\"d0da64ab-ab8c-4cd8-ab18-e2661009e436\\\",\\\"patientName\\\":\\\"Doudou Balde\\\",\\\"patientNumber\\\":\\\"\\\",\\\"photo\\\":{\\\"filePath\\\":null,\\\"resourceId\\\":2131230828},\\\"previousVaccineId\\\":null,\\\"serviceType\\\":{\\\"dateEntity\\\":\\\"encounter\\\",\\\"dateEntityId\\\":\\\"encounter_date\\\",\\\"expiryOffset\\\":\\\"+5y\\\",\\\"id\\\":13,\\\"milestoneOffset\\\":\\\"[+6m]\\\",\\\"name\\\":\\\"Vitamin A1\\\",\\\"preOffset\\\":\\\"+6m\\\",\\\"prerequisite\\\":\\\"dob\\\",\\\"serviceGroup\\\":\\\"child\\\",\\\"serviceLogic\\\":\\\"\\\",\\\"serviceNameEntity\\\":\\\"concept\\\",\\\"serviceNameEntityId\\\":\\\"86339AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\\\",\\\"type\\\":\\\"Vitamin A\\\",\\\"units\\\":null,\\\"updatedAt\\\":1566980540710},\\\"status\\\":\\\"due\\\",\\\"synced\\\":false,\\\"today\\\":false,\\\"updatedVaccineDate\\\":\\\"2019-08-30T22:20:42.718+08:00\\\",\\\"vaccineDate\\\":\\\"2015-03-23T00:00:00.000+08:00\\\",\\\"value\\\":null}}\"],\"set\":[],\"formSubmissionField\":\"service\",\"humanReadableValues\":[]},{\"fieldType\":\"formsubmissionField\",\"fieldDataType\":\"text\",\"fieldCode\":\"serviceNotGiven\",\"parentCode\":\"\",\"values\":[\"{\\\"Deworming\\\":{\\\"alert\\\":{\\\"caseID\\\":\\\"d0da64ab-ab8c-4cd8-ab18-e2661009e436\\\",\\\"completionDate\\\":null,\\\"expiryDate\\\":\\\"2019-09-23\\\",\\\"offline\\\":true,\\\"scheduleName\\\":\\\"Deworming 1\\\",\\\"startDate\\\":\\\"2015-09-23\\\",\\\"status\\\":\\\"normal\\\",\\\"visitCode\\\":\\\"deworming1\\\"},\\\"color\\\":null,\\\"createdAt\\\":null,\\\"dbKey\\\":null,\\\"defaultName\\\":\\\"Deworming\\\",\\\"dob\\\":\\\"2014-09-23T08:00:00.000+08:00\\\",\\\"gender\\\":\\\"Male\\\",\\\"id\\\":\\\"d0da64ab-ab8c-4cd8-ab18-e2661009e436\\\",\\\"patientName\\\":\\\"Doudou Balde\\\",\\\"patientNumber\\\":\\\"\\\",\\\"photo\\\":{\\\"filePath\\\":null,\\\"resourceId\\\":2131230828},\\\"previousVaccineId\\\":null,\\\"serviceType\\\":{\\\"dateEntity\\\":\\\"encounter\\\",\\\"dateEntityId\\\":\\\"encounter_date\\\",\\\"expiryOffset\\\":\\\"+5y\\\",\\\"id\\\":22,\\\"milestoneOffset\\\":\\\"[+1y]\\\",\\\"name\\\":\\\"Deworming 1\\\",\\\"preOffset\\\":\\\"+1y\\\",\\\"prerequisite\\\":\\\"dob\\\",\\\"serviceGroup\\\":\\\"child\\\",\\\"serviceLogic\\\":\\\"\\\",\\\"serviceNameEntity\\\":\\\"concept\\\",\\\"serviceNameEntityId\\\":\\\"159922AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\\\",\\\"type\\\":\\\"Deworming\\\",\\\"units\\\":null,\\\"updatedAt\\\":1566980540721},\\\"status\\\":\\\"due\\\",\\\"synced\\\":false,\\\"today\\\":false,\\\"updatedVaccineDate\\\":null,\\\"vaccineDate\\\":\\\"2015-09-23T00:00:00.000+08:00\\\",\\\"value\\\":null}}\"],\"set\":[],\"formSubmissionField\":\"serviceNotGiven\",\"humanReadableValues\":[]},{\"fieldType\":\"formsubmissionField\",\"fieldDataType\":\"text\",\"fieldCode\":\"birth_certificate\",\"parentCode\":\"\",\"values\":[\"{\\\"birtCert\\\":\\\"{\\\\\\\"count\\\\\\\":\\\\\\\"1\\\\\\\",\\\\\\\"encounter_type\\\\\\\":\\\\\\\"Birth Certification\\\\\\\",\\\\\\\"entity_id\\\\\\\":\\\\\\\"d0da64ab-ab8c-4cd8-ab18-e2661009e436\\\\\\\",\\\\\\\"metadata\\\\\\\":{\\\\\\\"start\\\\\\\":{\\\\\\\"openmrs_entity_parent\\\\\\\":\\\\\\\"\\\\\\\",\\\\\\\"openmrs_entity\\\\\\\":\\\\\\\"concept\\\\\\\",\\\\\\\"openmrs_data_type\\\\\\\":\\\\\\\"start\\\\\\\",\\\\\\\"openmrs_entity_id\\\\\\\":\\\\\\\"163137AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\\\\\\\",\\\\\\\"value\\\\\\\":\\\\\\\"2019-08-30 22:20:17\\\\\\\"},\\\\\\\"end\\\\\\\":{\\\\\\\"openmrs_entity_parent\\\\\\\":\\\\\\\"\\\\\\\",\\\\\\\"openmrs_entity\\\\\\\":\\\\\\\"concept\\\\\\\",\\\\\\\"openmrs_data_type\\\\\\\":\\\\\\\"end\\\\\\\",\\\\\\\"openmrs_entity_id\\\\\\\":\\\\\\\"163138AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\\\\\\\",\\\\\\\"value\\\\\\\":\\\\\\\"2019-08-30 22:20:38\\\\\\\"},\\\\\\\"today\\\\\\\":{\\\\\\\"openmrs_entity_parent\\\\\\\":\\\\\\\"\\\\\\\",\\\\\\\"openmrs_entity\\\\\\\":\\\\\\\"encounter\\\\\\\",\\\\\\\"openmrs_entity_id\\\\\\\":\\\\\\\"encounter_date\\\\\\\",\\\\\\\"value\\\\\\\":\\\\\\\"30-08-2019\\\\\\\"},\\\\\\\"deviceid\\\\\\\":{\\\\\\\"openmrs_entity_parent\\\\\\\":\\\\\\\"\\\\\\\",\\\\\\\"openmrs_entity\\\\\\\":\\\\\\\"concept\\\\\\\",\\\\\\\"openmrs_data_type\\\\\\\":\\\\\\\"deviceid\\\\\\\",\\\\\\\"openmrs_entity_id\\\\\\\":\\\\\\\"163149AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\\\\\\\",\\\\\\\"value\\\\\\\":\\\\\\\"865959046137007\\\\\\\"},\\\\\\\"subscriberid\\\\\\\":{\\\\\\\"openmrs_entity_parent\\\\\\\":\\\\\\\"\\\\\\\",\\\\\\\"openmrs_entity\\\\\\\":\\\\\\\"concept\\\\\\\",\\\\\\\"openmrs_data_type\\\\\\\":\\\\\\\"subscriberid\\\\\\\",\\\\\\\"openmrs_entity_id\\\\\\\":\\\\\\\"163150AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\\\\\\\",\\\\\\\"value\\\\\\\":\\\\\\\"460031208673496\\\\\\\"},\\\\\\\"simserial\\\\\\\":{\\\\\\\"openmrs_entity_parent\\\\\\\":\\\\\\\"\\\\\\\",\\\\\\\"openmrs_entity\\\\\\\":\\\\\\\"concept\\\\\\\",\\\\\\\"openmrs_data_type\\\\\\\":\\\\\\\"simserial\\\\\\\",\\\\\\\"openmrs_entity_id\\\\\\\":\\\\\\\"163151AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\\\\\\\",\\\\\\\"value\\\\\\\":\\\\\\\"89860314107971674978\\\\\\\"},\\\\\\\"phonenumber\\\\\\\":{\\\\\\\"openmrs_entity_parent\\\\\\\":\\\\\\\"\\\\\\\",\\\\\\\"openmrs_entity\\\\\\\":\\\\\\\"concept\\\\\\\",\\\\\\\"openmrs_data_type\\\\\\\":\\\\\\\"phonenumber\\\\\\\",\\\\\\\"openmrs_entity_id\\\\\\\":\\\\\\\"163152AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\\\\\\\",\\\\\\\"value\\\\\\\":\\\\\\\"\\\\\\\"},\\\\\\\"encounter_location\\\\\\\":\\\\\\\"\\\\\\\"},\\\\\\\"step1\\\\\\\":{\\\\\\\"title\\\\\\\":\\\\\\\"Birth Certification\\\\\\\",\\\\\\\"fields\\\\\\\":[{\\\\\\\"key\\\\\\\":\\\\\\\"birth_cert\\\\\\\",\\\\\\\"openmrs_entity_parent\\\\\\\":\\\\\\\"\\\\\\\",\\\\\\\"openmrs_entity\\\\\\\":\\\\\\\"concept\\\\\\\",\\\\\\\"openmrs_entity_id\\\\\\\":\\\\\\\"165406AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\\\\\\\",\\\\\\\"openmrs_data_type\\\\\\\":\\\\\\\"select one\\\\\\\",\\\\\\\"type\\\\\\\":\\\\\\\"spinner\\\\\\\",\\\\\\\"hint\\\\\\\":\\\\\\\"Does the child have a birth certificate?\\\\\\\",\\\\\\\"values\\\\\\\":[\\\\\\\"Yes\\\\\\\",\\\\\\\"No\\\\\\\"],\\\\\\\"openmrs_choice_ids\\\\\\\":{\\\\\\\"Yes\\\\\\\":\\\\\\\"1065AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\\\\\\\",\\\\\\\"No\\\\\\\":\\\\\\\"1066AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\\\\\\\"},\\\\\\\"v_required\\\\\\\":{\\\\\\\"value\\\\\\\":true,\\\\\\\"err\\\\\\\":\\\\\\\"Please select option\\\\\\\"},\\\\\\\"step\\\\\\\":\\\\\\\"step1\\\\\\\",\\\\\\\"is-rule-check\\\\\\\":true,\\\\\\\"value\\\\\\\":\\\\\\\"Yes\\\\\\\"},{\\\\\\\"key\\\\\\\":\\\\\\\"birth_cert_issue_date\\\\\\\",\\\\\\\"openmrs_entity_parent\\\\\\\":\\\\\\\"\\\\\\\",\\\\\\\"openmrs_entity\\\\\\\":\\\\\\\"concept\\\\\\\",\\\\\\\"openmrs_entity_id\\\\\\\":\\\\\\\"164129AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\\\\\\\",\\\\\\\"type\\\\\\\":\\\\\\\"date_picker\\\\\\\",\\\\\\\"hint\\\\\\\":\\\\\\\"Birth certificate issuance date\\\\\\\",\\\\\\\"expanded\\\\\\\":false,\\\\\\\"max_date\\\\\\\":\\\\\\\"today\\\\\\\",\\\\\\\"min_date\\\\\\\":\\\\\\\"today-1802d\\\\\\\",\\\\\\\"v_required\\\\\\\":{\\\\\\\"value\\\\\\\":\\\\\\\"true\\\\\\\",\\\\\\\"err\\\\\\\":\\\\\\\"Please enter Birth certificate issuance date\\\\\\\"},\\\\\\\"relevance\\\\\\\":{\\\\\\\"rules-engine\\\\\\\":{\\\\\\\"ex-rules\\\\\\\":{\\\\\\\"rules-file\\\\\\\":\\\\\\\"birth_certification.yml\\\\\\\"}}},\\\\\\\"is_visible\\\\\\\":true,\\\\\\\"value\\\\\\\":\\\\\\\"30-08-2019\\\\\\\"},{\\\\\\\"key\\\\\\\":\\\\\\\"birth_cert_num\\\\\\\",\\\\\\\"openmrs_entity_parent\\\\\\\":\\\\\\\"\\\\\\\",\\\\\\\"openmrs_entity\\\\\\\":\\\\\\\"concept\\\\\\\",\\\\\\\"openmrs_entity_id\\\\\\\":\\\\\\\"162052AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\\\\\\\",\\\\\\\"openmrs_data_type\\\\\\\":\\\\\\\"text\\\\\\\",\\\\\\\"type\\\\\\\":\\\\\\\"edit_text\\\\\\\",\\\\\\\"hint\\\\\\\":\\\\\\\"Birth certificate number\\\\\\\",\\\\\\\"v_required\\\\\\\":{\\\\\\\"value\\\\\\\":\\\\\\\"true\\\\\\\",\\\\\\\"err\\\\\\\":\\\\\\\"Please enter number\\\\\\\"},\\\\\\\"relevance\\\\\\\":{\\\\\\\"rules-engine\\\\\\\":{\\\\\\\"ex-rules\\\\\\\":{\\\\\\\"rules-file\\\\\\\":\\\\\\\"birth_certification.yml\\\\\\\"}}},\\\\\\\"is_visible\\\\\\\":true,\\\\\\\"value\\\\\\\":\\\\\\\"20150901\\\\\\\"},{\\\\\\\"key\\\\\\\":\\\\\\\"birth_notification\\\\\\\",\\\\\\\"openmrs_entity_parent\\\\\\\":\\\\\\\"\\\\\\\",\\\\\\\"openmrs_entity\\\\\\\":\\\\\\\"concept\\\\\\\",\\\\\\\"openmrs_entity_id\\\\\\\":\\\\\\\"165405AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\\\\\\\",\\\\\\\"openmrs_data_type\\\\\\\":\\\\\\\"select one\\\\\\\",\\\\\\\"type\\\\\\\":\\\\\\\"spinner\\\\\\\",\\\\\\\"hint\\\\\\\":\\\\\\\"Was the birth notification done?\\\\\\\",\\\\\\\"values\\\\\\\":[\\\\\\\"Yes\\\\\\\",\\\\\\\"No\\\\\\\"],\\\\\\\"openmrs_choice_ids\\\\\\\":{\\\\\\\"Yes\\\\\\\":\\\\\\\"1065AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\\\\\\\",\\\\\\\"No\\\\\\\":\\\\\\\"1066AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\\\\\\\"},\\\\\\\"v_required\\\\\\\":{\\\\\\\"value\\\\\\\":false,\\\\\\\"err\\\\\\\":\\\\\\\"Please select option\\\\\\\"},\\\\\\\"relevance\\\\\\\":{\\\\\\\"rules-engine\\\\\\\":{\\\\\\\"ex-rules\\\\\\\":{\\\\\\\"rules-file\\\\\\\":\\\\\\\"birth_certification.yml\\\\\\\"}}},\\\\\\\"is_visible\\\\\\\":false,\\\\\\\"step\\\\\\\":\\\\\\\"step1\\\\\\\",\\\\\\\"is-rule-check\\\\\\\":true},{\\\\\\\"key\\\\\\\":\\\\\\\"birthinstroductions\\\\\\\",\\\\\\\"openmrs_entity_parent\\\\\\\":\\\\\\\"\\\\\\\",\\\\\\\"openmrs_entity\\\\\\\":\\\\\\\"\\\\\\\",\\\\\\\"openmrs_entity_id\\\\\\\":\\\\\\\"\\\\\\\",\\\\\\\"type\\\\\\\":\\\\\\\"label\\\\\\\",\\\\\\\"text\\\\\\\":\\\\\\\"Ask to see the birth notification and instruct the caregiver to register the birth at County Health.\\\\\\\",\\\\\\\"relevance\\\\\\\":{\\\\\\\"rules-engine\\\\\\\":{\\\\\\\"ex-rules\\\\\\\":{\\\\\\\"rules-file\\\\\\\":\\\\\\\"birth_certification.yml\\\\\\\"}}},\\\\\\\"is_visible\\\\\\\":false},{\\\\\\\"key\\\\\\\":\\\\\\\"caregiverinstroductions\\\\\\\",\\\\\\\"openmrs_entity_parent\\\\\\\":\\\\\\\"\\\\\\\",\\\\\\\"openmrs_entity\\\\\\\":\\\\\\\"\\\\\\\",\\\\\\\"openmrs_entity_id\\\\\\\":\\\\\\\"\\\\\\\",\\\\\\\"type\\\\\\\":\\\\\\\"label\\\\\\\",\\\\\\\"text\\\\\\\":\\\\\\\"Instruct the caregiver to have the birth registered at County Health.\\\\\\\",\\\\\\\"relevance\\\\\\\":{\\\\\\\"rules-engine\\\\\\\":{\\\\\\\"ex-rules\\\\\\\":{\\\\\\\"rules-file\\\\\\\":\\\\\\\"birth_certification.yml\\\\\\\"}}},\\\\\\\"is_visible\\\\\\\":false}]}}\\\"}\"],\"set\":[],\"formSubmissionField\":\"birth_certificate\",\"humanReadableValues\":[]},{\"fieldType\":\"formsubmissionField\",\"fieldDataType\":\"text\",\"fieldCode\":\"illness_information\",\"parentCode\":\"\",\"values\":[\"{}\"],\"set\":[],\"formSubmissionField\":\"illness_information\",\"humanReadableValues\":[]}],\"entityType\":\"ec_child\",\"version\":1567174886660,\"teamId\":\"d9eb010a-6d03-4bf8-b57a-b488dedd6f51\",\"team\":\"Clinic A Team\",\"dateCreated\":\"2019-08-30T14:21:50.258Z\",\"serverVersion\":1567174910258,\"clientApplicationVersion\":1,\"clientDatabaseVersion\":9,\"type\":\"Event\",\"id\":\"2e2601e9-e612-4eb5-9ca2-0ea19829fac2\",\"revision\":\"v1\"}";
        String oldServiceEvent = "{\"identifiers\":{},\"baseEntityId\":\"d0da64ab-ab8c-4cd8-ab18-e2661009e436\",\"locationId\":\"402ecf03-af72-4c93-b099-e1ce327d815b\",\"eventDate\":\"2019-08-30T22:21:26.660Z\",\"eventType\":\"Child Home Visit\",\"formSubmissionId\":\"2735394b-b799-4cd0-9d2b-6d84d34448d9\",\"providerId\":\"chaone\",\"duration\":0,\"obs\":[{\"fieldType\":\"formsubmissionField\",\"fieldDataType\":\"text\",\"fieldCode\":\"home_visit_id\",\"parentCode\":\"\",\"values\":[\"6f28e369-0db3-44a1-a61f-5b907a98f13e\"],\"set\":[],\"formSubmissionField\":\"home_visit_id\",\"humanReadableValues\":[]},{\"fieldType\":\"formsubmissionField\",\"fieldDataType\":\"text\",\"fieldCode\":\"last_home_visit\",\"parentCode\":\"\",\"values\":[\"1567174886622\"],\"set\":[],\"formSubmissionField\":\"last_home_visit\",\"humanReadableValues\":[]},{\"fieldType\":\"formsubmissionField\",\"fieldDataType\":\"text\",\"fieldCode\":\"singleVaccine\",\"parentCode\":\"\",\"values\":[\"{\\\"singleVaccinesGiven\\\":[]}\"],\"set\":[],\"formSubmissionField\":\"singleVaccine\",\"humanReadableValues\":[]},{\"fieldType\":\"formsubmissionField\",\"fieldDataType\":\"text\",\"fieldCode\":\"groupVaccine\",\"parentCode\":\"\",\"values\":[\"{\\\"groupVaccinesGiven\\\":[]}\"],\"set\":[],\"formSubmissionField\":\"groupVaccine\",\"humanReadableValues\":[]},{\"fieldType\":\"formsubmissionField\",\"fieldDataType\":\"text\",\"fieldCode\":\"vaccineNotGiven\",\"parentCode\":\"\",\"values\":[\"{\\\"vaccineNotGiven\\\":[]}\"],\"set\":[],\"formSubmissionField\":\"vaccineNotGiven\",\"humanReadableValues\":[]},{\"fieldType\":\"formsubmissionField\",\"fieldDataType\":\"text\",\"fieldCode\":\"service\",\"parentCode\":\"\",\"values\":[\"{\\\"Vitamin A\\\":{\\\"alert\\\":{\\\"caseID\\\":\\\"d0da64ab-ab8c-4cd8-ab18-e2661009e436\\\",\\\"completionDate\\\":null,\\\"expiryDate\\\":\\\"2019-09-23\\\",\\\"offline\\\":true,\\\"scheduleName\\\":\\\"Vitamin A1\\\",\\\"startDate\\\":\\\"2015-03-23\\\",\\\"status\\\":\\\"normal\\\",\\\"visitCode\\\":\\\"vitamina1\\\"},\\\"color\\\":null,\\\"createdAt\\\":null,\\\"dbKey\\\":1,\\\"defaultName\\\":\\\"Vitamin A\\\",\\\"dob\\\":\\\"2014-09-23T08:00:00.000+08:00\\\",\\\"gender\\\":\\\"Male\\\",\\\"id\\\":\\\"d0da64ab-ab8c-4cd8-ab18-e2661009e436\\\",\\\"patientName\\\":\\\"Doudou Balde\\\",\\\"patientNumber\\\":\\\"\\\",\\\"photo\\\":{\\\"filePath\\\":null,\\\"resourceId\\\":2131230828},\\\"previousVaccineId\\\":null,\\\"serviceType\\\":{\\\"dateEntity\\\":\\\"encounter\\\",\\\"dateEntityId\\\":\\\"encounter_date\\\",\\\"expiryOffset\\\":\\\"+5y\\\",\\\"id\\\":13,\\\"milestoneOffset\\\":\\\"[+6m]\\\",\\\"name\\\":\\\"Vitamin A1\\\",\\\"preOffset\\\":\\\"+6m\\\",\\\"prerequisite\\\":\\\"dob\\\",\\\"serviceGroup\\\":\\\"child\\\",\\\"serviceLogic\\\":\\\"\\\",\\\"serviceNameEntity\\\":\\\"concept\\\",\\\"serviceNameEntityId\\\":\\\"86339AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\\\",\\\"type\\\":\\\"Vitamin A\\\",\\\"units\\\":null,\\\"updatedAt\\\":1566980540710},\\\"status\\\":\\\"due\\\",\\\"synced\\\":false,\\\"today\\\":false,\\\"updatedVaccineDate\\\":\\\"2019-08-30T22:20:42.718+08:00\\\",\\\"vaccineDate\\\":\\\"2015-03-23T00:00:00.000+08:00\\\",\\\"value\\\":null}}\"],\"set\":[],\"formSubmissionField\":\"service\",\"humanReadableValues\":[]},{\"fieldType\":\"formsubmissionField\",\"fieldDataType\":\"text\",\"fieldCode\":\"serviceNotGiven\",\"parentCode\":\"\",\"values\":[\"{\\\"Deworming\\\":{\\\"alert\\\":{\\\"caseID\\\":\\\"d0da64ab-ab8c-4cd8-ab18-e2661009e436\\\",\\\"completionDate\\\":null,\\\"expiryDate\\\":\\\"2019-09-23\\\",\\\"offline\\\":true,\\\"scheduleName\\\":\\\"Deworming 1\\\",\\\"startDate\\\":\\\"2015-09-23\\\",\\\"status\\\":\\\"normal\\\",\\\"visitCode\\\":\\\"deworming1\\\"},\\\"color\\\":null,\\\"createdAt\\\":null,\\\"dbKey\\\":null,\\\"defaultName\\\":\\\"Deworming\\\",\\\"dob\\\":\\\"2014-09-23T08:00:00.000+08:00\\\",\\\"gender\\\":\\\"Male\\\",\\\"id\\\":\\\"d0da64ab-ab8c-4cd8-ab18-e2661009e436\\\",\\\"patientName\\\":\\\"Doudou Balde\\\",\\\"patientNumber\\\":\\\"\\\",\\\"photo\\\":{\\\"filePath\\\":null,\\\"resourceId\\\":2131230828},\\\"previousVaccineId\\\":null,\\\"serviceType\\\":{\\\"dateEntity\\\":\\\"encounter\\\",\\\"dateEntityId\\\":\\\"encounter_date\\\",\\\"expiryOffset\\\":\\\"+5y\\\",\\\"id\\\":22,\\\"milestoneOffset\\\":\\\"[+1y]\\\",\\\"name\\\":\\\"Deworming 1\\\",\\\"preOffset\\\":\\\"+1y\\\",\\\"prerequisite\\\":\\\"dob\\\",\\\"serviceGroup\\\":\\\"child\\\",\\\"serviceLogic\\\":\\\"\\\",\\\"serviceNameEntity\\\":\\\"concept\\\",\\\"serviceNameEntityId\\\":\\\"159922AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\\\",\\\"type\\\":\\\"Deworming\\\",\\\"units\\\":null,\\\"updatedAt\\\":1566980540721},\\\"status\\\":\\\"due\\\",\\\"synced\\\":false,\\\"today\\\":false,\\\"updatedVaccineDate\\\":null,\\\"vaccineDate\\\":\\\"2015-09-23T00:00:00.000+08:00\\\",\\\"value\\\":null}}\"],\"set\":[],\"formSubmissionField\":\"serviceNotGiven\",\"humanReadableValues\":[]},{\"fieldType\":\"formsubmissionField\",\"fieldDataType\":\"text\",\"fieldCode\":\"birth_certificate\",\"parentCode\":\"\",\"values\":[\"{\\\"birtCert\\\":\\\"{\\\\\\\"count\\\\\\\":\\\\\\\"1\\\\\\\",\\\\\\\"encounter_type\\\\\\\":\\\\\\\"Birth Certification\\\\\\\",\\\\\\\"entity_id\\\\\\\":\\\\\\\"d0da64ab-ab8c-4cd8-ab18-e2661009e436\\\\\\\",\\\\\\\"metadata\\\\\\\":{\\\\\\\"start\\\\\\\":{\\\\\\\"openmrs_entity_parent\\\\\\\":\\\\\\\"\\\\\\\",\\\\\\\"openmrs_entity\\\\\\\":\\\\\\\"concept\\\\\\\",\\\\\\\"openmrs_data_type\\\\\\\":\\\\\\\"start\\\\\\\",\\\\\\\"openmrs_entity_id\\\\\\\":\\\\\\\"163137AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\\\\\\\",\\\\\\\"value\\\\\\\":\\\\\\\"2019-08-30 22:20:17\\\\\\\"},\\\\\\\"end\\\\\\\":{\\\\\\\"openmrs_entity_parent\\\\\\\":\\\\\\\"\\\\\\\",\\\\\\\"openmrs_entity\\\\\\\":\\\\\\\"concept\\\\\\\",\\\\\\\"openmrs_data_type\\\\\\\":\\\\\\\"end\\\\\\\",\\\\\\\"openmrs_entity_id\\\\\\\":\\\\\\\"163138AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\\\\\\\",\\\\\\\"value\\\\\\\":\\\\\\\"2019-08-30 22:20:38\\\\\\\"},\\\\\\\"today\\\\\\\":{\\\\\\\"openmrs_entity_parent\\\\\\\":\\\\\\\"\\\\\\\",\\\\\\\"openmrs_entity\\\\\\\":\\\\\\\"encounter\\\\\\\",\\\\\\\"openmrs_entity_id\\\\\\\":\\\\\\\"encounter_date\\\\\\\",\\\\\\\"value\\\\\\\":\\\\\\\"30-08-2019\\\\\\\"},\\\\\\\"deviceid\\\\\\\":{\\\\\\\"openmrs_entity_parent\\\\\\\":\\\\\\\"\\\\\\\",\\\\\\\"openmrs_entity\\\\\\\":\\\\\\\"concept\\\\\\\",\\\\\\\"openmrs_data_type\\\\\\\":\\\\\\\"deviceid\\\\\\\",\\\\\\\"openmrs_entity_id\\\\\\\":\\\\\\\"163149AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\\\\\\\",\\\\\\\"value\\\\\\\":\\\\\\\"865959046137007\\\\\\\"},\\\\\\\"subscriberid\\\\\\\":{\\\\\\\"openmrs_entity_parent\\\\\\\":\\\\\\\"\\\\\\\",\\\\\\\"openmrs_entity\\\\\\\":\\\\\\\"concept\\\\\\\",\\\\\\\"openmrs_data_type\\\\\\\":\\\\\\\"subscriberid\\\\\\\",\\\\\\\"openmrs_entity_id\\\\\\\":\\\\\\\"163150AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\\\\\\\",\\\\\\\"value\\\\\\\":\\\\\\\"460031208673496\\\\\\\"},\\\\\\\"simserial\\\\\\\":{\\\\\\\"openmrs_entity_parent\\\\\\\":\\\\\\\"\\\\\\\",\\\\\\\"openmrs_entity\\\\\\\":\\\\\\\"concept\\\\\\\",\\\\\\\"openmrs_data_type\\\\\\\":\\\\\\\"simserial\\\\\\\",\\\\\\\"openmrs_entity_id\\\\\\\":\\\\\\\"163151AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\\\\\\\",\\\\\\\"value\\\\\\\":\\\\\\\"89860314107971674978\\\\\\\"},\\\\\\\"phonenumber\\\\\\\":{\\\\\\\"openmrs_entity_parent\\\\\\\":\\\\\\\"\\\\\\\",\\\\\\\"openmrs_entity\\\\\\\":\\\\\\\"concept\\\\\\\",\\\\\\\"openmrs_data_type\\\\\\\":\\\\\\\"phonenumber\\\\\\\",\\\\\\\"openmrs_entity_id\\\\\\\":\\\\\\\"163152AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\\\\\\\",\\\\\\\"value\\\\\\\":\\\\\\\"\\\\\\\"},\\\\\\\"encounter_location\\\\\\\":\\\\\\\"\\\\\\\"},\\\\\\\"step1\\\\\\\":{\\\\\\\"title\\\\\\\":\\\\\\\"Birth Certification\\\\\\\",\\\\\\\"fields\\\\\\\":[{\\\\\\\"key\\\\\\\":\\\\\\\"birth_cert\\\\\\\",\\\\\\\"openmrs_entity_parent\\\\\\\":\\\\\\\"\\\\\\\",\\\\\\\"openmrs_entity\\\\\\\":\\\\\\\"concept\\\\\\\",\\\\\\\"openmrs_entity_id\\\\\\\":\\\\\\\"165406AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\\\\\\\",\\\\\\\"openmrs_data_type\\\\\\\":\\\\\\\"select one\\\\\\\",\\\\\\\"type\\\\\\\":\\\\\\\"spinner\\\\\\\",\\\\\\\"hint\\\\\\\":\\\\\\\"Does the child have a birth certificate?\\\\\\\",\\\\\\\"values\\\\\\\":[\\\\\\\"Yes\\\\\\\",\\\\\\\"No\\\\\\\"],\\\\\\\"openmrs_choice_ids\\\\\\\":{\\\\\\\"Yes\\\\\\\":\\\\\\\"1065AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\\\\\\\",\\\\\\\"No\\\\\\\":\\\\\\\"1066AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\\\\\\\"},\\\\\\\"v_required\\\\\\\":{\\\\\\\"value\\\\\\\":true,\\\\\\\"err\\\\\\\":\\\\\\\"Please select option\\\\\\\"},\\\\\\\"step\\\\\\\":\\\\\\\"step1\\\\\\\",\\\\\\\"is-rule-check\\\\\\\":true,\\\\\\\"value\\\\\\\":\\\\\\\"Yes\\\\\\\"},{\\\\\\\"key\\\\\\\":\\\\\\\"birth_cert_issue_date\\\\\\\",\\\\\\\"openmrs_entity_parent\\\\\\\":\\\\\\\"\\\\\\\",\\\\\\\"openmrs_entity\\\\\\\":\\\\\\\"concept\\\\\\\",\\\\\\\"openmrs_entity_id\\\\\\\":\\\\\\\"164129AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\\\\\\\",\\\\\\\"type\\\\\\\":\\\\\\\"date_picker\\\\\\\",\\\\\\\"hint\\\\\\\":\\\\\\\"Birth certificate issuance date\\\\\\\",\\\\\\\"expanded\\\\\\\":false,\\\\\\\"max_date\\\\\\\":\\\\\\\"today\\\\\\\",\\\\\\\"min_date\\\\\\\":\\\\\\\"today-1802d\\\\\\\",\\\\\\\"v_required\\\\\\\":{\\\\\\\"value\\\\\\\":\\\\\\\"true\\\\\\\",\\\\\\\"err\\\\\\\":\\\\\\\"Please enter Birth certificate issuance date\\\\\\\"},\\\\\\\"relevance\\\\\\\":{\\\\\\\"rules-engine\\\\\\\":{\\\\\\\"ex-rules\\\\\\\":{\\\\\\\"rules-file\\\\\\\":\\\\\\\"birth_certification.yml\\\\\\\"}}},\\\\\\\"is_visible\\\\\\\":true,\\\\\\\"value\\\\\\\":\\\\\\\"30-08-2019\\\\\\\"},{\\\\\\\"key\\\\\\\":\\\\\\\"birth_cert_num\\\\\\\",\\\\\\\"openmrs_entity_parent\\\\\\\":\\\\\\\"\\\\\\\",\\\\\\\"openmrs_entity\\\\\\\":\\\\\\\"concept\\\\\\\",\\\\\\\"openmrs_entity_id\\\\\\\":\\\\\\\"162052AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\\\\\\\",\\\\\\\"openmrs_data_type\\\\\\\":\\\\\\\"text\\\\\\\",\\\\\\\"type\\\\\\\":\\\\\\\"edit_text\\\\\\\",\\\\\\\"hint\\\\\\\":\\\\\\\"Birth certificate number\\\\\\\",\\\\\\\"v_required\\\\\\\":{\\\\\\\"value\\\\\\\":\\\\\\\"true\\\\\\\",\\\\\\\"err\\\\\\\":\\\\\\\"Please enter number\\\\\\\"},\\\\\\\"relevance\\\\\\\":{\\\\\\\"rules-engine\\\\\\\":{\\\\\\\"ex-rules\\\\\\\":{\\\\\\\"rules-file\\\\\\\":\\\\\\\"birth_certification.yml\\\\\\\"}}},\\\\\\\"is_visible\\\\\\\":true,\\\\\\\"value\\\\\\\":\\\\\\\"20150901\\\\\\\"},{\\\\\\\"key\\\\\\\":\\\\\\\"birth_notification\\\\\\\",\\\\\\\"openmrs_entity_parent\\\\\\\":\\\\\\\"\\\\\\\",\\\\\\\"openmrs_entity\\\\\\\":\\\\\\\"concept\\\\\\\",\\\\\\\"openmrs_entity_id\\\\\\\":\\\\\\\"165405AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\\\\\\\",\\\\\\\"openmrs_data_type\\\\\\\":\\\\\\\"select one\\\\\\\",\\\\\\\"type\\\\\\\":\\\\\\\"spinner\\\\\\\",\\\\\\\"hint\\\\\\\":\\\\\\\"Was the birth notification done?\\\\\\\",\\\\\\\"values\\\\\\\":[\\\\\\\"Yes\\\\\\\",\\\\\\\"No\\\\\\\"],\\\\\\\"openmrs_choice_ids\\\\\\\":{\\\\\\\"Yes\\\\\\\":\\\\\\\"1065AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\\\\\\\",\\\\\\\"No\\\\\\\":\\\\\\\"1066AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\\\\\\\"},\\\\\\\"v_required\\\\\\\":{\\\\\\\"value\\\\\\\":false,\\\\\\\"err\\\\\\\":\\\\\\\"Please select option\\\\\\\"},\\\\\\\"relevance\\\\\\\":{\\\\\\\"rules-engine\\\\\\\":{\\\\\\\"ex-rules\\\\\\\":{\\\\\\\"rules-file\\\\\\\":\\\\\\\"birth_certification.yml\\\\\\\"}}},\\\\\\\"is_visible\\\\\\\":false,\\\\\\\"step\\\\\\\":\\\\\\\"step1\\\\\\\",\\\\\\\"is-rule-check\\\\\\\":true},{\\\\\\\"key\\\\\\\":\\\\\\\"birthinstroductions\\\\\\\",\\\\\\\"openmrs_entity_parent\\\\\\\":\\\\\\\"\\\\\\\",\\\\\\\"openmrs_entity\\\\\\\":\\\\\\\"\\\\\\\",\\\\\\\"openmrs_entity_id\\\\\\\":\\\\\\\"\\\\\\\",\\\\\\\"type\\\\\\\":\\\\\\\"label\\\\\\\",\\\\\\\"text\\\\\\\":\\\\\\\"Ask to see the birth notification and instruct the caregiver to register the birth at County Health.\\\\\\\",\\\\\\\"relevance\\\\\\\":{\\\\\\\"rules-engine\\\\\\\":{\\\\\\\"ex-rules\\\\\\\":{\\\\\\\"rules-file\\\\\\\":\\\\\\\"birth_certification.yml\\\\\\\"}}},\\\\\\\"is_visible\\\\\\\":false},{\\\\\\\"key\\\\\\\":\\\\\\\"caregiverinstroductions\\\\\\\",\\\\\\\"openmrs_entity_parent\\\\\\\":\\\\\\\"\\\\\\\",\\\\\\\"openmrs_entity\\\\\\\":\\\\\\\"\\\\\\\",\\\\\\\"openmrs_entity_id\\\\\\\":\\\\\\\"\\\\\\\",\\\\\\\"type\\\\\\\":\\\\\\\"label\\\\\\\",\\\\\\\"text\\\\\\\":\\\\\\\"Instruct the caregiver to have the birth registered at County Health.\\\\\\\",\\\\\\\"relevance\\\\\\\":{\\\\\\\"rules-engine\\\\\\\":{\\\\\\\"ex-rules\\\\\\\":{\\\\\\\"rules-file\\\\\\\":\\\\\\\"birth_certification.yml\\\\\\\"}}},\\\\\\\"is_visible\\\\\\\":false}]}}\\\"}\"],\"set\":[],\"formSubmissionField\":\"birth_certificate\",\"humanReadableValues\":[]},{\"fieldType\":\"formsubmissionField\",\"fieldDataType\":\"text\",\"fieldCode\":\"illness_information\",\"parentCode\":\"\",\"values\":[\"{}\"],\"set\":[],\"formSubmissionField\":\"illness_information\",\"humanReadableValues\":[]}],\"entityType\":\"ec_child\",\"version\":1567174886660,\"teamId\":\"d9eb010a-6d03-4bf8-b57a-b488dedd6f51\",\"team\":\"Clinic A Team\",\"dateCreated\":\"2019-08-30T14:21:50.258Z\",\"serverVersion\":1567174910258,\"clientApplicationVersion\":1,\"clientDatabaseVersion\":9,\"type\":\"Event\",\"id\":\"2e2601e9-e612-4eb5-9ca2-0ea19829fac2\",\"revision\":\"v1\"}";
        String oldBirthCertEvent = "{\"identifiers\":{},\"baseEntityId\":\"d0da64ab-ab8c-4cd8-ab18-e2661009e436\",\"locationId\":\"402ecf03-af72-4c93-b099-e1ce327d815b\",\"eventDate\":\"2019-08-30T22:21:26.660Z\",\"eventType\":\"Child Home Visit\",\"formSubmissionId\":\"2735394b-b799-4cd0-9d2b-6d84d34448d9\",\"providerId\":\"chaone\",\"duration\":0,\"obs\":[{\"fieldType\":\"formsubmissionField\",\"fieldDataType\":\"text\",\"fieldCode\":\"home_visit_id\",\"parentCode\":\"\",\"values\":[\"6f28e369-0db3-44a1-a61f-5b907a98f13e\"],\"set\":[],\"formSubmissionField\":\"home_visit_id\",\"humanReadableValues\":[]},{\"fieldType\":\"formsubmissionField\",\"fieldDataType\":\"text\",\"fieldCode\":\"last_home_visit\",\"parentCode\":\"\",\"values\":[\"1567174886622\"],\"set\":[],\"formSubmissionField\":\"last_home_visit\",\"humanReadableValues\":[]},{\"fieldType\":\"formsubmissionField\",\"fieldDataType\":\"text\",\"fieldCode\":\"singleVaccine\",\"parentCode\":\"\",\"values\":[\"{\\\"singleVaccinesGiven\\\":[]}\"],\"set\":[],\"formSubmissionField\":\"singleVaccine\",\"humanReadableValues\":[]},{\"fieldType\":\"formsubmissionField\",\"fieldDataType\":\"text\",\"fieldCode\":\"groupVaccine\",\"parentCode\":\"\",\"values\":[\"{\\\"groupVaccinesGiven\\\":[]}\"],\"set\":[],\"formSubmissionField\":\"groupVaccine\",\"humanReadableValues\":[]},{\"fieldType\":\"formsubmissionField\",\"fieldDataType\":\"text\",\"fieldCode\":\"vaccineNotGiven\",\"parentCode\":\"\",\"values\":[\"{\\\"vaccineNotGiven\\\":[]}\"],\"set\":[],\"formSubmissionField\":\"vaccineNotGiven\",\"humanReadableValues\":[]},{\"fieldType\":\"formsubmissionField\",\"fieldDataType\":\"text\",\"fieldCode\":\"service\",\"parentCode\":\"\",\"values\":[\"{\\\"Vitamin A\\\":{\\\"alert\\\":{\\\"caseID\\\":\\\"d0da64ab-ab8c-4cd8-ab18-e2661009e436\\\",\\\"completionDate\\\":null,\\\"expiryDate\\\":\\\"2019-09-23\\\",\\\"offline\\\":true,\\\"scheduleName\\\":\\\"Vitamin A1\\\",\\\"startDate\\\":\\\"2015-03-23\\\",\\\"status\\\":\\\"normal\\\",\\\"visitCode\\\":\\\"vitamina1\\\"},\\\"color\\\":null,\\\"createdAt\\\":null,\\\"dbKey\\\":1,\\\"defaultName\\\":\\\"Vitamin A\\\",\\\"dob\\\":\\\"2014-09-23T08:00:00.000+08:00\\\",\\\"gender\\\":\\\"Male\\\",\\\"id\\\":\\\"d0da64ab-ab8c-4cd8-ab18-e2661009e436\\\",\\\"patientName\\\":\\\"Doudou Balde\\\",\\\"patientNumber\\\":\\\"\\\",\\\"photo\\\":{\\\"filePath\\\":null,\\\"resourceId\\\":2131230828},\\\"previousVaccineId\\\":null,\\\"serviceType\\\":{\\\"dateEntity\\\":\\\"encounter\\\",\\\"dateEntityId\\\":\\\"encounter_date\\\",\\\"expiryOffset\\\":\\\"+5y\\\",\\\"id\\\":13,\\\"milestoneOffset\\\":\\\"[+6m]\\\",\\\"name\\\":\\\"Vitamin A1\\\",\\\"preOffset\\\":\\\"+6m\\\",\\\"prerequisite\\\":\\\"dob\\\",\\\"serviceGroup\\\":\\\"child\\\",\\\"serviceLogic\\\":\\\"\\\",\\\"serviceNameEntity\\\":\\\"concept\\\",\\\"serviceNameEntityId\\\":\\\"86339AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\\\",\\\"type\\\":\\\"Vitamin A\\\",\\\"units\\\":null,\\\"updatedAt\\\":1566980540710},\\\"status\\\":\\\"due\\\",\\\"synced\\\":false,\\\"today\\\":false,\\\"updatedVaccineDate\\\":\\\"2019-08-30T22:20:42.718+08:00\\\",\\\"vaccineDate\\\":\\\"2015-03-23T00:00:00.000+08:00\\\",\\\"value\\\":null}}\"],\"set\":[],\"formSubmissionField\":\"service\",\"humanReadableValues\":[]},{\"fieldType\":\"formsubmissionField\",\"fieldDataType\":\"text\",\"fieldCode\":\"serviceNotGiven\",\"parentCode\":\"\",\"values\":[\"{\\\"Deworming\\\":{\\\"alert\\\":{\\\"caseID\\\":\\\"d0da64ab-ab8c-4cd8-ab18-e2661009e436\\\",\\\"completionDate\\\":null,\\\"expiryDate\\\":\\\"2019-09-23\\\",\\\"offline\\\":true,\\\"scheduleName\\\":\\\"Deworming 1\\\",\\\"startDate\\\":\\\"2015-09-23\\\",\\\"status\\\":\\\"normal\\\",\\\"visitCode\\\":\\\"deworming1\\\"},\\\"color\\\":null,\\\"createdAt\\\":null,\\\"dbKey\\\":null,\\\"defaultName\\\":\\\"Deworming\\\",\\\"dob\\\":\\\"2014-09-23T08:00:00.000+08:00\\\",\\\"gender\\\":\\\"Male\\\",\\\"id\\\":\\\"d0da64ab-ab8c-4cd8-ab18-e2661009e436\\\",\\\"patientName\\\":\\\"Doudou Balde\\\",\\\"patientNumber\\\":\\\"\\\",\\\"photo\\\":{\\\"filePath\\\":null,\\\"resourceId\\\":2131230828},\\\"previousVaccineId\\\":null,\\\"serviceType\\\":{\\\"dateEntity\\\":\\\"encounter\\\",\\\"dateEntityId\\\":\\\"encounter_date\\\",\\\"expiryOffset\\\":\\\"+5y\\\",\\\"id\\\":22,\\\"milestoneOffset\\\":\\\"[+1y]\\\",\\\"name\\\":\\\"Deworming 1\\\",\\\"preOffset\\\":\\\"+1y\\\",\\\"prerequisite\\\":\\\"dob\\\",\\\"serviceGroup\\\":\\\"child\\\",\\\"serviceLogic\\\":\\\"\\\",\\\"serviceNameEntity\\\":\\\"concept\\\",\\\"serviceNameEntityId\\\":\\\"159922AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\\\",\\\"type\\\":\\\"Deworming\\\",\\\"units\\\":null,\\\"updatedAt\\\":1566980540721},\\\"status\\\":\\\"due\\\",\\\"synced\\\":false,\\\"today\\\":false,\\\"updatedVaccineDate\\\":null,\\\"vaccineDate\\\":\\\"2015-09-23T00:00:00.000+08:00\\\",\\\"value\\\":null}}\"],\"set\":[],\"formSubmissionField\":\"serviceNotGiven\",\"humanReadableValues\":[]},{\"fieldType\":\"formsubmissionField\",\"fieldDataType\":\"text\",\"fieldCode\":\"birth_certificate\",\"parentCode\":\"\",\"values\":[\"{\\\"birtCert\\\":\\\"{\\\\\\\"count\\\\\\\":\\\\\\\"1\\\\\\\",\\\\\\\"encounter_type\\\\\\\":\\\\\\\"Birth Certification\\\\\\\",\\\\\\\"entity_id\\\\\\\":\\\\\\\"d0da64ab-ab8c-4cd8-ab18-e2661009e436\\\\\\\",\\\\\\\"metadata\\\\\\\":{\\\\\\\"start\\\\\\\":{\\\\\\\"openmrs_entity_parent\\\\\\\":\\\\\\\"\\\\\\\",\\\\\\\"openmrs_entity\\\\\\\":\\\\\\\"concept\\\\\\\",\\\\\\\"openmrs_data_type\\\\\\\":\\\\\\\"start\\\\\\\",\\\\\\\"openmrs_entity_id\\\\\\\":\\\\\\\"163137AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\\\\\\\",\\\\\\\"value\\\\\\\":\\\\\\\"2019-08-30 22:20:17\\\\\\\"},\\\\\\\"end\\\\\\\":{\\\\\\\"openmrs_entity_parent\\\\\\\":\\\\\\\"\\\\\\\",\\\\\\\"openmrs_entity\\\\\\\":\\\\\\\"concept\\\\\\\",\\\\\\\"openmrs_data_type\\\\\\\":\\\\\\\"end\\\\\\\",\\\\\\\"openmrs_entity_id\\\\\\\":\\\\\\\"163138AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\\\\\\\",\\\\\\\"value\\\\\\\":\\\\\\\"2019-08-30 22:20:38\\\\\\\"},\\\\\\\"today\\\\\\\":{\\\\\\\"openmrs_entity_parent\\\\\\\":\\\\\\\"\\\\\\\",\\\\\\\"openmrs_entity\\\\\\\":\\\\\\\"encounter\\\\\\\",\\\\\\\"openmrs_entity_id\\\\\\\":\\\\\\\"encounter_date\\\\\\\",\\\\\\\"value\\\\\\\":\\\\\\\"30-08-2019\\\\\\\"},\\\\\\\"deviceid\\\\\\\":{\\\\\\\"openmrs_entity_parent\\\\\\\":\\\\\\\"\\\\\\\",\\\\\\\"openmrs_entity\\\\\\\":\\\\\\\"concept\\\\\\\",\\\\\\\"openmrs_data_type\\\\\\\":\\\\\\\"deviceid\\\\\\\",\\\\\\\"openmrs_entity_id\\\\\\\":\\\\\\\"163149AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\\\\\\\",\\\\\\\"value\\\\\\\":\\\\\\\"865959046137007\\\\\\\"},\\\\\\\"subscriberid\\\\\\\":{\\\\\\\"openmrs_entity_parent\\\\\\\":\\\\\\\"\\\\\\\",\\\\\\\"openmrs_entity\\\\\\\":\\\\\\\"concept\\\\\\\",\\\\\\\"openmrs_data_type\\\\\\\":\\\\\\\"subscriberid\\\\\\\",\\\\\\\"openmrs_entity_id\\\\\\\":\\\\\\\"163150AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\\\\\\\",\\\\\\\"value\\\\\\\":\\\\\\\"460031208673496\\\\\\\"},\\\\\\\"simserial\\\\\\\":{\\\\\\\"openmrs_entity_parent\\\\\\\":\\\\\\\"\\\\\\\",\\\\\\\"openmrs_entity\\\\\\\":\\\\\\\"concept\\\\\\\",\\\\\\\"openmrs_data_type\\\\\\\":\\\\\\\"simserial\\\\\\\",\\\\\\\"openmrs_entity_id\\\\\\\":\\\\\\\"163151AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\\\\\\\",\\\\\\\"value\\\\\\\":\\\\\\\"89860314107971674978\\\\\\\"},\\\\\\\"phonenumber\\\\\\\":{\\\\\\\"openmrs_entity_parent\\\\\\\":\\\\\\\"\\\\\\\",\\\\\\\"openmrs_entity\\\\\\\":\\\\\\\"concept\\\\\\\",\\\\\\\"openmrs_data_type\\\\\\\":\\\\\\\"phonenumber\\\\\\\",\\\\\\\"openmrs_entity_id\\\\\\\":\\\\\\\"163152AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\\\\\\\",\\\\\\\"value\\\\\\\":\\\\\\\"\\\\\\\"},\\\\\\\"encounter_location\\\\\\\":\\\\\\\"\\\\\\\"},\\\\\\\"step1\\\\\\\":{\\\\\\\"title\\\\\\\":\\\\\\\"Birth Certification\\\\\\\",\\\\\\\"fields\\\\\\\":[{\\\\\\\"key\\\\\\\":\\\\\\\"birth_cert\\\\\\\",\\\\\\\"openmrs_entity_parent\\\\\\\":\\\\\\\"\\\\\\\",\\\\\\\"openmrs_entity\\\\\\\":\\\\\\\"concept\\\\\\\",\\\\\\\"openmrs_entity_id\\\\\\\":\\\\\\\"165406AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\\\\\\\",\\\\\\\"openmrs_data_type\\\\\\\":\\\\\\\"select one\\\\\\\",\\\\\\\"type\\\\\\\":\\\\\\\"spinner\\\\\\\",\\\\\\\"hint\\\\\\\":\\\\\\\"Does the child have a birth certificate?\\\\\\\",\\\\\\\"values\\\\\\\":[\\\\\\\"Yes\\\\\\\",\\\\\\\"No\\\\\\\"],\\\\\\\"openmrs_choice_ids\\\\\\\":{\\\\\\\"Yes\\\\\\\":\\\\\\\"1065AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\\\\\\\",\\\\\\\"No\\\\\\\":\\\\\\\"1066AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\\\\\\\"},\\\\\\\"v_required\\\\\\\":{\\\\\\\"value\\\\\\\":true,\\\\\\\"err\\\\\\\":\\\\\\\"Please select option\\\\\\\"},\\\\\\\"step\\\\\\\":\\\\\\\"step1\\\\\\\",\\\\\\\"is-rule-check\\\\\\\":true,\\\\\\\"value\\\\\\\":\\\\\\\"Yes\\\\\\\"},{\\\\\\\"key\\\\\\\":\\\\\\\"birth_cert_issue_date\\\\\\\",\\\\\\\"openmrs_entity_parent\\\\\\\":\\\\\\\"\\\\\\\",\\\\\\\"openmrs_entity\\\\\\\":\\\\\\\"concept\\\\\\\",\\\\\\\"openmrs_entity_id\\\\\\\":\\\\\\\"164129AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\\\\\\\",\\\\\\\"type\\\\\\\":\\\\\\\"date_picker\\\\\\\",\\\\\\\"hint\\\\\\\":\\\\\\\"Birth certificate issuance date\\\\\\\",\\\\\\\"expanded\\\\\\\":false,\\\\\\\"max_date\\\\\\\":\\\\\\\"today\\\\\\\",\\\\\\\"min_date\\\\\\\":\\\\\\\"today-1802d\\\\\\\",\\\\\\\"v_required\\\\\\\":{\\\\\\\"value\\\\\\\":\\\\\\\"true\\\\\\\",\\\\\\\"err\\\\\\\":\\\\\\\"Please enter Birth certificate issuance date\\\\\\\"},\\\\\\\"relevance\\\\\\\":{\\\\\\\"rules-engine\\\\\\\":{\\\\\\\"ex-rules\\\\\\\":{\\\\\\\"rules-file\\\\\\\":\\\\\\\"birth_certification.yml\\\\\\\"}}},\\\\\\\"is_visible\\\\\\\":true,\\\\\\\"value\\\\\\\":\\\\\\\"30-08-2019\\\\\\\"},{\\\\\\\"key\\\\\\\":\\\\\\\"birth_cert_num\\\\\\\",\\\\\\\"openmrs_entity_parent\\\\\\\":\\\\\\\"\\\\\\\",\\\\\\\"openmrs_entity\\\\\\\":\\\\\\\"concept\\\\\\\",\\\\\\\"openmrs_entity_id\\\\\\\":\\\\\\\"162052AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\\\\\\\",\\\\\\\"openmrs_data_type\\\\\\\":\\\\\\\"text\\\\\\\",\\\\\\\"type\\\\\\\":\\\\\\\"edit_text\\\\\\\",\\\\\\\"hint\\\\\\\":\\\\\\\"Birth certificate number\\\\\\\",\\\\\\\"v_required\\\\\\\":{\\\\\\\"value\\\\\\\":\\\\\\\"true\\\\\\\",\\\\\\\"err\\\\\\\":\\\\\\\"Please enter number\\\\\\\"},\\\\\\\"relevance\\\\\\\":{\\\\\\\"rules-engine\\\\\\\":{\\\\\\\"ex-rules\\\\\\\":{\\\\\\\"rules-file\\\\\\\":\\\\\\\"birth_certification.yml\\\\\\\"}}},\\\\\\\"is_visible\\\\\\\":true,\\\\\\\"value\\\\\\\":\\\\\\\"20150901\\\\\\\"},{\\\\\\\"key\\\\\\\":\\\\\\\"birth_notification\\\\\\\",\\\\\\\"openmrs_entity_parent\\\\\\\":\\\\\\\"\\\\\\\",\\\\\\\"openmrs_entity\\\\\\\":\\\\\\\"concept\\\\\\\",\\\\\\\"openmrs_entity_id\\\\\\\":\\\\\\\"165405AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\\\\\\\",\\\\\\\"openmrs_data_type\\\\\\\":\\\\\\\"select one\\\\\\\",\\\\\\\"type\\\\\\\":\\\\\\\"spinner\\\\\\\",\\\\\\\"hint\\\\\\\":\\\\\\\"Was the birth notification done?\\\\\\\",\\\\\\\"values\\\\\\\":[\\\\\\\"Yes\\\\\\\",\\\\\\\"No\\\\\\\"],\\\\\\\"openmrs_choice_ids\\\\\\\":{\\\\\\\"Yes\\\\\\\":\\\\\\\"1065AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\\\\\\\",\\\\\\\"No\\\\\\\":\\\\\\\"1066AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\\\\\\\"},\\\\\\\"v_required\\\\\\\":{\\\\\\\"value\\\\\\\":false,\\\\\\\"err\\\\\\\":\\\\\\\"Please select option\\\\\\\"},\\\\\\\"relevance\\\\\\\":{\\\\\\\"rules-engine\\\\\\\":{\\\\\\\"ex-rules\\\\\\\":{\\\\\\\"rules-file\\\\\\\":\\\\\\\"birth_certification.yml\\\\\\\"}}},\\\\\\\"is_visible\\\\\\\":false,\\\\\\\"step\\\\\\\":\\\\\\\"step1\\\\\\\",\\\\\\\"is-rule-check\\\\\\\":true},{\\\\\\\"key\\\\\\\":\\\\\\\"birthinstroductions\\\\\\\",\\\\\\\"openmrs_entity_parent\\\\\\\":\\\\\\\"\\\\\\\",\\\\\\\"openmrs_entity\\\\\\\":\\\\\\\"\\\\\\\",\\\\\\\"openmrs_entity_id\\\\\\\":\\\\\\\"\\\\\\\",\\\\\\\"type\\\\\\\":\\\\\\\"label\\\\\\\",\\\\\\\"text\\\\\\\":\\\\\\\"Ask to see the birth notification and instruct the caregiver to register the birth at County Health.\\\\\\\",\\\\\\\"relevance\\\\\\\":{\\\\\\\"rules-engine\\\\\\\":{\\\\\\\"ex-rules\\\\\\\":{\\\\\\\"rules-file\\\\\\\":\\\\\\\"birth_certification.yml\\\\\\\"}}},\\\\\\\"is_visible\\\\\\\":false},{\\\\\\\"key\\\\\\\":\\\\\\\"caregiverinstroductions\\\\\\\",\\\\\\\"openmrs_entity_parent\\\\\\\":\\\\\\\"\\\\\\\",\\\\\\\"openmrs_entity\\\\\\\":\\\\\\\"\\\\\\\",\\\\\\\"openmrs_entity_id\\\\\\\":\\\\\\\"\\\\\\\",\\\\\\\"type\\\\\\\":\\\\\\\"label\\\\\\\",\\\\\\\"text\\\\\\\":\\\\\\\"Instruct the caregiver to have the birth registered at County Health.\\\\\\\",\\\\\\\"relevance\\\\\\\":{\\\\\\\"rules-engine\\\\\\\":{\\\\\\\"ex-rules\\\\\\\":{\\\\\\\"rules-file\\\\\\\":\\\\\\\"birth_certification.yml\\\\\\\"}}},\\\\\\\"is_visible\\\\\\\":false}]}}\\\"}\"],\"set\":[],\"formSubmissionField\":\"birth_certificate\",\"humanReadableValues\":[]},{\"fieldType\":\"formsubmissionField\",\"fieldDataType\":\"text\",\"fieldCode\":\"illness_information\",\"parentCode\":\"\",\"values\":[\"{}\"],\"set\":[],\"formSubmissionField\":\"illness_information\",\"humanReadableValues\":[]}],\"entityType\":\"ec_child\",\"version\":1567174886660,\"teamId\":\"d9eb010a-6d03-4bf8-b57a-b488dedd6f51\",\"team\":\"Clinic A Team\",\"dateCreated\":\"2019-08-30T14:21:50.258Z\",\"serverVersion\":1567174910258,\"clientApplicationVersion\":1,\"clientDatabaseVersion\":9,\"type\":\"Event\",\"id\":\"2e2601e9-e612-4eb5-9ca2-0ea19829fac2\",\"revision\":\"v1\"}";

        Event event = gson.fromJson(oldIllnessInfo, Event.class);
        EventClient eventClient = new EventClient(event,new Client("12345"));
        List<EventClient> result = Utils.processOldEvents(eventClient);
        Assert.assertEquals(result.size(),1);
    }
     */

    @Test
    public void canConvertReferralToANCMemberObject() {
        client = new CommonPersonObjectClient("case1", details, "test user");
        client.setColumnmaps(columnMap);
        MemberObject referralMember = new MemberObject(client);
        referralMember.setChwReferralHf("facility 2");
        Assert.assertTrue(Utils.referralToAncMember(referralMember) instanceof org.smartregister.chw.anc.domain.MemberObject);
    }

    @Test
    public void trueReturnedIfWomanIsOfReproductiveAge() {
        columnMap.put("dob", "1995-12-12");
        columnMap.put("gender", "Female");
        client = new CommonPersonObjectClient("case1", details, "test user 2");
        client.setColumnmaps(columnMap);
        int toAge = 50;
        int fromAge = 18;
        Assert.assertTrue(Utils.isMemberOfReproductiveAge(client, fromAge, toAge));
    }

    @Test
    public void falseReturnedIfWomanIsNotOfReproductiveAge() {
        String dateFormat = "yyyy-MM-dd";
        String dateOfBirth = new SimpleDateFormat(dateFormat).format(new Date());
        columnMap.put("dob", dateOfBirth);
        columnMap.put("gender", "Female");
        client = new CommonPersonObjectClient("case1", details, "test user 2");
        client.setColumnmaps(columnMap);
        int toAge = 50;
        int fromAge = 18;
        Assert.assertFalse(Utils.isMemberOfReproductiveAge(client, fromAge, toAge));
    }

    @Test
    public void formatReferralDurationReturnsCorrectString() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_YEAR, -1);
        Date oneDayBefore = cal.getTime();

        DateTime referralTime = new DateTime(oneDayBefore);
        Assert.assertEquals("Yesterday", Utils.formatReferralDuration(referralTime, RuntimeEnvironment.application));
    }


    @Test
    public void getFormTagShouldNotReturnNullValues() {
        FormTag formTag = Utils.getFormTag(org.smartregister.util.Utils.getAllSharedPreferences());
        Assert.assertNotNull(formTag);
    }

    @Test
    public void assertGetDurationTests() {
        Utils.getDuration("2020-10-09T18:17:07.830+05:00", "2020-05-15T18:17:07.830+05:00");
        Locale locale = RuntimeEnvironment.application.getApplicationContext().getResources().getConfiguration().locale;
        DateTime todayDateTime = new DateTime("2020-10-09T18:17:07.830+05:00");

        Assert.assertEquals("1d", Utils.getDuration(Long.parseLong("100000000"),
                new DateTime("2020-10-10T18:17:07.830+05:00"),
                todayDateTime,
                locale));

        Assert.assertEquals("2w 5d", Utils.getDuration(Long.parseLong("1641600000"),
                new DateTime("2020-11-12T18:17:07.830+05:00"),
                todayDateTime,
                locale));


        Assert.assertEquals("4m 3w", Utils.getDuration(Long.parseLong("12700800000"),
                new DateTime("2020-11-12T18:17:07.830+05:00"),
                todayDateTime,
                locale));

        Assert.assertEquals("4y 11m", Utils.getDuration(Long.parseLong("157334400000"),
                new DateTime("2015-10-10T05:00:00.000+05:00"),
                todayDateTime,
                locale));

        Assert.assertEquals("5y", Utils.getDuration(Long.parseLong("157334400000"),
                new DateTime("2015-10-09T05:00:00.000+05:00"),
                todayDateTime,
                locale));

        Assert.assertEquals("25y 11m", Utils.getDuration("2020-10-12T00:00:00.000+00:00",
                "1994-10-15T00:00:00.000+00:00"));
    }

    @Test
    public void getGenderLanguageSpecificReturnsCorrectString() {
        Assert.assertEquals("", Utils.getGenderLanguageSpecific(context, ""));
        Assert.assertEquals("Male", Utils.getGenderLanguageSpecific(context, "male"));
        Assert.assertEquals("Female", Utils.getGenderLanguageSpecific(context, "female"));
    }

    @Test
    public void getImmunizationHeaderLanguageSpecificReturnsCorrectString() {
        Assert.assertEquals("", Utils.getImmunizationHeaderLanguageSpecific(context, ""));
        Assert.assertEquals("at birth", Utils.getImmunizationHeaderLanguageSpecific(context, "at birth"));
        Assert.assertEquals("weeks", Utils.getImmunizationHeaderLanguageSpecific(context, "weeks"));
        Assert.assertEquals("months", Utils.getImmunizationHeaderLanguageSpecific(context, "months"));
    }

    @Test
    public void getFileNameReturnsFormIdentityIfFormExists() {
        Locale locale = CoreChwApplication.getCurrentLocale();
        AssetManager assetManager = CoreChwApplication.getInstance().getAssets();
        Assert.assertEquals("test_form", Utils.getFileName("test_form", locale, assetManager));
        Assert.assertEquals("child_enrollment", Utils.getFileName("child_enrollment", locale, assetManager));
    }

    @Test
    public void getDayOfMonthWithSuffixThrowsExceptionWhenIllegalDaySupplied() {
        exceptionRule.expect(IllegalArgumentException.class);
        exceptionRule.expectMessage("illegal day of month: 99");
        Utils.getDayOfMonthWithSuffix(99, context);
    }

    @Test
    public void testGetDayOfMonthSuffix() {
        String suffixOne = "st";
        String suffixTwo = "nd";
        String suffixThree = "th";
        Assert.assertEquals(suffixOne, Utils.getDayOfMonthSuffix(1));
        Assert.assertEquals(suffixTwo, Utils.getDayOfMonthSuffix(2));
        Assert.assertEquals(suffixThree, Utils.getDayOfMonthSuffix(11));
    }

    @Test
    public void testGetLocale() {
        Locale expectedLocaleNull = Locale.getDefault();
        Locale expectedLocaleNotNull = RuntimeEnvironment.application.getResources().getConfiguration().locale;
        Assert.assertEquals(expectedLocaleNull, Utils.getLocale(null));
        Assert.assertEquals(expectedLocaleNotNull, Utils.getLocale(RuntimeEnvironment.application));
    }

    @Test
    public void getDayOfMonthWithSuffixReturnsCorrectSuffix() {
        Assert.assertEquals("1st", Utils.getDayOfMonthWithSuffix(1, context));
        Assert.assertEquals("2nd", Utils.getDayOfMonthWithSuffix(2, context));
        Assert.assertEquals("8th", Utils.getDayOfMonthWithSuffix(8, context));
        Assert.assertEquals("11th", Utils.getDayOfMonthWithSuffix(11, context));
        Assert.assertEquals("12th", Utils.getDayOfMonthWithSuffix(12, context));
        Assert.assertNull(Utils.getDayOfMonthWithSuffix(22, context));
    }

    @Test
    public void testGetDuration() {
        LocalDate localDate = LocalDate.now().minusYears(15).minusMonths(1);
        String time = localDate.toString();

        String duration = Utils.getDuration(time);
        Assert.assertEquals("15y 1m", duration);
    }

    @After
    public void tearDown() {
        columnMap = null;
        details = null;
        client = null;
    }

}
