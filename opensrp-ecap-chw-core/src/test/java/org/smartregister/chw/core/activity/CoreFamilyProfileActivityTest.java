package org.smartregister.chw.core.activity;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.robolectric.Robolectric;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.chw.core.BaseUnitTest;
import org.smartregister.chw.core.R;
import org.smartregister.chw.core.activity.impl.CoreFamilyProfileActivityTestImpl;
import org.smartregister.chw.core.custom_views.FamilyFloatingMenu;
import org.smartregister.chw.core.presenter.CoreFamilyProfilePresenter;
import org.smartregister.chw.core.utils.ChildDBConstants;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.commonregistry.CommonPersonObjectClient;

import java.util.HashMap;
import java.util.Map;

public class CoreFamilyProfileActivityTest extends BaseUnitTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    private CoreFamilyProfileActivityTestImpl activity;
    private ActivityController<CoreFamilyProfileActivityTestImpl> controller;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        Context context = Context.getInstance();
        CoreLibrary.init(context);

        //Auto login by default
        context.session().start(context.session().lengthInMilliseconds());

        controller = Robolectric.buildActivity(CoreFamilyProfileActivityTestImpl.class).create().start();
        activity = controller.get();
    }

    @After
    public void tearDown() {
        try {
            activity.finish();
            controller.pause().stop().destroy(); //destroy controller if we can
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSetupViews() {
        activity.setTheme(org.smartregister.R.style.AppTheme); //we need this here
        activity.setContentView(R.layout.activity_family_profile);
        ReflectionHelpers.setField(activity, "presenter", Mockito.mock(CoreFamilyProfilePresenter.class));
        activity.setupViews();

        Assert.assertNotNull(ReflectionHelpers.getField(activity, "familyFloatingMenu"));
    }

    @Test
    public void testOnCreateOptionsMenuInflatesProfileMenu() {
        activity = Mockito.spy(activity);

        MenuInflater menuInflater = Mockito.mock(MenuInflater.class);
        Mockito.doReturn(menuInflater).when(activity).getMenuInflater();

        Menu menu = Mockito.mock(Menu.class);
        activity.onCreateOptionsMenu(menu);

        Mockito.verify(menuInflater).inflate(R.menu.profile_menu, menu);
    }

    @Test
    public void testOnOptionsItemSelected() {
        activity = Mockito.spy(activity);
        Mockito.doNothing().when(activity).startActivityForResult(Mockito.any(), Mockito.anyInt());
        Mockito.doNothing().when(activity).startFormForEdit();

        MenuItem item = Mockito.mock(MenuItem.class);

        Mockito.doReturn(R.id.action_family_details).when(item).getItemId();
        activity.onOptionsItemSelected(item);
        Mockito.verify(activity).startFormForEdit();

        Mockito.doReturn(R.id.action_remove_member).when(item).getItemId();
        activity.onOptionsItemSelected(item);
        Mockito.verify(activity).startActivityForResult(Mockito.any(), Mockito.eq(CoreConstants.ProfileActivityResults.CHANGE_COMPLETED));

        Mockito.doReturn(R.id.action_change_head).when(item).getItemId();
        activity.onOptionsItemSelected(item);
        Mockito.verify(activity, Mockito.times(2)).startActivityForResult(Mockito.any(), Mockito.eq(CoreConstants.ProfileActivityResults.CHANGE_COMPLETED));

        Mockito.doReturn(R.id.action_change_care_giver).when(item).getItemId();
        activity.onOptionsItemSelected(item);
        Mockito.verify(activity, Mockito.times(3)).startActivityForResult(Mockito.any(), Mockito.eq(CoreConstants.ProfileActivityResults.CHANGE_COMPLETED));
    }

    /*
    @Test
    public void testOnActivityResult() {
        activity = Mockito.spy(activity);
        FamilyProfileExtendedContract.Presenter presenter = Mockito.mock(FamilyProfileExtendedContract.Presenter.class);
        Mockito.doReturn(presenter).when(activity).presenter();

        Intent data = Mockito.mock(Intent.class);
        String json = "{\"encounter_type\": \"Child Registration\"}";
        Mockito.doReturn(json).when(data).getStringExtra(Constants.JSON_FORM_EXTRA.JSON);

        activity.onActivityResult(JsonFormUtils.REQUEST_CODE_GET_JSON, Activity.RESULT_OK, data);
        Mockito.verify(presenter).saveChildForm(json, false);
    }
     */

    @Test
    public void updateHasPhoneAnswered() {
        FamilyFloatingMenu familyFloatingMenu = Mockito.mock(FamilyFloatingMenu.class);
        ReflectionHelpers.setField(activity, "familyFloatingMenu", familyFloatingMenu);

        activity.updateHasPhone(true);
        Mockito.verify(familyFloatingMenu).reDraw(true);
    }

    @Test
    public void goToProfileActivityAnswered() {
        View view = Mockito.mock(View.class);
        activity = Mockito.spy(activity);

        Map<String, String> columnMap = new HashMap<>();
        columnMap.put(ChildDBConstants.KEY.ENTITY_TYPE, CoreConstants.TABLE_NAME.CHILD);
        CommonPersonObjectClient client = Mockito.mock(CommonPersonObjectClient.class);
        Mockito.doReturn(columnMap).when(client).getColumnmaps();

        Mockito.doReturn(client).when(view).getTag();
        Mockito.doNothing().when(activity).goToChildProfileActivity(Mockito.any(), Mockito.any());

        activity.goToProfileActivity(view, null);

        Mockito.verify(activity).goToChildProfileActivity(client, null);
    }

    @Test
    public void whenGetFamilyBaseEntityIdAnswered() {
        ReflectionHelpers.setField(activity, "familyBaseEntityId", "12345");
        Assert.assertEquals("12345", activity.getFamilyBaseEntityId());
    }


}
