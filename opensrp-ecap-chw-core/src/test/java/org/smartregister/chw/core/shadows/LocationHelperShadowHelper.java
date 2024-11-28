package org.smartregister.chw.core.shadows;

import org.mockito.Mockito;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.smartregister.location.helper.LocationHelper;

@Implements(LocationHelper.class)
public class LocationHelperShadowHelper {

    private static LocationHelper locationHelperInstance;

    @Implementation
    public static LocationHelper getInstance() {
        if (locationHelperInstance == null) {
            locationHelperInstance = Mockito.mock(LocationHelper.class);
        }
        Mockito.doReturn("test_location_id").when(locationHelperInstance).getOpenMrsLocationId(Mockito.anyString());

        return locationHelperInstance;
    }
}
