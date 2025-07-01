package org.smartregister.chw.core.dao;

import android.content.Context;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.smartregister.chw.core.utils.Utils;
import org.smartregister.thinkmd.model.FHIRBundleModel;

import static org.smartregister.chw.core.dao.ChildDao.getChildProfileData;
import static org.smartregister.chw.core.dao.VisitDao.getMUACValue;
import static org.smartregister.chw.core.utils.Utils.fetchMUACValues;
import static org.smartregister.chw.core.utils.Utils.getRandomGeneratedId;

@RunWith(PowerMockRunner.class)
public class FHIRBundleDaoTest {

    @Mock
    private Context context;
    private final String childBaseEntityId = "123456";

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @PrepareForTest({ChildDao.class, Utils.class})
    @Test
    public void getFHIRBundleTest() {
        FHIRBundleDao fhirBundleDao = Mockito.spy(FHIRBundleDao.class);
        PowerMockito.mockStatic(ChildDao.class);
        PowerMockito.mockStatic(Utils.class);
        PowerMockito.when(getChildProfileData(childBaseEntityId)).thenReturn(Triple.of("9416", "15-10-1994", "male"));
        PowerMockito.when(fetchMUACValues(childBaseEntityId)).thenReturn(Pair.of("green", "Green"));
        PowerMockito.when(getRandomGeneratedId()).thenReturn("123-456-789");
        PowerMockito.doReturn("111-222-333").when(fhirBundleDao).getLocationId();
        PowerMockito.doReturn("dummy").when(fhirBundleDao).getProviderId();
        FHIRBundleModel bundle = fhirBundleDao.fetchFHIRDateModel(context, childBaseEntityId);

        Assert.assertNotNull(bundle);
    }

    @PrepareForTest({VisitDao.class})
    @Test
    public void testFetchMUACValues() {
        PowerMockito.mockStatic(VisitDao.class);
        PowerMockito.when(getMUACValue(childBaseEntityId)).thenReturn("green");
        Pair<String, String> MUACValues = fetchMUACValues("123456");
        Assert.assertEquals("green", MUACValues.getKey());
        Assert.assertEquals("Green", MUACValues.getValue());
    }
}


