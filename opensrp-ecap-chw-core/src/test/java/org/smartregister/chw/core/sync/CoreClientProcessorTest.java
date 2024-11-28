package org.smartregister.chw.core.sync;

import android.content.Context;

import org.apache.commons.codec.CharEncoding;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RuntimeEnvironment;
import org.smartregister.chw.core.BaseUnitTest;
import org.smartregister.domain.Client;
import org.smartregister.domain.Event;
import org.smartregister.domain.db.EventClient;
import org.smartregister.domain.jsonmapping.ClientClassification;
import org.smartregister.domain.jsonmapping.Table;
import org.smartregister.immunization.domain.ServiceRecord;
import org.smartregister.immunization.domain.ServiceType;
import org.smartregister.immunization.domain.Vaccine;
import org.smartregister.immunization.repository.RecurringServiceRecordRepository;
import org.smartregister.immunization.repository.RecurringServiceTypeRepository;
import org.smartregister.immunization.repository.VaccineRepository;
import org.smartregister.immunization.service.intent.RecurringIntentService;
import org.smartregister.immunization.service.intent.VaccineIntentService;
import org.smartregister.util.JsonFormUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CoreClientProcessorTest extends BaseUnitTest {

    @Mock
    private Context context;

    @Mock
    private VaccineRepository vaccineRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testAddVaccine() {
        Vaccine vaccine = new Vaccine();
        vaccine.setName("MEASLES 1");

        CoreClientProcessor.addVaccine(vaccineRepository, vaccine);
        Mockito.verify(vaccineRepository).add(vaccine);
        Mockito.verify(vaccineRepository).updateFtsSearch(Mockito.any());
    }

    @Test
    public void testProcessClient() throws Exception {
        CoreClientProcessor processor = Mockito.spy(new CoreClientProcessor(context));
        Mockito.doNothing().when(processor).processEvents(Mockito.any(), Mockito.any(),  Mockito.any(), Mockito.any());

        List<EventClient> eventClients = new ArrayList<>();
        Event event = new Event();
        event.setEventType("Remove Family");
        EventClient eventClient = new EventClient(event, Mockito.mock(Client.class));
        eventClients.add(eventClient);
        eventClients.add(eventClient);

        processor.processClient(eventClients);
        Mockito.verify(processor, Mockito.times(2)).processEvents(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
    }

    private String getAsset(String name) throws Exception {
        Context mContext = RuntimeEnvironment.application;
        InputStream inputStream = mContext.getApplicationContext().getAssets()
                .open(name);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, CharEncoding.UTF_8));
        String jsonString;
        StringBuilder stringBuilder = new StringBuilder();

        while ((jsonString = reader.readLine()) != null) {
            stringBuilder.append(jsonString);
        }
        inputStream.close();

        return stringBuilder.toString();
    }

    @Test
    public void testProcessVaccineEvents() throws Exception {
        ClientClassification clientClassification = JsonFormUtils.gson.fromJson(getAsset("ec_client_classification.json"), ClientClassification.class);
        Table vaccineTable = JsonFormUtils.gson.fromJson(getAsset("ec_client_vaccine.json"), Table.class);
        Table serviceTable = JsonFormUtils.gson.fromJson(getAsset("ec_client_service.json"), Table.class);

        String eventSample = "{\n" +
                "    \"_id\": \"e13eb8d3-3593-443d-8ca1-e54a792c96af\",\n" +
                "    \"_rev\": \"v1\",\n" +
                "    \"baseEntityId\": \"13a99817-dce6-4202-abf0-1f50851c2624\",\n" +
                "    \"clientApplicationVersion\": 4,\n" +
                "    \"clientDatabaseVersion\": 9,\n" +
                "    \"dateCreated\": \"2019-09-14T17:14:05.937Z\",\n" +
                "    \"duration\": 0,\n" +
                "    \"entityType\": \"vaccination\",\n" +
                "    \"eventDate\": \"2019-04-15T00:00:00.000Z\",\n" +
                "    \"eventType\": \"Vaccination\",\n" +
                "    \"formSubmissionId\": \"155118ff-75d7-40b5-b02c-36cb6ec17789\",\n" +
                "    \"identifiers\": {},\n" +
                "    \"locationId\": \"9ada13d3-2251-4943-adfa-1feb256490d8\",\n" +
                "    \"obs\": [\n" +
                "        {\n" +
                "            \"fieldCode\": \"1410AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "            \"fieldDataType\": \"date\",\n" +
                "            \"fieldType\": \"concept\",\n" +
                "            \"formSubmissionField\": \"tt_1\",\n" +
                "            \"humanReadableValues\": [],\n" +
                "            \"parentCode\": \"\",\n" +
                "            \"saveObsAsArray\": false,\n" +
                "            \"set\": [],\n" +
                "            \"values\": [\n" +
                "                \"2019-04-15\"\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"fieldCode\": \"1418AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "            \"fieldDataType\": \"calculate\",\n" +
                "            \"fieldType\": \"concept\",\n" +
                "            \"formSubmissionField\": \"tt_1_dose\",\n" +
                "            \"humanReadableValues\": [],\n" +
                "            \"parentCode\": \"\",\n" +
                "            \"saveObsAsArray\": false,\n" +
                "            \"set\": [],\n" +
                "            \"values\": [\n" +
                "                \"1\"\n" +
                "            ]\n" +
                "        }\n" +
                "    ],\n" +
                "    \"providerId\": \"amikaila83\",\n" +
                "    \"serverVersion\": 1568481245833,\n" +
                "    \"team\": \"BITCHABE\",\n" +
                "    \"teamId\": \"d719f98f-1d87-4db4-8972-6442ea91cfe8\",\n" +
                "    \"type\": \"Event\",\n" +
                "    \"version\": 1568476207694\n" +
                "}";

        String eventType = VaccineIntentService.EVENT_TYPE;

        Event event = JsonFormUtils.gson.fromJson(eventSample, Event.class);
        EventClient eventClient = new EventClient(event, new Client("12345"));

        CoreClientProcessor processor = Mockito.spy(new CoreClientProcessor(context));

        VaccineRepository vaccineRepository = Mockito.mock(VaccineRepository.class);
        Mockito.doReturn(vaccineRepository).when(processor).getVaccineRepository();

        processor.processEvents(clientClassification, vaccineTable, serviceTable, eventClient, event, eventType);
        Mockito.verify(vaccineRepository).add(Mockito.any(Vaccine.class));
    }

    @Test
    public void testProcessServiceEvents() throws Exception {
        ClientClassification clientClassification = JsonFormUtils.gson.fromJson(getAsset("ec_client_classification.json"), ClientClassification.class);
        Table vaccineTable = JsonFormUtils.gson.fromJson(getAsset("ec_client_vaccine.json"), Table.class);
        Table serviceTable = JsonFormUtils.gson.fromJson(getAsset("ec_client_service.json"), Table.class);

        String eventSample = "{\"type\":\"Event\",\"dateCreated\":\"2019-09-14T15:10:43.138Z\",\"serverVersion\":1568473843067,\"clientApplicationVersion\":4,\"clientDatabaseVersion\":9,\"identifiers\":{},\"baseEntityId\":\"b3c90260-8822-4ce8-a7ce-8d24ec1df380\",\"locationId\":\"4e220e32-d3f5-466e-bb19-36f2c76ff819\",\"eventDate\":\"2019-09-13T10:55:39.347Z\",\"eventType\":\"Recurring Service\",\"formSubmissionId\":\"4d707dfe-59b7-4d9f-8bfd-758880ef34ab\",\"providerId\":\"msotila86\",\"duration\":0,\"obs\":[{\"fieldType\":\"concept\",\"fieldDataType\":\"coded\",\"fieldCode\":\"5526AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"values\":[\"1065AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"],\"set\":[],\"formSubmissionField\":\"exclusive_breastfeeding0\",\"humanReadableValues\":[\"yes\"],\"saveObsAsArray\":false},{\"fieldType\":\"concept\",\"fieldDataType\":\"numeric\",\"fieldCode\":\"1639AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"values\":[\"0\"],\"set\":[],\"formSubmissionField\":\"exclusive_breastfeeding0_dose\",\"humanReadableValues\":[],\"saveObsAsArray\":false}],\"entityType\":\"recurring_service\",\"version\":1568460336580,\"teamId\":\"d719f98f-1d87-4db4-8972-6442ea91cfe8\",\"team\":\"BITCHABE\",\"_id\":\"9be53215-6ca3-4a42-b77a-2a35a39eb1e8\",\"_rev\":\"v1\"}";

        String eventType = RecurringIntentService.EVENT_TYPE;

        Event event = JsonFormUtils.gson.fromJson(eventSample, Event.class);
        EventClient eventClient = new EventClient(event, new Client("12345"));

        CoreClientProcessor processor = Mockito.spy(new CoreClientProcessor(context));

        RecurringServiceTypeRepository recurringServiceTypeRepository = Mockito.mock(RecurringServiceTypeRepository.class);
        Mockito.doReturn(recurringServiceTypeRepository).when(processor).getRecurringServiceTypeRepository();

        List<ServiceType> serviceTypeList = new ArrayList<>();
        serviceTypeList.add(new ServiceType());
        Mockito.doReturn(serviceTypeList).when(recurringServiceTypeRepository).searchByName(Mockito.any());

        RecurringServiceRecordRepository recurringServiceRecordRepository = Mockito.mock(RecurringServiceRecordRepository.class);
        Mockito.doReturn(recurringServiceRecordRepository).when(processor).getRecurringServiceRecordRepository();

        Mockito.doReturn(false).when(processor).eventIsVoided(Mockito.anyString());

        processor.processEvents(clientClassification, vaccineTable, serviceTable, eventClient, event, eventType);
        Mockito.verify(recurringServiceRecordRepository).add(Mockito.any(ServiceRecord.class));
    }
}
