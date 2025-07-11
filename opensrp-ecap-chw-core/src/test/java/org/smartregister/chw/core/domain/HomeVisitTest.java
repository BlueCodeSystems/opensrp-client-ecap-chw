package org.smartregister.chw.core.domain;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class HomeVisitTest {
    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
    private final HomeVisit homeVisit = new HomeVisit();
    private Date date;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        date = sdf.parse("2020-02-08");
    }
    @Test
    public void testGetHomeVisitIdAndSetHomeVisitId() {
        homeVisit.setHomeVisitId("234wertsdf12345");
        Assert.assertEquals(homeVisit.getHomeVisitId(), "234wertsdf12345");
    }

    @Test
    public void testGetDateAndSetDateSent() {
        homeVisit.setDate(date);
        Assert.assertEquals(homeVisit.getDate(), date);
    }

    @Test
    public void testGetNameAndSetName() {
        homeVisit.setName("James");
        Assert.assertEquals(homeVisit.getName(), "James");
    }
    @Test
    public void testGetAndSetCreatedAt() {
        homeVisit.setCreatedAt(date);
        Assert.assertEquals(homeVisit.getCreatedAt(), date);
    }
    @Test
    public void testGetAndSetUpdatedAt() {
        Long date = 123456798765L;
        homeVisit.setUpdatedAt(date);
        Assert.assertEquals(homeVisit.getUpdatedAt(), date);
    }
    @Test
    public void testGetFormSubmissionIdAndSetFormSubmissionId() {
        homeVisit.setFormSubmissionId("5939443003773");
        Assert.assertEquals(homeVisit.getFormSubmissionId(), "5939443003773");
    }

    @Test
    public void testSetIDAndGetID (){
        Long identifier = 1234567890L;
        homeVisit.setId(identifier);
        Assert.assertEquals(homeVisit.getId(), identifier);
    }
    @Test
    public void testGetBaseEntityIdAndSetBaseEntityId(){
        homeVisit.setBaseEntityId("QW23456789RTYU");
        Assert.assertEquals(homeVisit.getBaseEntityId(), "QW23456789RTYU");
    }

    @Test
    public void testSetAnmIdAndGetAnmId() {
        homeVisit.setAnmId("werty65437654");
        Assert.assertEquals(homeVisit.getAnmId(), "werty65437654");
    }
    @Test
    public void testSetLocationIdAndGetLocationId() {
        homeVisit.setLocationId("werty65437654");
        Assert.assertEquals(homeVisit.getLocationId(), "werty65437654");
    }

    @Test
    public void testGetEventIdAndSetEventId() {
        homeVisit.setEventId("1234uytrew1234");
        Assert.assertEquals(homeVisit.getEventId(), "1234uytrew1234");
    }
    @Test
    public void testGetSyncStatusAndSetSyncStatus() {
        homeVisit.setSyncStatus("done");
        Assert.assertEquals(homeVisit.getSyncStatus(), "done");
    }
    @Test
    public void testSetFormFieldsAndGetFormFields (){
        Map<String, String> map  = new HashMap<>();
        map.put("form1", "field1");
        map.put("form2", "field2");
        map.put("form3", "field3");
        homeVisit.setFormFields(map);
        Assert.assertEquals(homeVisit.getFormFields(), map);
    }

    @Test
    public void testGetVaccineGroupsGivenAndSetVaccineGroupsGiven() throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("vaccine", "group");
        obj.put("group2", 2);
        obj.put("group3", 3);
        homeVisit.setVaccineGroupsGiven(obj);
        Assert.assertEquals(homeVisit.getVaccineGroupsGiven(), obj);
    }
    @Test
    public void testGetSingleVaccinesGivenAndSetSingleVaccinesGiven() throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("vaccine", "group");
        obj.put("OPV", 2);
        homeVisit.setSingleVaccinesGiven(obj);
        Assert.assertEquals(homeVisit.getSingleVaccinesGiven(), obj);
    }
    @Test
    public void testGetServicesGivenAndSetServicesGiven() throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("service", "status");
        obj.put("Wash check", "given");
        obj.put("deworming tablets", "given");
        homeVisit.setServicesGiven(obj);
        Assert.assertEquals(homeVisit.getServicesGiven(), obj);
    }
    @Test
    public void testGetBirthCertificationStateAndSetBirthCertificationState() throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("certificate", "status");
        obj.put("Birth certificate", "given");
        homeVisit.setBirthCertificationState(obj);
        Assert.assertEquals(homeVisit.getBirthCertificationState(), obj);
    }
    @Test
    public void testGetIllnessInformationAndSetIllnessInformation() throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("illness", "information");
        obj.put("malaria", "critical");
        obj.put("typhoid", "not sick");
        homeVisit.setIllnessInformation(obj);
        Assert.assertEquals(homeVisit.getIllnessInformation(), obj);
    }
    @Test
    public void testGetServiceNotGivenAndSetServiceNotGiven() throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("service", "status");
        obj.put("wash check", "not given");
        homeVisit.setServiceNotGiven(obj);
        Assert.assertEquals(homeVisit.getServiceNotGiven(), obj);
    }
    @Test
    public void testGetVaccineNotGivenAndSetVaccineNotGiven() throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("vaccine", "status");
        obj.put("OPV 0", "not given");
        obj.put("OPV 1", "not given");
        homeVisit.setVaccineNotGiven(obj);
        Assert.assertEquals(homeVisit.getVaccineNotGiven(), obj);
    }
}
