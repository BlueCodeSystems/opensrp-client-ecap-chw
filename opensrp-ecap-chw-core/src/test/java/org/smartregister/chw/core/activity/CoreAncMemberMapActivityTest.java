package org.smartregister.chw.core.activity;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.powermock.reflect.Whitebox;
import org.smartregister.chw.anc.fragment.BaseAncRespondersCallDialogFragment;
import org.smartregister.chw.core.BaseUnitTest;
import org.smartregister.chw.core.application.CoreChwApplication;
import org.smartregister.chw.core.model.CommunityResponderModel;
import org.smartregister.chw.core.repository.CommunityResponderRepository;
import org.smartregister.chw.core.utils.CoreConstants;

import java.util.ArrayList;
import java.util.List;

import io.ona.kujaku.listeners.OnFeatureClickListener;
import io.ona.kujaku.views.KujakuMapView;

import static org.mockito.ArgumentMatchers.eq;

@PrepareForTest({BaseAncRespondersCallDialogFragment.class, CoreChwApplication.class, CoreAncMemberMapActivity.class})
public class CoreAncMemberMapActivityTest extends BaseUnitTest {

    @Rule
    public PowerMockRule rule = new PowerMockRule();
    private CoreAncMemberMapActivity activity;
    @Mock
    private CoreChwApplication coreChwApplication;
    @Mock
    private CommunityResponderRepository communityResponderRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        activity = Mockito.mock(CoreAncMemberMapActivity.class, Mockito.CALLS_REAL_METHODS);

    }

    @Test
    public void featureClickedLaunchesRespondersCallDialog() throws Exception {
        CommunityResponderModel communityResponderModel = Mockito.mock(CommunityResponderModel.class);
        PowerMockito.whenNew(CommunityResponderModel.class).withArguments("ttony", "123456789", null, null).thenReturn(communityResponderModel);
        PowerMockito.mockStatic(BaseAncRespondersCallDialogFragment.class);
        Feature feature = Feature.fromJson("{\"type\":\"Feature\",\"properties\":{\"" + CoreConstants.JsonAssets.RESPONDER_NAME + "\":\"ttony\", " +
                "\"" + CoreConstants.JsonAssets.RESPONDER_PHONE_NUMBER + "\":\"123456789\"}}");
        Whitebox.invokeMethod(activity, "featureClicked", feature);
        PowerMockito.verifyStatic(BaseAncRespondersCallDialogFragment.class, Mockito.times(1));
        BaseAncRespondersCallDialogFragment.launchDialog(eq(activity), eq(communityResponderModel), eq(null), eq(null), eq(false), eq(false), eq(null));
        Assert.assertNotNull(activity);
    }

    @Test
    public void setOnFeatureLongClickListenerSetsMapViewListener() throws Exception {
        KujakuMapView kujakuMapView = Mockito.mock(KujakuMapView.class);
        Whitebox.invokeMethod(activity, "addCommunityTransporterClickListener", kujakuMapView);
        Mockito.verify(kujakuMapView, Mockito.times(1)).setOnFeatureClickListener(Mockito.any(OnFeatureClickListener.class), Mockito.any(String.class));
    }

    @Test
    public void loadTransportersReturnsCorrectFeatureCollection() throws Exception {
        PowerMockito.mockStatic(CoreChwApplication.class);
        List<CommunityResponderModel> responders = new ArrayList<>();
        responders.add(new CommunityResponderModel("First Responder", "001122", "-1.958955 33.7909233", "123456"));
        responders.add(new CommunityResponderModel("Second Responder", "003344", "-1.958955 33.7909233", "16789"));
        Mockito.when(CoreChwApplication.getInstance()).thenReturn(coreChwApplication);
        Mockito.when(coreChwApplication.communityResponderRepository()).thenReturn(communityResponderRepository);
        Mockito.when(communityResponderRepository.readAllResponders()).thenReturn(responders);
        FeatureCollection featureCollection = Whitebox.invokeMethod(activity, "loadCommunityTransporters");
        Assert.assertNotNull(featureCollection);
        Assert.assertEquals(2, featureCollection.features().size());
        Assert.assertEquals("Second Responder", featureCollection.features().get(1).getStringProperty(CoreConstants.JsonAssets.RESPONDER_NAME));
    }

}
