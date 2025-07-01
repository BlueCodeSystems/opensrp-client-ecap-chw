package org.smartregister.chw.core.utils;

import android.content.Context;
import android.content.Intent;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.Form;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.smartregister.chw.core.BaseUnitTest;
import org.smartregister.chw.core.shadows.UtilsShadowUtil;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.family.domain.FamilyEventClient;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Objects;

import static org.junit.Assert.assertEquals;

@Config(shadows = {UtilsShadowUtil.class})
public class FormUtilsTest extends BaseUnitTest {

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testUpdateWraForBA() throws ParseException {
        Event event = new Event();
        Client client = Mockito.spy(Client.class);
        client.setGender("female");
        String string = "July 6, 1985";
        DateFormat format = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);
        client.setBirthdate(format.parse(string));
        FamilyEventClient familyEventClient = new FamilyEventClient(client, event);
        FormUtils.updateWraForBA(familyEventClient);
        assertEquals(1, event.getObs().size());
        assertEquals(event.getObs().get(0).getFieldType(), "concept");
        assertEquals(event.getObs().get(0).getFieldDataType(), "text");
        assertEquals(event.getObs().get(0).getFieldCode(), "162849AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        assertEquals(event.getObs().get(0).getFormSubmissionField(), "wra");
    }


    @Test
    @Ignore("FIX Android CI unit tests NPE thrown here")
    public void getStartFormActivityReturnsCorrectIntent() {
        Context context = RuntimeEnvironment.application;
        Intent testIntent = FormUtils.getStartFormActivity(new JSONObject(), "test form", context);
        Assert.assertNotNull(testIntent);
        Form form = (Form) Objects.requireNonNull(testIntent.getExtras()).get(JsonFormConstants.JSON_FORM_KEY.FORM);
        assertEquals("test form", form.getName());
    }
}