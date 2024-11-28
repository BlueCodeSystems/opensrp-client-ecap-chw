package org.smartregister.chw.core.form_data;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.smartregister.chw.core.BaseUnitTest;
import org.smartregister.chw.core.shadows.ContextShadow;
import org.smartregister.chw.core.shadows.FormUtilsShadowHelper;
import org.smartregister.chw.core.shadows.LocationHelperShadowHelper;
import org.smartregister.chw.core.shadows.LocationPickerViewShadowHelper;

@Config(shadows = {ContextShadow.class,
        FormUtilsShadowHelper.class, LocationHelperShadowHelper.class, LocationPickerViewShadowHelper.class})
public class NativeFormsDataBinderTest extends BaseUnitTest {

    @Test
    public void testGetPrePopulatedFormReturnsCorrectJSON() throws JSONException {
        Context context = RuntimeEnvironment.application;
        String testBaseEntityId = "test-entity-id";

        NativeFormsDataBinder dataBinder = new NativeFormsDataBinder(context, testBaseEntityId);

        JSONObject populatedFormJson = dataBinder.getPrePopulatedForm("child_enrollment");

        Assert.assertNotNull(populatedFormJson);
        Assert.assertEquals(testBaseEntityId, populatedFormJson.getString(org.smartregister.family.util.JsonFormUtils.ENTITY_ID));
        JSONObject metadata = populatedFormJson.getJSONObject(org.smartregister.family.util.JsonFormUtils.METADATA);
        Assert.assertEquals("test_location_id", metadata.getString(org.smartregister.family.util.JsonFormUtils.ENCOUNTER_LOCATION));
    }

}
