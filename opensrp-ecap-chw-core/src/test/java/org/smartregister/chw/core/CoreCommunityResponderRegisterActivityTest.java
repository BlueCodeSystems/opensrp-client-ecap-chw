package org.smartregister.chw.core;

import android.view.MenuItem;
import android.widget.Toast;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowToast;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.chw.core.activity.CoreCommunityRespondersRegisterActivity;
import org.smartregister.chw.core.adapter.CommunityResponderCustomAdapter;
import org.smartregister.chw.core.contract.CoreCommunityRespondersContract;
import org.smartregister.chw.core.model.CommunityResponderModel;
import org.smartregister.chw.core.shadows.CommunityResponderRepositoryShadowHelper;
import org.smartregister.chw.core.shadows.FamilyLibraryShadowUtil;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

@Config(shadows = {CommunityResponderRepositoryShadowHelper.class, FamilyLibraryShadowUtil.class})
public class CoreCommunityResponderRegisterActivityTest extends BaseUnitTest {

    private CoreCommunityRespondersRegisterActivity activity;
    private ActivityController<CoreCommunityRespondersRegisterActivity> activityController;
    private CoreCommunityRespondersContract.Presenter presenter;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        activityController = Robolectric.buildActivity(CoreCommunityRespondersRegisterActivity.class).create();
        activity = activityController.get();
        presenter = Mockito.mock(CoreCommunityRespondersContract.Presenter.class);
        ReflectionHelpers.setField(activity, "presenter", presenter);
    }

    @Test
    public void onResumeFetchesCommunityResponders() {
        activityController.resume();
        Mockito.verify(presenter, Mockito.times(1)).fetchAllCommunityResponders();
    }

    @Test
    public void maximumNumberOfRespondersToastShowIfAddingMoreThan7Responders() {
        activity = Mockito.spy(activity);
        MenuItem item = Mockito.mock(MenuItem.class);
        Mockito.doReturn(R.id.menu_add_responder).when(item).getItemId();
        CommunityResponderRepositoryShadowHelper.setRespondersCount(8);
        activity.onOptionsItemSelected(item);
        List<Toast> toasts = Shadows.shadowOf(RuntimeEnvironment.application).getShownToasts();
        Assert.assertEquals(1, toasts.size());
        Assert.assertTrue(ShadowToast.showedToast("You have reached the maximum number of responders allowed"));
    }


    @Test
    public void refreshCommunityRespondersUpdatesAdapter() {
        activity = Mockito.spy(activity);
        CommunityResponderCustomAdapter adapter = Mockito.mock(CommunityResponderCustomAdapter.class);
        List<CommunityResponderModel> communityResponderModelList = new ArrayList<>();

        ReflectionHelpers.setField(activity, "adapter", adapter);
        ReflectionHelpers.setField(activity, "communityResponders", communityResponderModelList);
        activity.refreshCommunityResponders(communityResponderModelList);
        Mockito.verify(adapter, Mockito.times(1)).notifyDataSetChanged();

    }

    @After
    public void tearDown() {
        try {
            activityController.pause().stop().destroy(); //destroy controller if we can
            activity.finish();
        } catch (Exception e) {
            Timber.e(e);
        }
    }
}
