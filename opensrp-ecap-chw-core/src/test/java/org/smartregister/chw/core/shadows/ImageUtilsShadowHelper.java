package org.smartregister.chw.core.shadows;

import org.mockito.Mockito;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.smartregister.domain.Photo;
import org.smartregister.util.ImageUtils;

@Implements(ImageUtils.class)
public class ImageUtilsShadowHelper {

    @Implementation
    public static Photo profilePhotoByClientID(String clientEntityId, int defaultProfileImage) {
        return Mockito.mock(Photo.class);
    }
}
