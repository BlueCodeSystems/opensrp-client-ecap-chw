package org.smartregister.chw.core.shadows;

import org.mockito.Mockito;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.smartregister.chw.anc.AncLibrary;

/**
 * @author rkodev
 */

@Implements(AncLibrary.class)
public class AncLibraryShadowUtil {

    private static AncLibrary instance;

    @Implementation
    public static AncLibrary getInstance() {
        if (instance == null) {
            instance = Mockito.mock(AncLibrary.class);
        }

        Mockito.doReturn("yyyy-MM-dd").when(instance).getSaveDateFormat();

        return instance;
    }
}
