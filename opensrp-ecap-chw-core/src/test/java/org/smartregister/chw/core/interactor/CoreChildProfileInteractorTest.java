package org.smartregister.chw.core.interactor;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.chw.core.BaseUnitTest;
import org.smartregister.chw.core.application.TestApplication;
import org.smartregister.chw.core.contract.CoreChildProfileContract;
import org.smartregister.chw.core.shadows.ContextShadow;
import org.smartregister.chw.core.shadows.CustomFontTextViewShadowHelper;
import org.smartregister.chw.core.shadows.FamilyLibraryShadowUtil;
import org.smartregister.chw.core.shadows.FormUtilsShadowHelper;
import org.smartregister.chw.core.shadows.ImageUtilsShadowHelper;
import org.smartregister.chw.core.utils.CoreJsonFormUtils;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.family.util.AppExecutors;
import org.smartregister.family.util.DBConstants;
import org.smartregister.family.util.JsonFormUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Executor;


@RunWith(RobolectricTestRunner.class)
@Config(application = TestApplication.class, shadows = {ContextShadow.class, FamilyLibraryShadowUtil.class, CustomFontTextViewShadowHelper.class, FormUtilsShadowHelper.class, ImageUtilsShadowHelper.class})
public class CoreChildProfileInteractorTest extends BaseUnitTest implements Executor {

    private CoreChildProfileInteractor interactor;

    @Override
    public void execute(Runnable command) {
        command.run();
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        AppExecutors appExecutors = new AppExecutors(this, this, this);
        interactor = Mockito.spy(new CoreChildProfileInteractor(appExecutors));
    }

    @Test
    public void testSetVaccineListReturnsExpectedList() {
        Map<String, Date> vaccineList = new LinkedHashMap<>();
        interactor.setVaccineList(vaccineList);
        Assert.assertEquals(vaccineList, ReflectionHelpers.getField(interactor, "vaccineList"));
    }

    @Test
    public void testGetVaccineListReturnsExpectedList() {
        Map<String, Date> vaccineList = interactor.getVaccineList();
        Assert.assertEquals(vaccineList, ReflectionHelpers.getField(interactor, "vaccineList"));
    }

    @Test
    public void testGetAutoPopulatedJsonEditFormStringAddsCorrectValues() throws JSONException {
        String title = "Test Title";

        Map<String, String> details = new HashMap<>();
        details.put(DBConstants.KEY.UNIQUE_ID, "UNIQUE_ID");
        CommonPersonObjectClient client = new CommonPersonObjectClient("", details, "Test name");
        client.setColumnmaps(details);

        Mockito.doReturn("12345").when(interactor).getCurrentLocationID(Mockito.any(Context.class));

        JSONObject jsonObject = interactor.getAutoPopulatedJsonEditFormString("child_enrollment", title, RuntimeEnvironment.application, client);

        Assert.assertNotNull(jsonObject);
        // check the title was properly set
        Assert.assertEquals(jsonObject.getJSONObject(JsonFormUtils.STEP1).get(CoreJsonFormUtils.TITLE), title);

        Mockito.verify(interactor, Mockito.times(jsonObject.getJSONObject(JsonFormUtils.STEP1).getJSONArray(JsonFormUtils.FIELDS).length()))
                .processPopulatableFields(Mockito.any(CommonPersonObjectClient.class), Mockito.any(JSONObject.class), Mockito.any(JSONArray.class));
    }

    @Test
    public void testSaveRegistration() {
        CoreChildProfileContract.InteractorCallBack callBack = Mockito.mock(CoreChildProfileContract.InteractorCallBack.class);
        Mockito.doNothing().when(interactor).saveRegistration(null, null, true);
        interactor.saveRegistration(null, null, true, callBack);
        Mockito.verify(callBack).onRegistrationSaved(true);
    }
}
